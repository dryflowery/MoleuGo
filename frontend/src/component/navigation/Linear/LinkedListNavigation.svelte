<!-- 좌클릭으로 drag, 우클릭으로 active  -->
<!-- active 상태에서는 #drag(손 아이콘)만 drag 가능  -->
<script>
    import { createEventDispatcher } from 'svelte';
    import { fly } from 'svelte/transition';
    import { animationWorking } from '../../../lib/visualizationStore';
    
    const dispatch = createEventDispatcher();

    export let nodePositions = []; // LinkedList에서 전달받은 노드 위치 데이터
    export let selectNode = null; 
    export let numNode = [];

    let tooltip = Array(9).fill(false);
    let toggle = Array(9).fill(false);
    let isActive = false; 
    let isDragging = false; 
    let offsetX = 0; 
    let offsetY = 0; 
    let navigationPos = { top: "700px", left: "1300px" }; 
    let arrowVisibility = { north: false, south: false }; // 화살표 표시 상태

    let NodeCnt = 5;
    let inputtedNode = '';
    let tmpNode = [];

    let searchNodeValue = '';
    let inputNodeValue = '';

    

    const updateArrowVisibility = () => {
        const windowHeight = window.innerHeight; // 화면 전체 높이
        const windowCenterY = windowHeight / 2; // 화면 중앙 Y 좌표
        const navHeight = 125; // 네비게이션 높이
        const navTop = parseFloat(navigationPos.top); // 네비게이션의 상단 위치
        const navCenterY = navTop + navHeight / 2; // 네비게이션 중앙 Y 좌표

        const navY = parseFloat(navigationPos.top); // 네비게이션의 Y 좌표
        const navX = parseFloat(navigationPos.left); // 네비게이션의 X 좌표

        selectNode = null; // 초기화

        // 초기값: 모든 화살표 숨기기
        arrowVisibility = { north: false, south: false };

        // 네비게이션 위치를 기준으로 가시성 업데이트
        if (navCenterY < windowCenterY) {
            arrowVisibility.south = true;
        } else {
            arrowVisibility.north = true;
        }

        // 애니메이션 실행 중일 때는 업데이트하지 않음
        if ($animationWorking) {
            return;
        }

        // 노드 탐지 및 selectNode 업데이트
        nodePositions.forEach((node, index) => {
            const dx = Math.abs(node.x - navX);
            const dy = Math.abs(node.y - navY);

            if (dx < 50 && dy < 200) {
                
                selectNode = index; // 가리키는 노드의 인덱스 저장
            }
        });

        // 선택된 노드 정보를 부모에 전달
        dispatch('updateSelectNode', { selectNode });
    };


    const toggleNavigation = (e) => {
        toggle = Array(9).fill(false);
        isActive = !isActive;
        e.preventDefault(); // 기본 우클릭 메뉴 나타나지 않게 설정
    };
    

    const startDrag = (e) => {
        const dragElement = e.target.closest("#drag"); 
        if (e.button !== 0 || (isActive && !dragElement)) {
            return;
        }

        isDragging = true;
        const nav = e.target.closest(".navigation-container");
        const rect = nav.getBoundingClientRect();   

        offsetX = e.clientX - rect.left;
        offsetY = e.clientY - rect.top;

        document.addEventListener("pointermove", onDrag);
        document.addEventListener("pointerup", stopDrag);
    };

    const onDrag = (e) => {
        if (!isDragging) return;

        requestAnimationFrame(() => {
            const navWidth = 125; 
            const navHeight = 125; 

            const minX = 0;
            const minY = 0;
            const maxX = window.innerWidth - navWidth;
            const maxY = window.innerHeight - navHeight;

            let newLeft = e.clientX - offsetX;
            let newTop = e.clientY - offsetY;

            newLeft = Math.max(minX, Math.min(newLeft, maxX));
            newTop = Math.max(minY, Math.min(newTop, maxY));

            navigationPos.top = `${newTop}px`;
            navigationPos.left = `${newLeft}px`;
        });
        updateArrowVisibility();
    };


    const stopDrag = () => {
        isDragging = false;
        updateArrowVisibility();
        document.removeEventListener("pointermove", onDrag);
        document.removeEventListener("pointerup", stopDrag);
    };

    const changeToggle = (idx) => {
        if(!isActive) {
            return;
        }
        
        toggle = toggle.map((_, i) => i === idx ? !toggle[i] : false);
    };

    // createRandomNode의 범위 체크 (1 ~ 15)
    const validNodeCntRange = (e) => {
        if (NodeCnt > 10) {
            NodeCnt = 10;
            alert("10 이하의 숫자를 입력해주세요");
            return false;
        } else if (NodeCnt < 2) {
            NodeCnt = 2;
            alert("2 이상의 숫자를 입력해주세요");
            return false;
        } else {
            NodeCnt = NodeCnt;
            return true;
        }
    };

    // searchNodeValue 의 유효함 체크
    const vaildSerchNodeValue = () => {
        const num = Number(searchNodeValue.trim());

        if (isNaN(num)) {
            alert("유효하지 않은 값입니다. 숫자를 입력해주세요.");
            return false;
        }

        if (num < -99 || num > 99) {
            alert("-99 이상, 99 이하의 숫자를 입력해주세요.");
            return false;
        }

        return true;
    }

    const validateInsertInputs = () => {
        // selectNode 검증
        if (selectNode === null || selectNode <= -1 || selectNode >= numNode.length + 1) {
            alert(`유효한 인덱스를 선택해주세요. (0 ~ ${numNode.length})`);
            return false;
        }

        // inputNodeValue 검증
        const value = Number(inputNodeValue.trim());
        if (isNaN(value) || value < -99 || value > 99) {
            alert("삽입할 값은 -99 이상, 99 이하의 숫자여야 합니다.");
            return false;
        }

        return true;
    };

    const validateDeleteInputs = () => {
        if (selectNode === null || selectNode < 0 || selectNode >= numNode.length) {
            alert(`삭제할 노드의 유효한 인덱스를 선택해주세요. (0 ~ ${numNode.length - 1})`);
            return false;
        }
        return true;
    };

    
    // createInputtedNode의 유효함 체크
    const validInputtedNode = (e) => {
        const elements = inputtedNode.split(',').map(num => num.trim());
        tmpNode = [];

        // 입력된 원소의 개수가 20개 이하인지 확인
        if (elements.length > 10) {
            alert("10개 이하의 원소를 입력해주세요");
            return false;
        }

        if(elements.length < 2) {
            alert("2개 이상의 원소를 입력해주세요");
            return false;
        }

        const isValid = elements.every(el => {
            const num = Number(el);
            return !isNaN(num) && num >= -99 && num <= 99 && el !== '';
        });

        if (isValid) {
            elements.forEach(el => {
                tmpNode.push(Number(el));
            });
        } else if (elements.some(el => isNaN(Number(el)) || el === '')) {
            alert("유효하지 않은 문자가 있습니다");
            return false;
        } else {
            alert("-99이상, 99이하의 원소를 입력해주세요");
            return false;
        }
        return true;
    };

    const createInputtedNode = (e) => {
        if(!isActive || !validInputtedNode(e)) {
            return;
        }
        dispatch('createInputtedNode', {tmpNode});
    };

    const createRandomNode = (e) => {
        if(!isActive || !validNodeCntRange(e)) {
            return;
        }

        dispatch('createRandomNode', {NodeCnt});
    };

    // ************************************************************
    const startLinkedListSearch = () => { // 검색
        if(!isActive || $animationWorking) {
            return;
        }

        if (!vaildSerchNodeValue()) {
            return;
        }

        toggle = Array(9).fill(false);
        isActive = false;
        dispatch('startLinkedListSearch', { value: searchNodeValue });
    }

    const startLinkedListInsert = () => { // 삽입

        if(!isActive || $animationWorking) {
            return;
        }

        if (!validateInsertInputs()) {
            return;
        }

        alert('삽입은 현재 오류로 인해 수정중 입니다. 검색을 이용해주세요');
            return;

        toggle = Array(9).fill(false);
        isActive = false;
        dispatch('startLinkedListInsert', { indexValue: selectNode, nodeValue: inputNodeValue });
    }

    const startLinkedListDelete = () => {
        if(!isActive || $animationWorking) {
            return;
        }

        if(!validateDeleteInputs()) {
            return;
        }

        alert('삭제는 현재 오류로 인해 수정중 입니다. 검색을 이용해주세요');
        return;

        toggle = Array(9).fill(false);
        isActive = false;
        dispatch('startLinkedListDelete', {indexValue: selectNode});
    }

    // ************************************************************

    $: {
        updateArrowVisibility();
    }

</script>

<main>
    <div class="navigation-container" on:pointerdown={startDrag}
    style="top: {navigationPos.top}; left: {navigationPos.left}; position: absolute; cursor: pointer;">

        <div class="arrow north" class:visible={arrowVisibility.north} class:active={isActive}>
            <ion-icon name="arrow-up-outline" size="large"></ion-icon>
        </div>
        
        <div class="arrow south" class:visible={arrowVisibility.south} class:active={isActive}>
            <ion-icon name="arrow-down-outline" size="large"></ion-icon>
        </div>

        <!-- 툴팁 표시 -->
        <div class="navigation-tooltip">
            {#if isActive}
                {#if tooltip[0]}
                    <span>추가</span>
                {:else if tooltip[1]}
                    <span>삭제</span>
                {:else if tooltip[2]}
                    <span>찾기</span>      
                {:else if tooltip[8]}
                    <span></span>
                {:else if tooltip[5]}
                <span>노드 랜덤 생성</span>
                {:else if tooltip[6]}
                <span>노드 직접 생성</span>
                {/if}
            {/if}
        </div>

        <!-- 토글 표시 -->
        <div class="navigation-container">
            {#if isActive}
                {#if toggle[0]}
                    <!-- 추가 -->
                    <div class="navigation-toggle" transition:fly={{ x: -45, duration: 500 }}>
                        <span class='txt'>i</span> <span class='txt'>=</span>
                        <input type="text" id="element-input" size="10" bind:value={selectNode} style="width: 30px;">
                        <span class='txt'>v</span> <span class='txt'>=</span>
                        <input type="text" id="element-input" size="10" bind:value={inputNodeValue}>
                        <button style="white-space: nowrap;"on:click={startLinkedListInsert}>추가</button>
                    </div>
                {:else if toggle[1]}
                <!-- 삭제 -->
                    <div class="navigation-toggle" transition:fly={{ x: -45, duration: 500 }}>
                        <span class='txt'>i</span> <span class='txt'>=</span>
                        <input type="text" id="element-input" size="10" bind:value={selectNode}>
                        <button style="white-space: nowrap;"on:click={startLinkedListDelete}>삭제</button>
                    </div>
                {:else if toggle[2]}
                <!-- 찾기 -->
                    <div class="navigation-toggle" transition:fly={{ x: -45, duration: 500 }}>
                        <span class='txt'>v</span> <span class='txt'>=</span>
                        <input type="text" id="element-input" size="10" bind:value={searchNodeValue}>
                        <button style="white-space: nowrap;" on:click={startLinkedListSearch}>찾기</button>
                    </div>
                {:else if toggle[5]}
                    <!-- 원소 랜덤 생성 -->
                    <div class="navigation-toggle" style="margin-top:120px;" transition:fly={{ x: -45, duration: 500 }}>
                        <span class='txt'>N</span> <span class='txt'>=</span>
                        <input type="number" id="element-cnt-input" min="2" max="10" bind:value={NodeCnt}>
                        <button style="white-space: nowrap;" on:click={createRandomNode}>생성</button>
                    </div>
                    
                {:else if toggle[6]}
                    <!-- 원소 직접 생성 -->
                    <div class="navigation-toggle" style="margin-top:120px;" transition:fly={{ x: -45, duration: 500 }}>
                        <span class='txt'>A</span> <span class='txt'>=</span>
                        <input type="text" id="element-input" size="10" bind:value={inputtedNode}>
                        <button style="white-space: nowrap;" on:click={createInputtedNode}>생성</button>
                    </div>
                {/if}
            {/if}
        </div>

        <!-- svelte-ignore a11y-no-static-element-interactions -->
        <!-- svelte-ignore a11y-click-events-have-key-events -->
        <div class="navigation-menu"  on:contextmenu={toggleNavigation}>
            <div class="navigation" class:active={isActive}>
                <!-- 0번 (추가) -->
                <span style="--i:0; --x:-1; --y:-1">
                    <ion-icon name="add-circle-outline" on:click={() => changeToggle(0)} 
                        on:mouseenter={() => tooltip[0] = true} on:mouseleave={() => tooltip[0] = false}></ion-icon>
                </span>

                <!-- 1번 (삭제) -->
                <span style="--i:1; --x:0; --y:-1"> 
                    <ion-icon name="remove-circle-outline" on:click={() => changeToggle(1)} 
                        on:mouseenter={() => tooltip[1] = true} on:mouseleave={() => tooltip[1] = false}></ion-icon>
                </span>

                <!-- 2번 (찾기) -->
                <span style="--i:2; --x:1; --y:-1" on:click={() => changeToggle(2)} 
                    on:mouseenter={() => tooltip[2] = true} on:mouseleave={() => tooltip[2] = false}>
                    <ion-icon name="search-outline"></ion-icon>
                </span>

                <!-- 3번 -->
                <span style="--i:3; --x:1; --y:0">
                 
                </span>

                <!-- 4번 (active시 잡고 이동하는 용도) -->
                <span style="--i:4; --x:1; --y:1" id="drag">
                    <ion-icon name="hand-left-outline"></ion-icon> 
                </span>

                <!-- 5번 -->
                <span style="--i:5; --x:0; --y:1">
                    <ion-icon name="shuffle-outline" on:click={() => changeToggle(5)} 
                        on:mouseenter={() => tooltip[5] = true} on:mouseleave={() => tooltip[5] = false}>
                    </ion-icon>
                </span>

                <!-- 6번 -->
                <span style="--i:6; --x:-1; --y:1">
                    <ion-icon name="duplicate-outline" on:click={() => changeToggle(6)} 
                        on:mouseenter={() => tooltip[6] = true} on:mouseleave={() => tooltip[6] = false}>
                    </ion-icon>
                </span>
                
                <!-- 7번 -->
                <span style="--i:7; --x:-1; --y:0">
                   
                </span>           
            </div>
            
            <!-- 8번 (알고리즘 실행) -->
            <div class="center">
                
            </div>
        </div>
    </div>
</main>

<style>

    .arrow {
        position: absolute;
        width: 20px;
        height: 20px;
        opacity: 0;
        transition: transform 0.5s ease, opacity 0.2s ease;

    }
    .arrow.visible {
        opacity: 1;
    }

    .north {
        top: -40px;
        left: 40%;
        transform: translateX(-50%);
    }
    .south {
        bottom: -25px;
        left: 38%;
        transform: translateX(-50%);
    }

    .arrow.active.north {
        transform: translate(-50%, -80px); /* 북쪽 화살표 위로 이동 */

    }

    .arrow.active.south {
        transform: translate(-50%, 60px); /* 남쪽 화살표 아래로 이동 */

    }


</style>