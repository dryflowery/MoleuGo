package com.Moleugo.moleugo.controller.mypage;

import com.Moleugo.moleugo.dto.GoalStatusResponse;
import com.Moleugo.moleugo.dto.VerifyGoalRequest;
import com.Moleugo.moleugo.entity.DailyGoal;
import com.Moleugo.moleugo.entity.Id.DailyGoalId;
import com.Moleugo.moleugo.entity.Member;
import com.Moleugo.moleugo.service.dailyGoal.DailyGoalService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class DailyGoalController {

    private final DailyGoalService dailyGoalService;
    private final HttpSession session;
    private final ObjectMapper objectMapper;

    private Member getMember(String sessionKey) {
        return (Member) session.getAttribute(sessionKey);
    }


    // 일일달성목표 생성
    @GetMapping("/today-goal")
    public ResponseEntity<List<String>> getTodayGoal(@CookieValue("user_session") String sessionKey) throws JsonProcessingException {
        Member member = getMember(sessionKey);
        if (member == null) {
            return ResponseEntity.status(401).build();
        }

        DailyGoal goal = dailyGoalService.getOrCreateTodayGoal(member.getEmail());

        String json = goal.getGoalListJson();
        if (json == null) {
            log.error("goalListJson is null for email {}", member.getEmail());
            return ResponseEntity.internalServerError().body(null);
        }

        List<String> goals = new ObjectMapper().readValue(json, List.class);

        return ResponseEntity.ok(goals);
    }

    @GetMapping("/today-goal-status")
    public ResponseEntity<List<GoalStatusResponse>> getGoalStatus(@CookieValue("user_session") String sessionKey)
            throws JsonProcessingException {

        Member member = getMember(sessionKey);
        if (member == null) return ResponseEntity.status(401).build();

        DailyGoalId id = new DailyGoalId(member.getEmail(), LocalDate.now());
        DailyGoal goal = dailyGoalService.getGoalById(id);  // 서비스에 따로 분리했으면 그대로 사용

        List<String> goals = objectMapper.readValue(goal.getGoalListJson(), List.class);
        List<String> achieved = objectMapper.readValue(goal.getAchievedListJson(), List.class);

        List<GoalStatusResponse> statusList = goals.stream()
                .map(algo -> new GoalStatusResponse(algo, achieved.contains(algo)))
                .toList();

        return ResponseEntity.ok(statusList);
    }


    // 각 애니메이션 페이지에 적용할 POST
    @PostMapping("/verify-daily-goal")
    public ResponseEntity<String> verifyGoal(@RequestBody VerifyGoalRequest request,
                                             @CookieValue("user_session") String sessionKey) throws JsonProcessingException {
        Member member = getMember(sessionKey);
        if (member == null) {
            return ResponseEntity.status(401).build();
        }

        boolean success = dailyGoalService.verifyGoalAndUpdate(member.getEmail(), request.getAlgorithm());
        return success
                ? ResponseEntity.ok("success")
                : ResponseEntity.badRequest().body("not a valid goal or already completed");
    }

}
