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
    let savedUserName = ""; // 저장된 이름

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

    let isvisualableNickNameSetting = true;
    

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


  </script>
  
<main>
  <div class="header-container">
    <Header />
  </div>
  
  <div class="main-container" style="transform: translateX({$isListVisible ? 360 : 155}px); margin-right: {$isListVisible ? 360: 155}px; "> <!--알고리즘 리스트 유무에 따른 위치,크기 조절-->
     
    <div class="content">
        
      <div id="content-box">

        <div id="left-container">

          <div id="user-profile-container">
            <div id="profile-image">
              <button id="profile-edit-Btn">설정</button>
            </div>
            <div id="nickName"> 닉네임: {usedNames} </div>
          </div>

          <div class="user-imformation-container">

          </div>
        
        </div><!--left-container 끝-->

        <div id="right-container">
        </div>

      </div> <!--content-box 끝-->
    </div> <!--content 끝-->
  </div> <!--main-container 끝-->
  
  <div class="footer-container">
    <Footer />
  </div>
</main>
  
<style>
  
  main {
    align-items: center;
    transition: transform 0.5s ease, margin 0.5s ease;
    
  }

 .main-container {
   color: white;
   transition: transform 0.5s ease, margin 0.5s ease;
   justify-content: center;
  }
  
 .content {
   column-gap: 35px;
   row-gap: 6px;
  }

  /* -------------------큰 틀------------------ */
 #content-box {
   display: grid;
   grid-template-columns: 0.35fr 0.65fr;
   margin: 0px 0px 0px 200px;
   width: 1200px;
   height: 770px;
   background-color: #151c23;
   border: 3px solid #3d444d;
   border-radius: 8px;
   box-sizing: border-box;
  }

  #left-container {
   display:grid;
   grid-template-rows: 0.55fr 1fr;
   border-right: 3px solid #3d444d;
   background-color: #12181d;

   border-top-left-radius: 8px;
   border-bottom-left-radius: 8px;
  }

  #right-container {
    display:grid;
    grid-template-rows: 0.8fr 1fr;
  }

  /* -------------------큰 틀------------------ */

  /* -------------------프로필 칸------------------ */

  #user-profile-container {
    border-bottom: 3px solid #3d444d;
  }

  #profile-image {
   width: 175px;
   height: 175px;
   border-radius: 50%;
   border: 4px solid #2e2e2e;
   background-color: rgb(88, 88, 88);
   margin: 20px 0px 0px 110px;
   
  }

  #profile-edit-Btn {
   position: relative;
   background-color: #434343;
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
    color: #9e9e9e;
    margin: 15px 0px 0px 135px;
  }
  /* -------------------프로필 칸------------------ */

  /* -------------------정보 수정 칸------------------ */
  #password-btn {
    margin-top: 17px;
    margin-left: 150px;
  }

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
  