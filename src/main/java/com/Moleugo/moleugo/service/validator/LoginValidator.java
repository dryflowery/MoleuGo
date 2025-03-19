package com.Moleugo.moleugo.service.validator;

import com.Moleugo.moleugo.entity.Member;
import com.Moleugo.moleugo.repository.MemberRepository;
import com.Moleugo.moleugo.response.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class LoginValidator {
    private final MemberRepository memberRepository;
    private final LoginResponse loginResponse;

    public HttpStatus isValidLogin(Member member) {
        if(!isValidEmail(member)) {
            loginResponse.setLoginMessage("등록되지 않은 이메일 주소입니다.");
            return HttpStatus.UNAUTHORIZED;
        }

        if(!isValidPassword(member)) {
            loginResponse.setLoginMessage("잘못된 비밀번호입니다.");
            return HttpStatus.UNAUTHORIZED;
        }

        loginResponse.setLoginMessage("로그인 성공");
        return HttpStatus.OK;
    }

    // 회원가입한 이메일인지 확인
    private boolean isValidEmail(Member member) {
        return memberRepository.isRegisteredEmail(member.getEmail());
    }

    // 해당 이메일로 회원가입한 비밀번호와 로그인을 시도한 비밀번호가 동일한지 확인
    private boolean isValidPassword(Member member) {
        return memberRepository.isCorrectPassword(member);
    }
}