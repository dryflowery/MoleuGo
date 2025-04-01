package com.Moleugo.moleugo.service.member;

import com.Moleugo.moleugo.entity.Member;
import com.Moleugo.moleugo.repository.MemberRepository;
import com.Moleugo.moleugo.service.validator.NicknameValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class NicknameChangeService {
    private final MemberRepository memberRepository;
    private final NicknameValidator nicknameValidator;

    public HttpStatus changeNickname(Member member, String newNickname) {
        if (!nicknameValidator.isValid(newNickname)) {
            return HttpStatus.BAD_REQUEST;
        }

        member.setNickname(newNickname);
        memberRepository.updateNickname(member.getEmail(), newNickname);
        return HttpStatus.OK;
    }
}

