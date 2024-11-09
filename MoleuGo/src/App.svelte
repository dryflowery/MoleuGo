<script>
  import Router from 'svelte-spa-router';
  import Index from "./routes/Index.svelte";
  import MainPage from './routes/mainPage.svelte';

  const routes = {
    '/': Index,
    '/main' : MainPage
  };
</script>

<Router {routes}/>

