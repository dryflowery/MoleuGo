<!-- 좌클릭으로 drag, 우클릭으로 active  -->
<!-- active 상태에서는 #drag(손 아이콘)만 drag 가능  -->
<script>
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

</script>

<main>
    <div class="container" on:pointerdown={startDrag}
    style="top: {navigationPos.top}; left: {navigationPos.left}; position: absolute; cursor: pointer;">
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

<style>
  
</style>