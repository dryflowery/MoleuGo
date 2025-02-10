# 버전 관리
1. 개인 branch 생성하고 작업 후 main branch에 merge
2. branch는 feature/{기능}으로 네이밍 (ex. feature/메인 페이지 1)
3. PR시 본문에 개발 내용 상세하게 작성하기

---

# 규칙
1. 여러 페이지에서 사용하는 컴포넌트는 component 폴더에 작성
2. 페이지는 pages 폴더에 작성하고, 기본적으로 header-container, main-container, footer-container 형식으로 작성
3. 전역변수 추가 시 관련 주석 작성

---

# 라우팅
1. routes 폴더에 페이지 이름과 동일한 이름의 파일 작성
2. App.svelte에 url과 함께 routes 파일 추가
3. 필요 시 svelte 파일에도 push or href를 사용해서 경로 추가

---

# 기타
1. 개발 시작 전에 app.css 확인. main, header-container, footer-container 등 전역 css 많음.
2. Index.svelte도 같이 읽어주세요.
3. 1920 * 1080, 100% 배율 환경에서 개발
4. app.css에 main-list-visible, main-list-invisible 있으니까 main-container 개발할 때 쓰세요.