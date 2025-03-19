<script>
    import { push } from "svelte-spa-router";
    import {onMount} from "svelte";

    let result;

    onMount(() => {
        const params = new URLSearchParams(window.location.search);
        result = params.get("result") || "unknown";
        console.log(result);
    });
</script>


<main>
    <div class="app-container">
        <div class="header">
            <div class="logo">
                <h1>Moleugo</h1>
                <p> 알고리즘 시각화 사이트 '모르고'</p>
            </div>
        </div>

        <div class="form-container">
            <h1>회원가입이 완료되었습니다!</h1><br>
            <img src="assets/success.png" width="120px" alt="">

            <br><br>
            {#if result === "normal-signup-success"}
                <p style="color: white; padding-top: 7.5px;">메인 화면으로 이동 후 다시 로그인하시면,</p>
                <p style="color: white; padding-top: 7.5px;">모르고의 다양한 서비스를 마음껏 이용하실 수 있습니다.</p>
            {:else if result === "google-signup-success"}
                <p style="color: white; padding-top: 7.5px;">메인 화면으로 이동 후 다시 로그인하시면,</p>
                <p style="color: white; padding-top: 7.5px;">모르고의 다양한 서비스를 마음껏 이용하실 수 있습니다.</p>
            {:else if result === "exist-google-account"}
                <p style="color: white; padding-top: 7.5px;">메인 화면으로 이동 후 다시 로그인하시면,</p>
                <p style="color: white; padding-top: 7.5px;">모르고의 다양한 서비스를 마음껏 이용하실 수 있습니다.</p>
            {/if}

            <button class="return-to-main-button" on:click={() => push('/')}>
                메인화면으로 돌아가기
            </button>
        </div>
    </div>
</main>

<style>
    .app-container {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: flex-start;
        min-height: 100vh;
        background-color: rgb(20, 20, 20);
        padding: 20px;
        box-sizing: border-box;
    }

    .header {
        margin-bottom: 20px;
    }
    .logo {
        text-align: center;
    }

    .form-container {
        width: 100%;
        height: 650px;
        max-width: 400px;
        text-align: center;
    }

    h1 {
        margin-bottom: 8px;
        font-size: 1.5rem;
        color: #ffffff;
    }

    p {
        margin: 0;
        font-size: 1rem;
        color: #8d8d8d;
    }

    .return-to-main-button {
        padding: 12px;
        background-color: #00a000;
        color: #fff;
        border: none;
        border-radius: 4px;
        cursor: pointer;
        font-size: 1rem;
        font-weight: bold;
        width: 270px;
        margin-top: 30px;
    }

    .return-to-main-button:hover {
        background-color: #007d00;
    }

    @media (min-width: 2560px) {
        .header {
            margin-bottom: 20px;
            margin-top: 130px;
        }
    }
</style>