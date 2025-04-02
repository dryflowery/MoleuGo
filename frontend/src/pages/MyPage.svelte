<script>
    import Footer from "../component/Footer.svelte";
    import Header from "../component/Header.svelte";
    import Toast from '../component/Toast.svelte';

    import {isListVisible } from "../lib/store"
    import { onMount } from "svelte";
    import { writable } from "svelte/store";

    let showToast = false;
    let toastMessage = '';
    let toastType = 'success'; // or 'error'

    function showToastMessage(message, type = 'success') {
      toastMessage = message;
      toastType = type;
      showToast = true;

      setTimeout(() => {
        showToast = false;
      }, 2000); // 2초 후 자동 숨김
    }

    let userName = ""; // 입력된 닉네임
    let savedUserName = ''; // 현재 닉네임 (서버에서 가져온 값)
    let nicknameMessage = '';


    let isLengthValid = false; // 길이 판별
    let isSpecialCharValid = false;
    let showResult = false; // 결과 메시지 표시 여부
    let saveMessage = ""; // 저장 메세지

    let userPassword = ''; //기존 비밀번호

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


    let myEmail = '';

    let currentSetting = null; // 현재 설정 상태
    let showSettingBox = true; // 기본적으로 setting-box 표시

    let newEmail = ""; // 새 이메일 입력값
    let message = '';

    let isLoading = false;
    let isError = false;

    let passwordMessage = '';
    let passwordMessageColor = "#bbbbbb";

    let accountType = "";

    const scaleFactorStore = writable(1);

    const socialIcons = {
      google: { src: 'assets/google.png',
                alt: '구글 계정'
      }
    };

    async function requestEmailChange() {
      isLoading = true;
      isError = false;
      message = '메일을 보내는 중...';

      try {
        const response = await fetch('/user/change-email-request', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          credentials: 'include',
          body: JSON.stringify({ email: newEmail })
        });

        if (response.ok) {
          showToastMessage("인증 메일이 전송되었습니다. 메일함을 확인해주세요.", "success");
          message = '';

        } else if (response.status === 409) {
          showToastMessage("이미 사용 중인 이메일입니다.", "error");
          message = '';

        } else {
          showToastMessage("이메일 변경 요청에 실패했습니다.", "error");
          message = '';

        }
      } catch (err) {
        console.error(err);
        showToastMessage("오류가 발생했습니다. 다시 시도해주세요.", "error");
        message = '';

      } finally {
        isLoading = false;
      }
    }

    function updateScaleFactor() {
      const width = window.innerWidth;
      const isHighResolution = width >= 2560;
      scaleFactorStore.set(isHighResolution ? 1.333 : 1);
    }

  updateScaleFactor();
  window.addEventListener("resize", updateScaleFactor);

  let scaleFactor = 0;

  scaleFactorStore.subscribe((value) => (scaleFactor = value));

  let currentHeight = 525 * scaleFactor; // 기본 높이
  let currentHeight_U = 224 * scaleFactor; // 기본 높이

  let showEmailInfo = false; // 설명 표시 여부


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
  async function saveNewPassword() {
    passwordMessage = "";

    if (!inputOldPassword || !inputNewPassword || !inputNewPassword_verify) {
      passwordMessage = "모든 항목을 입력해주세요.";
      passwordMessageColor = "red";
      return;
    }


    const response = await fetch("/user/change-password", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({
        currentPassword: inputOldPassword,
        newPassword: inputNewPassword,
        verifyNewPassword: inputNewPassword_verify
      })
    });

    switch (response.status) {
      case 200:
        passwordMessage = "비밀번호가 성공적으로 변경되었습니다.";
        passwordMessageColor = "#3f862d";
        inputOldPassword = "";
        inputNewPassword = "";
        inputNewPassword_verify = "";
        showToastMessage("성공적으로 업데이트되었습니다.", "success");
        break;
      case 403:
        passwordMessage = "현재 비밀번호가 틀렸습니다.";
        passwordMessageColor = "#892A35";
        break;
      case 400:
        passwordMessage = "새 비밀번호가 유효하지 않습니다.";
        passwordMessageColor = "#892A35";
        break;
      case 401:
        passwordMessage = "로그인이 만료되었습니다.";
        passwordMessageColor = "#892A35";
        break;
      default:
        passwordMessage = "비밀번호 변경에 실패했습니다.";
        passwordMessageColor = "#892A35";
    }
  }




  // 닉네임 검증 함수
  function validateUserName(name) {
    const lengthRegex = /^.{2,10}$/; // 2~10 글자
    const specialCharRegex = /^[^\s!@#$%^&*(),.?":{}|<>]*$/; // 띄어쓰기 및 특수문자 제외

    isLengthValid = lengthRegex.test(name);
    isSpecialCharValid = specialCharRegex.test(name);
  }

  // 닉네임 저장 버튼 클릭 이벤트
    async function saveUserName() {
      try {
        const res = await fetch('/user/change-nickname', {
          method: 'POST',
          credentials: 'include',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({ newNickname: userName })
        });

        if (res.status === 200) {
          await fetchNickname();
          nicknameMessage = '닉네임이 성공적으로 변경되었습니다.';
          isError = false;
          userName = "";
          showToastMessage("성공적으로 업데이트되었습니다.", "success");

        } else if (res.status === 400) {
          nicknameMessage = '닉네임 형식이 올바르지 않습니다.';
          isError = true;
        } else if (res.status === 409) {
          nicknameMessage = '이미 사용 중인 닉네임입니다.';
          isError = true;
        } else {
          nicknameMessage = '닉네임 변경 실패';
          isError = true;
        }
      } catch (error) {
        console.error('닉네임 변경 오류:', error);
        nicknameMessage = '서버 오류가 발생했습니다.';
        isError = true;
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

  $: {
    const isHighResolution = window.innerWidth >= 2560; // 화면 너비 체크
    if (!isHighResolution) { // 2560 이상이면 적용하지 않음
      if (currentSetting === null) currentHeight = 525 * scaleFactor;
      else if (currentSetting === "email") currentHeight = 490 * scaleFactor;
      else if (currentSetting === "nickname") currentHeight = 580 * scaleFactor;
      else if (currentSetting === "password") currentHeight = 720 * scaleFactor;
    } else {
      currentHeight = 525* scaleFactor; // 고정 높이
    }
  }

  $: {
    const isHighResolution = window.innerWidth >= 2560; // 화면 너비 체크
    if (!isHighResolution) { // 2560 이상이면 적용하지 않음
      if (currentSetting === null) currentHeight_U = 224 * scaleFactor;
      else if (currentSetting === "email") currentHeight_U = 257 * scaleFactor;
      else if (currentSetting === "nickname") currentHeight_U = 169 * scaleFactor;
      else if (currentSetting === "password") currentHeight_U = 27 * scaleFactor;
    } else {
      currentHeight_U = 224* scaleFactor; // 고정 높이
    }
  }

  let roadMap_h = 50 * scaleFactor;
  let activity_h = 700 * scaleFactor;

  let isRoadMapVisible = false;


  $: {
    if (isRoadMapVisible) {
      roadMap_h = 710 * scaleFactor;
      activity_h = 40 * scaleFactor;
    } else {
      roadMap_h = 50 * scaleFactor;
      activity_h = 700 * scaleFactor;
    }
  }

  function toggleView() {
    isRoadMapVisible = !isRoadMapVisible;
  }

    async function fetchNickname() {
      try {
        const res = await fetch("/user/nickname", { credentials: "include" });
        if (res.ok) {
          savedUserName = await res.text();
        }
      } catch (err) {
        console.error("닉네임 로딩 실패:", err);
      }
    }

  onMount(async () => {
    try {
      // 이메일 요청
      const emailRes = await fetch('/user/email', { credentials: 'include' });

      if (emailRes.ok) {
        myEmail = await emailRes.text();
      }

      fetchNickname(); // 닉네임 요청

      // account_type 요청
      const typeRes = await fetch('/user/account-type', { credentials: 'include' });
      if (typeRes.ok) {
        accountType = await typeRes.text();
      }

    } catch (e) {
      console.error('유저 정보 불러오기 중 오류 발생:', e);
    }
  });

</script>
  
<main>

  <Toast message={toastMessage} type={toastType} show={showToast} />

  <div class="header-container">
    <Header />
  </div>
  
  <div class="main-container" style="transform: translateX({$isListVisible ? 400 : 0}px); margin-right: {$isListVisible ? 400: 0}px; "> <!--알고리즘 리스트 유무에 따른 위치,크기 조절-->
     
    <div class="content">

      <div class="left-content ">
        
        <div id="profile-box" 
            style="height: {currentHeight}px; transition: height 0.3s ease;">

          <div id="profile-top-container">
            <span id='profile-title'>내 프로필</span>

            <div id="profile-image">
              <button id="profile-edit-Btn"> 설정</button>
            </div>

            <div id='nickName'>닉네임: {savedUserName}
              {#if accountType && socialIcons[accountType]}
                <img
                        src={socialIcons[accountType].src}
                        alt={socialIcons[accountType].alt}
                        class="social-icon"
                />
              {/if}
            </div>

          </div>

          <div id="profile-bottom-container">

            <span id='setting-title'>기본 정보</span>

            {#if showSettingBox}
              <div id="setting-box">
                <div id="email-setting">
                  <div id="email-title"> 이메일 </div>
                  <div id="email-icon" on:mouseover={() => (showEmailInfo = true)} on:mouseout={() => (showEmailInfo = false)}>
                    <ion-icon name="help-circle-outline"></ion-icon>
                    {#if showEmailInfo}
                    <div class="tooltip">
                      {#if accountType === 'normal'}
                        이메일 인증이 필요합니다.
                      {:else}
                         소셜계정은 이메일과 비밀번호를 변경할 수 없습니다.
                      {/if}
                    </div>
                    {/if}
                  </div>
                  <div id="email-output"> {myEmail} </div>

                  <button
                          id="email-btn"
                          on:click={() => { showSettingBox = false; currentSetting = 'email'; }}
                          disabled={accountType !== 'normal'}>
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

                  <button
                          id="password-btn"
                          on:click={() => { showSettingBox = false; currentSetting = 'password'; }}
                          disabled={accountType !== 'normal'}
                  >

                    설정
                  </button>
                </div>
              </div><!--setting-box 끝-->

              {:else if currentSetting === 'email'} <!--이메일-->
                <div class="email-settings">

                  <div id="change-email-Title" style="display: flex; align-items: center; gap: 12px;">
                    <span style="color: #bbbbbb;">이메일 변경</span>
                    {#if message}
                      <span style="font-size: 14px; color: #999;"> {message} </span>
                    {/if}
                  </div>

                  <div id="email-input">
                    <input type="text" placeholder="example123@email.com" bind:value="{newEmail}">
                  </div>

                  <div id="email-btn-group">
                    <button id="userName-edit-Btn" on:click={() => {currentSetting = null; showSettingBox = true;} }>취소</button>
                    <button id="userName-edit-Btn" on:click={requestEmailChange} > 저장</button>
                  </div>
                </div>

              {:else if currentSetting === 'nickname'} <!--닉네임-->
                <div class="nickname-settings" >
                  <div id='change-userName-container'  >
                      <div id="change-userName-Title">
                        <t style="color: #bbbbbb;">현재 닉네임: {savedUserName} </t>
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

                        <div id="Requirements_2" style="color: {isError ? '#892A35' : '#3f862d'}; margin-left: 1rem;">
                          {nicknameMessage}
                        </div>


                      </div>
                  </div>

                  <div id="btn-container">
                    <button id="userName-edit-Btn" on:click={() => {currentSetting = null; showSettingBox = true; nicknameMessage = ''; userName = ''; } }>취소</button>
                    <button id="userName-edit-Btn" on:click={saveUserName}>저장</button>
                  </div>

                </div>
                
              {:else if currentSetting === 'password'} <!--비밀번호-->
                <div class="password-settings">
                  <!--비밀번호 변경 영역-->
                  <div id="change-password-container">

                    <div id="change-password-Title">
                      <t style="color: #bbbbbb;">비밀번호 변경</t>
                      {#if passwordMessage}
                        <span style="margin-left: 1rem; font-size: 14px; color: {passwordMessageColor}; font-weight: bold;">
                          {passwordMessage}
                        </span>
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
                      <button id="userName-edit-Btn" on:click={() => {currentSetting = null; showSettingBox = true; passwordMessage = ''; inputOldPassword = ""; inputNewPassword = ""; inputNewPassword_verify = "";} }>취소</button>
                      <button id="password-save-Btn" on:click={saveNewPassword}>저장</button>
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
   display: flex; /* 추가 */
   align-items: center; /* 추가 */
   justify-content: center; /* 추가 */
   
  }
  
 .content {
   display:grid;
   grid-template-columns: 1fr 1.9fr;
  }

  .left-container  {
    
  }

  #right-container {

  }

  /* -------------------큰 틀------------------ */
 #profile-box {
   display: grid;
   grid-template-rows: 270px 1fr;
   margin: 0px 0px 0px 0px;
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
   margin: 20px 0px 0px 0px;
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

    margin: 15px 0px 0px 0px;

    display: flex;
    justify-content: center;
    align-items: center;
    text-align: center;
    flex-wrap: wrap;

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

  @media screen and (min-width: 2560px)  {
    .main-container {
      transform: scale(1.333); /* 모든 요소를 1.333배 확대 */
      transform-origin: top left; /* 왼쪽 상단 기준 확대 */
    }

    #profile-box {
      width: 533px; /* 400px x 1.333 */
      height: 700px; /* 525px x 1.333 */
      
    }

    #profile-image {
      width: 175px;
      height: 175px;
      border: 1px solid #949494;
      background-color: rgb(88, 88, 88);
      margin: 10px 0px 0px 175px;
    }

    #nickName {
      font-weight: bold;
      font-size: large;
      color: #c4c4c4;

      margin: 15px 0px 0px 0px;

      display: flex;
      justify-content: center;
      align-items: center;
      text-align: center;
      flex-wrap: wrap;
    }

    #email-setting, #nickname-setting, #password-setting {
      display: flex;
      flex-direction: row;
      align-items: center;
      justify-content: space-between; /* 요소 간의 간격을 유지 */
      width: 100%; /* 부모의 너비에 맞게 확장 */
    }

    #nickname-title, #password-title {
      flex: 0 0 105px; /* 제목 영역은 15% 크기로 설정 */
    }

    #email-title {
      flex: 0 0 50px; /* 제목 영역은 15% 크기로 설정 */
    }

    #nickname-output, #password-output {
      flex: 1; /* 출력 영역이 남은 공간을 차지 */
    }

    #email-btn, #nickname-btn, #password-btn {
      flex: 0 0 70px; /* 버튼 영역은 10% 크기로 설정 */
      margin-right: 15px;
    }

    #email-icon {
      flex: 0 0 55px; /* 아이콘 크기 고정 */
      position: relative;
      cursor: pointer;
    }

    #email-output {
      flex: 1; /* 출력 영역이 남은 공간을 차지 */
      text-align: left; /* 텍스트 정렬 */
    }

    #activity-box {
      width: 1066px; /* 800px x 1.333 */
      height: 933px; /* 700px x 1.333 */
    }

    #left-under-box {
      width: 533px; /* 400px x 1.333 */
      height: 298px; /* 224px x 1.333 */
    }

    #lawn-box {
      height: 266px; /* 200px x 1.333 */
      width: 933px; /* 700px x 1.333 */
    }

    #wrote-box, #comment-box {
      width: 444px; /* 333px x 1.333 */
      height: 266px; /* 200px x 1.333 */
    }

    .roadMap {
      width: 1066px; /* 800px x 1.333 */
      height: 67px; /* 50px x 1.333 */
    }

    #change-userName input[type="text"] {
      width: 470px;
    }

    #btn-container {
      margin: 25px 0px 0px 328px;
    }

    #email-input input[type="text"] {
      width: 470px;
    }

    #email-btn-group {
      margin: 15px 0px 0px 328px;
    }

    .password-container input {
      width: 470px;
    }

    .password-container_2 input, .password-container_3 input {
      width: 470px;
    }

    #password-btn-group {
      margin-top: 20px;
      margin-left: 236px;
    }

    #password-search-Btn {
      margin: 0px 0px 0px 0px;

    }

  }

  #email-btn:disabled,
  #password-btn:disabled {
    background-color: #2a2a2a;   /* 어두운 회색 배경 */
    color: #777777;              /* 흐릿한 글씨색 */
    border: 1px solid #444444;
  }

  .social-icon {
    width: 18px;
    height: 18px;
    margin-left: 3px;
    vertical-align: middle;
  }

</style>
  