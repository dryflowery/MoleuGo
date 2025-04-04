package com.Moleugo.moleugo.repository;

import com.Moleugo.moleugo.entity.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import java.util.List;

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

    @Override
    public void updateEmail(String oldEmail, String newEmail) {
        Member oldMember = findByEmail(oldEmail);
        if (oldMember != null) {
            Member newMember = new Member(
                    newEmail,
                    oldMember.getPassword(),
                    oldMember.getAccount_type(),
                    null,
                    oldMember.getNickname()
            );
            em.persist(newMember);   // 새 회원 먼저 등록
            em.remove(oldMember);    // 기존 회원 제거
        }
    }

    @Override
    public List<Member> findAll() {
        return em.createQuery("SELECT m FROM Member m", Member.class).getResultList();
    }

    @Override
    public void updatePassword(String email, String encodedPassword) {
        Member member = findByEmail(email);
        if (member != null) {
            member.setPassword(encodedPassword);
        }
    }

    @Override
    public void updateNickname(String email, String newNickname) {
        Member member = findByEmail(email);
        if (member != null) {
            member.setNickname(newNickname);
        }
    }
}