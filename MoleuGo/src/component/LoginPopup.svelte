<script>
    import { isLoginVisible } from "../lib/store";

    const closeLoginPopup = (event) => {
        const popupContent = document.getElementById("popup-content");

        if (!popupContent.contains(event.target)) {
            closePopup();
        }
    };

    const closePopup = () => {
        $isLoginVisible = false;
    };
</script>

<main>
    <div id="popup-container" on:click={closeLoginPopup}>
        <div id="popup-content">
            <button id="close-btn" on:click={closePopup}>x</button>
            
            <h1>Moleugo</h1>
            
            <!-- 로그인 추가(백엔드) -->
            <input type="text" class="input-field" placeholder="아이디(이메일) 입력 ...." />
            <input type="password" class="input-field" placeholder="비밀번호 입력 ...." />
            
            <button id="login-btn">로그인</button>
            
            <div id="link-container">
                <a href="#">비밀번호 찾기</a> | <a href="#">회원가입</a>
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
        background-color: rgba(0, 0, 0, 0.4);
        z-index: 1000;
    }

    #popup-content {
        text-align: center;
        width: 400px;
        height: 500px;
        padding: 20px;
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
        border: 2px solid #3d444d;
        border-radius: 5px;
        color: #FFFFFF;
        background-color: #152321;
        font-size: 1rem;
    }

    #login-btn {
        width: 274px;
        padding: 10px;
        margin: 10px 0px;
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

    #link-container a {
        color: #9e9e9e;
        text-decoration: none;
        margin: 0 5px;
    }

    #link-container a:hover {
        text-decoration: underline;
    }

    .divider {
        margin: 50px 0px;
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