<script>
    import { onDestroy } from "svelte";
    import Header from "../../../component/Header.svelte";
    import Navigation from "../../../component/navigation/sort/SelectionSortNavigation.svelte";
    import {isListVisible} from "../../../lib/store";
    import { isPaused, pausedIcon, fromBtn, isReplay, explanation, animationSpeed, animationWorking, animationQuery, codeColor, animationStep, 
             asyncCnt, gradient, indentSize, maxSpeed } from "../../../lib/visualizationStore";

    let numArr = [15, 10, 20, 30, 7]
    let graphLeft = [];
    let indexLeft = [];
    let isAsc = true;

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
        $codeColor = Array($codeColor.length).fill();
        $animationStep = [0, 0]; 

        const graphElements = document.querySelectorAll('.graph');
        const elementElements = document.querySelectorAll('.element');
        const indexElements = document.querySelectorAll('.index');

        // 색상 수정
        graphElements.forEach(element => {
            element.style.backgroundColor = "#d9d9d9";
        });

        elementElements.forEach(element => {
            element.style.backgroundColor = "#737373";
            element.style.color = "#dcdcdc";
        });

        indexElements.forEach(element => {
            element.style.color = "#000000";
        });

        graphElements.forEach(element => {
            element.style.transition = "left 0.5s ease, height 0.5s ease";
        });
    };

    const createRandomElement = (e) => {
        InitAnimation();

        const elementCnt = e.detail.elementCnt;
        numArr = [];  

        for (let i = 0; i < elementCnt; i++) {
            numArr.push(Math.floor(Math.random() * 38) + 3);
        }
    };

    const createInputtedElement = (e) => {
        InitAnimation();

        const tmpArr = e.detail.tmpArr;
        numArr = tmpArr;
    };

    // SelectionSort animation start
    const startSelectionSort = (e) => {
        InitAnimation();

        isAsc = e.detail.isAsc;
        generateSelectionSortQuries(isAsc);

        $animationWorking = true;
        $pausedIcon = false;
        $isPaused = false;
        executeSelectionSortQuries($asyncCnt++);
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

    const pushAnimationQuery = (tmpArr, tmpGraphBgColor, tmpElementBgColor, tmpElementColor, 
                                tmpIndexColor,tmpSwap1, tmpSwap2, tmpExplanation, tmpCode) => {
            $animationQuery.push({
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

    const generateSelectionSortQuries = (isAsc) => {  
        // min, max 구분하지 않고 min으로 색깔 이름 통일
        const graphBg = {normal: "#d9d9d9", selected: "#ecadae", sorted: "#9fda9b", min: "#adbfec"};
        const elementBg = {normal: "#737373", selected: "#ad7677", sorted: "#6a9068", min: "#7687ad"};
        const elementColor = {normal: "#dcdcdc", selected: "#ffebeb", sorted: "#e8ffe6", min: "#ebebff"};
        const indexColor = {normal: "#000000", selected: "#e05a5d", sorted: "#72c36b", min: "#5577e6"};

        $animationQuery = [];
        let tmpArr = [...numArr];
        let tmpGraphBgColor = Array(tmpArr.length).fill(graphBg.normal);
        let tmpElementBgColor = Array(tmpArr.length).fill(elementBg.normal);
        let tmpElementColor = Array(tmpArr.length).fill(elementColor.normal);
        let tmpIndexColor = Array(tmpArr.length).fill(indexColor.normal);
        let tmpSwap1 = 1000, tmpSwap2 = 1000;
        let tmpExplanation = ``;
        let tmpCode = 1000;
        let minIdx = 1000;

        const initColor = (sortedIdx) => {
            for(let i = sortedIdx + 1; i < tmpArr.length; i++) {
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

        const setMinColor = (minIdx) => {
            tmpGraphBgColor[minIdx] = graphBg.min;
            tmpElementBgColor[minIdx] = elementBg.min;
            tmpElementColor[minIdx] = elementColor.min;
            tmpIndexColor[minIdx] = indexColor.min;
        }

        // 초기 상태
        tmpExplanation = `배열의 초기 상태입니다`
        pushAnimationQuery(tmpArr, tmpGraphBgColor, tmpElementBgColor, tmpElementColor, tmpIndexColor, tmpSwap1, tmpSwap2, tmpExplanation, tmpCode);

        for(let i = 0; i < tmpArr.length - 1; i++) {
            // 인덱스 i부터 tmpArr.length - 1까지 정렬 시작
            // 인덱스 i를 최소값으로 설정
            initColor(i - 1);
            minIdx = i;
            setMinColor(minIdx);
            tmpExplanation = isAsc ? `index ${i}부터 ${tmpArr.length - 1}까지 정렬을 시작합니다.<br>현재 최솟값: ${tmpArr[minIdx]}` :
                                     `index ${i}부터 ${tmpArr.length - 1}까지 정렬을 시작합니다.<br>현재 최댓값: ${tmpArr[minIdx]}`;
            tmpCode = 0;
            pushAnimationQuery(tmpArr, tmpGraphBgColor, tmpElementBgColor, tmpElementColor, tmpIndexColor, tmpSwap1, tmpSwap2, tmpExplanation, tmpCode);

            for(let j = i + 1; j < tmpArr.length; j++) {
                // minIdx의 조건 체크
                setSelectedColor(j, j);
                tmpExplanation = isAsc ? `${tmpArr[j]} < ${tmpArr[minIdx]}(이)면 최솟값을 ${tmpArr[j]}(으)로 설정합니다<br>현재 최솟값: ${tmpArr[minIdx]}` :
                                         `${tmpArr[j]} > ${tmpArr[minIdx]}(이)면 최댓값을 ${tmpArr[j]}(으)로 설정합니다<br>현재 최댓값: ${tmpArr[minIdx]}`;
                tmpCode = 1;
                pushAnimationQuery(tmpArr, tmpGraphBgColor, tmpElementBgColor, tmpElementColor, tmpIndexColor, tmpSwap1, tmpSwap2, tmpExplanation, tmpCode);

                if((isAsc && (tmpArr[j] < tmpArr[minIdx])) || (!isAsc && (tmpArr[j] > tmpArr[minIdx]))) {
                    // 조건을 만족하면 minIdx 변경
                    setNormalColor(minIdx);
                    minIdx = j;
                    setMinColor(minIdx);
                    tmpExplanation = isAsc ? `최솟값을 ${tmpArr[j]}(으)로 설정합니다<br>현재 최솟값: ${tmpArr[minIdx]}` :
                                            `최댓값을 ${tmpArr[j]}(으)로 설정합니다<br>현재 최댓값: ${tmpArr[minIdx]}`;
                    tmpCode = 2;
                    pushAnimationQuery(tmpArr, tmpGraphBgColor, tmpElementBgColor, tmpElementColor, tmpIndexColor, tmpSwap1, tmpSwap2, tmpExplanation, tmpCode);
                }
                else {
                    setNormalColor(j);
                }
            }

            // i != minIdx 체크
            setSelectedColor(i, minIdx);
            tmpExplanation = `${i} != ${minIdx}면 두 원소를 교환합니다`
            tmpCode = 3;
            pushAnimationQuery(tmpArr, tmpGraphBgColor, tmpElementBgColor, tmpElementColor, tmpIndexColor, tmpSwap1, tmpSwap2, tmpExplanation, tmpCode);
            
            if(i != minIdx) {
                // 두 원소 교환
                [tmpArr[i], tmpArr[minIdx]] = [tmpArr[minIdx], tmpArr[i]];
                tmpExplanation = `${tmpArr[i]}과(와) ${tmpArr[minIdx]}을(를) 교환합니다`
                tmpCode = 3;
                pushAnimationQuery(tmpArr, tmpGraphBgColor, tmpElementBgColor, tmpElementColor, tmpIndexColor, i, minIdx, tmpExplanation, tmpCode);
                setNormalColor(minIdx);
            }

            // i까지 정렬 완료
            setSortedColor(i);
            tmpExplanation = `index ${i}까지 정렬이 완료되었습니다`;
            tmpCode = 1000;
            pushAnimationQuery(tmpArr, tmpGraphBgColor, tmpElementBgColor, tmpElementColor, tmpIndexColor, tmpSwap1, tmpSwap2, tmpExplanation, tmpCode);
        }
        
        // 정렬 완료
        initColor(-1);
        tmpExplanation = `배열의 정렬이 완료되었습니다.`
        tmpCode = 1000;
        pushAnimationQuery(tmpArr, tmpGraphBgColor, tmpElementBgColor, tmpElementColor, tmpIndexColor, tmpSwap1, tmpSwap2, tmpExplanation, tmpCode);
    };

    const executeSelectionSortQuries = async (myAsync) => {
        $animationStep = [0, $animationQuery.length - 1];

        while(true) {
            if((myAsync + 1) != $asyncCnt) {
                break;
            }

            if($animationStep[0] == $animationStep[1]) {
                $pausedIcon = true;
                $isPaused = true;
            }

            await drawSelectionSortAnimation($animationStep[0]);
            await waitPause();
            if($animationSpeed <= 30) await delay(20);

            // 버튼을 통해서 제어하는 경우 $animationStep의 값을 변경하면 안됨. 정해진 $animationStep[0]의 값으로 설정해야 함.
            if(!$fromBtn) {
                $animationStep[0] = Math.min($animationStep[0] + 1, $animationStep[1]);
            }
        }
    };

    const drawSelectionSortAnimation = async (i) => {
        const isSwap = $animationQuery[i].curSwap1 != $animationQuery[i].curSwap2;
        const graphElements = document.querySelectorAll('.graph');
        const elementElements = document.querySelectorAll('.element');
        const indexElements = document.querySelectorAll('.index');

        $explanation = $animationQuery[i].curExplanation; // $explanation 수정
        changeCodeColor($animationQuery[i].curCode); // $codeColor 수정

        // 색상 수정
        graphElements.forEach((element, idx) => {
            element.style.backgroundColor = $animationQuery[i].curGraphBgColor[idx];
        });

        elementElements.forEach((element, idx) => {
            element.style.backgroundColor = $animationQuery[i].curElementBgColor[idx];
            element.style.color = $animationQuery[i].curElementColor[idx];
        });

        indexElements.forEach((element, idx) => {
            element.style.color = $animationQuery[i].curIndexColor[idx];
        });

        let swap1, swap2;
        
        // animation-control 영역의 버튼을 통해서 함수가 호출된 경우, 애니메이션을 재생하지 않고 색상, 배열만 변경
        // replay인 경우, 항상 초기 상태의 배열만 출력
        if($fromBtn || $isReplay) {            
            $fromBtn = false;

            graphElements.forEach(element => {
                element.style.transition = "left 0.5s ease, height 0.5s ease";
            });

            numArr = [...$animationQuery[i].curArr];

            if($isReplay) {
                await delay(2000 * (1 / $animationSpeed));
                $isReplay = false;
            }
            else {
                $isPaused = true;
            }

            return;
        }

        // swap이 필요한 경우에만
        if (isSwap) {
            swap1 = $animationQuery[i].curSwap1;
            swap2 = $animationQuery[i].curSwap2;
            
            graphElements.forEach(element => {
                element.style.transition = `left ${(1 / $animationSpeed)}s ease`;
            });

            // swap animation
            [[graphLeft[swap1], graphLeft[swap2]]] = [[graphLeft[swap2], graphLeft[swap1]]];
        }

        await delay(2000 * (1 / $animationSpeed));

        graphElements.forEach(element => {
            element.style.transition = "left 0s ease, height 0s ease";
        });

        if(isSwap) {
            numArr = [...$animationQuery[i].curArr];
        }
    };
</script>

<main>
    <div class="navigation-container">
        <Navigation on:createRandomElement={createRandomElement} 
        on:createInputtedElement={createInputtedElement} 
        on:startSelectionSort={startSelectionSort}
        $animationWorking={$animationWorking}/>
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
                선택 정렬(Selection Sort) 
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
                    {#if isAsc}
                        <div class="code" style="background-color: {$codeColor[0]}; padding-left: {0 * $indentSize + 10}px">for i = 0 to n - 2</div>
                        <div class="code" style="background-color: {$codeColor[0]}; padding-left: {0 * $indentSize + 10}px">min = i</div>
                        <div class="code" style="background-color: {$codeColor[0]}; padding-left: {0 * $indentSize + 10}px">for j = i + 1 to n - 2</div>
                        <div class="code" style="background-color: {$codeColor[1]}; padding-left: {1 * $indentSize + 10}px">if A[j] &lt; A[min] then</div>
                        <div class="code" style="background-color: {$codeColor[2]}; padding-left: {2 * $indentSize + 10}px">min = j</div>
                        <div class="code" style="background-color: {$codeColor[3]}; padding-left: {0 * $indentSize + 10}px">if min != i then swap A[i] and A[min]</div>
                    {:else}
                        <div class="code" style="background-color: {$codeColor[0]}; padding-left: {0 * $indentSize + 10}px">for i = 0 to n - 2</div>
                        <div class="code" style="background-color: {$codeColor[0]}; padding-left: {0 * $indentSize + 10}px">max = i</div>
                        <div class="code" style="background-color: {$codeColor[0]}; padding-left: {0 * $indentSize + 10}px">for j = i + 1 to n - 2</div>
                        <div class="code" style="background-color: {$codeColor[1]}; padding-left: {1 * $indentSize + 10}px">if A[j] &gt; A[max] then</div>
                        <div class="code" style="background-color: {$codeColor[2]}; padding-left: {2 * $indentSize + 10}px">max = j</div>
                        <div class="code" style="background-color: {$codeColor[3]}; padding-left: {0 * $indentSize + 10}px">if max != i then swap A[i] and A[max]</div>
                    {/if}
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
        bottom: 30px; 
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
        bottom: 0px; 
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