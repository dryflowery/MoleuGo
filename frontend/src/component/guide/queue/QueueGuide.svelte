<script>
    /******************************사용자 작성 영역 예시****************************

     // component/{algorithm-name}/sheet에 각 페이지 내용 작성
     import BubbleSortSheet1 from "./sheet/BubbleSortSheet1.svelte";
     import BubbleSortSheet2 from "./sheet/BubbleSortSheet2.svelte";
     import BubbleSortSheet3 from "./sheet/BubbleSortSheet3.svelte";
     import BubbleSortSheet4 from "./sheet/BubbleSortSheet4.svelte";

     // 왼쪽 페이지의 앞면(해당 페이지를 보고 있을 때 보이는 면)과 뒷면
     const leftSheets = {
     1: { forward: BubbleSortSheet1, backward: undefined },
     3: { forward: BubbleSortSheet3, backward: BubbleSortSheet2 },
     };

     // 오른쪽 페이지의 앞면(해당 페이지를 보고 있을 때 보이는 면)과 뒷면
     const rightSheets = {
     1: { forward: BubbleSortSheet2, backward: BubbleSortSheet3 },
     3: { forward: BubbleSortSheet4, backward: undefined },
     };

     let curPage = 1, minPage = 1, maxPage = 3; // 왼쪽 페이지 기준
     *****************************사용자 작성 영역 예시****************************/

    /******************************사용자 작성 영역****************************/
    import QueueSheet1 from "./sheet/QueueSheet1.svelte";
    import QueueSheet2 from "./sheet/QueueSheet2.svelte";
    import QueueSheet3 from "./sheet/QueueSheet3.svelte";
    import QueueSheet4 from "./sheet/QueueSheet4.svelte";

    const leftSheets = {
        1: { forward: QueueSheet1, backward: undefined },
        3: { forward: QueueSheet3, backward: QueueSheet2 },
    };

    const rightSheets = {
        1: { forward: QueueSheet2, backward: QueueSheet3 },
        3: { forward: QueueSheet4, backward: undefined }
    };

    let curPage = 1, minPage = 1, maxPage = 3;
    /******************************사용자 작성 영역****************************/

    import { isAlgoGuideVisible, algoGuideDelay } from "../../../lib/store.js";
    import { fade } from 'svelte/transition';

    // nxt1, prev1은 0도 ~ 90도, nxt2, prev2는 90도 ~ 180도
    let flipNxt1 = false, flipNxt2 = false;
    let flipPrev1 = false, flipPrev2 = false;
    $: flipAnimationWorking = (flipNxt1 || flipNxt2 || flipPrev1 || flipPrev2)

    const delay = (duration) => {
        return new Promise((resolve) => { setTimeout(resolve, duration); });
    };

    // 오른쪽 페이지 클릭
    const nextPage = async () => {
        if (curPage + 2 <= maxPage && !flipAnimationWorking) {
            // 0도 ~ 90도
            flipNxt1 = true;
            await delay($algoGuideDelay);

            // 90도 ~ 180도
            flipNxt1 = false;
            flipNxt2 = true;
            await delay($algoGuideDelay);

            flipNxt2 = false;
            curPage += 2;
        }
    };

    // 왼쪽 페이지 클릭
    const prevPage = async () => {
        if (curPage - 2 >= minPage && !flipAnimationWorking) {
            // 0도 ~ 90도
            flipPrev1 = true;
            await delay($algoGuideDelay);

            // 90도 ~ 180도
            flipPrev1 = false;
            flipPrev2 = true;
            await delay($algoGuideDelay);

            flipPrev2 = false;
            curPage -= 2;
        }
    };

    // pop-content 영역 밖을 클릭하면 팝업 닫기
    const closeAlgoGuidePopup = (event) => {
        const popupContent = document.getElementById("guide-book");

        if (!popupContent.contains(event.target)) {
            $isAlgoGuideVisible = false;
        }
    };
</script>


<main>
    <div id="popup-container" on:click={closeAlgoGuidePopup} transition:fade={{ duration: 200 }}>
        <!-- 전체 책(책 커버 포함) -->
        <div id="guide-book">
            <!-- 전체 페이지 -->
            <div class="page">

                <!-- 배경 페이지(없으면 flip 애니메이션 실행 시 넘기는 페이지의 원래 자리에 아무것도 안나옴) -->
                <div class="background-page" class:show-next={flipNxt1 || flipNxt2} class:show-prev={flipPrev1 || flipPrev2}>
                    {#if flipNxt1 || flipNxt2}
                        <svelte:component this={rightSheets[curPage + 2].forward} />
                    {:else if flipPrev1 || flipPrev2}
                        <svelte:component this={leftSheets[curPage - 2].forward} />
                    {/if}
                </div>

                <!-- 왼쪽 페이지 -->
                <div class="left-page"
                     class:first-page={curPage === minPage}
                     class:flip-prev1={flipPrev1}
                     class:flip-prev2={flipPrev2}
                     on:click|stopPropagation={prevPage}
                >
                    <!-- 왼쪽 페이지 앞면 -->
                    <div class="forward-page">
                        <svelte:component this={leftSheets[curPage].forward} />
                    </div>

                    <!-- 왼쪽 페이지 뒷면 -->
                    <div class="backward-page">
                        <svelte:component this={leftSheets[curPage].backward} />
                    </div>
                </div>

                <!-- 오른쪽 페이지 -->
                <div class="right-page"
                     class:last-page={curPage === maxPage}
                     class:flip-nxt1={flipNxt1}
                     class:flip-nxt2={flipNxt2}
                     on:click|stopPropagation={nextPage}
                >
                    <!-- 오른쪽 페이지 앞면 -->
                    <div class="forward-page">
                        <svelte:component this={rightSheets[curPage].forward} />
                    </div>

                    <!-- 오른쪽 페이지 뒷면 -->
                    <div class="backward-page">
                        <svelte:component this={rightSheets[curPage].backward} />
                    </div>
                </div>
            </div>
        </div>
    </div>
</main>