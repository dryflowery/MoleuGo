<script>
    import { isAboutVisible } from "../../lib/store.js";

    const closeLoginPopup = (event) => {
        const popupContent = document.getElementById("popup-content");

        if (!popupContent.contains(event.target)) {
            closePopup();
        }
    };

    const closePopup = () => {
        $isAboutVisible = false;
    };
</script>


<main>
    <div id="popup-container" on:click={closeLoginPopup}>
        <div id="popup-content">
            <div id="about-top">
                <button id="close-btn" on:click={closePopup}>x</button>
                <p>About</p>
            </div>

            <div id="about-content">
                About
            </div>
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
        z-index: 2000;
    }

    #popup-content {
        text-align: center;
        width: 50vw;
        height: 60vh;
        border: 2px solid #1e2024;
        border-radius: 10px;
        color: #FFFFFF;
        background-color: #151515;
        position: relative;
    }

    #about-top {
        display: flex;
        justify-content: center;
        align-items: center;
        border-bottom: 2px solid white;
        height: 60px;
    }

    #close-btn {
        position: absolute;
        top: 10px;
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

    p {
        font-size: 1.5rem;
    }

    #about-content {
        font-size: 1rem;
        padding: 20px;
    }
</style>