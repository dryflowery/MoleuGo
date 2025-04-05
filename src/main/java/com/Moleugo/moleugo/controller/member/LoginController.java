package com.Moleugo.moleugo.controller.member;

import com.Moleugo.moleugo.entity.Member;
import com.Moleugo.moleugo.response.LoginResponse;
import com.Moleugo.moleugo.service.member.facade.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class LoginController {
    private final MemberService memberService;
    private final ApplicationContext ac;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Member member, HttpServletResponse resp) {
        LoginResponse loginResponse = ac.getBean(LoginResponse.class);
        HttpStatus loginStatus = memberService.login(member);

        if(loginResponse.getCookie() != null) {
            resp.addCookie(loginResponse.getCookie());
            loginResponse.setCookie(null);
        }

        return ResponseEntity.status(loginStatus).body(loginResponse);
    }

    @GetMapping("/login")
    public String googleLogin(@RequestParam("code") String code, @RequestParam("scope") String scope,
                              @RequestParam("authuser") String authuser, @RequestParam("prompt") String prompt,
                              HttpServletResponse resp) {

        LoginResponse loginResponse = ac.getBean(LoginResponse.class);
        HttpStatus googleLoginStatus = memberService.googleLogin(code);

        if(googleLoginStatus == HttpStatus.OK) {
            resp.addCookie(loginResponse.getCookie());
            loginResponse.setCookie(null);

            return "redirect:/#/my-page";
        }
        else {
            return "redirect:/#/signup";
        }
    }

    @GetMapping("/auth/status")
    public ResponseEntity<?> isUserLoggedIn(@CookieValue(value = "user_session", required = false) String userSession) {
        if (userSession != null) {
            return ResponseEntity.status(memberService.isUserLoggedIn(userSession)).build();
        }
        else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue(value = "user_session", required = false) String userSession, HttpServletResponse resp) {
        if (userSession != null) {
            HttpStatus logoutStatus = memberService.logout(userSession);

            if(logoutStatus == HttpStatus.OK) {
                Cookie cookie = new Cookie("user_session", null);
                cookie.setMaxAge(0);
                resp.addCookie(cookie);
            }

            return ResponseEntity.status(logoutStatus).build();
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
