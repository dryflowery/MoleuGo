<script>
    import Header from "../../component/Header.svelte";
    import Navigation from "../../component/navigation/BubbleSortNavigation.svelte"; // Navigation 경로 수정
    import {isListVisible} from "../../lib/store";

    let isPaused = false;
    let explanation = ``;
    let animationSpeed = 1;
    let animationStep = [0, 0]; // [curStep, maxStep]

    // 슬라이더 색깔관리
    $: gradientValue = (animationStep[0] / animationStep[1]) * 100;
    $: sliderStyle = `linear-gradient(to right, #509650 ${gradientValue}%, #585858 ${gradientValue}%)`;

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
                버블 정렬(Bubble Sort) 
                <button class="theory-btn">이론 설명</button> 
            </div>

            <div class="canvas">
                <!-- canvas안에 자료구조, 알고리즘 구현 -->
            </div>

            <div class="animation-control-container">
                <!-- animation-control-btn은 on:click으로 애니메이션 제어 -->
                <ion-icon name="play-back" class="animation-control-btn"></ion-icon>
                <ion-icon name="caret-back" class="animation-control-btn"></ion-icon>

                {#if isPaused}
                    <ion-icon name="play-outline" class="animation-control-btn" style="font-size: 2.5rem; color: #d9d9d9;" on:click={() => isPaused = !isPaused}></ion-icon>
                {:else}
                    <ion-icon name="pause-outline" class="animation-control-btn" style="font-size: 2.5rem; color: #d9d9d9;" on:click={() => isPaused = !isPaused}></ion-icon>
                {/if}

                <ion-icon name="caret-forward" class="animation-control-btn"></ion-icon>
                <ion-icon name="play-forward" class="animation-control-btn"></ion-icon>

                <!-- input은 on:input으로 애니메이션 제어 -->
                <input
                    type="range"
                    style="background: {sliderStyle};"
                    min={0}
                    max={animationStep[1]}
                    bind:value={animationStep[0]}
                />
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
        display: grid;
        height: 100vh;
        grid-template-rows: 70px 1fr;
        user-select: none;
        -ms-use-select: none;
        -moz-user-select: none;
        -webkit-user-select: none;
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
</style>