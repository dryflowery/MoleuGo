package com.Moleugo.moleugo.entity;

import com.Moleugo.moleugo.entity.Id.DailyGoalId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "daily_goal")
public class DailyGoal {

    // 복합키
    @EmbeddedId
    private DailyGoalId id;

    // 일일 목표 달성 횟수
    @Column(name = "achieved_count", nullable = false)
    private int achievedCount = 0;
}