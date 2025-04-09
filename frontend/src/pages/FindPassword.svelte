<script>
	import {push} from "svelte-spa-router";

	let findPasswordHttpStatusCode;
	let inputEmail = "";
	let emailErrorMessage = "";
	let changeToVerifyEmailPage = false;

	// 이메일 유효성 검사
	const validateEmail = (email) => {
		const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

		if (!emailRegex.test(email)) {
			emailErrorMessage = "이메일 형식이 올바르지 않습니다.";
		} else {
			emailErrorMessage = "";
		}
	};

	const isValidEmail = () => {
		return inputEmail !== "" && emailErrorMessage === "";
	};

	const sendVerifyEmailRequest = () => {
		return fetch('/user/find-password/verify-email', {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify({
				email: inputEmail,
			})
		});
	};

	const findPassword = () => {
		if(!isValidEmail()) {
			return;
		}

		changeToVerifyEmailPage = true;
		sendVerifyEmailRequest();
	};
</script>


<main>
	<div class="app-container">
		<div class="form-container">
			{#if changeToVerifyEmailPage === false}
				<h1>비밀번호 찾기</h1>
				<p style="font-size: 0.9rem">가입한 이메일을 입력해 주세요.<br>이메일을 통해 비밀번호 재설정 메일을 보내드립니다.</p>

				<form on:submit|preventDefault={findPassword}>
					<!-- 이메일 입력 -->
					<div class="input-field">
						<label for="email">이메일</label>

						<input
								id="sign-up-email-input"
								type="email"
								placeholder="example@moleugo.com"
								bind:value={inputEmail}
								on:input={() => validateEmail(inputEmail)}
						/>

						{#if emailErrorMessage}
							<p class="error-message">{emailErrorMessage}</p>
						{/if}
					</div>

					<!-- 가입 버튼 -->
					<button type="submit" class="submit-button">
						전송하기
					</button>
				</form>
			{:else}
				<h1>인증 메일을 보내드렸어요.</h1><br>
				<img src="assets/mail.png" width="120px" alt="">

				<br><br>
				<p style="color: white; padding-top: 7.5px;">메일함을 확인해주세요.</p>
				<p style="color: white; padding-top: 7.5px;">가입하신 이메일을 인증해주시면,</p>
				<p style="color: white; padding-top: 7.5px;">비밀번호 재설정이 가능해요.</p>

				<button class="return-to-main-button" on:click={() => push('/')}>
					메인화면으로 돌아가기
				</button>
			{/if}
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
		text-align: center;
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
		margin: 50px 0px 10px;
	}

	.input-field {
		display: flex;
		flex-direction: column;
		gap: 4px;
		position: relative;
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
	}

	.submit-button:hover {
		background-color: #007d00;
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

	.error-message {
		margin-top: 5px;
		font-size: 0.85rem;
		color: rgb(173, 44, 44);
		text-align: left;
	}

	@media (min-width: 2560px) {
		.header {
			margin-bottom: 20px;
			margin-top: 130px;
		}
	}
</style>