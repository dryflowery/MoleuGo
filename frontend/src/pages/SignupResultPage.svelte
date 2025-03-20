<script>
    import { push } from "svelte-spa-router";
    import {onMount} from "svelte";

    let result;

    onMount(() => {
        const hash = window.location.hash;
        const queryString = hash.includes('?') ? hash.substring(hash.indexOf('?')) : '';
        const params = new URLSearchParams(queryString);
        result = params.get("result") || "unknown";
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
            {#if result === "normal-signup-success" || result === "google-signup-success"}
                <h1>회원가입이 완료되었습니다!</h1><br>
            {:else if result === "exist-google-account"}
                <h1>이미 구글 계정과<br> 연동이 완료된 이메일입니다!</h1><br>
            {:else if result === "google-link-success"}
                <h1>기존 계정과 연동이 완료되었습니다!</h1><br>
            {/if}

            <img src="assets/success.png" width="120px" alt="">
            <br><br>
            <p style="color: white; padding-top: 7.5px;">메인 화면으로 이동 후 로그인하시면,</p>
            <p style="color: white; padding-top: 7.5px;">모르고의 다양한 서비스를 마음껏 이용하실 수 있습니다.</p>

            {#if result === "google-signup-success"}
                <br>
                <p style="color: white; padding-top: 7.5px;">발급된 임시 비밀번호는 <b style="color: #00a000">1q2w3e4r!</b> 입니다.</p>
                <p style="color: white; padding-top: 7.5px;">로그인 후 <b style="color: #00a000">마이 페이지 ⮕ 비밀번호 변경</b>에서 반드시 비밀번호를 변경해주세요!</p>
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