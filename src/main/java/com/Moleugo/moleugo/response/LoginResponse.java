package com.Moleugo.moleugo.response;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Setter
@Getter
public class LoginResponse {
    String loginMessage;
}
