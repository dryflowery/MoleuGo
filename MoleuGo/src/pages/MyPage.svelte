<script>
    import Footer from "../component/Footer.svelte";
    import Header from "../component/Header.svelte";
    import {isListVisible } from "../lib/store"
    import {isLogin } from "../lib/store"
    import { push } from "svelte-spa-router";


  
  </script>
  
<main>
   <div class="header-container">
    <Header />
   </div>
  
  <div class="main-container" style="transform: translateX({$isListVisible ? 500 : 300}px); margin-right: {$isListVisible ? 400: 0}px; "> <!--알고리즘 리스트 유무에 따른 위치,크기 조절-->
     <div class="content">

      <div id="content-box">
          <div id="top-container">

            <div id="profile-edit-container">
              <div id="profile-image">
                <button id="profile-edit-Btn">Edit</button>
              </div>
              
            </div>

            <div id="lawn-container">잔디밭</div>

          </div>

          <div id="bottom-container">
              
          </div>
      </div>
  
    </div>
  </div>
  
  <div class="footer-container">
    <Footer />
  </div>
</main>
  
<style>

 .main-container {
   color: white;
   transition: transform 0.5s ease, margin 0.5s ease;
   justify-content: center;
  }
  
 .content {
   column-gap: 35px;
   row-gap: 6px;
   margin-top: 25px;
  }

 #content-box {
   margin: 25px 0px 0px 200px;
   width: 930px;
   height: 780px;
   background-color: #151c23;
   border: 3px solid #3d444d;
   border-radius: 8px;
   box-sizing: border-box;
   display: grid;
   grid-template-rows: 1fr 1fr;
  }

  #top-container {
   display: grid;
   grid-template-columns: 0.4fr 0.6fr;
  }

  #bottom-container {

  }

  #profile-edit-container {
    columns: 1;
  }

  #profile-image {
  width: 200px;
   height: 200px;
   border-radius: 50%;
   border: 5px solid #3d4d3d;
   background-color: rgb(100, 100, 100);
   margin: 30px 0px 0px 30px;
  }

  #profile-edit-Btn {
   position: relative;
   background-color: #3d444d;
   color: white;
   border: 2px solid #888888;
   padding: 8px 16px;
   font-size: 14px;
   font-weight: bold;
   border-radius: 4px;
   cursor: pointer;
   box-shadow: 0 2px 5px rgba(0, 0, 0, 0.3); /* Subtle shadow */
   margin: 150px 0px 0px 150px
  }

  #profile-edit-Btn:hover {
    background-color: #555b63;
  }
    
  #lawn-container {
   columns: 2;
      
  }

</style>
  