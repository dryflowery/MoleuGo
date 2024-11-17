<main>
	<body>
    
		<div class="box">
			<h1>MoleuGo</h1>
			<h3>알고리즘 시각화 '모르고' </h3>
			
			<div style="padding: 10px;"></div>

			<h2>회원가입</h2>

			<div style="padding: 25px;"></div>
	
			<div class="container">
				<div class="submit-text">이메일</div> 
				<input type="email" class="input-box" placeholder="example@MoleuGo.com">
				<button class="submit-button" style="margin-left: 15px;">인증</button>
			</div>
			
			<div style="padding: 10px;"></div>

			<div class="container" style="padding-left: 5px;">
				<div class="submit-text" style="top: -5px; padding-left: 5px;">이메일 인증 번호</div>
				<div class="square-container">
					<input type="text" class="square-box" maxlength="1" />
    				<input type="text" class="square-box" maxlength="1" />
    				<input type="text" class="square-box" maxlength="1" />
    				<input type="text" class="square-box" maxlength="1" />
    				<input type="text" class="square-box" maxlength="1" />
					
					<button class="submit-button" style="height: 46px;">확인</button>
				</div>

			</div>
			
			<div style="padding: 30px;"></div>

			<div>
				<div class="container" >
					<div class="submit-text">닉네임</div> 
					<input type="email" class="input-box" placeholder="모르고" style="width: 19vw;">
				</div>
			</div>

			<div style="padding: 20px;"></div>

			<div>
				<div class="container" >
					<div class="submit-text">비밀번호</div> 
					<input type="email" class="input-box" placeholder="********" style="width: 19vw;">

					<div class="under-text">
						<h3>영문/숫자/특수문자 중, 2가지 이상 포함</h3>
						<h3>8자 이상 32자 이하 입력(공백 제외)</h3>
						<h3>연속 3자 이상 동일한 문자/숫자 제외</h3>
					</div>

				</div>
				
			</div>
			
			<div style="padding: 65px;"></div>

			<div>
				<div class="container" >
					<div class="submit-text">비밀번호 확인</div> 
					<input type="email" class="input-box" placeholder="********" style="width: 19vw;">
				</div>
			</div>

			<div style="padding: 10px;"></div>

			<div class="container">
				<button class="submit-button" style="width: 20vw;">회원가입</button>
				
				<div class="under-text">
					<h3>회원가입시 유의사항</h3>
				</div>
			</div>

			<div style="padding: 20px;"></div>

			<div class="line-container">
				<div class="line"></div>
				<div class="or-text">간편 가입</div>
				<div class="line"></div>
			</div>

			<div id="social-login-container">
                <div id="google-login">
                    <img src="https://img.icons8.com/color/200/google-logo.png" alt="Google Login" />
                </div>
            </div>

		</div>
	</body>
</main>

<style>
	body {
		margin: 0;
		padding: 0;
		width: 100vw;
		height: 100vh;
		background-color: #0000;  
	}

	.box {
		width: 35vw;  
		height: 98%; 
		background-color: #101010;
		border-radius: 20px; 
		position: absolute;   
		top: 50%;              
		left: 50%;             
		transform: translate(-50%, -50%);  
		display: flex;         
		flex-direction: column;  
		align-items: center;      
		text-align: center;   
	}

	h1, h2, h3 {
		margin: 0.3vw;  
		padding: 0; 
		color: #fff; 
	}

	h3 {
		font-size: 0.8rem;  
		color: #424141;   
	}

	.input-box {
        padding: 5px;
        font-size: 1.2rem;
        width: 15.5vw;
        border: 2px solid #131b1a;
        border-radius: 10px;
        background-color: #131b1a;
        color: #fff;
        height: 30px; 
    }

	.submit-button {
        font-size: 1.2rem; 
        background-color: #086c42;
        color: #fff;
        border-radius: 10px;
        cursor: pointer;
        border: none;
        height: 44px; 
        padding: 0 10px; 
    }

	.submit-button:hover {
		background-color: #444; 
	}

	.container {
		display: flex; 
		justify-content: flex-start;
		align-items: center;
		position: relative; 
	}


	.submit-text {
		font-size: 13px;  
		color: #fff;
		margin: 0;
		position: absolute;
		left: 1px;; 
		top: -25px; 
	}

	.under-text{
		position: absolute;
		left: 0px;
		top: 40px;
		text-align: left;
	}

	.square-container {
		display: flex;              
		justify-content: space-between;  
		width: 100%;                
		margin-top: 20px;           
	}

	.square-box {
		width: 50px;  
		height: 45px; 
		background-color: #131b1a; 
		border-radius: 5px; 
		margin-right: 10px;
		border: none;
	}

	.line-container {
		display: flex;
		align-items: center;
		justify-content: center;
		width: 100%;
		margin: 20px 0;
	}

	.line {
		flex: 1;
		height: 1px;
		background-color: #302e2e;
	}

	.or-text {
		margin: 0 10px;
		font-size: 0.6rem;
		color: #fff;
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