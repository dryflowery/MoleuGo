<script>
    import { onMount } from 'svelte';

    export let text = "";
    export let speed = 25;

    let displayed = "";

    function typeWriterEffect(str, index = 0) {
        if (index <= str.length) {
            displayed = str.slice(0, index);
            setTimeout(() => typeWriterEffect(str, index + 1), speed);
        }
    }

    onMount(() => {
        if (text) typeWriterEffect(text);
    });

    $: if (text && displayed === "") {
        typeWriterEffect(text);
    }
</script>

<pre class="typewriter">{displayed}</pre>

<style>
    .typewriter {
        font-family: monospace;
        white-space: pre-wrap;
        color: #00ac2b;
    }
</style>
