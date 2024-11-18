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
  
  <div class="main-container" style="transform: translateX({$isListVisible ? 410 : 205}px); margin-right: {$isListVisible ? 410: 205}px; "> <!--알고리즘 리스트 유무에 따른 위치,크기 조절-->
     <div class="content">

      <div id="content-box">
          <div id="top-container">

            <div id="profile-edit-container"> <!--프로필 사진 및 닉네임 변경 영역-->
              <div id="profile-image"> <!--프로필 사진 영역-->
                <button id="profile-edit-Btn">Edit</button>
              </div>
              
              <div id="change-userName">
                <input type="text" placeholder="닉네임 변경...">
                <button id="userName-edit-Btn">Change</button>
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
  
  main {
    align-items: center;
  }

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
   width: 1100px;
   height: 795px;
   background-color: #151c23;
   border: 3px solid #3d444d;
   border-radius: 8px;
   box-sizing: border-box;
   display: grid;
   grid-template-rows: 1fr 1fr;
  }

  #top-container {
   display: grid;
   grid-template-columns: 0.35fr 0.65fr;
  }

  #bottom-container {
    display: grid;
    grid-template-columns: 0.3fr 0.7fr;
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

  #change-userName {
    display: flex;
  }

  #change-userName input[type="text"] {
    padding: 6px;
    margin: 15px 0px 0px 30px;
    border-radius: 7px;
    border: 1px solid #3d444d;
    width: 150px;
    height: 30px;
    background-color: #151b23;
    font-size: 0.9rem;
    color: #FFFFFF;
  }

  #userName-edit-Btn {
   position: relative;
   background-color: #3d444d;
   color: white;
   border: 1px solid #888888;
   padding: 5px 10px;
   font-size: 14px;
   font-weight: bold;
   border-radius: 4px;
   cursor: pointer;
   box-shadow: 0 2px 5px rgba(0, 0, 0, 0.3); /* Subtle shadow */
   margin: 19px 0px 0px 6px;
   width: 75px;
   height: 37px;
  }

  #userName-edit-Btn:hover {
    background-color: #555b63;
  }
    
  #lawn-container {
   columns: 2;
      
  }

</style>
  