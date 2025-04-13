package com.Moleugo.moleugo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "member")
public class Member {
    public Member() {}

    public Member(String email) {
        this(email, null, "normal", null, null);
    }

    public Member(String email, String password, String account_type) {
        this(email, password, account_type, null);
    }

    public Member(String email, String password, String account_type, String verifyPassword) {
        this(email, password, account_type, verifyPassword, null);
    }

    public Member(String email, String password, String account_type, String verifyPassword, String nickname) {
        this.email = email;
        this.password = password;
        this.account_type = account_type;
        this.verifyPassword = verifyPassword;
        this.nickname = nickname;
    }

    @Id
    private String email;

    private String password;
    private String account_type;
    private String nickname;

    @Transient
    private String verifyPassword;
}