<script>
    import Footer from "../component/Footer.svelte";
    import Header from "../component/Header.svelte";
    import {isListVisible } from "../lib/store"
    import {isLogin } from "../lib/store"
    import { push } from "svelte-spa-router";
    import { onMount } from "svelte";
    import Template from "./visualization/Template.svelte";

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

    let currentSetting = null; // 현재 설정 상태
    let showSettingBox = true; // 기본적으로 setting-box 표시

    let currentHeight = 525;   // 기본 높이
    let currentHeight_U = 247;   // 기본 높이

    let showEmailInfo = false; // 설명 표시 여부
    

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

        showSettingBox = true;
        currentSetting = null;
        
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
            alert("새 닉네임이 변경되었습니다..");
            showSettingBox = true;
            currentSetting = null;
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

    // 높이와 애니메이션 상태 업데이트  
    $: {
    if (currentSetting === null) currentHeight = 525;
    else if (currentSetting === "email") currentHeight = 490;
    else if (currentSetting === "nickname") currentHeight = 580;
    else if (currentSetting === "password") currentHeight = 720;

  }

  $: {  // 컬랙션 컨테이너 높이와 애니메이션 상태 업데이트 
    if (currentSetting === null) currentHeight_U = 224;
    else if (currentSetting === "email") currentHeight_U = 257;
    else if (currentSetting === "nickname") currentHeight_U = 169;
    else if (currentSetting === "password") currentHeight_U = 27;

  }

  let roadMap_h = 50;
  let activity_h = 700;

  let isRoadMapVisible = false;

  $: {
    if (isRoadMapVisible) {
      roadMap_h = 710;
      activity_h = 40;
    } else {
      roadMap_h = 50;
      activity_h = 700;
    }
  }

  function toggleView() {
    isRoadMapVisible = !isRoadMapVisible;
  }


  </script>
  
<main>
  <div class="header-container">
    <Header />
  </div>
  
  <div class="main-container" style="transform: translateX({$isListVisible ? 360 : 155}px); margin-right: {$isListVisible ? 360: 155}px; "> <!--알고리즘 리스트 유무에 따른 위치,크기 조절-->
     
    <div class="content">

      <div class="left-content ">
        
        <div id="profile-box" 
            style="height: {currentHeight}px; transition: height 0.3s ease; overflow: hidden;">

          <div id="profile-top-container">
            <span id='profile-title'>내 프로필</span>
            <div id="profile-image">
              <button id="profile-edit-Btn"> 설정</button>
            </div>
            <div id='nickName'>닉네임: {savedUserName} </div>
          </div>

          <div id="profile-bottom-container">

            <span id='setting-title'>기본 정보</span>

            {#if showSettingBox}
              <div id="setting-box">
                <div id="email-setting">
                  <div id="email-title"> 이메일 </div>
                  <div id="email-icon" on:mouseover={() => (showEmailInfo = true)} on:mouseout={() => (showEmailInfo = false)}>
                    <ion-icon name="activity-circle-outline"></ion-icon>
                    {#if showEmailInfo}
                    <div class="tooltip">이메일 인증이 필요합니다.</div>
                    {/if}
                  </div>
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

              {:else if currentSetting === 'email'} <!--이메일-->
                <div class="email-settings">

                  <div id="change-email-Title">
                    <span style="color: #bbbbbb;">이메일 변경</span>
                  </div>

                  <div id="email-input">
                    <input type="text" placeholder="example123@email.com" >
                  </div>

                  <div id="email-btn-group">
                    <button id="userName-edit-Btn" on:click={() => {currentSetting = null; showSettingBox = true;} }>취소</button>
                    <button id="userName-edit-Btn"> 저장</button>
                  </div>
                </div>

              {:else if currentSetting === 'nickname'} <!--닉네임-->
                <div class="nickname-settings" >
                  <div id='change-userName-container'  >
                      <div id="change-userName-Title">
                        <t style="color: #bbbbbb;">현재 닉네임: {savedUserName || "컴붕이1"} </t>
                        {#if saveMessage == '중복된 닉네임입니다. 다시 설정해주세요.'}
                          <ion-icon name="warning-outline" style="color: yellow;"></ion-icon>
                        {:else if saveMessage == "중복확인 닉네임 변경 완료"}
                          <ion-icon name="checkmark-outline" style="color: green;" ></ion-icon>
                        {/if}
                      </div>
    
                      <div id="change-userName">
                        <input type="text" placeholder="닉네임 변경 ..." bind:value="{userName}">
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
                            <ion-icon name="checkmark-outline"></ion-icon>
                            {saveMessage}
                          </div>
                        {/if}
                      </div>
                  </div>

                  <div id="btn-container">
                    <button id="userName-edit-Btn" on:click={() => {currentSetting = null; showSettingBox = true;} }>취소</button>
                    <button id="userName-edit-Btn" on:click={saveUserName}>저장</button>
                  </div>

                </div>
                
              {:else if currentSetting === 'password'} <!--비밀번호-->
                <div class="password-settings">
                  <!--비밀번호 변경 영역-->
                  <div id="change-password-container">

                    <div id="change-password-Title">
                      <t style="color: #bbbbbb;">비밀번호 변경</t>
                      {#if ispasswordCheckIcon }
                      <ion-icon name="checkmark-outline" style="color: #238636;"></ion-icon>
                      {/if}
                    </div>

                    <!-- 기존 비밀번호 입력 -->
                    <div id="change-userPassword_1" class="password-container">

                      {#if isOldPasswordVisible}
                      <!-- 비밀번호 보이기 -->
                      <input 
                        type="text" 
                        placeholder="기존 비밀번호" 
                        bind:value="{inputOldPassword}">
                      {:else}
                      <!-- 비밀번호 숨기기 -->
                      <input 
                        type="password" 
                        placeholder="기존 비밀번호" 
                        bind:value="{inputOldPassword}">
                      {/if}

                        <button 
                            type="button" 
                            class="toggle-password-btn" 
                            on:click={toggleOldPasswordVisibility}>
                            <ion-icon name={isOldPasswordVisible ? "eye-off-outline" : "eye-outline"}></ion-icon>
                        </button>
                    </div>
                
                    <div id="Requirements_password_1" style="color: {oldPasswordStatusColor};" >
                      <ion-icon name={oldPasswordIcon}></ion-icon> {oldPasswordStatus || "확인을 위해 비밀번호를 입력해주세요"}
                    </div>
                
                    <!-- 새 비밀번호 입력 -->
                    <div id="change-userPassword_2" class="password-container_2" style="margin-top: 11px;">
                      
                      {#if isNewPasswordVisible}
                      <!-- 비밀번호 보이기 -->
                      <input 
                        type="text" 
                        placeholder="새 비밀번호" 
                        bind:value="{inputNewPassword}">
                      {:else}
                      <!-- 비밀번호 숨기기 -->
                      <input 
                        type="password" 
                        placeholder="새 비밀번호" 
                        bind:value="{inputNewPassword}">
                      {/if}
                      
                        <button 
                          type="button" 
                          class="toggle-password-btn_2" 
                          on:click={toggleNewPasswordVisibility}>
                          <ion-icon name={isNewPasswordVisible ? "eye-off-outline" : "eye-outline"}></ion-icon>
                        </button>
                    </div>
                
                    <div id="Requirements_password_3" style="color: {condition1Met ? '#238636' : '#9198a1'};">
                      <ion-icon name="checkmark-outline"></ion-icon> 영문/숫자/특수문자 중, 2가지 이상 포함
                    </div>

                    <div id="Requirements_password_4" style="color: {condition2Met ? '#238636' : '#9198a1'};">
                      <ion-icon name="checkmark-outline"></ion-icon> 8자 이상 32자 이하 입력 (공백 제외)
                    </div>

                    <div id="Requirements_password_5" style="color: {condition3Met ? '#238636' : '#9198a1'};">
                      <ion-icon name="checkmark-outline"></ion-icon> 연속 3자 이상 동일한 문자/숫자 제외
                    </div>
                    
                    <div id="change-userPassword_3" class="password-container_3" style="margin-top: 11px;">
                      
                      {#if isNewPasswordVisible_verify}
                      <!-- 비밀번호 보이기 -->
                      <input 
                        type="text" 
                        placeholder="새 비밀번호 확인" 
                        bind:value="{inputNewPassword_verify}">
                      {:else}
                      <!-- 비밀번호 숨기기 -->
                      <input 
                        type="password" 
                        placeholder="새 비밀번호 확인" 
                        bind:value="{inputNewPassword_verify}">
                      {/if}
                      
                        <button 
                          type="button" 
                          class="toggle-password-btn_2" 
                          on:click={toggleNewPasswordVisibility_verify}>
                          <ion-icon name={isNewPasswordVisible_verify ? "eye-off-outline" : "eye-outline"}></ion-icon>
                        </button>
                    </div>

                    <div id="Requirements_password_6" style={verifyPasswordMessageStyle}>
                      <ion-icon name="checkmark-outline"></ion-icon> {verifyPasswordMessage}
                    </div>

                    <div id="password-btn-group">
                      <t id="password-search-Btn"> 비밀번호 찾기 </t> <!--비밀번호 찾기 페이지로 이동-->
                      <button id="userName-edit-Btn" on:click={() => {currentSetting = null; showSettingBox = true;} }>취소</button>
                      <button id="password-save-Btn" on:click={checkOldPassword}>저장</button>
                    </div>
                  </div> <!--change-password-container 끝-->

                </div>
              {/if}
          </div>
        </div> <!--profile-box 끝-->

        <div id="left-under-container"> 
          <div id="left-under-box" style="height: {currentHeight_U}px; transition: height 0.3s ease;">
            <span id='under-title' style="color: #4C905E;">컬렉션</span>
          </div>
        </div>

      </div> <!--left-container 끝-->

      <div id="right-container">
        <div id="activity-box" style="height: {activity_h}px; padding-top: {isRoadMapVisible ? '10px' : '20px'}; transition: height 0.3s ease, padding 0.3s ease;" >
          <span id='activity-title'>활동 내역</span>
          <div id="activity-top-container">
            <div class="lawn-container">

              <span id='lawn-title'>2024년에 128 기여 했습니다.</span>

              <div id="lawn-box"> </div>

            </div>

            <div id="activity-top-information-container">
              <span id='informaition-txt'>최근 공부한 알고리즘: bubble-sort </span>
              <span id='informaition-txt'>연속 출석일수: 3일 </span>
              <span id='informaition-txt' style="color:#83b06d ">모은 commit: 162 </span>
            </div>

          </div>

          <div id="activity-bottom-container">

            <div id="wrote-container">
              <span id='wrote-title'>내가 쓴 글</span>
              <div id="wrote-box"></div>
            </div>

            <div id="comment-container" style="padding-left:10px;">
              <span id='comment-title'>내가 쓴 댓글</span>
              <div id="comment-box"></div>
            </div>

          </div>
        </div> <!--activity-box 끝-->

        <div class="roadMap" style="height: {roadMap_h}px; transition: height 0.3s ease; overflow: hidden;" >
          <div id="goToRoadMap">
            <div id="RoadMapBtn" style="margin-top:8px;" on:click={toggleView}>
              <div style="margin-left: 22px;"><ion-icon size="large" name="map-outline"></ion-icon></div>
            </div>
          </div>
        </div>
        
      </div><!--right-container 끝-->
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
   overflow: hidden;
  }
  
 .content {
   display:grid;
   grid-template-columns: 620px 1fr;
  }

  .left-container  {
    
  }

  #right-container {

  }

  /* -------------------큰 틀------------------ */
 #profile-box {
   display: grid;
   grid-template-rows: 270px 1fr;
   margin: 0px 0px 0px 200px;
   padding: 20px 0px 0px 0px;
   width: 400px;
   height: 520px;
   background-color: #151b23;
   border: 1px solid #3d444d;
   border-radius: 8px;
   box-sizing: border-box;

   overflow: hidden;

  }

  #activity-box {
   display: grid;
   grid-template-rows: 40px 1fr 1fr;
   width: 800px;
   height: 700px;
   background-color: #151b23;
   border: 1px solid #3d444d;
   border-radius: 8px;
   box-sizing: border-box;
   padding: 0 0 0 20px;
   overflow: hidden;
  }

  #left-under-box {
   display: grid;
   grid-template-rows: 270px 1fr;
   margin: 20px 0px 0px 200px;
   padding: 20px 0px 0px 20px;
   width: 400px;
   height: 224px;
   background-color: #15231c;
   border: 1px solid #4a7744;
   border-radius: 8px;
   box-sizing: border-box;
   overflow: hidden; /* 높이를 초과하는 내용을 숨김 */
   

  }

  #left-under-container {
    position: fixed;
    height: 247px;
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

  #setting-title, #under-title, #activity-title {
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
    position: relative;
    cursor: pointer;
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

  

  /* -------------------닉네임 변경 칸------------------ */

  #change-userName-Title {
    margin: 16px 0px 10px 0px;
    font-weight: bold;
  }

  #verify-userName-container {
    margin: 10px 0px 0px 0px;
    font-size: 14px;
    color: #9198a1;
  }

  #change-userName input[type="text"] {
    padding: 6px;
    margin: 0px 0px 0px 0px;
    border-radius: 7px;
    border: 1px solid #3d444d;
    width: 330px;
    height: 21px;
    background-color: #151b23;
    font-size: 0.9rem;
    color: #FFFFFF;
  }

  #change-userName-container {
    padding-left: 1px;
    height: 160px; /* 고정 높이 설정 */
    overflow: hidden; /* 높이를 초과하는 내용을 숨김 */
  }

  #btn-container {
    margin: 25px 0px 0px 190px;
  }


  #userName-edit-Btn {
   position: relative;
   background-color: #3f862d;
   color: white;
   padding: 5px 10px;
   font-size: 14px;
   font-weight: bold;
   border-radius: 7px;
   cursor: pointer;
   box-shadow: 0 2px 5px rgba(0, 0, 0, 0.3);
   margin: 0px 0px 0px 6px;
   width: 70px;
   height: 30px;
  }

  #userName-edit-Btn:hover {
    background-color: #2f6321;
  }

 /* -------------------닉네임 변경 칸 끝 ------------------ */

 /* -------------------비밀번호 변경 칸 ------------------ */


  #change-password-container {
      
  }

  #change-password-Title {
    margin: 10px 0px 10px 0px;
    font-weight: bold;
  }

  .password-container input {
    padding: 6px;
    margin: 0px 0px 0px 0px;
    border-radius: 7px;
    border: 1px solid #3d444d;
    width: 340px;
    height: 21px;
    background-color: #151b23;
    font-size: 0.9rem;
    color: #FFFFFF;
  }

  .password-container_2 input, .password-container_3 input {
    padding: 6px;
    margin: 0px 0px 0px 0px;
    border-radius: 7px;
    border: 1px solid #3d444d;
    width: 340px;
    height: 21px;
    background-color: #152321;
    font-size: 0.9rem;
    color: #FFFFFF;
  }

  .toggle-password-btn,
  .toggle-password-btn_2 {
      position: absolute;
      top: 58%;
      right: 8px;
      transform: translateY(-50%);
      background: none;
      border: none;
      cursor: pointer;
      font-size: 1.2rem;
      color: #bbbbbb;
      margin-right: 23px;
  }

  .toggle-password-btn:hover,   
  .toggle-password-btn_2:hover {
    color: #424242;
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
   color: rgb(99, 99, 99);
   padding: 5px 10px;
   font-size: 12px;
   font-weight: bold;
   border-radius: 7px;
   cursor: pointer;
   margin: 0px 95px 0px 0px;
   width: 120px;
   height: 30px;
  }

  #password-btn-group {
    margin-top: 30px;
    margin-left: 0px;
  }

  #password-save-Btn:hover {
    background-color: #1c682b;
  }

  #password-search-Btn:hover {
    text-decoration: underline;
  }

  #profile-edit-Btn:hover {
    background-color: #2c2c2c;
  }

  #change-userName {
    display: flex;
  }

  .password-container, .password-container_2, .password-container_3 {
    position: relative;
    display: inline-block;
    width: 100%;
  }

  #Requirements_password_1 {
    margin: 10px 0px 0px 5px;
    font-size: 14px;
    color: #9198a1;
  }

  #Requirements_password_2 {
    margin: 10px 0px 0px 5px;
    font-size: 14px;
    color: #9198a1;
  }

  #Requirements_password_3 {
    margin: 10px 0px 0px 5px;
    font-size: 14px;
    color: #9198a1;
  }

  #Requirements_password_4 {
    margin: 10px 0px 0px 5px;
    font-size: 14px;
    color: #9198a1;
  }

  #Requirements_password_5 {
    margin: 10px 0px 0px 5px;
    font-size: 14px;
    color: #9198a1;
  }

  #Requirements_password_6 {
    margin: 5px 0px 0px 5px;
    font-size: 14px;
    color: #9198a1;
  }

  #Requirements_1 {
    margin-bottom: 5px;
  }

  #Requirements_2 {
    margin-bottom: 5px;
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

  /* -------------------비밀번호 변경 칸 끝 ------------------ */

  /* -------------------이메일 변경 칸 ------------------ */

  #change-email-Title {
    margin: 20px 0px 10px 0px;
    font-weight: bold;
  }

  #email-input input[type="text"] {
    padding: 6px;
    margin: 0px 0px 0px 0px;
    border-radius: 7px;
    border: 1px solid #3d444d;
    width: 344px;
    height: 21px;
    background-color: #151b23;
    font-size: 0.9rem;
    color: #FFFFFF;
  }

  #email-btn-group {
    margin: 15px 0px 0px 198px;
  }

  .tooltip {
    position: absolute;
    top: 120%; /* 아이콘 아래로 조금 띄우기 */
    left: 200%;
    transform: translateX(-50%);
    background-color: rgba(50, 104, 50, 0.8);
    color: white;
    padding: 8px 12px;
    border-radius: 8px;
    white-space: nowrap;
    font-size: 0.9rem;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.3);
    z-index: 100;
  }
  


  /* -------------------이메일 변경 칸 끝 ------------------ */

  /* -------------------  활동(잔디밭) 칸 ------------------ */
  
  #lawn-box {
    height: 200px;
    width: 700px;
    background-color: #151b23;
    border: 2px solid #3d444d;
    border-radius: 5px;
    margin-left: 28px;
    box-sizing: border-box;
  }

  #lawn-title {
    margin: 20px 0px 0px 28px;
    font-size: 1.2rem;
    color: #8d8d8d;
  }


  #activity-top-information-container {
    padding: 28px 28px 0px 28px;
    display: flex;
    flex-direction: column;
    gap: 10px;
    color: #dddddd;
  }

  #activity-bottom-container {
    display: grid;
    padding: 28px 28px 0px 28px;
    grid-template-columns:1fr 1fr;
  }

  #wrote-title, #comment-container {
    font-size: large;
    color: #c9c9c9;
    font-weight: bold;
  }

  #wrote-box, #comment-box {
    width: 335px;
    height: 200px;
    border: 2px solid #3d444d;
    border-radius: 5px;
    margin-top: 5px;
  }

  #goToRoadMap {
    margin: 0 auto;
  }

  #RoadMapBtn {
    display: inline-block;
    transition: transform 0.3s ease-in-out, color 0.3s ease-in-out;
    color: #858585;
  }

  #RoadMapBtn:hover {
    animation: shake 0.5s ease-in-out infinite;
    color: #1c682b;
    
  }

  @keyframes shake {
    0% { transform: translateY(0); }
    20% { transform: translateY(-4px); }
    40% { transform: translateY(-6px); }
    50% { transform: translateY(-8px); }
    70% { transform: translateY(-10px); }
    90% { transform: translateY(-5px); }
    100% { transform: translateY(0px); }
  }

  /* -------------------활동 칸 끝 ------------------ */

  /* -------------------  로드맵 ------------------ */

  .roadMap {
   display: grid;
   grid-template-rows: 40px 1fr 0.8fr 60px;
   margin:20px 0px 0px 0px;
   width: 800px;
   height: 50px;
   background-color: #151b23;
   border: 1px solid #3d444d;
   border-radius: 8px;
   box-sizing: border-box;
   padding: 0px;
  }

</style>
  