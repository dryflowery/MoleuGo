<script>
    import { isTeamsVisible, isAboutVisible, isLoginVisible } from "../lib/store.js";
    import Teams from "./footer/Teams.svelte";
    import About from "./footer/About.svelte";
    
    const openTeamsPopup = () => {
        $isAboutVisible = $isLoginVisible = false;
        $isTeamsVisible = true;
    }

    const openAboutPopup = () => {
        $isTeamsVisible = $isLoginVisible = false;
        $isAboutVisible = true;
    }
</script>

<main>
    <div id="footer-container">
        <div id="footer-github">
            <a href="https://github.com/dryflowery/MoleuGo" target="_blank" id="footer-github">
                <ion-icon name="logo-github" style="font-size: 1.5rem" class="footer-element"></ion-icon>
            </a>
        </div>

        <div id="footer-teams" class="footer-element" on:click={openTeamsPopup}>Teams</div>
        <div id="footer-about" class="footer-element" on:click={openAboutPopup}>About</div>
    </div>

    <div>
        {#if $isTeamsVisible === true}
            <Teams/>
        {:else if $isAboutVisible === true}
            <About/>
        {/if}
    </div>
</main>

<style>
    main {
        height: 39px;
        border-top: 1px solid #3d444d;
        background-color: #000000;
        color: white;
        overflow: hidden;
        font-size: 0.8rem;
    }

    #footer-container {
        -webkit-user-select:none;
        -moz-user-select:none;
        -ms-user-select:none;
        user-select:none;
        display: flex;
        gap: 10px;
        align-items: center;
        margin-bottom: 25px;
        justify-content: center;
    }

    .footer-element:hover {
        cursor: pointer;
        text-decoration: underline;
    }

    #footer-github {
        color: white;
        transition: color 0.3s;
    }


    #footer-github:hover ion-icon {
        color: #aaa;
    }
</style>