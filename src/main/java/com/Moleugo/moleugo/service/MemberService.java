package com.Moleugo.moleugo.service;

import com.Moleugo.moleugo.entity.Member;
import com.Moleugo.moleugo.repository.MemberRepository;
import com.Moleugo.moleugo.service.validator.LoginValidator;
import com.Moleugo.moleugo.service.validator.SignUpValidator;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final ApplicationContext ac;
    private final HttpSession session;
    private final BCryptPasswordEncoder encoder;
    private final MemberRepository memberRepository;
    private final MailService mailService;

    public HttpStatus isValidForm(Member member) {
        SignUpValidator signUpValidator = ac.getBean(SignUpValidator.class);
        return signUpValidator.isFormValid(member);
    }

    public void sendVerificationEmail(Member member) {
        String verificationLink = "http://localhost:8080/signup/" + createSession(member);
        String to = member.getEmail();
        String title = "[moleugo] 회원가입을 완료하려면 이메일을 확인하세요!";
        String content = """
                        안녕하세요, <b>moleugo</b>를 이용해 주셔서 감사합니다!<br/>
                        회원가입을 완료하려면 아래의 버튼을 클릭하여 이메일을 인증해주세요.<br/><br/>
                        
                       🔗 <a href="{url}" style="text-decoration: none; color: #007bff; font-weight: bold;">이메일 인증하기</a><br><br>
                        
                        이메일 인증을 완료하면 <b>moleugo</b>의 모든 서비스를 이용할 수 있습니다.<br/>
                        만약 회원가입을 요청하지 않았다면 이 이메일을 무시하셔도 됩니다.<br/><br/>
                        """.replace("{url}", verificationLink);

        mailService.setTo(to);
        mailService.setTitle(title);
        mailService.setContent(content);
        mailService.send();
    }

    public UUID createSession(Member member) {
        UUID uuid = UUID.randomUUID();
        session.setAttribute(uuid.toString(), member);
        return uuid;
    }

    public HttpStatus signUp(String uuid) {
        Member member = (Member)session.getAttribute(uuid);

        if(member != null && !memberRepository.isRegisteredEmail(member)) {
            session.removeAttribute(uuid);
            encodePassword(member);
            memberRepository.registerMember(member);
            return HttpStatus.OK;
        }
        else {
            return HttpStatus.NOT_FOUND;
        }
    }

    public HttpStatus login(Member member) {
        LoginValidator loginValidator = ac.getBean(LoginValidator.class);
        return loginValidator.isValidLogin(member);
    }

    public void encodePassword(Member member) {
        member.setPassword(encoder.encode(member.getPassword()));
    }
}