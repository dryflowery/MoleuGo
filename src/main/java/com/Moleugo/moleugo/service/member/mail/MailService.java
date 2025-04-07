package com.Moleugo.moleugo.service.member.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Getter
@Setter
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;
    private String to;
    private String title;
    private String content;

    public void send() {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

            helper.setTo(to);
            helper.setSubject(title);
            helper.setText(content, true);

            mailSender.send(mimeMessage);
        }
        catch (MessagingException e) {
            throw new RuntimeException("메일 전송 실패", e);
        }
    }
}