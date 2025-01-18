<!-- 좌클릭으로 drag, 우클릭으로 active  -->
<!-- active 상태에서는 #drag(손 아이콘)만 drag 가능  -->
<script>
    import { createEventDispatcher } from 'svelte';
    import { fly } from 'svelte/transition';
    import { animationWorking, isPaused, pausedIcon } from '../../../lib/visualizationStore';
    const dispatch = createEventDispatcher();

    let tooltip = Array(9).fill(false);
    let toggle = Array(9).fill(false);
    let isActive = false; 
    let isDragging = false; 
    let offsetX = 0; 
    let offsetY = 0; 
    let navigationPos = { top: "700px", left: "1300px" }; 

    let elementCnt = 1;
    let insertNum = 0;
    let inputtedElement = ''
    let tmpHeap = [];

    const toggleNavigation = (e) => {
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

    // createInputtedElement의 유효함 체크
    const validInputtedElement = (e) => {
        const elements = inputtedElement.split(',').map(num => num.trim());
        tmpHeap = [];

        // 입력된 원소의 개수가 20개 이하인지 확인
        if (elements.length > 15) {
            alert("15개 이하의 원소를 입력해주세요");
            return false;
        }

        if(elements.length < 1) {
            alert("1개 이상의 원소를 입력해주세요");
            return false;
        }

        const isValid = elements.every(el => {
            const num = Number(el);
            return !isNaN(num) && num >= 1 && num <= 99 && Number.isInteger(num) && el !== '';
        });

        if (isValid) {
            elements.forEach(el => {
                tmpHeap.push(Number(el));
            });
        } else if (elements.some(el => isNaN(Number(el)) || el === '')) {
            alert("유효하지 않은 문자가 있습니다");
            return false;
        } else {
            alert("1 이상, 99 이하의 정수를 입력해주세요");
            return false;
        }

        return true;
    };

    // createRandomElements의 범위 체크 (1 ~ 15)
    const validElementCntRange = (e) => {
        if (!Number.isInteger(elementCnt)) {
            alert("정수를 입력해주세요");
            return false;
        }
        else if (elementCnt > 15) {
            elementCnt = 15;
            alert("15 이하의 숫자를 입력해주세요");
            return false;
        } else if (elementCnt < 1) {
            elementCnt = 1;
            alert("1 이상의 숫자를 입력해주세요");
            return false;
        } else {
            elementCnt = elementCnt;
            return true;
        }
    };

    const validInsertRange = (e) => {
        if (insertNum < 0) {
            insertNum = 0;
            alert("0 이상의 숫자를 입력해주세요");
            return false;
        } else if (insertNum > 99) {
            insertNum = 99;
            alert("99 이하의 숫자를 입력해주세요");
            return false;
        } else {
            insertNum = insertNum;
            return true;
        }
    };

    const createInputtedElement = (e) => {
        toggle = Array(9).fill(false);

        if(($animationWorking && !$isPaused && !$pausedIcon) || !isActive || !validInputtedElement(e)) {
            return;
        }

        isActive = false;
        dispatch('createInputtedElement', {tmpHeap});
    }

    const createRandomElement = (e) => {
        toggle = Array(9).fill(false);

        if(!isActive || !validElementCntRange(e) || ($animationWorking && !$isPaused && !$pausedIcon)) {
            return;
        }

        isActive = false;
        dispatch('createRandomElement', {elementCnt});
    };

    const extractMax = (e) => {
        if(!isActive || ($animationWorking && !$isPaused && !$pausedIcon)) {
            return;
        }

        isActive = false;
        dispatch('extractMax');
    };

    const insertElement = (e) => {
        toggle = Array(9).fill(false);

        if(!isActive || !validInsertRange(e) || ($animationWorking && !$isPaused && !$pausedIcon)) {
            return;
        }

        isActive = false;
        dispatch('insertElement', {insertNum});
    };
</script>

<main>
    <div class="navigation-container" on:pointerdown={startDrag}
    style="top: {navigationPos.top}; left: {navigationPos.left}; position: absolute; cursor: pointer;">
        <!-- 툴팁 표시 -->
        <div class="navigation-tooltip">
            {#if isActive}
                {#if tooltip[0]}
                    <span>힙 직접 생성</span>
                {:else if tooltip[1]}
                    <span>힙 랜덤 생성</span>
                {:else if tooltip[2]}
                    <span>최댓값 반환</span>
                {:else if tooltip[7]}
                    <span>원소 삽입</span>
                {/if}
            {/if}
        </div>

        <!-- 토글 표시 -->
        <div class="navigation-container">
            {#if isActive}
                {#if toggle[0]}
                    <!-- 원소 직접 생성 -->
                    <div class="navigation-toggle" transition:fly={{ x: -45, duration: 500 }}>
                        <span class='txt'>A</span> <span class='txt'>=</span>
                        <input type="text" id="element-input" size="10" bind:value={inputtedElement}>
                        <button style="white-space: nowrap;" on:click={createInputtedElement}>생성</button>
                    </div>
                {:else if toggle[1]}
                    <!-- 원소 랜덤 생성 -->
                    <div class="navigation-toggle" transition:fly={{ x: -45, duration: 500 }}>
                        <span class='txt'>N</span> <span class='txt'>=</span>
                        <input type="number" id="element-cnt-input" min="1" max="15" bind:value={elementCnt}>
                        <button style="white-space: nowrap;" on:click={createRandomElement}>생성</button>
                    </div>
                {:else if toggle[7]}
                    <!-- 원소 삽입 -->
                    <div class="navigation-toggle" transition:fly={{ x: -45, duration: 500 }}>
                        <span class='txt'>num</span> <span class='txt'>=</span>
                        <input type="number" id="num-input" min="0" max="99" bind:value={insertNum}>
                        <button style="white-space: nowrap;" on:click={insertElement}>생성</button>
                    </div>
                {/if}
            {/if}
        </div>

        <!-- svelte-ignore a11y-no-static-element-interactions -->
        <!-- svelte-ignore a11y-click-events-have-key-events -->
        <div class="navigation-menu"  on:contextmenu={toggleNavigation}>
            <div class="navigation" class:active={isActive}>
                <!-- 0번 (원소 직접 생성) -->
                <span style="--i:0; --x:-1; --y:-1">
                    <ion-icon name="duplicate-outline" on:click={() => changeToggle(0)} 
                    on:mouseenter={() => tooltip[0] = true} on:mouseleave={() => tooltip[0] = false}></ion-icon>
                </span>

                <!-- 1번 (원소 랜덤 생성) -->
                <span style="--i:1; --x:0; --y:-1"> 
                    <ion-icon name="shuffle-outline" on:click={() => changeToggle(1)} 
                    on:mouseenter={() => tooltip[1] = true} on:mouseleave={() => tooltip[1] = false}></ion-icon>
                </span>

                <!-- 2번 (최댓값 반환)-->
                <span style="--i:2; --x:1; --y:-1">
                    <ion-icon name="reload-outline" on:click={extractMax} 
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
                  
                </span>

                <!-- 6번 -->
                <span style="--i:6; --x:-1; --y:1">
              
                </span>
                
                <!-- 7번 (원소 삽입)-->
                <span style="--i:7; --x:-1; --y:0">
                    <ion-icon name="add-outline" on:click={() => changeToggle(7)} 
                    on:mouseenter={() => tooltip[7] = true} on:mouseleave={() => tooltip[7] = false}></ion-icon>
                </span>           
            </div>
            
            <!-- 8번 (알고리즘 실행) -->
            <div class="center">
                <ion-icon name="play-outline"
                on:mouseenter={() => tooltip[8] = true} on:mouseleave={() => tooltip[8] = false}></ion-icon>
            </div>
        </div>
    </div>
</main>