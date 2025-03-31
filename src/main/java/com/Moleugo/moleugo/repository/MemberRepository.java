package com.Moleugo.moleugo.repository;

import com.Moleugo.moleugo.entity.Member;
import java.util.List;


public interface MemberRepository {
    void registerMember(Member member);
    Member findByEmail(String email);
    boolean isRegisteredEmail(String email);
    boolean isCorrectPassword(Member member);
    boolean isNormalMember(String email);
    boolean isGoogleMember(String email);
    void updateAccountType(String email, String newAccountType);
    void updateEmail(String oldEmail, String newEmail);
    void updatePassword(String email, String encodedPassword);
    void updateNickname(String email, String newNickname);
    List<Member> findAll();
}
