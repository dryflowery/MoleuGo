<script>
    import { isAlgoGuideVisible, algoGuideDelay } from "../../../lib/store.js";
    import { fade } from 'svelte/transition';

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

<style>
    #popup-container {
        position: absolute;
        display: flex; align-items: center; justify-content: center;
        width: 100%; height: 100%;
        top: 0; left: 0;
        background-color: rgba(0, 0, 0, 0.4);
        z-index: 1000;
    }

    /* 책 전체 */
    #guide-book {
        position: relative;
        width: 70%; height: 90%;
        background-color: saddlebrown; color: #FFFFFF;
        border-radius: 10px;
        display: flex; align-items: center; justify-content: center;
        box-shadow: inset 0 0 30px rgba(0, 0, 0, 0.5), 0 1em 3em rgba(0, 0, 0, 0.5);
        overflow: hidden;
    }

    /* 책등 표현*/
    #guide-book::before {
        content: '';
        position: absolute;
        top: 0; bottom: 0; left: 50%;
        box-shadow: 0 0 1rem 1rem rgba(60, 30, 10, 0.5);
    }

    /* 페이지 전체 */
    .page {
        position: relative;
        display: grid; grid-template-columns: 1fr 1fr;
        width: 95%; height: 95%;
        background: #fff; color: black;
        box-shadow: inset 0 0 30px rgba(0, 0, 0, 0.5), 0 1em 3em rgba(0, 0, 0, 0.5);
        border-radius: 10px;
        overflow: hidden;
    }

    /* 페이지 가운데 접힘부 */
    .page::before {
        content: '';
        position: absolute;
        top: 0; bottom: 0; left: 50%;
        width: 0.3rem;
        transform: translateX(-50%);
        box-shadow: 0 0 1rem 1rem #d5d5d5;
        background: linear-gradient(
                to right,
                #a1a1a1 0%,
                #a1a1a1 33%,
                #7a7a7a 33%,
                #7a7a7a 100%
        );
    }

    .left-page, .right-page {
        overflow: hidden;
    }

    /* 왼쪽 남은 페이지 */
    .left-page:not(.first-page)::after {
        content: '';
        position: absolute;
        top: 0; bottom: 0; left: 0;
        width: 1.5rem;
        box-shadow: -0.5rem 0 1rem -0.5rem rgba(0, 0, 0, 0.3);
        transform: translateX(-0.5rem);
        background: repeating-linear-gradient(
                to left,
                rgba(0, 0, 0, 0.05) 0px,
                rgba(0, 0, 0, 0.05) 2px,
                transparent 2px,
                transparent 6px
        );
    }

    /* 오른쪽 남은 페이지 */
    .right-page:not(.last-page)::before {
        content: '';
        position: absolute;
        top: 0; bottom: 0; right: 0;
        width: 1.5rem;
        box-shadow: -0.5rem 0 1rem -0.5rem rgba(0, 0, 0, 0.3);
        transform: translateX(0.5rem);
        background: repeating-linear-gradient(
                to left,
                rgba(0, 0, 0, 0.05) 0px,
                rgba(0, 0, 0, 0.05) 2px,
                transparent 2px,
                transparent 6px
        );
    }

    .forward-page, .backward-page {
        width: 100%; height: 100%;
    }

    /* 뒷페이지는 180도 반전 */
    .backward-page {
        transform: rotateY(180deg);
        display: none;
    }

    /* 오른쪽 페이지 flip 애니메이션 */
    .right-page.flip-nxt1 {
        transform-origin: left center;
        animation: flipNxt1 0.35s ease-out forwards;
    }

    .right-page.flip-nxt2 {
        transform-origin: left center;
        animation: flipNxt2 0.35s ease-out forwards;
    }

    /* 0도 ~ 90도에서는 앞면이 보이게, 90도 ~ 180도에서는 뒷면이 보이게 */
    .right-page.flip-nxt2 .forward-page {
        display: none;
    }

    .right-page.flip-nxt2 .backward-page {
        display: block;
    }

    @keyframes flipNxt1 {
        0%   { transform: rotateY(0deg); }
        100% { transform: rotateY(-90deg); }
    }

    @keyframes flipNxt2 {
        0%   { transform: rotateY(-90deg); }
        100% { transform: rotateY(-180deg); }
    }
    /* 오른쪽 페이지 flip 애니메이션 */

    /* 왼쪽 페이지 flip 애니메이션 */
    .left-page.flip-prev1 {
        transform-origin: right center;
        animation: flipPrev1 0.35s ease-out forwards;
    }

    .left-page.flip-prev2 {
        transform-origin: right center;
        animation: flipPrev2 0.35s ease-out forwards;
    }

    /* 0도 ~ 90도에서는 앞면이 보이게, 90도 ~ 180도에서는 뒷면이 보이게 */
    .left-page.flip-prev2 .forward-page {
        display: none;
    }

    .left-page.flip-prev2 .backward-page {
        display: block;
    }

    @keyframes flipPrev1 {
        0%   { transform: rotateY(0deg); }
        100% { transform: rotateY(90deg); }
    }

    @keyframes flipPrev2 {
        0%   { transform: rotateY(90deg); }
        100% { transform: rotateY(180deg); }
    }
    /* 왼쪽 페이지 flip 애니메이션 */

    /* flip 애니메이션에서 넘기는 페이지의 원래 자리가 비어있지 않도록 */
    .background-page {
        position: absolute;
        height: 100%; width: 50%;
        overflow: hidden;
        display: none;
    }

    .background-page.show-next {
        right: 0;
        display: block;
    }

    .background-page.show-prev {
        left: 0;
        display: block;
    }
    /* flip 애니메이션에서 넘기는 페이지의 원래 자리가 비어있지 않도록 */
</style>