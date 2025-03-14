<script>
    import { isLoginVisible, isLogin } from "../lib/store";
    import {OK, UNAUTHORIZED} from "../lib/httpStatusStore.js";
    import { push } from "svelte-spa-router";

    let loginHttpStatusCode;
    let loginMessage;

    let inputEmail = "";
    let inputPassword = "";

    const closeLoginPopup = (event) => {
        const popupContent = document.getElementById("popup-content");

        if (!popupContent.contains(event.target)) {
            closePopup();
        }
    };

    const closePopup = () => {
        $isLoginVisible = false;
    };

    const login = () => {
        if (!isValidLoginInput()) {
            return;
        }

        sendLoginRequest()
        .then(noArgs => {
            if (loginHttpStatusCode === OK) {
                $isLogin = true;
                closePopup();
                push("/my-page")
            }
            else if (loginHttpStatusCode === UNAUTHORIZED) {
                alert(loginMessage);
            }
        });
    }

    // 이메일, 비밀번호가 전부 입력되었나 확인
    const isValidLoginInput = () => {
        let isValid = true;

        if(isEmailEntered()) {
            document.getElementById("login-email-input").style.border = "1px solid #c0392b";
            isValid = false;
        }

        if(isPasswordEntered()) {
            document.getElementById("login-password-input").style.border = "1px solid #c0392b";
            isValid = false;
        }

        if(isValid) {
            document.getElementById("login-email-input").style.border = "1px solid #3d444d";
            document.getElementById("login-password-input").style.border = "1px solid #3d444d";
        }

        return isValid;
    }

    const isEmailEntered = () => {
        let checkEmail = inputEmail.replace(/\s+/g, '');
        return checkEmail === "";
    }

    const isPasswordEntered = () => {
        let checkPassword = inputPassword.replace(/\s+/g, '');
        return checkPassword === "";
    }

    const sendLoginRequest = () => {
        return fetch('/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                email: inputEmail,
                password: inputPassword,
            }),
            credentials: "include"
        })
        .then(response => {
            loginHttpStatusCode = response.status;
            return response.json();
        })
        .then(data => {
            loginMessage = data.loginMessage;
        });
    }
</script>


<main>
    <div id="popup-container" on:click={closeLoginPopup}>
        <div id="popup-content">
            <button id="close-btn" on:click={closePopup}>x</button>
            
            <h1>Moleugo</h1>
            
            <!-- 로그인 추가(백엔드) -->
            <input type="text"
                   class="input-field"
                   id="login-email-input"
                   placeholder="아이디(이메일) 입력 ...."
                   bind:value={inputEmail}
            />
            <input type="password"
                   class="input-field"
                   id="login-password-input"
                   placeholder="비밀번호 입력 ...."
                   bind:value={inputPassword}
            />
            
            <button id="login-btn" on:click={login}>로그인</button>
            
            <div id="link-container">
                <span on:click={()=> push('/find-password')}>비밀번호 찾기</span> | <span on:click={()=> push('/signup')}>회원가입</span>
            </div>
            
            <div class="divider"></div>
            
            <!-- 로그인 추가(백엔드) -->
            <div id="social-login-container">
                <div id="google-login">
                    <img src="https://img.icons8.com/color/200/google-logo.png" alt="Google Login" />
                </div>
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
        z-index: 1000;
    }

    #popup-content {
        text-align: center;
        width: 400px;
        height: 500px;
        padding: 20px;
        border: 2px solid #1e2024;
        border-radius: 10px;
        color: #FFFFFF;
        background-color: #151515;
        position: relative;
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

    h1 {
        font-size: 2em;
        padding-bottom: 20px;
        padding-top: 40px;
    }

    .input-field {
        width: 250px;
        padding: 10px;
        margin-bottom: 10px;
        border: 1px solid #3d444d;
        border-radius: 5px;
        color: #FFFFFF;
        background-color: #152321;
        font-size: 1rem;
    }

    #login-btn {
        width: 274px;
        padding: 10px;
        margin: 10px 0;
        border: none;
        border-radius: 5px;
        background-color: #28a745;
        color: #FFFFFF;
        font-size: 1rem;
        font-weight: bold;
        cursor: pointer;
    }

    #login-btn:hover {
        background-color: #218838;
    }

    #link-container {
        font-size: 0.9em;
        color: #9e9e9e;
    }

    #link-container span {
        color: #9e9e9e;
        text-decoration: none;
        margin: 0 5px;
    }

    #link-container span:hover {
        text-decoration: underline;
        cursor: pointer;
    }

    .divider {
        margin: 50px 0;
        border-top: 1px solid #444;
        position: relative;
        color: #9e9e9e;
    }

    .divider:before {
        content: "간편로그인";
        position: absolute;
        top: -13px;
        left: 50%;
        transform: translateX(-50%);
        background-color: #151515;
        padding: 0 10px;
    }

    #social-login-container {
        display: flex;
    }

    #google-login {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 50px;
        height: 50px;
        margin: 0 auto;
        border-radius: 5px;
        background-color: #FFFFFF;
        cursor: pointer;
    }

    #google-login:hover {
        background-color: #E0E0E0;
    }

    #social-login-container img {
        width: 24px;
        height: 24px;
    }
</style>