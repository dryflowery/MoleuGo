package com.Moleugo.moleugo.service.member.auth;

import com.Moleugo.moleugo.entity.Member;
import com.Moleugo.moleugo.repository.member.MemberRepository;
import com.Moleugo.moleugo.service.member.mail.MailService;
import com.Moleugo.moleugo.validator.SignUpValidator;
import com.Moleugo.moleugo.util.NicknameGenerator;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignupService {
    private final ApplicationContext ac;
    private final HttpSession session;
    private final MailService mailService;
    private final MemberRepository memberRepository;
    private final AuthService authService;
    private final NicknameGenerator nicknameGenerator;

    public HttpStatus isValidForm(Member member) {
        SignUpValidator signUpValidator = ac.getBean(SignUpValidator.class);
        return signUpValidator.isValidForm(member);
    }

    public void sendVerificationEmail(Member member) {
        String verificationLink = "http://localhost:8080/signup/" + authService.createSession(member, 1800);
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

    public HttpStatus signUp(String uuid) {
        Member member = (Member)session.getAttribute(uuid);

        if(member != null && !memberRepository.isRegisteredEmail(member.getEmail())) {
            session.removeAttribute(uuid);
            member.setPassword(authService.encode(member.getPassword()));

            member.setNickname(nicknameGenerator.generate());

            memberRepository.registerMember(member);
            return HttpStatus.CREATED;
        }
        else {
            return HttpStatus.NOT_FOUND;
        }
    }

    public HttpStatus googleSignUp(String code) {
        String accessToken = authService.getGoogleAccessToken(code, "http://localhost:8080/signup");
        String email = authService.getGoogleEmail(accessToken);

        if(memberRepository.isRegisteredEmail(email)) {
            return HttpStatus.CONFLICT;
        }
        else {
            String nickname = nicknameGenerator.generate();
            memberRepository.registerMember(new Member(email, null, "google", null, nickname));

            return HttpStatus.CREATED;
        }
    }
}