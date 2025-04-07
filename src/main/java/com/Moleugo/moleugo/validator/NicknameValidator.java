package com.Moleugo.moleugo.validator;

import com.Moleugo.moleugo.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class NicknameValidator {
    private final MemberRepository memberRepository;

    private static final List<String> BLOCKED_WORDS = List.of(
         // 금지어는 여기에 추가
    );

    public boolean isValid(String nickname) {
        return isLengthValid(nickname)
                && isFormatValid(nickname)
                && !containsBlockedWord(nickname)
                && !isDuplicate(nickname);
    }

    private boolean isLengthValid(String nickname) {
        return nickname != null && nickname.length() >= 2 && nickname.length() <= 10;
    }

    private boolean isFormatValid(String nickname) {
        return Pattern.matches("^[가-힣a-zA-Z0-9]+$", nickname); // 공백/특수문자 허용 X
    }

    private boolean containsBlockedWord(String nickname) {
        return BLOCKED_WORDS.stream().anyMatch(nickname::contains);
    }

    public  boolean isDuplicate(String nickname) {
        return memberRepository.findAll().stream()
                .anyMatch(member -> nickname.equals(member.getNickname()));
    }
}