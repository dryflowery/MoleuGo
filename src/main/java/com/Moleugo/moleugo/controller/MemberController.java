package com.Moleugo.moleugo.controller;

import com.Moleugo.moleugo.entity.Member;
import com.Moleugo.moleugo.response.LoginResponse;
import com.Moleugo.moleugo.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
    public ResponseEntity<?> verifyEmail(@RequestBody Member member) {
        return ResponseEntity.status(memberService.verifyEmail(member)).build();
    }

    @PostMapping("/signup")
    public void signUp(@RequestBody Member member) {
        memberService.signUp(member);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Member member) {
        LoginResponse loginResponse = ac.getBean("loginResponse", LoginResponse.class);
        return ResponseEntity.status(memberService.login(member)).body(loginResponse);
    }
}