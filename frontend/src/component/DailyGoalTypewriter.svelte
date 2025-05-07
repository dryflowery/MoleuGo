<script>
    export let goals = [];
    export let charSpeed = 30;   // 글자 속도
    export let lineDelay = 400;  // 줄 간격 딜레이

    const labelMap = {
        linkedList: '연결리스트',
        stack: '스택',
        queue: '큐',
        deque: '덱',
        heap: '힙',
        binarySearch: '이분 탐색',
        bubbleSort: '버블 정렬',
        selectionSort: '선택 정렬',
        insertionSort: '삽입 정렬',
        dfs: 'DFS',
        bfs: 'BFS',
        dijkstra: '다익스트라',
        bellmanFord: '벨만-포드',
        floydWarshall: '플로이드-워셜',
        convexHull: '볼록 껍질'
    };

    let lines = [];
    let currentLineIndex = 0;

    $: if (goals.length && lines.length === 0) {
        lines = goals.flatMap(goal => {
            const label = labelMap[goal.algorithm] || goal.algorithm || '알 수 없음';
            const base = { text: `- ${label} 애니메이션을 실행하세요.`, complete: false, typed: '' };
            if (goal.achieved) {
                return [
                    base,
                    { text: `    > 실행 완료!`, complete: true, typed: '' }
                ];
            }
            return [base];
        });

        typeLine(0);
    }

    function typeLine(index) {
        if (index >= lines.length) return;
        let i = 0;
        const full = lines[index].text;

        function typeChar() {
            if (i <= full.length) {
                lines[index].typed = full.slice(0, i);
                i++;
                setTimeout(typeChar, charSpeed);
            } else {
                currentLineIndex++;
                setTimeout(() => typeLine(index + 1), lineDelay);
            }
        }

        typeChar();
    }
</script>

<div class="typewriter">
    {#each lines as line, i (i)}
        <div class:complete-line={line.complete}>
            {line.typed}
            {#if i === currentLineIndex || (i === lines.length - 1 && currentLineIndex >= lines.length)}
                <span class="cursor">|</span>
            {/if}
        </div>
    {/each}
</div>

<style>
    .typewriter {
        font-family: monospace;
        color: #ffffff;
        line-height: 1.5;
        white-space: pre;
    }

    .complete-line {
        color: #00ccff;
        font-weight: bold;
    }

    .cursor {
        display: inline-block;
        animation: blink 1s step-start infinite;
        color: #ffffff;
    }

    @keyframes blink {
        50% { opacity: 0; }
    }
</style>
