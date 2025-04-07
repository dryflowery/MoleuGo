<script>
    import {onDestroy, onMount, tick} from "svelte";
    import Header from "../../../component/Header.svelte";
    import Navigation from "../../../component/navigation/search/BinarySearchNavigation.svelte";
    import { fade } from 'svelte/transition';
    import {isListVisible} from "../../../lib/store";
    import { isPaused, pausedIcon, fromBtn, isReplay, explanation, animationSpeed, animationWorking, animationQuery, codeColor, animationStep,
        asyncCnt, gradient, indentSize, maxSpeed } from "../../../lib/visualizationStore";

    let arr = [];
    let arrLeft, low, mid, high, lowArrowLeft, midArrowLeft, highArrowLeft;

    onMount(() => {
        window.addEventListener("resize", () => {
            setArrPos();
        });

        let elementCnt = Math.floor(Math.random() * 18) + 3;

        let tempArr = [];
        for (let i = 0; i < elementCnt; i++) {
            tempArr.push(Math.floor(Math.random() * 99) + 1);
        }

        arr = tempArr.sort((a, b) => a - b);

        low = 0;
        high = arr.length - 1;
        mid = Math.floor((low + high) / 2);
    });

    // 페이지 바뀌면 애니메이션 종료
    onDestroy(() => {
        $animationSpeed = 1;
        InitAnimation();
    });

    $: arr, setArrPos();

    const setArrPos = async () => {
        let elementWidth = 57;  // 원소 하나의 길이
        let totalWidth = arr.length * elementWidth;  // 전체 원소의 길이
        arrLeft = (window.innerWidth * 0.73 - totalWidth) / 2; // 시작점

        await tick();

        setArrowPos();
    };

    const setArrowPos = () => {
        const indexElement = document.getElementsByClassName('bs-index')
        const lowElement = indexElement[low];
        const midElement = indexElement[mid];
        const highElement = indexElement[high];

        lowArrowLeft = lowElement.getBoundingClientRect().left - arrLeft + 19;
        midArrowLeft = midElement.getBoundingClientRect().left - arrLeft + 19;
        highArrowLeft = highElement.getBoundingClientRect().left - arrLeft + 19;
    };

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

        const elementElements = document.getElementsByClassName('bs-element');
        const indexElements = document.getElementsByClassName('bs-index');
        const arrowElements = document.getElementsByClassName('bs-arrow');

        for (let i = 0; i < elementElements.length; i++) {
            elementElements[i].style.color = "#000000";
            elementElements[i].style.border = "4px solid #000000";
            elementElements[i].style.transition = `color 0s eaes`;
        }

        for (let i = 0; i < indexElements.length; i++) {
            indexElements[i].style.color = "#000000";
            indexElements[i].style.transition = `color 0s eaes`;
        }

        for (let i = 0; i < arrowElements.length; i++) {
            arrowElements[i].style.opacity = 0;
            arrowElements[i].style.transition = `0s ease, left 0s ease`;
        }
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

    const createRandomElement = async (e) => {
        InitAnimation();

        let tableElement = document.getElementById('bs-array');
        tableElement.style.transition = 'opacity 0.25s ease';
        tableElement.style.opacity = 0;
        await delay(250);

        const elementCnt = e.detail.elementCnt;
        arr = [];

        for (let i = 0; i < elementCnt; i++) {
            arr.push(Math.floor(Math.random() * 99) + 1);
        }

        arr.sort((a, b) => a - b);

        low = 0;
        high = arr.length - 1;
        mid = Math.floor((low + high) / 2);

        tableElement.style.opacity = 1;
        await delay(250);
        tableElement.style.transition = 'opacity 0s ease';
    };

    const createInputtedElement = async (e) => {
        InitAnimation();

        let tableElement = document.getElementById('bs-array');
        tableElement.style.transition = 'opacity 0.25s ease';
        tableElement.style.opacity = 0;
        await delay(250);

        arr = e.detail.tmpArr;
        arr.sort((a, b) => a - b);

        low = 0;
        high = arr.length - 1;
        mid = Math.floor((low + high) / 2);

        tableElement.style.opacity = 1;
        await delay(250);
        tableElement.style.transition = 'opacity 0s ease';
    };

    const startBinarySearch = (e) => {
        InitAnimation();

        let key = e.detail.key;
        generateBinarySearchQueries(key);

        $animationWorking = true;
        $pausedIcon = false;
        $isPaused = false;
        executeBinarySearchQueries($asyncCnt++);
    };

    const pushAnimationQuery = (tmpElementColor, tmpLow, tmpMid, tmpHigh, tmpArrowOpacity, tmpExplanation, tmpCode) => {
        $animationQuery.push({
            curElementColor: [...tmpElementColor],
            curLow: tmpLow,
            curMid: tmpMid,
            curHigh: tmpHigh,
            curArrowOpacity: [...tmpArrowOpacity], // low, mid, high
            curExplanation: tmpExplanation,
            curCode: tmpCode
        })
    };

    const generateBinarySearchQueries = (key) => {
        const elementColor = {black: "#000000", green: "#52bc69", orange: "#e97714"};

        $animationQuery = [];
        let tmpElementColor = Array(arr.length).fill(elementColor.black);
        let tmpLow = 0;
        let tmpHigh = arr.length - 1;
        let tmpMid = Math.floor((tmpLow + tmpHigh) / 2);
        let tmpArrowOpacity = [0, 0, 0];
        let tmpExplanation = ``;
        let tmpCode = 1000;

        // 초기 상태
        tmpExplanation = `배열의 초기 상태입니다.\nkey = ${key}`
        pushAnimationQuery(tmpElementColor, tmpLow, tmpMid, tmpHigh, tmpArrowOpacity, tmpExplanation, tmpCode);

        // 0. low, high 설정
        tmpElementColor[0] = tmpElementColor[arr.length - 1] = elementColor.orange;
        tmpArrowOpacity = [1, 0, 1];
        tmpExplanation = `배열의 가장 왼쪽 인덱스를 low(${tmpLow})로, 가장 오른쪽 인덱스를 high(${tmpHigh})로 설정합니다.`;
        tmpCode = 0;
        pushAnimationQuery(tmpElementColor, tmpLow, tmpMid, tmpHigh, tmpArrowOpacity, tmpExplanation, tmpCode);

        while(tmpLow + 1 < tmpHigh) {
            // 1. mid 계산
            tmpMid = Math.floor((tmpLow + tmpHigh) / 2);
            tmpElementColor[tmpMid] = elementColor.green;
            tmpArrowOpacity = [1, 1, 1];
            tmpExplanation = `low(${tmpLow})와 high(${tmpHigh}) 값을 기준으로 중앙 인덱스를 mid(${tmpMid})로 설정합니다.`;
            tmpCode = 1;
            pushAnimationQuery(tmpElementColor, tmpLow, tmpMid, tmpHigh, tmpArrowOpacity, tmpExplanation, tmpCode);

            // 2. arr[mid] < key then low = mid
            tmpExplanation = `arr[mid](${arr[tmpMid]}) < key(${key})이면, low를 mid(${tmpMid})로 설정합니다.`;
            tmpCode = 1;
            pushAnimationQuery(tmpElementColor, tmpLow, tmpMid, tmpHigh, tmpArrowOpacity, tmpExplanation, tmpCode);

            if(arr[tmpMid] < key) {
                // 2. arr[mid] < key then low = mid
                tmpElementColor[tmpLow] = elementColor.black;
                tmpLow = tmpMid;
                tmpElementColor[tmpLow] = elementColor.orange;
                tmpArrowOpacity = [1, 0, 1];
                tmpExplanation = `low를 mid(${tmpMid})로 설정합니다.`;
                tmpCode = 1;
                pushAnimationQuery(tmpElementColor, tmpLow, tmpMid, tmpHigh, tmpArrowOpacity, tmpExplanation, tmpCode);
            }
            else {
                // 2. arr[mid] > key then high = mid
                tmpExplanation = `arr[mid](${arr[tmpMid]}) > key(${key})이면, high를 mid(${tmpMid})로 설정합니다.`;
                tmpCode = 2;
                pushAnimationQuery(tmpElementColor, tmpLow, tmpMid, tmpHigh, tmpArrowOpacity, tmpExplanation, tmpCode);

                if(arr[tmpMid] > key) {
                    // 2. arr[mid] > key then high = mid
                    tmpElementColor[tmpHigh] = elementColor.black;
                    tmpHigh = tmpMid;
                    tmpElementColor[tmpHigh] = elementColor.orange;
                    tmpArrowOpacity = [1, 0, 1];
                    tmpExplanation = `high를 mid(${tmpMid})로 설정합니다.`;
                    tmpCode = 2;
                    pushAnimationQuery(tmpElementColor, tmpLow, tmpMid, tmpHigh, tmpArrowOpacity, tmpExplanation, tmpCode);
                }
                else {
                    // 3. else return mid
                    tmpMid = Math.floor((tmpLow + tmpHigh) / 2);
                    tmpElementColor[tmpMid] = elementColor.green;
                    tmpArrowOpacity = [1, 1, 1];
                    tmpExplanation = `key 값(${key})을(를) 찾았습니다.`;
                    tmpCode = 3;
                    pushAnimationQuery(tmpElementColor, tmpLow, tmpMid, tmpHigh, tmpArrowOpacity, tmpExplanation, tmpCode);
                    return;
                }
            }
        }

        // 4. not exist
        tmpExplanation = `배열에 key 값(${key})이(가) 존재하지 않습니다.`;
        tmpCode = 1000;
        pushAnimationQuery(tmpElementColor, tmpLow, tmpMid, tmpHigh, tmpArrowOpacity, tmpExplanation, tmpCode);
    }

    const executeBinarySearchQueries = async (myAsync) => {
        $animationStep = [0, $animationQuery.length - 1];

        while(true) {
            if((myAsync + 1) != $asyncCnt) {
                break;
            }

            if($animationStep[0] == $animationStep[1]) {
                $pausedIcon = true;
                $isPaused = true;
            }

            await drawBinarySearchAnimation($animationStep[0]);
            await waitPause();

            // 버튼을 통해서 제어하는 경우 $animationStep의 값을 변경하면 안됨. 정해진 $animationStep[0]의 값으로 설정해야 함.
            if(!$fromBtn) {
                $animationStep[0] = Math.min($animationStep[0] + 1, $animationStep[1]);
            }
        }
    };

    const drawBinarySearchAnimation = async (queryNum) => {
        let transitionSecond;

        if($fromBtn || $isReplay) {
            transitionSecond = `0s ease`;
        }
        else {
            transitionSecond = `${(1 / $animationSpeed)}s ease`;
        }

        // element 선택
        const elementElements = document.getElementsByClassName('bs-element');
        const indexElements = document.getElementsByClassName('bs-index');
        const lowArrowElement = document.getElementById('low-arrow');
        const midArrowElement = document.getElementById('mid-arrow');
        const highArrowElement = document.getElementById('high-arrow');

        // transition 설정
        for(let i = 0; i < elementElements.length; i++) {
            elementElements[i].style.transition = `color ${transitionSecond}`;
        }

        for(let i = 0; i < indexElements.length; i++) {
            indexElements[i].style.transition = `color ${transitionSecond}`;
        }

        lowArrowElement.style.transition = `opacity ${transitionSecond}, left ${transitionSecond}`;
        midArrowElement.style.transition = `opacity ${transitionSecond}`;
        highArrowElement.style.transition = `opacity ${transitionSecond}, left ${transitionSecond}`;

        // explanation, codeColor 변경
        $explanation = $animationQuery[queryNum].curExplanation;
        changeCodeColor($animationQuery[queryNum].curCode);

        if(!($fromBtn || $isReplay) && (mid !== $animationQuery[queryNum].curMid)) await delay(2000 * (1 / $animationSpeed));

        // 항상 mid관련 style부터 변경
        mid = $animationQuery[queryNum].curMid;
        midArrowElement.style.opacity = $animationQuery[queryNum].curArrowOpacity[1];
        elementElements[mid].style.color = $animationQuery[queryNum].curElementColor[mid];
        elementElements[mid].style.border = `4px solid ${$animationQuery[queryNum].curElementColor[mid]}`;
        indexElements[mid].style.color = $animationQuery[queryNum].curElementColor[mid];
        setArrowPos();

        if(!($fromBtn || $isReplay) &&
            ((low !== $animationQuery[queryNum].curLow) || high !== $animationQuery[queryNum].curHigh)) await delay(2000 * (1 / $animationSpeed));

        low = $animationQuery[queryNum].curLow;
        high = $animationQuery[queryNum].curHigh;
        lowArrowElement.style.opacity = $animationQuery[queryNum].curArrowOpacity[0];
        highArrowElement.style.opacity = $animationQuery[queryNum].curArrowOpacity[2];
        setArrowPos();

        for(let i = 0; i < elementElements.length; i++) {
            elementElements[i].style.color = $animationQuery[queryNum].curElementColor[i];
            elementElements[i].style.border = `4px solid ${$animationQuery[queryNum].curElementColor[i]}`;
        }

        for(let i = 0; i < indexElements.length; i++) {
            indexElements[i].style.color = $animationQuery[queryNum].curElementColor[i];
        }

        if(!($fromBtn || $isReplay)) await delay(2000 * (1 / $animationSpeed));

        if($fromBtn || $isReplay) {
            $fromBtn = false;

            if($isReplay) {
                await delay(2000 * (1 / $animationSpeed));
                $isReplay = false;
            }
            else {
                $isPaused = true;
            }
        }
    };
</script>

<main>
    <div class="navigation-container">
        <Navigation on:createRandomElement={createRandomElement}
                    on:createInputtedElement={createInputtedElement}
                    on:startBinarySearch={startBinarySearch}
                    arr={arr}/>
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
                이분 탐색(Binary Search)
            </div>

            <div class="canvas">

                <table id="bs-array" style="left: {arrLeft}px" transition:fade>
                    <svg id="low-arrow" class="bs-arrow"
                         width="20" height="50" viewBox="0 0 20 50" xmlns="http://www.w3.org/2000/svg"
                         style="position: absolute; top: 105%; left: {lowArrowLeft}; overflow: visible; opacity: 0;">
                        <line x1="10" y1="50" x2="10" y2="10" stroke="#e97714" stroke-width="3.5" />
                        <polygon points="4,15 16,15 10,5" fill="#e97714" />
                        <text x="10" y="70" font-size="20" text-anchor="middle" fill="#e97714" font-weight="bold">low</text>
                    </svg>

                    <svg id="mid-arrow" class="bs-arrow"
                         width="20" height="50" viewBox="0 0 20 50" xmlns="http://www.w3.org/2000/svg"
                         style="position: absolute; top: 105%; left: {midArrowLeft}; overflow: visible; opacity: 0;">
                        <line x1="10" y1="50" x2="10" y2="10" stroke="#52bc69" stroke-width="3.5" />
                        <polygon points="4,15 16,15 10,5" fill="#52bc69" />
                        <text x="10" y="70" font-size="20" text-anchor="middle" fill="#52bc69" font-weight="bold">mid</text>
                    </svg>

                    <svg id="high-arrow" class="bs-arrow"
                         width="20" height="50" viewBox="0 0 20 50" xmlns="http://www.w3.org/2000/svg"
                         style="position: absolute; top: 105%; left: {highArrowLeft}; overflow: visible; opacity: 0;">
                        <line x1="10" y1="50" x2="10" y2="10" stroke="#e97714" stroke-width="3.5" />
                        <polygon points="4,15 16,15 10,5" fill="#e97714" />
                        <text x="10" y="70" font-size="20" text-anchor="middle" fill="#e97714" font-weight="bold">high</text>
                    </svg>

                    <tr>
                        {#each arr as element, index}
                            <td>
                                <div class="bs-element">{element}</div>
                                <div class="bs-index">{index}</div>
                            </td>
                        {/each}
                    </tr>
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
                    <div class="code" style="background-color: {$codeColor[0]}; padding-left: {0 * $indentSize + 10}px">low = 0</div>
                    <div class="code" style="background-color: {$codeColor[0]}; padding-left: {0 * $indentSize + 10}px">high = arr.length() - 1</div>
                    <br>
                    <div class="code" style="background-color: {$codeColor[1]}; padding-left: {0 * $indentSize + 10}px">while(low + 1 &lt high)</div>
                    <div class="code" style="background-color: {$codeColor[1]}; padding-left: {1 * $indentSize + 10}px">mid = (low + high) / 2</div>
                    <br>
                    <div class="code" style="background-color: {$codeColor[2]}; padding-left: {1 * $indentSize + 10}px">if(arr[mid] &lt key)</div>
                    <div class="code" style="background-color: {$codeColor[2]}; padding-left: {2 * $indentSize + 10}px">low = mid</div>

                    <div class="code" style="background-color: {$codeColor[3]}; padding-left: {1 * $indentSize + 10}px">else if(arr[mid] &gt key)</div>
                    <div class="code" style="background-color: {$codeColor[3]}; padding-left: {2 * $indentSize + 10}px">high = mid</div>

                    <div class="code" style="background-color: {$codeColor[4]}; padding-left: {1 * $indentSize + 10}px">else</div>
                    <div class="code" style="background-color: {$codeColor[4]}; padding-left: {2 * $indentSize + 10}px">return mid</div>
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

    #bs-array {
        position: absolute;
        top: 45%;
    }

    td {
        text-align: center;
    }

    .bs-element {
        position: relative;
        width: 50px;
        height: 50px;
        border: 4px solid black;
        color: #000000;
        border-radius: 10px;
        font-weight: bold;
        display: flex;
        text-align: center;
        justify-content: center;
        align-items: center;
        transition: color 0s ease;
    }

    .bs-index {
        font-weight: bold;
        color: #000000;
        margin-top: 5px;
        transition: color 0s ease;
    }

    .bs-arrow {
        transition: opacity 0s ease, left 0s ease;
    }
</style>