<!-- 좌클릭으로 drag, 우클릭으로 active  -->
<!-- active 상태에서는 #drag(손 아이콘)만 drag 가능  -->
<script>
    import { createEventDispatcher } from 'svelte';
    const dispatch = createEventDispatcher();

    let tooltip = Array(4).fill(false);
    let isActive = false; 
    let isDragging = false; 
    let offsetX = 0; 
    let offsetY = 0; 
    let navigationPos = { top: "700px", left: "1300px" }; 
    let isAsc = true;

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

        const container = e.target.closest(".navigation-container");
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

    const switchAsc = () => {
        isAsc = !isAsc;
    };

    const createRandomElements = () => {
        let cnt = Math.floor(Math.random() * 20) + 1; // 사용자에게 직접 입력 받도록 수정
        dispatch('createRandomElements', {cnt});
    };
</script>

<main>
    <div class="navigation-container" on:pointerdown={startDrag}
    style="top: {navigationPos.top}; left: {navigationPos.left}; position: absolute; cursor: pointer;">
        <!-- 툴팁 표시 -->
        <div class="tooltip">
            {#if isActive}
                {#if tooltip[0]}
                    <span>배열 직접 생성</span>
                {:else if tooltip[1]}
                    <span>랜덤 원소 생성</span>
                {:else if tooltip[2]}
                    {#if isAsc}
                        <span>내림차순으로 설정</span>
                    {:else}
                        <span>오름차순으로 설정</span>
                    {/if}
                {:else if tooltip[3]}
                    <span>정렬 시작</span>
                {/if}
            {/if}
        </div>

        <!-- svelte-ignore a11y-no-static-element-interactions -->
        <!-- svelte-ignore a11y-click-events-have-key-events -->
        <div class="navigation-menu"  on:contextmenu={toggleNavigation}>
            <div class="navigation" class:active={isActive}>
                <span style="--i:0; --x:-1; --y:-1">
    
                </span>

                <span style="--i:1; --x:0; --y:-1"> 
                    <!-- 원소 개수 설정 후 랜덤 생성 -->
                    <ion-icon name="shuffle-outline" on:click={createRandomElements} 
                    on:mouseenter={() => tooltip[1] = true} on:mouseleave={() => tooltip[1] = false}></ion-icon>
                </span>

                <span style="--i:2; --x:1; --y:-1" on:click={switchAsc}>
                    <!-- 오름차순, 내림차순 변경 -->
                    {#if isAsc}
                        <ion-icon name="arrow-down-outline"
                        on:mouseenter={() => tooltip[2] = true} on:mouseleave={() => tooltip[2] = false}></ion-icon>
                    {:else}
                        <ion-icon name="arrow-up-outline"
                        on:mouseenter={() => tooltip[2] = true} on:mouseleave={() => tooltip[2] = false}></ion-icon>
                    {/if}
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
                <ion-icon name="play-outline"
                on:mouseenter={() => tooltip[3] = true} on:mouseleave={() => tooltip[3] = false}></ion-icon>
            </div>
        </div>
    </div>
</main>

<style>
    
</style>