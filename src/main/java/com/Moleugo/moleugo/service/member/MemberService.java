package com.Moleugo.moleugo.service.member;

import com.Moleugo.moleugo.entity.Member;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final SignupService signupService;
    private final LoginService loginService;

    public HttpStatus isValidForm(Member member) {
        return signupService.isValidForm(member);
    }

    public void sendVerificationEmail(Member member) {
        signupService.sendVerificationEmail(member);
    }

    public HttpStatus signUp(String uuid) {
        return signupService.signUp(uuid);
    }

    public HttpStatus googleSignUp(String code) {
        return signupService.googleSignUp(code);
    }

    public HttpStatus login(Member member) {
        return loginService.login(member);
    }

    public HttpStatus isUserLoggedIn(String loginInfo) {
        return loginService.isUserLoggedIn(loginInfo);
    }

    public HttpStatus logout(String loginInfo) {
        return loginService.logout(loginInfo);
    }
}