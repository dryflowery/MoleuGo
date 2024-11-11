<main>
	<body>
    
		<div class="box">
			<br>
			<h2>MoleuGo</h2>
			<br><br>

			<h2>비밀번호 찾기</h2>
			<h3>가입한 이메일을 입력해 주세요</h3>
			<h3>이메일을 통해 비밀번호 재설정 메일을 보내드립니다.</h3>	
			
			<br><br><br>

			<div class="container">
				<div class="submit-text" style="left: 120px;">이메일</div> 
				<input type="email" class="input-box" placeholder="example@MoleuGo.com">
				<button class="submit-button">인증</button>
			</div>
			
			<br>

			<div class="container">
				<button class="submit-button" style="width: 18vw;">비밀번호 재설정</button>
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
		font-size: 1.1rem;
		background-color: #00ac4d; 
		color: #fff;  
		border-radius: 10px;
		cursor: pointer;
		border: none;
	}

</style>