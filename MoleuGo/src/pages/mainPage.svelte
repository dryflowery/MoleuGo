<!--메인페이지-->
<script>
    import Footer from "../component/Footer.svelte";
    import Header from "../component/revised_version/Header_2.svelte";
    import {isListVisible } from "../lib/store"
    import {isLogin } from "../lib/store"
    import AuthPage from "../component/isAuthpage/authPage.svelte";
    import NoAuthPage from "../component/isAuthpage/noAuthPage.svelte";
    import AlgorithmList from "../component/AlgorithmList.svelte";

</script>
  
<main>
    <!-- HTML 구조는 header-container, main-container, footer-container으로 구성하기(Header, Footer 없는 페이지 제외) -->
    <!--  해당 클래스의 css는 app.css에 정의 -->
    <div class="header-container">
        <Header/>
    </div>

    <div class="main-container" style="transform: translateX({$isListVisible ? 400 : 0}px); " >
        <AuthPage/>
    </div>

    <div class="footer-container">
        <Footer/>
    </div>
 </main>
 
  
<style>
    .main-container {
        color: white;
        transition: transform 0.5s ease;
    }

</style>