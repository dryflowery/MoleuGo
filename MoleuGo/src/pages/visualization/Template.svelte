<script>
    import Header from "../../component/Header.svelte";
    import {isListVisible} from "../../lib/store";

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
    <div class="header-container">
        <Header/>
    </div>

    <div id="main-container" class:visualization-list-visible={$isListVisible} class:visualization-list-invisible={!$isListVisible}>
        <div id="main-left-container">
            <div id="algorithm-title-container">
                <!-- 알고리즘 이름 추가. ex) 버블 정렬(Bubble Sort) -->
                알고리즘 이름
                <button id="theory-btn">이론 설명</button> 
            </div>

            <div id="canvas">
                <!-- canvas안에 자료구조, 알고리즘 구현 -->
            </div>

            <div id="animation-control-container">

            </div>
        </div>

        <div id="main-right-container">
            <div id="explanation-container">
                <div id="explanation-title">
                    단계별 알고리즘 설명
                </div>

                <div id="explanation">

                </div>
            </div>

            <div id="code-container">
                <div id="code-title">
                    의사코드
                </div>

                <div id="code">

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

    #main-container {
        display: grid;
        grid-template-columns: 1400px auto;
    }

    #main-left-container {
        display: grid;
        grid-template-rows: 45px 1fr 65px;
        border-right: 1px solid #414852;
    }

    #main-right-container {
        display: grid;
        grid-template-rows: 200px 649px;
        background-color: #0d0e0f;
        font-size: 0.8rem;
    }

    #explanation-container {
        display: grid;
        grid-template-rows: 40px 140px;
        background-color: #509650;
        height: 180px;
        width: 480px;
        margin: 20px 0px 0px 20px;
        border-radius: 0 0 5px 5px; 
    }
</style>