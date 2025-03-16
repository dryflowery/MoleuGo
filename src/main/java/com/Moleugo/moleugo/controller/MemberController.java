package com.Moleugo.moleugo.controller;

import com.Moleugo.moleugo.entity.Member;
import com.Moleugo.moleugo.response.LoginResponse;
import com.Moleugo.moleugo.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final ApplicationContext ac;

    @PostMapping("/signup/check-form")
    public ResponseEntity<?> isValidForm(@RequestBody Member member) {
        return ResponseEntity.status(memberService.isValidForm(member)).build();
    }

    @PostMapping("/signup/verify-email")
    public void sendVerificationEmail(@RequestBody Member member) {
        memberService.sendVerificationEmail(member);
    }

    @GetMapping("/signup/{uuid}")
    public String signUp(@PathVariable("uuid") String uuid) {
        if(memberService.signUp(uuid) == HttpStatus.OK) {
            return "redirect:/#/signup-success";
        }
        else {
            return "redirect:/#";
        }
    }

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