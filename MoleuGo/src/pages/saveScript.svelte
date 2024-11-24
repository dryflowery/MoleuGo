<div id="left-container">

            <!--프로필 사진 및 닉네임 변경 영역-->
            <div id="profile-edit-container">
              <!--프로필 사진 영역-->
              <div id="profile-image">
                <button id="profile-edit-Btn">설정</button>
              </div>

              <div id='change-userName-container'>
                {#if isvisualableNickNameSetting}
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
                {/if}
              </div>
            </div>

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

              <div id="password-btn">
                <button id="password-search-Btn">비밀번호 찾기</button>
                <button id="password-save-Btn" on:click={checkOldPassword}>저장</button>
              </div>
            </div> <!--change-password-container 끝-->
          </div> <!--왼쪽 컨테이너 끝-->

        <div id="right-container"> <!--오른쪽 컨테이너 시작-->

          <div id="lawn-container"><!--잔디밭 컨테이너 시작-->
            
          </div>

          <div id="community"><!--잔디밭 컨테이너 시작-->
            
          </div>

        </div ><!--오른쪽 컨테이너 끝-->