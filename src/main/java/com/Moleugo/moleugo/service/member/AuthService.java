package com.Moleugo.moleugo.service.member;

import com.Moleugo.moleugo.entity.Member;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final HttpSession session;
    private final BCryptPasswordEncoder encoder;

    @Value("${oauth.google-client-secret}")
    private String googleClientSecret;

    public String createSession(Member member, int second) {
        UUID uuid = UUID.randomUUID();
        session.setAttribute(uuid.toString(), member);
        session.setMaxInactiveInterval(second);
        return uuid.toString();
    }

    public String encode(String string) {
        return encoder.encode(string);
    }

    public String getGoogleAccessToken(String code, String redirectUri) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", "548082459277-meajirn1br2a1g2916ottstii0d54sc2.apps.googleusercontent.com");
        params.add("client_secret", googleClientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        return new RestTemplate()
                .postForObject("https://oauth2.googleapis.com/token", request, Map.class)
                .get("access_token").toString();
    }

    public String getGoogleEmail(String accessToken) {
        return new RestTemplate()
                .getForObject("https://www.googleapis.com/oauth2/v1/userinfo?access_token=" + accessToken, Map.class)
                .get("email").toString();
    }
}