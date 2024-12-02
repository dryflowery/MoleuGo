<script>
    import { onDestroy, onMount } from "svelte";
    import Header from "../../../component/Header.svelte";
    import Navigation from "../../../component/navigation/geometry/ConvexHullNavigation.svelte";
    import {isListVisible} from "../../../lib/store";

    let pointsInfo = [];
    let pointsNum = Array(54).fill(undefined);
    let cells = Array.from({ length: 54 }); 
    let visibleCells = Array(54).fill(false);

    let isPaused = true;
    let pausedIcon = true;
    let fromBtn = false;
    let isReplay = false;

    let explanation = ``;
    let animationSpeed = 1;
    let animationWorking = false;
    let animationQuery = [];
    let codeColor = Array(7).fill()
    let animationStep = [0, 0]; // [curStep, maxStep]
    let asyncCnt = 0; // 비동기 함수 한 번에 하나만 실행하기 위한 변수
    let gradient = 0;

    // 페이지 바뀌면 애니메이션 종료
    onDestroy(() => {
        InitAnimation();
    });

    onMount(() => {
        visibleCells = Array(54).fill(false);

        for (let i = 0; i < 3; i++) {
            let r = Math.floor(Math.random() * 54); 

            if(!visibleCells[r]) 
            {
                visibleCells[r] = true;
            }
            else {
                i--;
            }
        }
    });

    // 슬라이더의 위치에 따른 animationSpeed 관리
    // 50%까지는 [1, 10], 51%부터는 [11, 1000]
    const updateSpeed = (event) => {
        const sliderValue = event.target.value;
        
        if (sliderValue <= 50) {
            animationSpeed = Math.round(sliderValue / 5); 
            
            if(animationSpeed == 0) {
                animationSpeed = 1;
            }
        } 
        else {
            animationSpeed = Math.min(1000, Math.round(10 + (sliderValue - 50) * 20));  
        }
        
    };

    // 슬라이더 색깔관리
    $: gradient = (animationStep[0] === 0 || animationStep[1] === 0) ? 0 : (animationStep[0] / animationStep[1]) * 100;
    $: sliderStyle = `linear-gradient(to right, #509650 ${gradient}%, #585858 ${gradient}%)`;

    const delay = (duration) => {
        return new Promise((resolve) => { setTimeout(resolve, duration); });
    };

    // pause 체크
    const waitPause = async () => {
        return new Promise((resolve) => {
            const checkPause = () => {
                if (isPaused === false) {
                    setTimeout(resolve, 0);
                } 
                else {
                    setTimeout(() => {
                        if (isPaused === true) {
                            checkPause(); 
                        } 
                        else {
                            resolve();
                        }
                    }, 50); 
                }
            };

            checkPause();
        });
    };

    const switchPointVisibility = (e) => {
        const gridElement = document.querySelector('.cell');
        const gridRect = gridElement.getBoundingClientRect();

        const x = Math.round((e.clientX - gridRect.left - 100) / 100);
        const y = Math.round((e.clientY - gridRect.top) / 100);

        if (x < 0 || y < 0 || x >= 9 || y >= 6) {
            return;
        }

        visibleCells[y * 9 + x] = !visibleCells[y * 9 + x];
    };

    const InitAnimation = () => {
        const pointElements = document.querySelectorAll('.point:not(.point-invisible)');
        const gridElements = document.querySelectorAll('.grid');
        const cellElements = document.querySelectorAll('.cell');

        gridElements.forEach(element => {
            element.style.transform = "scale(1)";
            element.style.visibility = "visible";
            element.style.margin = "";
        });

        cellElements.forEach(element => {
            element.style.transform = "scale(1)";
            element.style.visibility = "visible";
        });

        pointElements.forEach(element => {
            element.style.width = "";
            element.style.height = "";
            element.style.backgroundColor = "";
            element.style.color = "";
            element.style.borderRadius = "";
            element.style.marginLeft = "";
            element.style.marginTop = "";
            element.style.zIndex = "";
            element.style.transform = "";
            element.style.border = "";
            element.style.transition = "";
            element.style.opacity = "";
        });
        
        animationWorking = false;
        pausedIcon = true;
        isPaused = true;
        isReplay = false;
        fromBtn = false;
        explanation = ``;
        animationQuery = [];
        codeColor = Array(7).fill();
        animationStep = [0, 0]; 
        pointsNum = Array(54).fill(undefined);
        pointsInfo = [];
    };

    const changeCodeColor = (idx) => {
        for(let i = 0; i < codeColor.length; i++) {
            if(i == idx) {
                codeColor[i] = "rgb(80, 150, 80)";
            }
            else {
                codeColor[i] = "rgba(255, 255, 255, 0)";
            }
        }
    };

    const createRandomPoint = (e) => {
        InitAnimation();

        visibleCells = Array(54).fill(false);
        for (let i = 0; i < e.detail.pointCnt; i++) {
            let r = Math.floor(Math.random() * 54); 

            if(!visibleCells[r]) 
            {
                visibleCells[r] = true;
            }
            else {
                i--;
            }
        }
    };

    const constructConvexHull = (e) => {
        InitAnimation();

        preDrawConvexHull();

        animationWorking = true;
        pausedIcon = false;
        isPaused = false;
        drawConvexHull(asyncCnt++);
    };

    const pushAnimationQuery = (tmpArr) => {
            animationQuery.push({
            curArr: [...tmpArr],
        })
    };

    const preDrawConvexHull = () => {

    };

    const drawConvexHull = async (myAsync) => {
        // 0. 초기 설정
        const pointElements = document.querySelectorAll('.point:not(.point-invisible)');
        const gridElements = document.querySelectorAll('.grid');
        const cellElements = document.querySelectorAll('.cell');

        // point에 숫자 채우기
        const cellElement = document.querySelector('.cell');
        const cellRect = cellElement.getBoundingClientRect();

        pointElements.forEach((element, index) => {
            const rect = element.getBoundingClientRect();

            const x = Math.round((rect.left - cellRect.left - 100) / 100);
            const y = Math.round((rect.top - cellRect.top) / 100);

            pointsInfo.push({x: x, y: y, pointsNumIdx: y * 9 + x, pointElementsIdx: index});
        });

        // 그리드에 transform: scale(0) 적용
        gridElements.forEach(element => {
            element.style.transition = `transform ${(1 / animationSpeed)}s ease`;
            element.style.transform = "scale(0)";
        });

        await delay(2000 * (1 / animationSpeed));

        // 원래 위치에서 visibility만 hidden으로 바꾸고 point 그리기
        gridElements.forEach(element => {
            element.style.margin = "50px 0px 0px 200px";
            element.style.transform = "scale(1)";
            element.style.visibility = "hidden";
        });

        cellElements.forEach(element => {
            element.style.transform = "scale(1)";
            element.style.visibility = "hidden";
        });
        
        pointElements.forEach(element => {
            element.style.width = "50px";
            element.style.height = "50px";
            element.style.backgroundColor = "#FFFFFF";
            element.style.color = "#000000";
            element.style.marginLeft = "75px";
            element.style.marginTop = "-25px";
            element.style.transform = "scale(1)";
            element.style.border = "5px solid #000000";
            element.style.transition = `transform ${(1 / animationSpeed)}s ease, width ${(1 / animationSpeed)}s ease, height ${(1 / animationSpeed)}s ease`;
            element.style.opacity = "1";
            element.style.visibility = "visible";
        });

        await delay(2000 * (1 / animationSpeed));
        // 0. 초기 설정

        // 1. set points[0]
        pointsInfo.sort((a, b) => {
            if (a.y === b.y) {
                return a.x - b.x; // y값이 같으면 x값이 작은 순으로 정렬
            }
            return b.y - a.y; // y값이 클수록 앞으로 정렬
        });

        let firstRect = pointElements[pointsInfo[0].pointElementsIdx].getBoundingClientRect();
        let startX = firstRect.left + (firstRect.width / 2) - 54.5; 
        let startY = firstRect.top + (firstRect.height / 2) - 125; 

        // 가로줄 그리기
        const rowAnimate = async (height) => {
            const svg = document.getElementById("svg");
            const canvasElement = document.querySelector('.canvas');
            const canvasRect = canvasElement.getBoundingClientRect();

            const path = document.createElementNS("http://www.w3.org/2000/svg", "path");
            path.setAttribute('d', `M${canvasRect.left - 54.5},${canvasRect.top - 125 + height} L${canvasRect.right - 54.5},${canvasRect.top - 125 + height}`);
            path.setAttribute("stroke", "black");
            path.setAttribute("stroke-width", "3");
            path.setAttribute("fill", "none");
            path.setAttribute("id", "rowLine")

            svg.appendChild(path);

            if(Math.abs(startY - (canvasRect.top - 125 + height)) <= 1) {
                return true;
            }

            setTimeout(() => {
                path.remove(); 
            }, 1 / animationSpeed * 5);


            await delay(1 / animationSpeed * 5);

            return false;
        }
        
        let height = 0;

        while(true) {
            if(await rowAnimate(height) === true) break;
            height += 1;
        }

        // 세로줄 그리기
        const colAnimate = async (width) => {
            const svg = document.getElementById("svg");
            const canvasElement = document.querySelector('.canvas');
            const canvasRect = canvasElement.getBoundingClientRect();

            const path = document.createElementNS("http://www.w3.org/2000/svg", "path");
            path.setAttribute('d', `M${canvasRect.left - 54.5 + width},${canvasRect.top - 125} L${canvasRect.left - 54.5 + width},${canvasRect.bottom - 125}`);
            path.setAttribute("stroke", "black");
            path.setAttribute("stroke-width", "3");
            path.setAttribute("fill", "none");
            path.setAttribute("id", "colLine")

            svg.appendChild(path);

            if(Math.abs(startX - (canvasRect.left - 54.5 + width)) <= 1) {
                return true;
            }

            setTimeout(() => {
                path.remove(); 
            }, 1 / animationSpeed * 5);

            await delay(1 / animationSpeed * 5);

            return false;
        }
        
        let width = 0;

        while(true) {
            if(await colAnimate(width) === true) break;
            width += 1;
        }

        // points[0] 번호 변경
        pointsNum[pointsInfo[0].pointsNumIdx] = 0;

        await delay(2000 * (1 / animationSpeed));
        // 1. set points[0]

        // 2. sort by ccw
        const basePoint = pointsInfo[0]; 

        // ccw 정렬 수정
        const sortedPoints = pointsInfo.slice(1).sort((a, b) => {
            const angleA = Math.atan2(a.y - basePoint.y, a.x - basePoint.x); 
            const angleB = Math.atan2(b.y - basePoint.y, b.x - basePoint.x); 
            
            return angleB - angleA; 
        });
        // ccw 정렬 수정

        pointsInfo = [basePoint, ...sortedPoints];

        // 탐지선 그리기
        firstRect = pointElements[pointsInfo[0].pointElementsIdx].getBoundingClientRect();
        startX = firstRect.left + (firstRect.width / 2) - 54.5; 
        startY = firstRect.top + (firstRect.height / 2) - 125; 

        let angle = 0; // 초기 각도
        let num = 1;
        
        const animateSemiCircle = async () => {
            const radian = (Math.PI / 180) * angle;
            const endX = startX + 1200 * Math.cos(radian);
            const endY = startY - 1200 * Math.sin(radian);

            const svg = document.getElementById("svg");
            const path = document.createElementNS("http://www.w3.org/2000/svg", "path");
            path.setAttribute('d', `M${startX},${startY} L${endX},${endY}`);
            path.setAttribute("stroke", "black");
            path.setAttribute("stroke-width", "3");
            path.setAttribute("fill", "none");
            path.setAttribute("id", "semiCircle");

            svg.appendChild(path);

            angle += 0.1;

            while(num < pointsInfo.length) {
                if(await isPositionOnLine(endX, endY) === false) {
                    break;
                }
            }

            if(num == pointsInfo.length) {
                return false;
            }

            setTimeout(() => {
                path.remove(); 
            }, 1 / animationSpeed * 5);

            await delay(1 / animationSpeed * 5);

            return true;
        }

        const isPositionOnLine = (endX, endY) => {
            let rect = pointElements[pointsInfo[num].pointElementsIdx].getBoundingClientRect();
            let curX = rect.left + (rect.width / 2) - 54.5; 
            let curY = rect.top + (rect.height / 2) - 125; 

            if(Math.abs((endX - startX) * (curY - startY) - (endY - startY) * (curX - startX)) <= 3000) {
                pointsNum[pointsInfo[num].pointsNumIdx] = num;
                num++;
                return true;
            }

            return false;
        };
        
        while(angle <= 180) {
            if(await animateSemiCircle() === false) {
                break;
            }
        }

        await delay(2000 * (1 / animationSpeed));

        let rowDel = document.querySelector('#rowLine');
        let colDel = document.querySelector('#colLine');
        let semiDel = document.querySelector('#semiCircle');

        setTimeout(() => {
            rowDel.remove(); 
            colDel.remove();
            semiDel.remove();
        }, 0);

        await delay(2000 * (1 / animationSpeed));
        // 2. sort by ccw

        // 3. stack.push(points[0])
        const changePointColor = (num, border, color, opacity) => {
            pointElements[pointsInfo[num].pointElementsIdx].style.border = border;
            pointElements[pointsInfo[num].pointElementsIdx].style.color = color;
            pointElements[pointsInfo[num].pointElementsIdx].style.opacity = opacity; 
        };

        changePointColor(0, "5px solid #50ad49", "#50ad49", 1);
        await delay(2000 * (1 / animationSpeed));
        // 3. stack.push(points[0])

        // 4. stack.push(points[1])
        changePointColor(1, "5px solid #50ad49", "#50ad49", 1);
        await delay(2000 * (1 / animationSpeed));
        // 4. stack.push(points[1])

        // 5. for i = 2 to points.length - 1
        const connectEdge = (start, end) => {
            let startRect = pointElements[pointsInfo[start].pointElementsIdx].getBoundingClientRect();
            startX = startRect.left + (startRect.width / 2) - 54.5; 
            startY = startRect.top + (startRect.height / 2) - 125; 

            let endRect = pointElements[pointsInfo[end].pointElementsIdx].getBoundingClientRect();
            let endX = endRect.left + (endRect.width / 2) - 54.5; 
            let endY = endRect.top + (endRect.height / 2) - 125; 

            const svg = document.getElementById("svg");
            const path = document.createElementNS("http://www.w3.org/2000/svg", "path");

            path.setAttribute('d', `M${startX},${startY} L${endX},${endY}`);
            path.setAttribute("stroke", "black");
            path.setAttribute("stroke-width", "3");
            path.setAttribute("fill", "none");
            path.setAttribute("id", `path_${end}`);
            svg.appendChild(path);

            const pathLength = path.getTotalLength();

            path.style.strokeDasharray = pathLength;
            path.style.strokeDashoffset = pathLength;

            // 애니메이션 추가
            path.animate(
                [
                    { strokeDashoffset: pathLength }, 
                    { strokeDashoffset: 0 }
                ],
                {
                    duration: 1000 * (1 / animationSpeed), 
                    easing: "ease-in-out", 
                    iterations: 1 
                }
            );

            path.style.strokeDashoffset = 0;
        };

        const disconnectEdge = (num) => {
            const path = document.getElementById(`path_${num}`);
            const pathLength = path.getTotalLength();

            path.style.strokeDasharray = pathLength;
            path.style.strokeDashoffset = 0;

            // 애니메이션 추가
            const animation = path.animate(
                [
                    { strokeDashoffset: 0 }, 
                    { strokeDashoffset: pathLength } 
                ],
                {
                    duration: 1000 * (1 / animationSpeed), 
                    easing: 'ease-in-out', 
                    iterations: 1, 
                }
            );

            animation.onfinish = () => {
                path.remove();  
            };
        };

        await connectEdge(0, 1);
        await delay(2000 * (1 / animationSpeed));

        let stack = [0, 1];

        const ccw = (a, b, c) => {
            let ax = pointsInfo[a].x;
            let ay = pointsInfo[a].y;

            let bx = pointsInfo[b].x;
            let by = pointsInfo[b].y;

            let cx = pointsInfo[c].x;
            let cy = pointsInfo[c].y;   

            const crossProduct = (bx - ax) * (cy - ay) - (by - ay) * (cx - ax);

            if (crossProduct > 0) {
                return 1; 
            } else if (crossProduct < 0) {
                return -1; 
            } else {
                return 0; 
            }
        };

        for(let i = 2; i < pointsInfo.length; i++) {
            let first, second;

            while(stack.length >= 2) {
                first = stack[stack.length - 2];
                second = stack[stack.length - 1];

                changePointColor(i, "5px solid #e97714", "#e97714", 1); // 체크할 노드 빨간색으로
                await delay(2000 * (1 / animationSpeed));

                if(ccw(first, second, i) >= 0) { // 뒤집어진 좌표라 ccw도 반대로
                    // 간선 끊고 opacity 0.1로 변경
                    stack.pop();
                    await disconnectEdge(second)
                    await delay(2000 * (1 / animationSpeed));

                    changePointColor(second, "5px solid #000000", "#000000", 0.1);
                }
                else {
                    break;
                }
            }

            // 간선 연결하고 노드 초록색으로 변경
            stack.push(i);  
            await connectEdge(stack[stack.length - 1], i);
            await delay(2000 * (1 / animationSpeed));

            changePointColor(i, "5px solid #50ad49", "#50ad49", 1);
            await delay(2000 * (1 / animationSpeed));
        }

        await connectEdge(stack[stack.length - 1], 0);
    };
</script>

<main>
    <div class="navigation-container">
        <Navigation on:createRandomPoint={createRandomPoint} 
        on:constructConvexHull={constructConvexHull}
        animationWorking={animationWorking}/>
    </div>

    <div class="header-container">
        <Header/>
    </div>

    <div class="main-container" class:visualization-list-visible={$isListVisible} class:visualization-list-invisible={!$isListVisible}>
        <div class="main-left-container">
            <!-- main-left-container, main-right-container 나누는 수직선 -->
            <div class="visualization-vertical-line"></div>

            <div class="algorithm-title-container">
                <!-- 알고리즘 이름 추가. ex) 버블 정렬(Bubble Sort) -->
                볼록 껍질(Convex Hull)  
            </div>
 
            <div class="canvas">
                <svg id="svg" width="1400" height="729" style="position: absolute; left: 0; top: 0; overflow: hidden"></svg>
                
                <!-- canvas안에 자료구조, 알고리즘 구현 -->
                <div class="grid">
                    {#each cells as _, index}
                        <!-- <div>{index}</div> -->
                        <div class="cell" on:click={(e) => switchPointVisibility(e)}>
                            <div class="point {visibleCells[index] ? '' : 'point-invisible'}">
                                {(pointsNum[index] !== undefined) ? pointsNum[index] : ''}
                            </div>
                        </div>
                    {/each}
                </div>                
            </div>

            <div class="animation-control-container">
                <ion-icon name="play-back" class="animation-control-btn" on:click={() => {if(animationWorking) {fromBtn = true; isPaused = false; pausedIcon = true; animationStep[0] = 0;}}}></ion-icon>
                <ion-icon name="caret-back" class="animation-control-btn" on:click={() => {if(animationWorking) {fromBtn = true; isPaused = false; pausedIcon = true; animationStep[0] = Math.max(animationStep[0] - 1, 0);}}}></ion-icon>

                {#if isPaused || pausedIcon} 
                    <ion-icon name="play-outline" class="animation-control-btn" style="font-size: 2.5rem; color: #d9d9d9;" 
                        on:click={() => {
                            if(animationWorking) {
                                if (animationStep[0] === animationStep[1]) {
                                    isReplay = true; animationStep[0] = -1;
                                } 

                                isPaused = false; 
                                pausedIcon = false;
                            }
                        }}>
                    </ion-icon>
            
                {:else}
                    <ion-icon name="pause-outline" class="animation-control-btn" style="font-size: 2.5rem; color: #d9d9d9;" on:click={() => {if(animationWorking) {isPaused = true; pausedIcon = true;}}}></ion-icon>
                {/if}

                <ion-icon name="caret-forward" class="animation-control-btn" on:click={() => {if(animationWorking) {fromBtn = true; isPaused = false; pausedIcon = true; animationStep[0] = Math.min(animationStep[0] + 1, animationStep[1]);}}}></ion-icon>
                <ion-icon name="play-forward" class="animation-control-btn" on:click={() => {if(animationWorking) {fromBtn = true; isPaused = false; pausedIcon = true; animationStep[0] = animationStep[1];}}}></ion-icon>

                <input class="animation-slider"
                    type="range"
                    style="background: {sliderStyle};"
                    min=0
                    max={animationStep[1]}
                    bind:value={animationStep[0]}
                    on:input={() => {if(animationWorking) {isPaused = false; pausedIcon = true; fromBtn = true;}}}
                />

                <input class="speed-slider"
                    type="range" 
                    min="0" 
                    max="100" 
                    step="1" 
                    value="0"
                    on:input={updateSpeed}
                />
                <span class="speed-label">x {animationSpeed}</span>
            </div>      
        </div>

        <div class="main-right-container">
            <div class="explanation-container">
                <div class="explanation-title">단계별 알고리즘 설명</div>
                <div class="explanation">{@html explanation}</div>
            </div>

            <div class="code-container">
                <div class="code-title">의사코드</div>

                <div class="code-area">
                    <!-- 코드의 class="code"로 설정 -->
                    <!-- 들여쓰기는 padding-left:35px -->
                    <div class="code" style="background-color: {codeColor[0]}">set points[0]</div>
                    <div class="code" style="background-color: {codeColor[1]}">sort in counter-clockwise order</div><br>
                    <div class="code" style="background-color: {codeColor[2]}">stack.push(points[0])</div>
                    <div class="code" style="background-color: {codeColor[2]}">stack.push(points[1])</div><br>
                    <div class="code" style="background-color: {codeColor[3]}">for i = 2 to points.length - 1</div>
                    <div class="code" style="background-color: {codeColor[4]}; padding-left: 35px">while stack.size() &ge; 2 and CCW() &le; 0</div>
                    <div class="code" style="background-color: {codeColor[5]}; padding-left: 70px">stack.pop()</div>
                    <div class="code" style="background-color: {codeColor[6]}; padding-left: 35px">stack.push(points[i])</div>
                </div>
            </div>
        </div>
    </div>
</main>

<style>    
    #svg {
        z-index: 0; /* SVG를 가장 뒤로 이동 */
        pointer-events: none; /* 클릭 이벤트를 SVG 아래 요소로 전달 */
        margin-left: 54.5px;
        margin-top: 55px;
    }

    main {
        height: 100vh;
        display: grid;
        grid-template-rows: 70px 1fr;   
        user-select: none;
        -ms-use-select: none;
        -moz-user-select: none;
        -webkit-user-select: none;
        overflow: hidden;
    }

    .main-container {
        display: grid;
        grid-template-columns: 1500px auto;
        align-items: center;
    }

    .main-left-container {
        display: grid;
        grid-template-rows: 50px 1fr 60px;
    }

    .main-right-container {
        display: grid;
        grid-template-rows: 200px 649px;
        background-color: #0d0e0f;
        font-size: 0.8rem;
    }
    
    .grid {
        display: grid;
        grid-template-rows: repeat(6, 100px); 
        grid-template-columns: repeat(9, 100px); 
        gap: 0; 
        width: 950px;
        height: 650px;
        border-left: 2px solid #000000;
        border-bottom: 2px solid #000000;
        margin: 25px 0px 0px 250px;
        position: relative; 
    }

    /* y축 화살표 */
    .grid::before {
        content: '';
        position: absolute;
        top: -10px;
        left: -0.5px;
        border-width: 7px;
        border-style: solid;
        border-color: transparent transparent transparent #000000;
        transform: translateX(-50%) rotate(270deg);
    }

    /* x축 화살표 */
    .grid::after {
        content: '';
        position: absolute;
        bottom: -7.5px; 
        left: 100%;
        border-width: 7px;
        border-style: solid;
        border-color: transparent transparent #000000 transparent; 
        transform: translateX(-50%) rotate(90deg); 
    }

    .cell {
        border-right: 2px dashed #509650;
        border-top: 2px dashed #509650; 
        width: 100px;
        height: 100px; 
        margin-top: 50px;
    }

    .point {
        display: flex;
        align-items: center;  
        justify-content: center;
        font-size: 1.5rem;
        width: 25px; 
        height: 25px;
        background-color: #509650;
        color: #000000;
        border-radius: 50%;
        margin-left: 88.5px;
        margin-top: -13.5px;
        z-index: 1000;
        transform: scale(1);
        border: none;
        transition: transform 0.25s ease, width 0.25s ease, height 0.25s ease;
        opacity: 1;
    }

    .point-invisible {
        transform: scale(0);
        transition: transform 0.25s ease;
    }
</style>

<!-- animationWorking시에만 grid 보이게 -->