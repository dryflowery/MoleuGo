<script>
    import { onDestroy, onMount } from "svelte";
    import Header from "../../../component/Header.svelte";
    import Navigation from "../../../component/navigation/graph/DfsNavigation.svelte";
    import {isListVisible} from "../../../lib/store";
    import { isPaused, pausedIcon, fromBtn, isReplay, explanation, animationSpeed, animationWorking, animationQuery, codeColor, animationStep, 
             asyncCnt, gradient, indentSize, maxSpeed } from "../../../lib/visualizationStore";

    let svgElement, svgRect, nodeElement, nodeRect;
    let startX, startY, startNode, endNode;
    let isDragging = false;
    let edgeElement = null; 
    let nodeCnt = 0;

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
        $codeColor = Array(3).fill();
        $animationStep = [0, 0]; 
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
        startNode = e.target.id.substring(5);

        svgElement = document.getElementById("svg");
        svgRect = svgElement.getBoundingClientRect();
        
        nodeElement = e.target.closest(".node"); 
        nodeRect = nodeElement.getBoundingClientRect();

        // 노드의 중간 지점
        startX = nodeRect.left - svgRect.left + 30;
        startY = nodeRect.top - svgRect.top + 30;

        // edge 생성
        edgeElement = document.createElementNS("http://www.w3.org/2000/svg", "path");
        edgeElement.setAttribute("stroke", "black");
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

        // 드래그 경로 업데이트
        requestAnimationFrame(() => {
            edgeElement.setAttribute("d", `M${startX},${startY} L${currentX},${currentY}`);
        });
    };

    const stopDrag = (e) => {
        if(e.target.className != 'node') {
            edgeElement.remove();
            return;
        }

        nodeElement = e.target.closest(".node"); 
        nodeRect = nodeElement.getBoundingClientRect();
        endNode = nodeElement.id.substring(5);

        if(startNode == endNode) {
            return;
        }
        
        let endX = e.clientX - svgRect.left;
        let endY = e.clientY - svgRect.top;

        // 경로 다시 설정하기(화살표 보이게)@@@@@@@@@@@@@@@

        // 경로 완성
        edgeElement.setAttribute("d", `M${startX},${startY} L${endX},${endY}`);
        edgeElement.setAttribute("id", `edge_${startNode}_${endNode}`)
        
        // pointermove, pointerup 이벤트 리스너 제거
        document.removeEventListener("pointermove", onDrag);
        document.removeEventListener("pointerup", stopDrag);
    };

    const deleteNode = (e) => {
        e.preventDefault(); 

        const clickedNode = e.target; 
        const nodeNum = clickedNode.id.substring(5);
        clickedNode.remove(); 

        // 연결된 간선 삭제
        const edgeElements = document.querySelectorAll('path');

        edgeElements.forEach(element => {
            const edgeNum = element.id.split('_');
            startNode = edgeNum[1];
            endNode = edgeNum[2];  

            if(nodeNum == startNode || nodeNum == endNode) {
                element.remove();
            }
        });
    };
</script>

<main>
    <div class="navigation-container">
        <Navigation/>
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
                깊이 우선 탐색(Depth First Search)
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
                <!-- canvas안에 자료구조, 알고리즘 구현 -->
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
                    <!-- 코드의 class="code"로 설정 -->
                    <!-- 들여쓰기는 padding-left:35px -->
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