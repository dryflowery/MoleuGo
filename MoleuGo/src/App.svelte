<script>
  import Router from 'svelte-spa-router';
  import Index from "./routes/Index.svelte";
  import MainPage from './routes/MainPage.svelte';
  import SignupPage from './routes/SignupPage.svelte';
  import FindPassword from './routes/FindPassword.svelte';
  import MyPage from './routes/MyPage.svelte';
  import BubbleSort from './routes/BubbleSort.svelte';
  import SelectionSort from './routes/SelectionSort.svelte';
  import InsertionSort from './routes/InsertionSort.svelte';

  const routes = {
    '/': Index,
    '/main' : MainPage,
    '/signup': SignupPage,
    '/findpassword' : FindPassword,
    '/main/myPage' : MyPage,
    '/visualization/BubbleSort': BubbleSort,
    '/visualization/SelectionSort': SelectionSort,
    '/visualization/InsertionSort': InsertionSort
  };
</script>

<Router {routes}/>

