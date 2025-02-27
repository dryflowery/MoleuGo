package com.Moleugo.moleugo.repository;

import com.Moleugo.moleugo.entity.Member;

public interface MemberRepository {
    void registerMember(Member member);
    Member findByEmail(String email);
}
