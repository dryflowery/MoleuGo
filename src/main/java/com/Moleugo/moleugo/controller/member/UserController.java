package com.Moleugo.moleugo.controller.member;

import com.Moleugo.moleugo.dto.FindPasswordRequest;
import com.Moleugo.moleugo.dto.NicknameChangeRequest;
import com.Moleugo.moleugo.dto.PasswordChangeRequest;
import com.Moleugo.moleugo.entity.Member;
import com.Moleugo.moleugo.service.member.facade.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final HttpSession session;
    private final MemberService memberService;

    @PostMapping("/change-email-request")
    public ResponseEntity<?> requestEmailChange(@CookieValue("user_session") String sessionId,
                                                @RequestBody Member newMemberInfo) {
        HttpStatus status = memberService.requestEmailChange(sessionId, newMemberInfo.getEmail());
        return ResponseEntity.status(status).build();
    }

    @GetMapping("/change-email/{uuid}") //이메일 인증 완료 링크 → 이메일 실제 변경
    public String confirmEmailChange(@PathVariable String uuid) {
        HttpStatus status = memberService.confirmEmailChange(uuid);
        if (status == HttpStatus.OK) {
            return "redirect:/#/result?type=" + "email-change-success";
        }
        else {
            return "redirect:/#/result?type=" + "fail";
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@CookieValue("user_session") String sessionId,
                                            @RequestBody PasswordChangeRequest req) {
        HttpStatus status = memberService.changePassword(sessionId, req);
        return ResponseEntity.status(status).build();

    }

    @PostMapping("/change-nickname")
    public ResponseEntity<?> changeNickname(@CookieValue("user_session") String sessionId,
                                            @RequestBody NicknameChangeRequest req) {
        HttpStatus status = memberService.changeNickname(sessionId, req.getNewNickname());
        return ResponseEntity.status(status).build();
    }

    @PostMapping("/find-password/verify-email")
    public void sendVerificationEmail(@RequestBody FindPasswordRequest req) {
        memberService.sendVerificationEmail(req);
    }

    @GetMapping("/find-password/{uuid}")
    public String setTemporaryPassword(@PathVariable("uuid") String uuid) {
        Member member = (Member) session.getAttribute(uuid);

        if(session.getAttribute(uuid) != null) {
            memberService.setTemporaryPassword(uuid);
            return "redirect:/#/result?type=" + "find-password-success" + "&uuid=" + uuid;
        }
        else {
            return "redirect:/#/result?type=" + "fail";
        }
    }

    @ResponseBody
    @GetMapping("/get-temporary-password/{uuid}")
    public String getTemporaryPassword(@PathVariable("uuid") String uuid) {
        return memberService.getTemporaryPassword(uuid);
    }
}