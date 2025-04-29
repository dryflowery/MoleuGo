<script>
    import AlgorithmList from "./AlgorithmList.svelte";
    import LoginPopup from "./LoginPopup.svelte";
    import { push } from "svelte-spa-router";
    import {isListVisible, isLoginVisible, isLogin, checkLoginStatus} from "../lib/store";
    import {OK} from "../lib/httpStatusStore.js";

    const setAlgorithmListVisible = () => {
        $isListVisible = !$isListVisible;
    };

    const openLoginPopup = () => {
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

</script>

<main>
    <div class="algorithm-list" class:visible={$isListVisible}>
        <AlgorithmList/>
    </div>

    {#if $isLoginVisible === true}
        <LoginPopup/>
    {/if}
    
    <span id="header-left-container">
        <div id="menu-circle" on:click={setAlgorithmListVisible}>
            <img src="assets/menu.png" alt="" id="menu-toggle-icon">
        </div>

        <div id="site-icon"></div>

        <div id="site-name" style="padding-left: 10px;" on:click={() => push('/')}>MoleuGo</div>
    </span>

    <div id="header-right-container">
        {#if $isLogin === false}
            <button id="sign-in-btn" class="sign-btn" on:click={openLoginPopup}>로그인</button>
            <button id="sign-up-btn" class="sign-btn" on:click={() => push('/signup')}>회원가입</button>
        {:else}
            <ion-icon name="exit-outline" class="icon-hover-logout" on:click={logout}></ion-icon>

            <ion-icon name="home-outline" class="icon-hover-home" on:click={() => push('/my-page')} ></ion-icon>
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

    #site-icon {
        width: 35px;
        height: 35px;
        border-radius: 50%;
        border: 5px solid #3d444d;
        background-color: #8d8d8d;
        cursor: pointer;
    }

    #site-name {
        color: #FFFFFF;
        font-size: 1rem;
        cursor: pointer;
        font-weight: bold;
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
        transition: color 0.3s;
    }

    .icon-hover-home:hover {
        color: #00bfff;
    }

    .icon-hover-logout {
        color: white;
        cursor: pointer;
        font-size: 25px;
        margin-right: 15px;
        transition: color 0.3s;
    }

    .icon-hover-logout:hover {
        color: #b1132b;
    }

</style>