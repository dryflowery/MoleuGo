package com.Moleugo.moleugo.repository;

import com.Moleugo.moleugo.entity.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@Primary
public class JpaMemberRepository implements MemberRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void registerMember(Member member) {
        em.persist(member);
    }

    @Override
    public Member findByEmail(Member member) {
        return em.find(Member.class, member.getEmail());
    }

    @Override
    public boolean isRegisteredEmail(Member member) {
        return findByEmail(member) != null;
    }

    @Override
    public boolean isCorrectPassword(Member member) {
        return em.find(Member.class, member.getEmail())
                .getPassword()
                .equals(member.getPassword());
    }
}