package com.Moleugo.moleugo.repository.animationcount;

import java.util.Map;

public interface AnimationCountRepository {

    // animation_count 테이블의 모든 count를 map에 담아 반환
    Map<String, Integer> findAllCountsByEmail(String email);
}
