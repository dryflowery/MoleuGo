// service/member/PasswordChangeService.java
package com.Moleugo.moleugo.service.member;

import com.Moleugo.moleugo.dto.PasswordChangeRequest;
import com.Moleugo.moleugo.entity.Member;
import com.Moleugo.moleugo.repository.MemberRepository;
import com.Moleugo.moleugo.service.validator.PasswordChangeValidator;
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
        memberRepository.updatePassword(sessionMember.getEmail(), encodedPassword);
        sessionMember.setPassword(encodedPassword);
        session.setAttribute(sessionId, sessionMember);

        return HttpStatus.OK;
    }
}
