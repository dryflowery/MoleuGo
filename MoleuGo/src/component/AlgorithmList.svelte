<script>
    let isAlgoType = true;

    const algoListType = () => {
        isAlgoType = !isAlgoType;
    }
</script>

<main>
    <div id="algo-list-header-container">
        <span id="algo-list-name">
            알고리즘 리스트
        </span>
        
        <span id="algo-list-sort-btn">
            {#if isAlgoType}
                 <button id="algo-search-btn" on:click={algoListType}>종류</button>
            {:else}
                <button id="algo-search-btn" style="font-size: 0.9rem;" on:click={algoListType}>난이도</button>
            {/if}
        </span>
    </div>

    <div id="algo-search-container">
        <input type="text" placeholder="Search algorithms...">
        <button id="algo-search-btn">검색</button>
    </div>


    {#if isAlgoType}
        <div id="algorithm-list">
            <div>
                <p class="algo-list-index-text">1. 선형 자료구조</p>
                
                <div class="algo-list-text">
                    <input type="checkbox" /> 연결리스트 <br>
                    <input type="checkbox" /> 스택 <br>
                    <input type="checkbox" /> 큐 <br>
                    <input type="checkbox" /> 덱
                </div>
            </div>

            <div>
                <p class="algo-list-index-text">2. 트리 자료구조</p>
                
                <div class="algo-list-text">
                    <input type="checkbox" /> 이진 탐색 트리 <br>
                    <input type="checkbox" /> 힙 <br>
                    <input type="checkbox" /> AVL트리 <br>
                    <input type="checkbox" /> 레드-블랙 트리 <br>
                    <input type="checkbox" /> 최소 신장 트리 <br>
                    <input type="checkbox" /> 세그먼트 트리 <br>
                    <input type="checkbox" /> 펜윅 트리 <br>
                </div>
            </div>

            <div>
                <p class="algo-list-index-text">3. 탐색</p>
                
                <div class="algo-list-text">
                    <input type="checkbox" /> 이분 탐색
                </div>
            </div>

            <div>
                <p class="algo-list-index-text">4. 재귀</p>
                
                <div class="algo-list-text">
                    <input type="checkbox" /> 하노이 탑 <br>
                    <input type="checkbox" /> N-Queen
                </div>
            </div>

            <div>
                <p class="algo-list-index-text">5. 정렬</p>
                
                <div class="algo-list-text">
                    <input type="checkbox" /> 버블 정렬 <br>
                    <input type="checkbox" /> 선택 정렬 <br>
                    <input type="checkbox" /> 삽입 정렬 <br>
                    <input type="checkbox" /> 병합 정렬 <br>
                    <input type="checkbox" /> 퀵 정렬 <br>
                    <input type="checkbox" /> 기수 정렬 <br>
                </div>
            </div>

            <div>
                <p class="algo-list-index-text">6. 그래프 탐색</p>
                
                <div class="algo-list-text">
                    <input type="checkbox" /> 깊이우선 탐색(DFS) <br>
                    <input type="checkbox" /> 너비 우선 탐색(BFS) 
                </div>
            </div>

            <div>
                <p class="algo-list-index-text">7. 최단 경로</p>
                
                <div class="algo-list-text">
                    <input type="checkbox" /> 다익스트라 <br>
                    <input type="checkbox" /> 벨만-포드 <br>
                    <input type="checkbox" /> 플로이드 워셜 <br>
                </div>
            </div>

            <div>
                <p class="algo-list-index-text">8. 그래프</p>
                
                <div class="algo-list-text">
                    <input type="checkbox" /> 최소 공통 조상(LCA) <br>
                    <input type="checkbox" /> 강한 연결 요소(SCC) <br>
                </div>
            </div>

            <div>
                <p class="algo-list-index-text">9. 기하</p>
                
                <div class="algo-list-text">
                    <input type="checkbox" /> 블록 껍질(Convex Hull) <br>
                </div>
            </div>

            <div>
                <p class="algo-list-index-text">9. 기타</p>
                
                <div class="algo-list-text">
                    <input type="checkbox" /> 투 포인터 <br>
                    <input type="checkbox" /> 위상 정렬 <br>
                </div>
            </div>
        </div>
    {:else}
        <div id="algorithm-list">
            <div>
                <p class="algo-list-index-text">1, 쉬움</p>
                
                <div class="algo-list-text">
                    <input type="checkbox" /> 연결리스트 <br>
                    <input type="checkbox" /> 스택 <br>
                    <input type="checkbox" /> 큐 <br>
                    <input type="checkbox" /> 덱 <br>
                    <input type="checkbox" /> 버블 정렬 <br>
                    <input type="checkbox" /> 선택 정렬 <br>
                    <input type="checkbox" /> 삽입 정렬
                </div>
            </div>

            <div>
                <p class="algo-list-index-text">2. 보통</p>
                
                <div class="algo-list-text">
                    <input type="checkbox" /> 이분 탐색 <br>
                    <input type="checkbox" /> 하노이 탑 <br>
                    <input type="checkbox" /> N-Quuen <br>
                    <input type="checkbox" /> 병합 정렬 <br>
                    <input type="checkbox" /> 퀵 정렬 <br>
                    <input type="checkbox" /> 깊이우선 탐색(DFS) <br>
                    <input type="checkbox" /> 너비우선 탐색(BFS) <br>
                    <input type="checkbox" /> 다익스트라 <br>
                    <input type="checkbox" /> 플로이드 워셜 <br>
                    <input type="checkbox" /> 투 포인터
                </div>
            </div>

            <div>
                <p class="algo-list-index-text">3. 어려움</p>
                
                <div class="algo-list-text">
                    <input type="checkbox" /> 이진 탐색 트르 <br>
                    <input type="checkbox" /> 힙 <br>
                    <input type="checkbox" /> 최소 신장 트리 <br>
                    <input type="checkbox" /> 위상 정렬 <br>
                    <input type="checkbox" /> 강한 연결 요소(SCC) <br>
                    <input type="checkbox" />  레드-블랙 트리 <br>
                    <input type="checkbox" /> AVL트리 <br>
                    <input type="checkbox" /> 벨만 포드 <br>
                    <input type="checkbox" /> 기수 정렬 <br>
                </div>
            </div>

            <div>
                <p class="algo-list-index-text">4. 매우 어려움</p>
                
                <div class="algo-list-text">
                    <input type="checkbox" /> 최소 공통 조상(LCA) <br>
                    <input type="checkbox" /> 블록 껍질(Convex Hull) <br>
                    <input type="checkbox" /> 세그먼트 트리 <br>
                    <input type="checkbox" /> 펜윅 트리
                </div>
            </div>
        </div>
    {/if}
    
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
        padding: 20px 0px 0px 15px;
        display: flex;
    }

    #algo-list-name {
        padding-top: 4px;
        color: #FFFFFF;
        font-size: 1.2rem;
    }

    #algo-list-sort-btn {
        padding-left: 161px;
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

    #algorithm-list {
        margin-top: -640px;
        height: 680px;
        padding-left: 20px; 
        border-radius: 5px;
        color: #333;
        font-size: 1rem;
        overflow-y: auto; /* 수직 스크롤 활성화 */
        overflow-x: hidden; /* 수평 스크롤 숨기기 */
    }

    .algo-list-index-text{
        font-size: 1.2rem;
        color: #FFFFFF;
    }

    .algo-list-text{
        font-size: 1rem;
        color: #8a8888;
    }

    p{
        margin-bottom: 5px;
    }

</style>
