<script>
    import { isTeamsVisible } from "../../lib/store.js";

    const closeLoginPopup = (event) => {
        const popupContent = document.getElementById("popup-content");
        if (!popupContent.contains(event.target)) {
            closePopup();
        }
    };

    const closePopup = () => {
        $isTeamsVisible = false;
    };
</script>

<main>
    <div id="popup-container" on:click={closeLoginPopup}>
        <div id="popup-content">
            <div id="teams-top">
                <button id="close-btn" on:click={closePopup}>×</button>
                <p>Teams</p>
            </div>

            <div id="teams-grid">
                <div class="member">
                    <img src="/assets/footer/member_1.png" alt="Team Leader" />
                    <h3>이세찬</h3>
                    <p class="role">팀장</p>
                    <p class="shared">프론트엔드 · 백엔드 공통</p>
                </div>

                <div class="team-desc">
                    <p>
                        두 팀원은 프론트엔드와 백엔드를 명확히 구분하지 않고 함께 분담하며,
                        프로젝트의 기획 초기 단계부터 배포 및 유지보수까지 모든 개발 프로세스에 유기적으로 참여하였습니다.<br /><br />
                        각자의 전문성과 관심 분야를 살리되, 기술적 장벽 없이 자유롭게 의견을 나누고 역할을 조율함으로써
                        보다 효율적이고 유연한 협업 체계를 구축하였습니다.<br /><br />
                        또한, Git을 활용한 버전 관리, Notion과 Figma를 활용한 문서화 및 디자인 협업,
                        회의를 통한 실시간 커뮤니케이션 등 다양한 협업 도구를 적극적으로 활용하여
                        원활한 개발 흐름을 유지하였습니다.<br /><br />
                        이러한 과정 속에서 팀원 간의 신뢰와 책임감을 바탕으로
                        최종 산출물의 완성도와 사용자 경험 향상에 기여하고자 노력하였습니다.
                    </p>
                </div>
                

                <div class="member">
                    <img src="/assets/footer/member_2.png" alt="UX/UI Designer" />
                    <h3>박웅기</h3>
                    <p class="role">UX/UI 디자인 담당</p>
                    <p class="shared">프론트엔드 · 백엔드 공통</p>
                </div>
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
        width: 80vw;
        max-height: 90vh;
        background-color: #151515;
        border: 2px solid #1e2024;
        border-radius: 10px;
        color: #ffffff;
        position: relative;
        overflow-y: auto;
        padding-bottom: 40px;
    }

    #teams-top {
        display: flex;
        justify-content: center;
        align-items: center;
        height: 60px;
        border-bottom: 2px solid white;
        position: relative;
    }

    #teams-top p {
        font-size: 1.7rem;
        margin: 0;
        font-weight: bold;
    }

    #close-btn {
        position: absolute;
        top: 10px;
        right: 20px;
        background: none;
        border: none;
        color: #ffffff;
        font-size: 1.5rem;
        cursor: pointer;
    }

    #teams-grid {
        display: grid;
        grid-template-columns: 1fr 1fr 1fr;
        align-items: start;
        padding: 50px 60px;
        text-align: center;
        gap: 20px;
    }

    .member img {
        width: 180px;
        height: 220px;
        object-fit: cover;
        border-radius: 12px;
        border: 3px solid white;
        margin-bottom: 15px;
    }

    .member h3 {
        margin: 5px 0 4px;
        font-size: 1.3rem;
        font-weight: bold;
    }

    .member .role {
        margin: 0;
        font-size: 1rem;
        color: #cccccc;
    }

    .member .shared {
        font-size: 0.95rem;
        color: #bbbbbb;
        margin-top: 4px;
    }

    .team-desc {
        display: flex;
        justify-content: center;
        align-items: center;
        padding: 0 10px;
    }

    .team-desc p {
        font-size: 1.1rem;
        line-height: 1.6;
        font-weight: 600;
        color: #eeeeee;

        /* 기존 text-align: center 제거 또는 아래로 대체 */
        text-align: left;
        max-width: 500px; /* 가독성을 위한 최대 폭 설정 */
    }

    @media (max-width: 900px) {
        #teams-grid {
            grid-template-columns: 1fr;
        }

        .team-desc {
            padding: 30px 0;
        }
    }
</style>
