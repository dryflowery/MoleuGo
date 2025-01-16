<script>
    import { onDestroy, onMount, tick } from "svelte";
    import Header from "../../../component/Header.svelte";
    import Navigation from "../../../component/navigation/tree/HeapNavigation.svelte";
    import {isListVisible} from "../../../lib/store";
    import { isPaused, pausedIcon, fromBtn, isReplay, explanation, animationSpeed, animationWorking, animationQuery, codeColor, animationStep, 
             asyncCnt, gradient, indentSize, maxSpeed } from "../../../lib/visualizationStore";

    let elementCnt = 0, nodeVw = 0, nodeVh = 0;
    let heap = [];
    let nodeTop = [], nodeLeft = [], idxTop = [], idxLeft = [];
    let initialNodeTop = [], initialNodeLeft = [];
    let isIndexVisible = Array(16).fill(true);

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
            idxLeft[i] = ((transIdxRect.left - canvasRect.left) / window.innerWidth) * 100 - (nodeVw / 20); 
        }

        elementCnt = originElementCnt;
        heap = [...originHeap];
    };

    onMount(async () => {
        await repositionComponents();

        // 랜덤 힙 구성
        // elementCnt = Math.floor(Math.random() * 15) + 1;
        elementCnt = 15;
        heap[1] = Math.floor(Math.random() * 15) + 40;

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

    };


</script>

<main>
    <div class="navigation-container">
        <Navigation />
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
                힙(Heap)
            </div>

            <div class="canvas">
                <svg class="lines" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; overflow: hidden">
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
                                    stroke-width="2"/>
                            {/if}
            
                            <!-- 오른쪽 자식 노드에 간선 추가 (고정된 좌표 사용) -->
                            {#if idx * 2 + 1 <= elementCnt}
                                <line 
                                    x1={initialNodeLeft[idx] + (nodeVw / 2)}vw 
                                    y1={initialNodeTop[idx] + (nodeVh / 2)}vh 
                                    x2={initialNodeLeft[idx * 2 + 1] + (nodeVw / 2)}vw
                                    y2={initialNodeTop[idx * 2 + 1] + (nodeVh / 2)}vh 
                                    stroke="black" 
                                    stroke-width="2"/>
                            {/if}
                        {/if}
                    {/each}
                </svg>

                {#each heap as element, idx}
                    {#if element != null}
                        <div id="node-{idx}" class="node" style="top: {nodeTop[idx]}vh; left: {nodeLeft[idx]}vw;">
                            {heap[idx]}
                            <div class="transparent-idx transparent-idx-{idx}">{idx}</div>
                        </div>

                        {#if isIndexVisible[idx] == true}
                            <div id="idx-{idx}" class="idx" style="top: {idxTop[idx]}vh; left: {idxLeft[idx]}vw;">{idx}</div>
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