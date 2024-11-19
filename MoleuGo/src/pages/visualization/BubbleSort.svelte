<script>
    import Header from "../../component/Header.svelte";
    import Navigation from "../../component/navigation/BubbleSortNavigation.svelte";
    import {isListVisible} from "../../lib/store";

    let numArr = [29, 10, 14, 37, 14]
    let graphLeft = [];
    let indexLeft = [];
    let isPaused = false;
    let explanation = ``;

    // 원소가 바뀔 때마다 위치 계산
    $: {
        let canvasWidth = 1400; // 캔버스 길이
        let elementWidth = 65;  // 원소 하나의 길이
        let totalWidth = numArr.length * elementWidth;  // 전체 원소의 길이
        let startX = (canvasWidth - totalWidth) / 2; // 시작점
        
        for (let i = 0; i < numArr.length; i++) {
            graphLeft[i] = startX + i * elementWidth; 
            indexLeft[i] = startX + i * elementWidth; 
        }
    }

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

    const createRandomElement = (e) => {
        let elementCnt = e.detail.elementCnt;
        numArr = [];  

        for (let i = 0; i < elementCnt; i++) {
            numArr.push(Math.floor(Math.random() * 38) + 3);
        }
    };

    const createInputtedElement = (e) => {
        let tmpArr = e.detail.tmpArr;
        numArr = tmpArr;
    };

    const startBubbleSort = (e) => {
        let isAsc = e.detail.isAsc;
    };
</script>

<main>
    <div class="navigation-container">
        <Navigation on:createRandomElement={createRandomElement} 
        on:createInputtedElement={createInputtedElement} 
        on:startBubbleSort={startBubbleSort}/>
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
                <table>
                    {#each numArr as element, index}
                        <div class="graph" style="height: {element * 8}px; left: {graphLeft[index]}px;">
                            <span class="element">{element}</span>
                        </div>
                        <span class="index" style="left: {indexLeft[index]}px;">{index}</span>
                    {/each}
                </table>
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
        height: 100vh;
        display: grid;
        grid-template-rows: 70px 1fr;   
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

    table {
        display: flex;
        position: relative; 
        height: 60%; 
        width: 100%;   
    }

    .graph {
        background-color: #d9d9d9;
        display: flex;
        position: absolute;
        bottom: 0; 
        width: 50px;
        text-align: center;
        transition: left 0.5s ease, height 0.5s ease; 
        border-radius: 5px;
    }

    .element {
        background-color: #737373;
        color: #dcdcdc;
        font-weight: bold;
        font-size: 18px;
        width: 100%;
        margin-top: auto;
        border-radius: 0 0 5px 5px; 
    }

    .index {
        position: absolute;
        bottom: -30px; 
        font-size: 18px;
        font-weight: bold;
        color: #000000; 
        width: 50px;    
        text-align: center; 
        transition: left 0.5s ease, height 0.5s ease; 
    }

    .main-right-container {
        display: grid;
        grid-template-rows: 200px 649px;
        background-color: #0d0e0f;
        font-size: 0.8rem;
    }
</style>