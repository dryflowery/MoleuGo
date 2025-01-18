<script>
    import { onDestroy, onMount, tick   } from "svelte";
    import Header from "../../../component/Header.svelte";
    import StackNavigation from "../../../component/navigation/Linear/StackNavigation.svelte";
    import {isListVisible} from "../../../lib/store.js";
    import { isPaused, pausedIcon, fromBtn, isReplay, explanation, animationSpeed, animationWorking, animationQuery, codeColor, animationStep, asyncCnt, gradient, indentSize, maxSpeed } from "../../../lib/visualizationStore";

    let canvasWidth = window.innerWidth * 0.73;
    let canvasHeight = window.innerHeight * 0.78;

    let numArr = [15, 10, 20, 30, 7] // 실제 값 배열
    let arrowArr = Array(numArr.length - 1).fill(1); // 화살표 배열 개시발 이거 하나떄문에?

    let nodePositions = [];
    let nodeAnimations = [];
    let arrowPositions = [];
    let arrowAnimations = [];

    const nodeWidth = 50;
    const nodeHeight = 50;
    const arrowLength = 50;
    
    const syncArrowArr = () => {
        // Adjust arrowArr length to match numArr.length - 1
        arrowArr = Array(numArr.length - 1).fill(1);
    };

    const calculateNumArrPositions = async () => {
        syncArrowArr();
        nodePositions = numArr.map((_, index) => {
            const totalHeight = (numArr.length - 1) * (nodeHeight + arrowLength);
            const baseY = canvasHeight - totalHeight -(nodeHeight + 50);
            return {
                x: canvasWidth / 2 - nodeWidth / 2,
                y: baseY + index * (nodeHeight + arrowLength),
            };
        });

        arrowPositions = arrowArr.map((_, index) => {
            return {
                x: canvasWidth / 2,
                y: nodePositions[index]?.y + nodeHeight + 4 || 0,
            };
        });

        await tick();
        nodeAnimations = Array(numArr.length).fill(true);
        arrowAnimations = Array(arrowArr.length).fill(true);

        setTimeout(() => {
            nodeAnimations = Array(numArr.length).fill(false);
            arrowAnimations = Array(arrowArr.length).fill(false);
        }, 350); // Animation duration
    };


    onMount(() => {
        calculateNumArrPositions();
        window.addEventListener('resize', () => {
            canvasWidth = window.innerWidth * 0.73;
            canvasHeight = window.innerHeight * 0.78;
            calculateNumArrPositions();
        });
    });

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
        $codeColor = Array($codeColor.length).fill();
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

    //=================================[ Push() ]============================================
    
    const startPush = (e) => {

        InitAnimation();
        
        const pushValue = e.detail.value;
        const pushNum = Number(pushValue.trim());

        generateStackPushQueries(pushNum);

        $animationWorking = true;
        $pausedIcon = false;
        $isPaused = false;

        executeStackPushQuries($asyncCnt++);

    };

    // 애니메이션(Push) 쿼리 저장
    const pushStackPushAnimationQuery = (tmpArr, tmpNodePositions, tmpArrowPositions, tmpNodeAnimations, tmpArrowAnimations, tmpExplanation, tmpNodeBgColor, tmpNodeBorderColor, tmpNodeTextColor, tmpArrowColor,tmpCode) => {
        
        $animationQuery.push({
            curArr: [...tmpArr],
            curNodePositions: [...tmpNodePositions],
            curArrowPositions: [...tmpArrowPositions],
            curNodeAnimation: [...tmpNodeAnimations],
            curArrowAnimations: [...tmpArrowAnimations],
            curExplanation: tmpExplanation,
            curNodeBgColor: tmpNodeBgColor,
            curNodeBorderColor: tmpNodeBorderColor,
            curNodeTextColor: tmpNodeTextColor,
            curArrowColor: tmpArrowColor,
            curCode :tmpCode
        });

    };
    
    // 애니메이션(Push)
    const generateStackPushQueries = (pushValue) => {

        const nodeBg = {normal: "#000000", selected: "#2a9ce8", completed: "#52bc69"};
        const nodeBorderColor = {normal: "#000000", selected: "#2a9ce8", completed: "#52bc69"};
        const textColor = {normal: "#000000", selected: "#ffffff"}
        const arrowColor = {normal: "000000", connecting: "2a9ce8"};

        const resetNodePositions = () => { // 갱신
            nodePositions = numArr.map((_, index) => {
                const totalHeight = (numArr.length - 1) * (nodeHeight + arrowLength);
                const baseY = canvasHeight - totalHeight -(nodeHeight + 50);
                return {
                    x: canvasWidth / 2 - nodeWidth / 2,
                    y: baseY + index * (nodeHeight + arrowLength),
                };
            });
            nodeAnimations.unshift(true);
        };

        const resetArrowPositions = () => {
            syncArrowArr();
            arrowPositions = arrowArr.map((_, index) => {
                return {
                    x: canvasWidth / 2,
                    y: nodePositions[index]?.y + nodeHeight + 4 || 0,
                };
            });
            arrowAnimations.unshift(true);
        };

        $animationQuery = [];

        let tmpArr = [...numArr];
        console.log("1쿼리: ",tmpArr)
        let tmpNodePositions = [...nodePositions];
        let tmpArrowPositions = [...arrowPositions];
        let tmpNodeAnimation = [...nodeAnimations];
        let tmpArrowAnimations = [...arrowAnimations];
        let tmpExplanation = ``;
        let tmpNodeBgColor = nodeBg.normal;
        let tmpNodeBorderColor = nodeBorderColor.normal;
        let tmpNodeTextColor = textColor.normal;
        let tmpArrowColor = arrowColor.normal;
        let tmpCode = 1000;


        tmpExplanation = `배열의 초기 상태입니다`;
        pushStackPushAnimationQuery(tmpArr, tmpNodePositions, tmpArrowPositions, tmpNodeAnimation, tmpArrowAnimations, tmpExplanation,tmpNodeBgColor,tmpNodeBorderColor,tmpNodeTextColor,tmpArrowColor,tmpCode)
        
        // Step 1: 새로운 노드 추가
        numArr.unshift(pushValue); // 숫자 추가
        resetNodePositions(); // 포지션 리셋
        
        tmpArr = [...numArr];
        console.log("2쿼리: ",tmpArr)
        tmpNodePositions = [...nodePositions];
        tmpArrowPositions = [...arrowPositions];
        tmpNodeAnimation = [...nodeAnimations];
        tmpArrowAnimations = [...arrowAnimations];
        tmpNodeBgColor = nodeBg.selected;
        tmpNodeBorderColor = nodeBorderColor.selected;
        tmpNodeTextColor = textColor.selected;
        tmpExplanation = `새로운 노드(${pushValue})가 추가되었습니다.`;
        pushStackPushAnimationQuery(tmpArr, tmpNodePositions, tmpArrowPositions, tmpNodeAnimation, tmpArrowAnimations, tmpExplanation,tmpNodeBgColor,tmpNodeBorderColor,tmpNodeTextColor,tmpArrowColor,tmpCode)

        // Step 2: Connect the arrow
        resetArrowPositions(); // 화살표 포지션 리셋

        tmpNodePositions = [...nodePositions];
        tmpArrowPositions = [...arrowPositions];
        tmpNodeAnimation = [...nodeAnimations];
        tmpArrowAnimations = [...arrowAnimations];
        tmpNodeBgColor = nodeBg.completed;
        tmpNodeBorderColor = nodeBorderColor.completed;
        tmpNodeTextColor = textColor.normal;
        tmpArrowColor = arrowColor.connecting;
        tmpExplanation = `새로운 노드와 기존 노드 간의 연결을 수행 합니다.`;

        pushStackPushAnimationQuery(tmpArr, tmpNodePositions, tmpArrowPositions, tmpNodeAnimation, tmpArrowAnimations, tmpExplanation,tmpNodeBgColor,tmpNodeBorderColor,tmpNodeTextColor,tmpArrowColor,tmpCode)

        // Step 3: Finalize connection
        tmpArrowColor = arrowColor.normal;
        tmpExplanation = `연결이 완료되었습니다.`;

        pushStackPushAnimationQuery(tmpArr, tmpNodePositions, tmpArrowPositions, tmpNodeAnimation, tmpArrowAnimations, tmpExplanation,tmpNodeBgColor,tmpNodeBorderColor,tmpNodeTextColor,tmpArrowColor,tmpCode)

    };

    // 애니메이션(Push) 실행
    const executeStackPushQuries = async (myAsync) => {
        $animationStep = [0, $animationQuery.length - 1];

        while (true) {
            if ((myAsync + 1) !== $asyncCnt) break;

            if ($animationStep[0] === $animationStep[1]) {
                $pausedIcon = true;
                $isPaused = true;
            }

            await drawStackPushAnimation($animationStep[0]);
            await waitPause();
            if($animationSpeed <= 30) await delay(20);

            if (!$fromBtn) {
                $animationStep[0] = Math.min($animationStep[0] + 1, $animationStep[1]);
            }
        }
    };
    // 각 단계의 애니메이션(Push) 렌더링
    const drawStackPushAnimation = async (i) => {

        $explanation = $animationQuery[i].curExplanation; // $explanation 수정
        changeCodeColor($animationQuery[i].curCode); // $codeColor 

        numArr = [...$animationQuery[i].curArr];
        nodePositions = [...$animationQuery[i].curNodePositions];
        nodeAnimations = [...$animationQuery[i].curNodeAnimation];
        arrowPositions = [...$animationQuery[i].curArrowPositions];
        arrowAnimations = [...$animationQuery[i].curArrowAnimations];


        // 슬라이드바 또는 전체 재생 중 처리
        if ($fromBtn || $isReplay) {
            $fromBtn = false;

            numArr = [...$animationQuery[i].curArr];
            nodePositions = [...$animationQuery[i].curNodePositions]

            if ($isReplay) {
                await delay(2000 * (1 / $animationSpeed));
                $isReplay = false;
            } else {
                $isPaused = true;
            }
            return;
        }

        await delay(1000 * (1 / $animationSpeed)); // 애니메이션 지연
    };


    //=================================[ Push() 끝 ]============================================ 

    const createRandomArr = (e) => { 
        InitAnimation();

        const arrCnt = e.detail.arrCnt;

        numArr = [];  

        for (let i = 0; i < arrCnt; i++) {
            numArr.push(Math.floor(Math.random() * 199) - 99);
        }
        calculateNumArrPositions()
    };

    const createInputtedArr = (e) => {
        InitAnimation();

        const tmpArr = e.detail.tmpArr;
        numArr = tmpArr;
        calculateNumArrPositions()
    };

</script>

<main>
    <div class="navigation-container">
        <StackNavigation {numArr}
        on:createRandomArr={createRandomArr} 
        on:createInputtedArr={createInputtedArr} 
        on:startPush={startPush}
        />
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
                스택(Stack)
            </div>

            <div class="canvas">
                <!-- canvas안에 자료구조, 알고리즘 구현 -->

                {#each nodePositions as pos, index (index)}
                    {#if index < numArr.length}
                        <div
                            class="node {nodeAnimations[index] ? 'node-animation' : ''}"
                            style="top: {pos.y}px; left: {pos.x}px; width: {nodeWidth}px; height: {nodeHeight}px;">
                            {numArr[index]}
                        </div>
                    {/if}
                {/each}

                {#each arrowPositions as arrow, index (index)}
                    {#if arrowArr[index] === 1 && index < arrowArr.length}
                        <svg
                            class="arrow"
                            style="top: {arrow.y}px; left: {arrow.x}px;"
                            width="10" height="{arrowLength - 3}" viewBox="0 0 10 {arrowLength}" xmlns="http://www.w3.org/2000/svg">
                            <line
                                class="{arrowAnimations[index] ? 'arrow-line-animation' : ''}"
                                x1="5" y1="0" x2="5" y2="{arrowLength - 10}" 
                                stroke="#000" stroke-width="3" />
                            <polygon
                                class="{arrowAnimations[index] ? 'arrow-head-animation' : ''}"
                                points="0,{arrowLength - 10} 11,{arrowLength - 10} 5,{arrowLength}" 
                                fill="#000" />
                        </svg>
                    {/if}
                {/each}


            </div>

            <!-- svelte-ignore a11y-no-static-element-interactions -->
            <!-- svelte-ignore a11y-click-events-have-key-events -->
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

    .canvas {
        position: relative;
        background-color: #FFFFFF;
        height: 78vh;
        width: 73vw;
        border: 5px solid #313131;
        border-radius: 20px;
        display: flex;
        justify-content: center;
        align-items: center;
        overflow: hidden;
    }

    .node {
        position: absolute;
        background-color: #ffffff;
        color: rgb(0, 0, 0);
        display: flex;
        justify-content: center;
        align-items: center;
        border-radius: 10px;
        border: 4px solid black;
        font-weight: bold;
    }

    .arrow {
        position: absolute;
        transform: translateX(-1px);
        
    }   

    @keyframes grow {
        0% {
            transform: scale(0.5);
            opacity: 0;
        }
        100% {
            transform: scale(1);
            opacity: 1;
        }
    }

    .node-animation {
        animation: grow 0.3s ease-out;
    }

    @keyframes arrow-line-grow {
        0% {
            transform: scaleY(0);
            opacity: 0;
        }
        100% {
            transform: scaleY(1);
            opacity: 1;
        }
    }

    @keyframes arrow-head-move {
        0% {
            transform: translateY(-100%);
            opacity: 0;
        }
        100% {
            transform: translateY(0);
            opacity: 1;
        }
    }

    .arrow-line-animation {
        animation: arrow-line-grow 0.3s ease-out;
        transform-origin: top;
    }

    .arrow-head-animation {
        animation: arrow-head-move 0.3s ease-out;
    }

</style>