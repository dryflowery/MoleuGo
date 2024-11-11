<script>
    let isDifficultyAsc = true;
    let isTypeAsc = true;

    const changeDiffSortType = () => {
        isDifficultyAsc = !isDifficultyAsc;
    }

    const changeTypeSortType = () => {
        isTypeAsc = !isTypeAsc;
    }
</script>
  
<main>
    <div id="algo-list-header-container">
        <span id="algo-list-name">
            알고리즘 리스트
        </span>
        
        <span id="algo-list-sort-btn">
            {#if isDifficultyAsc}
                <button id="difficulty-asc-sort-btn" on:click={changeDiffSortType}>난이도 ▴</button>
            {:else}
                <button id="difficulty-dsc-sort-btn" on:click={changeDiffSortType}>난이도 ▾</button>
            {/if}

            {#if isTypeAsc}
                <button id="type-sort-btn" on:click={changeTypeSortType}>종류 ▴</button>
            {:else}
                <button id="type-sort-btn" on:click={changeTypeSortType}>종류 ▾</button>
            {/if}
        </span>
    </div>

    <div id="algo-search-container">
        <input type="text" placeholder="Search algorithms...">
        <button id="algo-search-btn">검색</button>
    </div>
</main>
  
<style>
    main {
        height: calc(100vh - 110px);
        width: 400px;
        background-color: #181a1e;
        border-right: 1px solid #3d444d;
        overflow: hidden;
    }

    #algo-list-header-container {
        padding: 20px 0px 0px 20px;
        display: flex;
    }

    #algo-list-name {
        padding-top: 4px;
        color: #FFFFFF;
        font-size: 1.2rem;
    }

    #algo-list-sort-btn {
        padding-left: 83px;
    }

    #algo-list-sort-btn button {
        color: #FFFFFF;
        background-color: #238636;
        border: none;
        border-radius: 4px;
        margin-left: 6px;
        padding: 6px 6px;
        font-size: 1rem;
        cursor: pointer
    }

    #algo-list-sort-btn button:hover {
        background-color: #1A6A2D;
    }

    #algo-search-container {
        margin-top: -10px;
        height: 10px;
        display: flex;
    }

    #algo-search-container input[type="text"] {
        padding: 6px;
        margin-left: 15px;
        border-radius: 7px;
        border: 1px solid #3d444d;
        width: 285px;
        height: 25px;
        background-color: #151b23;
        font-size: 0.9rem;
        color: #FFFFFF;
    }

    #algo-search-btn {
        color: #FFFFFF;
        background-color: #238636;
        border: none;
        border-radius: 4px;
        width: 58.89px;
        height: 37px;
        margin: 1px 0px 0px 9px;
        font-size: 1rem;
        cursor: pointer
    }

    #algo-search-btn:hover {
        background-color: #1A6A2D;
    }
</style>