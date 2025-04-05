package com.Moleugo.moleugo.service.member.profile;

import com.Moleugo.moleugo.entity.Member;
import com.Moleugo.moleugo.repository.member.MemberRepository;
import com.Moleugo.moleugo.service.member.auth.LoginService;
import com.Moleugo.moleugo.validator.NicknameValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class NicknameChangeService {
    private final MemberRepository memberRepository;
    private final NicknameValidator nicknameValidator;
    private final LoginService loginService;

    public HttpStatus changeNickname(String sessionId, String newNickname) {
        Member member = loginService.getSession().getAttribute(sessionId) instanceof Member m ? m : null;

        if (member == null) {
            return HttpStatus.UNAUTHORIZED;
        }

        if (!nicknameValidator.isValid(newNickname)) {
            return HttpStatus.BAD_REQUEST;
        }

        member.setNickname(newNickname);
        memberRepository.updateNickname(member.getEmail(), newNickname);
        return HttpStatus.OK;
    }
}