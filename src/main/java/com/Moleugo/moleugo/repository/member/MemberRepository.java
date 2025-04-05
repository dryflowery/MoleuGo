package com.Moleugo.moleugo.repository.member;

import com.Moleugo.moleugo.entity.Member;
import java.util.List;

public interface MemberRepository {
    // DB에 멤버 insert
    void registerMember(Member member);

    // 이메일로 멤버 조회
    Member findByEmail(String email);

    // DB에 존재하는 이메일인지 확인
    boolean isRegisteredEmail(String email);

    // 매개변수로 들어온 멤버의 비밀번호와 DB의 비밀번호가 동일한지 확인
    boolean isCorrectPassword(Member member);

    // account_type이 normal인지 확인
    boolean isNormalMember(String email);

    // account_type이 google인지 확인
    boolean isGoogleMember(String email);

    // 이메일 수정
    void updateEmail(String oldEmail, String newEmail);

    // 비밀번호 수정
    void updatePassword(String email, String encodedPassword);

    // 닉네임 수정
    void updateNickname(String email, String newNickname);
    List<Member> findAll();
}
