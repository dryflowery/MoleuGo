<script>
    import Header from "../../component/Header.svelte";
    import Navigation from "../../component/navigation/BubbleSortNavigation.svelte"; // Navigation 경로 수정
    import {isListVisible} from "../../lib/store";

    let explanation = ``;
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
            <div class="algorithm-title-container">
                <!-- 알고리즘 이름 추가. ex) 버블 정렬(Bubble Sort) -->
                버블 정렬(Bubble Sort) 
                <button class="theory-btn">이론 설명</button> 
            </div>

            <div class="canvas">
                <!-- canvas안에 자료구조, 알고리즘 구현 -->
            </div>

            <div class="animation-control-container">

            </div>
        </div>

        <div class="main-right-container">
            <div class="explanation-container">
                <div class="explanation-title">
                    단계별 알고리즘 설명
                </div>

                <div class="explanation">{@html explanation}</div>
            </div>

            <div class="code-container">
                <div class="code-title">
                    의사코드
                </div>

                <div class="code">

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
    }

    .main-container {
        display: grid;
        grid-template-columns: 1500px auto;
    }

    .main-left-container {
        display: grid;
        grid-template-rows: 45px 1fr 65px;
        border-right: 1px solid #414852;
    }

    .main-right-container {
        display: grid;
        grid-template-rows: 200px 649px;
        background-color: #0d0e0f;
        font-size: 0.8rem;
    }
</style>