package com.Moleugo.moleugo.service.member.auth;

import com.Moleugo.moleugo.dto.FindPasswordRequest;
import com.Moleugo.moleugo.entity.Member;
import com.Moleugo.moleugo.repository.member.MemberRepository;
import com.Moleugo.moleugo.service.member.mail.MailService;
import com.Moleugo.moleugo.util.PasswordGenerator;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FindPasswordService {
    private final AuthService authService;
    private final MailService mailService;
    private final PasswordGenerator passwordGenerator;
    private final MemberRepository memberRepository;
    private final HttpSession session;
    @Value("${app.base-url}")
    private String baseUrl;

    public void sendVerificationEmail(FindPasswordRequest req) {
        Member member = new Member(req.getEmail());
        String verificationLink = baseUrl + "/user/find-password/" + authService.createSession(member, 1800);
        String to = member.getEmail();
        String title = "[moleugo] 비밀번호 변경을 완료하려면 이메일을 확인하세요!";
        String content = """
                        안녕하세요, <b>moleugo</b>를 이용해 주셔서 감사합니다!<br/>
                        비밀번호 변경 요청이 접수되었습니다. 아래의 버튼을 클릭하여 이메일 인증을 완료해주세요.<br/><br/>
                        
                        🔗 <a href="{url}" style="text-decoration: none; color: #007bff; font-weight: bold;">비밀번호 변경</a><br><br>
                        
                        만약 본인이 비밀번호 변경을 요청하지 않으셨다면 이 이메일을 무시하셔도 됩니다.<br/><br/>
                        """.replace("{url}", verificationLink);

        mailService.setTo(to);
        mailService.setTitle(title);
        mailService.setContent(content);
        mailService.send();
    }

    public void setTemporaryPassword(String uuid) {
        Member member = (Member)session.getAttribute(uuid);

        // 랜덤한 임시 비밀번호 생성 후 db 갱신
        member.setPassword(passwordGenerator.generatePassword());
        String encodedPassword = authService.encode(member.getPassword());
        memberRepository.updatePassword(member.getEmail(), encodedPassword);

        // session 갱신
        session.removeAttribute(uuid);
        session.setAttribute(uuid, member);
    }

    public String getTemporaryPassword(String uuid) {
        Member member = (Member)session.getAttribute(uuid);
        session.removeAttribute(uuid);
        return member.getPassword();
    }
}