<script>

	import {OK, CONFLICT, BAD_REQUEST} from "../lib/httpStatusStore.js";

	let httpStatusCode;

	let inputEmail = ""; // 이메일 입력값
	let emailErrorMessage = ""; // 이메일 에러 메시지

	let inputNewPassword = ""; // 새 비밀번호 입력값
	let inputNewPasswordVerify = ""; // 새 비밀번호 확인값

	let condition1Met = false; // 영문/숫자/특수문자 중 2가지 이상 포함
	let condition2Met = false; // 8자 이상 32자 이하 입력 (공백 제외)
	let condition3Met = false; // 연속 3자 이상 동일한 문자/숫자 제외

	let verifyPasswordMessage = "확인을 위해 새 비밀번호를 다시 입력하세요"; // 비밀번호 확인 메시지
	let verifyPasswordMessageStyle = ""; // 메시지 스타일

	let isNewPasswordVisible = false; // 새 비밀번호 보이기 상태
	let isNewPasswordVerifyVisible = false; // 새 비밀번호 확인용 보이기 상태

	  // 이메일 유효성 검사
	function validateEmail(email) {

    	const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

		if (!emailRegex.test(email)) {
			emailErrorMessage = "이메일 형식이 올바르지 않습니다.";
		} else {
			emailErrorMessage = "";
		}
  	}

	function validateNewPassword(password) {

		if (password === "") {
			// 비밀번호가 비어 있으면 모든 조건 초기화
			condition1Met = false;
			condition2Met = false;
			condition3Met = false;
			return;
		}

    // 1. 영문/숫자/특수문자 중 2가지 이상 포함
    condition1Met = [
      /[a-zA-Z]/.test(password), // 영문
      /[0-9]/.test(password), // 숫자
      /[^a-zA-Z0-9\s]/.test(password), // 특수문자
    ].filter(Boolean).length >= 2;

    // 2. 8자 이상 32자 이하 입력 (공백 제외)
    condition2Met = password.replace(/\s/g, "").length >= 8 && password.length <= 32;

    // 3. 연속 3자 이상 동일한 문자/숫자 제외
    condition3Met = !/(.)\1\1/.test(password);
	}

  	// 비밀번호 확인 일치 여부 검사
  	$: {
		if (inputNewPasswordVerify === "") {
			verifyPasswordMessage = "";
			verifyPasswordMessageStyle = ""; // 기본 스타일
		} else if (inputNewPasswordVerify === inputNewPassword) {
			verifyPasswordMessage = "비밀번호가 일치합니다";
			verifyPasswordMessageStyle = "color: #238636;"; // 초록색
		} else {
			verifyPasswordMessage = "비밀번호가 일치하지 않습니다";
			verifyPasswordMessageStyle = "color: rgb(173, 44, 44);"; // 빨간색
		}
  	}

	// 비밀번호 보이기 버튼 토글
	function toggleNewPasswordVisibility() {
		isNewPasswordVisible = !isNewPasswordVisible;
	}

	function toggleNewPasswordVerifyVisibility() {
		isNewPasswordVerifyVisible = !isNewPasswordVerifyVisible;
	}

	// 폼 유효성 검사 후 백엔드로 회원가입 요청 전송
	const signUp = () => {
		if (!isFormValid()) {
			return;
		}

		sendSignUpRequest()
			.then(noArgs => {
				if (httpStatusCode === OK) {
					alert("회원가입이 완료되었습니다.");
				}
				else if (httpStatusCode === BAD_REQUEST) {
					alert("올바르지 않은 형식의 입력입니다.\n이메일 혹은 비밀번호를 다시 입력해주세요.");
				}
				else if (httpStatusCode === CONFLICT) {
					alert("이미 등록된 이메일입니다.\n다른 이메일을 사용해주세요.");
				}
			});
	};

	// 전체 폼 유효성 검사
	const isFormValid = () => {
		return isValidEmail() && isValidPassword() && isValidVerifyPassword();
	}

	const isValidEmail = () => {
		return inputEmail !== "" && emailErrorMessage === "";
	}

	const isValidPassword = () => {
		return inputNewPassword !== "" && condition1Met && condition2Met && condition3Met;
	}

	const isValidVerifyPassword = () => {
		return inputNewPasswordVerify === inputNewPassword;
	}

	const sendSignUpRequest = () => {
		return fetch('/signup', {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify({
				email: inputEmail,
				password: inputNewPassword,
				verifyPassword: inputNewPasswordVerify
			})
		})
		.then(response => {
			httpStatusCode = response.status;
		});
	}
</script>


<main>
  
  <div class="app-container">
	<!-- 헤더 -->
	<div class="header">
	  <div class="logo">
		<h1>Moleugo</h1>
	  </div>
	</div>
  
	<!-- 회원가입 폼 -->
	<div class="form-container">
	  <h1>회원가입</h1>
	  <p> 알고리즘 시각화 사이트 '모르고'</p>
  
	  <form on:submit|preventDefault={signUp}>
		<!-- 이메일 입력 -->
		<div class="input-field">
		  <label for="email">이메일</label>

			<input
				id="email"
				type="email"
				placeholder="example@moleugo.com"
				bind:value={inputEmail}
				on:input={() => validateEmail(inputEmail)}          	
			/>

			{#if emailErrorMessage}
			  <p class="error-message">{emailErrorMessage}</p>
			{/if}

		</div>
  
		<!-- 비밀번호 입력 -->
		<div class="input-field">
		  <label for="password">비밀번호</label>

		  <div class="password-wrapper">

			{#if isNewPasswordVisible}
			<input
				type="text"
				placeholder="비밀번호를 입력하세요"
				bind:value={inputNewPassword}
				on:input={() => validateNewPassword(inputNewPassword)}
			/>
			{:else}
				<input
					id="password"
					type="password"
					placeholder="비밀번호를 입력하세요"
					bind:value={inputNewPassword}
					on:input={() => validateNewPassword(inputNewPassword)}
				/>
			{/if}

			<button 
				type="button" 
				class="toggle-password-btn" 
				on:click={toggleNewPasswordVisibility}>
				<ion-icon name={isNewPasswordVisible ? "eye-off-outline" : "eye-outline"}></ion-icon>
			</button>
		  </div>

		  <ul class="requirements">
            <li style="color: {condition1Met ? '#238636' : '#9198a1'};">
               영문/숫자/특수문자 중, 2가지 이상 포함
            </li>
            <li style="color: {condition2Met ? '#238636' : '#9198a1'};">
               8자 이상 32자 이하 입력 (공백 제외)
            </li>
            <li style="color: {condition3Met ? '#238636' : '#9198a1'};">
               연속 3자 이상 동일한 문자/숫자 제외
            </li>
          </ul>
		</div>
  
		<!-- 비밀번호 확인 입력 -->
		<div class="input-field">
			<label for="confirm-password">비밀번호 확인</label>
		  
			<div class="password-wrapper">
			  {#if isNewPasswordVerifyVisible}
			  <input
				id="confirm-password"
				type="text"
				placeholder="비밀번호를 다시 입력하세요"
				bind:value={inputNewPasswordVerify}
			  />
			  {:else}
			  <input
				id="confirm-password"
				type="password"
				placeholder="비밀번호를 다시 입력하세요"
				bind:value={inputNewPasswordVerify}
			  />
			  {/if}
		  
			  <button 
				type="button" 
				class="toggle-password-btn2" 
				on:click={toggleNewPasswordVerifyVisibility}>
				<ion-icon name={isNewPasswordVerifyVisible ? "eye-off-outline" : "eye-outline"}></ion-icon>
			  </button>
			</div>
		  
			<p class="hint" style={verifyPasswordMessageStyle}>
			  {verifyPasswordMessage}
			</p>
		  </div>
  
		<!-- 가입 버튼 -->
		<button type="submit" class="submit-button">
			가입하기
		</button>

	  </form>
  
	  <!-- 약관 -->
	  <p class="terms">
		해당 계정을 통한 이용은 ... 제공됩니다. <br />
		개인 정보 제공 및 처리에 동의합니다.
	  </p>

	  <div class="divider"></div>

	  <div id="google-login">
		<img src="https://img.icons8.com/color/200/google-logo.png" alt="Google Login" />
	  </div>

	</div>
  
	<!-- 하단 소셜 로그인 -->
	<div class="footer">
	</div>

  </div>
</main>

<style>

	/* 전체 컨테이너 스타일 */
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
  
	/* 헤더 스타일 */
	.header {
	  margin-bottom: 20px;
	}
	.logo {
	  font-size: 2rem;
	}
  
	/* 폼 컨테이너 스타일 */
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
  
	form {
	  align-items: center;	
	  display: flex;
	  flex-direction: column;
	  gap: 20px;
	  margin: 50px 0px;
	  margin-bottom: 10px;
	  
	}
  
	.input-field {
	  display: flex;
	  flex-direction: column;
	  gap: 4px;
	  position: relative;
	}

	.password-wrapper {
	  position: relative; /* 부모 요소를 기준으로 자식 위치 지정 */
	}

	
  
	label {
	  text-align: left;
	  font-size: 1rem;
	  color: #ffffff;
	}
  
	input {
	  background-color: #414141;
	  padding: 10px;
	  border: 1px solid #292929;
	  border-radius: 6px;
	  font-size: 1rem;
	  width: 300px;
	  height: 20px;
	  color: #ffffff;
	}
  
	input:focus {
	  outline: none;
	  border-color: #ffffff;
	}
  
	.hint {
	  font-size: 0.8rem;
	  color: #888;
	  text-align: left;
	  margin-top: 4px;
	}
  
	.submit-button {
	  padding: 12px;
	  background-color: #00a000;
	  color: #fff;
	  border: none;
	  border-radius: 4px;
	  cursor: pointer;
	  font-size: 1rem;
	  font-weight: bold;
	  width: 322px;
	  margin-top: 15px;
	}
  
	.submit-button:hover {
	  background-color: #007d00;
	}
  
	.terms {
	  font-size: 0.9rem;
	  color: #555;
	  margin-top: 10px;
	  line-height: 1.4;
	}

    .divider {
        margin: 25px 0px;
        border-top: 1px solid #444;
        position: relative;
        color: #9e9e9e;
    }

    .divider:before {
        content: "간편 회원가입";
        position: absolute;
        top: -13px;
        left: 50%;
        transform: translateX(-50%);
        background-color: #151515;
        padding: 0 10px;
    }
  
	/* 하단 소셜 로그인 스타일 */
	.footer {
	  display: flex;
	  gap: 8px;
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

	#google-login img {
        width: 24px;
        height: 24px;
    }

	.requirements {
		margin-top: 10px;
		text-align: left;
		font-size: 0.85rem;
		line-height: 1.5;
		padding-left: 20px;
	}

	.error-message {
		margin-top: 5px;
		font-size: 0.85rem;
		color: rgb(173, 44, 44);
		text-align: left;
	}

	.toggle-password-btn,
	.toggle-password-btn2 {
		position: absolute;
		top: 55%; /* 입력 필드 높이의 중간 */
		right: 5px; /* 입력 필드의 오른쪽 끝에서 약간 떨어진 위치 */
		transform: translateY(-50%); /* 수직 중앙 정렬 */
		background: none;
		border: none;
		cursor: pointer;
		font-size: 1.2rem;
		color: #bbbbbb;
	}

  .toggle-password-btn:hover,   
  .toggle-password-btn2:hover {
    color: #9c9c9c;
  }

  @media (min-width: 2560px) {

	.header {
	  margin-bottom: 20px;
	  margin-top: 130px;
	}

  }

</style>
  