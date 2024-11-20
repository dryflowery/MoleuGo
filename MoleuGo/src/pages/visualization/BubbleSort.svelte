<script>
    import Header from "../../component/Header.svelte";
    import Navigation from "../../component/navigation/BubbleSortNavigation.svelte";
    import {isListVisible} from "../../lib/store";

    let numArr = [29, 10, 14, 37, 14]
    let graphLeft = [];
    let indexLeft = [];

    let isPaused = false;
    let explanation = ``;
    let animationSpeed = 2;
    let animationQuery = [];
    let codeColor = Array(3).fill()

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
        const elementCnt = e.detail.elementCnt;
        numArr = [];  

        for (let i = 0; i < elementCnt; i++) {
            numArr.push(Math.floor(Math.random() * 38) + 3);
        }
    };

    const createInputtedElement = (e) => {
        const tmpArr = e.detail.tmpArr;
        numArr = tmpArr;
    };

    // BubbleSort animation start
    const startBubbleSort = (e) => {
        const isAsc = e.detail.isAsc;

        preDrawBubbleSort(isAsc);
        drawBubbleSort();
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
    }

    const pushAnimationQuery = (tmpArr, tmpGraphBgColor, tmpElementBgColor, tmpElementColor, 
                                tmpIndexColor,tmpSwap1, tmpSwap2, tmpExplanation, tmpCode) => {
            animationQuery.push({
            curArr: [...tmpArr],
            curGraphBgColor: [...tmpGraphBgColor], 
            curElementBgColor: [...tmpElementBgColor],
            curElementColor: [...tmpElementColor],
            curIndexColor: [...tmpIndexColor],
            curSwap1: tmpSwap1, 
            curSwap2: tmpSwap2,
            curExplanation: tmpExplanation,
            curCode: tmpCode // 색깔 바꿀 코드
        })
    };

    const preDrawBubbleSort = (isAsc) => {  
        const graphBg = {normal: "#d9d9d9", selected: "#ecadae", sorted: "#9fda9b"};
        const elementBg = {normal: "#737373", selected: "#ad7677", sorted: "#6a9068"};
        const elementColor = {normal: "#dcdcdc", selected: "#ffebeb", sorted: "#e8ffe6"};
        const indexColor = {normal: "#000000", selected: "#e05a5d", sorted: "#72c36b"};

        animationQuery = [];
        let tmpArr = [...numArr];
        let tmpGraphBgColor = Array(tmpArr.length).fill(graphBg.normal);
        let tmpElementBgColor = Array(tmpArr.length).fill(elementBg.normal);
        let tmpElementColor = Array(tmpArr.length).fill(elementColor.normal);
        let tmpIndexColor = Array(tmpArr.length).fill(indexColor.normal);
        let tmpSwap1 = 1000, tmpSwap2 = 1000;
        let tmpExplanation = ``;
        let tmpCode = 1000;

        const initColor = (sortedIdx) => {
            for(let i = 0; i < sortedIdx; i++) {
                tmpGraphBgColor[i] = graphBg.normal; 
                tmpElementBgColor[i] = elementBg.normal;
                tmpElementColor[i] = elementColor.normal;
                tmpIndexColor[i] = indexColor.normal;
            }
        };

        const setNormalColor = (idx) => {
            tmpGraphBgColor[idx] = graphBg.normal;
            tmpElementBgColor[idx] = elementBg.normal;
            tmpElementColor[idx] = elementColor.normal;
            tmpIndexColor[idx] = indexColor.normal;
        }

        const setSelectedColor = (idx1, idx2) => {
            tmpGraphBgColor[idx1] = tmpGraphBgColor[idx2] = graphBg.selected;
            tmpElementBgColor[idx1] = tmpElementBgColor[idx2] = elementBg.selected;
            tmpElementColor[idx1] = tmpElementColor[idx2] = elementColor.selected;
            tmpIndexColor[idx1] = tmpIndexColor[idx2] = indexColor.selected;
        };
        
        const setSortedColor = (sortedIdx) => {
            tmpGraphBgColor[sortedIdx] = graphBg.sorted;
            tmpElementBgColor[sortedIdx] = elementBg.sorted;
            tmpElementColor[sortedIdx] = elementColor.sorted;
            tmpIndexColor[sortedIdx] = indexColor.sorted;
        };

        // 초기 상태
        tmpExplanation = `배열의 초기 상태입니다`
        pushAnimationQuery(tmpArr, tmpGraphBgColor, tmpElementBgColor, tmpElementColor, tmpIndexColor, tmpSwap1, tmpSwap2, tmpExplanation, tmpCode);

        for(let i = 0; i < tmpArr.length; i++) {
            // 인덱스 j부터 tmpArr.length - i - 1까지 정렬 시작
            initColor(tmpArr.length - i);
            tmpExplanation = `index ${0}부터 ${tmpArr.length - i - 1}까지 정렬을 시작합니다`;
            tmpCode = 0;
            pushAnimationQuery(tmpArr, tmpGraphBgColor, tmpElementBgColor, tmpElementColor, tmpIndexColor, tmpSwap1, tmpSwap2, tmpExplanation, tmpCode);

            for(let j = 0; j < tmpArr.length - i - 1; j++) {
                // isAsc ? tmpArr[j] > tmpArr[j + 1] : tmpArr[j] < tmpArr[j]면 두 원소 교환
                setSelectedColor(j, j + 1);
                tmpExplanation = isAsc ? `${tmpArr[j]} > ${tmpArr[j + 1]}(이)면 두 원소를 교환합니다` :
                                         `${tmpArr[j]} < ${tmpArr[j + 1]}(이)면 두 원소를 교환합니다`;
                tmpCode = 1;
                pushAnimationQuery(tmpArr, tmpGraphBgColor, tmpElementBgColor, tmpElementColor, tmpIndexColor, tmpSwap1, tmpSwap2, tmpExplanation, tmpCode);

                if((isAsc && (tmpArr[j] > tmpArr[j + 1])) || (!isAsc && (tmpArr[j] < tmpArr[j + 1]))) {
                    // 두 원소 교환
                    [tmpArr[j], tmpArr[j + 1]] = [tmpArr[j + 1], tmpArr[j]];
                    tmpExplanation = `${tmpArr[j]}과(와) ${tmpArr[j + 1]}을(를) 교환합니다`
                    tmpCode = 2;
                    pushAnimationQuery(tmpArr, tmpGraphBgColor, tmpElementBgColor, tmpElementColor, tmpIndexColor, j, j + 1, tmpExplanation, tmpCode);
                    setNormalColor(j);
                }
                else {
                    // 교환이 발생하지 않으면 왼쪽 원소만 normal로 변경
                    setNormalColor(j);
                }
            }

            // tmpArr.length - i - 1까지 정렬 완료
            setSortedColor(tmpArr.length - i - 1);
            tmpExplanation = `index ${tmpArr.length - i - 1}까지 정렬이 완료되었습니다`;
            tmpCode = 0;
            pushAnimationQuery(tmpArr, tmpGraphBgColor, tmpElementBgColor, tmpElementColor, tmpIndexColor, tmpSwap1, tmpSwap2, tmpExplanation, tmpCode);
        }
        
        // 정렬 완료
        initColor(tmpArr.length);
        tmpExplanation = `배열의 정렬이 완료되었습니다.`
        tmpCode = 1000;
        pushAnimationQuery(tmpArr, tmpGraphBgColor, tmpElementBgColor, tmpElementColor, tmpIndexColor, tmpSwap1, tmpSwap2, tmpExplanation, tmpCode);
    };

    const drawBubbleSort = async () => {
        const graphElements = document.querySelectorAll('.graph');
        const elementElements = document.querySelectorAll('.element');
        const indexElements = document.querySelectorAll('.index');
        
        let animationStep = [0, animationQuery.length];

        while (animationStep[0] < animationStep[1]) {
            await waitPause();

            explanation = animationQuery[animationStep[0]].curExplanation; // explanation 수정
            changeCodeColor(animationQuery[animationStep[0]].curCode); // codeColor 수정
            
            // 색상 수정
            graphElements.forEach((element, idx) => {
                element.style.backgroundColor = animationQuery[animationStep[0]].curGraphBgColor[idx];
            });

            elementElements.forEach((element, idx) => {
                element.style.backgroundColor = animationQuery[animationStep[0]].curElementBgColor[idx];
                element.style.color = animationQuery[animationStep[0]].curElementColor[idx];
            });

            indexElements.forEach((element, idx) => {
                element.style.color = animationQuery[animationStep[0]].curIndexColor[idx];
            });

            // swap이 필요한 경우에만
            if (animationQuery[animationStep[0]].curSwap1 != animationQuery[animationStep[0]].curSwap2) {
                let swap1 = animationQuery[animationStep[0]].curSwap1;
                let swap2 = animationQuery[animationStep[0]].curSwap2;
                
                graphElements.forEach(element => {
                    element.style.transition = `left ${(1 / animationSpeed)}s ease`;
                });

                // swap animation
                var tmp = graphLeft[swap1];
                graphLeft[swap1] = graphLeft[swap2];
                graphLeft[swap2] = tmp;

                await delay(2000 * (1 / animationSpeed));

                graphElements.forEach(element => {
                    element.style.transition = "left 0s ease";
                });

                // non-animation
                numArr = [...animationQuery[animationStep[0] + 1].curArr];
            } else {
                await delay(2000 * (1 / animationSpeed));
            }

            animationStep[0]++;
        }

        graphElements.forEach(element => {
            element.style.transition = "left 0.5s ease, height 0.5s ease";
        });
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
                <div class="explanation-title">단계별 알고리즘 설명</div>
                <div class="explanation">{@html explanation}</div>
            </div>

            <div class="code-container">
                <div class="code-title">의사코드</div>

                <div class="code-area">
                    <!-- 코드의 class="code"로 설정 -->
                    <!-- 들여쓰기는 padding-left:35px -->
                    <div class="code" style="background-color: {codeColor[0]}">for i = 1 to n - 1</div>
                    <div class="code" style="background-color: {codeColor[0]}; padding-left: 35px">for j = n downto i + 1</div>
                    <div class="code" style="background-color: {codeColor[1]}; padding-left: 70px">if A[j] &lt; A[j - 1]</div>
                    <div class="code" style="background-color: {codeColor[2]}; padding-left: 105px">exchange A[j] with A[j - 1]</div>
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