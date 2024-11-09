<main>
	<body>
    
		<div class="box">
			<h2>MoleuGo</h2>
			<h3>알고리즘 시각화 '모르고'</h3>
			<br>
	
			<h2>회원가입</h2>
			<br><br><br>
	
			<div class="container">
				<div class="submit-text">이메일</div> 
				<input type="email" class="input-box" placeholder="example@MoleuGo.com">
				<button class="submit-button">인증</button>
			</div>
			
			<br>
			<div class="container">
				<div class="submit-text" style="top: -5px;">이메일 인증 번호</div>
				<div class="square-container">
					<input type="text" class="square-box" maxlength="1" />
    				<input type="text" class="square-box" maxlength="1" />
    				<input type="text" class="square-box" maxlength="1" />
    				<input type="text" class="square-box" maxlength="1" />
    				<input type="text" class="square-box" maxlength="1" />
					
					<button class="submit-button" style="height: 40px;">확인</button>
				</div>

			</div>
			
			<br><br><br><br>
			<div>
				<div class="container" >
					<div class="submit-text">닉네임</div> 
					<input type="email" class="input-box" placeholder="모르고" style="width: 18vw;">
				</div>
			</div>
	
			<br>
			<div>
				<div class="container" >
					<div class="submit-text">비밀번호</div> 
					<input type="email" class="input-box" placeholder="********" style="width: 18vw;">

					<div class="under-text">
						<h3>영문/숫자/특수문자 중, 2가지 이상 포함</h3>
						<h3>8자 이상 32자 이하 입력(공백 제외)</h3>
						<h3>연속 3자 이상 동일한 문자/숫자 제외</h3>
					</div>

				</div>
				
			</div>
			<br><br><br><br><br><br>
	
			<div>
				<div class="container" >
					<div class="submit-text">비밀번호 확인</div> 
					<input type="email" class="input-box" placeholder="********" style="width: 18vw;">
				</div>
			</div>

			<div class="container">
				<button class="submit-button" style="width: 18vw;">회원가입</button>
				<div class="under-text">
					<h3>회원가입시 유의사항</h3>
				</div>
			</div>

			<br><br>

			<div class="line-container">
				<div class="line"></div>
				<div class="or-text">간편회원가입</div>
				<div class="line"></div>
			</div>

			<div>

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
		background-color: #000000;  
	}

	.box {
		width: 30vw;  
		height: 100%; 
		background-color: #1b1a1a; 
		border-radius: 20px; 
		position: absolute;   
		top: 50%;              
		left: 50%;             
		transform: translate(-50%, -50%);  
		display: flex;         
		flex-direction: column;  
		justify-content: center;  
		align-items: center;      
		text-align: center;   
	}

	h2, h3 {
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
		width: 15vw;  
		border: 2px solid #333;  
		border-radius: 10px;
		margin-right: 10px;
		background-color: #333;  
		color: #fff;  
	}


	.submit-button {
		padding: 5px 10px;
		font-size: 1.2rem;
		background-color: #00ac4d; 
		color: #fff;  
		border-radius: 10px;
		cursor: pointer;
		border: none;
	}

	.submit-button:hover {
		background-color: #444; 
	}

	.container {
		display: flex; 
		justify-content: center;
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
		background-color: #333; 
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
		font-size: 0.5rem;
		color: #fff;
	}
</style>