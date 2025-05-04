import {isLogin} from "../store.js";

// 매개변수 algorithm은 카멜 케이스로 작성
// 각 알고리즘 이름은 src/main/java/com/Moleugo/moleugo/entity/AnimationCount.java 참고
export const incrementAnimationCount = async (algorithm) => {
    if(isLogin) {
        await fetch('/mypage/increment-animation-count', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ algorithm }),
        });
    }
}