<!-- 좌클릭으로 drag, 우클릭으로 active  -->
<!-- active 상태에서는 #drag(손 아이콘)만 drag 가능  -->
<script>
    import { createEventDispatcher } from 'svelte';
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
        const nav = e.target.closest(".container");
        const rect = nav.getBoundingClientRect();   

        offsetX = e.clientX - rect.left;
        offsetY = e.clientY - rect.top;

        document.addEventListener("pointermove", onDrag);
        document.addEventListener("pointerup", stopDrag);
    };

    const onDrag = (e) => {
        if (!isDragging) return;

        const container = e.target.closest(".container");
        if (container) {
            navigationPos.top = `${e.clientY - offsetY}px`;
            navigationPos.left = `${e.clientX - offsetX}px`;
        }
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
</script>

<main>
    <div class="container" on:pointerdown={startDrag}
    style="top: {navigationPos.top}; left: {navigationPos.left}; position: absolute; cursor: pointer;">
        <!-- 툴팁 표시 -->
        <div class="navigation-tooltip">
            {#if isActive}
                <!-- 툴팁 제어 -->
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
        <div class="menu"  on:contextmenu={toggleNavigation}>
            <div class="navigation" class:active={isActive}>
                <span style="--i:0; --x:-1; --y:-1">
    
                </span>

                <span style="--i:1; --x:0; --y:-1">
           
                </span>

                <span style="--i:2; --x:1; --y:-1">

                </span>

                <span style="--i:3; --x:1; --y:0">
                 
                </span>

                <span style="--i:4; --x:1; --y:1" id="drag">
                    <!-- 잡고 이동하는 용도 -->
                    <ion-icon name="hand-left-outline"></ion-icon> 
                </span>

                <span style="--i:5; --x:0; --y:1">
                  
                </span>

                <span style="--i:6; --x:-1; --y:1">
              
                </span>
                
                <span style="--i:7; --x:-1; --y:0">
                   
                </span>           
            </div>
            
            <div class="center">
                <ion-icon name="play-outline"></ion-icon>
            </div>
        </div>
    </div>
</main>