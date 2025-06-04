<script>
    import { isAboutVisible } from "../../lib/store.js";

    const closeLoginPopup = (event) => {
        const popupContent = document.getElementById("popup-content");

        if (!popupContent.contains(event.target)) {
            closePopup();
        }
    };

    const closePopup = () => {
        $isAboutVisible = false;
    };
</script>


<main>
    <div id="popup-container" on:click={closeLoginPopup}>
        <div id="popup-content">
            <div id="about-top">
                <button id="close-btn" on:click={closePopup}>x</button>
                <p>About</p>
            </div>
                
            <div id="about-content">
                <h2>About MoleuGo</h2>

                <h3>개발 동기</h3>
                <p>
                    최근 인공지능과 데이터 과학의 급속한 발전으로 컴퓨터 과학 분야의 전문 인력 수요가 전 세계적으로 증가하고 있습니다.
                    특히 소프트웨어 개발 직종은 2023년부터 2033년까지 약 18%의 고용 증가가 예상될 만큼 성장 가능성이 높습니다.<br />
                    이러한 시대적 흐름 속에서, <strong>자료구조와 알고리즘은 문제 해결 능력과 컴퓨팅 사고력을 키우는 핵심 역량</strong>입니다.<br />
                    하지만 많은 학습자들이 알고리즘의 추상적인 개념과 절차적 흐름을 텍스트만으로 이해하기 어려워하며,
                    이로 인해 학습의 흥미와 효율성이 떨어지는 문제를 겪고 있습니다.
                </p>

                <h3>개발 목적</h3>
                <p>
                    'MoleuGo'는 이러한 학습의 어려움을 해결하고자 만들어졌습니다.<br />
                    사용자가 직접 알고리즘과 연산을 선택하고, 시각화된 장면을 단계별 애니메이션으로 확인할 수 있는
                    <strong>인터랙티브 웹 애플리케이션</strong>입니다.<br />
                    각 연산은 자연어 기반 설명과 의사코드를 함께 제공하여, 초보자도 쉽게 이해할 수 있도록 구성되었습니다.
                </p>

                <h3>기대 효과</h3>
                <p>
                    • <strong>초보자에게는</strong> 알고리즘 개념을 쉽고 직관적으로 이해할 수 있는 입문 도구<br />
                    • <strong>숙련자에게는</strong> 시각화를 통해 알고리즘을 복습하고 점검할 수 있는 효율적인 학습 도구<br />
                    <br />
                    또한 교육 현장, 자율 학습 환경, 온라인 콘텐츠 등 다양한 교육 분야에서 활용 가능성이 높으며,
                    알고리즘 학습의 접근성과 효과를 크게 향상시킬 수 있습니다.
                </p>

                <h3>결론</h3>
                <p>
                    'MoleuGo'는 기존 알고리즘 시각화 도구들이 가진 <strong>직관성 부족, 한글 미지원, 설명 부족</strong> 등의 한계를 극복하고자 기획되었습니다.<br />
                    특히 연산 선택 도구를 도입함으로써, 별도의 복잡한 학습 없이도 누구나 쉽게 알고리즘 시각화를 체험할 수 있도록 진입 장벽을 낮추고 사용성을 높였습니다.<br />
                    앞으로도 사용자 중심의 개선을 통해 더 많은 학습자들에게 도움이 되는 도구로 발전해나가겠습니다.
                </p>
            </div>
        </div>
    </div>
</main>

<style>
#popup-container {
    position: absolute;
    display: flex;
    align-items: center;
    justify-content: center;
    width: 100%;
    height: 100%;
    top: 0;
    left: 0;
    background-color: rgba(0, 0, 0, 0.4);
    z-index: 2000;
}

#popup-content {
    width: 50vw;
    height: 60vh;
    border: 2px solid #1e2024;
    border-radius: 10px;
    color: #FFFFFF;
    background-color: #151515;
    position: relative;
    overflow: hidden;
    display: flex;
    flex-direction: column;
}

#about-top {
    display: flex;
    justify-content: center;
    align-items: center;
    border-bottom: 2px solid white;
    height: 60px;
    position: relative;
}

#close-btn {
    position: absolute;
    top: 10px;
    right: 20px;
    background: none;
    border: none;
    color: #FFFFFF;
    font-size: 1.5rem;
    cursor: pointer;
}

#close-btn:hover {
    color: #E0E0E0;
}

#about-content {
    flex: 1;
    overflow-y: auto;
    padding: 20px;
    text-align: left;
    font-size: 1rem;
    word-wrap: break-word;
}

#about-content h2,
#about-content h3,
#about-content p {
    text-align: left;
    margin-bottom: 1rem;
}

#about-content p {
    font-size: 1rem;
    line-height: 1.6;
}

</style>