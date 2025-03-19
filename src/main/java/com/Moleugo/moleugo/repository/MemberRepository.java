package com.Moleugo.moleugo.repository;

import com.Moleugo.moleugo.entity.Member;

public interface MemberRepository {
    void registerMember(Member member);
    Member findByEmail(String email);
    boolean isRegisteredEmail(String email);
    boolean isCorrectPassword(Member member);
}
