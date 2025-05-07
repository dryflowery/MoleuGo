package com.Moleugo.moleugo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GoalStatusResponse {
    private String algorithm;
    private boolean achieved;
}
