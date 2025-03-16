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
  import ConvexHull from './routes/visualization/geometry/ConvexHull.svelte';
  import LinkedList from './routes/visualization/Linear/LinkedList.svelte';
  import Stack from './routes/visualization/Linear/Stack.svelte';
  import Queue from './routes/visualization/Linear/Queue.svelte';
  import Deque from './routes/visualization/Linear/Deque.svelte';
  import Dfs from './routes/visualization/graph/Dfs.svelte';
  import Bfs from './routes/visualization/graph/Bfs.svelte';
  import SignupSuccessPage from "./routes/SignupSuccessPage.svelte";
  import {checkLoginStatus} from "./lib/store.js";
  import {onMount} from "svelte";

  const routes = {
      '/': Index,
      '/main' : MainPage,
      '/signup': SignupPage,
      '/find-password' : FindPassword,
      '/my-page' : MyPage,
      '/visualization/sort/BubbleSort': BubbleSort,
      '/visualization/sort/SelectionSort': SelectionSort,
      '/visualization/sort/InsertionSort': InsertionSort,
      '/visualization/tree/Heap': Heap,
      '/visualization/geometry/ConvexHull': ConvexHull,
      '/visualization/Linear/LinkedList': LinkedList,
      '/visualization/Linear/Stack': Stack,
      '/visualization/Linear/Queue': Queue,
      '/visualization/Linear/Deque': Deque,
      '/visualization/graph/Dfs': Dfs,
      '/visualization/graph/Bfs': Bfs,
      '/signup-success': SignupSuccessPage
  };

  onMount(() => {
      checkLoginStatus();
      setInterval(() => checkLoginStatus(), 10 * 60 * 1000);
  });
</script>

<Router {routes}/>