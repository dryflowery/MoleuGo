package com.Moleugo.moleugo.controller.mypage;


import com.Moleugo.moleugo.service.mypage.UserInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MyPageController {
    private final UserInfoService userInfoService;
    
    // 사용자 이메일 반환
    @GetMapping("/email")
    public ResponseEntity<String> getUserEmail(@CookieValue(value = "user_session", required = false) String userSession) {
        return userInfoService.getUserEmail(userSession);
    }

    // 사용자 닉네임 반환
    @GetMapping("/nickname")
    public ResponseEntity<String> getUserNickname(@CookieValue(value = "user_session", required = false) String userSession) {
        return userInfoService.getUserNickname(userSession);
    }

    // 사용자 계정 타입 반환
    @GetMapping("/account-type")
    public ResponseEntity<String> getUserAccountType(@CookieValue(value = "user_session", required = false) String userSession) {
        return userInfoService.getUserAccountType(userSession);
    }

    // dailyGoalYear 기준으로 해당 년도의 일일목표 히스토리 반환
    @PostMapping("/daily-goal")
    public ResponseEntity<List<Integer>> getDailyGoal(@CookieValue(value = "user_session", required = false) String userSession,
                                                      @RequestBody(required = false) Integer dailyGoalYear) {
        return userInfoService.getDailyGoal(userSession, dailyGoalYear);
    }
    
    // 애니메이션 실행 횟수 반환
    @GetMapping("/animation-count")
    public ResponseEntity<Map<String, Integer>> getAnimationCount(@CookieValue(value = "user_session", required = false) String userSession) {
        return userInfoService.getAnimationCount(userSession);
    }
}
