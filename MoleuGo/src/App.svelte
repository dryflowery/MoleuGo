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
  import LinkedList from './routes/LinkedList.svelte';
  import Dfs from './routes/visualization/graph/Dfs.svelte';
  import Bfs from './routes/visualization/graph/Bfs.svelte';

  const routes = {
    '/': Index,
    '/main' : MainPage,
    '/signup': SignupPage,
    '/findpassword' : FindPassword,
    '/main/myPage' : MyPage,
    '/visualization/sort/BubbleSort': BubbleSort,
    '/visualization/sort/SelectionSort': SelectionSort,
    '/visualization/sort/InsertionSort': InsertionSort,
    '/visualization/tree/Heap': Heap,
    '/visualization/geometry/ConvexHull': ConvexHull,
    '/visualization/LinkedList': LinkedList,
    '/visualization/graph/Dfs': Dfs,
    '/visualization/graph/Bfs': Bfs,
  };
</script>

<Router {routes}/>