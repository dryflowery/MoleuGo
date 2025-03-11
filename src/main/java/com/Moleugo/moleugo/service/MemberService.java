package com.Moleugo.moleugo.service;

import com.Moleugo.moleugo.entity.Member;
import com.Moleugo.moleugo.repository.MemberRepository;
import com.Moleugo.moleugo.service.validator.LoginValidator;
import com.Moleugo.moleugo.service.validator.SignUpValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final ApplicationContext ac;
    private final JavaMailSender mailSender;

    public HttpStatus isValidForm(Member member) {
        SignUpValidator signUpValidator = ac.getBean(SignUpValidator.class);
        return signUpValidator.isFormValid(member);
    }

    public HttpStatus verifyEmail(Member member) {
        /*
        1. smtp로 member.getEmail()에 인증 메일 보내기
        2. 인증 링크 클릭하면 HttpStatus.OK return
        3. 일정 시간이 지나면 BadRequest(확실하지 않음) return
         */

        return HttpStatus.OK;
    }

    public void signUp(Member member) {
        memberRepository.registerMember(member);
    }

    public HttpStatus login(Member member) {
        LoginValidator loginValidator = ac.getBean(LoginValidator.class);
        return loginValidator.isValidLogin(member);
    }
}