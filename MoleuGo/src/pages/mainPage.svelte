<script>
  import Footer from "../component/Footer.svelte";
  import HeaderMainPage from "../component/Header_mainPage.svelte";
  import { isListVisible } from "../lib/store";
  import { isLogin } from "../lib/store";
  import AlgorithmList from "../component/AlgorithmList.svelte";
</script>

<main>
  <div class="header-container">
    <HeaderMainPage />
  </div>

  <div class="main-container" style="transform: translateX({$isListVisible ? 400 : 200}px); margin-right: {$isListVisible ? 400: 0}px; "> <!--알고리즘 리스트 유무에 따른 위치,크기 조절-->
    <div class="content">

      <div id="dailyGoal"> <!--일일 목표 -->
        <t style="color: #a3a3a3; font-size: 25px; font-weight: bold; color: #4C905E">일일 목표</t>
        <div id="dailyGoal-box" style="margin-top: 5px;"> 일일목표</div>
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
        <div id="myProfile-box" style="margin-top: 5px;"> 마이페이지</div>
      </div>

      <div id="roadMap"> <!--로드맵 -->
        <t style="color: #a3a3a3; font-size: 25px; font-weight: bold;">로드맵</t>
        <div id="roadMap-box" style="margin-top: 5px;"> 로드맵</div>
      </div>

    </div>
  </div>

  <div class="footer-container">
    <Footer />
  </div>
</main>

<style>
  

  main {
    height: 150vh;

  }

  .main-container {
    color: white;
    transition: transform 0.5s ease, margin 0.5s ease;
  }

  .content {
    display: grid;
    grid-column: 2;
    justify-content: center;
    grid-template-columns: 120px 456px 456px 1fr auto;
    grid-template-rows: 350px 480px;
    column-gap: 35px;
    row-gap: 10px;
    margin-top: 25px;
  }

  #dailyGoal {
    grid-column: 2;
    grid-row: 1;
  }

  #dailyGoal-box {
    width: 450px;
    height: 300px;
    background-color: #15231c;
    border: 3px solid #4a7744;
    border-radius: 8px;
  }

  #recentAlgorithm {
    grid-column: 3;
    grid-row: 1;
  }

  #recentAlgorithm-box {
    width: 450px;
    height: 300px;
    background-color: #151b23;
    border: 3px solid #3d444d;
    border-radius: 8px;
  }

  #community {
    grid-column: 2;
    grid-row: 2;
  }

  #community-box {
    width: 450px;
    height: 425px;
    background-color: #151b23;
    border: 3px solid #3d444d;
    border-radius: 8px;
  }

  #myProfile {
    grid-column: 3;
    grid-row: 2;
  }

  #myProfile-box {
    width: 450px;
    height: 425px;
    background-color: #151b23;
    border: 3px solid #3d444d;
    border-radius: 8px;
  }

  #roadMap:nth-child(5) {
    grid-column: 4 / 5;
	  grid-row: 1 / 3;
  }

  #roadMap-box {
    width: 200px;
    height: 786px;
    background-color: #151b23;
    border: 3px solid #3d444d;
    border-radius: 8px;
  }

</style>
