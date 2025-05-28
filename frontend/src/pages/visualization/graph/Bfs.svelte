<script>
    import { onDestroy, onMount } from "svelte";
    import Header from "../../../component/Header.svelte";
    import Navigation from "../../../component/navigation/graph/BfsNavigation.svelte";
    import {isListVisible} from "../../../lib/store";
    import { isPaused, pausedIcon, fromBtn, isReplay, explanation, animationSpeed, animationWorking, animationQuery, codeColor, animationStep, 
             asyncCnt, gradient, indentSize, maxSpeed } from "../../../lib/visualizationStore";
    import {incrementAnimationCount, verifyGoal} from "../../../lib/updateMypageInfo.js";

    let svgElement, svgRect, nodeElement, nodeRect;
    let startX, startY, startNodeNum, endNodeNum;
    let isDragging = false;
    let edgeElement = null; 
    let nodeCnt = 0;
    let edgeCnt = 0;
    let edgeIdx = {};

    // 페이지 바뀌면 애니메이션 종료
    onDestroy(() => {
        $animationSpeed = 1;
        InitAnimation();
    });
    
    // 슬라이더의 위치에 따른 $animationSpeed 관리
    // 50%까지는 [1, 10], 51%부터는 [11, 500]
    const updateSpeed = (e) => {
        const sliderValue = e.target.value;
        
        if (sliderValue <= 50) {
            $animationSpeed = Math.round(sliderValue / 5); 
            
            if($animationSpeed == 0) {
                $animationSpeed = 1;
            }
        } 
        else {
            $animationSpeed = Math.min($maxSpeed, Math.round(10 + (sliderValue - 50) * 20));  
        }
        
    };

    // 슬라이더 색깔관리
    $: $gradient = ($animationStep[0] === 0 || $animationStep[1] === 0) ? 0 : ($animationStep[0] / $animationStep[1]) * 100;
    $: sliderStyle = `linear-gradient(to right, #509650 ${$gradient}%, #585858 ${$gradient}%)`;

    const delay = (duration) => {
        return new Promise((resolve) => { setTimeout(resolve, duration); });
    };

    // pause 체크
    const waitPause = async () => {
        return new Promise((resolve) => {
            const checkPause = () => {
                if ($isPaused === false) {
                    setTimeout(resolve, 0);
                } 
                else {
                    setTimeout(() => {
                        if ($isPaused === true) {
                            checkPause(); 
                        } 
                        else {
                            resolve();
                        }
                    }, 50); 
                }
            };

            checkPause();
        });
    };

    const InitAnimation = () => {
        $animationWorking = false;
        $pausedIcon = true;
        $isPaused = true;
        $isReplay = false;
        $fromBtn = false;
        $explanation = ``;
        $animationQuery = [];
        $codeColor = Array(5).fill();
        $animationStep = [0, 0]; 
        $asyncCnt = 0;

        isDragging = false;
        edgeElement = null; 
        nodeCnt = 0;
        edgeCnt = 0;
        edgeIdx = {};

        const nodeElements = document.querySelectorAll('.node'); 
        nodeElements.forEach(element => {
            element.remove();
        });

        const edgeElements = document.querySelectorAll('path');
        edgeElements.forEach(element => {
            element.remove();
        });
    };

    const changeCodeColor = (idx) => {
        for(let i = 0; i < $codeColor.length; i++) {
            if(i == idx) {
                $codeColor[i] = "rgb(80, 150, 80)";
            }
            else {
                $codeColor[i] = "rgba(255, 255, 255, 0)";
            }
        }
    };

    const createNode = (e) => {
        if(isDragging) {
            isDragging = false;
            return;
        }

        // 노드 위에 새로운 노드 생성 불가능
        if(e.target.className == 'node') {
            return;
        }

        const rect = e.currentTarget.getBoundingClientRect();
        const x = e.clientX - rect.left;
        const y = e.clientY - rect.top;

        // canvas 내부에 생성
        if(e.clientX - 60 < rect.left || e.clientX + 60 > rect.right || e.clientY - 60 < rect.top || e.clientY + 60 > rect.bottom) {
            return;
        }

        // 노드의 상대 위치 구하기
        const radiusW = (30 / window.innerWidth) * 100;
        const radiusH = (30 / window.innerHeight) * 100;
        const centerW = (x / window.innerWidth) * 100 - radiusW;
        const centerH = (y / window.innerHeight) * 100 - radiusH;

        // 노드 속성 설정
        const node = document.createElement('div');
        node.id = `node_${nodeCnt}`;
        node.className = 'node';
        node.style.position = 'absolute';
        node.style.left = `${centerW}vw `; 
        node.style.top = `${centerH}vh`; 
        node.style.display = 'flex';
        node.style.alignItems = 'center';
        node.style.justifyContent = 'center';
        node.style.fontSize = '1.5rem';
        node.style.width = '50px';
        node.style.height = '50px';
        node.style.backgroundColor = '#FFFFFF';
        node.style.color = '#000000';
        node.style.border = '5px solid #000000';
        node.style.borderRadius = '50%';
        node.style.zIndex = '1000';
        node.textContent = nodeCnt++;

        e.currentTarget.appendChild(node);

        // 우클릭하면 노드 삭제
        node.addEventListener('contextmenu', (e) => {
            deleteNode(e);
        });

        // 드래그하면 간선 잇기
        node.addEventListener('pointerdown', (e) => {
            startDrag(e);
        });
    };

    const startDrag = (e) => {
        if (e.button != 0) { 
            return; 
        }

        isDragging = true;
        startNodeNum = e.target.id.substring(5);

        svgElement = document.getElementById("svg");
        svgRect = svgElement.getBoundingClientRect();
        
        nodeElement = e.target.closest(".node"); 
        nodeRect = nodeElement.getBoundingClientRect();

        // 노드의 중간 지점에서 간선 시작
        startX = nodeRect.left - svgRect.left + 30;
        startY = nodeRect.top - svgRect.top + 30;

        // 간선 생성
        edgeElement = document.createElementNS("http://www.w3.org/2000/svg", "path");
        edgeElement.setAttribute("stroke", "#000000");
        edgeElement.setAttribute("stroke-width", "3");
        edgeElement.setAttribute("fill", "none");
        edgeElement.setAttribute("marker-end", "url(#arrow)");
        edgeElement.setAttribute("d", `M${startX},${startY} L${startX},${startY}`);
        svgElement.appendChild(edgeElement);

        // pointermove, pointerup 이벤트 리스너 추가
        document.addEventListener("pointermove", onDrag);
        document.addEventListener("pointerup", stopDrag);
    };

    const onDrag = (e) => {
        if (!isDragging) return;

        let currentX = e.clientX - svgRect.left;
        let currentY = e.clientY - svgRect.top;

        // 간선 좌표 업데이트
        requestAnimationFrame(() => {
            edgeElement.setAttribute("d", `M${startX},${startY} L${currentX},${currentY}`);
        });
    };

    const stopDrag = (e) => {
        const getIntersection = (S, E, radius) => {
            // y - y1 = m(x - x1)
            const x1 = S.x, y1 = S.y;
            const x2 = E.x, y2 = E.y;
            const dx = x2 - x1;
            const dy = y2 - y1;

            // (x - a)² + (y - b)² = r²
            const a = E.x;
            const b = E.y;
            const r = radius;

            // 직선의 벡터 방정식과 원의 방정식을 연립해서 At² + Bt + C = 0꼴로 표현
            const A = (dx * dx) + (dy * dy);
            const B = 2 * (dx * (x1 - a) + dy * (y1 - b));
            const C = (x1 - a) * (x1 - a) + (y1 - b) * (y1 - b) - (r * r);

            // 근의 공식으로 t 구하기
            const t1 = (-B - Math.sqrt(B * B - 4 * A * C)) / (2 * A);
            const t2 = (-B + Math.sqrt(B * B - 4 * A * C)) / (2 * A);

            // 0 <= t <= 1인 값 선택
            const t = 0 <= t1 && t1 <= 1 ? t1 : t2;

            // 벡터를 이용한 교점 좌표 계산
            const x = x1 + t * dx;
            const y = y1 + t * dy;

            return { x: x, y: y };
        }

        // 노드로 잇지 않으면 간선 삭제
        if(e.target.className != 'node') {
            edgeElement.remove();
            return;
        }

        nodeElement = e.target.closest(".node"); 
        nodeRect = nodeElement.getBoundingClientRect();
        endNodeNum = nodeElement.id.substring(5);

         // self loop, 중복 간선 생성 방지
        if(startNodeNum == endNodeNum || document.querySelector(`#edge_${startNodeNum}_${endNodeNum}`) != null) {
            edgeElement.remove();
            return;
        }
        
        // 목표 노드와 간선의 교점 좌표 구하기
        const S = {x: startX, y: startY};
        const E = {x: nodeRect.left - svgRect.left + 30, y: nodeRect.top - svgRect.top + 30};
        const intersection = getIntersection(S, E, 40);

        let endX = intersection.x;
        let endY = intersection.y;

        // 경로 완성
        edgeElement.setAttribute("d", `M${startX},${startY} L${endX},${endY}`);
        edgeElement.setAttribute("id", `edge_${startNodeNum}_${endNodeNum}`)
        
        // pointermove, pointerup 이벤트 리스너 제거
        document.removeEventListener("pointermove", onDrag);
        document.removeEventListener("pointerup", stopDrag);
    };

    const deleteNode = (e) => {
        e.preventDefault(); 

        const delNode = e.target; 
        const delNodeNum = parseInt(delNode.id.substring(5), 10);
        delNode.remove(); 
        nodeCnt--;

        // 연결된 간선 삭제
        const edgeElements = document.querySelectorAll('path');

        edgeElements.forEach(element => {
            if(element.id == 'arrow-path') {
                return;
            }

            const edgeNum = element.id.split('_');
            startNodeNum = parseInt(edgeNum[1], 10);
            endNodeNum = parseInt(edgeNum[2], 10);  

            if(delNodeNum == startNodeNum || delNodeNum == endNodeNum) {
                element.remove();
            }
            else { // 간선 번호 하나씩 당기기
                element.setAttribute("id", `edge_${startNodeNum > delNodeNum ? startNodeNum - 1 : startNodeNum}_${endNodeNum > delNodeNum ? endNodeNum - 1 : endNodeNum}`);
            }
        });

        // 정점 번호 하나씩 당기기
        const nodeElements = document.querySelectorAll('.node'); 

        nodeElements.forEach(element => {
            const nodeNum = parseInt(element.id.substring(5), 10);

            if(nodeNum > delNodeNum) {
                element.id = `node_${nodeNum - 1}`;
                element.textContent = nodeNum - 1;
            }
        });
    };

    const startBfs = (e) => {
        if(nodeCnt == 0) {
            alert("1개 이상의 노드가 필요합니다.");
            return;
        }

        generateBfsQueries();

        $animationWorking = true;
        $pausedIcon = false;
        $isPaused = false;

        incrementAnimationCount("bfs");
        verifyGoal("bfs");
        executeBfsQueries($asyncCnt++);
    };

    const pushAnimationQuery = (tmpExplanation, tmpCode, tmpNodeColor, tmpEdgeColor) => {
            $animationQuery.push({
            curExplanation: tmpExplanation,
            curCode: tmpCode,
            curNodeColor: tmpNodeColor,
            curEdgeColor: tmpEdgeColor
        })
    };

    const generateBfsQueries = () => {
        const makeGraph = (graph) => {
            const edgeElements = document.querySelectorAll('path');

            edgeElements.forEach((element, index) => {
                if(element.id == 'arrow-path') {
                    return;
                }

                const edgeNum = element.id.split('_');
                startNodeNum = parseInt(edgeNum[1], 10);
                endNodeNum = parseInt(edgeNum[2], 10); 

                edgeIdx[element.id] = index - 1; // arrow-path 제외
                edgeCnt++;
                
                addEdge(graph, startNodeNum, endNodeNum);
            });
        };

        const addEdge = (graph, u, v) => {
            if(!graph[u]) {
                graph[u] = [];
            }
            
            graph[u].push(v);
        };

        const sortGraph = (graph) => {
            const sortedGraph = Object.keys(graph)
                .sort((a, b) => a - b) 
                .reduce((acc, key) => {
                    acc[key] = graph[key].sort((a, b) => a - b);
                    return acc;
                }, {});
            return sortedGraph;
        };

        class Queue {
            constructor() {
                this.items = [];
            }

            push(item) {
                this.items.push(item);
            }

            pop() {
                this.items.shift();
            }

            top() {
                return this.items[0];
            }

            empty() {
                return this.items.length == 0;
            }
        }
        
        // 그래프 자료구조 생성
        let graph = {};
        makeGraph(graph);
        sortGraph(graph);

        // 쿼리 구성 시작
        $animationQuery = [];
        let tmpExplanation = ``;
        let tmpCode = 1000;
        let tmpNodeColor = Array(nodeCnt).fill("#000000");
        let tmpEdgeColor = Array(edgeCnt).fill("#000000");
        let visited = Array(nodeCnt).fill(false);
        const q = new Queue();

        // 0. 그래프 초기 상태
        tmpExplanation = '그래프의 초기 상태입니다';
        pushAnimationQuery(tmpExplanation, tmpCode, [...tmpNodeColor], [...tmpEdgeColor]);

        // 1. bfs
        const bfs = (cur) => {
            q.push(cur)
            visited[cur] = true;

            while(!q.empty()) {
                let cur = q.top();
                q.pop();
                tmpCode = cur == 0 ? 0 : 3;
                tmpExplanation = `${cur}번 노드에서 탐색을 시작합니다`
                tmpNodeColor[cur] = "#50ad49";
                pushAnimationQuery(tmpExplanation, tmpCode, [...tmpNodeColor], [...tmpEdgeColor]);

                if(graph[cur] != undefined) {
                    graph[cur].forEach(nxt => {
                    // 간선 색깔 변경
                    tmpCode = 1;
                    tmpExplanation = `${nxt}번 노드를 방문하지 않았다면 방문하고, 아니면 다른 노드를 탐색합니다`
                    tmpNodeColor[nxt] = tmpNodeColor[nxt] == "#000000" ? "#e97714" : "#50ad49";
                    tmpEdgeColor[edgeIdx[`edge_${cur}_${nxt}`]] = "#e97714"; 
                    pushAnimationQuery(tmpExplanation, tmpCode, [...tmpNodeColor], [...tmpEdgeColor]);

                    tmpNodeColor[nxt] = "#50ad49";
                    tmpEdgeColor[edgeIdx[`edge_${cur}_${nxt}`]] = "#50ad49"; 

                    if(!visited[nxt]) {
                        tmpCode = 2
                        tmpExplanation = `${nxt}번 노드를 방문합니다`
                        tmpNodeColor[nxt] = "#50ad49";
                        visited[nxt] = true;
                        q.push(nxt);
                        pushAnimationQuery(tmpExplanation, tmpCode, [...tmpNodeColor], [...tmpEdgeColor]);
                    }
                    else {
                        tmpCode = 1000;
                        tmpExplanation = `${nxt}번 노드를 이미 방문했으므로, 다른 노드를 탐색합니다`
                        pushAnimationQuery(tmpExplanation, tmpCode, [...tmpNodeColor], [...tmpEdgeColor]);
                    }
                    });
                }
            }
        };

        for(let i = 0; i < nodeCnt; i++) {
            if(!visited[i]) {
                bfs(i);
            }
        }

        // 2. 알고리즘 종료
        tmpExplanation = '그래프의 순회를 완료했습니다';
        tmpCode = 1000;
        pushAnimationQuery(tmpExplanation, tmpCode, [...tmpNodeColor], [...tmpEdgeColor]);
    };

    const executeBfsQueries = async (myAsync) => {
        $animationStep = [0, $animationQuery.length - 1];

        while(true) {
            if((myAsync + 1) != $asyncCnt) {
                break;
            }

            if($animationStep[0] == $animationStep[1]) {
                $pausedIcon = true;
                $isPaused = true;
            }

            await drawBfsAnimation($animationStep[0]);
            await waitPause();

            // 버튼을 통해서 제어하는 경우 $animationStep의 값을 변경하면 안됨. 정해진 $animationStep[0]의 값으로 설정해야 함.
            if(!$fromBtn) {
                $animationStep[0] = Math.min($animationStep[0] + 1, $animationStep[1]);
            }
        }
    };

    const rgbToHex = (rgb) => {
        const result = rgb.match(/\d+/g);
        if (!result || result.length < 3) {
            return null;
        }

        return (
            '#' +
            result
                .slice(0, 3)
                .map((n) => parseInt(n).toString(16).padStart(2, '0'))
                .join('')
        );
    };

    const drawBfsAnimation = async (queryNum) => {
        $explanation = $animationQuery[queryNum].curExplanation; 
        changeCodeColor($animationQuery[queryNum].curCode);

        if(queryNum !== 0) {
            const edgeElements = document.querySelectorAll('path');

            for (let idx = 0; idx < edgeElements.length; idx++) {
                const element = edgeElements[idx];

                if (element.getAttribute("stroke") === null || element.getAttribute("stroke") === undefined) {
                    continue;
                }

                const newStroke = `${$animationQuery[queryNum].curEdgeColor[idx - 1]}`;

                if (element.getAttribute("stroke") !== newStroke) {

                    if (!($fromBtn || $isReplay)) {
                        const newEdge = document.createElementNS("http://www.w3.org/2000/svg", "path");
                        newEdge.setAttribute("stroke", newStroke);
                        newEdge.setAttribute("stroke-width", "3.25");
                        newEdge.setAttribute("fill", "none");

                        const d = element.getAttribute("d");
                        const tempPath = document.createElementNS("http://www.w3.org/2000/svg", "path");
                        tempPath.setAttribute("d", d);

                        const markerWidth = parseFloat(document.getElementById("arrow").getAttribute("markerWidth")) + 5.0;
                        const totalLength = tempPath.getTotalLength();
                        const newLength = totalLength - markerWidth;

                        const startPoint = tempPath.getPointAtLength(0);
                        const endPoint = tempPath.getPointAtLength(newLength);
                        const newD = `M${startPoint.x},${startPoint.y} L${endPoint.x},${endPoint.y}`;

                        newEdge.setAttribute("d", newD);
                        svgElement.appendChild(newEdge);

                        newEdge.style.strokeDasharray = newLength;
                        newEdge.style.strokeDashoffset = newLength;

                        newEdge.animate(
                            [
                                {strokeDashoffset: newLength},
                                {strokeDashoffset: 0}
                            ],
                            {
                                duration: 2000 * (1 / $animationSpeed),
                                easing: "ease-in-out",
                                iterations: 1
                            }
                        );

                        newEdge.style.strokeDashoffset = 0;

                        await delay(2000 * (1 / $animationSpeed));

                        element.setAttribute("stroke", newStroke);
                        newEdge.remove();
                    }
                    else {
                        element.setAttribute("stroke", newStroke);
                    }
                }
            }

            const nodeElements = document.querySelectorAll('.node');
            for (let idx = 0; idx < nodeElements.length; idx++) {
                const element = nodeElements[idx];
                const newColor = $animationQuery[queryNum].curNodeColor[idx];

                if (rgbToHex(element.style.color) !== newColor) {
                    if (!($fromBtn || $isReplay)) {
                        element.style.transition = `color ${800 * (1 / $animationSpeed)}ms, border ${800 * (1 / $animationSpeed)}ms`;
                    }

                    const color = $animationQuery[queryNum].curNodeColor[idx];
                    element.style.color = color;
                    element.style.border = `5px solid ${color}`;

                    if (!($fromBtn || $isReplay)) {
                        await delay(800 * (1 / $animationSpeed));
                    }
                }
            }
        }

        if(!($fromBtn || $isReplay)) {            
            await delay(2000 * (1 / $animationSpeed));
        }
        else {
            $fromBtn = false;

            if($isReplay) {
                await delay(2000 * (1 / $animationSpeed));
                $isReplay = false;
            }
            else {
                $isPaused = true;
            }
        }
    };
</script>

<main>
    <div class="navigation-container">
        <Navigation on:startBfs={startBfs}/>
    </div>

    <div class="header-container">
        <Header/>
    </div>

    <div class="main-container" class:visualization-list-visible={$isListVisible} class:visualization-list-invisible={!$isListVisible}>
        <div class="main-left-container">
            <!-- main-left-container, main-right-container 나누는 수직선 -->
            <div class="visualization-vertical-line"></div>

            <div class="algorithm-title-container">
                <!-- 알고리즘 이름 추가. ex) 버블 정렬(Bubble Sort) -->
                너비 우선 탐색(Breadthe First Search)
            </div>

            <div class="canvas" on:click={createNode}>
                <svg id="svg" style="position: absolute; left: 0; top: 0; overflow: hidden">
                    <marker 
                        id='arrow' 
                        orient="auto" 
                        markerWidth='6'
                        markerHeight='6' 
                        refX='3' 
                        refY='3'> 
                        <path d='M0,0 L6,3 L0,6 L0,0 Z' fill="black" />
                    </marker>
                </svg>
            </div>

            <div class="animation-control-container">
                <ion-icon name="play-back" class="animation-control-btn" on:click={() => {if($animationWorking) {$fromBtn = true; $isPaused = false; $pausedIcon = true; $animationStep[0] = 0;}}}></ion-icon>
                <ion-icon name="caret-back" class="animation-control-btn" on:click={() => {if($animationWorking) {$fromBtn = true; $isPaused = false; $pausedIcon = true; $animationStep[0] = Math.max($animationStep[0] - 1, 0);}}}></ion-icon>

                {#if $isPaused || $pausedIcon} 
                    <ion-icon name="play-outline" class="animation-control-btn" style="font-size: 2.5rem; color: #d9d9d9;" 
                        on:click={() => {
                            if($animationWorking) {
                                if ($animationStep[0] === $animationStep[1]) {
                                    $isReplay = true; $animationStep[0] = -1;
                                } 

                                $isPaused = false; 
                                $pausedIcon = false;
                            }
                        }}>
                    </ion-icon>
            
                {:else}
                    <ion-icon name="pause-outline" class="animation-control-btn" style="font-size: 2.5rem; color: #d9d9d9;" on:click={() => {if($animationWorking) {$isPaused = true; $pausedIcon = true;}}}></ion-icon>
                {/if}

                <ion-icon name="caret-forward" class="animation-control-btn" on:click={() => {if($animationWorking) {$fromBtn = true; $isPaused = false; $pausedIcon = true; $animationStep[0] = Math.min($animationStep[0] + 1, $animationStep[1]);}}}></ion-icon>
                <ion-icon name="play-forward" class="animation-control-btn" on:click={() => {if($animationWorking) {$fromBtn = true; $isPaused = false; $pausedIcon = true; $animationStep[0] = $animationStep[1];}}}></ion-icon>

                <input class="animation-slider"
                    type="range"
                    style="background: {sliderStyle};"
                    min=0
                    max={$animationStep[1]}
                    bind:value={$animationStep[0]}
                    on:input={() => {if($animationWorking) {$isPaused = false; $pausedIcon = true; $fromBtn = true;}}}
                />

                <input class="speed-slider"
                    type="range" 
                    min="0" 
                    max="100" 
                    step="1" 
                    value="0"
                    on:input={updateSpeed}
                />
                <span class="speed-label">x {$animationSpeed}</span>
            </div>      
        </div>

        <div class="main-right-container">
            <div class="explanation-container">
                <div class="explanation-title">단계별 알고리즘 설명</div>
                <div class="explanation">{@html $explanation}</div>
            </div>

            <div class="code-container">
                <div class="code-title">의사코드</div>

                <div class="code-area">
                    <div class="code" style="background-color: {$codeColor[0]}; padding-left: {0 * $indentSize + 10}px">current = start_node</div>
                    <div class="code" style="background-color: {$codeColor[0]}; padding-left: {0 * $indentSize + 10}px">visited[current] = True</div>
                    <div class="code" style="background-color: {$codeColor[0]}; padding-left: {0 * $indentSize + 10}px">queue.append(current)</div><br>
                    <div class="code" style="background-color: {$codeColor[4]}; padding-left: {0 * $indentSize + 10}px">while queue:</div>
                    <div class="code" style="background-color: {$codeColor[1]}; padding-left: {1 * $indentSize + 10}px">for next in graph[current]:</div>
                    <div class="code" style="background-color: {$codeColor[1]}; padding-left: {2 * $indentSize + 10}px">if not visited[next]:</div>
                    <div class="code" style="background-color: {$codeColor[2]}; padding-left: {3 * $indentSize + 10}px">visited[next] = True</div>
                    <div class="code" style="background-color: {$codeColor[2]}; padding-left: {3 * $indentSize + 10}px">queue.append(next)</div>
                    <div class="code" style="background-color: {$codeColor[3]}; padding-left: {1 * $indentSize + 10}px">current = queue[0]</div>
                    <div class="code" style="background-color: {$codeColor[3]}; padding-left: {1 * $indentSize + 10}px">queue.pop(0)</div>
                </div>
            </div>
        </div>
    </div>
</main>

<style>    
    main {
        height: 100vh;
        display: grid;
        grid-template-rows: 70px 1fr;   
        user-select: none;
        -ms-use-select: none;
        -moz-user-select: none;
        -webkit-user-select: none;
        overflow: hidden;
    }

    .main-container {
        display: grid;
        grid-template-columns: 78vw auto;
        align-items: center;
    }

    .main-left-container {
        display: grid;
        grid-template-rows: 50px 1fr 60px;
        justify-content: center;
        align-items: center;
    }

    .main-right-container {
        display: grid;
        grid-template-rows: 19vh 68vh;
        font-size: 0.8rem;
        justify-content: center;
        align-items: center;
    }

    .canvas {
        position: relative;
        overflow: hidden;
    }

    #svg {
        z-index: 0; /* SVG를 가장 뒤로 이동 */
        pointer-events: none; /* 클릭 이벤트를 SVG 아래 요소로 전달 */
        width: 100%;
        height: 100%;
    }
</style>