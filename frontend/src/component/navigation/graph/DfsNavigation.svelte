<!-- 좌클릭으로 drag, 우클릭으로 active  -->
<!-- active 상태에서는 #drag(손 아이콘)만 drag 가능  -->
<script>
    import { createEventDispatcher } from 'svelte';
    import { fly } from 'svelte/transition';
    import { animationWorking } from '../../../lib/visualizationStore';
    const dispatch = createEventDispatcher();

    let tooltip = Array(9).fill(false);
    let toggle = Array(9).fill(false);
    let isActive = false; 
    let isDragging = false; 
    let offsetX = 0; 
    let offsetY = 0; 
    let navigationPos = { top: "700px", left: "1300px" }; 

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

    const startDfs = (e) => {
        if(!isActive) {
            return;
        }

        toggle = Array(9).fill(false);
        isActive = false;
        dispatch('startDfs');
    };
</script>

<main>
    <div class="navigation-container" on:pointerdown={startDrag}
    style="top: {navigationPos.top}; left: {navigationPos.left}; position: absolute; cursor: pointer;">
        <!-- 툴팁 표시 -->
        <div class="navigation-tooltip">
            {#if isActive}
                {#if tooltip[8]}
                    <span>DFS 시작하기</span>
                {/if}
            {/if}
        </div>

        <!-- 토글 표시 -->
        <div class="navigation-container">
            <!-- 토글 제어 -->
            <!-- 토글에 class="navigation-toggle" 적용 -->
            <!-- navigation-toggle에 transition:fly={{ x: -45, duration: 500 }} 적용 -->
        </div>

        <!-- svelte-ignore a11y-no-static-element-interactions -->
        <!-- svelte-ignore a11y-click-events-have-key-events -->
        <div class="navigation-menu"  on:contextmenu={toggleNavigation}>
            <div class="navigation" class:active={isActive}>
                <!-- 0번 -->
                <span style="--i:0; --x:-1; --y:-1">
    
                </span>

                <!-- 1번 -->
                <span style="--i:1; --x:0; --y:-1">
           
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
                <ion-icon name="play-outline" on:click={startDfs}
                on:mouseenter={() => tooltip[8] = true} on:mouseleave={() => tooltip[8] = false}></ion-icon>
            </div>
        </div>
    </div>
</main>