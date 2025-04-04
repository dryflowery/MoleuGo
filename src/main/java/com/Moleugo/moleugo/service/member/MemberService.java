package com.Moleugo.moleugo.service.member;

import com.Moleugo.moleugo.dto.PasswordChangeRequest;
import com.Moleugo.moleugo.entity.Member;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpSession;
import com.Moleugo.moleugo.service.member.EmailChangeService;


@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final SignupService signupService;
    private final LoginService loginService;
    private final EmailChangeService emailChangeService;
    private final PasswordChangeService passwordChangeService;
    private final NicknameChangeService nicknameChangeService;


    public HttpStatus isValidForm(Member member) {
        return signupService.isValidForm(member);
    }

    public void sendVerificationEmail(Member member) {
        signupService.sendVerificationEmail(member);
    }

    public HttpStatus signUp(String uuid) {
        return signupService.signUp(uuid);
    }

    public HttpStatus googleSignUp(String code) {
        return signupService.googleSignUp(code);
    }

    public HttpStatus login(Member member) {
        return loginService.login(member);
    }

    public HttpStatus googleLogin(String code) {
        return loginService.googleLogin(code);
    }

    public HttpStatus isUserLoggedIn(String userSession) {
        return loginService.isUserLoggedIn(userSession);
    }

    public HttpStatus logout(String userSession) {
        return loginService.logout(userSession);
    }

    public HttpSession getSession() {
        return loginService.getSession();
    }

    public HttpStatus requestEmailChange(String sessionId, String newEmail) {
        return emailChangeService.requestEmailChange(sessionId, newEmail);
    }

    public HttpStatus confirmEmailChange(String uuid) {
        return emailChangeService.confirmEmailChange(uuid);
    }

    public HttpStatus changePassword(String sessionId, PasswordChangeRequest req) {
        return passwordChangeService.changePassword(sessionId, req);
    }

    public HttpStatus changeNickname(String sessionId, String newNickname) {
        Member member = loginService.getSession().getAttribute(sessionId) instanceof Member m ? m : null;
        if (member == null) return HttpStatus.UNAUTHORIZED;

        return nicknameChangeService.changeNickname(member, newNickname);
    }
}