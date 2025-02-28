package com.Moleugo.moleugo.service;

import com.Moleugo.moleugo.entity.Member;
import com.Moleugo.moleugo.repository.MemberRepository;
import com.Moleugo.moleugo.response.LoginResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final ApplicationContext ac;

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
                return memberRepository.findByEmail(member) != null;
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

    public HttpStatus login(Member member) {

        // 로그인 정보 유효성 검사
        class LoginValidator {
            final LoginResponse loginResponse = ac.getBean("loginResponse", LoginResponse.class);

            HttpStatus isValidLogin() {
                if(!isValidEmail()) {
                    loginResponse.setLoginMessage("등록되지 않은 이메일 주소입니다.");
                    return HttpStatus.UNAUTHORIZED;
                }

                if(!isValidPassword()) {
                    loginResponse.setLoginMessage("잘못된 비밀번호입니다.");
                    return HttpStatus.UNAUTHORIZED;
                }

                loginResponse.setLoginMessage("로그인 성공");
                return HttpStatus.OK;
            }

            private boolean isValidEmail() {
                return memberRepository.hasEmail(member);
            }

            private boolean isValidPassword() {
                return memberRepository.isCorrectPassword(member);
            }
        }

        LoginValidator loginValidator = new LoginValidator();
        return loginValidator.isValidLogin();
    }
}
