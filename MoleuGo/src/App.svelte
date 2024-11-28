<script>
  import Router from 'svelte-spa-router';
  import Index from "./routes/Index.svelte";
  import MainPage from './routes/MainPage.svelte';
  import SignupPage from './routes/SignupPage.svelte';
  import FindPassword from './routes/FindPassword.svelte';
  import MyPage from './routes/MyPage.svelte';
  import BubbleSort from './routes/visualization/sort/BubbleSort.svelte';
  import SelectionSort from './routes/visualization/sort/SelectionSort.svelte';
  import InsertionSort from './routes/visualization/sort/InsertionSort.svelte';
  import Heap from './pages/visualization/tree/Heap.svelte';

  const routes = {
    '/': Index,
    '/main' : MainPage,
    '/signup': SignupPage,
    '/findpassword' : FindPassword,
    '/main/myPage' : MyPage,
    '/visualization/sort/BubbleSort': BubbleSort,
    '/visualization/sort/SelectionSort': SelectionSort,
    '/visualization/sort/InsertionSort': InsertionSort,
    '/visualization/tree/Heap': Heap
  };
</script>

<Router {routes}/>

