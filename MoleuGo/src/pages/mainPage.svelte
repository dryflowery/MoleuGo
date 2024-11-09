<script>
    import Footer from "../component/Footer.svelte";
    import Header from "../component/revised_version/Header_2.svelte";
    import { isListVisible } from "../lib/store";
    import { isLogin } from "../lib/store";
    import AuthPage from "../component/isAuthpage/authPage.svelte";
    import NoAuthPage from "../component/isAuthpage/noAuthPage.svelte";
    import AlgorithmList from "../component/AlgorithmList.svelte";
  </script>
  
  <main>
    <div class="header-container">
      <Header />
    </div>
  
    <div class="main-container" style="transform: translateX({$isListVisible ? 400 : 0}px); margin-right: {$isListVisible ? 400 : 0}px;">
      <AuthPage />
    </div>
  
    <div class="footer-container">
      <Footer />
    </div>
  </main>
  
  <style>
    .main-container {
      color: white;
      transition: transform 0.5s ease, margin 0.5s ease;
    }
  </style>
  