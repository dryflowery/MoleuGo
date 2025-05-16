<script>
    import { push } from "svelte-spa-router";
    import Footer from "../component/Footer.svelte";
    import Header from "../component/Header.svelte";
    import { onMount } from "svelte";
    import { fade } from 'svelte/transition';
    import { isListVisible } from "../lib/store";

    onMount(() => 
        $isListVisible = false
    );

    // 자료구조, 알고리즘, 컨텐츠 캔버스 확인변수
    let isvisible_datastruct = true;
    let isvisible_algorithm = false;
    let isvisible_content = false;
    

    // 변수 세개중 하나만 true 유지
    const toggleView = (type) => {
        isvisible_datastruct = type === 'datastruct';
        isvisible_algorithm = type === 'algorithm';
        isvisible_content = type === 'content';

        if (type === 'datastruct') {
            animateRadialGradient(gradientStops_datastruct);
        } else if (type === 'algorithm') {
            animateRadialGradient(gradientStops_algorithm);
        } else {
            animateRadialGradient(gradientStops_content);
        }
    };

    // 반응형: 변수에 대응하는 테두리색 변화
    $: selectedColor = isvisible_datastruct
        ? '#4caf50'
    : isvisible_algorithm
        ? '#8473ff'
    : '#424242';

    // 반응형: 변수에 따라 대응하는 높이 변화
    $: selectedTag = isvisible_datastruct
        ? 'datastruct'
    : isvisible_algorithm
        ? 'algorithm'
    : 'content';

    // 변형 함수들
    const lerp = (start, end, t) => start + (end - start) * t;
    const rgbaToObject = (r, g, b, a, stop) => ({ r, g, b, a, stop });
    const rgbaString = ({ r, g, b, a, stop }) =>
    `rgba(${Math.round(r)}, ${Math.round(g)}, ${Math.round(b)}, ${a.toFixed(2)}) ${stop}`;

    let currentStops = [];

    // 색상 변형 애니메이션 함수
    const animateRadialGradient = (targetStops) => {
        const fromStops = currentStops.length ? currentStops : targetStops.map(stop => ({ ...stop }));

        let progress = 0;

        const step = () => {
            progress += 0.015;
            if (progress > 1) progress = 1;

            const lerpedStops = fromStops.map((from, i) => {
                const to = targetStops[i];
                return {
                    r: lerp(from.r, to.r, progress),
                    g: lerp(from.g, to.g, progress),
                    b: lerp(from.b, to.b, progress),
                    a: lerp(from.a, to.a, progress),
                    stop: to.stop
                };
            });

            currentStops = lerpedStops;

            const gradient = `radial-gradient(circle at center, ${lerpedStops
                .map(rgbaString)
                .join(', ')}, #000000 100%)`;

            const el = document.querySelector(".main-container");
            if (el) el.style.background = gradient;

            if (progress < 1) requestAnimationFrame(step);
        };

        requestAnimationFrame(step);
    };

    const gradientStops_datastruct = [
        rgbaToObject(76, 175, 80, 0.3, "0%"),   // 초록빛 중심
        rgbaToObject(0, 0, 0, 0, "55%")   
    ];

    const gradientStops_algorithm = [
        rgbaToObject(132, 115, 255, 0.3, "0%"), // 보라 중심
        rgbaToObject(0, 0, 0, 0, "55%")  
    ];

    const gradientStops_content = [
        rgbaToObject(66, 66, 66, 0.8, "0%"),    // 회색 중심
        rgbaToObject(0, 0, 0, 0, "55%")  
    ];


    // 자료구조 컨텐츠 박스 스크롤 별 설명 구역  => 스택과 큐에 대한 예시 gif 삽입 예정
    const datastruct_sections = [
        {
            title: '스택 자료구조를 애니메이션으로',
            desc: '스택은 나중에 넣은 데이터가 먼저 나오는 LIFO(후입선출) 방식의 자료구조입니다. 애니메이션을 통해 push와 pop 동작을 직관적으로 확인할 수 있습니다.',
            image: '../assets/index/stack.png',
        },
        {
            title: '자료구조 큐를 애니메이션으로',
            desc: '큐는 먼저 넣은 데이터가 먼저 나오는 FIFO(선입선출) 방식의 자료구조입니다. 애니메이션으로 enqueue와 dequeue 과정을 쉽게 이해할 수 있습니다.',
            image: '../assets/index/image2.png',
        },
    ];

    // datastruct 현재 위치 
    let datastruct_currentIndex = 0;

    // datastruct 캔버스 스크롤 길이 계산 함수
    const datastruct_handleScroll = (event) => {
        const containerTop = event.target.getBoundingClientRect().top;
        const sectionEls = document.querySelectorAll(".datastruct-left-section");

        let minDistance = Infinity;
        let closestIdx = 0;

        sectionEls.forEach((el, index) => {
            const rect = el.getBoundingClientRect();
            const distance = Math.abs(rect.top - containerTop);
            if (distance < minDistance) {
                minDistance = distance;
                closestIdx = index;
            }
        });

        datastruct_currentIndex = closestIdx;
    };

    // 알고리즘 컨텐츠 박스 스크롤 별 설명 구역  => 임의의 알고리즘 두 개정도 gif 삽입 예정
    const algo_sections = [
        {
            title: '버블정렬을 애니메이션으로',
            desc: '버블정렬에 대한 설명입니다다다다다다다ㅏ다다다다다다다다다다다다.',
            image: '../assets/index/image1.png',
        },
        {
            title: 'DFS를 애니메이션으로',
            desc: 'DFS에 대한 설명입니다다다다다다다ㅏ다다다다다다다다다다다다.',
            image: '../assets/index/image2.png',
        },
    ];

    // algorithm 현재 위치 
    let algo_currentIndex = 0;

    // algorithm 캔버스 스크롤 길이 계산 함수
    const algo_handleScroll = (event) => {
        const containerTop = event.target.getBoundingClientRect().top;
        const sectionEls = document.querySelectorAll(".algo-left-section");

        let minDistance = Infinity;
        let closestIdx = 0;

        sectionEls.forEach((el, index) => {
            const rect = el.getBoundingClientRect();
            const distance = Math.abs(rect.top - containerTop);
            if (distance < minDistance) {
                minDistance = distance;
                closestIdx = index;
            }
        });

        algo_currentIndex = closestIdx;
    };

    // 컨텐츠 컨텐츠 박스 스크롤 별 설명 구역  => 모듈에 대한 예시 gif 삽입 예정
    const content_sections = [
        {
            title: '모듈 설명 1',
            desc: '모듈에 대한 설명입니다다다다다다다ㅏ다다다다다다다다다다다다.',
            image: '../assets/index/image1.png',
        },
        {
            title: '모듈 설명 2',
            desc: '모듈에 대한 설명입니다다다다다다다ㅏ다다다다다다다다다다다다.',
            image: '../assets/index/image2.png',
        },
        {
            title: '모듈 설명 3',
            desc: '모듈에 대한 설명입니다다다다다다다ㅏ다다다다다다다다다다다다.',
            image: '../assets/index/image3.png',
        },
        {
            title: '모듈 설명 4',
            desc: '모듈에 대한 설명입니다다다다다다다ㅏ다다다다다다다다다다다다.',
            image: '../assets/index/image4.png',
        },
    ];

    // content 현재 위치 
    let content_currentIndex = 0;

    // content 캔버스 스크롤 길이 계산 함수
    const content_handleScroll = (event) => {
        const containerTop = event.target.getBoundingClientRect().top;
        const sectionEls = document.querySelectorAll(".content-left-section");

        let minDistance = Infinity;
        let closestIdx = 0;

        sectionEls.forEach((el, index) => {
            const rect = el.getBoundingClientRect();
            const distance = Math.abs(rect.top - containerTop);
            if (distance < minDistance) {
                minDistance = distance;
                closestIdx = index;
            }
        });

        content_currentIndex = closestIdx;
    };



</script>
  
<main>
    <!-- HTML 구조는 header-container, main-container, footer-container으로 구성하기(Header, Footer 없는 페이지 제외) -->
    <!--  해당 클래스의 css는 app.css에 정의 -->
    <div class="header-container">
        <Header/>
    </div>

    <div class="main-container" style="background: radial-gradient(circle at center, rgba(76, 175, 80, 0.3) 0%, rgba(0, 0, 0, 0.2) 55%, #000000 100%);">

        <div id="index-title-container">
            
            <div id="index-title">
                <p id="title-p"> 알고리즘 시각화를 경험하세요 </p>
            </div>

            <div id="index-explain-container">
                <span> 
                    <span class="highlight-ds" on:click={() => toggleView('datastruct')} >&#123; 자료구조 &#125;</span>, 
                    <span class="highlight-algo" on:click={() => toggleView('algorithm')} >&#123; 알고리즘 &#125;</span> 에 어려움을 느끼는 전공자를 위한 웹사이트 <br>
                    : 모르고(MoleuGo)
                </span>
            </div>

            <div id="index-gif-container">

                <!-- svelte-ignore a11y-no-static-element-interactions -->
                <div id="index-gif-box" style="border: 1px solid {selectedColor}; transition: border 0.5s ease;">
                    <!-- svelte-ignore missing-declaration -->
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <div class="corner-tag-1 {selectedTag === 'datastruct' ? 'selected' : ''}" 
                        on:click={() => toggleView('datastruct')}>
                        자료구조
                    </div>
                    <!-- svelte-ignore missing-declaration -->
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <div class="corner-tag-2 {selectedTag === 'algorithm' ? 'selected' : ''}"
                        on:click={() => toggleView('algorithm')}>
                        알고리즘
                    </div>
                    <!-- svelte-ignore missing-declaration -->
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <div class="corner-tag-3 {selectedTag === 'content' ? 'selected' : ''}"
                        on:click={() => toggleView('content')}>
                        컨텐츠
                    </div>
                    
                    <!--상태 변수 값에 따라 달라지는 화면-->
                    {#if isvisible_datastruct === true }
                        <div class="box-content-datastruct" on:scroll={datastruct_handleScroll} >

                            <div id="box-content-datastruct-left"> 

                                {#each datastruct_sections as section, i}

                                    <div class="datastruct-left-section"
                                         class:active-section={i === datastruct_currentIndex}
                                         class:inactive-section={i !== datastruct_currentIndex}>

                                        <h2>{section.title}</h2>
                                        <p>{section.desc}</p>
                                    </div>

                                {/each}

                            </div>

                            <div id="box-content-datastruct-right" > 

                                <div class="datastruct-sticky-image">
                                    <img src={datastruct_sections[datastruct_currentIndex].image} alt="image" />
                                </div>

                            </div>
                            
                        </div>
                    
                    {:else if isvisible_algorithm === true}
                        <div class="box-content-algorithm" on:scroll={algo_handleScroll} >

                            <div id="box-content-algo-left"> 

                                {#each algo_sections as section, i}

                                    <div class="algo-left-section"
                                         class:active-section={i === algo_currentIndex}
                                         class:inactive-section={i !== algo_currentIndex}>
                                        <h2>{section.title}</h2>
                                        <p>{section.desc}</p>
                                    </div>

                                {/each}

                            </div>

                            <div id="box-content-algo-right" > 

                                <div class="algo-sticky-image">
                                    <img src={algo_sections[algo_currentIndex].image} alt="image" />
                                </div>

                            </div>
                                
                        </div>
                    {:else}
                        <div class="box-content-content" on:scroll={content_handleScroll} >
                            <div id="box-content-content-left"> 

                                {#each content_sections as section, i}

                                    <div class="content-left-section"
                                         class:active-section={i === content_currentIndex}
                                         class:inactive-section={i !== content_currentIndex}>
                                        <h2>{section.title}</h2>
                                        <p>{section.desc}</p>
                                    </div>

                                {/each}

                            </div>

                            <div id="box-content-content-right" > 

                                <div class="content-sticky-image">
                                    <img src={content_sections[content_currentIndex].image} alt="image" />
                                </div>
                            </div>
                        </div>
                    {/if}

                </div>
                
            </div>
            
        </div>


    </div>

    <div class="footer-container">
        <Footer/>
    </div>
 </main>
 
  
<style>

    /* main-container: 수직정렬 - 확대,축소시 가운데로 오도록 */
    .main-container {
        display: flex;
        flex-direction: column;
        
        align-items: center;
        color: white;
        
    }

    /* index-title-container */
    #index-title-container {
        margin: 0px 0px 0px 0px;
        padding: 0px 0px 0px 0px;
    }

    /* index-title */
    #index-title {
        margin: 100px 350px 0px 0px;
        padding: 0px 50px 0px 50px;
    }

    /* title-p */
    #title-p {
        font-size: 4rem;
        font-weight: 600;
    }

    /* index-explain-container */
    #index-explain-container {
        margin: 0px 0px 0px 0px;
        padding: 0px 50px 0px 50px;
        font-size: 2rem;
        font-weight: 500;
    }

    /* index-explain-container 설명 부분 중괄호 안 CSS 모음 */
    .highlight-ds,
    .highlight-algo {
        cursor: pointer;
        transition: all 0.3s ease;
        text-decoration: none;
    }

    /* 각각 기본 색상 */
    .highlight-ds {
        color: #4CAF50;
    }

    .highlight-algo {
        color: #8473ff;
    }

    /* hover 효과 모음 */
    .highlight-ds:hover,
    .highlight-algo:hover {
        text-decoration: underline;
        filter: brightness(0.8);
    }


    /* index-gif-container */
    #index-gif-container {
        /* height: 95%; */
    }

    /* index-gif-box */
    #index-gif-box {
        position: relative;
        margin: 75px 0px 0px 0px;
        box-sizing: border-box;
        width: 100%;
        height: 750px; /*고정 길이 해야 안늘어남*/
        background-color: #0c0c0c;
        border: 1px solid #3d444d;
        border-radius: 10px;
        padding: 10px;
        z-index: 0;
    }

    /* corner-tag- 1 ~ 3 */
    .corner-tag-1 {
        width: 100px;
        height: 28px;
        position: absolute;
        top: -37px;
        right: 300px;
        background-color: #4caf50;
        color: white;
        font-weight: bold;
        padding: 4px 10px;
        font-size: 1em;
        transform-origin: top right;
        z-index: 2;
        border-radius: 5px 5px 0px 0px;
        cursor: pointer;
    }

    .corner-tag-2 {
        width: 100px;
        height: 28px;
        position: absolute;
        top: -37px;
        right: 170px;
        background-color: #8473ff;
        color: white;
        font-weight: bold;
        padding: 4px 10px;
        font-size: 1em;
        transform-origin: top right;
        z-index: 2;
        border-radius: 5px 5px 0px 0px;
        cursor: pointer;
    }

    .corner-tag-3 {
        width: 100px;
        height: 28px;
        position: absolute;
        top: -37px;
        right: 40px;
        background-color: #424242;
        color: white;
        font-weight: bold;
        padding: 4px 10px;
        font-size: 1em;
        transform-origin: top right;
        z-index: 2;
        border-radius: 5px 5px 0px 0px;
        cursor: pointer;
    }

    /*corner-tag 호버 및 변화 클래스 */
    .corner-tag-1:hover,
    .corner-tag-2:hover,
    .corner-tag-3:hover {
        filter: brightness(0.8);
        transition: all 0.2s ease;
    }

    .corner-tag-1.selected,
    .corner-tag-2.selected,
    .corner-tag-3.selected {
        height: 35px;
        transform: translateY(-7px);
        transition: all 0.3s ease;
    }

    .corner-tag-1:not(.selected),
    .corner-tag-2:not(.selected),
    .corner-tag-3:not(.selected) {
        transform: translateY(0px);
        transition: all 0.3s ease;
    }

    /* box-content-datastruct 관련된 모든 CSS */
    .box-content-datastruct {
        box-sizing: border-box;
        
        display: grid;
        grid-template-columns: 0.35fr 0.75fr;

        padding: 10px 10px 10px 10px;
        width: 100%;
        height: 100%;

        overflow-y: scroll;
        scroll-snap-type: y mandatory;

        scrollbar-width: thin; 
        scrollbar-color: #4caf50 transparent;

    }

    #box-content-datastruct-left {
        height: 100px;
    }

    .datastruct-left-section {
        margin: 70px 0px 0px 0px;
        min-height: 250px;
        padding: 3rem;
        color: white;
        display: flex;
        flex-direction: column;
        justify-content: center;
        transition: all 0.4s ease;
    }

    .active-section {
        filter: none;
        opacity: 1;
        transform: scale(1);
    }

    .inactive-section {
        filter: grayscale(100%) blur(1.5px);
        opacity: 0.4;
        transform: scale(0.97);
    }

    #box-content-datastruct-right {
        height: 1000px;
        position: relative;
    }

    .datastruct-sticky-image {
        position: sticky;
        top: 10vh;
        padding: 2rem;
    }

    .datastruct-sticky-image img {
        width: 718px;
        border-radius: 15px;
    }

    /* box-content-algorithm 과 관련된 모든 CSS*/
    .box-content-algorithm {
        width: 100%;
        height: 100%;

        box-sizing: border-box;
        
        display: grid;
        grid-template-columns: 0.35fr 0.75fr;

        padding: 10px 10px 10px 10px;
        width: 100%;
        height: 100%;

        overflow-y: scroll;
        scroll-snap-type: y mandatory;

        scrollbar-width: thin; 
        scrollbar-color: #8473ff transparent;
    }

    #box-content-algo-left {
        height: 100px;
    }

    .algo-left-section {
        margin: 20px 0px 0px 0px;
        min-height: 300px;
        padding: 3rem;
        color: white;
        display: flex;
        flex-direction: column;
        justify-content: center;
        transition: all 0.4s ease;
    }

    #box-content-algo-right {
        height: 1000px;
        position: relative;
    }

    .algo-sticky-image {
        position: sticky;
        top: 5vh;
        padding: 2rem;
    }

    .algo-sticky-image img {
        width: 100%;
        border-radius: 15px;
    }

    /* box-content-content 과 관련된 모든 CSS*/
    .box-content-content {
        width: 100%;
        height: 100%;

        box-sizing: border-box;
        
        display: grid;
        grid-template-columns: 0.35fr 0.75fr;

        padding: 10px 10px 10px 10px;
        width: 100%;
        height: 100%;

        overflow-y: scroll;
        scroll-snap-type: y mandatory;

        scrollbar-width: thin; 
        scrollbar-color: #424242 transparent;
    }

    #box-content-content-left {
        height: 100px;
    }

    .content-left-section {
        margin: 20px 0px 0px 0px;
        min-height: 300px;
        padding: 3rem;
        color: white;
        display: flex;
        flex-direction: column;
        justify-content: center;
        transition: all 0.4s ease;
    }

    #box-content-content-right {
        height: 1850px;
        position: relative;
    }

    .content-sticky-image {
        position: sticky;
        top: 5vh;
        padding: 2rem;
    }

    .content-sticky-image img {
        width: 100%;
        border-radius: 15px;
    }

    /*FHD(1920x1080) 환경 미디어 쿼리*/
    @media screen and (max-width: 1920px) {

        /* main-container: 수직정렬 - 확대,축소시 가운데로 오도록 */
        .main-container {
            display: flex;
            flex-direction: column;
            align-items: center;
            color: white;
        }

        /* index-title-container */
        #index-title-container {
            margin: 0px 0px 0px 0px;
            padding: 0px 0px 0px 0px;
            
        }

        /* index-title */
        #index-title {
            margin: 0px 350px 0px 0px;
            padding: 0px 50px 0px 50px;
        }

        /* title-p */
        #title-p {
            font-size: 2.7rem;
            font-weight: 600;
        }

        /* index-explain-container */
        #index-explain-container {
            margin: 0px 0px 0px 0px;
            padding: 0px 50px 0px 50px;
            font-size: 1.5rem;
            font-weight: 500;
        }


        /* index-gif-box */
        #index-gif-box {
            position: relative;
            margin: 50px 0px 0px 0px;
            box-sizing: border-box;
            width: 1000px; /*FHD 한정 고정 길이*/
            height: 550px; /*고정 길이 해야 안늘어남*/
            background-color: #0c0c0c;
            border: 1px solid #3d444d;
            border-radius: 10px;
            padding: 10px;
        }

        /* corner-tag- 1 ~ 3 */
        .corner-tag-1 {
            width: 100px;
            height: 25px;
            position: absolute;
            top: -34px;
            right: 300px;
            background-color: #4caf50;
            color: white;
            font-weight: bold;
            padding: 4px 10px;
            font-size: 1em;
            transform-origin: top right;
            z-index: 10;
            border-radius: 5px 5px 0px 0px;
            cursor: pointer;
        }

        .corner-tag-2 {
            width: 100px;
            height: 25px;
            position: absolute;
            top: -34px;
            right: 170px;
            background-color: #8473ff;
            color: white;
            font-weight: bold;
            padding: 4px 10px;
            font-size: 1em;
            transform-origin: top right;
            z-index: 10;
            border-radius: 5px 5px 0px 0px;
            cursor: pointer;
        }

        .corner-tag-3 {
            width: 100px;
            height: 25px;
            position: absolute;
            top: -34px;
            right: 40px;
            background-color: #424242;
            color: white;
            font-weight: bold;
            padding: 4px 10px;
            font-size: 1em;
            transform-origin: top right;
            z-index: 10;
            border-radius: 5px 5px 0px 0px;
            cursor: pointer;
        }

        /*corner-tag 호버 및 변화 클래스 */
        .corner-tag-1:hover,
        .corner-tag-2:hover,
        .corner-tag-3:hover {
            filter: brightness(0.8);
            transition: all 0.2s ease;
        }

        .corner-tag-1.selected,
        .corner-tag-2.selected,
        .corner-tag-3.selected {
            height: 35px;
            transform: translateY(-9px);
            transition: all 0.3s ease;
        }

        .corner-tag-1:not(.selected),
        .corner-tag-2:not(.selected),
        .corner-tag-3:not(.selected) {
            transform: translateY(0px);
            transition: all 0.3s ease;
        }

        /* box-content-datastruct 관련된 모든 CSS */
        .box-content-datastruct {
            box-sizing: border-box;
            
            display: grid;
            grid-template-columns: 0.35fr 0.75fr;

            padding: 10px 10px 10px 10px;
            width: 100%;
            height: 100%;

            overflow-y: scroll;
            scroll-snap-type: y mandatory;

            scrollbar-width: thin; 
            scrollbar-color: #4caf50 transparent;

        }

        #box-content-datastruct-left {
            height: 100px;
        }

        .datastruct-left-section {
            margin: 20px 0px 0px 0px;
            min-height: 300px;
            padding: 3rem;
            color: white;
            display: flex;
            flex-direction: column;
            justify-content: center;
            transition: all 0.4s ease;
        }

        .active-section {
            filter: none;
            opacity: 1;
            transform: scale(1);
        }

        .inactive-section {
            filter: grayscale(100%) blur(1.5px);
            opacity: 0.4;
            transform: scale(0.97);
        }

        #box-content-datastruct-right {
            height: 1000px;
            position: relative;
        }

        .datastruct-sticky-image {
            position: sticky;
            top: 1.5vh;
            padding: 2rem;
        }

        .datastruct-sticky-image img {
            width: 100%;
            border-radius: 15px;
        }

        /* box-content-algorithm 과 관련된 모든 CSS*/
        .box-content-algorithm {
            width: 100%;
            height: 100%;

            box-sizing: border-box;
            
            display: grid;
            grid-template-columns: 0.35fr 0.75fr;

            padding: 10px 10px 10px 10px;
            width: 100%;
            height: 100%;

            overflow-y: scroll;
            scroll-snap-type: y mandatory;

            scrollbar-width: thin; 
            scrollbar-color: #8473ff transparent;
        }

        #box-content-algo-left {
            height: 100px;
        }

        .algo-left-section {
            margin: 20px 0px 0px 0px;
            min-height: 300px;
            padding: 3rem;
            color: white;
            display: flex;
            flex-direction: column;
            justify-content: center;
            transition: all 0.4s ease;
        }

        #box-content-algo-right {
            height: 1000px;
            position: relative;
        }

        .algo-sticky-image {
            position: sticky;
            top: 1.5vh;
            padding: 2rem;
        }

        .algo-sticky-image img {
            width: 100%;
            border-radius: 15px;
        }

        /* box-content-content 과 관련된 모든 CSS*/
        .box-content-content {
            width: 100%;
            height: 100%;

            box-sizing: border-box;
            
            display: grid;
            grid-template-columns: 0.35fr 0.75fr;

            padding: 10px 10px 10px 10px;
            width: 100%;
            height: 100%;

            overflow-y: scroll;
            scroll-snap-type: y mandatory;

            scrollbar-width: thin; 
            scrollbar-color: #424242 transparent;
        }

        #box-content-content-left {
            height: 100px;
        }

        .content-left-section {
            margin: 20px 0px 0px 0px;
            min-height: 300px;
            padding: 3rem;
            color: white;
            display: flex;
            flex-direction: column;
            justify-content: center;
            transition: all 0.4s ease;
        }

        #box-content-content-right {
            height: 1850px;
            position: relative;
        }

        .content-sticky-image {
            position: sticky;
            top: 1.5vh;
            padding: 2rem;
        }

        .content-sticky-image img {
            width: 100%;
            border-radius: 15px;
        }
    }

</style>