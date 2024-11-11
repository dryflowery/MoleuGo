<script>
  import Footer from "../component/Footer.svelte";
  import HeaderMainPage from "../component/Header.svelte";
  import { isListVisible } from "../lib/store";
  import { isLogin } from "../lib/store";
  import AlgorithmList from "../component/AlgorithmList.svelte";
  import LoginPopup from "../component/LoginPopup.svelte";

  let userName = 'combung2'; // 예시 -> 나중에 데베에서 끌고와야할것들

</script>

<main>
  <div class="header-container">
    <HeaderMainPage />
  </div>

  <div class="main-container" style="transform: translateX({$isListVisible ? 400 : 200}px); margin-right: {$isListVisible ? 400: 0}px; "> <!--알고리즘 리스트 유무에 따른 위치,크기 조절-->
    <div class="content">

      <div id="dailyGoal"> <!--일일 목표 -->
        <t style="color: #a3a3a3; font-size: 25px; font-weight: bold; color: #4C905E">일일 목표</t>
        <div id="dailyGoal-box" style="margin-top: 5px;"> 

          <!--비 로그인 시 생기는 잠금-->
          {#if !$isLogin}
            <div id="locked-box-daily">
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
        <div id="recentAlgorithm-box" style="margin-top: 5px;"> 최근 알고리즘</div>
      </div>

      <div id="community"> <!--커뮤니티 -->
        <t style="color: #a3a3a3; font-size: 25px; font-weight: bold;">커뮤니티</t>
        <div id="community-box" style="margin-top: 5px;"> 최근 알고리즘</div>
      </div>

      <div id="myProfile"> <!--마이페이지 -->
        <t style="color: #a3a3a3; font-size: 25px; font-weight: bold;">마이페이지</t>
        <div id="myProfile-box" style="margin-top: 5px;">

          {#if !$isLogin}
            <div id="locked-box-myProfile">
              <img src="assets/lock.png" style="height: 130px;">
              <p>로그인 하고 컨텐츠 보기</p>
            </div>

          {/if}

          <div id="myProfile-box-content" class="{ $isLogin ? ' ' : 'blurred' }"> 
            안녕하세요
          </div>

        </div>
      </div>

      <div id="roadMap"> <!--로드맵 -->
        <t style="color: #a3a3a3; font-size: 25px; font-weight: bold;">로드맵</t>
        <div id="roadMap-box" style="margin-top: 5px;">

          {#if !$isLogin}
            <div id="locked-box-roadMap">
              <img src="assets/lock.png" style="height: 130px;">
              <p>로그인 하고 컨텐츠 보기</p>
            </div>

          {/if}

          <div id="roadMap-box-content" class="{ $isLogin ? ' ' : 'blurred' }"> 
            안녕하세요
          </div>

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
    height: 100vh;

  }

  .main-container {
    color: white;
    transition: transform 0.5s ease, margin 0.5s ease;
    justify-content: center;
  }

  .content {
    display: grid;
    grid-column: 2;
    grid-template-columns: 115px 456px 456px 1fr auto;
    grid-template-rows: 350px 462px;
    column-gap: 35px;
    row-gap: 6px;
    margin-top: 25px;
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
    border: 3px solid #4a7744;
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
    border: 3px solid #3d444d;
    border-radius: 8px;
  }

  #community {
    grid-column: 2;
    grid-row: 2;
  }
  

  #community-box {
    box-sizing: border-box;
    width: 450px;
    height: 425px;
    background-color: #151b23;
    border: 3px solid #3d444d;
    border-radius: 8px;
  }

  #locked-box-myProfile {
    display: flex;
    flex-direction: column;
    position: absolute;
    width: 425px;
    height: 400px;
    background-color: #151b23;
    opacity: 0.8;
    align-items: center;
    justify-content: center;
    
  }

  #myProfile {
    grid-column: 3;
    grid-row: 2;
  }

  #myProfile-box {
    position : relative;
    box-sizing: border-box;
    width: 450px;
    height: 425px;
    background-color: #151b23;
    border: 3px solid #3d444d;
    border-radius: 8px;
    padding: 10px;
  }

  #myProfile-box-content {
    
  }

  #locked-box-roadMap {
    display: flex;
    flex-direction: column;
    position: absolute;
    width: 200px;
    height: 765px;
    background-color: #151b23;
    opacity: 0.8;
    align-items: center;
    justify-content: center;
    
  }

  #roadMap:nth-child(5) {
    grid-column: 4 / 5;
	  grid-row: 1 / 3;
  }

  #roadMap-box {
    width: 200px;
    height: 765px;
    background-color: #151b23;
    border: 3px solid #3d444d;
    border-radius: 8px;
  }

  .blurred {
    filter: blur(4px);
  }

</style>
