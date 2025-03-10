package com.Moleugo.moleugo.controller;

import com.Moleugo.moleugo.entity.Member;
import com.Moleugo.moleugo.response.LoginResponse;
import com.Moleugo.moleugo.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final ApplicationContext ac;

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody Member member) {
        return ResponseEntity.status(memberService.signUp(member)).build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Member member) {
        LoginResponse loginResponse = ac.getBean("loginResponse", LoginResponse.class);
        return ResponseEntity.status(memberService.login(member)).body(loginResponse);
    }
}