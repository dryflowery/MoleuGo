<!-- 좌클릭으로 drag, 우클릭으로 active  -->
<!-- active 상태에서는 #drag(손 아이콘)만 drag 가능  -->
<script>
    import { createEventDispatcher } from 'svelte';
    import { fly } from 'svelte/transition';
    import { animationWorking, animationStep, isPaused } from '../../../lib/visualizationStore';
    
    const dispatch = createEventDispatcher();

    export let numArr = [];

    let tooltip = Array(9).fill(false);
    let toggle = Array(9).fill(false);
    let isActive = false; 
    let isDragging = false; 
    let offsetX = 0; 
    let offsetY = 0; 
    let navigationPos = { top: "700px", left: "1300px" }; 

    let pushValue = '';

    let arrCnt = 7;
    let inputtedNode = '';
    let tmpArr = [];



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
        
    };


    const stopDrag = () => {
        isDragging = false;
        
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
        if (arrCnt > 12) {
            arrCnt = 12;
            alert("12 이하의 숫자를 입력해주세요");
            return false;
        } else if (arrCnt < 1) {
            arrCnt = 1;
            alert("1 이상의 숫자를 입력해주세요");
            return false;
        } else {
            arrCnt = arrCnt;
            return true;
        }
    };

    // ********************************************* 큐 삽입 값 검증 *******************************************************
    const vaildEnqueueValue = () => {
        const num = Number(pushValue.trim());

        if (numArr.length >= 12) {
            alert("최대 길이 도달입니다.");
            return false;
        }

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
    

    
    // ********************************************* createInputtedNode의 유효함 체크 *******************************************************
    const validInputtedNode = (e) => {
        const elements = inputtedNode.split(',').map(num => num.trim());
        tmpArr = [];

        // 입력된 원소의 개수가 20개 이하인지 확인
        if (elements.length > 12) {
            alert("12개 이하의 원소를 입력해주세요");
            return false;
        }

        if(elements.length < 1) {
            alert("1개 이상의 원소를 입력해주세요");
            return false;
        }

        const isValid = elements.every(el => {
            const num = Number(el);
            return !isNaN(num) && num >= -99 && num <= 99 && el !== '';
        });

        if (isValid) {
            elements.forEach(el => {
                tmpArr.push(Number(el));
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

    const createInputtedArr = (e) => { // ************************************ [직접 배열 넣기]
        if(!isActive || !validInputtedNode(e)) {
            return;
        }
        
        dispatch('createInputtedArr', {tmpArr});
    };

    const createRandomArr = (e) => { // ************************************ [랜덤 배열 넣기]
        if(!isActive || !validNodeCntRange(e)) {
            return;
        }

        dispatch('createRandomArr', {arrCnt});
    };

    const startEnqueue = (direction) => { // ******************************************* [Enqueue]

        if(!isActive || !$isPaused ) {
            alert("애니메이션 실행 중 입니다.");
            return; // 애니메이션 실행 도중 삽입 불가능
        }


        if($animationWorking && $animationStep[0] !== $animationStep[1]) {
            alert("마지막 단계에서만 삽입 가능합니다.");
            return; // 마지막 쿼리에서만 삽입 가능
        }

        if(!vaildEnqueueValue()) {
            return; // 삽입 값 검사 
        }

        toggle = Array(9).fill(false);
        isActive = false;

        dispatch('startEnqueue', { value: pushValue, direction });
    }

    const startDequeue = (direction) => { // ******************************************* [Dequeue]

        if(!isActive || !$isPaused ) {
            alert("애니메이션 실행 중 입니다.");
            return; // 애니메이션 실행 도중 삭제 불가능
        }


        if($animationWorking && $animationStep[0] !== $animationStep[1]) {
            alert("마지막 단계에서만 삽입 가능합니다.");
            return; // 마지막 쿼리에서만 삭제 가능
        }


        toggle = Array(9).fill(false);
        isActive = false;

        dispatch('startDequeue', { direction } );
    }

    const startPeek = (direction) => { // ******************************************* [Peek]

        if(!isActive || !$isPaused ) {
            alert("애니메이션 실행 중 입니다.");
            return; // 애니메이션 실행 도중 삭제 불가능
        }


        if($animationWorking && $animationStep[0] !== $animationStep[1]) {
            alert("마지막 단계에서만 삽입 가능합니다.");
            return; // 마지막 쿼리에서만 삭제 가능
        }


        toggle = Array(9).fill(false);
        isActive = false;

        dispatch('startPeek', { direction });
    }


</script>

<main>
    <div class="navigation-container" on:pointerdown={startDrag}
    style="top: {navigationPos.top}; left: {navigationPos.left}; position: absolute; cursor: pointer;">

        <!-- 툴팁 표시 -->
        <div class="navigation-tooltip">
            {#if isActive}
                {#if tooltip[0]}
                    <span>원소 삽입</span>
                {:else if tooltip[1]}
                    <span>원소 삭제</span>
                {:else if tooltip[2]}
                    <span>최상단 원소 조회</span>      
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
                    <!-- Push() -->
                    <div class="navigation-toggle" transition:fly={{ x: -45, duration: 500 }}>
                        <span class='txt'>v</span> <span class='txt'>=</span>
                        <input type="text" id="element-input" size="10" bind:value={pushValue} >
                        <button style="white-space: nowrap;"on:click={() => startEnqueue('Front')}>Front</button>
                        <button style="white-space: nowrap;"on:click={() => startEnqueue('Back')}>Back</button>
                    </div>
                {:else if toggle[1]}
                    <!-- Pop() -->
                    <div class="navigation-toggle" transition:fly={{ x: -45, duration: 500 }}>

                        <button style="white-space: nowrap;" on:click={() => startDequeue('Front')}>
                            Front
                        </button>

                        <button style="white-space: nowrap;" on:click={() => startDequeue('Back')}>
                            Back
                        </button>

                    </div>
                     
                {:else if toggle[2]}
                    <!-- Peek() -->
                    <div class="navigation-toggle" transition:fly={{ x: -45, duration: 500 }}>

                        <button style="white-space: nowrap;" on:click={() => startPeek('Front')}>
                            Front
                        </button>

                        <button style="white-space: nowrap;" on:click={() => startPeek('Back')}>
                            Back
                        </button>

                    </div>
                {:else if toggle[5]}
                    <!-- 원소 랜덤 생성 -->
                    <div class="navigation-toggle" style="margin-top:120px;" transition:fly={{ x: -45, duration: 500 }}>
                        <span class='txt'>N</span> <span class='txt'>=</span>
                        <input type="number" id="element-cnt-input" min="1" max="12" bind:value={arrCnt}>
                        <button style="white-space: nowrap;" on:click={createRandomArr}>생성</button>
                    </div>
                    
                {:else if toggle[6]}
                    <!-- 원소 직접 생성 -->
                    <div class="navigation-toggle" style="margin-top:120px;" transition:fly={{ x: -45, duration: 500 }}>
                        <span class='txt'>A</span> <span class='txt'>=</span>
                        <input type="text" id="element-input" size="10" bind:value={inputtedNode}>
                        <button style="white-space: nowrap;" on:click={createInputtedArr}>생성</button>
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

                <!-- 2번 (맨위) -->
                <span style="--i:2; --x:1; --y:-1">
                    <ion-icon name="golf-outline" on:click={() => changeToggle(2)} 
                    on:mouseenter={() => tooltip[2] = true} on:mouseleave={() => tooltip[2] = false}></ion-icon>
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



</style>