<script>
    import Header from "../../../component/Header.svelte";
    import { isListVisible } from "../../../lib/store";
    import { onMount } from "svelte";

    let videoElement;

    onMount(() => {
        videoElement.addEventListener('click', () => {
            if (videoElement.paused) {
                videoElement.play();
            } else {
                videoElement.pause();
            }
        });
    });
</script>

<main>
    <div class="header-container">
        <Header/>
    </div>

    <div class="pre-main-container" class:visualization-list-visible={$isListVisible} class:visualization-list-invisible={!$isListVisible}>
        <video bind:this={videoElement} src="assets/pre/convexhull.mp4" loop muted class="pre-video"></video>
    </div>
</main>

<style>
    main {
        height: 100vh;
        display: grid;
        grid-template-rows: 70px 1fr;
        user-select: none;
        -ms-user-select: none;
        -moz-user-select: none;
        -webkit-user-select: none;
        overflow: hidden;
    }

    .pre-main-container {
        display: grid;
        grid-template-columns: 78vw auto;
        align-items: center;
    }

    .pre-video {
        cursor: none;
        outline: none;
    }
</style>
