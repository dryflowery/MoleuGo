<main class="sheet">
    <header class="page-title">컨벡스 헐의 작동 원리(스택에 점 넣기)</header>

    <section class="page-content">
        <div class="page-content-img">
            <video src="assets/guide/convexhull/4.mp4" autoplay loop muted playsinline style="height: 350px; object-fit: contain"></video>
        </div>

        <div class="page-content-txt">
            정렬된 점들을 하나씩 확인하며 스택에 넣습니다. 스택에 최소 3개 이상의 점이 쌓였을 때부터는, 가장 최근 3개 점이 왼쪽으로 꺾이는지 확인합니다.

            <ul>
                <li>왼쪽으로 꺾이면 → 볼록하므로 그대로 유지</li>
                <li>오른쪽으로 꺾이면 → 오목하므로 중간 점을 제거</li>
            </ul>

            이 과정을 반복해, 볼록한 형태만 남깁니다.
        </div>
    </section>
    <footer class="page-num">4</footer>
</main>
