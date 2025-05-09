package com.Moleugo.moleugo.repository.dailygoal;

import com.Moleugo.moleugo.entity.DailyGoal;
import com.Moleugo.moleugo.entity.Id.DailyGoalId;

import java.time.LocalDate;
import java.util.List;

public interface DailyGoalRepository {
    // emil+date, achieve_count 저장
    void save(DailyGoal dailyGoal);

    // id로 조회
    DailyGoal findById(DailyGoalId id);

    // 특정 이메일의 전체 기록 조회
    List<DailyGoal> findAllByEmail(String email);

    // achieve_count 업데이트
    void updateAchievedCount(DailyGoal dailyGoal);

    void delete(DailyGoal dailyGoal);
}
