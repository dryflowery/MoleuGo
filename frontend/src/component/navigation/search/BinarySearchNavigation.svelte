<!-- 좌클릭으로 drag, 우클릭으로 active  -->
<!-- active 상태에서는 #drag(손 아이콘)만 drag 가능  -->
<script>
    import { createEventDispatcher } from 'svelte';
    import { fly } from 'svelte/transition';
    import { animationWorking } from '../../../lib/visualizationStore';
    const dispatch = createEventDispatcher();

    export let arr;

    let tooltip = Array(9).fill(false);
    let toggle = Array(9).fill(false);
    let isActive = false;
    let isDragging = false;
    let offsetX = 0;
    let offsetY = 0;
    let navigationPos = { top: "700px", left: "1300px" };

    let elementCnt = 5;
    let inputtedElement = ''
    let key;
    let tmpArr = [];

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

    // createRandomElements의 범위 체크 (3 ~ 20)
    const validElementCntRange = (e) => {
        if (!Number.isInteger(elementCnt)) {
            alert("정수를 입력해주세요");
            return false;
        }
        else if (elementCnt > 20) {
            elementCnt = 20;
            alert("20 이하의 숫자를 입력해주세요");
            return false;
        } else if (elementCnt < 3) {
            elementCnt = 3;
            alert("3 이상의 숫자를 입력해주세요");
            return false;
        } else {
            elementCnt = elementCnt;
            return true;
        }
    };

    // createInputtedElement의 유효함 체크
    const validInputtedElement = (e) => {
        const elements = inputtedElement.split(',').map(num => num.trim());
        tmpArr = [];

        // 입력된 원소의 개수가 20개 이하인지 확인
        if (elements.length > 20) {
            alert("20개 이하의 원소를 입력해주세요");
            return false;
        }

        if(elements.length < 3) {
            alert("3개 이상의 원소를 입력해주세요");
            return false;
        }

        const isValid = elements.every(el => {
            const num = Number(el);
            return !isNaN(num) && 1 <= num && num <= 99 && Number.isInteger(num) && el !== '';
        });

        if (isValid) {
            elements.forEach(el => {
                tmpArr.push(Number(el));
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

    const isValidKey = (e) => {
        if(1 <= key && key <= 99) {
            return true;
        }
        else {
            alert("1 이상, 99 이하의 정수를 입력해주세요")
            return false;
        }
    }

    const createInputtedElement = (e) => {
        if(!isActive || !validInputtedElement(e)) {
            return;
        }

        dispatch('createInputtedElement', {tmpArr});
    }

    const createRandomElement = (e) => {
        if(!isActive || !validElementCntRange(e)) {
            return;
        }

        dispatch('createRandomElement', {elementCnt});
    };

    const startBinarySearch = (e) => {
        if(!isActive || !isValidKey(e) || $animationWorking) {
            return;
        }

        toggle = Array(9).fill(false);
        isActive = false;
        dispatch('startBinarySearch', {key});
    }
</script>

<main>
    <div class="navigation-container" on:pointerdown={startDrag}
         style="top: {navigationPos.top}; left: {navigationPos.left}; position: absolute; cursor: pointer;">
        <!-- 툴팁 표시 -->
        <div class="navigation-tooltip">
            {#if isActive}
                <!-- 툴팁 제어 -->
                {#if tooltip[0]}
                    <span>배열 직접 생성</span>
                {:else if tooltip[1]}
                    <span>랜덤 배열 생성</span>
                {:else if tooltip[8]}
                    <span>이분 탐색 시작</span>
                {/if}
            {/if}
        </div>

        <!-- 토글 표시 -->
        <div class="navigation-container">
            <!-- 토글 제어 -->
            <!-- 토글에 class="navigation-toggle" 적용 -->
            <!-- navigation-toggle에 transition:fly={{ x: -45, duration: 500 }} 적용 -->
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
                        <input type="number" id="element-cnt-input" min="3" max="20" bind:value={elementCnt}>
                        <button style="white-space: nowrap;" on:click={createRandomElement}>생성</button>
                    </div>
                {:else if toggle[8]}
                    <div class="navigation-toggle" transition:fly={{ x: -45, duration: 500 }} style="top: -21.5px;">
                        <span class='txt'>key</span> <span class='txt'>=</span>
                        <input type="number" id="key-input" min="1" max="99" bind:value={key}>
                        <button style="white-space: nowrap;" on:click={startBinarySearch}>탐색</button>
                    </div>
                {/if}
            {/if}
        </div>

        <!-- svelte-ignore a11y-no-static-element-interactions -->
        <!-- svelte-ignore a11y-click-events-have-key-events -->
        <div class="navigation-menu"  on:contextmenu={toggleNavigation}>
            <div class="navigation" class:active={isActive}>
                <!-- 0번 -->
                <span style="--i:0; --x:-1; --y:-1">
                    <ion-icon name="duplicate-outline" on:click={() => changeToggle(0)}
                    on:mouseenter={() => tooltip[0] = true} on:mouseleave={() => tooltip[0] = false}></ion-icon>
                </span>

                <!-- 1번 -->
                <span style="--i:1; --x:0; --y:-1">
                    <ion-icon name="shuffle-outline" on:click={() => changeToggle(1)}
                    on:mouseenter={() => tooltip[1] = true} on:mouseleave={() => tooltip[1] = false}></ion-icon>
                </span>

                <!-- 2번 -->
                <span style="--i:2; --x:1; --y:-1">

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

                <!-- 7번 -->
                <span style="--i:7; --x:-1; --y:0">

                </span>
            </div>

            <!-- 8번 (알고리즘 실행) -->
            <div class="center">
                <ion-icon name="play-outline" on:click={() => changeToggle(8)}
                          on:mouseenter={() => tooltip[8] = true} on:mouseleave={() => tooltip[8] = false}></ion-icon>
            </div>
        </div>
    </div>
</main>