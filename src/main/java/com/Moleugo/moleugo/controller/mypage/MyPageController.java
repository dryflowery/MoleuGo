package com.Moleugo.moleugo.controller.mypage;


import com.Moleugo.moleugo.service.mypage.UserInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

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

    // 사용자 계정 타입 반환
    @GetMapping("/daily-goal")
    public ResponseEntity<List<Integer>> getDailyGoal(@CookieValue(value = "user_session", required = false) String userSession) {
        return userInfoService.getDailyGoal(userSession);
    }
}
