<script>
    import AlgorithmList from "./AlgorithmList.svelte";
    import LoginPopup from "./LoginPopup.svelte";
    import { push } from "svelte-spa-router";
    import {isListVisible, isLoginVisible, isLogin, isAlgoGuideVisible, isAboutVisible, isTeamsVisible, checkLoginStatus} from "../lib/store";
    import {OK} from "../lib/httpStatusStore.js";

    const setAlgorithmListVisible = () => {
        $isListVisible = !$isListVisible;
    };

    const openLoginPopup = () => {
        $isAboutVisible = $isTeamsVisible = false;
        $isLoginVisible = true;
    };

    const logout = () => {
        fetch('/logout', {
            method: 'POST',
            credentials: 'include',
        })
        .then(response => {
            checkLoginStatus();

            if(response.status === OK) {
                push('/');
            }
        })
    };

    // Header랑 Main Container가 분리되어 있어서 따로 작성(Header 눌러도 AlgoGuidePopup 안꺼짐 등 여러 문제 있음)
    const closeAlgoGuidePopup = (event) => {
        const popupContent = document.getElementById("guide-book");

        if (!popupContent.contains(event.target) && $isAlgoGuideVisible) {
            $isAlgoGuideVisible = false;
        }
    };
</script>

<main  style="background-color: { $isAlgoGuideVisible ? 'rgba(0, 0, 0, 0.4)' : '#000000' }"
       on:click={closeAlgoGuidePopup}>
    <div class="algorithm-list" class:visible={$isListVisible}>
        <AlgorithmList/>
    </div>

    {#if $isLoginVisible === true}
        <LoginPopup/>
    {/if}
    
    <div id="header-left-container">

        <div id="menu-circle" on:click={setAlgorithmListVisible}>
            <img src="assets/header/menu.png" alt="" id="menu-toggle-icon">
        </div>

        <div id="site-name" on:click={() => push('/')}>
            MoleuGo
        </div>

    </div>

    <div id="header-right-container">
        {#if $isLogin === false}
            <button id="sign-in-btn" class="sign-btn" on:click={openLoginPopup}>로그인</button>
            <button id="sign-up-btn" class="sign-btn" on:click={() => push('/signup')}>회원가입</button>
        {:else}
            <ion-icon name="exit-outline" class="icon-hover-logout" on:click={logout}></ion-icon>
            <ion-icon name="person-outline" class="icon-hover-home" on:click={() => push('/my-page')}></ion-icon>
        {/if}
    </div>
</main>

<style>
    main, #header-left-container, #header-right-container, #menu-circle {
        display: flex;
        justify-content: center;
        align-items: center;
    }
    
    main {
        height: 70px;
        display: flex;
        border-bottom: 1px solid #3d444d;
        background-color: #000000;
    }

    #header-left-container {
        position: absolute;
        left: 10px;
        gap: 5px;
    }

    #header-right-container {
        position: absolute;
        right: 35px;
    }

    #menu-circle {
        border-radius: 50%;
        width: 50px;
        height: 50px;
        cursor: pointer;
    }

    #menu-circle:hover {
        background-color: #4d4d4d;
    }

    .algorithm-list {
        position: absolute;
        top: 71px;
        left: -401px; 
        width: 400px;
        height: calc(100vh - 120px);
        transition: left 0.5s ease;
        z-index: 1;
    }

    .visible {
        left: 0; 
    }

    #site-name {
        color: #FFFFFF;
        font-size: 1.1rem;
        cursor: pointer;
        font-weight: bold;

        border-radius: 6px;
        cursor: pointer;

        padding-left: 10px;
        padding: 8px 12px;
    }

    #site-name:hover {
        background-color: rgba(255, 255, 255, 0.1); /* 예: 어두운 배경에서 약간 밝게 */
    }

    .sign-btn {
        padding: 10px 20px;
        color: #FFFFFF;
        border-radius: 10px;
        cursor: pointer;
        font-size: 1rem;
        border: 1px solid #FFFFFF;
    }

    .sign-btn:hover {
        background-color: #1a1a1a;
        border: 1px solid #cccccc;
        color: #cccccc;
    }

    #sign-in-btn {
        padding: 12px 20px;
        background-color: transparent;
        border: none;
    }

    #sign-up-btn {
        background-color: #000000;
    }

    .icon-hover-home {
        color: white;
        cursor: pointer;
        font-size: 25px;
        margin-right: 5px;
        
    }

    .icon-hover-home:hover {
        color: #5e5e5e;
    }

    .icon-hover-logout {
        color: white;
        cursor: pointer;
        font-size: 25px;
        margin-right: 15px;
        
    }

    .icon-hover-logout:hover {
        color: #5e5e5e;
    }

</style>