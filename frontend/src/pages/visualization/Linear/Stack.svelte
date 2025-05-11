<script>
    import { onDestroy, onMount, tick   } from "svelte";
    import Header from "../../../component/Header.svelte";
    import StackNavigation from "../../../component/navigation/Linear/StackNavigation.svelte";
    import {isListVisible} from "../../../lib/store.js";
    import { isPaused, pausedIcon, fromBtn, isReplay, explanation, animationSpeed, animationWorking, animationQuery, codeColor, animationStep, asyncCnt, gradient, indentSize, maxSpeed } from "../../../lib/visualizationStore";
    import {incrementAnimationCount, verifyGoal} from "../../../lib/updateMypageInfo.js";

    let canvasWidth = window.innerWidth * 0.73;
    let canvasHeight = window.innerHeight * 0.78;

    let operation = '';

    let numArr = [15, 10, 20, 30, 7] // 실제 값 배열
    let arrowArr = Array(numArr.length - 1).fill(1); // 화살표 배열(1: true , 0: false)
    let nodePositions = [];
    let nodeAnimations = [];
    let arrowPositions = [];
    let arrowAnimations = [];

    let nodePopAnimations = [];
    let arrowPopAnimations = [];

    let flagAnimation = false; 
    let flagDisappearAnimation = false;
    let hasPeekRun = false;  // peek 연산이 실행되었는지 확인하는 변수

    const nodeWidth = 50;
    const nodeHeight = 50;
    const arrowLength = 50;

    $: {
        if (!flagAnimation && hasPeekRun) {
            flagDisappearAnimation = true;
        } else {
            flagDisappearAnimation = false;
        }
    }

        
    const syncArrowArr = () => {
        arrowArr = Array(numArr.length - 1).fill(1);
    };

    const resetNodeStyles = () => {
        document.querySelectorAll('.node').forEach(node => {
            node.classList.remove('no-transition');
            node.style.transition = "";
        });
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
        }, 500); 

    };

    const calculateNumArrPositionsNA = async () => { // 애니메이션 없이 갱신하는거
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

    $: {
        const root = document.documentElement;
        root.style.setProperty('--speed', $animationSpeed || 1); // 애니메이션 속도 조절
    }



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

        resetNodeStyles();

        flagAnimation = false;

        nodeAnimations = Array(numArr.length).fill(false);
        arrowAnimations = Array(arrowArr.length).fill(false);
        nodePopAnimations = Array(numArr.length).fill(false);
        arrowPopAnimations = Array(arrowArr.length).fill(false);

        const node = document.querySelectorAll('.node');
        const arrowLines = document.querySelectorAll('.arrow line');
        const arrowPolygons = document.querySelectorAll('.arrow polygon');
   

        node.forEach(element => { 
            element.style.backgroundColor = "#ffffff";
            element.style.borderColor = "#000000";
            element.style.color = "#000000";
        });

        arrowLines.forEach(line => {
            line.setAttribute('stroke', "#000000");
        });

        arrowPolygons.forEach(polygon => {
            polygon.setAttribute('fill', "#000000");
        });

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

    //=================================[ Peek() ]============================================

    const startPeek = () => {
        operation = 'peek';
        InitAnimation();

        generateStackPeekQueries();
        
        $animationWorking = true;
        $pausedIcon = false;
        $isPaused = false;

        incrementAnimationCount("stack");
        verifyGoal("stack");
        executeStackPeekQueries($asyncCnt++);
    };

    const pushStackPeekAnimationQuery = (tmpExplanation, tmpFlagAnimation, tmpNodeBgColor, tmpNodeBorderColor, tmpNodeTextColor,tmpCode) => {
        
        $animationQuery.push({
            curExplanation: tmpExplanation,
            curFlagAnimation: tmpFlagAnimation,
            curNodeBgColor: [...tmpNodeBgColor],
            curNodeBorderColor: [...tmpNodeBorderColor],
            curNodeTextColor: [...tmpNodeTextColor],
            curCode :tmpCode
        });

    };

    const generateStackPeekQueries = () => {

        const nodeBg = {normal: "#ffffff", selected: "#ed8925", completed: "#52bc69", hidden: "#ffffff"};
        const nodeBorderColor = {normal: "#000000", selected: "#d97511", completed: "#13a300", hidden: "#ffffff"};
        const textColor = {normal: "#000000", selected: "#ffffff", hidden: "#ffffff"}

        $animationQuery = [];

        if (numArr.length === 0) {
            
            pushStackPeekAnimationQuery(`스택이 비어있는지 확인합니다`, [], [], [], 0);

            return;  // 비어있는 경우
        }


        let tmpArr = [...numArr];
        let tmpExplanation = ``;
        let tmpFlagAnimation = flagAnimation;
        let tmpNodeBgColor = Array(tmpArr.length).fill(nodeBg.normal);
        let tmpNodeBorderColor = Array(tmpArr.length).fill(nodeBorderColor.normal);
        let tmpNodeTextColor = Array(tmpArr.length).fill(textColor.normal);
        let tmpCode = 1000;

        tmpExplanation = `배열의 초기 상태입니다`;
        pushStackPeekAnimationQuery(tmpExplanation, tmpFlagAnimation, tmpNodeBgColor, tmpNodeBorderColor, tmpNodeTextColor,tmpCode);

        tmpFlagAnimation = true;
        tmpExplanation = `head 노드의 값(${tmpArr[0]})을 반환합니다`
        tmpCode = 1;

        tmpNodeBgColor[0] = nodeBg.completed; 
        tmpNodeBorderColor[0] = nodeBorderColor.completed;
        tmpNodeTextColor[0] = textColor.selected;

        pushStackPeekAnimationQuery(tmpExplanation, tmpFlagAnimation ,tmpNodeBgColor, tmpNodeBorderColor, tmpNodeTextColor,tmpCode);

    }

    const executeStackPeekQueries = async (myAsync) => {
        $animationStep = [0, $animationQuery.length - 1];

        while (true) {
            if ((myAsync + 1) !== $asyncCnt) break;

            if ($animationStep[0] === $animationStep[1]) {
                $pausedIcon = true;
                $isPaused = true;
            }

            await drawStackPeekAnimation($animationStep[0]);
            await waitPause();
            if($animationSpeed <= 30) await delay(20);

            if (!$fromBtn) {
                $animationStep[0] = Math.min($animationStep[0] + 1, $animationStep[1]);
            }
        }
    }; 

    const drawStackPeekAnimation = async (i) => {

        $explanation = $animationQuery[i].curExplanation; // $explanation 수정
        changeCodeColor($animationQuery[i].curCode); // $codeColor 

        flagAnimation = $animationQuery[i].curFlagAnimation; // 깃발 상태 

        await tick(); // UI 갱신을 위해 tick() 사용

        if (flagAnimation) {
            await delay(500);  // 깃발 애니메이션 효과
        }

        const nodeBgColors = $animationQuery[i].curNodeBgColor;
        const nodeBorderColors = $animationQuery[i].curNodeBorderColor;
        const nodeTextColors = $animationQuery[i].curNodeTextColor;

        document.querySelectorAll('.node').forEach((node, index) => {
            if (nodeBgColors[index]) node.style.backgroundColor = nodeBgColors[index];
            if (nodeBorderColors[index]) node.style.borderColor = nodeBorderColors[index];
            if (nodeTextColors[index]) node.style.color = nodeTextColors[index];
        });

        if (i === $animationQuery.length - 1) {
            hasPeekRun = true;
            calculateNumArrPositionsNA();
        }
  
        // 슬라이드바 또는 전체 재생 중 처리
        if ($fromBtn || $isReplay) {

            $fromBtn = false;

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


    //=================================[ Pop() ]============================================

    const startPop = (e) => {
        operation = 'pop';
        InitAnimation();
    
        generateStackPopQueries();

        $animationWorking = true;
        $pausedIcon = false;
        $isPaused = false;

        incrementAnimationCount("stack");
        verifyGoal("stack");
        executeStackPopQueries($asyncCnt++);
    };

    const pushStackPopAnimationQuery = (tmpArr, tmpArrowArr, tmpNodePositions, tmpArrowPositions, tmpNodePopAnimations, tmpArrowPopAnimations, tmpExplanation, tmpNodeBgColor, tmpNodeBorderColor, tmpNodeTextColor, tmpArrowColor,tmpCode) => {
        
        $animationQuery.push({
            curArr: [...tmpArr],
            curArrowArr: [...tmpArrowArr],
            curNodePositions: [...tmpNodePositions],
            curArrowPositions: [...tmpArrowPositions],
            curNodePopAnimation: [...tmpNodePopAnimations],
            curArrowPopAnimations: [...tmpArrowPopAnimations],
            curExplanation: tmpExplanation,
            curNodeBgColor: [...tmpNodeBgColor],
            curNodeBorderColor: [...tmpNodeBorderColor],
            curNodeTextColor: [...tmpNodeTextColor],
            curArrowColor: [...tmpArrowColor],
            curCode :tmpCode
        });

    };


    $: if (arrowPopAnimations[0]) {
        setTimeout(() => {
            arrowArr[0] = 0;
        }, 450 / $animationSpeed); 
    }

    $: if (nodePopAnimations[0]) {
        setTimeout(() => {
            const nodes = document.querySelectorAll('.node');
            if (nodes.length > 0) {

                nodes[0].style.backgroundColor = "#ffffff";
                nodes[0].style.borderColor = "#ffffff";
                nodes[0].style.color = "#ffffff";
                nodes[0].style.transition = "none";

            }
        }, (250 / $animationSpeed) + 10); 
    }


    const generateStackPopQueries = () => {

        const nodeBg = {normal: "#ffffff", selected: "#ed8925", completed: "#52bc69", hidden: "#ffffff"};
        const nodeBorderColor = {normal: "#000000", selected: "#d97511", completed: "#13a300", hidden: "#ffffff"};
        const textColor = {normal: "#000000", selected: "#ffffff", hidden: "#ffffff"}
        const arrowColor = {normal: "#000000", connecting: "#d97511"};

        const resetNodePositions = () => { // 노드 포지션 갱신
            nodePositions = numArr.map((_, index) => {
                const totalHeight = (numArr.length - 1) * (nodeHeight + arrowLength);
                const baseY = canvasHeight - totalHeight -(nodeHeight + 50);
                return {
                    x: canvasWidth / 2 - nodeWidth / 2,
                    y: baseY + index * (nodeHeight + arrowLength),
                };
            });
        };

        $animationQuery = [];

        if (numArr.length === 0) {
            
            pushStackPopAnimationQuery([], [], [], [], [], [], `스택이 비어있어 삭제할 항목이 없습니다`, [], [], [], [], 0);

            return;  // 비어있는 경우
        }


        let tmpArr = [...numArr];
        let tmpArrowArr = [...arrowArr];
        let tmpNodePositions = [...nodePositions];
        let tmpArrowPositions = [...arrowPositions];
        let tmpNodePopAnimations = [...nodePopAnimations];
        let tmpArrowPopAnimations = [...arrowPopAnimations];
        let tmpExplanation = ``;
        let tmpNodeBgColor = Array(tmpArr.length).fill(nodeBg.normal);
        let tmpNodeBorderColor = Array(tmpArr.length).fill(nodeBorderColor.normal);
        let tmpNodeTextColor = Array(tmpArr.length).fill(textColor.normal);
        let tmpArrowColor = Array(tmpArrowArr.length).fill(arrowColor.normal);
        let tmpCode = 1000;

        tmpExplanation = `배열의 초기 상태입니다`;
        pushStackPopAnimationQuery(tmpArr, tmpArrowArr, tmpNodePositions, tmpArrowPositions, tmpNodePopAnimations, tmpArrowPopAnimations, tmpExplanation, tmpNodeBgColor, tmpNodeBorderColor, tmpNodeTextColor, tmpArrowColor,tmpCode);

        tmpExplanation = `배열의 초기 상태입니다`; // 애니메이션 재실행 보험용 쿼리
        pushStackPopAnimationQuery(tmpArr, tmpArrowArr, tmpNodePositions, tmpArrowPositions, tmpNodePopAnimations, tmpArrowPopAnimations, tmpExplanation, tmpNodeBgColor, tmpNodeBorderColor, tmpNodeTextColor, tmpArrowColor,tmpCode);


        // Step 1
        tmpCode = 1;

        tmpNodeBgColor[0] = nodeBg.selected; 
        tmpNodeBorderColor[0] = nodeBorderColor.selected;
        tmpNodeTextColor[0] = textColor.selected;

        tmpArr = [...numArr];
        tmpArrowArr = [...arrowArr];
        tmpNodePositions = [...nodePositions];
        tmpArrowPositions = [...arrowPositions];
        tmpNodePopAnimations = [...nodePopAnimations];
        tmpArrowPopAnimations = [...arrowPopAnimations];

        tmpExplanation = `head 노드의 값(${tmpArr[0]})을 임시 변수에 저장합니다`;


        pushStackPopAnimationQuery(tmpArr, tmpArrowArr, tmpNodePositions, tmpArrowPositions, tmpNodePopAnimations, tmpArrowPopAnimations, tmpExplanation, tmpNodeBgColor, tmpNodeBorderColor, tmpNodeTextColor, tmpArrowColor,tmpCode);

        // Step 2
        tmpCode = 2;
        arrowPopAnimations[0] = true; // 화살표 삭제 애니메이션

        tmpNodeBgColor[1] = nodeBg.completed;
        tmpNodeBorderColor[1] = nodeBorderColor.completed;
        tmpNodeTextColor[1] = textColor.selected;

        tmpArr = [...numArr];
        tmpArrowArr = [...arrowArr];
        tmpNodePositions = [...nodePositions];
        tmpArrowPositions = [...arrowPositions];
        tmpNodePopAnimations = [...nodePopAnimations];
        tmpArrowPopAnimations = [...arrowPopAnimations];

        tmpExplanation = `head 포인터를 다음 노드(${tmpArr[1]})로 변경합니다`;
        pushStackPopAnimationQuery(tmpArr, tmpArrowArr, tmpNodePositions, tmpArrowPositions, tmpNodePopAnimations, tmpArrowPopAnimations, tmpExplanation, tmpNodeBgColor, tmpNodeBorderColor, tmpNodeTextColor, tmpArrowColor,tmpCode);

        // Step 3
        tmpCode = 3;

        arrowArr[0] = 0; // 화살표 없는상태 유지
        arrowPopAnimations[0] = false; 
        nodePopAnimations[0] = true; // true 이후 반응형으로 첫 노드 하얀색으로 변경(히든)


        tmpExplanation = `저장한 값(${tmpArr[0]})을 반환합니다`;
        
        tmpArr = [...numArr];
        tmpArrowArr = [...arrowArr];
        tmpNodePositions = [...nodePositions];
        tmpArrowPositions = [...arrowPositions];
        tmpNodePopAnimations = [...nodePopAnimations];
        tmpArrowPopAnimations = [...arrowPopAnimations];
        
        pushStackPopAnimationQuery(tmpArr, tmpArrowArr, tmpNodePositions, tmpArrowPositions, tmpNodePopAnimations, tmpArrowPopAnimations, tmpExplanation, tmpNodeBgColor, tmpNodeBorderColor, tmpNodeTextColor, tmpArrowColor,tmpCode);

        // step 4
        tmpCode = 3;
        tmpExplanation = `삭제연산 완료`;
        nodePopAnimations[0] = false;

        numArr.shift();
        arrowArr[0] = 1;    
        resetNodePositions();
        calculateNumArrPositionsNA();

        tmpNodeBgColor[0] = nodeBg.completed; 
        tmpNodeBorderColor[0] = nodeBorderColor.completed;
        tmpNodeTextColor[0] = textColor.selected;

        tmpNodeBgColor[1] = nodeBg.normal;
        tmpNodeBorderColor[1] = nodeBorderColor.normal;
        tmpNodeTextColor[1] = textColor.normal;


        tmpArr = [...numArr];
        tmpArrowArr = [...arrowArr];
        tmpNodePositions = [...nodePositions];
        tmpArrowPositions = [...arrowPositions];
        tmpNodePopAnimations = [...nodePopAnimations];

        pushStackPopAnimationQuery(tmpArr, tmpArrowArr, tmpNodePositions, tmpArrowPositions, tmpNodePopAnimations, tmpArrowPopAnimations, tmpExplanation, tmpNodeBgColor, tmpNodeBorderColor, tmpNodeTextColor, tmpArrowColor,tmpCode);
    };

    const executeStackPopQueries = async (myAsync) => {
        $animationStep = [0, $animationQuery.length - 1];

        while (true) {
            if ((myAsync + 1) !== $asyncCnt) break;

            if ($animationStep[0] === $animationStep[1]) {
                $pausedIcon = true;
                $isPaused = true;
            }

            await drawStackPopAnimation($animationStep[0]);
            await waitPause();
            if($animationSpeed <= 30) await delay(20);

            if (!$fromBtn) {
                $animationStep[0] = Math.min($animationStep[0] + 1, $animationStep[1]);
            }
        }
    };

    const drawStackPopAnimation = async (i) => {

        $explanation = $animationQuery[i].curExplanation; // $explanation 수정
        changeCodeColor($animationQuery[i].curCode); // $codeColor 

        numArr = [...$animationQuery[i].curArr];
        arrowArr = [...$animationQuery[i].curArrowArr];

        nodePositions = [...$animationQuery[i].curNodePositions];
        nodePopAnimations = [...$animationQuery[i].curNodePopAnimation];
        arrowPositions = [...$animationQuery[i].curArrowPositions];
        arrowPopAnimations = [...$animationQuery[i].curArrowPopAnimations];

        const nodeBgColors = $animationQuery[i].curNodeBgColor;
        const nodeBorderColors = $animationQuery[i].curNodeBorderColor;
        const nodeTextColors = $animationQuery[i].curNodeTextColor;
        const arrowColors = $animationQuery[i].curArrowColor;

        document.querySelectorAll('.node').forEach((node, index) => {
            if (nodeBgColors[index]) {
                node.style.backgroundColor = nodeBgColors[index];
                node.style.borderColor = nodeBorderColors[index];
                node.style.color = nodeTextColors[index];

                // 마지막 쿼리에서 트랜지션 제거
                if (i === $animationQuery.length - 1) {
                    node.style.transition = "none";
                } else {
                    node.style.transition = "";
                }
            }
        });

        // 화살표 스타일 동적으로 적용
        document.querySelectorAll('.arrow line').forEach((line, index) => {
            if (arrowColors[index]) line.setAttribute('stroke', arrowColors[index]);
        });

        document.querySelectorAll('.arrow polygon').forEach((polygon, index) => {
            if (arrowColors[index]) polygon.setAttribute('fill', arrowColors[index]);
        });

        // 슬라이드바 또는 전체 재생 중 처리
        if ($fromBtn || $isReplay) {

            $fromBtn = false;

            if ($isReplay) {
                await delay(2000 * (1 / $animationSpeed));
                $isReplay = false;
            } else {
                $isPaused = true;
            }
            return;
        }

        if (i === $animationQuery.length - 1) { // 애니메이션 종료시
            numArr = [...numArr];
            calculateNumArrPositionsNA();
        }

        await delay(1000 * (1 / $animationSpeed)); // 애니메이션 지연
    };


    //=================================[ Push() ]============================================
    
    const startPush = (e) => {
        operation = 'push';
        InitAnimation();
        
        const pushValue = e.detail.value;
        const pushNum = Number(pushValue.trim());

        generateStackPushQueries(pushNum);

        $animationWorking = true;
        $pausedIcon = false;
        $isPaused = false;

        incrementAnimationCount("stack");
        verifyGoal("stack");
        executeStackPushQueries($asyncCnt++);
    };

    // 애니메이션(Push) 쿼리 저장
    const pushStackPushAnimationQuery = (tmpArr, tmpArrowArr, tmpNodePositions, tmpArrowPositions, tmpNodeAnimationss, tmpArrowAnimations, tmpExplanation, tmpNodeBgColor, tmpNodeBorderColor, tmpNodeTextColor, tmpArrowColor,tmpCode) => {
        
        $animationQuery.push({
            curArr: [...tmpArr],
            curArrowArr: [...tmpArrowArr],
            curNodePositions: [...tmpNodePositions],
            curArrowPositions: [...tmpArrowPositions],
            curNodeAnimation: [...tmpNodeAnimationss],
            curArrowAnimations: [...tmpArrowAnimations],
            curExplanation: tmpExplanation,
            curNodeBgColor: [...tmpNodeBgColor],
            curNodeBorderColor: [...tmpNodeBorderColor],
            curNodeTextColor: [...tmpNodeTextColor],
            curArrowColor: [...tmpArrowColor],
            curCode :tmpCode
        });

    };
    
    // 애니메이션(Push)
    const generateStackPushQueries = (pushValue) => {

        const nodeBg = {normal: "#ffffff", selected: "#ed8925", completed: "#52bc69"};
        const nodeBorderColor = {normal: "#000000", selected: "#d97511", completed: "#13a300"};
        const textColor = {normal: "#000000", selected: "#ffffff"}
        const arrowColor = {normal: "#000000", connecting: "#d97511"};

        const resetNodePositions = () => { // 노드 포지션 갱신
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

        const resetArrowPositions = () => { // 화살표 포지션 갱신
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
        let tmpArrowArr = [...arrowArr];
        let tmpNodePositions = [...nodePositions];
        let tmpArrowPositions = [...arrowPositions];
        let tmpNodeAnimations = [...nodeAnimations];
        let tmpArrowAnimations = [...arrowAnimations];
        let tmpExplanation = ``;
        let tmpNodeBgColor = Array(tmpArr.length).fill(nodeBg.normal);
        let tmpNodeBorderColor = Array(tmpArr.length).fill(nodeBorderColor.normal);
        let tmpNodeTextColor = Array(tmpArr.length).fill(textColor.normal);
        let tmpArrowColor = Array(tmpArrowArr.length).fill(arrowColor.normal);
        let tmpCode = 1000;

        tmpExplanation = `배열의 초기 상태입니다.`;
        pushStackPushAnimationQuery(tmpArr, tmpArrowArr, tmpNodePositions, tmpArrowPositions, tmpNodeAnimations, tmpArrowAnimations, tmpExplanation, tmpNodeBgColor, tmpNodeBorderColor, tmpNodeTextColor, tmpArrowColor,tmpCode);

        tmpExplanation = `배열의 초기 상태입니다.`; // 애니메이션 재실행 보험용 쿼리
        pushStackPushAnimationQuery(tmpArr, tmpArrowArr, tmpNodePositions, tmpArrowPositions, tmpNodeAnimations, tmpArrowAnimations, tmpExplanation,tmpNodeBgColor,tmpNodeBorderColor,tmpNodeTextColor,tmpArrowColor,tmpCode);
        
        
        // Step 1: 새로운 노드 추가
        numArr.unshift(pushValue); // 숫자 추가
        resetNodePositions(); // 포지션 리셋

        tmpNodeBgColor[0] = nodeBg.selected; // 파란색
        tmpNodeBorderColor[0] = nodeBorderColor.selected;
        tmpNodeTextColor[0] = textColor.selected;
        
        tmpCode = 0;

        tmpArr = [...numArr];
        tmpArrowArr = [...arrowArr];
        tmpNodePositions = [...nodePositions];
        tmpArrowPositions = [...arrowPositions];
        tmpNodeAnimations = [...nodeAnimations];
        tmpArrowAnimations = [...arrowAnimations];
    
        tmpExplanation = `새로운 노드(${pushValue})가 추가되었습니다`;
        pushStackPushAnimationQuery(tmpArr, tmpArrowArr, tmpNodePositions, tmpArrowPositions, tmpNodeAnimations, tmpArrowAnimations, tmpExplanation,tmpNodeBgColor,tmpNodeBorderColor,tmpNodeTextColor,tmpArrowColor,tmpCode);

        // Step 2: Connect the arrow
        tmpArrowColor[0] = arrowColor.connecting;
        resetArrowPositions(); // 화살표 포지션 리셋

        tmpCode = 1;

        tmpArrowArr = [...arrowArr];
        tmpNodePositions = [...nodePositions];
        tmpArrowPositions = [...arrowPositions];
        tmpNodeAnimations = [...nodeAnimations];
        tmpArrowAnimations = [...arrowAnimations];

        tmpExplanation = `새로운 노드(${pushValue})와 기존 노드(${tmpArr[1]}) 간의 연결을 수행 합니다`;

        pushStackPushAnimationQuery(tmpArr, tmpArrowArr, tmpNodePositions, tmpArrowPositions, tmpNodeAnimations, tmpArrowAnimations, tmpExplanation,tmpNodeBgColor,tmpNodeBorderColor,tmpNodeTextColor,tmpArrowColor,tmpCode);
        
        
        // Step 3: Finalize connection
        tmpExplanation = `연결이 완료되었습니다`;

        tmpCode = 2;

        tmpArrowColor[0] = arrowColor.normal;
        tmpNodeBgColor[0] = nodeBg.completed;
        tmpNodeBorderColor[0] = nodeBorderColor.completed;
        tmpNodeTextColor[0] = textColor.selected;

        pushStackPushAnimationQuery(tmpArr, tmpArrowArr, tmpNodePositions, tmpArrowPositions, tmpNodeAnimations, tmpArrowAnimations, tmpExplanation, tmpNodeBgColor, tmpNodeBorderColor, tmpNodeTextColor, tmpArrowColor, tmpCode);
    };

    // 애니메이션(Push) 실행
    const executeStackPushQueries = async (myAsync) => {
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

        arrowArr = [...$animationQuery[i].curArrowArr];

        nodePositions = [...$animationQuery[i].curNodePositions];
        nodeAnimations = [...$animationQuery[i].curNodeAnimation];
        arrowPositions = [...$animationQuery[i].curArrowPositions];
        arrowAnimations = [...$animationQuery[i].curArrowAnimations];

        const nodeBgColors = $animationQuery[i].curNodeBgColor;
        const nodeBorderColors = $animationQuery[i].curNodeBorderColor;
        const nodeTextColors = $animationQuery[i].curNodeTextColor;
        const arrowColors = $animationQuery[i].curArrowColor;

        document.querySelectorAll('.node').forEach((node, index) => {
            if (nodeBgColors[index]) node.style.backgroundColor = nodeBgColors[index];
            if (nodeBorderColors[index]) node.style.borderColor = nodeBorderColors[index];
            if (nodeTextColors[index]) node.style.color = nodeTextColors[index];
        });

        // 화살표 스타일 동적으로 적용
        document.querySelectorAll('.arrow line').forEach((line, index) => {
            if (arrowColors[index]) line.setAttribute('stroke', arrowColors[index]);
        });

        document.querySelectorAll('.arrow polygon').forEach((polygon, index) => {
            if (arrowColors[index]) polygon.setAttribute('fill', arrowColors[index]);
        });

        // 슬라이드바 또는 전체 재생 중 처리
        if ($fromBtn || $isReplay) {

            $fromBtn = false;

            if ($isReplay) {
                await delay(2000 * (1 / $animationSpeed));
                $isReplay = false;
            } else {
                $isPaused = true;
            }
            return;
        }

        if (i === $animationQuery.length - 1) { // 애니메이션 종료시
            numArr = [...numArr];
            calculateNumArrPositionsNA();
            nodeAnimations = Array(numArr.length).fill(false);
            arrowAnimations = Array(arrowArr.length).fill(false);
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
        calculateNumArrPositions();
    };

    const createInputtedArr = (e) => {
        InitAnimation();

        const tmpArr = e.detail.tmpArr;
        numArr = tmpArr;
        calculateNumArrPositions();
    };

</script>

<main>
    <div class="navigation-container">
        <StackNavigation {numArr}
        on:createRandomArr={createRandomArr} 
        on:createInputtedArr={createInputtedArr} 
        on:startPush={startPush}
        on:startPop={startPop}
        on:startPeek={startPeek}
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
                            class="node {nodeAnimations[index] ? 'node-animation' : ''} {nodePopAnimations[index] ? 'node-pop-animation' : ''} {index === 0 && flagAnimation ? 'flagged' : ''}"
                            style="top: {pos.y}px; left: {pos.x}px; width: {nodeWidth}px; height: {nodeHeight}px;">
                            {numArr[index]}

                            {#if index === 0}
                                <div class="flag-container {flagAnimation ? 'appear' : ''} {flagDisappearAnimation ? 'disappear' : ''}">
                                    <div class="flag-pole"></div>
                                    <div class="flag-cloth"></div>
                                </div>
                            {/if}

                        </div>
                    {/if}
                {/each}

                {#each arrowPositions as arrow, index (index)}
                    {#if arrowArr[index] === 1 && index < arrowArr.length}
                        <svg
                            class="arrow"
                            style="top: {arrow.y + 4}px; left: {arrow.x}px;"
                            width="10" height="{arrowLength - 7}" viewBox="0 0 10 {arrowLength}" xmlns="http://www.w3.org/2000/svg"
                            >
                            <line
                                class="{arrowAnimations[index] ? 'arrow-line-animation' : ''} {arrowPopAnimations[index] ? 'arrow-line-animation-pop' : ''}"
                                x1="5" y1="0" x2="5" y2="{arrowLength - 10}" 
                                stroke="#000" stroke-width="3.5" />
                            <polygon
                                class="{arrowAnimations[index] ? 'arrow-head-animation' : ''} {arrowPopAnimations[index] ? 'arrow-head-animation-pop' : ''}"
                                points="0,{arrowLength - 10} 11,{arrowLength - 10} 5,{arrowLength}" 
                                fill="#000" />
                        </svg>
                    {/if}
                {/each}

                <div class="max-indicator">
                    <div class="max-line"></div>
                    <div class="max-label">MAX</div>
                </div>
                
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
                    on:input={() => {if($animationWorking) {$isPaused = false; $pausedIcon = true; $fromBtn = true; }}}
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
                <div class="explanation-title">
                    {#if operation === 'push'}
                        단계별 알고리즘 설명 - 삽입
                    {:else if operation === 'pop'}
                        단계별 알고리즘 설명 - 삭제
                    {:else if operation === 'peek'}
                        단계별 알고리즘 설명 - 조회
                    {:else}
                        단계별 알고리즘 설명
                    {/if}
                </div>
                <div class="explanation">{@html $explanation}</div>
            </div>

            <div class="code-container">
                <div class="code-title">의사코드</div>

                <div class="code-area">
                    <!-- 코드의 class="code"로 설정 -->
                    <!-- 들여쓰기는 padding-left:35px -->
                     
                    {#if operation === 'push'}
                        <!-- Push 연산 의사코드 -->
                        <div class="code" style="background-color: {$codeColor[0]}; padding-left: {0 * $indentSize + 10}px">Node item = new Node(value)</div>
                        <div class="code" style="background-color: {$codeColor[1]}; padding-left: {0 * $indentSize + 10}px">item.next = head</div>
                        <div class="code" style="background-color: {$codeColor[2]}; padding-left: {0 * $indentSize + 10}px">head = item</div>

                    {:else if operation === 'pop'}
                        <!-- Pop 연산 의사코드 -->
                        <div class="code" style="background-color: {$codeColor[0]}; padding-left: {0 * $indentSize + 10}px">if head == null then return null</div>
                        <div class="code" style="background-color: {$codeColor[1]}; padding-left: {0 * $indentSize + 10}px">value = head.value</div>
                        <div class="code" style="background-color: {$codeColor[2]}; padding-left: {0 * $indentSize + 10}px">head = head.next</div>
                        <div class="code" style="background-color: {$codeColor[3]}; padding-left: {0 * $indentSize + 10}px">return value</div>

                    {:else if operation === 'peek'}
                        <!-- Peek 연산 의사코드 -->
                        <div class="code" style="background-color: {$codeColor[0]}; padding-left: {0 * $indentSize + 10}px">if head == null then return null</div>
                        <div class="code" style="background-color: {$codeColor[1]}; padding-left: {0 * $indentSize + 10}px">return head.value</div>

                    {:else}
                        <!-- Default case -->
                        <div class="code" style="background-color: rgba(255, 255, 255, 0); padding-left: {0 * $indentSize + 10}px">연산이 선택되지 않았습니다.</div>
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

        transition: background-color 0.3s ease, border-color 0.3s ease, color 0.3s ease;
    }

    .arrow line {
        transition: stroke 0.3s ease;
    }

    .arrow polygon {
        transition: fill 0.3s ease;
    }

    .arrow {
        position: absolute;
        transform: translateX(0px);

    }   
    :global(.node) {
        transition: background-color calc(0.5s / var(--speed, 1)) ease, 
                    border-color calc(0.5s / var(--speed, 1)) ease, 
                    color calc(0.5s / var(--speed, 1)) ease;
    }

    :global(.arrow line) {
        transition: stroke calc(0.5s / var(--speed, 1)) ease;
    }

    :global(.arrow polygon) {
        transition: fill calc(0.5s / var(--speed, 1)) ease;
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

    :global(.node-animation) {
        animation: grow calc(0.5s / var(--speed, 1)) ease-out;
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

    :global(.arrow-line-animation) {
        animation: arrow-line-grow calc(0.5s / var(--speed, 1)) ease-out;
        transform-origin: top;
    }

    :global(.arrow-head-animation) {
        animation: arrow-head-move calc(0.5s / var(--speed, 1)) ease-out;
    }

    .max-indicator {
        display: none;
    }

    @media screen and (min-width: 2560px) {
            .max-indicator {
            position: absolute;
            top: calc(100% - 7 * (50px + 50px) - 40px); /* 스택 노드 7개 기준으로 위치 조정 */
            left: 51.7%;
            transform: translateX(-50%);
            display: flex;
            align-items: center;
            gap: 20px;
        }

        .max-line {
            width: 150px; /* 선의 길이 조정 */
            height: 3px;
            background-color: #509650;
        }

        .max-label {
            font-size: 1.1rem;
            font-weight: bold;
            color: #509650;
        }
    }


    @keyframes shrink { /*축소 애니메이션*/
        0% {
            transform: scale(1);
            opacity: 1;
        }
        100% {
            transform: scale(0);
            opacity: 0;
        }
    }

    :global(.node-pop-animation) {
        animation: shrink calc(0.5s / var(--speed, 1)) ease-out;
    }

    @keyframes arrow-line-shrink  {
        0% {
            transform: scaleY(1);
            opacity: 0;
        }
        100% {
            transform: scaleY(0);
            opacity: 1;
        }
    }

    @keyframes arrow-head-move-reverse {
        0% {
            transform: translateY(0);
            opacity: 0;
        }
        100% {
            transform: translateY(-100%);
            opacity: 1;
        }
    }

    :global(.arrow-line-animation-pop) {
        animation: arrow-line-shrink calc(0.5s / var(--speed, 1)) ease-out;
        transform-origin: top;
    }

    :global(.arrow-head-animation-pop) {
        animation: arrow-head-move-reverse calc(0.5s / var(--speed, 1)) ease-out;
    }

    @keyframes flagAppear {
        0% {
            opacity: 0;
            transform: translateY(-10px) scale(0.8) rotate(15deg);
        }
        100% {
            opacity: 1;
            transform: translateY(0) scale(1) rotate(15deg);
        }
    }

    @keyframes flagDisappear {
    0% {
        opacity: 1;
        transform: translateY(0) scale(1) rotate(15deg);
    }
    100% {
        opacity: 0;
        transform: translateY(-10px) scale(0.8) rotate(15deg);
    }
}

    @keyframes flagWave {
        0% { transform: skewX(0deg) translateX(0px) rotate(0deg); }
        20% { transform: skewX(-5deg) translateX(2px) rotate(-3deg); }
        40% { transform: skewX(5deg) translateX(-4px) rotate(5deg); }
        60% { transform: skewX(-3deg) translateX(3px) rotate(-2deg); }
        80% { transform: skewX(4deg) translateX(-2px) rotate(3deg); }
        100% { transform: skewX(0deg) translateX(0px) rotate(0deg); }
    }

    @keyframes flagFlap {
        0% { transform: scaleX(1) rotate(0deg); }
        50% { transform: scaleX(0.9) rotate(2deg); }
        100% { transform: scaleX(1) rotate(0deg); }
    }

    .flag-container {
        position: absolute;
        top: -42px;
        left: 100%;
        transform: translateX(-50%);
        transform: rotate(15deg);
        display: flex;
        align-items: center;
        justify-content: center;
        opacity: 0;
        transition: opacity 0.5s ease, transform 0.5s ease;
    }

    .flag-container.appear {
        animation: flagAppear 0.5s ease forwards;
    }

    .flag-container.disappear {
        animation: flagDisappear 0.5s ease forwards;
    }

    .flag-pole {
        width: 5px;
        height: 45px;
        background-color: rgb(68, 34, 34);
        border-radius: 3px;
    }

    .flag-cloth {
        margin-bottom: 10px;
        width: 30px;
        height: 20px;
        background: linear-gradient(to right, red, darkred);
        border-radius: 3px 10px 10px 3px;
        transform-origin: left center;
        animation: 
            flagWave 2s infinite cubic-bezier(0.36, 0.55, 0.63, 0.99),
            flagFlap 1s infinite ease-in-out;
        box-shadow: 2px 2px 5px rgba(0, 0, 0, 0.2);
    }
        

</style>