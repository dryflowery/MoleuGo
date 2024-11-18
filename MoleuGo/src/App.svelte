<script>
  import Router from 'svelte-spa-router';
  import Index from "./routes/Index.svelte";
  import MainPage from './routes/MainPage.svelte';
  import SignupPage from './routes/SignupPage.svelte';
  import FindPassword from './routes/FindPassword.svelte'
  import BubbleSort from './routes/BubbleSort.svelte';
  import MyPage from './routes/MyPage.svelte';

  const routes = {
    '/': Index,
    '/main' : MainPage,
    '/signup': SignupPage,
    '/findpassword' : FindPassword,
    '/visualization/BubbleSort': BubbleSort,
    '/main/myPage' : MyPage
  };
</script>

<Router {routes}/>

