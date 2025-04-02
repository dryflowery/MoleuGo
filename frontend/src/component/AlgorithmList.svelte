<script>
    import { push } from "svelte-spa-router";
    import { isListVisible } from "../lib/store";

    let inputAlgorithm = '';
    $: inputAlgorithm, searchAlgorithm();

    const navigateTo = (algorithmType) => {
        $isListVisible = false;
        push('/visualization/' + algorithmType);
    }

    const searchAlgorithm = () => {
        const checkAlgorithmString = inputAlgorithm.replace(/\s+/g, '').toUpperCase();
        const elements = document.querySelectorAll('.algo-list-index-text, .algo-list-text-div, .algo-divider');

        if(checkAlgorithmString === '') {
            elements.forEach(element => {element.style.display = 'block';});
        }
        else {
            elements.forEach(element => {
                if(element.id.includes(checkAlgorithmString)) {
                    element.style.display = 'block';
                }
                else {
                    element.style.display = 'none';
                }
            });
        }
    }
</script>

<main>
    <div id="algo-list-header-container">
        <span id="algo-list-name">
            알고리즘 리스트
        </span>
    </div>

    <div id="algo-search-container">
        <input type="text" placeholder="Search algorithms..." bind:value={inputAlgorithm}>
        <button id="algo-search-btn">검색</button>
    </div>

    <div id="algorithm-list">
        <div>
            <p class="algo-list-index-text">1. 선형 자료구조</p>
            <div class="algo-list-text-div" id="연결리스트" on:click={() => navigateTo('Linear/LinkedList')}>
                <span>연결 리스트</span>
            </div>

            <div class="algo-list-text-div" id="스택" on:click={()=> navigateTo('Linear/Stack')}>
                <span>스택</span>
            </div>

            <div class="algo-list-text-div" id="큐" on:click={()=> navigateTo('Linear/Queue')}>
                <span>큐</span>
            </div>

            <div class="algo-list-text-div" id="덱" on:click={()=> navigateTo('Linear/Deque')}>
                <span>덱</span>
            </div>
        </div>

        <div class="algo-divider"></div>

        <div>
            <p class="algo-list-index-text">2. 트리 자료구조</p>

            <div class="algo-list-text-div" id="이진탐색트리">
                <span>이진 탐색 트리</span>
            </div>

            <div class="algo-list-text-div" id="힙" on:click={()=> navigateTo('tree/Heap')}>
                <span>힙</span>
            </div>

            <div class="algo-list-text-div" id="AVL트리">
                <span>AVL 트리</span>
            </div>

            <div class="algo-list-text-div" id="레드블랙트리">
                <span>레드-블랙 트리</span>
            </div>

            <div class="algo-list-text-div" id="최소신장트리">
                <span>최소 신장 트리</span>
            </div>

            <div class="algo-list-text-div" id="세그먼트 트리">
                <span>세그먼트 트리</span>
            </div>

            <div class="algo-list-text-div" id="펜윅트리">
                <span>펜윅 트리</span>
            </div>
        </div>

        <div class="algo-divider"></div>

        <div>
           <p class="algo-list-index-text">3. 탐색</p>

           <div class="algo-list-text-div" id="이분탐색" on:click={() => navigateTo('search/BinarySearch')}>
                <span>이분 탐색</span>
            </div>
        </div>
        
        <div class="algo-divider"></div>

        <div>
           <p class="algo-list-index-text">4. 재귀</p>

           <div class="algo-list-text-div" id="하노이탑">
                <span>하노이 탑</span>
            </div>

            <div class="algo-list-text-div" id="NQueen">
                <span>N-Queen</span>
            </div>
        </div>

        <div class="algo-divider"></div>

        <div>
            <p class="algo-list-index-text">5. 정렬</p>

            <div class="algo-list-text-div" id="버블정렬" on:click={()=> navigateTo('sort/BubbleSort')}>
                <span>버블 정렬</span>
            </div>

            <div class="algo-list-text-div" id="선택정렬" on:click={()=> navigateTo('sort/SelectionSort')}>
                <span>선택 정렬</span>
            </div>

            <div class="algo-list-text-div" id="삽입정렬" on:click={()=> navigateTo('sort/InsertionSort')}>
                <span>삽입 정렬</span>
            </div>

            <div class="algo-list-text-div" id="병합정렬">
                <span>병합 정렬</span>
            </div>

            <div class="algo-list-text-div" id="퀵정렬">
                <span>퀵 정렬</span>
            </div>

            <div class="algo-list-text-div" id="기수정렬">
                <span>기수 정렬</span>
            </div>
        </div>

        <div class="algo-divider"></div>

        <div>
            <p class="algo-list-index-text">6. 그래프 탐색</p>

            <div class="algo-list-text-div" id="깊이우선탐색 DFS" on:click={()=> navigateTo('graph/Dfs')}>
                <span>깊이 우선 탐색(DFS)</span>
            </div>

            <div class="algo-list-text-div" id="너비우선탐색 BFS" on:click={()=> navigateTo('graph/Bfs')}>
                <span>너비 우선 탐색(BFS)</span>
            </div>
        </div>

        <div class="algo-divider"></div>

        <div>
            <p class="algo-list-index-text">7. 최단 경로</p>

            <div class="algo-list-text-div" id="다익스트라">
                <span>다익스트라</span>
            </div>

            <div class="algo-list-text-div" id="벨만포드">
                <span>벨만-포드</span>
            </div>

            <div class="algo-list-text-div" id="플로이드워셜">
                <span>플로이드-워셜</span>
            </div>
        </div>

        <div class="algo-divider"></div>

        <div>
            <p class="algo-list-index-text">8. 그래프</p>

            <div class="algo-list-text-div" id="최소공통조상 LCA">
                <span>최소 공동 조상(LCA)</span>
            </div>

            <div class="algo-list-text-div" id="강한연결요소 SCC">
                <span>강한 연결 요소(SCC)</span>
            </div>      
        </div>

        <div class="algo-divider"></div>

        <div>
            <p class="algo-list-index-text">9. 기하</p>
                
            <div class="algo-list-text-div" id="볼록껍질 컨벡스헐" on:click={() => navigateTo('geometry/ConvexHull')}>
                <span>볼록 껍질(Convex Hull)</span>
            </div>
        </div>

        <div class="algo-divider"></div>

        <div>
            <p class="algo-list-index-text">9. 기타</p>
            
            <div class="algo-list-text-div" id="투포인터">
                <span>투 포인터</span>
            </div>

            <div class="algo-list-text-div" id="위상정렬">
                <span>위상 정렬</span>
            </div>
        </div>
    </div>
</main>

<style>
    main {
        height: calc(100vh - 110px);
        width: 400px;
        background-color: #181a1e;
        border-right: 1px solid #3d444d;
        display: flex; /* Flexbox 사용 */
        flex-direction: column; /* 세로로 정렬 */
    }

    #algo-list-header-container {
        padding: 20px 0 0 15px;
        display: flex;
    }

    #algo-list-name {
        padding-top: 4px;
        color: #d3d3d3;
        font-size: 1.2rem;
        font-weight: bold;
    }

    #algo-search-container {
        margin: 10px 0;
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
        margin: 1px 0 0 9px;
        font-size: 1rem;
        cursor: pointer
    }

    #algo-search-btn:hover {
        background-color: #1A6A2D;
    }

    #algorithm-list {
        flex: 1; /* 남은 공간을 채우도록 설정 */
        padding-left: 25px;
        border-radius: 5px;
        color: #333;
        font-size: 1rem;
        overflow-y: auto; 
        overflow-x: hidden; 
        scrollbar-width: none;
        
    }

    #algorithm-list:hover {
        scrollbar-width: auto; 
    }

    #algorithm-list::-webkit-scrollbar {
        width: 10px; 
        display: none; 
    }

    #algorithm-list:hover::-webkit-scrollbar {
        display: block; 
    }

    #algorithm-list::-webkit-scrollbar-track {
        background: #181a1e; 
        border-radius: 4px;
    }

    #algorithm-list::-webkit-scrollbar-thumb {
        background: #656a73; 
        border-radius: 4px; 
        border: 2px solid #1e1e1e; 
    }

    .algo-list-index-text{
        font-size: 1.2rem;
        color: #e4e4e4;
        font-weight: bold;
        margin-left: 2px;
    }

    p{
        margin-bottom: 5px;
    }

    .algo-list-text-div{
        width: 300px;
        height: 35px;
        padding: 5px 5px 5px 6px;
        background-color: #181a1e;
        border-radius: 8px;
        display: flex; 
        align-items: center; 
        color: #BDBDBD; 
        font-size: 1.1rem; 
        margin: 0;
        font-weight: bold;
    }

    .algo-list-text-div:hover {
        background-color: #272a30; 
        color: #BDBDBD; 
        cursor: pointer; 
    }

    .algo-divider {
        width: 90%; 
        height: 1px; 
        background-color: #3d444d; 
        margin: 10px 0;
    }
</style>
