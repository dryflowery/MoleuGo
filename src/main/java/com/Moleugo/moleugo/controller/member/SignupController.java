package com.Moleugo.moleugo.controller.member;

import com.Moleugo.moleugo.entity.Member;
import com.Moleugo.moleugo.service.member.facade.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class SignupController {
    private final MemberService memberService;

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
        if(memberService.signUp(uuid) == HttpStatus.CREATED) {
            return "redirect:/#/result?result=" + "signup-success";
        }
        else {
            return "redirect:/#/result?result=" + "fail";
        }
    }

    @GetMapping("/signup")
    public String googleSignup(@RequestParam("code") String code, @RequestParam("scope") String scope,
                               @RequestParam("authuser") String authuser, @RequestParam("prompt") String prompt) {

        HttpStatus googleSignupStatus = memberService.googleSignUp(code);

        if(googleSignupStatus == HttpStatus.CREATED) {
            return "redirect:/#/result?type=" + "signup-success";
        }
        else {
            return "redirect:/#/result?type=" + "fail";
        }
    }
}