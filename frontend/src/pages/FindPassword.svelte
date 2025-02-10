<main>
	<body>
    
		<div class="box">
			<br>
			<h1>MoleuGo</h1>

			<h2>비밀번호 찾기</h2>
			<h3>가입한 이메일을 입력해 주세요. <br>
				이메일을 통해 비밀번호 재설정 메일을 보내드립니다.</h3>
			
			<br><br><br>

			<div class="container">
				<div class="submit-text">이메일</div> 
				<input type="email" class="input-box" placeholder="example@MoleuGo.com">
				<button class="submit-button" style="margin-left: 10px;">인증</button>
			</div>
			
			<br>

			<div class="container">
				<button class="submit-button" style="width: 19.8vw;">비밀번호 재설정</button>
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

	h1, h2{
		padding: 0; 
		color: #fff; 
	}

	h3 {
		font-size: 0.8rem;  
		color: #424141;   
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

	.submit-button:hover {
		background-color: #444; 
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

</style>