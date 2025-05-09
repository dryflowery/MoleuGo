package com.Moleugo.moleugo.validator;

import com.Moleugo.moleugo.entity.Member;
import com.Moleugo.moleugo.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SignUpValidator {
    private final MemberRepository memberRepository;
    private final PasswordValidator passwordValidator;

    public HttpStatus isValidForm(Member member) {
        if(isDuplicatedEmail(member)) {
            return HttpStatus.CONFLICT;
        }
        else if(!(isValidEmail(member) && passwordValidator.isValidPassword(member.getPassword()) && isValidVerifyPassword(member))) {
            return HttpStatus.BAD_REQUEST;
        }
        else {
            return HttpStatus.OK;
        }
    }

    // 중복 이메일 체크
    private boolean isDuplicatedEmail(Member member) {
        return memberRepository.findByEmail(member.getEmail()) != null;
    }

    // 이메일 유효성 검사
    private boolean isValidEmail(Member member) {
        return Pattern.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", member.getEmail());
    }

    // password verifyPassword 비교
    private boolean isValidVerifyPassword(Member member) {
        return member.getPassword().equals(member.getVerifyPassword());
    }
}