package com.Moleugo.moleugo.repository;

import com.Moleugo.moleugo.entity.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
@Primary
public class JpaMemberRepository implements MemberRepository {
    private final BCryptPasswordEncoder encoder;

    @PersistenceContext
    private EntityManager em;

    @Override
    public void registerMember(Member member) {
        em.persist(member);
    }

    @Override
    public Member findByEmail(String email) {
        return em.find(Member.class, email);
    }

    @Override
    public boolean isRegisteredEmail(String email) {
        return findByEmail(email) != null;
    }

    @Override
    public boolean isCorrectPassword(Member member) {
        return encoder.matches(member.getPassword(), findByEmail(member.getEmail()).getPassword());
    }

    @Override
    public boolean isNormalMember(String email) {
        return "normal".equals(findByEmail(email).getAccount_type());
    }

    @Override
    public boolean isGoogleMember(String email) {
        return "google".equals(findByEmail(email).getAccount_type());
    }

    @Override
    public void updateAccountType(String email, String newAccountType) {
        findByEmail(email).setAccount_type(newAccountType);
    }
}