package com.Moleugo.moleugo.service.member.profile;

import com.Moleugo.moleugo.entity.Member;
import com.Moleugo.moleugo.repository.member.MemberRepository;
import com.Moleugo.moleugo.service.member.auth.AuthService;
import com.Moleugo.moleugo.service.member.mail.MailService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Enumeration;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailChangeService {

    private final HttpSession session;
    private final MemberRepository memberRepository;
    private final MailService mailService;
    private final AuthService authService;

    /**
     * 현재 로그인한 사용자로부터 이메일 변경 요청을 받아 새 이메일로 인증 메일 전송
     */
    public HttpStatus requestEmailChange(String sessionId, String newEmail) {
        Member currentMember = (Member) session.getAttribute(sessionId);
        if (currentMember == null) return HttpStatus.UNAUTHORIZED;

        if (memberRepository.isRegisteredEmail(newEmail)) {
            return HttpStatus.CONFLICT;
        }

        // 새 Member 정보와 함께 기존 이메일도 따로 저장
        Member newMember = new Member(newEmail, currentMember.getPassword(), currentMember.getAccount_type());
        String uuid = authService.createSession(newMember, 1800);
        session.setAttribute("old_email_" + uuid, currentMember.getEmail()); // 기존 이메일 저장

        String verificationLink = "http://localhost:8080/user/change-email/" + uuid;
        String content = """
            이메일 변경 요청이 접수되었습니다.<br/>
            아래 링크를 클릭하여 새 이메일을 인증해주세요.<br/><br/>
            🔗 <a href="%s">이메일 인증하기</a>
        """.formatted(verificationLink);

        mailService.setTo(newEmail);
        mailService.setTitle("[moleugo] 이메일 변경 인증 요청");
        mailService.setContent(content);
        mailService.send();

        return HttpStatus.OK;
    }

    /**
     * 인증 메일 링크 클릭 시 이메일을 실제로 변경
     */
    public HttpStatus confirmEmailChange(String uuid) {
        Member newMember = (Member) session.getAttribute(uuid);
        String oldEmail = (String) session.getAttribute("old_email_" + uuid);

        if (newMember == null || oldEmail == null) {
            return HttpStatus.NOT_FOUND;
        }

        log.info("이메일 변경 시도: {} → {}", oldEmail, newMember.getEmail());

        session.removeAttribute(uuid);
        session.removeAttribute("old_email_" + uuid);

        memberRepository.updateEmail(oldEmail, newMember.getEmail());

        Enumeration<String> attributeNames = session.getAttributeNames();

        while (attributeNames.hasMoreElements()) { // 세션 내 user_session도 새 이메일로 업데이트
            String attr = attributeNames.nextElement();
            Object obj = session.getAttribute(attr);

            if (obj instanceof Member member && member.getEmail().equals(oldEmail)) {
                Member updatedMember = memberRepository.findByEmail(newMember.getEmail());
                session.setAttribute(attr, updatedMember); // 세션 갱신
                log.info("세션 이메일 업데이트 완료: {}", updatedMember.getEmail());
                break;
            }
        }

        return HttpStatus.OK;
    }
}