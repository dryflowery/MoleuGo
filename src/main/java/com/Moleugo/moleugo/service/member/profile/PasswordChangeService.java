// service/member/PasswordChangeService.java
package com.Moleugo.moleugo.service.member.profile;

import com.Moleugo.moleugo.dto.PasswordChangeRequest;
import com.Moleugo.moleugo.entity.Member;
import com.Moleugo.moleugo.repository.member.MemberRepository;
import com.Moleugo.moleugo.service.member.auth.AuthService;
import com.Moleugo.moleugo.validator.PasswordChangeValidator;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordChangeService {

    private final HttpSession session;
    private final MemberRepository memberRepository;
    private final AuthService authService;
    private final PasswordChangeValidator passwordChangeValidator;

    public HttpStatus changePassword(String sessionId, PasswordChangeRequest req) {
        Member sessionMember = (Member) session.getAttribute(sessionId);
        if (sessionMember == null) return HttpStatus.UNAUTHORIZED;

        if (!memberRepository.isCorrectPassword(new Member(sessionMember.getEmail(), req.getCurrentPassword(), null))) {
            return HttpStatus.FORBIDDEN;
        }

        if (!passwordChangeValidator.isValid(req)) {
            return HttpStatus.BAD_REQUEST;
        }

        String encodedPassword = authService.encode(req.getNewPassword());

        // 비밀번호 암호화 및 새 Member 객체 생성
        Member updatedMember = new Member(
                sessionMember.getEmail(),
                encodedPassword,
                sessionMember.getAccount_type(),
                null,
                sessionMember.getNickname()
        );

        memberRepository.updateMember(updatedMember); // Repository에 저장

        session.setAttribute(sessionId, sessionMember); // 세션 갱신

        return HttpStatus.OK;
    }
}