<script>
    import Footer from "../component/Footer.svelte";
    import Header from "../component/Header.svelte";
    import {isListVisible } from "../lib/store"
    import {isLogin } from "../lib/store"
    import { push } from "svelte-spa-router";

    let userName = "";
    let isLengthValid = false;
    let isSpecialCharValid = false;
    let saveMessage = ""; // 저장 메세지
    let savedUserName = ""; // 저장된 이름

    // 닉네임 검증 함수
    function validateUserName(name) {
        const lengthRegex = /^.{2,10}$/; // 2~10 글자
        const specialCharRegex = /^[^\s!@#$%^&*(),.?":{}|<>]*$/; // 띄어쓰기 및 특수문자 제외

        isLengthValid = lengthRegex.test(name);
        isSpecialCharValid = specialCharRegex.test(name);
    }

    // 닉네임 저장 버튼 클릭 이벤트
    function saveUserName() {
      if (isLengthValid && isSpecialCharValid) {
          savedUserName = userName; // 저장된 닉네임 업데이트
          saveMessage = "수정됨";
        } else {
          saveMessage = "수정안됨";
        }
    }

    // 닉네임이 변경될 때마다 검증
    $: validateUserName(userName);


  </script>
  
<main>
   <div class="header-container">
    <Header />
   </div>
  
  <div class="main-container" style="transform: translateX({$isListVisible ? 410 : 205}px); margin-right: {$isListVisible ? 410: 205}px; "> <!--알고리즘 리스트 유무에 따른 위치,크기 조절-->
     
    <div class="content">

      <div id="content-box">
          <div id="left-container">

            <!--프로필 사진 및 닉네임 변경 영역-->
            <div id="profile-edit-container">
              <!--프로필 사진 영역-->
              <div id="profile-image">
                <button id="profile-edit-Btn">설 정</button>
              </div>

              <div id='change-userName-container'>

                <div id="change-userName-Title">
                  <t style="color: #e4e4e4;">닉네임: {savedUserName || "컴붕이1"} </t>
                  {#if saveMessage == '수정안됨'}
                    <ion-icon name="warning-outline" style="color: yellow;"></ion-icon>
                  {:else}
                    <ion-icon name="checkbox-outline" style="color: green;" ></ion-icon>
                  {/if}
                </div>

                <div id="change-userName">
                  <input type="text" placeholder="닉네임 변경 ..." bind:value="{userName}">
                  <button id="userName-edit-Btn" on:click={saveUserName}>변 경</button>
                </div>

                <div id="verify-userName-container">

                  <div id="Requirements_1" 
                       class:valid={isLengthValid}  
                       class:invalid={!isLengthValid}> 
                       <ion-icon name={isLengthValid ? "checkmark-circle-outline" : "checkmark-outline"}></ion-icon> 
                       2 ~ 10 글자 사이의 닉네임을 정해주세요
                  </div>

                  <div id="Requirements_2" 
                       class:valid={isSpecialCharValid}
                       class:invalid={!isSpecialCharValid}> 
                       <ion-icon name={isSpecialCharValid ? "checkmark-circle-outline" : "checkmark-outline"}></ion-icon> 
                       띄어쓰기를 포함한 특수문자 X
                  </div>

                  <div id="Requirements_3">   
                       <ion-icon name={isSpecialCharValid ? "checkmark-circle-outline" : "checkmark-outline"}></ion-icon> 
                       중복확인: 통과
                  </div>

                </div>
              </div>
            </div>

            <div id="change-password-container">
              <div id="change-password-Title">비밀번호 변경</div>

            </div>
          </div>

          <div id="right-container">
            <div id="lawn-container"></div>
          </div>
      </div>
  
    </div>
  </div>
  
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

 #content-box {
   display: grid;
   grid-template-columns: 0.35fr 0.65fr;
   margin: 0px 0px 0px 200px;
   width: 1100px;
   height: 770px;
   background-color: #151c23;
   border: 3px solid #3d444d;
   border-radius: 8px;
   box-sizing: border-box;
  }

  #left-container {
   display:grid;
   grid-template-rows: 1.1fr 0.9fr;
   border-right: 3px solid #3d444d;
  }

  #right-container {
    display:grid;
    grid-template-rows: 1fr 1fr;
  }

  #profile-edit-container {
    
  }

  #change-userName-container {
    margin-top: 35px;
  }

  #profile-image {
   width: 200px;
   height: 200px;
   border-radius: 50%;
   border: 5px solid #3d4d3d;
   background-color: rgb(100, 100, 100);
   margin: 30px 0px 0px 75px;
  }

  #profile-edit-Btn {
   position: relative;
   background-color: #434343;
   color: white;
   padding: 8px 16px;
   border: 1px solid #888888;
   font-size: 13px;
   font-weight: bold;
   border-radius: 7px;
   cursor: pointer;
   box-shadow: 0 2px 5px rgba(0, 0, 0, 0.3); /* Subtle shadow */
   margin: 150px 0px 0px 150px;
   width: 70px;
  }

  #profile-edit-Btn:hover {
    background-color: #1c682b;
  }

  #change-userName {
    display: flex;
  }

  #change-userName-Title {
    margin: 16px 0px 16px 30px;
    font-weight: bold;
  }

  #change-password-container {
    
  }

  #change-password-Title {
    margin: 16px 0px 16px 30px;
    font-weight: bold;
    color: #e4e4e4;
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

  #userName-edit-Btn {
   position: relative;
   background-color: #434343;
   color: white;
   padding: 5px 10px;
   font-size: 14px;
   font-weight: bold;
   border-radius: 7px;
   cursor: pointer;
   box-shadow: 0 2px 5px rgba(0, 0, 0, 0.3); /* Subtle shadow */
   margin: 0px 0px 0px 6px;
   width: 75px;
   height: 35px;
  }

  #userName-edit-Btn:hover {
    background-color: #1c682b;
  }

  #verify-userName-container {
    margin: 15px 0px 0px 30px;
    font-size: 12px;
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

</style>
  