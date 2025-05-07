<script>
    import { onDestroy, onMount, tick } from "svelte";
    import Header from "../../../component/Header.svelte";
    import Navigation from "../../../component/navigation/tree/HeapNavigation.svelte";
    import {isListVisible} from "../../../lib/store";
    import { isPaused, pausedIcon, fromBtn, isReplay, explanation, animationSpeed, animationWorking, animationQuery, codeColor, animationStep, 
             asyncCnt, gradient, indentSize, maxSpeed } from "../../../lib/visualizationStore";
    import { fly } from "svelte/transition";
    import {incrementAnimationCount, verifyGoal} from "../../../lib/mypage/animationCount.js";

    let elementCnt = 0, nodeVw = 0, nodeVh = 0;
    let heap = [];
    let nodeTop = [], nodeLeft = [], idxTop = [], idxLeft = [];
    let initialNodeTop = [], initialNodeLeft = [];
    let isIndexVisible = Array(16).fill(true);
    let createAnimation = false, insertAnimation = false, extractAnimation = false;

    const BLACK = '#000000';
    const ORANGE = '#E97714';
    const GREEN = '#50AD49';

    const repositionComponents = async () => {
        let originElementCnt = elementCnt;
        let originHeap = [...heap];

        elementCnt = 15;
        heap[0] = null;

        for(let i = 1; i <= elementCnt; i++) {
            heap[i] = 0;
        }

        // node, index 위치 초기화
        let topDiff = 18;
        let initialLeftDiff = 15;

        nodeTop[1] = 7;
        nodeLeft[1] = 35;

        for(let i = 1; i <= 7; i++) {
            let level = Math.floor(Math.log2(i)) + 1; // 레벨은 1부터 시작
            let leftDiff = initialLeftDiff / Math.pow(2, level - 1);

            // 왼쪽 자식 노드
            nodeTop[i * 2] = nodeTop[i] + topDiff;
            nodeLeft[i * 2] = nodeLeft[i] - leftDiff;

            // 오른쪽 자식 노드
            nodeTop[i * 2 + 1] = nodeTop[i] + topDiff;
            nodeLeft[i * 2 + 1] = nodeLeft[i] + leftDiff;
        }

        // 고정된 좌표 배열에 그래프의 초기 좌표 저장
        initialNodeTop = [...nodeTop];
        initialNodeLeft = [...nodeLeft];

        nodeVw = (60 / window.innerWidth) * 100;
        nodeVh = (60 / window.innerHeight) * 100;

        // 투명 index 이용해서 항상 index 위치 고정
        for (let i = 1; i <= 15; i++) {
            await tick(); 
            let transIdxRect = document.querySelector(`.transparent-idx-${i}`).getBoundingClientRect();
            let canvasRect = document.querySelector('.canvas').getBoundingClientRect();

            idxTop[i] = ((transIdxRect.top - canvasRect.top) / window.innerHeight) * 100 + (nodeVh / 2.5);
            idxLeft[i] = ((transIdxRect.left - canvasRect.left) / window.innerWidth) * 100 - (nodeVw / 15); 
        }

        elementCnt = originElementCnt;
        heap = [...originHeap];
    };

    onMount(async () => {
        await repositionComponents();

        // 랜덤 힙 구성
        elementCnt = Math.floor(Math.random() * 15) + 1;
        heap[1] = Math.floor(Math.random() * 40) + 59;

        for(let i = 2; i <= 15; i++) {
            if (i > elementCnt) {
                heap[i] = null;
            } else {
                let minRange = Math.floor(heap[Math.floor(i / 2)] * 0.67); 
                heap[i] = minRange + Math.floor(Math.random() * (heap[Math.floor(i / 2)] * 0.33)); 
            }
        }

        window.addEventListener("resize", repositionComponents);
    });

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

    const InitAnimation = () => {
        let nodeElements = document.querySelectorAll('.node');
        nodeElements.forEach(node => {
            node.style.border = '5px solid black';
            node.style.color = '#000000';
            node.style.transition = 'top 0s ease, left 0s ease'
        });

        $animationWorking = false;
        $pausedIcon = true;
        $isPaused = true;
        $isReplay = false;
        $fromBtn = false;
        $explanation = ``;
        $animationQuery = [];
        $codeColor = Array(6).fill();
        $animationStep = [0, 0]; 
        createAnimation = insertAnimation = extractAnimation = false;
        isIndexVisible = Array(16).fill(true);
    };

    const createInputtedElement = (e) => {
        InitAnimation();
        createAnimation = true;

        let argsHeap = [];
        heap = [];
        e.detail.tmpHeap.forEach((element, idx) => {
            argsHeap = [...generateHeapQueries(argsHeap, element, idx + 1)];
        });

        $animationWorking = true;
        $pausedIcon = false;
        $isPaused = false;

        incrementAnimationCount("heap");
        verifyGoal("heap");
        executeHeapQueries($asyncCnt++);
    };

    const createRandomElement = (e) => {
        InitAnimation();
        createAnimation = true;

        // 입력받은 크기만큼 랜덤한 원소를 가진 addList 생성
        let addList = [null, Math.floor(Math.random() * 40) + 59];

        for(let i = 2; i <= e.detail.elementCnt; i++) {
            let minRange = Math.floor(heap[Math.floor(i / 2)] * 0.67); 
            addList.push(minRange + Math.floor(Math.random() * (heap[Math.floor(i / 2)] * 0.33))); 
        }

        addList.shift();
        addList.sort(() => Math.random() - 0.5);

        let argsHeap = [];
        heap = [];
        addList.forEach((element, idx) => {
            argsHeap = [...generateHeapQueries(argsHeap, element, idx + 1)];
        });

        $animationWorking = true;
        $pausedIcon = false;
        $isPaused = false;

        incrementAnimationCount("heap");
        verifyGoal("heap");
        executeHeapQueries($asyncCnt++);
    };

    const extractMax = (e) => {
        InitAnimation();
        extractAnimation = true;

        generateHeapQueries(heap, null, 1);

        $animationWorking = true;
        $pausedIcon = false;
        $isPaused = false;

        incrementAnimationCount("heap");
        verifyGoal("heap");
        executeHeapQueries($asyncCnt++);
    };

    const insertElement = (e) => {
        if(elementCnt == 15) {
            alert("원소를 더 이상 추가할 수 없습니다");
            return;
        }

        InitAnimation();
        insertAnimation = true;

        generateHeapQueries(heap, e.detail.insertNum, 1);

        $animationWorking = true;
        $pausedIcon = false;
        $isPaused = false;

        incrementAnimationCount("heap");
        verifyGoal("heap");
        executeHeapQueries($asyncCnt++);
    };

    const pushAnimationQuery = (tmpExplanation, tmpCode, tmpHeap, tmpElementCnt, tmpBorderColor, tmpFontColor, 
                                tmpSwap1, tmpSwap2, tmpInvisibleIndex) => {
            $animationQuery.push({
            curExplanation: tmpExplanation,
            curCode: tmpCode,
            curHeap: [...tmpHeap],
            curElementCnt: tmpElementCnt,
            curBorderColor: [...tmpBorderColor],
            curFontColor: [...tmpFontColor],
            curSwap1: tmpSwap1,
            curSwap2: tmpSwap2,
            curInvisibleIndex: tmpInvisibleIndex
        })
    };

    const generateHeapQueries = (argsHeap, insertNum, functionCallCnt) => {
        let tmpExplanation = '';
        let tmpCode = 99;
        let tmpHeap = [...argsHeap];
        let tmpElementCnt = 0;
        let tmpBorderColor = Array(16).fill(BLACK);
        let tmpFontColor = Array(16).fill(BLACK);
        let tmpSwap1 = null;
        let tmpSwap2 = null;
        let tmpInvisibleIndex = 99;

        for(let i = 1; i < tmpHeap.length; i++) {
            if(tmpHeap[i] != null) {
                tmpElementCnt = i;
            }
        }

        if(functionCallCnt == 1) {
            // 0. 힙의 초기 상태
            tmpExplanation = '힙의 초기 상태입니다';
            pushAnimationQuery(tmpExplanation, tmpCode, tmpHeap, tmpElementCnt, tmpBorderColor, tmpFontColor, tmpSwap1, tmpSwap2, tmpInvisibleIndex);
        }

        if(insertAnimation || createAnimation) {
            // 1. insertNum을 힙의 마지막 노드로 push
            tmpHeap[++tmpElementCnt] = insertNum;
            tmpBorderColor[tmpElementCnt] = tmpFontColor[tmpElementCnt] = ORANGE;
            tmpExplanation = `${insertNum}을(를) 힙의 마지막 원소로 삽입합니다`;
            tmpCode = 0; 
            pushAnimationQuery(tmpExplanation, tmpCode, tmpHeap, tmpElementCnt, tmpBorderColor, tmpFontColor, tmpSwap1, tmpSwap2, tmpInvisibleIndex);
        }
        else if(extractAnimation) {
            // 1. 루트 노드 반환
            tmpExplanation = `힙의 첫 번째 원소(힙의 최댓값: ${tmpHeap[1]})을(를) 반환합니다`;
            tmpCode = 0; 
            tmpBorderColor[1] = tmpFontColor[1] = ORANGE;
            pushAnimationQuery(tmpExplanation, tmpCode, tmpHeap, tmpElementCnt, tmpBorderColor, tmpFontColor, tmpSwap1, tmpSwap2, tmpInvisibleIndex);

            // 2. 루트 노드 삭제
            tmpBorderColor[1] = tmpFontColor[1] = 'transparent';
            tmpExplanation = '힙의 첫 번째 원소를 삭제합니다';
            tmpCode = 1; 
            pushAnimationQuery(tmpExplanation, tmpCode, tmpHeap, tmpElementCnt, tmpBorderColor, tmpFontColor, tmpSwap1, tmpSwap2, tmpInvisibleIndex);

            if(tmpElementCnt == 1) {
                // 힙의 원소가 하나인 경우
                tmpHeap[tmpElementCnt--] = null;
                tmpExplanation = '힙의 정렬이 완료되었습니다';
                tmpCode = 99;
                pushAnimationQuery(tmpExplanation, tmpCode, tmpHeap, tmpElementCnt, tmpBorderColor, tmpFontColor, tmpSwap1, tmpSwap2, tmpInvisibleIndex);
                return;
            }

            // 3. 마지막 노드를 루트 노드로 이동
            tmpHeap[1] = tmpHeap[tmpElementCnt];
            tmpBorderColor[tmpElementCnt] = tmpFontColor[tmpElementCnt] = ORANGE;
            tmpHeap[tmpElementCnt--] = null;
            tmpExplanation = '힙의 마지막 원소를 첫 번째 원소로 삽입합니다';
            tmpCode = 2;
            tmpInvisibleIndex = tmpElementCnt + 1;
            pushAnimationQuery(tmpExplanation, tmpCode, tmpHeap, tmpElementCnt, tmpBorderColor, tmpFontColor, 1, tmpElementCnt + 1, tmpInvisibleIndex);
        }


        // 힙을 구성하는 과정에서 현재 노드와 swap하게 되는 노드 저장
        let pathNodes = [], curNode, nxtNode;

        if(insertAnimation || createAnimation) { // start from leaf
            curNode = tmpElementCnt;
            let leafValue = tmpHeap[curNode];
            tmpBorderColor[curNode] = tmpFontColor[curNode] = GREEN;

            while(curNode != 1) {
                nxtNode = leafValue > tmpHeap[Math.floor(curNode / 2)] ? Math.floor(curNode / 2) : -1;

                if(nxtNode == -1) {
                    break;
                }

                tmpBorderColor[nxtNode] = tmpFontColor[nxtNode] = ORANGE;
                pathNodes.push(nxtNode);
                curNode = nxtNode;
            }

            curNode = tmpElementCnt;
        }
        else if(extractAnimation) { // start from root
            curNode = 1;
            let rootValue = tmpHeap[curNode];
            tmpBorderColor[curNode] = tmpFontColor[curNode] = GREEN;

            while(curNode * 2 <= tmpElementCnt) {
                if(curNode * 2 + 1 <= tmpElementCnt) {
                    nxtNode = tmpHeap[curNode * 2 + 1] > tmpHeap[curNode * 2] ? curNode * 2 + 1 : curNode * 2;
                    nxtNode = tmpHeap[nxtNode] > rootValue ? nxtNode : -1;
                }
                else {
                    nxtNode = tmpHeap[curNode * 2] > rootValue ? curNode * 2 : -1;
                }

                if(nxtNode == -1) {
                    break;
                }

                tmpBorderColor[nxtNode] = tmpFontColor[nxtNode] = ORANGE;
                pathNodes.push(nxtNode);
                curNode = nxtNode;
            }

            curNode = 1;
        }

        // 1. 실제 swap
        pathNodes.forEach(nxtNode => {
            tmpExplanation = extractAnimation ? `${tmpHeap[nxtNode]} > ${tmpHeap[curNode]}(이)면 두 원소를 교환합니다` :
                                                `${tmpHeap[curNode]} > ${tmpHeap[nxtNode]}(이)면 두 원소를 교환합니다`;
            tmpCode = 3;

            pushAnimationQuery(tmpExplanation, tmpCode, tmpHeap, tmpElementCnt, tmpBorderColor, tmpFontColor, tmpSwap1, tmpSwap2, tmpInvisibleIndex);

            tmpExplanation = `${tmpHeap[nxtNode]}와(과) ${tmpHeap[curNode]}을(를) 교환합니다`;
            tmpCode = 4;
            [tmpHeap[curNode], tmpHeap[nxtNode]] = [tmpHeap[nxtNode], tmpHeap[curNode]];
            pushAnimationQuery(tmpExplanation, tmpCode, tmpHeap, tmpElementCnt, tmpBorderColor, tmpFontColor, curNode, nxtNode, tmpInvisibleIndex);

            tmpBorderColor[curNode] = tmpFontColor[curNode] = BLACK;
            tmpBorderColor[nxtNode] = tmpFontColor[nxtNode] = GREEN;
            curNode = nxtNode;
        });

        // 2. 힙 구성 완료
        tmpBorderColor[curNode] = tmpFontColor[curNode] = BLACK;
        tmpExplanation = '힙의 정렬이 완료되었습니다';
        tmpCode = 99;
        pushAnimationQuery(tmpExplanation, tmpCode, tmpHeap, tmpElementCnt, tmpBorderColor, tmpFontColor, tmpSwap1, tmpSwap2, tmpInvisibleIndex);

        if(createAnimation) {
            return tmpHeap;
        }
    };

    const executeHeapQueries = async (myAsync) => {
        $animationStep = [0, $animationQuery.length - 1];

        while(true) {
            if((myAsync + 1) != $asyncCnt) {
                break;
            }

            if($animationStep[0] == $animationStep[1]) {
                $pausedIcon = true;
                $isPaused = true;
            }

            await drawHeapAnimation($animationStep[0]);
            await waitPause();

            if((myAsync + 1) != $asyncCnt) {
                break;
            }

            // 버튼을 통해서 제어하는 경우 $animationStep의 값을 변경하면 안됨. 정해진 $animationStep[0]의 값으로 설정해야 함.
            if(!$fromBtn) {
                $animationStep[0] = Math.min($animationStep[0] + 1, $animationStep[1]);
            }
        }
    };

    const drawHeapAnimation = async (queryNum) => {
        $explanation = $animationQuery[queryNum].curExplanation; 
        changeCodeColor($animationQuery[queryNum].curCode);
        elementCnt = $animationQuery[queryNum].curElementCnt;
        isIndexVisible = Array(16).fill(true);

        if($fromBtn || $isReplay) {            
            $fromBtn = false;

            heap = [...$animationQuery[queryNum].curHeap];
            isIndexVisible[$animationQuery[queryNum].curInvisibleIndex] = false;

            await tick();

            let nodeElements = document.querySelectorAll('.node');
            nodeElements.forEach((node, idx) => {
                if(extractAnimation && queryNum == 3 && elementCnt != 0 && idx == 0) {
                    node.style.border = `5px solid ${GREEN}`;
                    node.style.color = GREEN;
                }
                else {
                    node.style.border = `5px solid ${$animationQuery[queryNum].curBorderColor[idx + 1]}`;
                    node.style.color = $animationQuery[queryNum].curFontColor[idx + 1];
                }
            });

            if($isReplay) {
                await delay(2000 * (1 / $animationSpeed));
                $isReplay = false;
            }
            else {
                $isPaused = true;
            }

            return;
        }

        // insert 연산이 이루어지는 경우, 노드가 1개일 때 최댓값을 반환하는 경우(index print issue)에는 힙 바로 변경
        if(((insertAnimation || createAnimation) && $animationQuery[queryNum].curCode == 0) || 
            $animationStep[0] == $animationStep[1]) {
            heap = [...$animationQuery[queryNum].curHeap];
        }

        await tick();

        // 노드 색상 변경
        let nodeElements = document.querySelectorAll('.node');
        nodeElements.forEach((node, idx) => {
            node.style.border = `5px solid ${$animationQuery[queryNum].curBorderColor[idx + 1]}`;
            node.style.color = $animationQuery[queryNum].curFontColor[idx + 1];
        });
        
        // index visible 처리
        isIndexVisible[$animationQuery[queryNum].curInvisibleIndex] = false;

        let swap1 = $animationQuery[queryNum].curSwap1;
        let swap2 = $animationQuery[queryNum].curSwap2;

        // swap을 하는 경우
        if(swap1 != null) {
            nodeElements.forEach(node => {
                node.style.transition = `top ${(2 / $animationSpeed)}s ease, left ${(2 / $animationSpeed)}s ease`
            });

            [nodeLeft[swap1], nodeLeft[swap2]] = [nodeLeft[swap2], nodeLeft[swap1]];
            [nodeTop[swap1], nodeTop[swap2]] = [nodeTop[swap2], nodeTop[swap1]];

            await delay(2000 * (1 / $animationSpeed));

            nodeElements.forEach(node => {
                node.style.transition = `top 0s ease, left 0s ease`
            });

            [nodeLeft[swap1], nodeLeft[swap2]] = [nodeLeft[swap2], nodeLeft[swap1]];
            [nodeTop[swap1], nodeTop[swap2]] = [nodeTop[swap2], nodeTop[swap1]];
        }
        else {
            await delay(2000 * (1 / $animationSpeed));
        }

        await tick();

        isIndexVisible = Array(16).fill(true);
        heap = [...$animationQuery[queryNum].curHeap];
    };
</script>

<main>
    <div class="navigation-container">
        <Navigation 
            on:createRandomElement={createRandomElement} 
            on:createInputtedElement={createInputtedElement} 
            on:extractMax={extractMax}
            on:insertElement={insertElement}
        />
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
                최대 힙(Max Heap)
            </div>

            <div class="canvas">
                <svg id="svg" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; overflow: hidden">
                    {#each heap as element, idx}
                        {#if element != null}
                            <!-- 왼쪽 자식 노드에 간선 추가 (고정된 좌표 사용) -->
                            {#if idx * 2 <= elementCnt}
                                <line 
                                    x1={initialNodeLeft[idx] + (nodeVw / 2)}vw 
                                    y1={initialNodeTop[idx] + (nodeVh / 2)}vh 
                                    x2={initialNodeLeft[idx * 2] + (nodeVw / 2)}vw 
                                    y2={initialNodeTop[idx * 2] + (nodeVh / 2)}vh 
                                    stroke="black" 
                                    stroke-width="2"
                                />
                            {/if}
            
                            <!-- 오른쪽 자식 노드에 간선 추가 (고정된 좌표 사용) -->
                            {#if idx * 2 + 1 <= elementCnt}
                                <line 
                                    x1={initialNodeLeft[idx] + (nodeVw / 2)}vw 
                                    y1={initialNodeTop[idx] + (nodeVh / 2)}vh 
                                    x2={initialNodeLeft[idx * 2 + 1] + (nodeVw / 2)}vw
                                    y2={initialNodeTop[idx * 2 + 1] + (nodeVh / 2)}vh 
                                    stroke="black" 
                                    stroke-width="2"
                                />
                            {/if}
                        {/if}
                    {/each}
                </svg>

                {#each heap as element, idx}
                    {#if element != null}
                        <div class="node" style="top: {nodeTop[idx]}vh; left: {nodeLeft[idx]}vw;">
                            {heap[idx]}
                            <div class="transparent-idx transparent-idx-{idx}">{idx}</div>
                        </div>

                        {#if isIndexVisible[idx] == true}
                            <div class="idx" style="top: {idxTop[idx]}vh; left: {idxLeft[idx]}vw;">{idx}</div>
                        {/if}
                    {/if}
                {/each}
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
                    {#if createAnimation} 
                        <div class="code" style="background-color: {$codeColor[5]}; padding-left: {0 * $indentSize + 10}px">for i = 0 to A.length</div>
                        <div class="code" style="background-color: {$codeColor[0]}; padding-left: {1 * $indentSize + 10}px">Heap[Heap.length] = num</div>
                        <div class="code" style="background-color: {$codeColor[3]}; padding-left: {1 * $indentSize + 10}px">while Heap[current] &gt; Heap[parent node]</div>
                        <div class="code" style="background-color: {$codeColor[4]}; padding-left: {2 * $indentSize + 10}px">Swap(Heap[current], Heap[parent node])</div>
                        <div class="code" style="background-color: {$codeColor[4]}; padding-left: {2 * $indentSize + 10}px">current = parent node</div>
                    {:else if extractAnimation}
                        <div class="code" style="background-color: {$codeColor[0]}; padding-left: {0 * $indentSize + 10}px">Extract root(MAX_VALUE)</div>
                        <div class="code" style="background-color: {$codeColor[1]}; padding-left: {0 * $indentSize + 10}px">Delete(Heap[1])</div>
                        <div class="code" style="background-color: {$codeColor[2]}; padding-left: {0 * $indentSize + 10}px">Heap[1] = Heap[Heap.length - 1]</div><br/>
                        <div class="code" style="background-color: {$codeColor[3]}; padding-left: {0 * $indentSize + 10}px">while Heap[larger child node] &gt; Heap[current]</div>
                        <div class="code" style="background-color: {$codeColor[4]}; padding-left: {1 * $indentSize + 10}px">Swap(Heap[current], Heap[larger child node])</div>
                        <div class="code" style="background-color: {$codeColor[4]}; padding-left: {1 * $indentSize + 10}px">current = larger child node</div>
                    {:else if insertAnimation}
                        <div class="code" style="background-color: {$codeColor[0]}; padding-left: {0 * $indentSize + 10}px">Heap[Heap.length] = num</div><br/>
                        <div class="code" style="background-color: {$codeColor[3]}; padding-left: {0 * $indentSize + 10}px">while Heap[current] &gt; Heap[parent node]</div>
                        <div class="code" style="background-color: {$codeColor[4]}; padding-left: {1 * $indentSize + 10}px">Swap(Heap[current], Heap[parent node])</div>
                        <div class="code" style="background-color: {$codeColor[4]}; padding-left: {1 * $indentSize + 10}px">current = parent node</div>
                    {/if}
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

    .node {
        position: absolute;
        width: 50px;
        height: 50px;
        border: 5px solid black;
        background-color: #FFFFFF;
        font-size: 1.5rem;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        transition: top 0s ease, left 0s ease;
    }

    .transparent-idx {
        position: absolute;
        top: 60%;
        font-size: 1.25rem;
        color: transparent;
    }

    .idx {
        position: absolute;
        font-size: 1.25rem;
        color: black;
    }

    #svg {
        z-index: 0; /* SVG를 가장 뒤로 이동 */
        pointer-events: none; /* 클릭 이벤트를 SVG 아래 요소로 전달 */
    }
</style>