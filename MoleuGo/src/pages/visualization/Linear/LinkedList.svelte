<script>
    import { onDestroy, onMount, tick   } from "svelte";
    import Header from "../../../component/Header.svelte";
    import LinkedListNavigation from "../../../component/navigation/Linear/LinkedListNavigation.svelte";
    import {isListVisible} from "../../../lib/store.js";
    import { isPaused, pausedIcon, fromBtn, isReplay, explanation, animationSpeed, animationWorking, animationQuery, codeColor, animationStep, 
             asyncCnt, gradient, indentSize, maxSpeed } from "../../../lib/visualizationStore";

    let numNode = [15, 10, 20, 30, 7]
    let nodePositions = []; // 노드의 실제 위치 저장
    let selectNode = null; // 추가: 강조된 노드의 인덱스
    
    $: totalWidth = numNode.length * (90);
    $: offsetX = (200 - totalWidth) / 2;


    const removeLastArrow = () => {
        const arrows = document.querySelectorAll(".node-arrow");
        if (arrows.length > 0) {
            const lastArrow = arrows[arrows.length - 1];
            lastArrow.style.backgroundColor = "transparent";
            lastArrow.style.borderColor = "transparent";
        }
    };
    
    const updateNodePositions = async () => {
        await tick();
        const nodes = document.querySelectorAll(".linked-list-node-container");

        let customOffsetX = -50;
        let customOffsetY = -10;

        nodes.forEach((node) => {
            node.addEventListener('transitionend', updateNodePositions, { once: true });
        });
        
        nodePositions = Array.from(nodes).map((node, index) => {
            const rect = node.getBoundingClientRect();
            return {
                index, // DOM의 노드 순서
                value: node.querySelector(".node-value")?.textContent.trim(), // 노드의 값
                x: rect.left + rect.width / 2 + customOffsetX, // 노드 중심 x 좌표
                y: rect.top + rect.height / 2 + customOffsetY, // 노드 중심 y 좌표
            };
        });
    };

    const handleSelectNode = (nodeIndex) => {
        // 애니메이션 실행 중에는 처리하지 않음
        if ($animationWorking) {
            return;
        }

        selectNode = nodeIndex;
        updateSelectedNode();
    };

    const updateSelectedNode = () => {
        const nodes = document.querySelectorAll(".linked-list-node");
        nodes.forEach((node, index) => {
            if (selectNode === index) {
                node.style.borderColor = "#ec3434"; // 강조 색상
            } else {
                node.style.borderColor = "#000000"; // 기본 색상
            }
        });
    };

    $: {
        if (!$animationWorking) {
            updateSelectedNode();
        }
    }
    

    onMount(() => {
        updateNodePositions();
        window.addEventListener("resize", updateNodePositions); // 창 크기 조정 시 업데이트
    });


    // 페이지 바뀌면 애니메이션 종료
    onDestroy(() => {
        $animationSpeed = 1;
        InitAnimation();
        window.removeEventListener("resize", updateNodePositions);
    });

    $: if (numNode) {
        tick().then(updateNodePositions);
    }
    
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

        const nodes = document.querySelectorAll(".linked-list-node");
        const arrows = document.querySelectorAll(".node-arrow");

        nodes.forEach(node => {
            node.style.backgroundColor = "#ffffff"; // 기본 배경색
            node.style.borderColor = "#000000"; // 기본 테두리 색상
            node.style.color = "#000000"; // 기본 텍스트 색상
        });

        arrows.forEach(arrow => {
            arrow.style.backgroundColor = "#000000"; // 기본 화살표 색상
            arrow.style.borderColor = "#000000"; // 기본 화살표 테두리 색상
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
//**************************************************삽입 애니메이션******************************************************************// 
   
    const startLinkedListInsert = async (e) => {
            InitAnimation();

            const InsertIndex = selectNode;
            const InsertValue = Number(e.detail.nodeValue.trim());

            generateLinkedListInsertQueries(InsertIndex,InsertValue);

            $animationWorking = true;
            $pausedIcon = false;
            $isPaused = false;

            executeLinkedListInsertAnimation($asyncCnt++);
        };

        

    // 애니메이션 단계 저장
    const pushAnimationInsertQuery = (index, nextIndex, explanationText, codeIndex, nodeStyles, arrowStyles, arrowHeadStyles, currentNodeArray) => {
        $animationQuery.push({
            index,
            nextIndex,
            explanation: explanationText,
            codeIndex,
            nodeStyles: [...nodeStyles],
            arrowStyles: [...arrowStyles],
            arrowHeadStyle: [...arrowHeadStyles],
            currentNodeArray: [...currentNodeArray]
        });
    };

    const generateLinkedListInsertQueries = (targetIndex,InsertValue) => {
        // 색상 팔레트
        const NodeBg = { normal: "#ffffff", selected: "#ffffff", found: "#91a9ed", validating: "#c6ffc6", notFound: "#ffffff" };
        const NodeBorderColor = { normal: "#000000", selected: "#28e02e", found: "#1f50d4", notFound: "#ff0000", hidden:"#ffffff" };
        const NodeTextColor = { normal: "#000000", selected: "#28e02e", found: "#ffffff", validating: "#28e02e", notFound: "#28e02e", hidden:"#ffffff" };
        const ArrowColor = { normal: "#000000", selected: "#28e02e", hidden:"#ffffff" };
        const ArrowHeadColor = { normal: "#000000", selected: "#28e02e", hidden:"#ffffff" };

        $animationQuery = [];
        
        console.log(numNode)
        const nodeStyles = numNode.map(() => ({
            bgColor: NodeBg.normal,
            borderColor: NodeBorderColor.normal,
            textColor: NodeTextColor.normal // 텍스트 색상 기본값 추가
        }));
        const arrowStyles = numNode.map((_, idx) => ({
            bgColor: idx < numNode.length - 1 ? ArrowColor.normal : "transparent", // 마지막 노드 화살표는 투명
        }));

        const arrowHeadStyle = numNode.map((_, idx) => ({
            bgColor: idx < numNode.length - 1 ? ArrowHeadColor.normal : "transparent", // 마지막 노드 화살표는 투명
        }));
        
        let foundIndex = -1;

        pushAnimationInsertQuery(
            0,
            -1,
            ``,
            0,
            [...nodeStyles],
            [...arrowStyles],
            [...arrowHeadStyle],
            [...numNode]
        );

        for (let i = 0; i < numNode.length; i++) {
            const isLastNode = i === numNode.length - 1;

            // 검증 애니메이션 단계
            nodeStyles[i] = { 
                bgColor: NodeBg.validating, 
                borderColor: NodeBorderColor.selected, 
                textColor: NodeTextColor.selected
            };

            pushAnimationInsertQuery(
                i,
                -1,
                `노드 ${i}를 검증 중입니다.`,
                0,
                [...nodeStyles],
                [...arrowStyles],
                [...arrowHeadStyle],
                [...numNode]
            );

            // Index가 맞지 않을 경우 원래 상태로 복귀
            if (i !== targetIndex) {
                nodeStyles[i] = { 
                    bgColor: NodeBg.normal, 
                    borderColor: NodeBorderColor.selected, 
                    textColor: NodeTextColor.selected 
                };

                pushAnimationInsertQuery(
                    i,
                    -1,
                    `노드 ${i}는 삽입 위치(${targetIndex})가 아닙니다.`,
                    0,
                    [...nodeStyles],
                    [...arrowStyles],
                    [...arrowHeadStyle],
                    [...numNode]
                );
            }

            // Index가 일치하는 경우 삽입 위치로 찾음
            if (i === targetIndex) {
                foundIndex = i;
                nodeStyles[i] = { 
                    bgColor: NodeBg.normal, 
                    borderColor: NodeBorderColor.selected, 
                    textColor: NodeTextColor.selected 
                };

                pushAnimationInsertQuery(i, -1, `삽입 위치(${targetIndex})를 찾았습니다`, 2, [...nodeStyles], [...arrowStyles], [...arrowHeadStyle], [...numNode]);

                nodeStyles[i] = { 
                    bgColor: NodeBg.normal, 
                    borderColor: NodeBorderColor.selected, 
                    textColor: NodeTextColor.selected,
                };

                if (targetIndex > 0) {
                    arrowStyles[targetIndex - 1] = { bgColor: ArrowColor.hidden };
                    arrowHeadStyle[targetIndex - 1] = { bgColor: ArrowHeadColor.hidden };
                }

                pushAnimationInsertQuery(
                    i,
                    -1,
                    `삽입 위치(${targetIndex})를 찾았습니다`,
                    3,
                    [...nodeStyles],
                    [...arrowStyles],
                    [...arrowHeadStyle],
                    [...numNode]
                );
                
                numNode.splice(targetIndex, 0, InsertValue); // 실제 값 삽입
                console.log(numNode)

                nodeStyles.splice(targetIndex, 0, {
                    bgColor: NodeBg.normal,
                    borderColor: NodeBorderColor.normal,
                    textColor: NodeTextColor.normal,
                });


                nodeStyles[i] = { 
                    bgColor: NodeBg.found, 
                    borderColor: NodeBorderColor.found, 
                    textColor: NodeTextColor.found,
                };

                nodeStyles[i+1] = { 
                    bgColor: NodeBg.normal, 
                    borderColor: NodeBorderColor.selected, 
                    textColor: NodeTextColor.selected,
                };

                arrowStyles[i] = { bgColor: ArrowColor.hidden };
                arrowHeadStyle[i] = { bgColor: ArrowHeadColor.hidden };
                

                pushAnimationInsertQuery(
                    i,
                    -1,
                    `값 ${InsertValue}을(를) ${targetIndex} 위치에 삽입합니다.`,
                    3,
                    [...nodeStyles],
                    [...arrowStyles],
                    [...arrowHeadStyle],
                    [...numNode]
                );
                
                
                break;
            }

            // 삽입 위치가 아닌 경우 화살표 이동
            if (!isLastNode && foundIndex === -1) {
                arrowStyles[i] = { bgColor: ArrowColor.selected };
                pushAnimationInsertQuery(
                    i,
                    i + 1,
                    `노드 ${i}에서 노드 ${i + 1}로 이동합니다.`,
                    1,
                    [...nodeStyles],
                    [...arrowStyles],
                    [...arrowHeadStyle],
                    [...numNode]
                );
            }
        }

        $animationStep = [0, $animationQuery.length - 1];
    };

    // 애니메이션 실행
    const executeLinkedListInsertAnimation = async (myAsync) => {
        $animationStep = [0, $animationQuery.length - 1];
        
        while (true) {
            if ((myAsync + 1) !== $asyncCnt) break;

            if ($animationStep[0] === $animationStep[1]) {
                $pausedIcon = true;
                $isPaused = true;
            }

            await drawLinkedListInsertAnimation($animationStep[0]);
            await waitPause();

            if (!$fromBtn) {
                $animationStep[0] = Math.min($animationStep[0] + 1, $animationStep[1]);
            }
        }
    };

    // 각 단계의 애니메이션 렌더링
    const drawLinkedListInsertAnimation = async (i) => {
        const nodes = document.querySelectorAll(".linked-list-node");
        const arrows = document.querySelectorAll(".node-arrow");
        const arrowHeads = document.querySelectorAll(".node-arrow::after");

        $explanation = $animationQuery[i].explanation;

        // 노드 상태 복원
        $animationQuery[i].nodeStyles.forEach((style, idx) => {
            const nodeElement = nodes[idx];

            if (!nodeElement) {
                console.warn(`Node at index ${idx} not found.`);
                return;
            }

            nodeElement.style.transition = "border-color 0.3s ease, background-color 0.3s ease";
            nodeElement.style.borderColor = style.borderColor;
            nodeElement.style.backgroundColor = style.bgColor;
            nodeElement.style.color = style.textColor;
        });

        // 화살표 상태 복원
        $animationQuery[i].arrowStyles.forEach((style, idx) => {
            const arrowElement = arrows[idx];
            arrowElement.style.transition = "background-color 0.3s ease";
            arrowElement.style.backgroundColor = style.bgColor;
        });

        // 화살표 머리 상태 복원
        $animationQuery[i].arrowHeadStyle.forEach((style, idx) => {
            const arrowElement = arrows[idx]; // 부모 요소를 선택
            if (arrowElement) {
                arrowElement.style.setProperty("--arrow-head-color", style.bgColor);
                
            }
        });


        // 화살표 애니메이션 실행 (삽입 위치 이후에는 실행하지 않음)
        if ($animationQuery[i].codeIndex === 1 && $animationQuery[i].explanation.includes("이동합니다")) {
            const arrowElement = arrows[$animationQuery[i].index];
            if (!arrowElement.isAnimating) {
                arrowElement.isAnimating = true;

                arrowElement.style.backgroundImage = "linear-gradient(to right, #28e02e 0%, #000000 100%)";
                arrowElement.style.transition = "background-image 0.5s ease";

                // 점진적 애니메이션
                let percentage = 0;
                const interval = setInterval(() => {
                    percentage += 10;
                    if (percentage > 100) {
                        clearInterval(interval);
                        arrowElement.style.backgroundImage = "none";
                        arrowElement.style.backgroundColor = "#28e02e";
                        arrowElement.isAnimating = false;
                    } else {
                        arrowElement.style.backgroundImage = `linear-gradient(to right, #28e02e ${percentage}%, #000000 100%)`;
                    }
                }, 50);
            }
        }

        await updateNodePositions(); // 화면 업데이트
        numNode = [...$animationQuery[i].currentNodeArray];

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
//**************************************************삽입 애니메이션 끝******************************************************************//    
//**************************************************검색 애니메이션******************************************************************//
    const startLinkedListSearch = async (e) => {
        InitAnimation();

        const targetValue = Number(e.detail.value.trim());

        generateLinkedListSearchQueries(targetValue);

        $animationWorking = true;
        $pausedIcon = false;
        $isPaused = false;
        
        executeLinkedListSearchAnimation($asyncCnt++);
    };



    // 애니메이션 단계 저장
    const pushAnimationSearchQuery = (index, nextIndex, explanationText, codeIndex, nodeStyles, arrowStyles) => {
        $animationQuery.push({
            index,
            nextIndex,
            explanation: explanationText,
            codeIndex,
            nodeStyles: [...nodeStyles],
            arrowStyles: [...arrowStyles]
        });
    };

    const generateLinkedListSearchQueries = (targetValue) => {
        // 색상 팔레트
        const NodeBg = { normal: "#ffffff", selected: "#ffffff", found: "#8aef8d", validating: "#c6ffc6", notFound: "#ffffff" };
        const NodeBorderColor = { normal: "#000000", selected: "#28e02e", found: "#28e02e", notFound: "#ff0000" };
        const NodeTextColor = { normal: "#000000", selected: "#28e02e", found: "#ffffff", validating: "#28e02e", notFound: "#28e02e" };
        const ArrowColor = { normal: "#000000", selected: "#28e02e" };

        $animationQuery = [];
        const nodeStyles = numNode.map(() => ({
            bgColor: NodeBg.normal,
            borderColor: NodeBorderColor.normal,
            textColor: NodeTextColor.normal // 텍스트 색상 기본값 추가
        }));
        const arrowStyles = numNode.map((_, idx) => ({
            bgColor: idx < numNode.length - 1 ? ArrowColor.normal : "transparent", // 마지막 노드 화살표는 투명
        }));

        let foundIndex = -1;

        for (let i = 0; i < numNode.length; i++) {
            const isLastNode = i === numNode.length - 1;

            // 검증 애니메이션 단계
            nodeStyles[i] = { 
                bgColor: NodeBg.validating, 
                borderColor: NodeBorderColor.selected, 
                textColor: NodeTextColor.selected // 텍스트 색상 설정
            };
            pushAnimationSearchQuery(
                i,
                -1,
                `노드 ${i}의 값(${numNode[i]})을(를) 검증 중입니다.`,
                0,
                [...nodeStyles],
                [...arrowStyles]
            );

            // 값이 맞지 않을 경우 원래 상태로 복귀
            if (numNode[i] !== targetValue) {
                
                nodeStyles[i] = { 
                    bgColor: NodeBg.normal, 
                    borderColor: NodeBorderColor.selected, 
                    textColor: NodeTextColor.selected 
                };
                pushAnimationSearchQuery(
                    i,
                    -1,
                    `노드 ${i}의 값(${numNode[i]})이 ${targetValue}과 다릅니다.`,
                    0,
                    [...nodeStyles],
                    [...arrowStyles]
                );
            }

            // 값이 일치하는 경우 찾음
            if (numNode[i] === targetValue) {
                foundIndex = i;
                nodeStyles[i] = { 
                    bgColor: NodeBg.found, 
                    borderColor: NodeBorderColor.found, 
                    textColor: NodeTextColor.found 
                };
                pushAnimationSearchQuery(i, -1, `값 ${targetValue}을(를) 찾았습니다!`, 2, [...nodeStyles], [...arrowStyles]);
                break;
            }

            // 찾는 값이 아닌 경우 화살표 이동
            if (!isLastNode && foundIndex === -1) {
                arrowStyles[i] = { bgColor: ArrowColor.selected };
                pushAnimationSearchQuery(
                    i,
                    i + 1,
                    `노드 ${i}에서 노드 ${i + 1}로 이동합니다.`,
                    1,
                    [...nodeStyles],
                    [...arrowStyles]
                );
            }
        }

        // 값을 찾지 못한 경우
        if (!$animationQuery.some((query) => query.explanation.includes("찾았습니다"))) {
            pushAnimationSearchQuery(
                numNode.length - 1,
                -1,
                `값 ${targetValue}을(를) 찾지 못했습니다.`,
                3,
                [...nodeStyles],
                [...arrowStyles]
            );
        }

        $animationStep = [0, $animationQuery.length - 1];
    };



    // 애니메이션 실행
    const executeLinkedListSearchAnimation = async (myAsync) => {
        $animationStep = [0, $animationQuery.length - 1];

        while (true) {
            if ((myAsync + 1) !== $asyncCnt) break;

            if ($animationStep[0] === $animationStep[1]) {
                $pausedIcon = true;
                $isPaused = true;
            }

            await drawLinkedListSearchAnimation($animationStep[0]);
            await waitPause();

            if (!$fromBtn) {
                $animationStep[0] = Math.min($animationStep[0] + 1, $animationStep[1]);
            }
        }
    };
    // 각 단계의 애니메이션 렌더링
    const drawLinkedListSearchAnimation = async (i) => {
        const nodes = document.querySelectorAll(".linked-list-node");
        const arrows = document.querySelectorAll(".node-arrow");

        $explanation = $animationQuery[i].explanation;

        // 노드 상태 복원
        $animationQuery[i].nodeStyles.forEach((style, idx) => {
            const nodeElement = nodes[idx];
            nodeElement.style.transition = "border-color 0.3s ease, background-color 0.3s ease";
            nodeElement.style.borderColor = style.borderColor;
            nodeElement.style.backgroundColor = style.bgColor;
            nodeElement.style.color = style.textColor;
        });

        // 화살표 상태 복원
        $animationQuery[i].arrowStyles.forEach((style, idx) => {
            const arrowElement = arrows[idx];
            arrowElement.style.transition = "background-color 0.3s ease";
            arrowElement.style.backgroundColor = style.bgColor;
        });

        // 화살표 애니메이션 실행 (찾는 값 이후에는 실행하지 않음)
        if ($animationQuery[i].codeIndex === 1 && $animationQuery[i].explanation.includes("이동합니다")) {
            const arrowElement = arrows[$animationQuery[i].index];
            if (!arrowElement.isAnimating) {
                arrowElement.isAnimating = true;

                arrowElement.style.backgroundImage = "linear-gradient(to right, #28e02e 0%, #000000 100%)";
                arrowElement.style.transition = "background-image 0.5s ease";

                // 점진적 애니메이션
                let percentage = 0;
                const interval = setInterval(() => {
                    percentage += 10;
                    if (percentage > 100) {
                        clearInterval(interval);
                        arrowElement.style.backgroundImage = "none";
                        arrowElement.style.backgroundColor = "#28e02e";
                        arrowElement.isAnimating = false;
                    } else {
                        arrowElement.style.backgroundImage = `linear-gradient(to right, #28e02e ${percentage}%, #000000 100%)`;
                    }
                }, 50);
            }
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

//**************************************************검색 애니메이션 끝******************************************************************//


    const createRandomNode = (e) => { 
        InitAnimation();

        const NodeCnt = e.detail.NodeCnt;

        numNode = [];  

        for (let i = 0; i < NodeCnt; i++) {
            numNode.push(Math.floor(Math.random() * 199) - 99);
        }

        removeLastArrow();
        updateNodePositions();
    };

    const createInputtedNode = (e) => {
        InitAnimation();

        const tmpNode = e.detail.tmpNode;
        numNode = tmpNode;

        removeLastArrow();
        updateNodePositions();
    };

</script>

<main>
    <div class="navigation-container">
        <LinkedListNavigation 
        {nodePositions} {numNode}
        bind:selectNode
        on:createRandomNode={createRandomNode} 
        on:createInputtedNode={createInputtedNode} 
        on:startLinkedListSearch={startLinkedListSearch}
        on:startLinkedListInsert={startLinkedListInsert}
        on:updateSelectNode={(e) => handleSelectNode(e.detail.selectNode)}/>
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
                연결리스트(LinkedList)
            </div>

            <div class="canvas">
                <!-- canvas안에 자료구조, 알고리즘 구현 -->

                {#each numNode as node, index}
                    <div class="linked-list-node-container" 
                        style="transform: translateX({offsetX + index * 95}px);">

                        <div class="linked-list-node">
                            <div class="node-value">{node}</div>
                        </div>

                        {#if index < numNode.length - 1}
                            <div class="node-arrow"></div>
                        {:else}
                            <div class="node-arrow white-arrow"></div>
                            <div class="white-arrow white-node-arrow::after"></div>
                        {/if}

                        {#if index === 0}
                        <div class="head-label">head[0]</div>
                        {/if}

                        {#if index === numNode.length - 2}
                        <div class="tail-label">tail[{numNode.length - 2}]</div>
                        {/if}

                    
                    </div>
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

    .linked-list-node-container {
        display: flex;
        align-items: center;
        position: absolute;
        transition: transform 0.5s ease;
    }

    .linked-list-node {
        width: 50px;
        height: 50px;
        display: flex;
        align-items: center;
        justify-content: center;
        background-color: #ffffff; 
        color: #000000;
        border-radius: 10px; 
        border: 3px solid black;
        font-size: 1rem;
        font-weight: bold;
        text-align: center;
    }

    .node-arrow {
        width: 30px;
        height: 2px;
        background-color: #000000; 
        position: relative;
        margin-right: 5px;
    }
    
    .node-arrow::after {
        content: '';
        position: absolute;
        top: -3.1px;
        right: -10px;
        border-width: 5px;
        border-style: solid;
        border-color: transparent transparent transparent var(--arrow-head-color, #000000);
        transition: border-color 0.3s ease, opacity 0.3s ease;
    }

    .white-arrow {
        background-color: #ffffff;
    }

    .white-arrow::after {
        border-color: transparent transparent transparent #ffffff;
    }

    .head-label, .tail-label {
        position: absolute;
        top: 62px; /* 노드 아래에 위치 */
        font-size: 1rem;
        font-weight: bold;
        color: #000000;
        background-color: #ffffff;
        color: #009400;
        z-index: 10;
    }

    .head-label {
        left: 32%;
        transform: translateX(-50%);
    }

    .tail-label {
        left: 32%;
        transform: translateX(-50%);
    }



</style>