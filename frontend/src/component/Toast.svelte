<script>
    export let message = '';
    export let type = 'success'; // success | error
    export let show = false;
</script>

<div class="toast {type} {show ? 'show' : ''}">
    <span>

        {#if type === 'success'}
            ✅
        {:else}
            ❌
        {/if}
        {message}
    </span>
</div>

<style>
    .toast {
        position: fixed;
        left: 50%;
        transform: translateX(-50%);
        top: 0;
        opacity: 0;
        margin-top: -80px;
        padding: 0;
        height: 0;
        overflow: hidden;

        background-color: #222;
        color: white;
        border-radius: 0.5rem;
        box-shadow: 0 2px 8px rgba(0,0,0,0.3);
        z-index: 1000;

        transition:
                opacity 0.4s ease,
                margin-top 0.4s ease,
                height 0.4s ease,
                padding 0.4s ease;
    }

    .toast.show {
        opacity: 1;
        margin-top: 30px;
        height: auto;
        padding: 1rem 2rem;
    }

    .toast.success {
        background-color: #1c1c1c;
    }

    .toast.error {
        background-color: #a0372e;
    }
</style>
