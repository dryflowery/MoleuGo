<script>
    import { onDestroy } from "svelte";
    import Header from "../../component/Header.svelte";
    import Navigation from "../../component/navigation/sort/BubbleSortNavigation.svelte";
    import {isListVisible, isAlgoGuideVisible} from "../../lib/store";
    import { isPaused, pausedIcon, fromBtn, isReplay, explanation, animationSpeed, animationWorking, animationQuery, codeColor, animationStep, 
             asyncCnt, gradient, indentSize, maxSpeed } from "../../lib/visualizationStore";
    import BubbleSortGuide from "../../component/guide/BubbleSortGuide.svelte";

    // 페이지 바뀌면 애니메이션 종료
    onDestroy(() => {
        $animationSpeed = 1;
        InitAnimation();
    });
    
    // 슬라이더의 위치에 따른 $animationSpeed 관리
    // 50%까지는 [1, 10], 51%부터는 [11, 500]
    const updateSpeed = (event) => {
        const sliderValue = event.target.value;
        
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
</script>

<main>
    <div class="navigation-container" style="display: {$isAlgoGuideVisible ? 'none' : 'block'};">
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
                버블 정렬(Bubble Sort)

                <!-- 알고리즘 가이드 -->
                <button class="guide-button" on:click={() => $isAlgoGuideVisible = true}>?</button>

                {#if $isAlgoGuideVisible}
                    <!-- 각 알고리즘에 맞는 가이드 넣기 -->
                    <BubbleSortGuide/>
                {/if}
            </div>

            <div class="canvas">
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
</style>