package com.Moleugo.moleugo.util;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
public class NicknameGenerator {

    private static final List<String> WORD_POOL = List.of(
            "정렬장인", "탐색러", "재귀의달인", "분할정복자", "이분탐색러", "힙정령", "큐매니아",
            "스택러버", "트리연금술사", "버블정렬러", "선택러", "삽입장인", "퀵소터", "병합장인",
            "dp의요정", "그리디왕", "백트래커", "bfs달인", "dfs러버", "우선순위장인", "트리순회자",
            "무한루퍼", "시간초과러", "런타임전사", "예외처리왕", "알고하는중", "코드분석가", "코딩기계",
            "알고리듬러", "최단경로러", "코드박살자", "함수중독자", "변수요정",
            "디버깅마법사", "컴파일도사", "알고영웅", "무한재귀러", "연산최적화러", "노드탐험가",
            "그래프정복자", "비트마스터", "포인터연금술사", "동적프로그래머", "캐시관리자",
            "오버플로워", "코드중첩러", "알고유목민", "조건문장인", "알고전사", "알고법사",
            "반복문전문가", "코드요리사", "알고시인", "빅오지배자"
    );

    private static final Random RANDOM = new Random();

    public String generate() {
        String word = WORD_POOL.get(RANDOM.nextInt(WORD_POOL.size()));
        int number = RANDOM.nextInt(9000) + 1000; // 1000 ~ 9999
        return word + number;
    }
}