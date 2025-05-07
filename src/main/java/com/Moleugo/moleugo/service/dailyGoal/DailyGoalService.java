package com.Moleugo.moleugo.service.dailyGoal;

import com.Moleugo.moleugo.entity.DailyGoal;
import com.Moleugo.moleugo.entity.Id.DailyGoalId;
import com.Moleugo.moleugo.repository.dailygoal.DailyGoalRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class DailyGoalService {

    private final DailyGoalRepository dailyGoalRepository;
    private final ObjectMapper objectMapper;

    private static final int MAX_DAILY_GOAL_COUNT = 3;

    private final List<String> allAlgorithms = List.of(
            "linkedList", "stack", "queue", "deque", "heap",
            "binarySearch", "bubbleSort", "selectionSort", "insertionSort",
            "dfs", "bfs", "dijkstra", "bellmanFord", "floydWarshall", "convexHull"
    );

    public DailyGoal getOrCreateTodayGoal(String email) throws JsonProcessingException {
        DailyGoalId id = new DailyGoalId(email, LocalDate.now());
        DailyGoal existing = dailyGoalRepository.findById(id);
        if (existing != null) return existing;

        // 목표 생성
        List<String> goals = new ArrayList<>(allAlgorithms);
        Collections.shuffle(goals);
        List<String> todayGoals = goals.subList(0, MAX_DAILY_GOAL_COUNT);

        DailyGoal newGoal = new DailyGoal();
        newGoal.setId(id);
        newGoal.setAchievedCount(0);
        newGoal.setGoalListJson(objectMapper.writeValueAsString(todayGoals));
        newGoal.setAchievedListJson(objectMapper.writeValueAsString(new ArrayList<>()));

        dailyGoalRepository.save(newGoal);
        return newGoal;
    }

    public boolean verifyGoalAndUpdate(String email, String algorithm) throws JsonProcessingException {
        DailyGoalId id = new DailyGoalId(email, LocalDate.now());
        DailyGoal goal = dailyGoalRepository.findById(id);
        if (goal == null || goal.getAchievedCount() >= MAX_DAILY_GOAL_COUNT) return false;

        List<String> goals = objectMapper.readValue(goal.getGoalListJson(), List.class);
        if (!goals.contains(algorithm)) return false;

        // 이미 달성한 항목이면 무시(중복 방지)
        List<String> achieved = objectMapper.readValue(goal.getAchievedListJson(), List.class);
        if (achieved.contains(algorithm)) {
            return false;
        }

        achieved.add(algorithm);
        goal.setAchievedListJson(objectMapper.writeValueAsString(achieved));
        goal.setAchievedCount(goal.getAchievedCount() + 1);

        dailyGoalRepository.updateAchievedCount(goal);
        return true;
    }

    public DailyGoal getGoalById(DailyGoalId id) {
        return dailyGoalRepository.findById(id);
    }
}