package com.Moleugo.moleugo.repository.animationcount;

import com.Moleugo.moleugo.entity.AnimationCount;

import java.util.Map;

public interface AnimationCountRepository {

    // animation_count 테이블의 모든 count를 map에 담아 반환
    Map<String, Integer> findAllCountsByEmail(String email);

    // algorithm의 카운트를 1 증가시킴
    void incrementCountsByEmail(String email, String algorithm);

    // 회원가입시 DB에 정보주입
    void insert(AnimationCount animationCount);

    //기존 이메일 삭제
    void deleteByEmail(String email);

    AnimationCount findByEmail(String email);


}
