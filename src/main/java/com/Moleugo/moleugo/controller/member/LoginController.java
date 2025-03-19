package com.Moleugo.moleugo.controller.member;

import com.Moleugo.moleugo.entity.Member;
import com.Moleugo.moleugo.response.LoginResponse;
import com.Moleugo.moleugo.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class LoginController {
    private final MemberService memberService;
    private final ApplicationContext ac;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Member member, HttpServletResponse resp) {
        LoginResponse loginResponse = ac.getBean(LoginResponse.class);
        HttpStatus loginStatus = memberService.login(member);

        if(loginResponse.getCookie() != null) {
            resp.addCookie(loginResponse.getCookie());
            loginResponse.setCookie(null);
        }

        return ResponseEntity.status(loginStatus).body(loginResponse);
    }

    @GetMapping("/auth/status")
    public ResponseEntity<?> isUserLoggedIn(@CookieValue(value = "LOGIN-INFO", required = false) String loginInfo) {
        if (loginInfo != null) {
            return ResponseEntity.status(memberService.isUserLoggedIn(loginInfo)).build();
        }
        else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue(value = "LOGIN-INFO", required = false) String loginInfo, HttpServletResponse resp) {
        if (loginInfo != null) {
            HttpStatus logoutStatus = memberService.logout(loginInfo);

            if(logoutStatus == HttpStatus.OK) {
                Cookie cookie = new Cookie("LOGIN-INFO", null);
                cookie.setMaxAge(0);
                resp.addCookie(cookie);
            }

            return ResponseEntity.status(logoutStatus).build();
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
