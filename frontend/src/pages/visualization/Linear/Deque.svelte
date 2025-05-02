<script>
    import { onDestroy, onMount, tick   } from "svelte";
    import Header from "../../../component/Header.svelte";
    import DequeNavigation from "../../../component/navigation/Linear/DequeNavigation.svelte";
    import {isListVisible} from "../../../lib/store.js";
    import { isPaused, pausedIcon, fromBtn, isReplay, explanation, animationSpeed, animationWorking, animationQuery, codeColor, animationStep, asyncCnt, gradient, indentSize, maxSpeed } from "../../../lib/visualizationStore";
    import {incrementAnimationCount} from "../../../lib/mypage/animationCount.js";


    let canvasWidth = window.innerWidth * 0.73;
    let canvasHeight = window.innerHeight * 0.78;

    let operation = '';
    let peekDirection = "Front";  // 현재 활성 peek 방향 (기본은 Front)

    let oldPeekIndex = null;      // 이전에 활성화된 깃발의 노드 인덱스
    let activePeek = false;      // 현재 피크 깃발이 활성화되었는지 여부

    let numArr = [15, 10, 20, 30, 7] // 실제 값 배열
    let arrowArr = Array(numArr.length - 1).fill(1); // 화살표 배열(1: true , 0: false)
    let underArrowArr = Array(numArr.length - 1).fill(1); // 화살표 배열(1: true , 0: false)

    let nodePositions = [];
    let arrowPositions = []; // 윗쪽
    let arrowUnderPositions = []; //아래쪽

    let nodeAnimations = [];
    let arrowAnimations = []; // 윗쪽 화살표 애니메이션
    let arrowUnderAnimations = []; // 아래쪽 화살표 애니메이션

    let nodeDequeueAnimations = []; 
    let arrowDequeueAnimations = [];  // 윗쪽 화살표 사라짐 애니메이션
    let arrowUnderDequeueAnimations = []; // 아래쪽 화살표 사라짐 애니메이션

    const nodeWidth = 50;
    const nodeHeight = 50;
    const arrowLength = 50;

    const calculateNumArrPositions = async () => { // 중앙 정렬되는 위치 리셋 함수
 
        syncArrowArr();
        
        const startX = (canvasWidth - (numArr.length * (nodeWidth + arrowLength) - arrowLength)) / 2;
        const startY = canvasHeight / 2 - nodeHeight / 2;

        nodePositions = numArr.map((_, index) => {
            return {
                x: startX + index * (nodeWidth + arrowLength),
                y: startY,
            };
        });

        arrowPositions = arrowArr.map((_, index) => {
            return {
                x: nodePositions[index].x + nodeWidth,  // 화살표 위치 조정
                y: startY + nodeHeight / 2,  // 화살표 중앙 정렬
            };
        });

        arrowUnderPositions = underArrowArr.map((_, index) => {
            return {
                x: nodePositions[index].x + nodeWidth,
                y: startY + nodeHeight / 2 + 20,  // 여기서 20px은 필요에 따라 조정하세요.
            };
        });

        await tick();
        nodeAnimations = Array(numArr.length).fill(true);
        arrowAnimations = Array(arrowArr.length).fill(true);
        arrowUnderAnimations = Array(underArrowArr.length).fill(true);

        setTimeout(() => {
            nodeAnimations = Array(numArr.length).fill(false);
            arrowAnimations = Array(arrowArr.length).fill(false);
            arrowUnderAnimations = Array(underArrowArr.length).fill(false);
        }, 500); 
        
    };

    const calculateNumArrPositionsNA = async () => { // 애니메이션 없이 갱신하는거
        syncArrowArr();

        const startX = (canvasWidth - (numArr.length * (nodeWidth + arrowLength)  - arrowLength )) / 2;
        const startY = canvasHeight / 2 - nodeHeight / 2;

        nodePositions = numArr.map((_, index) => {
            return {
                x: startX + index * (nodeWidth + arrowLength),
                y: startY,
            };
        });

        arrowPositions = arrowArr.map((_, index) => {
            return {
                x: nodePositions[index].x + nodeWidth,  // 화살표 위치 조정
                y: startY + nodeHeight / 2,  // 화살표 중앙 정렬
            };
        });

        arrowUnderPositions = underArrowArr.map((_, index) => {
            return {
                x: nodePositions[index].x + nodeWidth,
                y: startY + nodeHeight / 2 + 20,  // 여기서 20px은 필요에 따라 조정하세요.
            };
        });
        
    };

    const resetPosition = async () => { // 마지막 정렬
        const startX = (canvasWidth - (numArr.length * (nodeWidth + arrowLength) - arrowLength)) / 2;
        const startY = canvasHeight / 2 - nodeHeight / 2;

        const nodes = document.querySelectorAll('.node');
        const upperArrows  = document.querySelectorAll('.arrow');
        const lowerArrows = document.querySelectorAll('.arrow.arrow-under');

        nodes.forEach((node, index) => {
            node.style.position = 'absolute';
            node.style.left = `${nodePositions[index].x}px`;
            node.style.top = `${nodePositions[index].y}px`;
        });

        upperArrows .forEach((arrow, index) => {
            if (arrowArr[index] === 1) {
                arrow.style.position = 'absolute';
                arrow.style.left = `${arrowPositions[index].x}px`;
                arrow.style.top = `${arrowPositions[index].y}px`;
            }
        });

        lowerArrows.forEach((arrow, index) => {
            if (underArrowArr[index] === 1) {
                arrow.style.position = 'absolute';
                arrow.style.left = `${arrowUnderPositions[index].x}px`;
                arrow.style.top = `${arrowUnderPositions[index].y}px`;
            }
        });

        // 강제 리플로우로 위치 초기화 보장 (애니메이션 시작 전에 초기 위치를 브라우저에 적용)
        await tick();

        // 노드 애니메이션: 현재 위치에서 목표 위치로 이동
        nodes.forEach((node, index) => {
            const targetX = startX + index * (nodeWidth + arrowLength);
            const targetY = startY;

            node.style.transition = `top 0.5s ease, left 0.5s ease`;
            node.style.left = `${targetX}px`;
            node.style.top = `${targetY}px`;
        });

        // 화살표 애니메이션: 현재 위치에서 목표 위치로 이동
        upperArrows .forEach((arrow, index) => {
            if (arrowArr[index] === 1) {
                const targetX = startX + index * (nodeWidth + arrowLength) + nodeWidth;
                const targetY = startY + nodeHeight / 2 - 5;

                arrow.style.transition = `top 0.5s ease, left 0.5s ease`;
                arrow.style.left = `${targetX + 8}px`;
                arrow.style.top = `${targetY}px`;
            }
        });

        // 아래쪽 화살표 애니메이션: 목표 위치로 이동
        lowerArrows.forEach((arrow, index) => {
            if (underArrowArr[index] === 1) {
                const targetX = startX + index * (nodeWidth + arrowLength) + nodeWidth;
                const targetY = startY + nodeHeight / 2 + 5; // 20px 오프셋 (원하는 값으로 조정 가능)

                arrow.style.transition = `top 0.5s ease, left 0.5s ease`;
                arrow.style.left = `${targetX + 8}px`;
                arrow.style.top = `${targetY}px`;
            }
        });

        // 애니메이션 완료 후 위치 업데이트
        await delay(500); // 0.5초 대기 (애니메이션 시간)

        // 새 위치 저장
        nodePositions = numArr.map((_, index) => ({
            x: startX + index * (nodeWidth + arrowLength),
            y: startY,
        }));

        arrowPositions = arrowArr.map((_, index) => ({
            x: startX + index * (nodeWidth + arrowLength) + nodeWidth,
            y: startY + nodeHeight / 2,
        }));

        arrowUnderPositions = underArrowArr.map((_, index) => ({
            x: startX + index * (nodeWidth + arrowLength) + nodeWidth,
            y: startY + nodeHeight / 2 + 20,
        }));

        // 애니메이션 속성 초기화
        nodes.forEach((node) => {
            node.style.transition = '';
        });

        upperArrows .forEach((arrow) => {
            arrow.style.transition = '';
        });

        lowerArrows.forEach((arrow) => {
            arrow.style.transition = '';
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

    const syncArrowArr = () => {
        arrowArr = Array(numArr.length - 1).fill(1);
        underArrowArr = Array(numArr.length - 1).fill(1);
    };

    const resetNodeStyles = () => {
        document.querySelectorAll('.node').forEach(node => {
            node.classList.remove('no-transition');
            node.style.transition = "";
        });
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

        nodeAnimations = Array(numArr.length).fill(false);
        arrowAnimations = Array(arrowArr.length).fill(false);
        
        nodeDequeueAnimations = Array(numArr.length).fill(false);
        arrowDequeueAnimations = Array(arrowArr.length).fill(false);

        arrowUnderAnimations = Array(underArrowArr.length).fill(false);
        arrowUnderDequeueAnimations = Array(underArrowArr.length).fill(false);

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

    const startPeek = (direction = "Front") => {

        if (activePeek && peekDirection !== direction) {
            // 이전 피크 깃발이 적용되어 있던 인덱스를 저장
            oldPeekIndex = (peekDirection === "Back") ? numArr.length - 1 : 0;
            // 이전 깃발은 현재 피크 상태에서만 활성화된 경우에만 disappear 효과가 적용됨
            // 0.5초 후에 oldPeekIndex를 초기화하여 DOM에서 제거
            setTimeout(() => { oldPeekIndex = null; }, 500);
            activePeek = false;
        }

        operation = 'peek';
        peekDirection = direction;  // 선택한 방향 저장

        InitAnimation();

        generateQueuePeekQueries(direction);
        
        $animationWorking = true;
        $pausedIcon = false;
        $isPaused = false;

        activePeek = true;

        incrementAnimationCount("deque");
        executeQueuePeekQueries($asyncCnt++);
        
    };

    const pushQueuePeekAnimationQuery = (tmpExplanation, tmpNodeBgColor, tmpNodeBorderColor, tmpNodeTextColor,tmpCode) => {
        
        $animationQuery.push({
            curExplanation: tmpExplanation,
            curNodeBgColor: [...tmpNodeBgColor],
            curNodeBorderColor: [...tmpNodeBorderColor],
            curNodeTextColor: [...tmpNodeTextColor],
            curCode :tmpCode
        });

    };

    const generateQueuePeekQueries = (direction) => {

        const nodeBg = {normal: "#ffffff", selected: "#ed8925", completed: "#52bc69", hidden: "#ffffff"};
        const nodeBorderColor = {normal: "#000000", selected: "#d97511", completed: "#13a300", hidden: "#ffffff"};
        const textColor = {normal: "#000000", selected: "#ffffff", hidden: "#ffffff"}

        $animationQuery = [];

        if (numArr.length === 0) {
            
            pushQueuePeekAnimationQuery(`스택이 비어있는지 확인합니다`, [], [], [], 0);

            return;  // 비어있는 경우
        }


        let tmpArr = [...numArr];
        let tmpExplanation = ``;
        let tmpNodeBgColor = Array(tmpArr.length).fill(nodeBg.normal);
        let tmpNodeBorderColor = Array(tmpArr.length).fill(nodeBorderColor.normal);
        let tmpNodeTextColor = Array(tmpArr.length).fill(textColor.normal);
        let tmpCode = 1000;

        tmpExplanation = `배열의 초기 상태입니다`;
        pushQueuePeekAnimationQuery(tmpExplanation, tmpNodeBgColor, tmpNodeBorderColor, tmpNodeTextColor,tmpCode);

        if (direction === "Back") {

            tmpExplanation = `tail 노드의 값 (${tmpArr[tmpArr.length - 1]})을 반환합니다.`;

            tmpCode = 1;

            tmpNodeBgColor[tmpArr.length - 1] = nodeBg.completed;
            tmpNodeBorderColor[tmpArr.length - 1] = nodeBorderColor.completed;
            tmpNodeTextColor[tmpArr.length - 1] = textColor.selected;

        } else {

            tmpExplanation = `head 노드의 값(${tmpArr[0]})을 반환합니다`

            tmpCode = 1;

            tmpNodeBgColor[0] = nodeBg.completed; 
            tmpNodeBorderColor[0] = nodeBorderColor.completed;
            tmpNodeTextColor[0] = textColor.selected;
        }

        pushQueuePeekAnimationQuery(tmpExplanation ,tmpNodeBgColor, tmpNodeBorderColor, tmpNodeTextColor,tmpCode);

    }

    const executeQueuePeekQueries = async (myAsync) => {
        $animationStep = [0, $animationQuery.length - 1];

        while (true) {
            if ((myAsync + 1) !== $asyncCnt) break;

            if ($animationStep[0] === $animationStep[1]) {
                $pausedIcon = true;
                $isPaused = true;
            }

            await drawQueuePeekAnimation($animationStep[0]);
            await waitPause();
            if($animationSpeed <= 30) await delay(20);

            if (!$fromBtn) {
                $animationStep[0] = Math.min($animationStep[0] + 1, $animationStep[1]);
            }
        }
    }; 

    const drawQueuePeekAnimation = async (i) => {

        $explanation = $animationQuery[i].curExplanation; // $explanation 수정
        changeCodeColor($animationQuery[i].curCode); // $codeColor 

        await tick(); // UI 갱신을 위해 tick() 사용

        const nodeBgColors = $animationQuery[i].curNodeBgColor;
        const nodeBorderColors = $animationQuery[i].curNodeBorderColor;
        const nodeTextColors = $animationQuery[i].curNodeTextColor;

        document.querySelectorAll('.node').forEach((node, index) => {
            if (nodeBgColors[index]) node.style.backgroundColor = nodeBgColors[index];
            if (nodeBorderColors[index]) node.style.borderColor = nodeBorderColors[index];
            if (nodeTextColors[index]) node.style.color = nodeTextColors[index];
        });

        if (i === $animationQuery.length - 1) {
            activePeek = true;
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


    //=================================[ 삭제 ]============================================

    const clearActivePeekFlag = () => {
        if (activePeek) {
            oldPeekIndex = (peekDirection === "Back") ? numArr.length - 1 : 0;
            setTimeout(() => { oldPeekIndex = null; }, 500);
            activePeek = false;
        }
    };

    const startDequeue = (direction) => {
        clearActivePeekFlag();

        operation = 'Dequeue';
        InitAnimation();
    
        generateDequeueQueries(direction);

        $animationWorking = true;
        $pausedIcon = false;
        $isPaused = false;

        incrementAnimationCount("deque");
        executeDequeueQueries($asyncCnt++);
        
    };

    const pushDequeueAnimationQuery = (tmpArr, tmpArrowArr, tmpNodePositions, tmpArrowPositions, tmpNodeDequeueAnimations, tmpArrowDequeueAnimations, tmpExplanation, tmpNodeBgColor, tmpNodeBorderColor, tmpNodeTextColor, tmpArrowColor, tmpUnderArrowArr, tmpArrowUnderPositions, tmpArrowUnderDequeueAnimations, tmpCode) => {
        
        $animationQuery.push({
            curArr: [...tmpArr],
            curArrowArr: [...tmpArrowArr],
            curNodePositions: [...tmpNodePositions],
            curArrowPositions: [...tmpArrowPositions],
            curNodeDequeueAnimation: [...tmpNodeDequeueAnimations],
            curArrowDequeueAnimations: [...tmpArrowDequeueAnimations],
            curExplanation: tmpExplanation,
            curNodeBgColor: [...tmpNodeBgColor],
            curNodeBorderColor: [...tmpNodeBorderColor],
            curNodeTextColor: [...tmpNodeTextColor],
            curArrowColor: [...tmpArrowColor],
            curUnderArrowArr: [...tmpUnderArrowArr],
            curArrowUnderPositions: [...tmpArrowUnderPositions],
            curArrowUnderDequeueAnimations: [...tmpArrowUnderDequeueAnimations],
            curCode :tmpCode
        });

    };

    $: if (arrowDequeueAnimations[0]) {
        setTimeout(() => {
            arrowArr[0] = 0;
        }, 450 / $animationSpeed); 
    };

    $: if (arrowUnderDequeueAnimations[0]) {
        setTimeout(() => {
            underArrowArr[0] = 0;
        }, 450 / $animationSpeed); 
    };

    $: if (nodeDequeueAnimations[0]) {
        setTimeout(() => {
            const nodes = document.querySelectorAll('.node');
            if (nodes.length > 0) {

                nodes[0].style.backgroundColor = "#ffffff";
                nodes[0].style.borderColor = "#ffffff";
                nodes[0].style.color = "#ffffff";
                nodes[0].style.transition = "none";

            }
        }, (250 / $animationSpeed) + 10); 
    };

    // ====================================================================

    $: if (arrowDequeueAnimations[arrowArr.length - 1]) {
        setTimeout(() => {
            arrowArr[arrowArr.length - 1] = 0;
        }, 450 / $animationSpeed); 
    };

    $: if (arrowUnderDequeueAnimations[underArrowArr.length - 1]) {
        setTimeout(() => {
            underArrowArr[underArrowArr.length - 1] = 0;
        }, 450 / $animationSpeed); 
    };

    $: if (nodeDequeueAnimations[numArr.length - 1]) {
        setTimeout(() => {
            const nodes = document.querySelectorAll('.node');
            if (nodes.length > 0) {

                nodes[numArr.length - 1].style.backgroundColor = "#ffffff";
                nodes[numArr.length - 1].style.borderColor = "#ffffff";
                nodes[numArr.length - 1].style.color = "#ffffff";
                nodes[numArr.length - 1].style.transition = "none";

            }
        }, (250 / $animationSpeed) + 10); 
    };

    const generateDequeueQueries = (direction) => {
        
        const nodeBg = {normal: "#ffffff", selected: "#ed8925", completed: "#52bc69", hidden: "#ffffff"};
        const nodeBorderColor = {normal: "#000000", selected: "#d97511", completed: "#13a300", hidden: "#ffffff"};
        const textColor = {normal: "#000000", selected: "#ffffff", hidden: "#ffffff"}
        const arrowColor = {normal: "#000000", connecting: "#d97511"};

        const resetNodePositions = () => {
            syncArrowArr();
            const startX = nodePositions[1]
                ? nodePositions[1].x // 1번째 노드의 x값을 기준으로 설정
                : (canvasWidth - (numArr.length * (nodeWidth + arrowLength) - arrowLength)) / 2; // 기본 중앙 정렬
            const startY = canvasHeight / 2 - nodeHeight / 2;

            nodePositions = numArr.map((_, index) => {
                return {
                    x: startX + index * (nodeWidth + arrowLength), // 1번째 기준으로 나머지 재배열
                    y: startY,
                };
            });

            arrowPositions = arrowArr.map((_, index) => {
                return {
                    x: nodePositions[index]?.x + nodeWidth || 0, // 노드 위치에 따라 x 좌표 동기화, 기본값 0
                    y: nodePositions[index]?.y + nodeHeight / 2 || 0, // 노드 위치에 따라 y 좌표 동기화, 기본값 0
                };
            });

            arrowUnderPositions = underArrowArr.map((_, index) => {
                return {
                    x: nodePositions[index]?.x + nodeWidth || 0,
                    y: nodePositions[index]?.y + nodeHeight / 2 + 20 || 0, // 오프셋 값은 필요에 따라 조정하세요.
                };
            });

        };

        const resetNodePositionsFromFirst = () => {
            // 화살표 배열 동기화
            syncArrowArr();
            
            // 첫 번째 노드의 기존 위치가 있으면 그 위치를 기준으로 사용,
            // 없으면 캔버스 중앙 기준 위치로 계산합니다.
            const startX = nodePositions.length > 0 
                ? nodePositions[0].x 
                : (canvasWidth - (numArr.length * (nodeWidth + arrowLength) - arrowLength)) / 2;
            const startY = nodePositions.length > 0 
                ? nodePositions[0].y 
                : canvasHeight / 2 - nodeHeight / 2;
            
            // 각 노드의 위치를 첫 번째 노드를 기준으로 재정렬합니다.
            nodePositions = numArr.map((_, index) => ({
                x: startX + index * (nodeWidth + arrowLength),
                y: startY,
            }));

            // 노드에 연결된 화살표 위치도 재계산합니다.
            arrowPositions = arrowArr.map((_, index) => ({
                x: nodePositions[index].x + nodeWidth,
                y: nodePositions[index].y + nodeHeight / 2,
            }));

            arrowUnderPositions = underArrowArr.map((_, index) => ({
                x: nodePositions[index].x + nodeWidth,
                y: nodePositions[index].y + nodeHeight / 2 + 20,
            }));
        };




        $animationQuery = [];

        if (numArr.length === 0) { // 비어있는 경우
            
            pushDequeueAnimationQuery([], [], [], [], [], [], `스택이 비어있어 삭제할 항목이 없습니다`, [], [], [], [], 0);

            return; 
        }


        let tmpArr = [...numArr];
        let tmpArrowArr = [...arrowArr];
        let tmpNodePositions = [...nodePositions];
        let tmpArrowPositions = [...arrowPositions];
        let tmpNodeDequeueAnimations = [...nodeDequeueAnimations];
        let tmpArrowDequeueAnimations = [...arrowDequeueAnimations];
        let tmpExplanation = ``;
        let tmpNodeBgColor = Array(tmpArr.length).fill(nodeBg.normal);
        let tmpNodeBorderColor = Array(tmpArr.length).fill(nodeBorderColor.normal);
        let tmpNodeTextColor = Array(tmpArr.length).fill(textColor.normal);
        let tmpArrowColor = Array(tmpArrowArr.length).fill(arrowColor.normal);

        let tmpUnderArrowArr = [...underArrowArr];
        let tmpArrowUnderPositions = [...arrowUnderPositions];
        let tmpArrowUnderDequeueAnimations = [...arrowUnderDequeueAnimations];

        let tmpCode = 1000;

        if (numArr.length === 1) { // 배열의 길이가 1인 경우
            
            tmpExplanation = `배열의 초기 상태입니다`;
            pushDequeueAnimationQuery(tmpArr, tmpArrowArr, tmpNodePositions, tmpArrowPositions, tmpNodeDequeueAnimations, tmpArrowDequeueAnimations, tmpExplanation, tmpNodeBgColor, tmpNodeBorderColor, tmpNodeTextColor, tmpArrowColor, tmpUnderArrowArr, tmpArrowUnderPositions, tmpArrowUnderDequeueAnimations, tmpCode);
            tmpExplanation = `배열의 초기 상태입니다`;// 애니메이션 재실행 보험용 쿼리
            pushDequeueAnimationQuery(tmpArr, tmpArrowArr, tmpNodePositions, tmpArrowPositions, tmpNodeDequeueAnimations, tmpArrowDequeueAnimations, tmpExplanation, tmpNodeBgColor, tmpNodeBorderColor, tmpNodeTextColor, tmpArrowColor, tmpUnderArrowArr, tmpArrowUnderPositions, tmpArrowUnderDequeueAnimations, tmpCode);

            // Step 1
            tmpCode = 1;
            tmpExplanation = `head 노드의 값(${tmpArr[0]})을 임시 변수에 저장합니다`;

            tmpNodeBgColor[0] = nodeBg.selected; 
            tmpNodeBorderColor[0] = nodeBorderColor.selected;
            tmpNodeTextColor[0] = textColor.selected;

            tmpArr = [...numArr];
            tmpArrowArr = [...arrowArr];
            tmpNodePositions = [...nodePositions];
            tmpArrowPositions = [...arrowPositions];
            tmpNodeDequeueAnimations = [...nodeDequeueAnimations];

            pushDequeueAnimationQuery(tmpArr, tmpArrowArr, tmpNodePositions, tmpArrowPositions, tmpNodeDequeueAnimations, tmpArrowDequeueAnimations, tmpExplanation, tmpNodeBgColor, tmpNodeBorderColor, tmpNodeTextColor, tmpArrowColor, tmpUnderArrowArr, tmpArrowUnderPositions, tmpArrowUnderDequeueAnimations, tmpCode);

            // Step 2
            tmpCode = 3;
            tmpExplanation = `저장한 값(${tmpArr[0]})을 반환합니다`;

            nodeDequeueAnimations[0] = true; // true 이후 반응형으로 첫 노드 하얀색으로 변경(히든)

            tmpArr = [...numArr];
            tmpArrowArr = [...arrowArr];
            tmpNodePositions = [...nodePositions];
            tmpArrowPositions = [...arrowPositions];
            tmpNodeDequeueAnimations = [...nodeDequeueAnimations];
            tmpArrowDequeueAnimations = [...arrowDequeueAnimations];

            pushDequeueAnimationQuery(tmpArr, tmpArrowArr, tmpNodePositions, tmpArrowPositions, tmpNodeDequeueAnimations, tmpArrowDequeueAnimations, tmpExplanation, tmpNodeBgColor, tmpNodeBorderColor, tmpNodeTextColor, tmpArrowColor, tmpUnderArrowArr, tmpArrowUnderPositions, tmpArrowUnderDequeueAnimations, tmpCode);

            // Step 3

            tmpCode = 3;
            tmpExplanation = `저장한 값(${tmpArr[0]})을 반환합니다`;

            numArr.shift();
            tmpArr = [...numArr];
            pushDequeueAnimationQuery(tmpArr, tmpArrowArr, tmpNodePositions, tmpArrowPositions, tmpNodeDequeueAnimations, tmpArrowDequeueAnimations, tmpExplanation, tmpNodeBgColor, tmpNodeBorderColor, tmpNodeTextColor, tmpArrowColor, tmpUnderArrowArr, tmpArrowUnderPositions, tmpArrowUnderDequeueAnimations, tmpCode);

            return;
        };

        if(direction === 'Front') {
            tmpExplanation = `배열의 초기 상태입니다`;
            pushDequeueAnimationQuery(tmpArr, tmpArrowArr, tmpNodePositions, tmpArrowPositions, tmpNodeDequeueAnimations, tmpArrowDequeueAnimations, tmpExplanation, tmpNodeBgColor, tmpNodeBorderColor, tmpNodeTextColor, tmpArrowColor, tmpUnderArrowArr, tmpArrowUnderPositions, tmpArrowUnderDequeueAnimations, tmpCode);

            tmpExplanation = `배열의 초기 상태입니다`; // 애니메이션 재실행 보험용 쿼리
            pushDequeueAnimationQuery(tmpArr, tmpArrowArr, tmpNodePositions, tmpArrowPositions, tmpNodeDequeueAnimations, tmpArrowDequeueAnimations, tmpExplanation, tmpNodeBgColor, tmpNodeBorderColor, tmpNodeTextColor, tmpArrowColor, tmpUnderArrowArr, tmpArrowUnderPositions, tmpArrowUnderDequeueAnimations, tmpCode);


            // Step 1
            tmpCode = 1;

            tmpNodeBgColor[0] = nodeBg.selected; 
            tmpNodeBorderColor[0] = nodeBorderColor.selected;
            tmpNodeTextColor[0] = textColor.selected;

            tmpArr = [...numArr];
            tmpArrowArr = [...arrowArr];
            tmpNodePositions = [...nodePositions];
            tmpArrowPositions = [...arrowPositions];
            tmpNodeDequeueAnimations = [...nodeDequeueAnimations];
            tmpArrowDequeueAnimations = [...arrowDequeueAnimations];

            tmpUnderArrowArr = [...underArrowArr];
            tmpArrowUnderPositions = [...arrowUnderPositions];
            tmpArrowUnderDequeueAnimations = [...arrowUnderDequeueAnimations];

            tmpExplanation = `head 노드의 값(${tmpArr[0]})을 임시 변수에 저장합니다`;


            pushDequeueAnimationQuery(tmpArr, tmpArrowArr, tmpNodePositions, tmpArrowPositions, tmpNodeDequeueAnimations, tmpArrowDequeueAnimations, tmpExplanation, tmpNodeBgColor, tmpNodeBorderColor, tmpNodeTextColor, tmpArrowColor, tmpUnderArrowArr, tmpArrowUnderPositions, tmpArrowUnderDequeueAnimations, tmpCode);

            // Step 2
            tmpCode = 2;
            arrowDequeueAnimations[0] = true; // 화살표 삭제 애니메이션
            arrowUnderDequeueAnimations[0] = true; // 아래 화살표 삭제 애니메이션

            tmpNodeBgColor[1] = nodeBg.completed;
            tmpNodeBorderColor[1] = nodeBorderColor.completed;
            tmpNodeTextColor[1] = textColor.selected;

            tmpArr = [...numArr];
            tmpArrowArr = [...arrowArr];
            tmpNodePositions = [...nodePositions];
            tmpArrowPositions = [...arrowPositions];
            tmpNodeDequeueAnimations = [...nodeDequeueAnimations];
            tmpArrowDequeueAnimations = [...arrowDequeueAnimations];

            tmpUnderArrowArr = [...underArrowArr];
            tmpArrowUnderPositions = [...arrowUnderPositions];
            tmpArrowUnderDequeueAnimations = [...arrowUnderDequeueAnimations];

            tmpExplanation = `head 포인터를 다음 노드(${tmpArr[1]})로 변경합니다`;
            pushDequeueAnimationQuery(tmpArr, tmpArrowArr, tmpNodePositions, tmpArrowPositions, tmpNodeDequeueAnimations, tmpArrowDequeueAnimations, tmpExplanation, tmpNodeBgColor, tmpNodeBorderColor, tmpNodeTextColor, tmpArrowColor, tmpUnderArrowArr, tmpArrowUnderPositions, tmpArrowUnderDequeueAnimations, tmpCode);

            // Step 3
            tmpCode = 3;

            arrowArr[0] = 0; // 화살표 없는상태 유지
            underArrowArr[0] = 0;

            arrowDequeueAnimations[0] = false; 
            arrowUnderDequeueAnimations[0] = false;

            nodeDequeueAnimations[0] = true; // true 이후 반응형으로 첫 노드 하얀색으로 변경(히든)


            tmpExplanation = `저장한 값(${tmpArr[0]})을 반환합니다`;
            
            tmpArr = [...numArr];
            tmpArrowArr = [...arrowArr];
            tmpNodePositions = [...nodePositions];
            tmpArrowPositions = [...arrowPositions];
            tmpNodeDequeueAnimations = [...nodeDequeueAnimations];
            tmpArrowDequeueAnimations = [...arrowDequeueAnimations];

            tmpUnderArrowArr = [...underArrowArr];
            tmpArrowUnderPositions = [...arrowUnderPositions];
            tmpArrowUnderDequeueAnimations = [...arrowUnderDequeueAnimations];
            
            pushDequeueAnimationQuery(tmpArr, tmpArrowArr, tmpNodePositions, tmpArrowPositions, tmpNodeDequeueAnimations, tmpArrowDequeueAnimations, tmpExplanation, tmpNodeBgColor, tmpNodeBorderColor, tmpNodeTextColor, tmpArrowColor, tmpUnderArrowArr, tmpArrowUnderPositions, tmpArrowUnderDequeueAnimations, tmpCode);

            // step 4
            tmpCode = 3;
            tmpExplanation = `삭제연산 완료`;
            nodeDequeueAnimations[0] = false;
            
            numArr.shift();
            arrowArr[0] = 1;    

            resetNodePositions();

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
            tmpNodeDequeueAnimations = [...nodeDequeueAnimations];

            tmpUnderArrowArr = [...underArrowArr];
            tmpArrowUnderPositions = [...arrowUnderPositions];
            tmpArrowUnderDequeueAnimations = [...arrowUnderDequeueAnimations];

            pushDequeueAnimationQuery(tmpArr, tmpArrowArr, tmpNodePositions, tmpArrowPositions, tmpNodeDequeueAnimations, tmpArrowDequeueAnimations, tmpExplanation, tmpNodeBgColor, tmpNodeBorderColor, tmpNodeTextColor, tmpArrowColor, tmpUnderArrowArr, tmpArrowUnderPositions, tmpArrowUnderDequeueAnimations, tmpCode);
            
            calculateNumArrPositionsNA();
            tmpNodePositions = [...nodePositions];
            tmpArrowPositions = [...arrowPositions];

            tmpUnderArrowArr = [...underArrowArr];
            tmpArrowUnderPositions = [...arrowUnderPositions];
            tmpArrowUnderDequeueAnimations = [...arrowUnderDequeueAnimations];

            pushDequeueAnimationQuery(tmpArr, tmpArrowArr, tmpNodePositions, tmpArrowPositions, tmpNodeDequeueAnimations, tmpArrowDequeueAnimations, tmpExplanation, tmpNodeBgColor, tmpNodeBorderColor, tmpNodeTextColor, tmpArrowColor, tmpUnderArrowArr, tmpArrowUnderPositions, tmpArrowUnderDequeueAnimations, tmpCode);
            
            return;
        };

        if(direction === 'Back') {
            tmpExplanation = `배열의 초기 상태입니다`;
            pushDequeueAnimationQuery(tmpArr, tmpArrowArr, tmpNodePositions, tmpArrowPositions, tmpNodeDequeueAnimations, tmpArrowDequeueAnimations, tmpExplanation, tmpNodeBgColor, tmpNodeBorderColor, tmpNodeTextColor, tmpArrowColor, tmpUnderArrowArr, tmpArrowUnderPositions, tmpArrowUnderDequeueAnimations, tmpCode);

            tmpExplanation = `배열의 초기 상태입니다`; // 애니메이션 재실행 보험용 쿼리
            pushDequeueAnimationQuery(tmpArr, tmpArrowArr, tmpNodePositions, tmpArrowPositions, tmpNodeDequeueAnimations, tmpArrowDequeueAnimations, tmpExplanation, tmpNodeBgColor, tmpNodeBorderColor, tmpNodeTextColor, tmpArrowColor, tmpUnderArrowArr, tmpArrowUnderPositions, tmpArrowUnderDequeueAnimations, tmpCode);


            // Step 1
            tmpCode = 1;

            tmpNodeBgColor[numArr.length - 1] = nodeBg.selected; 
            tmpNodeBorderColor[numArr.length - 1] = nodeBorderColor.selected;
            tmpNodeTextColor[numArr.length - 1] = textColor.selected;

            tmpArr = [...numArr];
            tmpArrowArr = [...arrowArr];
            tmpNodePositions = [...nodePositions];
            tmpArrowPositions = [...arrowPositions];
            tmpNodeDequeueAnimations = [...nodeDequeueAnimations];
            tmpArrowDequeueAnimations = [...arrowDequeueAnimations];

            tmpUnderArrowArr = [...underArrowArr];
            tmpArrowUnderPositions = [...arrowUnderPositions];
            tmpArrowUnderDequeueAnimations = [...arrowUnderDequeueAnimations];

            tmpExplanation = `head 노드의 값(${tmpArr[0]})을 임시 변수에 저장합니다`;


            pushDequeueAnimationQuery(tmpArr, tmpArrowArr, tmpNodePositions, tmpArrowPositions, tmpNodeDequeueAnimations, tmpArrowDequeueAnimations, tmpExplanation, tmpNodeBgColor, tmpNodeBorderColor, tmpNodeTextColor, tmpArrowColor, tmpUnderArrowArr, tmpArrowUnderPositions, tmpArrowUnderDequeueAnimations, tmpCode);

            // Step 2
            tmpCode = 2;
            arrowDequeueAnimations[arrowArr.length - 1] = true; // 화살표 삭제 애니메이션
            arrowUnderDequeueAnimations[underArrowArr.length - 1] = true; // 아래 화살표 삭제 애니메이션

            tmpNodeBgColor[numArr.length - 2] = nodeBg.completed;
            tmpNodeBorderColor[numArr.length - 2] = nodeBorderColor.completed;
            tmpNodeTextColor[numArr.length - 2] = textColor.selected;

            tmpArr = [...numArr];
            tmpArrowArr = [...arrowArr];
            tmpNodePositions = [...nodePositions];
            tmpArrowPositions = [...arrowPositions];
            tmpNodeDequeueAnimations = [...nodeDequeueAnimations];
            tmpArrowDequeueAnimations = [...arrowDequeueAnimations];

            tmpUnderArrowArr = [...underArrowArr];
            tmpArrowUnderPositions = [...arrowUnderPositions];
            tmpArrowUnderDequeueAnimations = [...arrowUnderDequeueAnimations];

            tmpExplanation = `head 포인터를 다음 노드(${tmpArr[1]})로 변경합니다`;
            pushDequeueAnimationQuery(tmpArr, tmpArrowArr, tmpNodePositions, tmpArrowPositions, tmpNodeDequeueAnimations, tmpArrowDequeueAnimations, tmpExplanation, tmpNodeBgColor, tmpNodeBorderColor, tmpNodeTextColor, tmpArrowColor, tmpUnderArrowArr, tmpArrowUnderPositions, tmpArrowUnderDequeueAnimations, tmpCode);

            // Step 3
            tmpCode = 3;

            arrowArr[arrowArr.length - 1] = 0; // 화살표 없는상태 유지
            underArrowArr[underArrowArr.length - 1] = 0;

            arrowDequeueAnimations[arrowArr.length - 1] = false; 
            arrowUnderDequeueAnimations[underArrowArr.length - 1] = false;

            nodeDequeueAnimations[numArr.length - 1] = true; // true 이후 반응형으로 첫 노드 하얀색으로 변경(히든)


            tmpExplanation = `저장한 값(${tmpArr[0]})을 반환합니다`;
            
            tmpArr = [...numArr];
            tmpArrowArr = [...arrowArr];
            tmpNodePositions = [...nodePositions];
            tmpArrowPositions = [...arrowPositions];
            tmpNodeDequeueAnimations = [...nodeDequeueAnimations];
            tmpArrowDequeueAnimations = [...arrowDequeueAnimations];

            tmpUnderArrowArr = [...underArrowArr];
            tmpArrowUnderPositions = [...arrowUnderPositions];
            tmpArrowUnderDequeueAnimations = [...arrowUnderDequeueAnimations];
            
            pushDequeueAnimationQuery(tmpArr, tmpArrowArr, tmpNodePositions, tmpArrowPositions, tmpNodeDequeueAnimations, tmpArrowDequeueAnimations, tmpExplanation, tmpNodeBgColor, tmpNodeBorderColor, tmpNodeTextColor, tmpArrowColor, tmpUnderArrowArr, tmpArrowUnderPositions, tmpArrowUnderDequeueAnimations, tmpCode);

            // step 4
            tmpCode = 3;
            tmpExplanation = `삭제연산 완료`;
            nodeDequeueAnimations[numArr.length - 1] = false;
            
            numArr.pop();
            
            arrowArr[arrowArr.length - 1] = 1; // 화살표 없는상태 유지
            underArrowArr[underArrowArr.length - 1] = 1;

            resetNodePositionsFromFirst();

            tmpArr = [...numArr];
            tmpArrowArr = [...arrowArr];
            tmpNodePositions = [...nodePositions];
            tmpArrowPositions = [...arrowPositions];
            tmpNodeDequeueAnimations = [...nodeDequeueAnimations];

            tmpUnderArrowArr = [...underArrowArr];
            tmpArrowUnderPositions = [...arrowUnderPositions];
            tmpArrowUnderDequeueAnimations = [...arrowUnderDequeueAnimations];

            pushDequeueAnimationQuery(tmpArr, tmpArrowArr, tmpNodePositions, tmpArrowPositions, tmpNodeDequeueAnimations, tmpArrowDequeueAnimations, tmpExplanation, tmpNodeBgColor, tmpNodeBorderColor, tmpNodeTextColor, tmpArrowColor, tmpUnderArrowArr, tmpArrowUnderPositions, tmpArrowUnderDequeueAnimations, tmpCode);
            
            // step 5
            calculateNumArrPositionsNA();
            tmpNodePositions = [...nodePositions];
            tmpArrowPositions = [...arrowPositions];

            tmpUnderArrowArr = [...underArrowArr];
            tmpArrowUnderPositions = [...arrowUnderPositions];
            tmpArrowUnderDequeueAnimations = [...arrowUnderDequeueAnimations];

            pushDequeueAnimationQuery(tmpArr, tmpArrowArr, tmpNodePositions, tmpArrowPositions, tmpNodeDequeueAnimations, tmpArrowDequeueAnimations, tmpExplanation, tmpNodeBgColor, tmpNodeBorderColor, tmpNodeTextColor, tmpArrowColor, tmpUnderArrowArr, tmpArrowUnderPositions, tmpArrowUnderDequeueAnimations, tmpCode);

        };
    };

    const executeDequeueQueries = async (myAsync) => {
        $animationStep = [0, $animationQuery.length - 1];

        while (true) {
            if ((myAsync + 1) !== $asyncCnt) break;

            if ($animationStep[0] === $animationStep[1]) {
                $pausedIcon = true;
                $isPaused = true;
            }

            await drawDequeueAnimation($animationStep[0]);
            await waitPause();
            if($animationSpeed <= 30) await delay(20);

            if (!$fromBtn) {
                $animationStep[0] = Math.min($animationStep[0] + 1, $animationStep[1]);
            }
        }
    };

    const drawDequeueAnimation = async (i) => {

        $explanation = $animationQuery[i].curExplanation; // $explanation 수정
        changeCodeColor($animationQuery[i].curCode); // $codeColor 

        numArr = [...$animationQuery[i].curArr];

        arrowArr = [...$animationQuery[i].curArrowArr];

        underArrowArr = [...$animationQuery[i].curUnderArrowArr];

        nodePositions = [...$animationQuery[i].curNodePositions];
        nodeDequeueAnimations = [...$animationQuery[i].curNodeDequeueAnimation];

        arrowPositions = [...$animationQuery[i].curArrowPositions];
        arrowDequeueAnimations = [...$animationQuery[i].curArrowDequeueAnimations];

        arrowUnderPositions = [...$animationQuery[i].curArrowUnderPositions];
        arrowUnderDequeueAnimations = [...$animationQuery[i].curArrowUnderDequeueAnimations];

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
                if (i === $animationQuery.length - 2) {
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
            resetPosition();
        }

        await delay(1000 * (1 / $animationSpeed)); // 애니메이션 지연
    };


    //=================================[ 삽입 ]============================================
    
    const startEnqueue = (e) => {
        clearActivePeekFlag();

        operation = 'push';
        InitAnimation();
        
        const { value, direction } = e.detail;

        const pushNum = Number(value.trim());

        generateEnqueueQueries(pushNum, direction);

        $animationWorking = true;
        $pausedIcon = false;
        $isPaused = false;

        incrementAnimationCount("deque");
        executeEnqueueQueries($asyncCnt++);
    };

    // 애니메이션(Push) 쿼리 저장
    const pushEnqueueAnimationQuery = (tmpArr, tmpArrowArr, tmpUnderArrowArr, tmpNodePositions, tmpArrowPositions, tmpUnderArrowPositions, tmpNodeAnimations, tmpArrowAnimations, tmpArrowUnderAnimations, tmpExplanation, tmpNodeBgColor, tmpNodeBorderColor, tmpNodeTextColor, tmpArrowColor,tmpCode) => {
        
        $animationQuery.push({
            curArr: [...tmpArr],
            curArrowArr: [...tmpArrowArr],
            curUnderArrowArr: [...tmpUnderArrowArr], // 아래 화살표 배열
            curNodePositions: [...tmpNodePositions],
            curArrowPositions: [...tmpArrowPositions],
            curUnderArrowPositions: [...tmpUnderArrowPositions],
            curNodeAnimation: [...tmpNodeAnimations],
            curArrowAnimations: [...tmpArrowAnimations],
            curArrowUnderAnimations: [...tmpArrowUnderAnimations],
            curExplanation: tmpExplanation,
            curNodeBgColor: [...tmpNodeBgColor],
            curNodeBorderColor: [...tmpNodeBorderColor],
            curNodeTextColor: [...tmpNodeTextColor],
            curArrowColor: [...tmpArrowColor],
            curCode :tmpCode
        });

    };
    
    // 애니메이션(삽입)
    const generateEnqueueQueries = (pushValue , direction) => {

        const nodeBg = {normal: "#ffffff", selected: "#ed8925", completed: "#259aed"};
        const nodeBorderColor = {normal: "#000000", selected: "#d97511", completed: "#1178d9"};
        const textColor = {normal: "#000000", selected: "#ffffff"};
        const arrowColor = {normal: "#000000", connecting: "#0067a3"};

        const resetNodePositions = () => {
            if (nodePositions.length === 0) {
                // 초기 상태인 경우 첫 번째 노드 위치 설정
                nodePositions = numArr.map((_, index) => ({
                    x: index * (nodeWidth + arrowLength), // 노드 간의 간격 계산
                    y: canvasHeight / 2 - nodeHeight / 2, // 초기 Y 위치 설정
                }));
            } else {
                // 기존 노드의 시작 위치 가져오기
                const startX = nodePositions[0].x; // 첫 노드의 X 좌표를 기준으로 계산
                const startY = nodePositions[0].y; // 첫 노드의 Y 좌표 유지

                // 모든 노드의 위치를 리셋 (기존 위치 기반)
                nodePositions = numArr.map((_, index) => ({
                    x: startX + index * (nodeWidth + arrowLength), // 기존 간격을 유지하며 위치 계산
                    y: startY, // 기존 Y 좌표 유지
                }));
            }
        };

        const RresetNodePositions = () => {
            if (nodePositions.length === 0) {
                // 초기 상태인 경우, 예를 들어 캔버스의 오른쪽을 기준으로 마지막 노드가 위치하도록 설정
                // (여기서는 캔버스 오른쪽에서 노드 너비와 화살표 길이만큼 여유를 둔다)
                const lastX = canvasWidth - nodeWidth - arrowLength;
                const startY = canvasHeight / 2 - nodeHeight / 2;
                nodePositions = numArr.map((_, index) => ({
                    // 마지막 노드(index = numArr.length - 1)가 lastX에 위치하도록 하고,
                    // 앞의 노드들은 왼쪽으로 (nodeWidth + arrowLength)씩 배치
                    x: lastX - (numArr.length - 1 - index) * (nodeWidth + arrowLength),
                    y: startY,
                }));
            } else {
                // 기존에 위치 정보가 있다면, 마지막 노드의 위치를 기준으로 계산
                const lastIndex = nodePositions.length - 1;
                const lastX = nodePositions[lastIndex].x;
                const lastY = nodePositions[lastIndex].y;
                nodePositions = numArr.map((_, index) => ({
                    x: lastX - (numArr.length - 1 - index) * (nodeWidth + arrowLength),
                    y: lastY,
                }));
            }
        };

        const resetArrowPositions = () => {
            syncArrowArr();
            if (nodePositions.length === 0 || numArr.length <= 1) {
                // 화살표가 없는 상태 (노드가 하나이거나 없는 경우)
                arrowPositions = [];
                arrowUnderPositions = [];
            } else {
                // 기존 노드 위치를 기준으로 화살표 위치 계산
                arrowPositions = arrowArr.map((_, index) => ({
                    x: nodePositions[index].x + nodeWidth, // 화살표는 노드의 끝 부분에서 시작
                    y: nodePositions[index].y + nodeHeight / 2, // 노드의 중앙 높이에 위치
                }));
                arrowUnderPositions = underArrowArr.map((_, index) => ({
                    x: nodePositions[index].x + nodeWidth,
                    y: nodePositions[index].y + nodeHeight / 2 + 20, // 오프셋 값은 필요에 따라 조정하세요.
                }));
            }
        };
        

        $animationQuery = [];

        let tmpArr = [...numArr];
        let tmpArrowArr = [...arrowArr];
        let tmpUnderArrowArr = [...underArrowArr];
        let tmpNodePositions = [...nodePositions];
        let tmpArrowPositions = [...arrowPositions];
        let tmpUnderArrowPositions = [...arrowUnderPositions];
        let tmpNodeAnimations = [...nodeAnimations];
        let tmpArrowAnimations = [...arrowAnimations];
        let tmpArrowUnderAnimations = [...arrowUnderAnimations];
        let tmpExplanation = ``;
        let tmpNodeBgColor = Array(tmpArr.length).fill(nodeBg.normal);
        let tmpNodeBorderColor = Array(tmpArr.length).fill(nodeBorderColor.normal);
        let tmpNodeTextColor = Array(tmpArr.length).fill(textColor.normal);
        let tmpArrowColor = Array(tmpArrowArr.length).fill(arrowColor.normal);
        let tmpCode = 1000;

        if (numArr.length === 0) { // 배열의 길이가 0인 경우
            
            tmpExplanation = `배열의 초기 상태입니다.`;
            pushEnqueueAnimationQuery(tmpArr, tmpArrowArr, tmpUnderArrowArr, tmpNodePositions, tmpArrowPositions, tmpUnderArrowPositions, tmpNodeAnimations, tmpArrowAnimations, tmpArrowUnderAnimations, tmpExplanation, tmpNodeBgColor, tmpNodeBorderColor, tmpNodeTextColor, tmpArrowColor,tmpCode);

            tmpExplanation = `배열의 초기 상태입니다.`; // 애니메이션 재실행 보험용 쿼리
            pushEnqueueAnimationQuery(tmpArr, tmpArrowArr, tmpUnderArrowArr, tmpNodePositions, tmpArrowPositions, tmpUnderArrowPositions, tmpNodeAnimations, tmpArrowAnimations, tmpArrowUnderAnimations, tmpExplanation, tmpNodeBgColor, tmpNodeBorderColor, tmpNodeTextColor, tmpArrowColor,tmpCode);

            // Step 1: 새로운 노드 추가
            tmpCode = 0;
            tmpExplanation = `새로운 노드(${pushValue})가 추가되었습니다`;

            numArr.push(pushValue); // 숫자 추가
            calculateNumArrPositionsNA(); // 포지션 리셋
            nodeAnimations.push(true);

            tmpArr = [...numArr];
            tmpArrowArr = [...arrowArr];
            tmpNodePositions = [...nodePositions];
            tmpArrowPositions = [...arrowPositions];
            tmpNodeAnimations = [...nodeAnimations];
            tmpArrowAnimations = [...arrowAnimations];
        
            tmpNodeBgColor = [...tmpNodeBgColor, nodeBg.selected]; 
            tmpNodeBorderColor = [...tmpNodeBorderColor, nodeBorderColor.selected];
            tmpNodeTextColor = [...tmpNodeTextColor, textColor.selected];
            
            pushEnqueueAnimationQuery(tmpArr, tmpArrowArr, tmpUnderArrowArr, tmpNodePositions, tmpArrowPositions, tmpUnderArrowPositions, tmpNodeAnimations, tmpArrowAnimations, tmpArrowUnderAnimations, tmpExplanation, tmpNodeBgColor, tmpNodeBorderColor, tmpNodeTextColor, tmpArrowColor,tmpCode);
            return;
        };

        if(direction === 'Back') {

            tmpExplanation = `배열의 초기 상태입니다.`;
            pushEnqueueAnimationQuery(tmpArr, tmpArrowArr, tmpUnderArrowArr, tmpNodePositions, tmpArrowPositions, tmpUnderArrowPositions, tmpNodeAnimations, tmpArrowAnimations, tmpArrowUnderAnimations, tmpExplanation, tmpNodeBgColor, tmpNodeBorderColor, tmpNodeTextColor, tmpArrowColor,tmpCode);

            tmpExplanation = `배열의 초기 상태입니다.`; // 애니메이션 재실행 보험용 쿼리
            pushEnqueueAnimationQuery(tmpArr, tmpArrowArr, tmpUnderArrowArr, tmpNodePositions, tmpArrowPositions, tmpUnderArrowPositions, tmpNodeAnimations, tmpArrowAnimations, tmpArrowUnderAnimations, tmpExplanation, tmpNodeBgColor, tmpNodeBorderColor, tmpNodeTextColor, tmpArrowColor,tmpCode);

            // Step 1: 새로운 노드 추가
            tmpCode = 0;
            tmpExplanation = `새로운 노드(${pushValue})가 추가되었습니다`;

            numArr.push(pushValue); // 숫자 추가
    
            resetNodePositions(); // 포지션 리셋
            nodeAnimations.push(true);

            tmpArr = [...numArr];
            tmpArrowArr = [...arrowArr];
            tmpNodePositions = [...nodePositions];
            tmpArrowPositions = [...arrowPositions];
            tmpUnderArrowArr = [...underArrowArr]; // 아래
            tmpUnderArrowPositions = [...arrowUnderPositions];// 아래
            tmpNodeAnimations = [...nodeAnimations];
            tmpArrowAnimations = [...arrowAnimations];

            tmpNodeBgColor[tmpArr.length - 2] = nodeBg.completed;
            tmpNodeBorderColor[tmpArr.length - 2] = nodeBorderColor.completed;
            tmpNodeTextColor[tmpArr.length - 2] = textColor.selected;
        
            tmpNodeBgColor = [...tmpNodeBgColor, nodeBg.selected]; 
            tmpNodeBorderColor = [...tmpNodeBorderColor, nodeBorderColor.selected];
            tmpNodeTextColor = [...tmpNodeTextColor, textColor.selected];
            
            pushEnqueueAnimationQuery(tmpArr, tmpArrowArr, tmpUnderArrowArr, tmpNodePositions, tmpArrowPositions, tmpUnderArrowPositions, tmpNodeAnimations, tmpArrowAnimations, tmpArrowUnderAnimations, tmpExplanation, tmpNodeBgColor, tmpNodeBorderColor, tmpNodeTextColor, tmpArrowColor,tmpCode);
            
            // Step 2: 화살표 연결
            tmpCode = 1;
            tmpExplanation = `새로운 노드(${pushValue})와 기존 노드(${tmpArr[tmpArr.length - 2]}) 간의 연결을 수행 합니다`;

            resetArrowPositions(); // 화살표 포지션 리셋
            arrowAnimations.push(true);
            arrowUnderAnimations.push(true);

            tmpArr = [...numArr];
            tmpArrowArr = [...arrowArr];
            tmpNodePositions = [...nodePositions];
            tmpArrowPositions = [...arrowPositions];
            tmpNodeAnimations = [...nodeAnimations];
            tmpArrowAnimations = [...arrowAnimations];

            tmpUnderArrowArr = [...underArrowArr]; // 아래
            tmpUnderArrowPositions = [...arrowUnderPositions]; // 아래
            tmpArrowUnderAnimations = [...arrowUnderAnimations];


            pushEnqueueAnimationQuery(tmpArr, tmpArrowArr, tmpUnderArrowArr, tmpNodePositions, tmpArrowPositions, tmpUnderArrowPositions, tmpNodeAnimations, tmpArrowAnimations, tmpArrowUnderAnimations, tmpExplanation, tmpNodeBgColor, tmpNodeBorderColor, tmpNodeTextColor, tmpArrowColor,tmpCode);
            
            
            // Step 3: 마무리 쿼리
            tmpCode = 2;
            tmpExplanation = `연결이 완료되었습니다`;
        

            tmpArrowArr = [...arrowArr];
            tmpNodePositions = [...nodePositions];
            tmpArrowPositions = [...arrowPositions];
            tmpNodeAnimations = [...nodeAnimations];
            tmpArrowAnimations = [...arrowAnimations];

            tmpUnderArrowArr = [...underArrowArr]; // 아래
            tmpUnderArrowPositions = [...arrowUnderPositions]; // 아래


            tmpNodeBgColor[numArr.length - 1] = nodeBg.completed;
            tmpNodeBorderColor[numArr.length - 1] = nodeBorderColor.completed;
            tmpNodeTextColor[numArr.length - 1] = textColor.selected;

            tmpNodeBgColor[numArr.length - 2] = nodeBg.normal;
            tmpNodeBorderColor[numArr.length - 2] = nodeBorderColor.normal;
            tmpNodeTextColor[numArr.length - 2] = textColor.normal;

            pushEnqueueAnimationQuery(tmpArr, tmpArrowArr, tmpUnderArrowArr, tmpNodePositions, tmpArrowPositions, tmpUnderArrowPositions, tmpNodeAnimations, tmpArrowAnimations, tmpArrowUnderAnimations, tmpExplanation, tmpNodeBgColor, tmpNodeBorderColor, tmpNodeTextColor, tmpArrowColor,tmpCode);

            // Step 3: 마지막 쿼리

            tmpCode = 2;
            tmpExplanation = `연결이 완료되었습니다`;

            calculateNumArrPositionsNA(); // 위치 리셋

            tmpArrowArr = [...arrowArr];
            tmpNodePositions = [...nodePositions];
            tmpArrowPositions = [...arrowPositions];
            tmpNodeAnimations = [...nodeAnimations];
            tmpArrowAnimations = [...arrowAnimations];

            tmpUnderArrowArr = [...underArrowArr]; // 아래
            tmpUnderArrowPositions = [...arrowUnderPositions]; // 아래

            
            pushEnqueueAnimationQuery(tmpArr, tmpArrowArr, tmpUnderArrowArr, tmpNodePositions, tmpArrowPositions, tmpUnderArrowPositions, tmpNodeAnimations, tmpArrowAnimations, tmpArrowUnderAnimations, tmpExplanation, tmpNodeBgColor, tmpNodeBorderColor, tmpNodeTextColor, tmpArrowColor,tmpCode);
            
            return;
        };

        if(direction === 'Front') {

            tmpExplanation = `배열의 초기 상태입니다.`;
            pushEnqueueAnimationQuery(tmpArr, tmpArrowArr, tmpUnderArrowArr, tmpNodePositions, tmpArrowPositions, tmpUnderArrowPositions, tmpNodeAnimations, tmpArrowAnimations, tmpArrowUnderAnimations, tmpExplanation, tmpNodeBgColor, tmpNodeBorderColor, tmpNodeTextColor, tmpArrowColor,tmpCode);

            tmpExplanation = `배열의 초기 상태입니다.`; // 애니메이션 재실행 보험용 쿼리
            pushEnqueueAnimationQuery(tmpArr, tmpArrowArr, tmpUnderArrowArr, tmpNodePositions, tmpArrowPositions, tmpUnderArrowPositions, tmpNodeAnimations, tmpArrowAnimations, tmpArrowUnderAnimations, tmpExplanation, tmpNodeBgColor, tmpNodeBorderColor, tmpNodeTextColor, tmpArrowColor,tmpCode);

            // Step 1: 새로운 노드 추가
            tmpCode = 0;
            tmpExplanation = `새로운 노드(${pushValue})가 추가되었습니다`;

            numArr.unshift(pushValue); // 숫자 추가

            RresetNodePositions(); // 포지션 리셋

            nodeAnimations.unshift(true);

            tmpArr = [...numArr];
            tmpArrowArr = [...arrowArr];
            tmpNodePositions = [...nodePositions];
            tmpArrowPositions = [...arrowPositions];
            tmpUnderArrowArr = [...underArrowArr]; // 아래
            tmpUnderArrowPositions = [...arrowUnderPositions];// 아래
            tmpNodeAnimations = [...nodeAnimations];
            tmpArrowAnimations = [...arrowAnimations];

            tmpNodeBgColor[1] = nodeBg.completed;
            tmpNodeBorderColor[1] = nodeBorderColor.completed;
            tmpNodeTextColor[1] = textColor.selected;

            tmpNodeBgColor[0] = nodeBg.selected;
            tmpNodeBorderColor[0] = nodeBorderColor.selected;
            tmpNodeTextColor[0] = textColor.selected;

            pushEnqueueAnimationQuery(tmpArr, tmpArrowArr, tmpUnderArrowArr, tmpNodePositions, tmpArrowPositions, tmpUnderArrowPositions, tmpNodeAnimations, tmpArrowAnimations, tmpArrowUnderAnimations, tmpExplanation, tmpNodeBgColor, tmpNodeBorderColor, tmpNodeTextColor, tmpArrowColor,tmpCode);

            // Step 2: 화살표 연결
            tmpCode = 1;
            tmpExplanation = `새로운 노드(${pushValue})와 기존 노드(${tmpArr[tmpArr.length - 2]}) 간의 연결을 수행 합니다`;

            resetArrowPositions(); // 화살표 포지션 리셋
            arrowAnimations.unshift(true);
            arrowUnderAnimations.unshift(true);
            
            tmpArr = [...numArr];
            tmpArrowArr = [...arrowArr];
            tmpNodePositions = [...nodePositions];
            tmpArrowPositions = [...arrowPositions];
            tmpNodeAnimations = [...nodeAnimations];
            tmpArrowAnimations = [...arrowAnimations];

            tmpUnderArrowArr = [...underArrowArr]; // 아래
            tmpUnderArrowPositions = [...arrowUnderPositions]; // 아래
            tmpArrowUnderAnimations = [...arrowUnderAnimations];


            pushEnqueueAnimationQuery(tmpArr, tmpArrowArr, tmpUnderArrowArr, tmpNodePositions, tmpArrowPositions, tmpUnderArrowPositions, tmpNodeAnimations, tmpArrowAnimations, tmpArrowUnderAnimations, tmpExplanation, tmpNodeBgColor, tmpNodeBorderColor, tmpNodeTextColor, tmpArrowColor,tmpCode);


            // Step 3: 마무리 쿼리
            tmpCode = 2;
            tmpExplanation = `연결이 완료되었습니다`;


            tmpArrowArr = [...arrowArr];
            tmpNodePositions = [...nodePositions];
            tmpArrowPositions = [...arrowPositions];
            tmpNodeAnimations = [...nodeAnimations];
            tmpArrowAnimations = [...arrowAnimations];

            tmpUnderArrowArr = [...underArrowArr]; // 아래
            tmpUnderArrowPositions = [...arrowUnderPositions]; // 아래


            tmpNodeBgColor[1] = nodeBg.normal;
            tmpNodeBorderColor[1] = nodeBorderColor.normal;
            tmpNodeTextColor[1] = textColor.normal;

            tmpNodeBgColor[0] = nodeBg.completed;
            tmpNodeBorderColor[0] = nodeBorderColor.completed;
            tmpNodeTextColor[0] = textColor.selected;

            pushEnqueueAnimationQuery(tmpArr, tmpArrowArr, tmpUnderArrowArr, tmpNodePositions, tmpArrowPositions, tmpUnderArrowPositions, tmpNodeAnimations, tmpArrowAnimations, tmpArrowUnderAnimations, tmpExplanation, tmpNodeBgColor, tmpNodeBorderColor, tmpNodeTextColor, tmpArrowColor,tmpCode);

            // Step 3: 마지막 쿼리

            tmpCode = 2;
            tmpExplanation = `연결이 완료되었습니다`;

            calculateNumArrPositionsNA(); // 위치 리셋

            tmpArrowArr = [...arrowArr];
            tmpNodePositions = [...nodePositions];
            tmpArrowPositions = [...arrowPositions];
            tmpNodeAnimations = [...nodeAnimations];
            tmpArrowAnimations = [...arrowAnimations];

            tmpUnderArrowArr = [...underArrowArr]; // 아래
            tmpUnderArrowPositions = [...arrowUnderPositions]; // 아래


            pushEnqueueAnimationQuery(tmpArr, tmpArrowArr, tmpUnderArrowArr, tmpNodePositions, tmpArrowPositions, tmpUnderArrowPositions, tmpNodeAnimations, tmpArrowAnimations, tmpArrowUnderAnimations, tmpExplanation, tmpNodeBgColor, tmpNodeBorderColor, tmpNodeTextColor, tmpArrowColor,tmpCode);

            return;
            };
    };

    // 애니메이션(Push) 실행
    const executeEnqueueQueries = async (myAsync) => {
        $animationStep = [0, $animationQuery.length - 1];

        while (true) {
            if ((myAsync + 1) !== $asyncCnt) break;

            if ($animationStep[0] === $animationStep[1]) {
                $pausedIcon = true;
                $isPaused = true;
            }

            await drawEnqueueAnimation($animationStep[0]);
            await waitPause();
            if($animationSpeed <= 30) await delay(20);

            if (!$fromBtn) {
                $animationStep[0] = Math.min($animationStep[0] + 1, $animationStep[1]);
            }
        }
    };

    // 각 단계의 애니메이션(Push) 렌더링
    const drawEnqueueAnimation = async (i) => {

        $explanation = $animationQuery[i].curExplanation; // $explanation 수정
        changeCodeColor($animationQuery[i].curCode); // $codeColor 

        numArr = [...$animationQuery[i].curArr];

        arrowArr = [...$animationQuery[i].curArrowArr];

        underArrowArr = [...$animationQuery[i].curUnderArrowArr];

        nodePositions = [...$animationQuery[i].curNodePositions];
        nodeAnimations = [...$animationQuery[i].curNodeAnimation];

        arrowPositions = [...$animationQuery[i].curArrowPositions];
        arrowAnimations = [...$animationQuery[i].curArrowAnimations];

        arrowUnderPositions = [...$animationQuery[i].curUnderArrowPositions];
        arrowUnderAnimations = [...$animationQuery[i].curArrowUnderAnimations];

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
            resetPosition();
        }

        await delay(1000 * (1 / $animationSpeed)); // 애니메이션 지연
    };


    //=================================[ 삽입 끝 ]============================================ 

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
        <DequeNavigation {numArr}
        on:createRandomArr={createRandomArr} 
        on:createInputtedArr={createInputtedArr} 
        on:startEnqueue={startEnqueue}
        on:startDequeue={(e) => startDequeue(e.detail.direction)}
        on:startPeek={(e) => startPeek(e.detail.direction)}
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
                데크(Deque)
            </div>

            <div class="canvas">
                <!-- canvas안에 자료구조, 알고리즘 구현 -->

                {#each nodePositions as pos, index (index)}
                    {#if index < numArr.length}
                        <div
                            class="node {nodeAnimations[index] ? 'node-animation' : ''} {nodeDequeueAnimations[index] ? 'node-Dequeue-animation' : ''}"
                            style="top: {pos.y}px; left: {pos.x}px; width: {nodeWidth}px; height: {nodeHeight}px;">
                            {numArr[index]}

                            {#if operation === 'peek'}
                                {#if activePeek && ((peekDirection === 'Front' && index === 0) || (peekDirection === 'Back' && index === numArr.length - 1))}
                                    <!-- 현재 활성 피크 깃발: appear 효과 -->
                                    <div class="flag-container appear">
                                        <div class="flag-pole"></div>
                                        <div class="flag-cloth"></div>
                                    </div>
                                {:else if oldPeekIndex === index}
                                    <!-- 이전 피크 깃발: disappear 효과 -->
                                    <div class="flag-container disappear">
                                        <div class="flag-pole"></div>
                                        <div class="flag-cloth"></div>
                                    </div>
                                {/if}
                            {/if}

                        </div>
                    {/if}
                {/each}

                {#each arrowPositions as arrow, index (index)}
                    {#if arrowArr[index] === 1 && index < arrowArr.length}
                        <svg
                            class="arrow"
                            style="top: {arrow.y - 5}px; left: {arrow.x + 8}px;"
                            width="{arrowLength - 8}" height="10" viewBox="0 0 {arrowLength} 10" xmlns="http://www.w3.org/2000/svg"
                        >
                            <line
                                class="{arrowAnimations[index] ? 'queue-arrow-line-animation' : ''} {arrowDequeueAnimations[index] ? 'queue-arrow-line-animation-Dequeue' : ''}"
                                x1="0" y1="5" x2="{arrowLength - 10}" y2="5" 
                                stroke="#000" stroke-width="3.5" 
                            />
                            <polygon
                                class="{arrowAnimations[index] ? 'queue-arrow-head-animation' : ''} {arrowDequeueAnimations[index] ? 'queue-arrow-head-animation-Dequeue' : ''}"
                                points="{arrowLength - 10},0 {arrowLength - 10},10 {arrowLength},5" 
                                fill="#000" 
                            />
                        </svg>
                    {/if}
                {/each}

                {#each arrowUnderPositions as arrow, index (index)}
                    {#if underArrowArr[index] === 1 && index < arrowUnderPositions.length}
                        <svg
                            class="arrow arrow-under"
                            style="top: {arrow.y - 15}px; left: {arrow.x + 8}px;"
                            width="{arrowLength - 8}" height="10" viewBox="0 0 {arrowLength} 10" xmlns="http://www.w3.org/2000/svg"
                        >
                            <line
                                class="{arrowUnderAnimations[index] ? 'queue-arrow-line-animation' : ''} {arrowUnderDequeueAnimations[index] ? 'queue-arrow-line-animation-Dequeue' : ''}"
                                x1="0" y1="5" x2="{arrowLength - 10}" y2="5" 
                                stroke="#000" stroke-width="3.5" 
                            />
                            <polygon
                                class="{arrowUnderAnimations[index] ? 'queue-arrow-head-animation' : ''} {arrowUnderDequeueAnimations[index] ? 'queue-arrow-head-animation-Dequeue' : ''}"
                                points="{arrowLength - 10},0 {arrowLength - 10},10 {arrowLength},5" 
                                fill="#000" 
                            />
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
                    {:else if operation === 'Dequeue'}
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

                    {:else if operation === 'Dequeue'}
                        <!-- Dequeue 연산 의사코드 -->
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

    .arrow-under {
        transform: rotate(180deg);
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

    @keyframes queue-arrow-line-grow {
        0% {
            transform: scaleX(0);
            opacity: 0;
        }
        100% {
            transform: scaleX(1);
            opacity: 1;
        }
    }

    @keyframes queue-arrow-head-move {
        0% {
            transform: translateX(-100%);
            opacity: 0;
        }
        100% {
            transform: translateX(0);
            opacity: 1;
        }
    }

    .queue-arrow-line-animation {
        animation: queue-arrow-line-grow calc(0.5s / var(--speed, 1)) ease-out;
        transform-origin: left;
    }

    .queue-arrow-head-animation {
        animation: queue-arrow-head-move calc(0.5s / var(--speed, 1)) ease-out;
    }

    @keyframes shrink { /* 노드 축소 애니메이션*/
        0% {
            transform: scale(1);
            opacity: 1;
        }
        100% {
            transform: scale(0);
            opacity: 0;
        }
    }

    :global(.node-Dequeue-animation) {
        animation: shrink calc(0.5s / var(--speed, 1)) ease-out;
    }

    @keyframes queue-arrow-line-shrink {
        0% {
            transform: scaleX(1);
            opacity: 0;
        }
        100% {
            transform: scaleX(0);
            opacity: 1;
        }
    }

    @keyframes queue-arrow-head-move-reverse {
        0% {
            transform: translateX(0);
            opacity: 0;
        }
        100% {
            transform: translateX(-100%);
            opacity: 1;
        }
    }

    .queue-arrow-line-animation-Dequeue {
        animation: queue-arrow-line-shrink calc(0.5s / var(--speed, 1)) ease-out;
        transform-origin: left;
    }

    .queue-arrow-head-animation-Dequeue {
        animation: queue-arrow-head-move-reverse calc(0.5s / var(--speed, 1)) ease-out;
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