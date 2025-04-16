<script>
    import { push } from "svelte-spa-router";
    import { onMount } from "svelte";
    import Toast from "../component/Toast.svelte";

    let showToast = false;
    let toastMessage = '';
    let toastType = 'success'; // or 'error'

    let type = "unknown";
    let uuid = null;
    let temporaryPassword = null;

    const showToastMessage = (message, type, timeout) => {
        toastMessage = message;
        toastType = type;
        showToast = true;

        if(timeout) {
            setTimeout(() => {
                showToast = false;
            }, 2000); // 2초 후 자동 숨김
        }
    };

    onMount(() => {
        const hash = window.location.hash;
        const queryString = hash.includes('?') ? hash.substring(hash.indexOf('?') + 1) : '';
        const params = new URLSearchParams(queryString);

        type = params.get("type") || "unknown";
        uuid = params.get("uuid") || null;

        if(uuid !== null) {
            getTemporaryPassword();
        }
    });

    const getTemporaryPassword = () => {
        return fetch(`/user/get-temporary-password/${uuid}`, {
            method: 'GET'
        })
        .then(response => response.text())
        .then(data => {
            temporaryPassword = data;
        })
    };

    const copyTemporaryPassword = () => {
        navigator.clipboard.writeText(temporaryPassword);
        showToastMessage("클립보드에 복사되었습니다.", "success", true);
    };

    const showCopyHint = () => {
        showToastMessage("클릭하면 복사됩니다.", "info", false);
    };

    const hideCopyHint = () => {
        if(toastType === "info") {
            showToast = false;
        }
    };
</script>


<main>
    <Toast message={toastMessage} type={toastType} show={showToast} />

    <div class="app-container">
        <div class="header">
            <div class="logo">
                <h1>Moleugo</h1>
                <p> 알고리즘 시각화 사이트 '모르고'</p>
            </div>
        </div>

        <div class="form-container">
            {#if type === "signup-success"}
                <h1>회원가입이 완료되었습니다!</h1><br>
            {:else if type === "email-change-success"}
                <h1>이메일이 성공적으로 수정되었습니다!</h1><br>
            {:else if type === "find-password-success"}
                <h1>비밀번호가 성공적으로 변경되었습니다!</h1><br>
            {:else if type === "fail"}
                <h1>다시 시도해주세요</h1><br>
            {/if}

            {#if type === "fail"}
                <img src="assets/fail.png" width="120px" alt=""><br>
                <br><br>
                <p style="color: white; padding-top: 7.5px;">세션이 만료되었거나, 잘못된 접근입니다.</p>
            {:else if type === "find-password-success"}
                <img src="assets/success.png" width="120px" alt="">
                <br><br>
                <p>발급된 임시 비밀번호는
                    <span style="color:#00a000; cursor: pointer;"
                          on:click={copyTemporaryPassword}
                          on:mouseover={showCopyHint}
                          on:mouseleave={hideCopyHint}>
                        {temporaryPassword}
                    </span>
                    입니다.</p>
                <p>로그인 후 비밀번호를 변경해주세요.</p>
            {:else}
                <img src="assets/success.png" width="120px" alt="">
                <br><br>
                <p style="color: white; padding-top: 7.5px;">메인 화면으로 이동 후 로그인하시면,</p>
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
