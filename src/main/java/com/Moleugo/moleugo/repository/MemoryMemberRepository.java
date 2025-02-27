package com.Moleugo.moleugo.repository;

import com.Moleugo.moleugo.entity.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.HashMap;

@Slf4j
@Repository
public class MemoryMemberRepository implements MemberRepository {
    private final HashMap<String, Member> repository = new HashMap<>();

    @Override
    public void registerMember(Member member) {
        repository.put(member.getEmail(), member);
    }

    @Override
    public Member findByEmail(String email) {
        return repository.get(email);
    }
}
