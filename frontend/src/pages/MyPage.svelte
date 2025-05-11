<script>
    import Footer from "../component/Footer.svelte";
    import Header from "../component/Header.svelte";
    import Toast from '../component/Toast.svelte';
    import TypewriterText from '../component/TypewriterText.svelte';
    import DailyGoalTypewriter from '../component/DailyGoalTypewriter.svelte';

    import Chart from 'chart.js/auto';
    import ChartDataLabels from 'chartjs-plugin-datalabels';

    import { BAD_REQUEST, CONFLICT, FORBIDDEN, OK, UNAUTHORIZED } from "../lib/httpStatusStore.js";
    import { isListVisible } from "../lib/store"
    import { isLogin } from "../lib/store"
    import { onMount } from "svelte";
    import { writable } from "svelte/store";
    import { push } from "svelte-spa-router";

    let showToast = false;
    let toastMessage = '';
    let toastType = 'success';

    let userName = ""; // 입력된 닉네임
    let savedUserName = ''; // 현재 닉네임 (서버에서 가져온 값)
    let nicknameMessage = '';

    let isLengthValid = false; // 길이 판별
    let isSpecialCharValid = false;

    let inputOldPassword = ""; // 기존 비밀번호 입력값
    let isOldPasswordVisible = false; // 기존 비밀번호 표시 상태

    let inputNewPassword = ""; // 새 비밀번호 입력값
    let isNewPasswordVisible = false; // 새 비밀번호 표시 상태

    let inputNewPassword_verify = ""; // 새 비밀번호 확인용 입력값
    let isNewPasswordVisible_verify = false; // 새 비밀번호 확인용 표시 상태

    let oldPasswordStatusColor = "";
    let oldPasswordIcon = "checkmark-outline";

    let oldPasswordStatus = "";

    let condition1Met = false; // 영문/숫자/특수문자 중 2가지 이상 포함
    let condition2Met = false; // 8자 이상 32자 이하 입력 (공백 제외)
    let condition3Met = false; // 연속 3자 이상 동일한 문자/숫자 제외

    let verifyPasswordMessage = "확인을 위해 새 비밀번호를 다시 입력하세요"; // 확인 메시지
    let verifyPasswordMessageStyle = ""; // 메시지 스타일

    let myEmail = '';

    let currentSetting = null; // 현재 설정 상태
    let showSettingBox = true; // 기본적으로 setting-box 표시

    let newEmail = ""; // 새 이메일 입력값
    let message = '';

    let isLoading = false;
    let isError = false;

    let passwordMessage = '';
    let passwordMessageColor = "#bbbbbb";

    let accountType = "";

    const scaleFactorStore = writable(1);
    let showEmailInfo = false; // 설명 표시 여부

    let dailyGoalYear = 2025; // dailyGoalYear년의 일일 목표 출력
    let achievedCount = Array.from({ length: 371 }, () => 0);
    let weeksByMonth = []; // 각 달의 시작일이 몇 주차인지
    let monthMargin = 13.25;

    // 애니메이션 실행 횟수
    let animationCnt = {
        linkedList: undefined,
        stack: undefined,
        queue: undefined,
        deque: undefined,
        heap: undefined,
        bubbleSort: undefined,
        selectionSort: undefined,
        insertionSort: undefined,
        binarySearch: undefined,
        dfs: undefined,
        bfs: undefined,
        dijkstra: undefined,
        bellmanFord: undefined,
        floydWarshall: undefined,
        convexHull: undefined,
    };

    // 로드맵 노드, 간선 활성화 유무 변수들. 순서는 animationCnt와 같음
    let isActiveNode = Array(Object.keys(animationCnt).length).fill(false);
    let isActiveEdge = Array(Object.keys(animationCnt).length).fill(false);

    // 로드맵 설명 칸 변수
    let isvisible_datastruct = false;
    let isvisible_datastructTree = false;
    let isvisible_algorithm = false;

    // 반응형: 로드맵 정보 호출
    $: dataStructStatus = {
        "연결리스트": isActiveNode[0],
        "스택": isActiveNode[1],
        "큐": isActiveNode[2],
        "덱": isActiveNode[3]
    };

    $: treeDataStructStatus = {
        "힙": isActiveNode[4]
    };

    $: algorithmStructStatus = {
        "버블정렬": isActiveNode[5],
        "선택정렬": isActiveNode[6],
        "삽입정렬": isActiveNode[7],
        "이분탐색": isActiveNode[8],
        "DFS": isActiveNode[9],
        "BFS": isActiveNode[10],
        "다익스트라": isActiveNode[11],
        "벨만포드": isActiveNode[12],
        "플로이드워셜": isActiveNode[13],
        "볼록 껍질": isActiveNode[14]
    };

    // 반응형: 로드맵 정보 호출 - 애니메이션 정보 작성
    $: typewriterText_dataStruct = `자료구조 목록: ${JSON.stringify(dataStructStatus, null, 4)}`;
    $: typewriterText_tree = `트리_자료구조 목록: ${JSON.stringify(treeDataStructStatus, null, 4)}`;
    $: typewriterText_algorithm = `알고리즘 목록: ${JSON.stringify(algorithmStructStatus, null, 4)}`;


    export let text = "";
    export let speed = 20; // 밀리초 단위

    let displayed = "";

    // 글자를 한 글자씩 추가
    $: if (text) {
        displayed = "";
        let i = 0;
        const interval = setInterval(() => {
            displayed += text[i++];
            if (i >= text.length) clearInterval(interval);
        }, speed);
    }

    // 애니메이션 실행 총 횟수
    let totalCount = 0;

    // 일일목표 저장할 배열
    let todayGoals = [];

    // 일일목표 달성 여부 배열
    let isTodayGoals = [];

    // 차트 객체 변수
    let canvasRef;
    let chart;

    // 알고리즘 횟수 테이블 정렬 변수
    let sortOrder = 'default';

    // 알고리즘 한글 매핑
    const labelMap = {
        linkedList: '연결리스트',
        stack: '스택',
        queue: '큐',
        deque: '덱',
        heap: '힙',
        binarySearch: '이분탐색',
        bubbleSort: '버블정렬',
        selectionSort: '선택정렬',
        insertionSort: '삽입정렬',
        dfs: 'DFS',
        bfs: 'BFS',
        dijkstra: '다익스트라',
        bellmanFord: '벨만포드',
        floydWarshall: '플로이드-워셜',
        convexHull: '볼록 껍질'
    };

    // 횟수 퍼센테이지 함수
    const percent = (value) => {
        if (!totalCount) return '0.0%';
        return ((value ?? 0) / totalCount * 100).toFixed(1) + '%';
    };

    const toggleSortOrder = () => {
        if (sortOrder === 'default') sortOrder = 'asc';
        else if (sortOrder === 'asc') sortOrder = 'desc';
        else sortOrder = 'default';
    }

    $: sortedEntries = (() => {
        const entries = Object.entries(animationCnt);

        if (sortOrder === 'asc') {
            return [...entries].sort((a, b) => (a[1] ?? 0) - (b[1] ?? 0));
        } else if (sortOrder === 'desc') {
            return [...entries].sort((a, b) => (b[1] ?? 0) - (a[1] ?? 0));
        } else {
            // 기본 순서는 labelMap에 정의된 순서로 유지
            const defaultOrder = Object.keys(labelMap);
            return defaultOrder.map(key => [key, animationCnt[key]]);
        }
    })();


    // 차트 생성 함수
    const drawAnimationChart = () => {
        const ctx = canvasRef.getContext('2d');

        // 차트 색상들 (순서대로 연결리스트, 스텍, 큐 ... )
        const colorMap = {
            // 자료구조 - 그린
            linkedList: '#4caf50',
            stack: '#43a047',
            queue: '#388e3c',
            deque: '#2e7d32',
            heap: '#1b5e20',

            // 이진탐색 - 블루그린
            binarySearch: '#00acc1',

            // 정렬 - 오렌지
            bubbleSort: '#ffb74d',
            selectionSort: '#ffa726',
            insertionSort: '#fb8c00',

            // 그래프 - 블루퍼플
            dfs: '#7986cb',
            bfs: '#5c6bc0',
            dijkstra: '#3f51b5',
            bellmanFord: '#303f9f',
            floydWarshall: '#1a237e',

            // 기하 - 퍼플핑크
            convexHull: '#ba68c8'
        };

        let labels = [];
        let data = [];
        let colors = [];

        for (const [key, value] of Object.entries(animationCnt)) {
            if (value > 0) {
                labels.push(labelMap[key]);
                data.push(value);
                colors.push(colorMap[key]);
            }
        }

        let total = data.reduce((a, b) => a + b, 0);

        if (total === 0) {
            labels = ['데이터 없음'];
            data = [1];
            colors = ['#4e4e4e'];
        }

        if (chart) chart.destroy(); // 기존 차트 있으면 제거

        chart = new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels,
                datasets: [{
                    data,
                    backgroundColor: colors,
                    borderWidth: 0
                }]
            },
            options: {
                animation: { duration: 2000 },
                hover: {
                    mode: 'nearest',
                    intersect: true
                },
                plugins: {
                    datalabels: {
                        color: '#fff',
                        font: { weight: 'bold', size: 10 },
                        formatter: (value, context) => {
                            const label = context.chart.data.labels[context.dataIndex];
                            return value > 0 ? `${label}` : '';
                        }
                    },
                    legend: { display: false },
                    tooltip: { enabled: true }
                },
                cutout: '30%'
            },
            plugins: [ChartDataLabels,
                {
                    id: 'centerText',
                    beforeDraw(chart) {
                        const { width, height, ctx } = chart;
                        const total = Object.values(animationCnt).reduce((sum, v) => sum + v, 0);

                        ctx.save();
                        ctx.font = 'bold 15px sans-serif';
                        ctx.fillStyle = '#ffffff';
                        ctx.textAlign = 'center';
                        ctx.textBaseline = 'middle';
                        ctx.fillText(`총합: ${total}`, width / 2, height / 2);
                        ctx.restore();
                    }
                }
            ]
        });
    }

    const socialIcons = {
        google: {
            src: 'assets/mypage/google.png',
            alt: '구글 계정'
        }
    };

    const showToastMessage = (message, type, timeout) => {
        toastMessage = message;
        toastType = type;
        showToast = true;

        if (timeout) {
            setTimeout(() => {
                showToast = false;
            }, 2000); // 2초 후 자동 숨김
        }
    };

    const requestEmailChange = async () => {
        isLoading = true;
        isError = false;
        message = '메일을 보내는 중...';

        try {
            const response = await fetch('/user/change-email-request', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                credentials: 'include',
                body: JSON.stringify({
                    email: newEmail
                })
            });

            if (response.status === OK) {
                showToastMessage("인증 메일이 전송되었습니다. 메일함을 확인해주세요.", "success", true);
            }
            else if (response.status === CONFLICT) {
                showToastMessage("이미 사용 중인 이메일입니다.", "error", true);
            }
            else {
                showToastMessage("이메일 변경 요청에 실패했습니다.", "error", true);
            }
        }
        catch (err) {
            console.error(err);
            showToastMessage("오류가 발생했습니다. 다시 시도해주세요.", "error", true);
        }
        finally {
            message = '';
            isLoading = false;
        }
    }

    const updateScaleFactor = () => {
        const width = window.innerWidth;
        const isHighResolution = width >= 2560;
        scaleFactorStore.set(isHighResolution ? 1.333 : 1);
    }

    updateScaleFactor();
    window.addEventListener("resize", updateScaleFactor);

    let scaleFactor = 0;

    scaleFactorStore.subscribe((value) => (scaleFactor = value));

    let currentHeight = 525 * scaleFactor; // 기본 높이
    let currentHeight_U = 224 * scaleFactor; // 기본 높이

    // 비밀번호 조건 확인 함수
    const validateNewPassword = (password) => {
        // 1. 영문/숫자/특수문자 중 2가지 이상 포함
        condition1Met = [
            /[a-zA-Z]/.test(password), // 영문
            /[0-9]/.test(password), // 숫자
            /[^a-zA-Z0-9\s]/.test(password), // 특수문자
        ].filter(Boolean).length >= 2;

        // 2. 8자 이상 32자 이하 입력 (공백 제외)
        condition2Met = password.replace(/\s/g, "").length >= 8 && password.length <= 32;

        // 3. 연속 3자 이상 동일한 문자/숫자 제외
        condition3Met = !/(.)\1\1/.test(password);

        // 모든 조건 충족 여부 반환
        return condition1Met && condition2Met && condition3Met;
    }

    // 비밀번호 변경 버튼 클릭
    const saveNewPassword = async () => {
        passwordMessage = "";

        if (!inputOldPassword || !inputNewPassword || !inputNewPassword_verify) {
            passwordMessage = "모든 항목을 입력해주세요.";
            passwordMessageColor = "red";
            return;
        }

        const response = await fetch("/user/change-password", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                currentPassword: inputOldPassword,
                newPassword: inputNewPassword,
                verifyNewPassword: inputNewPassword_verify
            })
        });

        if (response.status === OK) {
            passwordMessage = "비밀번호가 성공적으로 변경되었습니다.";
            passwordMessageColor = "#3f862d";
            inputOldPassword = "";
            inputNewPassword = "";
            inputNewPassword_verify = "";
            showToastMessage("성공적으로 업데이트되었습니다.", "success", true);
        }
        else if (response.status === FORBIDDEN) {
            passwordMessage = "현재 비밀번호가 틀렸습니다.";
            passwordMessageColor = "#892A35";
        }
        else if (response.status === BAD_REQUEST) {
            passwordMessage = "새 비밀번호가 유효하지 않습니다.";
            passwordMessageColor = "#892A35";
        }
        else if (response.status === UNAUTHORIZED) {
            passwordMessage = "로그인이 만료되었습니다.";
            passwordMessageColor = "#892A35";
        }
        else {
            passwordMessage = "비밀번호 변경에 실패했습니다.";
            passwordMessageColor = "#892A35";
        }
    }

    // 닉네임 검증 함수
    const validateUserName = (name) => {
        const lengthRegex = /^.{2,10}$/; // 2~10 글자
        const specialCharRegex = /^[^\s!@#$%^&*(),.?":{}|<>]*$/; // 띄어쓰기 및 특수문자 제외

        isLengthValid = lengthRegex.test(name);
        isSpecialCharValid = specialCharRegex.test(name);
    };

    // 닉네임 저장 버튼 클릭 이벤트
    const saveUserName = async () => {
        try {
            const res = await fetch('/user/change-nickname', {
                method: 'POST',
                credentials: 'include',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    newNickname: userName
                })
            });

            if (res.status === OK) {
                await fetchNickname();
                nicknameMessage = '닉네임이 성공적으로 변경되었습니다.';
                isError = false;
                userName = "";
                showToastMessage("성공적으로 업데이트되었습니다.", "success", true);

            }
            else if (res.status === BAD_REQUEST) {
                nicknameMessage = '닉네임 형식이 올바르지 않습니다.';
                isError = true;
            }
            else if (res.status === CONFLICT) {
                nicknameMessage = '이미 사용 중인 닉네임입니다.';
                isError = true;
            }
            else {
                nicknameMessage = '닉네임 변경 실패';
                isError = true;
            }
        }
        catch (error) {
            nicknameMessage = '서버 오류가 발생했습니다.';
            isError = true;
        }
    }

    function toggleOldPasswordVisibility() { // OLD
        isOldPasswordVisible = !isOldPasswordVisible;
    }

    function toggleNewPasswordVisibility() { // NEW
        isNewPasswordVisible = !isNewPasswordVisible;
    }

    function toggleNewPasswordVisibility_verify() { // NEW
        isNewPasswordVisible_verify = !isNewPasswordVisible_verify;
    }

    // 닉네임이 변경될 때마다 검증
    $: validateUserName(userName);

    // 비밀번호 변경 시 조건 확인
    $: validateNewPassword(inputNewPassword);

    // 비밀번호 확인 일치 여부 검사
    $: {
        if (inputNewPassword_verify === "") {
            verifyPasswordMessage = "확인을 위해 새 비밀번호를 다시 입력하세요";
            verifyPasswordMessageStyle = ""; // 기본 스타일
        }
        else if (inputNewPassword_verify === inputNewPassword) {
            verifyPasswordMessage = "비밀번호가 일치합니다";
            verifyPasswordMessageStyle = "color: #238636;"; // 초록색
        }
        else {
            verifyPasswordMessage = "비밀번호가 일치하지 않습니다";
            verifyPasswordMessageStyle = "color: rgb(173, 44, 44);"; // 빨간색
        }
    }

    $: {
        const isHighResolution = window.innerWidth >= 2560; // 화면 너비 체크
        if (!isHighResolution) { // 2560 이상이면 적용하지 않음
            if (currentSetting === null) {
                currentHeight = 525 * scaleFactor;
            }
            else if (currentSetting === "email") {
                currentHeight = 490 * scaleFactor;
            }
            else if (currentSetting === "nickname") {
                currentHeight = 580 * scaleFactor;
            }
            else if (currentSetting === "password") {
                currentHeight = 720 * scaleFactor;
            }

            monthMargin = 13.25
        }
        else {

            if (currentSetting === "password") {
                currentHeight = 600 * scaleFactor;
            } else {
                currentHeight = 525 * scaleFactor; // 고정 높이
            }

            monthMargin = 18.2;
        }
    }

    $: {
        const isHighResolution = window.innerWidth >= 2560; // 화면 너비 체크
        if (!isHighResolution) { // 2560 이상이면 적용하지 않음
            if (currentSetting === null) {
                currentHeight_U = 224 * scaleFactor;
            }
            else if (currentSetting === "email") {
                currentHeight_U = 257 * scaleFactor;
            }
            else if (currentSetting === "nickname") {
                currentHeight_U = 169 * scaleFactor;
            }
            else if (currentSetting === "password") {
                currentHeight_U = 27 * scaleFactor;
            }

            monthMargin = 13.25
        }
        else {

            if (currentSetting === "password") {
                currentHeight_U = 149 * scaleFactor;
            } else {
                currentHeight_U = 224 * scaleFactor; // 고정 높이
            }

            monthMargin = 18.2;
        }
    }

    let roadMap_h = 50 * scaleFactor;
    let activity_h = 700 * scaleFactor;

    let isRoadMapVisible = false;


    $: {
        if (isRoadMapVisible) {
            roadMap_h = 705 * scaleFactor;
            activity_h = 45 * scaleFactor;
        }
        else {
            roadMap_h = 50 * scaleFactor;
            activity_h = 700 * scaleFactor;
        }
    }

    const toggleView = () => {
        isRoadMapVisible = !isRoadMapVisible;
    };

    // 이메일 요청
    const fetchEmail = async () => {
        const emailRes = await fetch('/mypage/get-email', {credentials: 'include'});

        if (emailRes.ok) {
            myEmail = await emailRes.text();
        }
        else {
            myEmail = "비로그인 상태";
        }
    };

    // 닉네임 요청
    const fetchNickname = async () => {
        try {
            const res = await fetch("/mypage/get-nickname", {credentials: "include"});
            if (res.ok) {
                savedUserName = await res.text();
            }
            else {
                savedUserName = 'Guest';
            }
        }
        catch (err) {
            console.error("닉네임 로딩 실패:", err);
        }
    };

    // account_type 요청
    const fetchAccountType = async () => {
        const typeRes = await fetch('/mypage/get-account-type', {credentials: 'include'});

        if (typeRes.ok) {
            accountType = await typeRes.text();
        }
    };

    // 일일 목표 가져오기
    const fetchDailyGoal = async () => {
        const dailyGoalRes = await fetch('/mypage/get-daily-goal', {
            method: 'POST',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(dailyGoalYear)
        });

        achievedCount = await dailyGoalRes.json();
        getMonthStartWeek();
    };

    // 애니메이션 실행 횟수 가져오기
    const fetchAnimationCnt = async () => {
        const animationCntRes = await fetch('/mypage/get-animation-count', {credentials: 'include'});

        if (animationCntRes.ok) {
            const data = await animationCntRes.json();
            animationCnt = { ...animationCnt, ...data };

            totalCount = Object.values(animationCnt)
            .map(v => v ?? 0)
            .reduce((a, b) => a + b, 0);
        }
    };

    // DB에 일일목표 생성
    const fetchTodayGoals = async () => {
        const res = await fetch('/mypage/today-goal', { credentials: 'include' });

        if (res.ok) {
            const data = await res.json();
            todayGoals = data;
        } else {
            console.error("오늘의 목표를 불러오는 데 실패했습니다.");
        }
    };

    // 일일목표 달성여부 확인
    const fetchGoalStatus = async () => {
        const res = await fetch('/mypage/today-goal-status', {
            credentials: 'include',
        });

        if (res.ok) {
            const data = await res.json();
            isTodayGoals = data;
        } else {
            console.error("오늘의 목표 상태를 불러오지 못했습니다.");
        }
    };

    // 로드맵 진행 정도 계산
    const calcRoadMapProgress = () => {
        const values = Object.values(animationCnt);

        for (let i = 0; i < values.length; i++) {
            if(values[i] === 0) {
                break;
            }

            isActiveNode[i] = isActiveEdge[i] = true;
        }
    };

    // 마이페이지 요소 초기화
    const initMypageInfo = async () => {
        getMonthStartWeek();
        await fetchEmail();
        await fetchNickname();
        await fetchAccountType();
        await fetchDailyGoal();
        await fetchAnimationCnt();
        await fetchTodayGoals();
        await fetchGoalStatus();
        await drawAnimationChart();
        calcRoadMapProgress();
    }

    // 페이지 로드될 때 초기화
    onMount(async () => {
        await initMypageInfo();
    });

    // 일반 로그인시 초기화
    $: if ($isLogin) {
        initMypageInfo();
    }

    // 값에 따라 배경색을 결정하는 함수
    const getColorByValue = (value) => {
        if(value === -1) {
            return "transparent";
        }
        else if (value === 0) {
            return "#48413b";
        }
        else if (value === 1) {
            return "#1a5321";
        }
        else if (value === 2) {
            return "#1c6b24";
        }
        else if (value === 3) {
            return "#219e29";
        }
    };

    // 각 달이 몇 주차에 시작하는지 구하는 함수
    const getMonthStartWeek = () => {
        weeksByMonth = [0];
        const jan1 = new Date(dailyGoalYear, 0, 1);

        // 1월 1일이 속한 주의 일요일
        const startOfFirstWeek = new Date(jan1);
        startOfFirstWeek.setDate(jan1.getDate() - jan1.getDay());

        for (let month = 0; month < 12; month++) {
            const firstDayOfMonth = new Date(dailyGoalYear, month, 1);
            const diffInDays = Math.floor((firstDayOfMonth.getTime() - startOfFirstWeek.getTime()) / (1000 * 60 * 60 * 24));
            const weekNumber = Math.floor(diffInDays / 7);

            weeksByMonth.push(weekNumber);
        }
    };

    // N번째 일이 몇 월 몇 일인지 구하는 함수
    const getDateByDayOfYear = (dayOfYear) => {
        const date = new Date(dailyGoalYear, 0);
        date.setDate(dayOfYear);

        return {
            month: date.getMonth() + 1,
            date: date.getDate()
        };
    }

    // 잔디에 커서 올리면 해당 년, 월, 일에 달성한 일일 목표 개수 알려주는 코드
    const showDailyGoalInfo = (idx) => {
        let startDay;

        for(let i = 0; i < achievedCount.length; i++) {
            if(achievedCount[i] !== -1) {
                startDay = idx - i + 1;
                break;
            }
        }

        const { month, date } = getDateByDayOfYear(startDay);
        const msg = `${dailyGoalYear}년 ${month}월 ${date}일 일일 목표 달성 횟수는 ${achievedCount[idx]}회`;
        showToastMessage(msg, "info", false);
    };

    // 잔디에서 커서 치웠을 때 toast 숨기기
    const hideDailyGoalInfo = () => {
        if(toastType === "info") {
            showToast = false;
        }
    };

    // 사용자가 잔디 위의 숫자 클릭해서 보고 싶은 년도로 변경
    const changeDailyGoalYear = (selectedYear) => {
        dailyGoalYear = selectedYear;
        fetchDailyGoal();
    }

</script>

<main>
    <Toast message={toastMessage} type={toastType} show={showToast}/>

    <div class="header-container">
        <Header/>
    </div>

    <div class="main-container"
         style="transform: translateX({$isListVisible ? 400 : 0}px); margin-right: {$isListVisible ? 400: 0}px; ">
        <!--알고리즘 리스트 유무에 따른 위치,크기 조절-->

        <div class="content">

            <div class="left-content ">

                <div id="profile-box"
                     style="height: {currentHeight}px; transition: height 0.3s ease;">

                    <div id="profile-top-container">
                        <span id='profile-title'>내 프로필</span>

                        <div id="profile-image-container">
                            <!--나중에 수정-->
                            <img id="profile-image" src={ $isLogin ? 'assets/mypage/profile/profile_basic.png' : 'assets/mypage/profile/profile_guest.png' }>
                            <button id="profile-edit-Btn"> 설정</button>
                        </div>

                        <div id='nickName'>닉네임: {savedUserName}
                            {#if accountType && socialIcons[accountType]}
                                <img
                                        src={socialIcons[accountType].src}
                                        alt={socialIcons[accountType].alt}
                                        class="social-icon"
                                />
                            {/if}
                        </div>

                    </div>

                    <div id="profile-bottom-container">

                        <span id='setting-title'>기본 정보</span>

                        {#if showSettingBox}
                            <div id="setting-box">
                                <div id="email-setting">
                                    <div id="email-title"> 이메일</div>
                                        <div id="email-icon" on:mouseover={() => (showEmailInfo = true)}
                                             on:mouseout={() => (showEmailInfo = false)}>
                                            <ion-icon name="help-circle-outline"></ion-icon>
                                            {#if showEmailInfo}
                                                <div class="tooltip">

                                                    {#if accountType === 'normal'}
                                                        이메일 인증이 필요합니다.
                                                    {:else if accountType ==='google'}
                                                        소셜계정은 이메일과 비밀번호를 변경할 수 없습니다.
                                                    {:else}
                                                        비로그인 상태입니다.
                                                    {/if}

                                                </div>
                                            {/if}
                                        </div>
                                    <div id="email-output"> {myEmail} </div>

                                    <button
                                            id="email-btn"
                                            on:click={() => { showSettingBox = false; currentSetting = 'email'; }}
                                            disabled={accountType !== 'normal'}>
                                        설정
                                    </button>

                                </div>
                                <div id="nickname-setting">
                                    <div id="nickname-title"> 닉네임</div>
                                    <div id="nickname-output"> {savedUserName} </div>
                                    <button id="nickname-btn"
                                            on:click={() => { showSettingBox = false; currentSetting = 'nickname'; }}
                                            disabled={!$isLogin}>
                                        설정
                                    </button>
                                </div>
                                <div id="password-setting">
                                    <div id="password-title"> 비밀번호</div>


                                    <div id="password-output"> ******</div>

                                    <button
                                            id="password-btn"
                                            on:click={() => { showSettingBox = false; currentSetting = 'password'; }}
                                            disabled={accountType !== 'normal'}
                                    >

                                        설정
                                    </button>
                                </div>
                            </div><!--setting-box 끝-->

                        {:else if currentSetting === 'email'} <!--이메일-->
                            <div class="email-settings">

                                <div id="change-email-Title" style="display: flex; align-items: center; gap: 12px;">
                                    <span style="color: #bbbbbb;">이메일 변경</span>
                                    {#if message}
                                        <span style="font-size: 14px; color: #999;"> {message} </span>
                                    {/if}
                                </div>

                                <div id="email-input">
                                    <input type="text" placeholder="example123@email.com" bind:value="{newEmail}">
                                </div>

                                <div id="email-btn-group">
                                    <button id="userName-edit-Btn"
                                            on:click={() => {currentSetting = null; showSettingBox = true;} }>취소
                                    </button>
                                    <button id="userName-edit-Btn" on:click={requestEmailChange}> 저장</button>
                                </div>
                            </div>

                        {:else if currentSetting === 'nickname'} <!--닉네임-->
                            <div class="nickname-settings">
                                <div id='change-userName-container'>
                                    <div id="change-userName-Title">
                                        <t style="color: #bbbbbb;">현재 닉네임: {savedUserName} </t>
                                    </div>

                                    <div id="change-userName">
                                        <input type="text" placeholder="닉네임 변경 ..." bind:value="{userName}">
                                    </div>

                                    <div id="verify-userName-container">

                                        <div id="Requirements_1"
                                             class:valid={isLengthValid}
                                             class:invalid={!isLengthValid}>
                                            <ion-icon name="checkmark-outline"></ion-icon>
                                            2 ~ 10 글자 사이의 닉네임을 정해주세요
                                        </div>

                                        <div id="Requirements_2"
                                             class:valid={isSpecialCharValid}
                                             class:invalid={!isSpecialCharValid}>
                                            <ion-icon name="checkmark-outline"></ion-icon>
                                            띄어쓰기를 포함한 특수문자 X
                                        </div>

                                        <div id="Requirements_2"
                                             style="color: {isError ? '#892A35' : '#3f862d'}; margin-left: 1rem;">
                                            {nicknameMessage}
                                        </div>


                                    </div>
                                </div>

                                <div id="btn-container">
                                    <button id="userName-edit-Btn"
                                            on:click={() => {currentSetting = null; showSettingBox = true; nicknameMessage = ''; userName = ''; } }>
                                        취소
                                    </button>
                                    <button id="userName-edit-Btn" on:click={saveUserName}>저장</button>
                                </div>

                            </div>

                        {:else if currentSetting === 'password'} <!--비밀번호-->
                            <div class="password-settings">
                                <!--비밀번호 변경 영역-->
                                <div id="change-password-container">

                                    <div id="change-password-Title">
                                        <t style="color: #bbbbbb;">비밀번호 변경</t>
                                        {#if passwordMessage}
                        <span style="margin-left: 1rem; font-size: 14px; color: {passwordMessageColor}; font-weight: bold;">
                          {passwordMessage}
                        </span>
                                        {/if}
                                    </div>

                                    <!-- 기존 비밀번호 입력 -->
                                    <div id="change-userPassword_1" class="password-container">

                                        {#if isOldPasswordVisible}
                                            <!-- 비밀번호 보이기 -->
                                            <input
                                                    type="text"
                                                    placeholder="기존 비밀번호"
                                                    bind:value="{inputOldPassword}">
                                        {:else}
                                            <!-- 비밀번호 숨기기 -->
                                            <input
                                                    type="password"
                                                    placeholder="기존 비밀번호"
                                                    bind:value="{inputOldPassword}">
                                        {/if}

                                        <button
                                                type="button"
                                                class="toggle-password-btn"
                                                on:click={toggleOldPasswordVisibility}>
                                            <ion-icon
                                                    name={isOldPasswordVisible ? "eye-off-outline" : "eye-outline"}></ion-icon>
                                        </button>
                                    </div>

                                    <div id="Requirements_password_1" style="color: {oldPasswordStatusColor};">
                                        <ion-icon
                                                name={oldPasswordIcon}></ion-icon> {oldPasswordStatus || "확인을 위해 비밀번호를 입력해주세요"}
                                    </div>

                                    <!-- 새 비밀번호 입력 -->
                                    <div id="change-userPassword_2" class="password-container_2"
                                         style="margin-top: 11px;">

                                        {#if isNewPasswordVisible}
                                            <!-- 비밀번호 보이기 -->
                                            <input
                                                    type="text"
                                                    placeholder="새 비밀번호"
                                                    bind:value="{inputNewPassword}">
                                        {:else}
                                            <!-- 비밀번호 숨기기 -->
                                            <input
                                                    type="password"
                                                    placeholder="새 비밀번호"
                                                    bind:value="{inputNewPassword}">
                                        {/if}

                                        <button
                                                type="button"
                                                class="toggle-password-btn_2"
                                                on:click={toggleNewPasswordVisibility}>
                                            <ion-icon
                                                    name={isNewPasswordVisible ? "eye-off-outline" : "eye-outline"}></ion-icon>
                                        </button>
                                    </div>

                                    <div id="Requirements_password_3"
                                         style="color: {condition1Met ? '#238636' : '#9198a1'};">
                                        <ion-icon name="checkmark-outline"></ion-icon>
                                        영문/숫자/특수문자 중, 2가지 이상 포함
                                    </div>

                                    <div id="Requirements_password_4"
                                         style="color: {condition2Met ? '#238636' : '#9198a1'};">
                                        <ion-icon name="checkmark-outline"></ion-icon>
                                        8자 이상 32자 이하 입력 (공백 제외)
                                    </div>

                                    <div id="Requirements_password_5"
                                         style="color: {condition3Met ? '#238636' : '#9198a1'};">
                                        <ion-icon name="checkmark-outline"></ion-icon>
                                        연속 3자 이상 동일한 문자/숫자 제외
                                    </div>

                                    <div id="change-userPassword_3" class="password-container_3"
                                         style="margin-top: 11px;">

                                        {#if isNewPasswordVisible_verify}
                                            <!-- 비밀번호 보이기 -->
                                            <input
                                                    type="text"
                                                    placeholder="새 비밀번호 확인"
                                                    bind:value="{inputNewPassword_verify}">
                                        {:else}
                                            <!-- 비밀번호 숨기기 -->
                                            <input
                                                    type="password"
                                                    placeholder="새 비밀번호 확인"
                                                    bind:value="{inputNewPassword_verify}">
                                        {/if}

                                        <button
                                                type="button"
                                                class="toggle-password-btn_2"
                                                on:click={toggleNewPasswordVisibility_verify}>
                                            <ion-icon
                                                    name={isNewPasswordVisible_verify ? "eye-off-outline" : "eye-outline"}></ion-icon>
                                        </button>
                                    </div>

                                    <div id="Requirements_password_6" style={verifyPasswordMessageStyle}>
                                        <ion-icon name="checkmark-outline"></ion-icon> {verifyPasswordMessage}
                                    </div>

                                    <div id="password-btn-group">
                                        <t id="password-search-Btn"> 비밀번호 찾기</t> <!--비밀번호 찾기 페이지로 이동-->
                                        <button id="userName-edit-Btn"
                                                on:click={() => {currentSetting = null; showSettingBox = true; passwordMessage = ''; inputOldPassword = ""; inputNewPassword = ""; inputNewPassword_verify = "";} }>
                                            취소
                                        </button>
                                        <button id="password-save-Btn" on:click={saveNewPassword}>저장</button>
                                    </div>
                                </div> <!--change-password-container 끝-->

                            </div>
                        {/if}
                    </div>
                </div> <!--profile-box 끝-->

                <div id="left-under-container">
                    <div id="left-under-box" style="height: {currentHeight_U}px; transition: height 0.3s ease;">

                        <span id='under-title' style="color: #d1d1d1;">일일목표</span>

                        <div class="dailyGoal-box">
                            {#if isTodayGoals.length > 0 }
                                <DailyGoalTypewriter goals={isTodayGoals} charSpeed={30} lineDelay={350} />
                            {/if}
                        </div>

                    </div>
                </div>

            </div> <!--left-container 끝-->

            <div id="right-container">
                <div id="activity-box"
                     style="height: {activity_h}px; padding-top: {isRoadMapVisible ? '10px' : '20px'}; transition: height 0.3s ease, padding 0.3s ease;"
                    >

                    <div id='activity-title'> 

                        <span class={isRoadMapVisible ? 'activity-title-span-off' : 'activity-title-span-on'}
                            on:click={() => isRoadMapVisible = false}>
                            활동내역
                        </span> 

                        <span> / </span>

                        <span class={isRoadMapVisible ? 'roadmap-title-span-on' : 'roadmap-title-span-off'}
                            on:click={() => isRoadMapVisible = true}>
                            로드맵
                        </span> 

                    </div>
                
                    <div id="activity-top-container">

                        <div class="lawn-container">
                            <div id='lawn-year-container'>
                                {#each [2025, 2024, 2023, 2022, 2021] as year}
                                    <div class="lawn-year {dailyGoalYear === year ? 'selected' : ''}"
                                         on:click={() => changeDailyGoalYear(year)}>{year}
                                    </div>
                                {/each}
                            </div>

                            <div id="lawn-box">

                                {#if !$isLogin}
                                    <div id="overlay-blocker">
                                        <img id='overlay-image' style="width: 5%" src="assets/mypage/lock.png" />
                                    </div>
                                {/if}

                                <!-- 박스 왼쪽(요일 있는 영역) -->
                                <div id="lawn-left">
                                    <div class="nope"></div>
                                    <!-- 요일 -->
                                    <div id="lawn-day-container">
                                        <div class="lawn-day">Mon</div>
                                        <div class="lawn-day">Wed</div>
                                        <div class="lawn-day">Fri</div>
                                    </div>

                                    <div class="nope"></div>
                                </div>

                                <!-- 박스 오른쪽 -->
                                <div id="lawn-right">
                                    <div id="lawn-month-container">
                                        <div class="lawn-month" style="margin-left: {(weeksByMonth[1] - weeksByMonth[0]) * monthMargin}px">Jan</div>
                                        <div class="lawn-month" style="margin-left: {(weeksByMonth[2] - weeksByMonth[1]) * monthMargin}px">Feb</div>
                                        <div class="lawn-month" style="margin-left: {(weeksByMonth[3] - weeksByMonth[2]) * monthMargin}px">Mar</div>
                                        <div class="lawn-month" style="margin-left: {(weeksByMonth[4] - weeksByMonth[3]) * monthMargin}px">Apr</div>
                                        <div class="lawn-month" style="margin-left: {(weeksByMonth[5] - weeksByMonth[4]) * monthMargin}px">May</div>
                                        <div class="lawn-month" style="margin-left: {(weeksByMonth[6] - weeksByMonth[5]) * monthMargin}px">Jun</div>
                                        <div class="lawn-month" style="margin-left: {(weeksByMonth[7] - weeksByMonth[6]) * monthMargin}px">Jul</div>
                                        <div class="lawn-month" style="margin-left: {(weeksByMonth[8] - weeksByMonth[7]) * monthMargin}px">Aug</div>
                                        <div class="lawn-month" style="margin-left: {(weeksByMonth[9] - weeksByMonth[8]) * monthMargin}px">Sep</div>
                                        <div class="lawn-month" style="margin-left: {(weeksByMonth[10] - weeksByMonth[9]) * monthMargin}px">Oct</div>
                                        <div class="lawn-month" style="margin-left: {(weeksByMonth[11] - weeksByMonth[10]) * monthMargin}px">Nov</div>
                                        <div class="lawn-month" style="margin-left: {(weeksByMonth[12] - weeksByMonth[11]) * monthMargin}px">Dec</div>
                                    </div>

                                    <div class="grid-container">
                                        {#each achievedCount as countValue, idx}
                                            <div class="lawn"
                                                 style="background-color: {getColorByValue(countValue)};"
                                                 on:mouseover={() => showDailyGoalInfo(idx)}
                                                 on:mouseleave={hideDailyGoalInfo}>
                                            </div>
                                        {/each}
                                    </div>

                                    <div id="lawn-info-container">
                                        <div id="lawn-goto-main">일일 목표를 달성하세요</div>

                                        <div id="lawn-info">
                                            Less
                                            {#each [0, 1, 2, 3] as color}
                                                <div class="lawn" style="background-color: {getColorByValue(color)};"></div>
                                            {/each}
                                            More
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div id="activity-bottom-container">
                        <div class="chart-title">
                            <span>알고리즘 차트</span>
                        </div>

                        <div class="chart-box">

                            <div class="chart-info">
                                <table>

                                    <thead>
                                        <tr>
                                            <th>알고리즘</th>

                                            <th>
                                                횟수
                                                <button class="sort-btn" on:click={toggleSortOrder}>
                                                  <span class="material-icons">
                                                    {sortOrder === 'default' ? 'drag_handle' : sortOrder === 'asc' ? 'arrow_upward' : 'arrow_downward'}
                                                  </span>
                                                </button>
                                            </th>
                                        </tr>
                                    </thead>

                                    <tbody>
                                        {#each sortedEntries as [key, value]}
                                            <tr>
                                                <td>{labelMap[key]}</td>
                                                <td><strong>{value ?? 0}</strong></td>
                                                <td style="text-align: right;">
                                                    <span style="color: gray;">{percent(value)}</span>
                                                </td>
                                            </tr>
                                        {/each}
                                    </tbody>

                                </table>
                            </div>

                            <div class="chart-img">
                                <!--알고리즘 횟수 차트-->
                                <canvas bind:this={canvasRef}></canvas>
                            </div>

                        </div>

                    </div>
                </div> <!--activity-box 끝-->

                <div class="roadMap" style="height: {roadMap_h}px; transition: height 0.3s ease; overflow: hidden;">

                    <!-- svelte-ignore a11y-no-static-element-interactions -->
                    <div id="goToRoadMap">
                        <!-- svelte-ignore a11y-click-events-have-key-events -->
                        <div id="RoadMapBtn"on:click={toggleView}>
                            <div style="margin-left: 22px;">
                                <ion-icon size="large" name="map-outline"></ion-icon>
                            </div>
                        </div>
                    </div>

                    <!--로드맵 캔버스-->
                    <div class="roadMap-content">

                        <div class="space-ship">
                            <img class="sapce-ship-img"src="../assets/mypage/starship.png"> <!--사용자 현재 위치 표시-->
                        </div>

                        <div class="start-to-linkedlist" class:active-line={isActiveEdge[0]}></div>
                        <div class="linkedlist-to-stack" class:active-line={isActiveEdge[1]}></div>
                        <div class="stack-to-queue" class:active-line={isActiveEdge[2]}></div>
                        <div class="queue-to-deque" class:active-line={isActiveEdge[3]}></div>
                        <div class="deque-to-heap" class:active-line={isActiveEdge[4]}></div>
                        <div class="heap-to-bubbleSort" class:active-line={isActiveEdge[5]}></div>
                        <div class="bubbleSort-to-selectionSort" class:active-line={isActiveEdge[6]}></div>
                        <div class="selectionSort-to-insertionSort" class:active-line={isActiveEdge[7]}></div>
                        <div class="insertionSort-to-binarySearch" class:active-line={isActiveEdge[8]}></div>
                        <div class="binarySearch-to-DFS" class:active-line={isActiveEdge[9]}></div>
                        <div class="DFS-to-BFS" class:active-line={isActiveEdge[10]}></div>
                        <div class="BFS-to-dijkstra" class:active-line={isActiveEdge[11]}></div>
                        <div class="dijkstra-to-bellmanFord" class:active-line={isActiveEdge[12]}></div>
                        <div class="bellmanFord-to-floydWarshall" class:active-line={isActiveEdge[13]}></div>
                        <div class="floydWarshall-to-convexHull" class:active-line={isActiveEdge[14]}></div>


                        <!-- 자료구조 구역 -->
                        <!-- svelte-ignore a11y-no-static-element-interactions -->
                        <div class="data-container"
                            on:mouseenter={() => isvisible_datastruct = true}
                            on:mouseleave={() => isvisible_datastruct = false}>

                            <div class="data-title"> Data-area </div>

                            <div class="linked-list-node" class:active-node-data={isActiveNode[0]}>
                                {#if isvisible_datastruct}
                                    <div class="node-label-dataStruct">연결리스트{#if isActiveNode[0] }*{/if}</div>
                                {/if}
                            </div>

                            <div class="stack-node" class:active-node-data={isActiveNode[1]}>
                                {#if isvisible_datastruct}
                                    <div class="node-label-dataStruct">스택{#if isActiveNode[1] }*{/if} </div>
                                {/if}
                            </div>
                        
                            <div class="queue-node" class:active-node-data={isActiveNode[2]}>
                                {#if isvisible_datastruct}
                                    <div class="node-label-dataStruct">큐{#if isActiveNode[2] }*{/if} </div>
                                {/if}
                            </div>
                        
                            <div class="deque-node" class:active-node-data={isActiveNode[3]}>
                                {#if isvisible_datastruct}
                                    <div class="node-label-dataStruct">덱{#if isActiveNode[3] }*{/if} </div>
                                {/if}
                            </div>
                            
                        </div>

                        <!-- 힙 구역 -->
                        <!-- svelte-ignore a11y-no-static-element-interactions -->
                        <div class="heap-container"
                        on:mouseenter={() => isvisible_datastructTree = true}
                        on:mouseleave={() => isvisible_datastructTree = false}>

                            <div class="heap-title"> Tree-area </div>

                            <div class="heap-node" class:active-node-heap={isActiveNode[4]} >
                                {#if isvisible_datastructTree}
                                    <div class="node-label-dataStructTree">힙{#if isActiveNode[4] }*{/if} </div>
                                {/if}
                            </div>
                              
                        </div>

                        <!-- 알고리즘 구역 -->
                        <!-- svelte-ignore a11y-no-static-element-interactions -->
                        <div class="algo-container"
                        on:mouseenter={() => isvisible_algorithm = true}
                        on:mouseleave={() => isvisible_algorithm = false}> 

                            <div class="Algorithm-title"> Algorithm-area </div>

                            <div class="bubbleSort-node" class:active-node-algo={isActiveNode[5]}>
                                {#if isvisible_algorithm}
                                    <div class="node-label-algorithm">버블정렬{#if isActiveNode[5] }*{/if}</div>
                                {/if}
                            </div>

                            <div class="selectionSort-node" class:active-node-algo={isActiveNode[6]}>
                                {#if isvisible_algorithm}
                                    <div class="node-label-algorithm">선택정렬{#if isActiveNode[6] }*{/if}</div>
                                {/if}
                            </div>

                            <div class="insertionSort-node" class:active-node-algo={isActiveNode[7]}>
                                {#if isvisible_algorithm}
                                    <div class="node-label-algorithm">삽입정렬{#if isActiveNode[7] }*{/if}</div>
                                {/if}
                            </div>

                            <div class="binarySearch-node" class:active-node-algo={isActiveNode[8]}>
                                {#if isvisible_algorithm}
                                    <div class="node-label-algorithm">이분탐색{#if isActiveNode[8]}*{/if}</div>
                                {/if}
                            </div>

                            <div class="DFS-node" class:active-node-algo={isActiveNode[9]}>
                                {#if isvisible_algorithm}
                                    <div class="node-label-algorithm_4">D F S{#if isActiveNode[9]}*{/if}</div>
                                {/if}
                            </div>

                            <div class="BFS-node" class:active-node-algo={isActiveNode[10]}>
                                {#if isvisible_algorithm}
                                    <div class="node-label-algorithm_4">B F S{#if isActiveNode[10]}*{/if}</div>
                                {/if}
                            </div>

                            <div class="dijkstra-node" class:active-node-algo={isActiveNode[11]}>
                                {#if isvisible_algorithm}
                                    <div class="node-label-algorithm_3">다익스트라{#if isActiveNode[11]}*{/if}</div>
                                {/if}
                            </div>

                            <div class="bellmanFord-node" class:active-node-algo={isActiveNode[12]}>
                                {#if isvisible_algorithm}
                                    <div class="node-label-algorithm">벨만포드{#if isActiveNode[12]}*{/if}</div>
                                {/if}
                            </div>

                            <div class="floydWarshall-node" class:active-node-algo={isActiveNode[13]}>
                                {#if isvisible_algorithm}
                                    <div class="node-label-algorithm_1">플로이드 워셜{#if isActiveNode[13]}*{/if}</div>
                                {/if}
                            </div>

                            <div class="convexHull-node" class:active-node-final={isActiveNode[14]}>
                                {#if isvisible_algorithm}
                                    <div class="node-label-algorithm_2">볼록껍질{#if isActiveNode[14]}*{/if}</div>
                                {/if}
                            </div>

                        </div>

                        <div class="ShowRoadMap-Info">

                            {#if isvisible_datastruct}

                                <div class="RoadMap-Info-DataStruct">
                                    <TypewriterText text={typewriterText_dataStruct} speed={20} />
                                </div>
                            
                            {:else if isvisible_datastructTree}

                                <div class="RoadMap-Info-DataStructTree">
                                    <TypewriterText text={typewriterText_tree} speed={20} />
                                </div>
                            {:else if isvisible_algorithm}

                                <div class="RoadMap-Info-Algorithm ">
                                    <TypewriterText text={typewriterText_algorithm} speed={12} />
                                </div>

                            {/if}

                        </div>

                    </div>

                </div>

            </div><!--right-container 끝-->
        </div> <!--content 끝-->
    </div> <!--main-container 끝-->

    <div class="footer-container">
        <Footer/>
    </div>
</main>

<style>

    main {
        align-items: center;
    }

    .main-container {
        color: white;
        transition: transform 0.5s ease, margin 0.5s ease;
        justify-content: center;
        overflow: hidden;
        display: flex; /* 추가 */
        align-items: center; /* 추가 */
        justify-content: center; /* 추가 */

    }

    .content {
        display: grid;
        grid-template-columns: 1fr 2.5fr;
    }


    /* -------------------큰 틀------------------ */
    #profile-box {
        display: grid;
        grid-template-rows: 270px 1fr;
        margin: 0px 0px 0px 0px;
        padding: 20px 0px 0px 0px;
        width: 400px;
        height: 520px;
        background-color: #151b23;
        border: 1px solid #3d444d;
        border-radius: 8px;
        box-sizing: border-box;

        overflow: hidden;


    }

    #left-under-box {
        display: grid;
        grid-template-rows: 35px 1fr;
        margin: 20px 0px 0px 0px;
        padding: 20px 0px 0px 20px;
        width: 400px;
        height: 224px;
        background-color: #131313;
        border: 1px solid #777e86;
        border-radius: 8px;
        box-sizing: border-box;
        overflow: hidden; /* 높이를 초과하는 내용을 숨김 */

    }

    .dailyGoal-box {
        margin: 3px 0px 0px 10px;
        font-size: 0.9rem;
    }

    #left-under-container {
        position: fixed;
        height: 247px;
    }

    /* -------------------큰 틀------------------ */

    /* -------------------프로필 칸------------------ */

    #profile-top-container {
        border-bottom: 1px solid #3d444d;
    }

    #profile-title {
        font-weight: bold;
        font-size: 18px;
        color: #d1d1d1;
        margin-left: 20px;
    }

    #profile-image-container {
        width: 175px;
        height: 175px;
        border-radius: 15%;
        border: 1px solid #949494;
        background-color: rgb(88, 88, 88);
        margin: 10px 0px 0px 110px;
    }

    #profile-image {
        position: absolute;
        width: 175px;
        height: 175px;
        border-radius: 15%;
    }

    #profile-edit-Btn {
        position: relative;
        background-color: #3f3f3f;
        color: white;
        padding: 8px 16px;
        border: 0.5px solid #888888;
        font-size: 13px;
        font-weight: bold;
        border-radius: 7px;
        cursor: pointer;
        box-shadow: 0 2px 5px rgba(0, 0, 0, 0.3); /* Subtle shadow */
        margin: 120px 0px 0px 130px;
        width: 70px;
    }

    #nickName {
        font-weight: bold;
        font-size: large;
        color: #c4c4c4;

        margin: 15px 0px 0px 0px;

        display: flex;
        justify-content: center;
        align-items: center;
        text-align: center;
        flex-wrap: wrap;
    }


    /* -------------------프로필 칸------------------ */

    /* -------------------정보 수정 칸------------------ */

    #profile-bottom-container {
        padding: 20px 0px 0px 20px;
    }

    #setting-title, #under-title, #activity-title {
        font-weight: bold;
        font-size: 1.25rem;
        color: #d1d1d1;
    }

    #activity-title {
        margin-left: 6px;
    }


    .activity-title-span-off {
        filter: brightness(0.4);
        cursor: pointer;
    }

    .activity-title-span-on {
        cursor: pointer;
    }

    .activity-title-span-off:hover {
        filter: brightness(0.8);
    }

    .roadmap-title-span-off {
        filter: brightness(0.4);
        cursor: pointer;
    }

    .roadmap-title-span-on {
        cursor: pointer;
    }

    .roadmap-title-span-off:hover {
        filter: brightness(0.8);
    }

    #setting-box {
        margin-top: 30px;
        display: flex;
        gap: 0.5rem;
        flex-direction: column;
    }

    #email-setting, #nickname-setting, #password-setting {
        display: flex;
        flex-direction: row;
        align-items: center;

    }

    #nickname-title, #password-title {
        padding: 10px 0;
        flex: 0 0 105px;
        color: #c7c7c7;
    }

    #email-title {
        padding: 10px 0;
        flex: 0 0 55px;
        color: #c7c7c7;
    }

    #email-icon {
        flex: 0 0 50px;
        position: relative;
        cursor: pointer;
    }

    #password-output, #nickname-output, #email-output {
        flex: 0 0 180px;
    }

    #email-btn, #nickname-btn, #password-btn {
        position: relative;
        background-color: #151c23;
        border: 1px solid #3d444d;
        color: white;
        padding: 5px 10px;
        font-size: 14px;
        border-radius: 7px;
        cursor: pointer;
        margin: 0px 0px 0px 6px;
        width: 70px;
        height: 33px;
    }

    #email-btn:hover, #nickname-btn:hover, #password-btn:hover {
        background-color: #11171d;
    }


    /* -------------------닉네임 변경 칸------------------ */

    #change-userName-Title {
        margin: 16px 0px 10px 0px;
        font-weight: bold;
    }

    #verify-userName-container {
        margin: 10px 0px 0px 0px;
        font-size: 14px;
        color: #9198a1;
    }

    #change-userName input[type="text"] {
        padding: 6px;
        margin: 0px 0px 0px 0px;
        border-radius: 7px;
        border: 1px solid #3d444d;
        width: 330px;
        height: 21px;
        background-color: #151b23;
        font-size: 0.9rem;
        color: #FFFFFF;
    }

    #change-userName-container {
        padding-left: 1px;
        height: 160px; /* 고정 높이 설정 */
        overflow: hidden; /* 높이를 초과하는 내용을 숨김 */
    }

    #btn-container {
        margin: 25px 0px 0px 190px;
    }


    #userName-edit-Btn {
        position: relative;
        background-color: #3f862d;
        color: white;
        padding: 5px 10px;
        font-size: 14px;
        font-weight: bold;
        border-radius: 7px;
        cursor: pointer;
        box-shadow: 0 2px 5px rgba(0, 0, 0, 0.3);
        margin: 0px 0px 0px 6px;
        width: 70px;
        height: 30px;
    }

    #userName-edit-Btn:hover {
        background-color: #2f6321;
    }

    /* -------------------닉네임 변경 칸 끝 ------------------ */

    /* -------------------비밀번호 변경 칸 ------------------ */


    #change-password-Title {
        margin: 10px 0px 10px 0px;
        font-weight: bold;
    }

    .password-container input {
        padding: 6px;
        margin: 0px 0px 0px 0px;
        border-radius: 7px;
        border: 1px solid #3d444d;
        width: 340px;
        height: 21px;
        background-color: #151b23;
        font-size: 0.9rem;
        color: #FFFFFF;
    }

    .password-container_2 input, .password-container_3 input {
        padding: 6px;
        margin: 0px 0px 0px 0px;
        border-radius: 7px;
        border: 1px solid #3d444d;
        width: 340px;
        height: 21px;
        background-color: #152321;
        font-size: 0.9rem;
        color: #FFFFFF;
    }

    .toggle-password-btn,
    .toggle-password-btn_2 {
        position: absolute;
        top: 58%;
        right: 8px;
        transform: translateY(-50%);
        background: none;
        border: none;
        cursor: pointer;
        font-size: 1.2rem;
        color: #bbbbbb;
        margin-right: 23px;
    }

    .toggle-password-btn:hover,
    .toggle-password-btn_2:hover {
        color: #424242;
    }


    #password-save-Btn {
        position: relative;
        background-color: #3f862d;
        color: white;
        padding: 5px 10px;
        font-size: 14px;
        font-weight: bold;
        border-radius: 7px;
        cursor: pointer;
        box-shadow: 0 2px 5px rgba(0, 0, 0, 0.3); /* Subtle shadow */
        margin: 0px 0px 0px 6px;
        width: 70px;
        height: 30px;
    }

    #password-search-Btn {
        position: relative;
        color: rgb(99, 99, 99);
        padding: 5px 10px;
        font-size: 12px;
        font-weight: bold;
        border-radius: 7px;
        cursor: pointer;
        margin: 0px 95px 0px 0px;
        width: 120px;
        height: 30px;
    }

    #password-btn-group {
        margin-top: 30px;
        margin-left: 0px;
    }

    #password-save-Btn:hover {
        background-color: #1c682b;
    }

    #password-search-Btn:hover {
        text-decoration: underline;
    }

    #profile-edit-Btn:hover {
        background-color: #2c2c2c;
    }

    #change-userName {
        display: flex;
    }

    .password-container, .password-container_2, .password-container_3 {
        position: relative;
        display: inline-block;
        width: 100%;
    }

    #Requirements_password_1 {
        margin: 10px 0px 0px 5px;
        font-size: 14px;
        color: #9198a1;
    }

    #Requirements_password_2 {
        margin: 10px 0px 0px 5px;
        font-size: 14px;
        color: #9198a1;
    }

    #Requirements_password_3 {
        margin: 10px 0px 0px 5px;
        font-size: 14px;
        color: #9198a1;
    }

    #Requirements_password_4 {
        margin: 10px 0px 0px 5px;
        font-size: 14px;
        color: #9198a1;
    }

    #Requirements_password_5 {
        margin: 10px 0px 0px 5px;
        font-size: 14px;
        color: #9198a1;
    }

    #Requirements_password_6 {
        margin: 5px 0px 0px 5px;
        font-size: 14px;
        color: #9198a1;
    }

    #Requirements_1 {
        margin-bottom: 5px;
    }

    #Requirements_2 {
        margin-bottom: 5px;
    }

    #verify-userName-container div.valid {
        color: #238636; /* 조건을 만족하면 초록색 */
    }

    #verify-userName-container div.invalid {
        color: #9c9c9c; /* 조건을 만족하지 않으면 빨간색 */
    }

    /* 유효한 경우 초록색 */
    .success {
        color: #238636;
    }

    /* 중복된 닉네임인 경우 빨간색 */
    .error {
        color: rgb(173, 44, 44);
    }

    /* -------------------비밀번호 변경 칸 끝 ------------------ */

    /* -------------------이메일 변경 칸 ------------------ */

    #change-email-Title {
        margin: 20px 0px 10px 0px;
        font-weight: bold;
    }

    #email-input input[type="text"] {
        padding: 6px;
        margin: 0px 0px 0px 0px;
        border-radius: 7px;
        border: 1px solid #3d444d;
        width: 344px;
        height: 21px;
        background-color: #151b23;
        font-size: 0.9rem;
        color: #FFFFFF;
    }

    #email-btn-group {
        margin: 15px 0px 0px 198px;
    }

    .tooltip {
        position: absolute;
        top: 120%;
        left: 200%;
        transform: translateX(-50%);
        background-color: rgba(50, 104, 50, 0.8);
        color: white;
        padding: 8px 12px;
        border-radius: 8px;
        white-space: nowrap;
        font-size: 0.9rem;
        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.3);
        z-index: 100;
    }


    /* -------------------이메일 변경 칸 끝 ------------------ */

    /* -------------------  활동(잔디밭) 칸 ------------------ */

    #overlay-blocker {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        z-index: 10;
        background-color: rgba(0, 0, 0, 0.3); /* 반투명 처리, 선택 */
        backdrop-filter: blur(3px); /* 뒷배경을 흐림 */
        pointer-events: all; /* 클릭 막기 */
        user-select: none;

        display: flex;
        justify-content: center;
        align-items: center;
    }

    #overlay-image {
        pointer-events: none;
        user-select: none;
    }

    #lawn-box {
        position: relative;

        height: 200px;
        width: 1000px;
        background-color: #151b23;
        border: 2px solid #3d444d;
        border-radius: 5px;
        margin-left: 4px;
        box-sizing: border-box;

        display: grid;
        grid-template-columns: 40px 1fr;
    }

    #lawn-left {
        display: grid;
        grid-template-rows: 50px 111px 1fr;
    }

    #lawn-right {
        display: grid;
        grid-template-rows: 50px 111px 1fr;
    }

    .lawn-day {
        padding-top: 16px;
        padding-left: 5px;
        font-size: 0.8rem;
    }

    #lawn-year-container {
        display: flex;
        margin: 20px 0px 5px 6px;
        font-size: 1.2rem;
        gap: 10px;
    }
    
    .lawn-year {
        cursor: pointer;
        color: #8d8d8d;
    }

    .lawn-year:hover {
        text-decoration: underline;
    }

    .lawn-year.selected {
        color: #1c6b24;
    }

    #lawn-month-container {
        display: flex;
        font-size: 0.8rem;
    }

    .lawn-month {
        margin-top: 30px;
    }

    .grid-container {
        display: grid;
        grid-template-rows: repeat(7, 15px);
        grid-auto-flow: column;
        gap: 1px;
    }

    .lawn {
        width: 11px;
        height: 11px;
        border-radius: 2px;
    }

    #lawn-info-container {
        display: flex;
        align-items: center;
        font-size: 0.8rem;
        color: #a0a0a0;
    }

    #lawn-info {
        gap: 6px;
        padding-left: 700px;
        display: flex;
        align-items: center;
    }

    /* 차트 칸 */
    #activity-box {
        display: grid;
        grid-template-rows: 15px 0.67fr 1fr;
        width: 1050px;
        height: 700px;
        background-color: #151b23;
        border: 1px solid #3d444d;
        border-radius: 8px;
        box-sizing: border-box;
        padding: 0 0 0 20px;
        overflow: hidden;
    }

    /* 알고리즘 실행 횟수 컨테이너 전체 */
    #activity-bottom-container {
        display: grid;
        padding: 15px 28px 0px 0px;
        grid-template-rows: 35px 1fr;
    }

    /* 알고리즘 실행 횟수 컨테이너 CSS 모음 */
    .chart-title {
        font-weight: bold;
        font-size: 1.25rem;
        color: #d1d1d1;
        padding-left: 6px;
    }
    
    /* 차트 CSS */
    .chart-box {
        position: relative;

        height: 300px;
        width: 1000px;
        background-color: #151b23;
        border: 2px solid #3d444d;
        border-radius: 5px;
        margin-left: 4px;
        box-sizing: border-box;
        padding: 15px 10px 10px 40px;

        display: grid;
        grid-template-columns: 1fr 0.6fr;

    }

    .chart-img {
        width: 250px;
        height: 250px;
        margin: auto;
        position: relative;
    }

    .chart-info {
        max-height: 400px;
        overflow-y: hidden;
        overflow-x: hidden;
        scrollbar-gutter: stable;
    }

    /* 스크롤 관련 CSS */
    .chart-info:hover {
        overflow-y: auto;
    }

    .chart-info::-webkit-scrollbar {
        width: 6px;
        opacity: 0;
        transition: opacity 0.2s;
    }

    .chart-info:hover::-webkit-scrollbar {
        opacity: 1;
    }

    .chart-info::-webkit-scrollbar-thumb {
        background-color: #888;
        border-radius: 4px;
    }

    .chart-info::-webkit-scrollbar-track {
        background: transparent;
    }

    /* 차트 테이블 */
    .chart-info table {
        width:85%;
        border-collapse: collapse;
        font-family: Arial, sans-serif;
    }

    .chart-info th, .chart-info td {
        text-align: left;
        padding: 8px 12px;
    }

    .chart-info thead {
        border-bottom: 1px solid #6d6d6d;
    }

    .sort-btn {
        padding: 2px 6px;
        font-size: 0.9em;
        border: 1px solid #151b23;
        background-color: #151b23;
        color: white;
        border-radius: 4px;
        cursor: pointer;
        transition: background-color 0.2s ease;
        width: 32px;
    }

    .sort-btn:hover {
        background-color: #2c2c2c;
    }

    .material-icons {
        font-size: 18px;
        vertical-align: middle;
    }

    canvas {
        width: 100% !important;
        height: 100% !important;
    }

    /* 차트 미디어 쿼리 */
    @media screen and (min-width: 2560px) {

        /* 차트 칸 QHD */
        #activity-box {
            width: 1400px;
            height: 933px;
            grid-template-rows: 30px 0.64fr 1fr;
        }
        .chart-title {
            font-size: 1.5rem;
        }

        /* 차트 */
        .chart-box {
            height: 400px;
            width: 1340px;
            margin-left: 4px;
            padding: 30px 10px 10px 52px;

            display: grid;
            grid-template-columns: 1fr 0.6fr;

        }

        .chart-img {
            width: 340px;
            height: 340px;
            margin: auto;
            position: relative;
        }

        .chart-info table {
            font-size: 1.2rem;
        }

    }



    /* 로드맵 버튼 */
    #goToRoadMap {
        margin: 0 auto;
    }

    #RoadMapBtn {
        display: inline-block;
        transition: transform 0.3s ease-in-out, color 0.3s ease-in-out;
        margin-top: 8px;
        color: #858585;
    }

    #RoadMapBtn:hover {
        animation: shake 0.5s ease-in-out infinite;
        color: #1c682b;
    }

    @keyframes shake {
        0% {
            transform: translateY(0);
        }
        20% {
            transform: translateY(-4px);
        }
        40% {
            transform: translateY(-6px);
        }
        50% {
            transform: translateY(-8px);
        }
        70% {
            transform: translateY(-10px);
        }
        90% {
            transform: translateY(-5px);
        }
        100% {
            transform: translateY(0px);
        }
    }

    /* -------------------활동 칸 끝 ------------------ */

    /* -------------------  로드맵 ------------------ */

    .roadMap {
        display: grid;
        grid-template-rows: 40px 1fr;
        margin: 20px 0px 0px 0px;
        width: 1050px;
        height: 50px;
        background-color: #12161b;
        border: 1px solid #3d444d;
        border-radius: 8px;
        box-sizing: border-box;
        padding: 0px;
    }

    .roadMap-content {
        position: relative;
        padding: 10px;
        margin: 35px;
        width: 955px;
        height: 560px;
        border: 1px solid #282c31;
        border-radius: 15px;
        overflow: hidden;

        background: radial-gradient(ellipse at 50% 80%, #21222c 0%, #020202 60%);
        box-shadow: inset 0 0 20px #383941;
    }

    .data-container {
        position: absolute;
        top: 320px;
        left: 620px;
        width: 300px;
        height: 200px;
        border: 1px solid #4caf50;
        border-radius: 15px;
        box-shadow: inset 0 0 5px #4caf50;
        
        background-color: rgba(76, 175, 80, 0.05);

        transition: transform 0.3s ease, box-shadow 0.3s ease;
        
    }

    .data-container:hover {
        transform: scale(1.03);
        box-shadow: 0 0 20px rgba(100, 255, 100, 0.3);
    }

    .algo-container {
        position: absolute;
        top: 50px;
        left: 50px;
        width: 540px;
        height: 250px;
        border: 1px solid #8473ff;
        border-radius: 15px;
        box-shadow: inset 0 0 5px #8473ff;
        
        background-color: rgba(132, 115, 255, 0.05);
        
        transition: transform 0.3s ease, box-shadow 0.3s ease;
    }

    .algo-container:hover {
        transform: scale(1.03);
        box-shadow: 0 0 20px rgba(132, 115, 255, 0.8);
    }

    .heap-container {
        position: absolute;
        top: 200px;
        left: 620px;
        width: 100px;
        height: 100px;
        border: 1px solid #70af4c;
        border-radius: 15px;
        box-shadow: inset 0 0 5px #60af4c;

        background-color: rgba(96, 175, 76, 0.05);

        transition: transform 0.3s ease, box-shadow 0.3s ease;
    }
    
    .heap-container:hover {
        transform: scale(1.05);
        box-shadow: 0 0 20px rgba(100, 255, 100, 0.3);
    }

    .ShowRoadMap-Info {
        position: absolute;
        top: 340px;
        left: 100px;
        width: 300px;
        height: 175px;
        border-radius: 10px 10px 0px 0px;
    }

    .RoadMap-Info-DataStruct,
    .RoadMap-Info-DataStructTree,
    .RoadMap-Info-Algorithm
    {
        white-space: pre-wrap;
        font-family: monospace;
        padding: 0px 10px 10px 10px;
        font-size: 1rem;
        border-radius: 8px;
    }

    /* 타이틀 */
    .data-title {
        position: absolute;
        font-size: 1.5rem;
        font-weight: 400;
        color: #4caf50;

        writing-mode: vertical-rl;
        text-orientation: mixed;

        left: -35px;
        top: 10px;
    }

    .heap-title {
        position: absolute;
        font-size: 1.2rem;
        font-weight: 400;
        color: #70af4c;

        left: 5px;
        top: -30px;
    }

    .Algorithm-title {
        position: absolute;
        font-size: 1.5rem;
        font-weight: 400;
        color: #8473ff;

        left: 5px;
        top: 260px;
    }

    /* 각 자료구조,알고리즘 이름 호출 CSS */

    @keyframes radarFadeIn {
        from {
            opacity: 0;
            transform: translateX(-50%) scale(0.4);
        }
        to {
            opacity: 1;
            transform: translateX(-50%) scale(1);
        }
    }

    .node-label-dataStruct {
        position: absolute;
        top: 38px; 
        left: 50%;
        transform: translateX(-50%) scale(0.8);
        background-color: rgba(0, 255, 100, 0.1);
        color: #00ff37;
        font-size: 0.85rem;
        font-family: 'Courier New', monospace;
        border: 1px solid #00ff37;
        border-radius: 4px;
        padding: 2px 6px;
        opacity: 0;
        animation: radarFadeIn 0.3s ease-out forwards;
        white-space: nowrap;
    }

    .node-label-dataStructTree {
        position: absolute;
        top: -30px; 
        left: 50%;
        transform: translateX(-50%) scale(0.8);
        background-color: rgba(0, 255, 100, 0.1);
        color: #a6ff00;
        font-size: 0.85rem;
        font-family: 'Courier New', monospace;
        border: 1px solid #a6ff00;
        border-radius: 4px;
        padding: 2px 6px;
        opacity: 0;
        animation: radarFadeIn 0.3s ease-out forwards;
        white-space: nowrap;
    }

    .node-label-algorithm {
        position: absolute;
        top: 17px; 
        left: 50%;
        transform: translateX(-50%) scale(0.8);
        rotate: -45deg;
        background-color: rgba(98, 0, 255, 0.3);
        color: #8473ff;
        font-size: 0.85rem;
        font-family: 'Courier New', monospace;
        border: 1px solid #8473ff;
        border-radius: 4px;
        padding: 4px 6px;
        opacity: 0;
        animation: radarFadeIn 0.3s ease-out forwards;
        white-space: nowrap;
    }

    /*플로이드 워셜 개별 수정*/
    .node-label-algorithm_1 {
        position: absolute;
        top: -3px; 
        left: 50%;
        transform: translateX(-50%) scale(0.8);
        rotate: -45deg;
        background-color: rgba(98, 0, 255, 0.3);
        color: #8473ff;
        font-size: 0.85rem;
        font-family: 'Courier New', monospace;
        border: 1px solid #8473ff;
        border-radius: 4px;
        padding: 4px 6px;
        opacity: 0;
        animation: radarFadeIn 0.3s ease-out forwards;
        white-space: nowrap;
    }

    /* 볼록껍질 개별 수정*/
    .node-label-algorithm_2 {
        position: absolute;
        top: 25px; 
        left: 10%;
        transform: translateX(-50%) scale(0.8);
        rotate: -45deg;
        background-color: rgba(98, 0, 255, 0.3);
        color: #8473ff;
        font-size: 0.85rem;
        font-family: 'Courier New', monospace;
        border: 1px solid #8473ff;
        border-radius: 4px;
        padding: 4px 6px;
        opacity: 0;
        animation: radarFadeIn 0.3s ease-out forwards;
        white-space: nowrap;
    }

    /* 다익스트라 개별 수정*/
    .node-label-algorithm_3 {
        position: absolute;
        top: 20px; 
        left: -40%;
        transform: translateX(-50%) scale(0.8);
        rotate: -45deg;
        background-color: rgba(98, 0, 255, 0.3);
        color: #8473ff;
        font-size: 0.85rem;
        font-family: 'Courier New', monospace;
        border: 1px solid #8473ff;
        border-radius: 4px;
        padding: 4px 6px;
        opacity: 0;
        animation: radarFadeIn 0.3s ease-out forwards;
        white-space: nowrap;
    }

    /* BFS DFS 개별 수정*/
    .node-label-algorithm_4 {
        position: absolute;
        top: 20px; 
        left: -40%;
        transform: translateX(-50%) scale(0.8);
        rotate: -45deg;
        background-color: rgba(98, 0, 255, 0.3);
        color: #8473ff;
        font-size: 0.85rem;
        font-family: 'Courier New', monospace;
        border: 1px solid #8473ff;
        border-radius: 4px;
        padding: 4px 6px;
        opacity: 0;
        animation: radarFadeIn 0.3s ease-out forwards;
        white-space: nowrap;
    }

    /* 사용자 위치 */
    .sapce-ship-img {
        position: absolute;
        top: 500px;
        left: 460px;
        width: 80px;
        height: 80px;
    }

    /* 기본적인 선 디자인(비활성화 기준) */
    .start-to-linkedlist,
    .linkedlist-to-stack,
    .stack-to-queue,
    .queue-to-deque,
    .deque-to-heap,
    .heap-to-bubbleSort,
    .bubbleSort-to-selectionSort,
    .selectionSort-to-insertionSort,
    .insertionSort-to-binarySearch,
    .binarySearch-to-DFS,
    .DFS-to-BFS,
    .BFS-to-dijkstra,
    .dijkstra-to-bellmanFord,
    .bellmanFord-to-floydWarshall,
    .floydWarshall-to-convexHull
    {
        position: absolute;
        border: 2px dashed #636363;
    }

    .active-line {
        border-style: solid;
        border-color: rgb(116, 116, 116);
    }

    /* 각 알고리즘 노드간 선 CSS 모음*/
    .start-to-linkedlist {
        width: 180px;
        height: 100px;

        top: 470px;
        left: 500px;

        border-right: none; 
        border-bottom: none;

        border-radius: 15px 0px 0px 0px;
    }

    .linkedlist-to-stack {
        width: 125px;
        height: 25px;

        top: 445px;
        left: 715px;

        border-top: none; 
        border-left: none;

        border-radius: 0px 0px 15px 0px;
    }

    .stack-to-queue {
        width: 45px;
        height: 40px;

        top: 368px;
        left: 795px;

        border-bottom: none; 
        border-left: none;

        border-radius: 0px 15px 0px 0px;
    }

    .queue-to-deque {
        width: 45px;
        height: 40px;

        top: 368px;
        left: 715px;

        border-bottom: none; 
        border-left: none;
        border-right: none;

        border-radius: 0px 0px 0px 0px;
    }

    .deque-to-heap {
        width: 10px;
        height: 97px;

        top: 267px;
        left: 670px;

        border-top: none; 
        border-right: none;

        border-radius: 0px 0px 0px 15px;
    }

    /* 알고리즘 간선 모음 */
    .heap-to-bubbleSort {
        width: 107px;
        height: 40px;

        top: 250px;
        left: 547px;

        border-bottom: none; 
        border-left: none;
        border-right: none;

        border-radius: 0px 0px 0px 0px;
    }

    .bubbleSort-to-selectionSort {
        width: 40px;
        height: 35px;

        top: 195px;
        left: 527px;

        border-top: none;
        border-bottom: none; 
        border-right: none;

        border-radius: 0px 0px 0px 0px;
    }

    .selectionSort-to-insertionSort {
        width: 40px;
        height: 35px;

        top: 125px;
        left: 527px;

        border-top: none;
        border-bottom: none; 
        border-right: none;

        border-radius: 0px 0px 0px 0px;
    }   

    .insertionSort-to-binarySearch {
        width: 70px;
        height: 10px;

        top: 105px;
        left: 435px;

        border-bottom: none; 
        border-right: none;

        border-radius: 15px 0px 0px 0px;
    }

    .binarySearch-to-DFS {
        width: 77px;
        height: 63px;

        top: 155px;
        left: 357px;

        border-top: none; 
        border-left: none;

        border-radius: 0px 0px 15px 0px;
    }

    .DFS-to-BFS {
        width: 40px;
        height: 40px;

        top: 159px;
        left: 337px;

        border-top: none;
        border-bottom: none; 
        border-right: none;

        border-radius: 0px 0px 0px 0px;
    }   

    .BFS-to-dijkstra {
        width: 89px;
        height: 10px;

        top: 105px;
        left: 247px;

        border-bottom: none;
        border-left: none;

        border-radius: 0px 15px 0px 0px;
    }

    .dijkstra-to-bellmanFord {
        width: 40px;
        height: 35px;

        top: 195px;
        left: 227px;

        border-top: none;
        border-bottom: none; 
        border-right: none;

        border-radius: 0px 0px 0px 0px;
    }

    .bellmanFord-to-floydWarshall {
        width: 40px;
        height: 30px;

        top: 127px;
        left: 227px;

        border-top: none;
        border-bottom: none; 
        border-right: none;

        border-radius: 0px 0px 0px 0px;
    }

    .floydWarshall-to-convexHull {
        width: 80px;
        height: 50px;

        top: 200px;
        left: 127px;

        border-top: none;
        border-right: none;

        border-radius: 0px 0px 0px 15px;
    }


    /* 각 자료구조 별 행성 모음 */
    .linked-list-node,
    .stack-node,
    .queue-node,
    .deque-node,
    .heap-node
    {
        position: absolute;
        width: 30px;
        height: 30px;
        border-radius: 50%;
        border: 2px solid #307432;
        box-shadow: 0 0 7px #4caf50;
    }

    .linked-list-node {
        top: 132px;
        left: 60px;
    }

    .stack-node {
        top: 90px;
        left: 202px;
    }

    .queue-node {
        top: 30px;
        left: 140px;
    }

    .deque-node {
        top: 30px;
        left: 60px;
    }

    .heap-node {
        top: 34px;
        left: 34px;

        border: 2px solid #488028;
        box-shadow: 0 0 5px rgb(19, 100, 22);
    }

    /* 자료구조 지역에 활성화된 노드에 적용되는 스타일 */
    .active-node-data::after {
        content: '';
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        
        width: 50%;
        height: 50%;
        border-radius: 50%;
        background-color: #00a336;
        box-shadow: 0 0 7px #00c943;
    }

    /* 트리 자료구조 지역에 활성화된 노드에 적용되는 스타일 */
    .active-node-heap::after {
        content: '';
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        
        width: 50%;
        height: 50%;
        border-radius: 50%;
        background-color: #39c900;
        box-shadow: 0 0 7px #11c900;
    }

    /* 각 알고리즘 별 행성 모음 */
    .bubbleSort-node,
    .selectionSort-node,
    .insertionSort-node,
    .binarySearch-node,
    .DFS-node,
    .BFS-node,
    .dijkstra-node,
    .bellmanFord-node,
    .floydWarshall-node,
    .convexHull-node {
        position: absolute;
        width: 30px;
        height: 30px;
        border-radius: 35%;
        transform: rotate(45deg);
        border: 2px solid #6256bc;
        box-shadow: 0 0 10px #392ba1;
    }

    .bubbleSort-node {
        top: 181px;
        left: 460px;
    }

    .selectionSort-node {
        top: 110px;
        left: 460px;
    }

    .insertionSort-node {
        top: 40px;
        left: 460px;
    }

    .binarySearch-node {
        top: 70px;
        left: 367px;
    }

    .DFS-node {
        top: 150px;
        left: 270px;
    }

    .BFS-node {
        top: 70px;
        left: 270px;
    }

    .dijkstra-node {
        top: 40px;
        left: 160px;
    }

    .bellmanFord-node {
        top: 110px;
        left: 160px;
    }

    .floydWarshall-node {
        top: 181px;
        left: 160px;
    }

    .convexHull-node {
        top: 110px;
        left: 60px;
        border-radius: 15%;
    }

    .active-node-algo::after {
        content: '';
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        
        width: 50%;
        height: 50%;
        border-radius: 35%;
        background-color: #796ae7;
        box-shadow: 0 0 10px #796ae7;
    }

    .active-node-final::after {
        content: '';
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        
        width: 50%;
        height: 50%;
        border-radius: 5%;
        background-color: #e5e76a;
        box-shadow: 0 0 10px #e7df6a;
    }

    /* 로드맵 미디어 쿼리 */
    @media screen and (min-width: 2560px) {
        .roadMap-content {
            width: 1305px;
            height: 800px;
        }

        .data-container {
            top: 450px;
            left: 826px;
            width: 400px;
            height: 266px;
        }

        .algo-container {
            top: 66px; 
            left: 66px;
            width: 719px;
            height: 333px;
        }

        .heap-container {
            top: 266px;
            left: 826px;
            width: 133px;
            height: 133px;
        }

        /* 사용자 위치 */
        .sapce-ship-img {
            position: absolute;
            top: 666px;
            left: 613.18px;
            width: 106.64px;
            height: 106.64px;
        }

        /* 각 알고리즘 노드간 선 CSS 모음 */
        .start-to-linkedlist {
            width: 239.94px;
            height: 133.3px;

            top: 650px;
            left: 666.5px;

            border-right: none;
            border-bottom: none;

            border-radius: 19.995px 0px 0px 0px;
        }

        .linkedlist-to-stack {
            width: 166.63px;
            height: 33.33px;

            top: 617px;
            left: 952.1px;

            border-top: none;
            border-left: none;

            border-radius: 0px 0px 19.995px 0px;
        }

        .stack-to-queue {
            width: 62px;
            height: 55px;

            top: 510px;
            left: 1057px;

            border-bottom: none;
            border-left: none;

            border-radius: 0px 19.995px 0px 0px;
        }

        .queue-to-deque {
            width: 59.99px;
            height: 53.32px;

            top: 510px;
            left: 952.1px;

            border-bottom: none;
            border-left: none;
            border-right: none;

            border-radius: 0px 0px 0px 0px;
        }

        .deque-to-heap {
            width: 13.33px;
            height: 150px;

            top: 355.01px;
            left: 893.1px;

            border-top: none;
            border-right: none;

            border-radius: 0px 0px 0px 19.995px;
        }

        .heap-to-bubbleSort {
            width: 143px;
            height: 53.32px;

            top: 333.25px;
            left: 727px;

            border-bottom: none;
            border-left: none;
            border-right: none;

            border-radius: 0px 0px 0px 0px;
        }

        .bubbleSort-to-selectionSort {
            width: 53.32px;
            height: 46.66px;

            top: 259.99px;
            left: 702.49px;

            border-top: none;
            border-bottom: none;
            border-right: none;

            border-radius: 0px 0px 0px 0px;
        }

        .selectionSort-to-insertionSort {
            width: 53.32px;
            height: 46.66px;

            top: 166.66px;
            left: 702.49px;

            border-top: none;
            border-bottom: none;
            border-right: none;

            border-radius: 0px 0px 0px 0px;
        }

        .insertionSort-to-binarySearch {
            width: 93.31px;
            height: 13.33px;

            top: 139.96px;
            left: 579.56px;

            border-bottom: none;
            border-right: none;

            border-radius: 19.995px 0px 0px 0px;
        }

        .binarySearch-to-DFS {
            width: 102.64px;
            height: 83.78px;

            top: 206.62px;
            left: 475.08px;

            border-top: none;
            border-left: none;

            border-radius: 0px 0px 19.995px 0px;
        }

        .DFS-to-BFS {
            width: 53.32px;
            height: 53.32px;

            top: 211.95px;
            left: 449.32px;

            border-top: none;
            border-bottom: none;
            border-right: none;

            border-radius: 0px 0px 0px 0px;
        }

        .BFS-to-dijkstra {
            width: 118.64px;
            height: 13.33px;

            top: 139.96px;
            left: 329.55px;

            border-bottom: none;
            border-left: none;

            border-radius: 0px 19.995px 0px 0px;
        }

        .dijkstra-to-bellmanFord {
            width: 53.32px;
            height: 46.66px;

            top: 259.99px;
            left: 302.29px;

            border-top: none;
            border-bottom: none;
            border-right: none;

            border-radius: 0px 0px 0px 0px;
        }

        .bellmanFord-to-floydWarshall {
            width: 53.32px;
            height: 39.99px;

            top: 169.59px;
            left: 302.29px;

            border-top: none;
            border-bottom: none;
            border-right: none;

            border-radius: 0px 0px 0px 0px;
        }

        .floydWarshall-to-convexHull {
            width: 106.64px;
            height: 66.66px;

            top: 266.6px;
            left: 169.99px;

            border-top: none;
            border-right: none;

            border-radius: 0px 0px 0px 19.995px;
        }

        /* 각 알고리즘 별 행성 모음 */
        .linked-list-node,
        .stack-node,
        .queue-node,
        .deque-node,
        .heap-node {
            position: absolute;
            width: 39.99px;
            height: 39.99px;
            border-radius: 50%;
            border: 2px solid #325033;
            box-shadow: 0 0 6.66px #006603;
        }

        .linked-list-node {
            top: 175.96px;
            left: 79.98px;
        }

        .stack-node {
            top: 119.97px;
            left: 269.26px;
        }

        .queue-node {
            top: 39.99px;
            left: 186.62px;
        }

        .deque-node {
            top: 39.99px;
            left: 79.98px;
        }

        .heap-node {
            top: 45.33px;
            left: 45.33px;

            border: 2px solid #488028;
            box-shadow: 0 0 6.66px rgb(32, 121, 35);
        }

        .bubbleSort-node,
        .selectionSort-node,
        .insertionSort-node,
        .binarySearch-node,
        .DFS-node,
        .BFS-node,
        .dijkstra-node,
        .bellmanFord-node,
        .floydWarshall-node,
        .convexHull-node {
            position: absolute;
            width: 39.99px;
            height: 39.99px;
            border-radius: 35%;
            transform: rotate(45deg);
            border: 2px solid #6256bc;
            box-shadow: 0 0 13.33px #392ba1;
        }

        .bubbleSort-node {
            top: 241.87px;
            left: 613.18px;
        }

        .selectionSort-node {
            top: 146.63px;
            left: 613.18px;
        }

        .insertionSort-node {
            top: 53.32px;
            left: 613.18px;
        }

        .binarySearch-node {
            top: 93.31px;
            left: 489.61px;
        }

        .DFS-node {
            top: 199.95px;
            left: 359.91px;
        }

        .BFS-node {
            top: 93.31px;
            left: 359.91px;
        }

        .dijkstra-node {
            top: 53.32px;
            left: 213.28px;
        }

        .bellmanFord-node {
            top: 146.63px;
            left: 213.28px;
        }

        .floydWarshall-node {
            top: 241.87px;
            left: 213.28px;
        }

        .convexHull-node {
            top: 146.63px;
            left: 79.98px;
            border-radius: 15%;
        }

        .data-title {
            position: absolute;
            font-size: 1.8rem;
            font-weight: 400;
            color: #4caf50;

            writing-mode: vertical-rl;
            text-orientation: mixed;

            left: -35px;
            top: 10px;
        }

        .heap-title {
            position: absolute;
            font-size: 1.5rem;
            font-weight: 400;
            color: #70af4c;

            left: 5px;
            top: -35px;
        }

        .Algorithm-title {
            position: absolute;
            font-size: 2rem;
            font-weight: 400;
            color: #8473ff;

            left: 5px;
            top: 340px;
        }

        .ShowRoadMap-Info {
            position: absolute;
            top: 475px;
            left: 100px;
            width: 300px;
            height: 175px;
            border-radius: 10px 10px 0px 0px;
        }

        .RoadMap-Info-DataStruct,
        .RoadMap-Info-DataStructTree,
        .RoadMap-Info-Algorithm
        {
            white-space: pre-wrap;
            font-family: monospace;
            padding: 5px 10px 10px 10px;
            font-size: 1rem;
            border-radius: 8px;
        }

        .node-label-dataStruct {
            top: 50px; 
            left: 50%;
        }

        .node-label-algorithm {
            top: 30px; 
            left: 50%;
        }

        /*플로이드 워셜 개별 수정*/
        .node-label-algorithm_1 {
            position: absolute;
            top: 10px; 
            left: 50%;
            transform: translateX(-50%) scale(0.8);
            rotate: -45deg;
            background-color: rgba(98, 0, 255, 0.3);
            color: #8473ff;
            font-size: 0.85rem;
            font-family: 'Courier New', monospace;
            border: 1px solid #8473ff;
            border-radius: 4px;
            padding: 4px 6px;
            opacity: 0;
            animation: radarFadeIn 0.3s ease-out forwards;
            white-space: nowrap;
        }

        /* 볼록껍질 개별 수정*/
        .node-label-algorithm_2 {
            position: absolute;
            top: 40px; 
            left: 10%;
            transform: translateX(-50%) scale(0.8);
            rotate: -45deg;
            background-color: rgba(98, 0, 255, 0.3);
            color: #8473ff;
            font-size: 0.85rem;
            font-family: 'Courier New', monospace;
            border: 1px solid #8473ff;
            border-radius: 4px;
            padding: 4px 6px;
            opacity: 0;
            animation: radarFadeIn 0.3s ease-out forwards;
            white-space: nowrap;
        }

        /* 다익스트라 개별 수정*/
        .node-label-algorithm_3 {
            position: absolute;
            top: 40px; 
            left: -40%;
            transform: translateX(-50%) scale(0.8);
            rotate: -45deg;
            background-color: rgba(98, 0, 255, 0.3);
            color: #8473ff;
            font-size: 0.85rem;
            font-family: 'Courier New', monospace;
            border: 1px solid #8473ff;
            border-radius: 4px;
            padding: 4px 6px;
            opacity: 0;
            animation: radarFadeIn 0.3s ease-out forwards;
            white-space: nowrap;
        }

        /* BFS DFS 개별 수정*/
        .node-label-algorithm_4 {
            position: absolute;
            top: 40px; 
            left: -10%;
            transform: translateX(-50%) scale(0.8);
            rotate: -45deg;
            background-color: rgba(98, 0, 255, 0.3);
            color: #8473ff;
            font-size: 0.85rem;
            font-family: 'Courier New', monospace;
            border: 1px solid #8473ff;
            border-radius: 4px;
            padding: 4px 6px;
            opacity: 0;
            animation: radarFadeIn 0.3s ease-out forwards;
            white-space: nowrap;
        }

    }


    @media screen and (min-width: 2560px) {
        .main-container {
            transform: scale(1.333); /* 모든 요소를 1.333배 확대 */
            transform-origin: top left; /* 왼쪽 상단 기준 확대 */
        }

        #profile-box {
            grid-template-rows: 345px 1fr;
            width: 533px; /* 400px x 1.333 */
            height: 700px; /* 525px x 1.333 */
        }

        #profile-image {
            position: absolute;
            width: 225px;
            height: 225px;
            border-radius: 15%;
        }

        #profile-title {
            font-weight: bold;
            font-size: 20px;
            color: #d1d1d1;
            margin-left: 20px;
        }

        #profile-edit-Btn {
            margin: 175px 0px 0px 175px;
        }


        #profile-image-container {
            width: 225px;
            height: 225px;
            border: 1px solid #949494;
            background-color: rgb(88, 88, 88);
            margin: 20px 0px 0px 150px;
        }

        #nickName {
            font-size: 1.2rem;
        }

        #email-setting, #nickname-setting, #password-setting {
            display: flex;
            flex-direction: row;
            align-items: center;
            justify-content: space-between; /* 요소 간의 간격을 유지 */
            width: 100%; /* 부모의 너비에 맞게 확장 */
        }

        #nickname-title, #password-title {
            flex: 0 0 105px; /* 제목 영역은 15% 크기로 설정 */
        }

        #email-title {
            flex: 0 0 50px; /* 제목 영역은 15% 크기로 설정 */
        }

        #nickname-output, #password-output {
            flex: 1; /* 출력 영역이 남은 공간을 차지 */
        }

        #email-btn, #nickname-btn, #password-btn {
            flex: 0 0 70px; /* 버튼 영역은 10% 크기로 설정 */
            margin-right: 15px;
        }

        #email-icon {
            flex: 0 0 55px; /* 아이콘 크기 고정 */
            position: relative;
            cursor: pointer;
        }

        #email-output {
            flex: 1; /* 출력 영역이 남은 공간을 차지 */
            text-align: left; /* 텍스트 정렬 */
        }

        #activity-title {
            margin-left: 8px;
        }

        #left-under-box {
            width: 533px; /* 400px x 1.333 */
            height: 298px; /* 224px x 1.333 */
        }

        #lawn-box {
            height: 260px; /* 200px x 1.333 */
            width: 1340px; /* 700px x 1.333 */
            margin-left: 6px;
            grid-template-columns: 50px 1fr;
        }

        #activity-title {
            font-size: 1.5rem;
        }

        #lawn-year-container {
            margin-left: 8px;
            font-size: 1.5rem;
        }

        .lawn-day, .lawn-month {
            font-size: 1rem;
        }

        .lawn-month {
            margin-top: 35px;
        }

        .lawn-day {
            padding-top: 27.7px;
            padding-left: 7px;
        }

        #lawn-right {
            display: grid;
            grid-template-rows: 60px 1fr 50px;
        }

        #lawn-info-container {
            font-size: 1rem;
        }

        #lawn-info {
            padding-left: 950px;
        }

        #activity-bottom-container {
            margin-left: 0px;
        }

        .grid-container {
            grid-template-rows: repeat(7, 15px);
            padding-right: 10px;
            gap: 7px;
        }

        .lawn {
            width: 15px;
            height: 15px;
        }

        #wrote-box, #comment-box {
            width: 444px; /* 333px x 1.333 */
            height: 266px; /* 200px x 1.333 */
        }

        .roadMap {
            width: 1400px; /* 800px x 1.333 */
            height: 67px; /* 50px x 1.333 */
        }

        #RoadMapBtn {
            margin-top: 16px;
        }

        #change-userName input[type="text"] {
            width: 470px;
        }

        #btn-container {
            margin: 25px 0px 0px 328px;
        }

        #email-input input[type="text"] {
            width: 470px;
        }

        #email-btn-group {
            margin: 15px 0px 0px 328px;
        }

        .password-container input {
            width: 470px;
        }

        .password-container_2 input, .password-container_3 input {
            width: 470px;
        }

        #password-btn-group {
            margin-top: 20px;
            margin-left: 236px;
        }

        #password-search-Btn {
            margin: 0px 0px 0px 0px;

        }

        #left-under-box {
            grid-template-rows: 40px 1fr;
        }

        .dailyGoal-box {
            margin: 3px 0px 0px 10px;
            font-size: 1rem;
        }


    }

    #email-btn:disabled,
    #password-btn:disabled,
    #nickname-btn:disabled {
        background-color: #2a2a2a; /* 어두운 회색 배경 */
        color: #777777; /* 흐릿한 글씨색 */
        border: 1px solid #444444;
    }

    .social-icon {
        width: 18px;
        height: 18px;
        margin-left: 3px;
        vertical-align: middle;
    }
</style>
  