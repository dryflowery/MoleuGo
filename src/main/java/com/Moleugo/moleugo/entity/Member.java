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
        this(email, null, null);
    }

    public Member(String email, String password) {
        this(email, password, null);
    }

    public Member(String email, String password, String verifyPassword) {
        this.email = email;
        this.password = password;
        this.verifyPassword = verifyPassword;
    }

    @Id
    private String email;

    private String password;

    @Transient
    private String verifyPassword;
}