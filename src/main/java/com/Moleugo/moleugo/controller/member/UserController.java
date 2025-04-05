package com.Moleugo.moleugo.controller.member;

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

    private final MemberService memberService;

    @GetMapping("/email")
    public ResponseEntity<String> getUserEmail(@CookieValue(value = "user_session", required = false) String userSession) {
        HttpSession session = memberService.getSession();
        if (userSession != null) {
            Member member = (Member) session.getAttribute(userSession);
            if (member != null) {
                return ResponseEntity.ok(member.getEmail());
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

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
            return "redirect:/#/signup-result?result=email-change-success";
        }
        return "redirect:/#/signup-result?result=email-change-fail";
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@CookieValue("user_session") String sessionId,
                                            @RequestBody PasswordChangeRequest req) {
        HttpStatus status = memberService.changePassword(sessionId, req);
        return ResponseEntity.status(status).build();

    }

    @GetMapping("/nickname")
    public ResponseEntity<String> getUserNickname(@CookieValue(value = "user_session", required = false) String userSession) {
        HttpSession session = memberService.getSession();
        if (userSession != null) {
            Member member = (Member) session.getAttribute(userSession);
            if (member != null) {

                return ResponseEntity.ok(member.getNickname());
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/change-nickname")
    public ResponseEntity<?> changeNickname(@CookieValue("user_session") String sessionId,
                                            @RequestBody NicknameChangeRequest req) {
        HttpStatus status = memberService.changeNickname(sessionId, req.getNewNickname());
        return ResponseEntity.status(status).build();
    }

    @GetMapping("/account-type")
    public ResponseEntity<String> getUserAccountType(@CookieValue(value = "user_session", required = false) String userSession) {
        HttpSession session = memberService.getSession();
        if (userSession != null) {
            Member member = (Member) session.getAttribute(userSession);
            if (member != null) {
                return ResponseEntity.ok(member.getAccount_type());
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}