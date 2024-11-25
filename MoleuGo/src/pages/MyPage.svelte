<script>
    import Footer from "../component/Footer.svelte";
    import Header from "../component/Header.svelte";
    import {isListVisible } from "../lib/store"
    import {isLogin } from "../lib/store"
    import { push } from "svelte-spa-router";

    let userName = "";
    let isLengthValid = false;
    let isSpecialCharValid = false;
    let showResult = false; // 결과 메시지 표시 여부
    let saveMessage = ""; // 저장 메세지
    let savedUserName = "컴붕이2"; // 저장된 이름

    let usedNames = ["컴붕이1"]; // 이미 존재하는 닉네임들 (임의로 추가)

    let userPassword = "comBung@369"; //기존 비밀번호

    let inputOldPassword = ""; // 기존 비밀번호 입력값
    let isOldPasswordVisible = false; // 기존 비밀번호 표시 상태

    let inputNewPassword = ""; // 새 비밀번호 입력값
    let isNewPasswordVisible = false; // 새 비밀번호 표시 상태

    let inputNewPassword_verify = ""; // 새 비밀번호 확인용 입력값
    let isNewPasswordVisible_verify = false; // 새 비밀번호 확인용 표시 상태

    let passwordChangeMessage = ""; // 비밀번호 변경 결과 메시지
    let oldPasswordStatusColor = "";
    let oldPasswordIcon = "checkmark-outline";

    let oldPasswordStatus = "";

    let condition1Met = false; // 영문/숫자/특수문자 중 2가지 이상 포함
    let condition2Met = false; // 8자 이상 32자 이하 입력 (공백 제외)
    let condition3Met = false; // 연속 3자 이상 동일한 문자/숫자 제외
    
    let verifyPasswordMessage = "확인을 위해 새 비밀번호를 다시 입력하세요"; // 확인 메시지
    let verifyPasswordMessageStyle = ""; // 메시지 스타일

    let ispasswordCheckIcon = false ;// 비밀번호 설정 옆 아이콘 유/무

    let myEmail = "email@gmail.com";

    let isVisualInformation = true;
    

    function checkOldPassword() {
        if (inputOldPassword === userPassword) {
            oldPasswordStatus = "비밀번호가 일치합니다";
            oldPasswordStatusColor = "#238636";
            oldPasswordIcon = "checkmark-outline";
            saveNewPassword(); // 비밀번호가 일치하면 새 비밀번호 저장 함수 호출
        } else {
            oldPasswordStatus = "비밀번호가 일치하지 않습니다";
            oldPasswordStatusColor = "rgb(173, 44, 44)";
            oldPasswordIcon = "close-outline";
        }
    }

    // 비밀번호 조건 확인 함수
    function validateNewPassword(password) {
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

      // 모든 조건 충족 여부 반환
      return condition1Met && condition2Met && condition3Met;
    }

      // 비밀번호 변경 버튼 클릭
      function saveNewPassword() {
        // 초기화
        passwordChangeMessage = "";

        // 기존 비밀번호 확인
        if (inputOldPassword !== userPassword) {
          return;
        }

        // 새 비밀번호 조건 확인
        if (!validateNewPassword(inputNewPassword)) {
          return;
        }

        // 새 비밀번호와 확인 비밀번호 일치 확인
        if (inputNewPassword !== inputNewPassword_verify) {
          return;
        }

        // 모든 조건 충족 시 비밀번호 변경
        userPassword = inputNewPassword;
        alert("새 비밀번호가 성공적으로 저장되었습니다.");
        ispasswordCheckIcon = true;
        
        // 입력값 초기화
        inputOldPassword = "";
        inputNewPassword = "";
        inputNewPassword_verify = "";
    }

    // 닉네임 검증 함수
    function validateUserName(name) {
        const lengthRegex = /^.{2,10}$/; // 2~10 글자
        const specialCharRegex = /^[^\s!@#$%^&*(),.?":{}|<>]*$/; // 띄어쓰기 및 특수문자 제외

        isLengthValid = lengthRegex.test(name);
        isSpecialCharValid = specialCharRegex.test(name);
    }

    // 닉네임 저장 버튼 클릭 이벤트
    function saveUserName() {
      showResult = true; // 결과 메시지 표시
      saveMessage = " "; // 기본값 설정

      if (isLengthValid && isSpecialCharValid) {
        if (usedNames.includes(userName)) {
          saveMessage = "중복된 닉네임입니다. 다시 설정해주세요.";
          } else {
            savedUserName = userName; // 저장된 닉네임 업데이트
            usedNames.push(userName); // 새 닉네임 추가
            saveMessage = "중복확인 닉네임 변경 완료";
          }
      } else if (!usedNames.includes(userName)) {
        saveMessage = "조건을 충족 해주세요";
      }
    }

    function toggleOldPasswordVisibility() { // OLD
        isOldPasswordVisible = !isOldPasswordVisible;
    }

    function toggleNewPasswordVisibility() { // NEW
        isNewPasswordVisible = !isNewPasswordVisible;
    }

    function toggleNewPasswordVisibility_verify() { // NEW
      isNewPasswordVisible_verify = !isNewPasswordVisible_verify;
    }

    // 닉네임이 변경될 때마다 검증
    $: validateUserName(userName);

    // 비밀번호 변경 시 조건 확인
    $: validateNewPassword(inputNewPassword);
      // 비밀번호 확인 일치 여부 검사
      $: {
      if (inputNewPassword_verify === "") {
        verifyPasswordMessage = "확인을 위해 새 비밀번호를 다시 입력하세요";
        verifyPasswordMessageStyle = ""; // 기본 스타일
      } else if (inputNewPassword_verify === inputNewPassword) {
        verifyPasswordMessage = "비밀번호가 일치합니다";
        verifyPasswordMessageStyle = "color: #238636;"; // 초록색
      } else {
        verifyPasswordMessage = "비밀번호가 일치하지 않습니다";
        verifyPasswordMessageStyle = "color: rgb(173, 44, 44);"; // 빨간색
      }
    }

    let showSettingBox = true; // 기본적으로 setting-box 표시
    let currentSetting = null; // 현재 설정 중인 항목 (null은 아무 것도 설정하지 않음)


  </script>
  
<main>
  <div class="header-container">
    <Header />
  </div>
  
  <div class="main-container" style="transform: translateX({$isListVisible ? 360 : 155}px); margin-right: {$isListVisible ? 360: 155}px; "> <!--알고리즘 리스트 유무에 따른 위치,크기 조절-->
     
    <div class="content">
        
      <div id="profile-box">
        <div id="profile-top-container">
          <span id='profile-title'>내 프로필</span>
          <div id="profile-image">
            <button id="profile-edit-Btn">설정</button>
          </div>
          <div id='nickName'>닉네임: {savedUserName} </div>
        </div>

        <div id="profile-bottom-container">

          <span id='setting-title'>기본 정보</span>

          {#if showSettingBox}
            <div id="setting-box">
              <div id="email-setting">
                <div id="email-title"> 이메일 </div>
                <div id="email-icon"><ion-icon name="information-circle-outline"></ion-icon></div>
                <div id="email-output"> {myEmail} </div>
                <button id="email-btn" on:click={() => { showSettingBox = false; currentSetting = 'email'; }}>
                  설정
                </button>
              </div>
              <div id="nickname-setting"> 
                <div id="nickname-title"> 닉네임 </div>
                <div id="nickname-output"> {savedUserName} </div>
                <button id="nickname-btn" on:click={() => { showSettingBox = false; currentSetting = 'nickname'; }}>
                  설정
                </button>
              </div>
              <div id="password-setting">
                <div id="password-title"> 비밀번호</div>
                <div id="password-output"> ****** </div>
                <button id="password-btn" on:click={() => { showSettingBox = false; currentSetting = 'password'; }}>
                  설정
                </button>
              </div>
            </div><!--setting-box 끝-->

            {:else if currentSetting === 'email'}
              <div class="email-settings">
                
                
                <button on:click={() => { showSettingBox = true; currentSetting = null; }}>완료</button>
              </div>
            {:else if currentSetting === 'nickname'}
              <div class="nickname-settings">
                <div id='change-userName-container'>
                    <div id="change-userName-Title">
                      <t style="color: #bbbbbb;">닉네임: {savedUserName || "컴붕이1"} </t>
                      {#if saveMessage == '중복된 닉네임입니다. 다시 설정해주세요.'}
                        <ion-icon name="warning-outline" style="color: yellow;"></ion-icon>
                      {:else if saveMessage == "중복확인 닉네임 변경 완료"}
                        <ion-icon name="checkmark-outline" style="color: green;" ></ion-icon>
                      {/if}
                    </div>
  
                    <div id="change-userName">
                      <input type="text" placeholder="닉네임 변경 ..." bind:value="{userName}">
                      <button id="userName-edit-Btn" on:click={saveUserName}>변경</button>
                    </div>
  
                    <div id="verify-userName-container">
  
                      <div id="Requirements_1" 
                          class:valid={isLengthValid}  
                          class:invalid={!isLengthValid}> 
                          <ion-icon name="checkmark-outline"></ion-icon> 
                          2 ~ 10 글자 사이의 닉네임을 정해주세요
                      </div>
  
                      <div id="Requirements_2" 
                          class:valid={isSpecialCharValid}
                          class:invalid={!isSpecialCharValid}> 
                          <ion-icon name="checkmark-outline"></ion-icon> 
                          띄어쓰기를 포함한 특수문자 X
                      </div>
  
                      {#if showResult}
                        <div id="Requirements_3" 
                          class:success={saveMessage === "중복확인 닉네임 변경 완료"} 
                          class:error={saveMessage === "중복된 닉네임입니다. 다시 설정해주세요." || saveMessage === "조건을 충족 해주세요"}>
                          <ion-icon name="close"></ion-icon>
                          {saveMessage}
                        </div>
                      {/if}
                    </div>
                </div>
                <button on:click={() => { showSettingBox = true; currentSetting = null; }}>완료</button>
              </div>
            {:else if currentSetting === 'password'}
              <div class="password-settings">
                
                
                <button on:click={() => { showSettingBox = true; currentSetting = null; }}>완료</button>
              </div>
          {/if}
        </div>

      </div> <!--profile-box 끝-->

      <div id="information-box">

      </div> <!--information-box 끝-->


    </div> <!--content 끝-->
  </div> <!--main-container 끝-->
  
  <div class="footer-container">
    <Footer />
  </div>
</main>
  
<style>
  
  main {
    align-items: center;
    
  }

 .main-container {
   color: white;
   transition: transform 0.5s ease, margin 0.5s ease;
   justify-content: center;
  }
  
 .content {
   display:grid;
   grid-template-columns: 620px 1fr;
  }

  /* -------------------큰 틀------------------ */
 #profile-box {
   display: grid;
   grid-template-rows: 270px 1fr;
   margin: 0px 0px 0px 200px;
   padding: 20px 0px 0px 0px;
   width: 400px;
   height: 60vh;
   background-color: #151b23;
   border: 1px solid #3d444d;
   border-radius: 8px;
   box-sizing: border-box;
  }

  #information-box {
   display: grid;
   grid-template-rows: 0.55fr 1fr;
   margin: 0px 0px 0px 0px;
   width: 800px;
   height: 770px;
   background-color: #151b23;
   border: 1px solid #3d444d;
   border-radius: 8px;
   box-sizing: border-box;
  }

  /* -------------------큰 틀------------------ */

  /* -------------------프로필 칸------------------ */

  #profile-top-container {
    border-bottom: 1px solid #3d444d;
  }

  #profile-title {
    font-weight: bold;
    font-size: 18px;
    color: #d1d1d1;
    margin-left: 20px;
  }

  #profile-image {
   width: 175px;
   height: 175px;
   border-radius: 15%;
   border: 1px solid #949494;
   background-color: rgb(88, 88, 88);
   margin: 10px 0px 0px 110px;
   
  }

  #profile-edit-Btn {
   position: relative;
   background-color: #3f3f3f;
   color: white;
   padding: 8px 16px;
   border: 0.5px solid #888888;
   font-size: 13px;
   font-weight: bold;
   border-radius: 7px;
   cursor: pointer;
   box-shadow: 0 2px 5px rgba(0, 0, 0, 0.3); /* Subtle shadow */
   margin: 120px 0px 0px 130px;
   width: 70px;
  }
  
  #nickName {
    font-weight: bold;
    font-size: large;
    color: #c4c4c4;
    margin: 15px 0px 0px 132px;
  }


  /* -------------------프로필 칸------------------ */

  /* -------------------정보 수정 칸------------------ */

  #profile-bottom-container {
    padding: 20px 0px 0px 20px;
  }

  #setting-title {
    font-weight: bold;
    font-size: 18px;
    color: #d1d1d1;
  }

  #setting-box {
    margin-top: 30px;
    display: flex;
    gap: 0.5rem;
    flex-direction: column;
  }

  #email-setting, #nickname-setting, #password-setting {
  display: flex;
  flex-direction: row;
  align-items: center;
  }

  #nickname-title, #password-title {
    padding: 10px 0;
    flex: 0 0 105px;
    color: #c7c7c7;
  }

  #email-title {
    padding: 10px 0;
    flex: 0 0 55px;
    color: #c7c7c7;
  }

  #email-icon {
    flex: 0 0 50px;
  }

  #password-output, #nickname-output, #email-output {
    flex: 0 0 180px;
  }

  #email-btn, #nickname-btn, #password-btn {
   position: relative;
   background-color: #151c23;
   border: 1px solid #3d444d;
   color: white;
   padding: 5px 10px;
   font-size: 14px;
   border-radius: 7px;
   cursor: pointer;
   margin: 0px 0px 0px 6px;
   width: 70px;
   height: 33px;
  }

  #email-btn:hover, #nickname-btn:hover, #password-btn:hover {
   background-color: #11171d;
  }

  /*
  #password-btn {
    margin-top: 17px;
    margin-left: 150px;
  }
  */

  #password-save-Btn {
   position: relative;
   background-color: #3f862d;
   color: white;
   padding: 5px 10px;
   font-size: 14px;
   font-weight: bold;
   border-radius: 7px;
   cursor: pointer;
   box-shadow: 0 2px 5px rgba(0, 0, 0, 0.3); /* Subtle shadow */
   margin: 0px 0px 0px 6px;
   width: 70px;
   height: 30px;
  }

  #password-search-Btn {
   position: relative;
   background-color: #3f862d;
   color: white;
   padding: 5px 10px;
   font-size: 14px;
   font-weight: bold;
   border-radius: 7px;
   cursor: pointer;
   box-shadow: 0 2px 5px rgba(0, 0, 0, 0.3); /* Subtle shadow */
   margin: 0px 0px 0px 6px;
   width: 120px;
   height: 30px;
  }

  #password-save-Btn:hover {
    background-color: #1c682b;
  }

  #password-search-Btn:hover {
    background-color: #1c682b;
  }

  #profile-edit-Btn:hover {
    background-color: #2c2c2c;
  }

  #change-userName {
    display: flex;
  }

  #change-userName-Title {
    margin: 16px 0px 10px 35px;
    font-weight: bold;
  }

  #change-password-container {
    
  }

  #change-password-Title {
    margin: 0px 0px 10px 35px;
    font-weight: bold;
  }

  #change-userName input[type="text"] {
    padding: 6px;
    margin: 0px 0px 0px 30px;
    border-radius: 7px;
    border: 1px solid #3d444d;
    width: 230px;
    height: 21px;
    background-color: #151b23;
    font-size: 0.9rem;
    color: #FFFFFF;
  }

  .password-container, .password-container_2, .password-container_3 {
    position: relative;
    display: inline-block;
    width: 100%;
  }

  .password-container input {
    padding: 6px;
    margin: 0px 0px 0px 30px;
    border-radius: 7px;
    border: 1px solid #3d444d;
    width: 310px;
    height: 21px;
    background-color: #151b23;
    font-size: 0.9rem;
    color: #FFFFFF;
  }

  .password-container_2 input, .password-container_3 input {
    padding: 6px;
    margin: 0px 0px 0px 30px;
    border-radius: 7px;
    border: 1px solid #3d444d;
    width: 310px;
    height: 21px;
    background-color: #152321;
    font-size: 0.9rem;
    color: #FFFFFF;
  }

  .toggle-password-btn,
  .toggle-password-btn_2 {
      position: absolute;
      top: 57%;
      right: 25px;
      transform: translateY(-50%);
      background: none;
      border: none;
      cursor: pointer;
      font-size: 1.2rem;
      color: #bbbbbb;
      margin-right: 23px;
  }

  .toggle-password-btn:hover {
    color: #424242; /* 버튼 호버 시 색상 변경 */
  }

  #userName-edit-Btn {
   position: relative;
   background-color: #434343;
   color: white;
   border: 1px solid #888888;
   padding: 5px 10px;
   font-size: 14px;
   font-weight: bold;
   border-radius: 7px;
   cursor: pointer;
   box-shadow: 0 2px 5px rgba(0, 0, 0, 0.3); /* Subtle shadow */
   margin: 0px 0px 0px 6px;
   width: 75px;
   height: 34px;
  }

  #Requirements_password_1 {
    margin: 10px 0px 0px 30px;
    font-size: 14px;
    color: #9198a1;
  }

  #Requirements_password_2 {
    margin: 10px 0px 0px 30px;
    font-size: 14px;
    color: #9198a1;
  }

  #Requirements_password_3 {
    margin: 10px 0px 0px 30px;
    font-size: 14px;
    color: #9198a1;
  }

  #Requirements_password_4 {
    margin: 10px 0px 0px 30px;
    font-size: 14px;
    color: #9198a1;
  }

  #Requirements_password_5 {
    margin: 10px 0px 0px 30px;
    font-size: 14px;
    color: #9198a1;
  }

  #Requirements_password_6 {
    margin: 5px 0px 0px 30px;
    font-size: 14px;
    color: #9198a1;
  }

  #userName-edit-Btn:hover {
    background-color: #1c682b;
  }

  #verify-userName-container {
    margin: 15px 0px 0px 30px;
    font-size: 14px;
    color: #9198a1;
  }

  #Requirements_1 {
    margin-bottom: 5px;
  }

  #Requirements_2 {
    margin-bottom: 5px;
  }
    
  #lawn-container {
   columns: 2;
      
  }

  #verify-userName-container div.valid {
    color: #238636; /* 조건을 만족하면 초록색 */
  }

  #verify-userName-container div.invalid {
    color: #9c9c9c; /* 조건을 만족하지 않으면 빨간색 */
  }

  /* 유효한 경우 초록색 */
  .success {
  color: #238636;
  }

  /* 중복된 닉네임인 경우 빨간색 */
  .error {
  color: rgb(173, 44, 44);
  }

  

</style>
  