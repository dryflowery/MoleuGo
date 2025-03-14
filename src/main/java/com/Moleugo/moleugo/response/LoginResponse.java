package com.Moleugo.moleugo.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.servlet.http.Cookie;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Component
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {
    String loginMessage;
    Cookie cookie;

    public void setCookie(Cookie cookie) {
        this.cookie = cookie;

        if(cookie != null) {
            cookie.setPath("/");
            cookie.setMaxAge(7200);
            cookie.setHttpOnly(true);
        }
    }
}
