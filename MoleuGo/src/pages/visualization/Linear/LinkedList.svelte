<script>
    import { onDestroy } from "svelte";
    import Header from "../../../component/Header.svelte";
    import LinkedListNavigation from "../../../component/navigation/Linear/LinkedListNavigation.svelte";
    import {isListVisible} from "../../../lib/store.js";

    let numNode = [15, 10, 20, 30, 7]

    let isPaused = true;
    let pausedIcon = true;
    let fromBtn = false;
    let isReplay = false;

    let explanation = ``;
    let animationSpeed = 1;
    let animationWorking = false;
    let animationQuery = [];
    let codeColor = Array(3).fill()
    let animationStep = [0, 0]; // [curStep, maxStep]
    let asyncCnt = 0; // 비동기 함수 한 번에 하나만 실행하기 위한 변수
    let gradient = 0;

    $: totalWidth = numNode.length * (90); // 노드의 총 길이 계산 (노드 크기와 간격 포함)
    $: offsetX = (200 - totalWidth) / 2;

    // 페이지 바뀌면 애니메이션 종료
    onDestroy(() => {
        InitAnimation();
    });
    
    // 슬라이더의 위치에 따른 animationSpeed 관리
    // 50%까지는 [1, 10], 51%부터는 [11, 1000]
    const updateSpeed = (event) => {
        const sliderValue = event.target.value;
        
        if (sliderValue <= 50) {
            animationSpeed = Math.round(sliderValue / 5); 
            
            if(animationSpeed == 0) {
                animationSpeed = 1;
            }
        } 
        else {
            animationSpeed = Math.min(1000, Math.round(10 + (sliderValue - 50) * 20));  
        }
        
    };

    // 슬라이더 색깔관리
    $: gradient = (animationStep[0] === 0 || animationStep[1] === 0) ? 0 : (animationStep[0] / animationStep[1]) * 100;
    $: sliderStyle = `linear-gradient(to right, #509650 ${gradient}%, #585858 ${gradient}%)`;

    const delay = (duration) => {
        return new Promise((resolve) => { setTimeout(resolve, duration); });
    };

    // pause 체크
    const waitPause = async () => {
        return new Promise((resolve) => {
            const checkPause = () => {
                if (isPaused === false) {
                    setTimeout(resolve, 0);
                } 
                else {
                    setTimeout(() => {
                        if (isPaused === true) {
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
        animationWorking = false;
        pausedIcon = true;
        isPaused = true;
        isReplay = false;
        fromBtn = false;
        explanation = ``;
        animationQuery = [];
        codeColor = Array(3).fill();
        animationStep = [0, 0]; 
    };

    const changeCodeColor = (idx) => {
        for(let i = 0; i < codeColor.length; i++) {
            if(i == idx) {
                codeColor[i] = "rgb(80, 150, 80)";
            }
            else {
                codeColor[i] = "rgba(255, 255, 255, 0)";
            }
        }
    };

    const startLinkedList = async (e) => {
        InitAnimation();

        animationWorking = true;
        pausedIcon = false;
        isPaused = false;
    };

    const createRandomNode = (e) => { 
        InitAnimation();

        const NodeCnt = e.detail.NodeCnt;

        numNode = [];  

        for (let i = 0; i < NodeCnt; i++) {
            numNode.push(Math.floor(Math.random() * 199) - 99);
        }
    };

    const createInputtedNode = (e) => {
        InitAnimation();

        const tmpNode = e.detail.tmpNode;
        numNode = tmpNode;
    };

    const calculateWidth = () => {
        // 노드와 화살표의 너비 계산
        const nodeWidth = 50; // 각 노드의 너비
        const arrowWidth = 30; // 각 화살표의 너비
        return numNode.length * nodeWidth + (numNode.length - 1) * arrowWidth;
    };

</script>

<main>
    <div class="navigation-container">
        <LinkedListNavigation 
        on:createRandomNode={createRandomNode} 
        on:createInputtedNode={createInputtedNode} 
        on:startBubbleSort={startLinkedList}
        animationWorking={animationWorking}/>
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
                연결리스트(LinkedList)
            </div>

            <div class="canvas">
                <!-- canvas안에 자료구조, 알고리즘 구현 -->

                {#each numNode as node, index}
                    <div class="linked-list-node-container" 
                        style="transform: translateX({offsetX + index * 95}px);">

                        <div class="linked-list-node">
                            <div class="node-value">{node}</div>
                        </div>

                        {#if index < numNode.length - 1}
                            <div class="node-arrow"></div>
                        {:else}
                            <div class="node-arrow white-arrow"></div>
                            <div class="white-arrow white-node-arrow::after"></div>
                        {/if}

                        {#if index === 0}
                        <div class="head-label">head[0]</div>
                        {/if}

                        {#if index === numNode.length - 1}
                        <div class="tail-label">tail[{numNode.length - 1}]</div>
                        {/if}

                    
                    </div>
                {/each}
            </div>

            <!-- svelte-ignore a11y-no-static-element-interactions -->
            <!-- svelte-ignore a11y-click-events-have-key-events -->
            <div class="animation-control-container">
                <ion-icon name="play-back" class="animation-control-btn" on:click={() => {if(animationWorking) {fromBtn = true; isPaused = false; pausedIcon = true; animationStep[0] = 0;}}}></ion-icon>
                <ion-icon name="caret-back" class="animation-control-btn" on:click={() => {if(animationWorking) {fromBtn = true; isPaused = false; pausedIcon = true; animationStep[0] = Math.max(animationStep[0] - 1, 0);}}}></ion-icon>

                {#if isPaused || pausedIcon} 
                    <ion-icon name="play-outline" class="animation-control-btn" style="font-size: 2.5rem; color: #d9d9d9;" 
                        on:click={() => {
                            if(animationWorking) {
                                if (animationStep[0] === animationStep[1]) {
                                    isReplay = true; animationStep[0] = -1;
                                } 

                                isPaused = false; 
                                pausedIcon = false;
                            }
                        }}>
                    </ion-icon>
            
                {:else}
                    <ion-icon name="pause-outline" class="animation-control-btn" style="font-size: 2.5rem; color: #d9d9d9;" on:click={() => {if(animationWorking) {isPaused = true; pausedIcon = true;}}}></ion-icon>
                {/if}

                <ion-icon name="caret-forward" class="animation-control-btn" on:click={() => {if(animationWorking) {fromBtn = true; isPaused = false; pausedIcon = true; animationStep[0] = Math.min(animationStep[0] + 1, animationStep[1]);}}}></ion-icon>
                <ion-icon name="play-forward" class="animation-control-btn" on:click={() => {if(animationWorking) {fromBtn = true; isPaused = false; pausedIcon = true; animationStep[0] = animationStep[1];}}}></ion-icon>

                <input class="animation-slider"
                    type="range"
                    style="background: {sliderStyle};"
                    min=0
                    max={animationStep[1]}
                    bind:value={animationStep[0]}
                    on:input={() => {if(animationWorking) {isPaused = false; pausedIcon = true; fromBtn = true;}}}
                />

                <input class="speed-slider"
                    type="range" 
                    min="0" 
                    max="100" 
                    step="1" 
                    value="0"
                    on:input={updateSpeed}
                />
                <span class="speed-label">x {animationSpeed}</span>
            </div>      
        </div>

        <div class="main-right-container">
            <div class="explanation-container">
                <div class="explanation-title">단계별 알고리즘 설명</div>
                <div class="explanation">{@html explanation}</div>
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
        grid-template-columns: 1500px auto;
        align-items: center;
    }

    .main-left-container {
        display: grid;
        grid-template-rows: 50px 1fr 60px;
    }

    .main-right-container {
        display: grid;
        grid-template-rows: 200px 649px;
        background-color: #0d0e0f;
        font-size: 0.8rem;
    }

    .canvas {
        position: relative;
        background-color: #FFFFFF;
        height: 729px;
        width: 1400px;
        margin-left: 49.5px;
        border: 5px solid #313131;
        border-radius: 20px;
        display: flex;
        justify-content: center;
        align-items: center;
        overflow: hidden;
    }   

    .linked-list-node-container {
        display: flex;
        align-items: center;
        position: absolute;
        transition: transform 0.5s ease;
    }

    .linked-list-node {
        width: 50px;
        height: 50px;
        display: flex;
        align-items: center;
        justify-content: center;
        background-color: #ffffff; 
        color: #000000;
        border-radius: 10px; 
        border: 4px solid black;
        font-size: 1rem;
        font-weight: bold;
        text-align: center;
    }

    .node-arrow {
        width: 30px;
        height: 2px;
        background-color: #000000; 
        position: relative;
        margin-right: 5px;
    }

    .node-arrow::after {
        content: '';
        position: absolute;
        top: -3px;
        right: -10px;
        border-width: 5px;
        border-style: solid;
        border-color: transparent transparent transparent #000000;
    }

    .white-arrow {
        background-color: #ffffff;
    }

    .white-arrow::after {
        border-color: transparent transparent transparent #ffffff;
    }

    .head-label, .tail-label {
        position: absolute;
        top: 62px; /* 노드 아래에 위치 */
        font-size: 1rem;
        font-weight: bold;
        color: #000000;
        background-color: #ffffff;
        color: #009400;
        z-index: 10;
    }

    .head-label {
        left: 32%;
        transform: translateX(-50%);
    }

    .tail-label {
        left: 32%;
        transform: translateX(-50%);
    }


</style>