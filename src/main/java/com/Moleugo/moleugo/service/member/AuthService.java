package com.Moleugo.moleugo.service.member;

import com.Moleugo.moleugo.entity.Member;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final HttpSession session;
    private final BCryptPasswordEncoder encoder;

    public String createSession(Member member, int second) {
        UUID uuid = UUID.randomUUID();
        session.setAttribute(uuid.toString(), member);
        session.setMaxInactiveInterval(second);
        return uuid.toString();
    }

    public void encodePassword(Member member) {
        member.setPassword(encoder.encode(member.getPassword()));
    }
}
