package com.Moleugo.moleugo.service;

import com.Moleugo.moleugo.entity.Member;
import com.Moleugo.moleugo.repository.MemberRepository;
import com.Moleugo.moleugo.service.validator.LoginValidator;
import com.Moleugo.moleugo.service.validator.SignUpValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final ApplicationContext ac;

    public HttpStatus signUp(Member member) {
        SignUpValidator signUpValidator = ac.getBean(SignUpValidator.class);
        HttpStatus status = signUpValidator.isFormValid(member);

        if(status == HttpStatus.OK) {
            memberRepository.registerMember(member);
        }

        return status;
    }

    public HttpStatus login(Member member) {
        LoginValidator loginValidator = ac.getBean(LoginValidator.class);
        return loginValidator.isValidLogin(member);
    }
}