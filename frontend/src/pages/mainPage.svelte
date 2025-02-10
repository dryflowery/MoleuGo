<script>
  import Footer from "../component/Footer.svelte";
  import Header from "../component/Header.svelte";
  import {isListVisible } from "../lib/store"
  import {isLogin } from "../lib/store"
  import { push } from "svelte-spa-router";

  //임시로 사용하는 함수(로그인/비로그인)
  const changeLogin = ()=> {
    $isLogin = !$isLogin
  }

</script>

<main>
  <div class="header-container">
    <Header />
  </div>

  <div class="main-container" style="transform: translateX({$isListVisible ? 400 : 0}px); margin-right: {$isListVisible ? 400: 0}px; "> <!--알고리즘 리스트 유무에 따른 위치,크기 조절-->
    <div class="content">

      <div id="dailyGoal"> <!--일일 목표 -->
        <t style="color: #a3a3a3; font-size: 25px; font-weight: bold; color: #4C905E" on:click={changeLogin}> 일일 목표</t>
        <div id="dailyGoal-box" style="margin-top: 5px;"> 

          <!--비 로그인 시 생기는 잠금-->
          {#if !$isLogin}
            <div id="locked-box-daily">
              <!-- svelte-ignore a11y-missing-attribute -->
              <img src="assets/lock.png" style="height: 130px;">
              <p>로그인 하고 컨텐츠 보기</p>
            </div>
          {/if}

          <!--로그인 시 생기는 화면-->
          <div id="dailyGoal-box-content" class="{ $isLogin ? ' ' : 'blurred' }"> 
            안녕하세요 
          </div>

        </div>
      </div>

      <div id="recentAlgorithm"> <!--최근알고리즘 -->
        <t style="color: #a3a3a3; font-size: 25px; font-weight: bold;">최근 알고리즘</t>
        <div id="recentAlgorithm-box" style="margin-top: 5px;"> 최근 알고리즘
        </div>
      </div>

      <div id="community"> <!--커뮤니티 -->
        <t style="color: #a3a3a3; font-size: 25px; font-weight: bold;">커뮤니티</t>
        <div id="community-box" style="margin-top: 5px;">안녕하세요</div>
      </div>

      <div id="myProfile"> <!--마이페이지 -->
        
        <!-- svelte-ignore a11y-click-events-have-key-events -->
        <!-- svelte-ignore a11y-no-static-element-interactions -->

         <!-- 비 로그인시 마이페이지 이동 불가-->
        <t style="color: #a3a3a3; font-size: 25px; font-weight: bold;" 
           class="{isLogin ? '' : 'disabled'}" 
           on:click={() => { if ($isLogin) push('/main/myPage') }}>

           마이페이지 

          {#if !$isLogin} <!--자물쇠 이미지 보이기 on/off-->
          <!-- svelte-ignore a11y-missing-attribute -->
          <img id='locked' src="assets/lock.png" style="height: 20px;">
          {/if}

        </t>

        <div id="myProfile-box" style="margin-top: 5px;">
          <div id="myProfile-box-content" class="{ $isLogin ? ' ' : 'blurred' }"> 
            안녕하세요
          </div>

        </div>
      </div>

      <div id="roadMap"> <!--로드맵 -->

        <!-- svelte-ignore a11y-click-events-have-key-events -->
        <!-- svelte-ignore a11y-no-static-element-interactions -->

        <!-- 비 로그인시 로드맵 이동 불가-->
        <t style="color: #a3a3a3; font-size: 25px; font-weight: bold;" 
           class="{isLogin ? '' : 'disabled'}" 
           on:click={() => { if ($isLogin) push('/main/myPage') }}>
           
           로드맵
          
          {#if !$isLogin} <!--자물쇠 이미지 보이기 on/off-->
          <!-- svelte-ignore a11y-missing-attribute -->
          <img id='locked' src="assets/lock.png" style="height: 20px;">
          {/if}
          
          </t>
        <div id="roadMap-box" style="margin-top: 5px;">
          <div id="roadMap-box-content" class="{ $isLogin ? ' ' : 'blurred' }"> 
            안녕하세요
          </div>
        </div>
      </div>

      <div id="additional-content"> <!--커뮤니티 -->
        <t style="color: #a3a3a3; font-size: 25px; font-weight: bold;">추가 컨텐츠 박스</t>
        <div id="additional-content-box" style="margin-top: 5px;">안녕하세요</div>
      </div>
      

    </div>
  </div>

  <div class="footer-container">
    <Footer />
  </div>
</main>

<style>
  

  main {
    height: 100vh;
    align-items: center;
    overflow: hidden;
  }

  .main-container {
    display: flex;
    justify-content: center;
    height: calc(100vh - 150px);
    transition: transform 0.5s ease, margin 0.5s ease;
    color: white;
    margin-top: 30px;
    
  }

  .content {
    display: grid;
    grid-template-columns: 0 456px 456px 1fr auto;
    grid-template-rows: 350px 415px 1fr;
    column-gap: 35px;
    row-gap: 6px;
    overflow-y: hidden; /* 기본적으로 스크롤바 숨김 */
    overflow-x: hidden; /* 가로 스크롤 방지 */
    height: 100%;
    scrollbar-gutter: stable; /* 스크롤바 공간을 고정 */
  }

  /* 마우스 올렸을 때 세로 스크롤바 표시 */
  .content:hover {
    overflow-y: auto; /* 세로 스크롤바 활성화 */
  }

  /* 스크롤바 스타일 */
  .content::-webkit-scrollbar {
    width: 8px; /* 스크롤바 너비 */
  }

  .content::-webkit-scrollbar-thumb {
    background-color: #474747; /* 스크롤바 색상 */
    border-radius: 4px;
  }

  .content::-webkit-scrollbar-track {
    background-color: #242424; /* 스크롤바 배경 */
  }

  /* 스크롤바 오버레이 (선택 사항, 최신 브라우저) */
  .content {
    scrollbar-width: thin; /* Firefox용 */
    scrollbar-color: #474747 #242424; /* 스크롤바 및 트랙 색상 */
  }


  #locked-box-daily {
    display: flex;
    flex-direction: column;
    position: absolute;
    width: 425px;
    height: 285px;
    background-color: #15231c;
    opacity: 0.8;
    align-items: center;
    justify-content: center;
    pointer-events: none; /* 마우스 이벤트 무시 */
    user-select: none;    /* 선택 불가 */
    
  }

  #dailyGoal {
    grid-column: 2;
    grid-row: 1;
  }

  #dailyGoal-box {
    position : relative;
    width: 450px;
    height: 310px;
    background-color: #15231c;
    border: 1px solid #4a7744;
    border-radius: 8px;
    box-sizing: border-box;
    padding: 10px;
  }

  #dailyGoal-box-content {
    
  }

  #recentAlgorithm {
    grid-column: 3;
    grid-row: 1;
  }

  #recentAlgorithm-box {
    box-sizing: border-box;
    width: 450px;
    height: 310px;
    background-color: #151b23;
    border: 1px solid #3d444d;
    border-radius: 8px;
    padding: 10px;
  }

  #community {
    grid-column: 2;
    grid-row: 2;
  }
  

  #community-box {
    box-sizing: border-box;
    width: 450px;
    height: 370px;
    background-color: #151b23;
    border: 1px solid #3d444d;
    border-radius: 8px;
    padding: 10px;
    display: grid;
    grid-template-rows: 50px 335px;
  }

  #locked-box-myProfile {
    display: flex;
    flex-direction: column;
    position: absolute;
    width: 425px;
    height: 352px;
    background-color: #151b23;
    opacity: 0.8;
    align-items: center;
    justify-content: center;
    pointer-events: none; /* 마우스 이벤트 무시 */
    user-select: none;    /* 선택 불가 */
    
  }

  #myProfile {
    grid-column: 3;
    grid-row: 2;
  }

  #myProfile-box {
    position : relative;
    box-sizing: border-box;
    width: 450px;
    height: 372px;
    background-color: #151b23;
    border: 1px solid #3d444d;
    border-radius: 8px;
    box-sizing: border-box;
    padding: 10px;
  }

  #myProfile-box-content {
    
  }

  #roadMap:nth-child(5) {
    grid-column: 4 / 5;
	  grid-row: 1 / 3;
  }

  #roadMap-box {
    box-sizing: border-box;
    width: 200px;
    height: 728px;

    background-color: #151b23;
    border: 1px solid #3d444d;
    border-radius: 8px;
    box-sizing: border-box;
    padding: 10px;
  }

  .blurred {
    filter: blur(4px);
    pointer-events: none; /* 마우스 이벤트 무시 */
    user-select: none;    /* 선택 불가 */
  }

  .disabled {
    pointer-events: none; /* 클릭 이벤트 비활성화 */
    opacity: 3; /* 비활성화 시 시각적 효과 */
    cursor: not-allowed; /* 커서를 비활성화된 모양으로 변경 */
  }
  
  #locked {
    pointer-events: none;
    user-select: none;
  }

  #additional-content-box {
      box-sizing: border-box;
      width: 1184px;
      height: 300px;
      background-color: #151b23;
      border: 1px solid #3d444d;
      border-radius: 8px;
      padding: 10px;
      display: grid;
      grid-template-rows: 50px 335px;
  }
  
  #additional-content-box {
    box-sizing: border-box;
    width: 1184px;
    height: 300px;
    background-color: #151b23;
    border: 1px solid #3d444d;
    border-radius: 8px;
    padding: 10px;
    display: grid;
    grid-template-rows: 50px 335px;
  }

  #additional-content {
    grid-column: 2;
    grid-row: 3;
  }

</style>
