package com.Moleugo.moleugo.entity.Id;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@EqualsAndHashCode
@Embeddable
// 복합키 구현
public class DailyGoalId implements Serializable {

    private String email;
    private LocalDate goalDate;

    public DailyGoalId() {}

    public DailyGoalId(String email, LocalDate goalDate) {
        this.email = email;
        this.goalDate = goalDate;
    }
}
