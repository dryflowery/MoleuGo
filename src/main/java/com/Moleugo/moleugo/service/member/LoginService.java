package com.Moleugo.moleugo.service.member;

import com.Moleugo.moleugo.entity.Member;
import com.Moleugo.moleugo.response.LoginResponse;
import com.Moleugo.moleugo.service.validator.LoginValidator;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final ApplicationContext ac;
    private final HttpSession session;
    private final AuthService authService;

    public HttpStatus login(Member member) {
        LoginValidator loginValidator = ac.getBean(LoginValidator.class);
        HttpStatus loginStatus = loginValidator.isValidLogin(member);

        if(loginStatus == HttpStatus.OK) {
            member.setPassword(authService.encode(member.getPassword()));
            String uuid = authService.createSession(member, 7200);

            LoginResponse loginResponse = ac.getBean(LoginResponse.class);
            loginResponse.setCookie(new Cookie("user_session", uuid));
        }

        return loginStatus;
    }

    public HttpStatus isUserLoggedIn(String userSession) {
        if (session.getAttribute(userSession) != null) {
            return HttpStatus.OK;
        }
        else {
            return HttpStatus.UNAUTHORIZED;
        }
    }

    public HttpStatus logout(String userSession) {
        if (session.getAttribute(userSession) != null) {
            session.removeAttribute(userSession);
            return HttpStatus.OK;
        }
        else {
            return HttpStatus.NOT_FOUND;
        }
    }
}