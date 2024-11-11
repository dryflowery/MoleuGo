<script>
  import Router from 'svelte-spa-router';
  import Index from "./routes/Index.svelte";
  import MainPage from './routes/MainPage.svelte';
  import SignupPage from './routes/SignupPage.svelte';

  const routes = {
    '/': Index,
    '/main' : MainPage,
    '/signup': SignupPage
  };
</script>

<Router {routes}/>

