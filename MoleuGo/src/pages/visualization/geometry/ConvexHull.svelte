<script>
    import { onDestroy, onMount } from "svelte";
    import Header from "../../../component/Header.svelte";
    import Navigation from "../../../component/navigation/geometry/ConvexHullNavigation.svelte";
    import {isListVisible} from "../../../lib/store";

    let cells = Array.from({ length: 54 }); 
    let visibleCells = Array(54).fill(false);

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

    const switchPointVisibility = (e) => {
        const x = Math.round((e.clientX - 400) / 100);
        const y = Math.round((e.clientY - 200) / 100);
        
        if(x < 0 || y < 0) {
            return;
        }

        console.log({x: x, y: y, idx: y * 9 + x});
        visibleCells[y * 9 + x] = !visibleCells[y * 9 + x];
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

    const createRandomPoint = (e) => {
        InitAnimation();

        visibleCells = Array(54).fill(false);

        for (let i = 0; i < e.detail.pointCnt; i++) {
            let r = Math.floor(Math.random() * 54); 

            if(!visibleCells[r]) 
            {
                visibleCells[r] = true;
            }
            else {
                i--;
            }
        }
    };

    const constructConvexHull = (e) => {
        // InitAnimation();

        // preDrawConvexHull();

        // animationWorking = true;
        // pausedIcon = false;
        // isPaused = false;
        // drawConvexHull(asyncCnt++);
    };
</script>

<main>
    <div class="navigation-container">
        <Navigation on:createRandomPoint={createRandomPoint} 
        on:constructConvexHull={constructConvexHull}
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
                볼록 껍질(Convex Hull)  
            </div>

            <div class="canvas">
                <!-- canvas안에 자료구조, 알고리즘 구현 -->
                <div class="grid">
                    {#each cells as _, index}
                        <!-- <div>{index}</div> -->
                        <div class="cell" on:click={(e) => switchPointVisibility(e)}>
                            <div class="point {visibleCells[index] ? '' : 'point-invisible'}"></div>
                        </div>
                    {/each}
                </div>
            </div>

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

    .grid {
        display: grid;
        grid-template-rows: repeat(6, 100px); 
        grid-template-columns: repeat(9, 100px); 
        gap: 0; 
        width: 950px;
        height: 650px;
        border-left: 2px solid #000000;
        border-bottom: 2px solid #000000;
        margin: 25px 0px 0px 250px;
        position: relative; 
    }

    /* y축 화살표 */
    .grid::before {
        content: '';
        position: absolute;
        top: -10px;
        left: -0.5px;
        border-width: 7px;
        border-style: solid;
        border-color: transparent transparent transparent #000000;
        transform: translateX(-50%) rotate(270deg);
    }

    /* x축 화살표 */
    .grid::after {
        content: '';
        position: absolute;
        bottom: -7.5px; 
        left: 100%;
        border-width: 7px;
        border-style: solid;
        border-color: transparent transparent #000000 transparent; 
        transform: translateX(-50%) rotate(90deg); 
    }

    .cell {
        border-right: 2px dashed #509650;
        border-top: 2px dashed #509650; 
        width: 100px;
        height: 100px; 
        margin-top: 50px;
    }

    .point {
        width: 50px;
        height: 50px;
        background-color: #509650;
        border-radius: 50%;
        margin-left: 75px;
        margin-top: -25px;
        z-index: 1000;
        transition: opacity 0.25s ease;
    }

    .point-invisible {
        opacity: 0; 
        transition: opacity 0s ease;
    }
</style>

<!-- animationWorking시에만 grid 보이게 -->