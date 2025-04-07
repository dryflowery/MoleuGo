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

    public HttpStatus isValidForm(Member member) {
        if(isDuplicatedEmail(member)) {
            return HttpStatus.CONFLICT;
        }
        else if(!(isValidEmail(member) && isValidPassword(member) && isValidVerifyPassword(member))) {
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

    // 비밀번호 유효성 검사
    private boolean isValidPassword(Member member) {
        String password = member.getPassword().replaceAll("\\s", "");
        return hasAtLeastTwoCharTypes(password) && isValidLength(password) && hasNoTripleConsecutiveChars(password);
    }

    // 영문/숫자/특수문자 중 2가지 이상 포함
    private boolean hasAtLeastTwoCharTypes(String password) {
        Pattern[] typePatterns = {
                Pattern.compile("[a-zA-Z]"),
                Pattern.compile("[0-9]"),
                Pattern.compile("[^a-zA-Z0-9\\s]")
        };

        long typeCount = Arrays.stream(typePatterns)
                .filter(pattern -> pattern.matcher(password).find())
                .count();

        return 2 <= typeCount;
    }

    // 8자 이상 32자 이하 입력 (공백 제외)
    private boolean isValidLength(String password) {
        return 8 <= password.length() && password.length() <= 32;
    }

    // 연속 3자 이상 동일한 문자/숫자 제외
    private boolean hasNoTripleConsecutiveChars(String password) {
        return !Pattern.compile("(.)\\1\\1").matcher(password).find();
    }

    // password verifyPassword 비교
    private boolean isValidVerifyPassword(Member member) {
        return member.getPassword().equals(member.getVerifyPassword());
    }
}