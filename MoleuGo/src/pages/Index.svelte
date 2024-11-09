<script>
    import Footer from "../component/Footer.svelte";
    import Header from "../component/Header.svelte";
</script>
  
<main>
    <!-- HTML 구조는 header-container, main-container, footer-container으로 구성하기(Header, Footer 없는 페이지 제외) -->
    <!--  해당 클래스의 css는 app.css에 정의 -->
    <div class="header-container">
        <Header/>
    </div>

    <div class="main-container">
        <div id="main-left-container">
            <div id="main-title">알고리즘의 시각화를 경험해보세요</div>
            <div id="sub-title">궁금한 알고리즘 검색하기</div>
            <input type="text" placeholder="Search algorithms...">
    
            <div>
                <button id="learn-more-btn">더 알아보기</button>
                <button id="search-start-btn">검색</button>
            </div>
        </div>
     
        <div id="main-right-container">
             <div id="algo-gif-container">
                <img src="assets/algoGif.gif" alt="visualized algorithm" id="algo-gif">
             </div>
        </div>
    </div>

    <div class="footer-container">
        <Footer/>
    </div>
 </main>
 
  
<style>
    .main-container {
        display: grid;
        grid-template-columns: 1030px auto;
    }

    #main-left-container {
        padding: 250px 0px 0px 190px;
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

    #main-left-container input[type="text"] {
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

    #main-left-container button {
        padding: 10px 20px;
        margin: 20px 10px 0px 0px;
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
        margin-top: 150px;
        border-radius: 10px;
        width: 800px;
        height: 500px;
        display: flex;
        justify-content: center;
        align-items: center;
    }
</style>