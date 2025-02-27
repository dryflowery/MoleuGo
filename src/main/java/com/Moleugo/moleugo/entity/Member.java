package com.Moleugo.moleugo.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Member {
    private String email;
    private String password;
    private String verifyPassword;
}
