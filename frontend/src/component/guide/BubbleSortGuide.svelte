<script>
    import { isAlgoGuideVisible } from "../../lib/store.js";

    const closeAlgoGuidePopup = (event) => {
        const popupContent = document.getElementById("popup-content");

        if (!popupContent.contains(event.target)) {
            closePopup();
        }
    };

    const closePopup = () => {
        $isAlgoGuideVisible = false;
    };
</script>


<main>
    <div id="popup-container" on:click={closeAlgoGuidePopup}>
        <div id="popup-content">
            <button id="close-btn" on:click={closePopup}>x</button>

            <!-- 매뉴얼 본문 -->


        </div>
    </div>
</main>

<style>
    #popup-container {
        position: absolute;
        display: flex;
        align-items: center;
        justify-content: center;
        width: 100%;
        height: 100%;
        top: 0;
        left: 0;
        background-color: rgba(0, 0, 0, 0.4);
        z-index: 1000;
    }

    #popup-content {
        position: relative;
        text-align: center;
        width: 90%;
        height: 90%;
        border: 2px solid #1e2024;
        border-radius: 10px;
        color: #FFFFFF;
        background-color: #151515;
        overflow: hidden;
    }

    #close-btn {
        position: absolute;
        top: 20px;
        right: 20px;
        background: none;
        border: none;
        color: #FFFFFF;
        font-size: 1.5rem;
        cursor: pointer;
    }

    #close-btn:hover {
        color: #E0E0E0;
    }
</style>