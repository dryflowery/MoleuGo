<script>
    import { push } from "svelte-spa-router";
    import Footer from "../component/Footer.svelte";
    import Header from "../component/Header.svelte";
    import { onMount } from "svelte";
    import { isListVisible } from "../lib/store";

    onMount(() => $isListVisible = false);
</script>
  
<main>
    <!-- HTML 구조는 header-container, main-container, footer-container으로 구성하기(Header, Footer 없는 페이지 제외) -->
    <!--  해당 클래스의 css는 app.css에 정의 -->
    <div class="header-container">
        <Header/>
    </div>

    <div class="main-container" class:index-list-visible={$isListVisible} class:index-list-invisible={!$isListVisible}>
        <div id="index-txt-container">
            <div id="main-title">알고리즘의 시각화를 경험해보세요</div>
            <div id="sub-title">궁금한 알고리즘 검색하기</div>
            <input type="text" placeholder="Search algorithms...">

            <div>
                <button id="learn-more-btn" on:click={()=> push('/main')}> 더 알아보기</button>
                <!-- 임시로 추가. 나중에 삭제 -->
                <button id="search-start-btn">검색</button> 
            </div>
    
        </div>

        <div id="algo-gif-container">
            <!-- <img src="assets/algoGif.gif" alt="visualized algorithm" id="algo-gif"> -->
            <video src="assets/algoGif.mp4" width="800px" height="500px" autoplay muted loop></video>
        </div>
    </div>

    <div class="footer-container">
        <Footer/>
    </div>
 </main>
 
  
<style>
    .main-container {
        display: flex;
        justify-content: center;
        align-items: center;
    }

    #main-title {
        font-size: 3.5rem;
        font-weight: bold;
        color: #FFFFFF;
    }

    #sub-title {
        font-size: 2rem;
        color: #dedede;
    }

    .main-container input[type="text"] {
        padding: 10px;
        margin-top: 20px;
        border-radius: 5px;
        border: 1px solid #2c3138;
        width: 420px;
        height: 25px;
        background-color: #151b23;
        font-size: 1rem;
        color: #FFFFFF;
    }

    .main-container button {
        padding: 10px 20px;
        margin: 20px 10px 0 0;
        border-radius: 5px;
        border: none;
        color: white;
        width: 213px;
        font-size: 1.1rem;
        cursor: pointer;
    }

    #learn-more-btn {
        background-color: #555;
    }

    #learn-more-btn:hover {
        background-color: #777; 
    }

    #search-start-btn {
        background-color: #28a745;
    }

    #search-start-btn:hover {
        background-color: #218838; 
    }

    #algo-gif-container {
        background-color: #dcddde;
        border-radius: 10px;
        width: 800px;
        height: 500px;
        display: flex;
        justify-content: center;
        align-items: center;
        margin-left: 50px
    }
</style>