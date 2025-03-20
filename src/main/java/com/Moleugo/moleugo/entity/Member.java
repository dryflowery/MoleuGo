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

    public Member(String email, String password, String account_type) {
        this(email, password, account_type, null);
    }

    public Member(String email, String password, String account_type, String verifyPassword) {
        this.email = email;
        this.password = password;
        this.account_type = account_type;
        this.verifyPassword = verifyPassword;
    }

    @Id
    private String email;

    private String password;
    private String account_type;

    @Transient
    private String verifyPassword;
}