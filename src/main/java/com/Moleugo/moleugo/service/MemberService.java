package com.Moleugo.moleugo.service;

import com.Moleugo.moleugo.entity.Member;
import com.Moleugo.moleugo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public HttpStatus signUp(Member member) {

        // 회원가입 정보 유효성 검사
        class SignUpValidator {
            HttpStatus isFormValid() {
                if(isDuplicatedEmail()) {
                    return HttpStatus.CONFLICT;
                }
                else if(!(isValidEmail() && isValidPassword() && isValidVerifyPassword())) {
                    return HttpStatus.BAD_REQUEST;
                }
                else {
                    return HttpStatus.OK;
                }
            }

            // 중복 이메일 체크
            boolean isDuplicatedEmail() {
                return memberRepository.findByEmail(member.getEmail()) != null;
            }

            // 이메일 유효성 검사
            boolean isValidEmail() {
                return Pattern.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", member.getEmail());
            }

            // 비밀번호 유효성 검사
            boolean isValidPassword() {
                String password = member.getPassword().replaceAll("\\s", "");
                return hasAtLeastTwoCharTypes(password) && isValidLength(password) && hasNoTripleConsecutiveChars(password);
            }

            // 영문/숫자/특수문자 중 2가지 이상 포함
            boolean hasAtLeastTwoCharTypes(String password) {
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
            boolean isValidLength(String password) {
                return 8 <= password.length() && password.length() <= 32;
            }

            // 연속 3자 이상 동일한 문자/숫자 제외
            boolean hasNoTripleConsecutiveChars(String password) {
                return !Pattern.compile("(.)\\1\\1").matcher(password).find();
            }

            // password verifyPassword 비교
            boolean isValidVerifyPassword() {
                return member.getPassword().equals(member.getVerifyPassword());
            }
        }

        SignUpValidator signUpValidator = new SignUpValidator();
        HttpStatus status = signUpValidator.isFormValid();

        if(status == HttpStatus.OK) {
            memberRepository.registerMember(member);
        }

        return status;
    }
}
