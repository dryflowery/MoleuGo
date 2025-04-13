package com.Moleugo.moleugo.service.member.facade;

import com.Moleugo.moleugo.dto.FindPasswordRequest;
import com.Moleugo.moleugo.dto.PasswordChangeRequest;
import com.Moleugo.moleugo.entity.Member;
import com.Moleugo.moleugo.service.member.auth.FindPasswordService;
import com.Moleugo.moleugo.service.member.auth.LoginService;
import com.Moleugo.moleugo.service.member.auth.SignupService;
import com.Moleugo.moleugo.service.member.profile.EmailChangeService;
import com.Moleugo.moleugo.service.member.profile.NicknameChangeService;
import com.Moleugo.moleugo.service.member.profile.PasswordChangeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpSession;


@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final SignupService signupService;
    private final LoginService loginService;
    private final EmailChangeService emailChangeService;
    private final PasswordChangeService passwordChangeService;
    private final NicknameChangeService nicknameChangeService;
    private final FindPasswordService findPasswordService;


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
        return nicknameChangeService.changeNickname(sessionId, newNickname);
    }

    public void sendVerificationEmail(FindPasswordRequest req) {
        findPasswordService.sendVerificationEmail(req);
    }

    public void setTemporaryPassword(String uuid) {
        findPasswordService.setTemporaryPassword(uuid);
    }

    public String getTemporaryPassword(String uuid) {
        return findPasswordService.getTemporaryPassword(uuid);
    }
}