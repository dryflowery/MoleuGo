package com.Moleugo.moleugo.validator;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.regex.Pattern;

@Component
public class PasswordValidator {
    // 비밀번호 유효성 검사
    public boolean isValidPassword(String password) {
        password = password.replaceAll("\\s", "");
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
}
