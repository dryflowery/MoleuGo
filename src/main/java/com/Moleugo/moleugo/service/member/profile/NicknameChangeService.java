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
            if (nicknameValidator.isDuplicate(newNickname)) {
                return HttpStatus.CONFLICT; //409 반환
            }
            return HttpStatus.BAD_REQUEST; // 그 외 400
        }

        Member updatedMember = new Member(
                member.getEmail(),
                member.getPassword(),
                member.getAccount_type(),
                null,
                newNickname
        );

        memberRepository.updateMember(updatedMember);

        loginService.getSession().setAttribute(sessionId, updatedMember); // 세션도 새 객체로 교체

        return HttpStatus.OK;
    }
}