package com.Moleugo.moleugo.util;

import com.Moleugo.moleugo.validator.PasswordValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.security.SecureRandom;

@Component
@RequiredArgsConstructor
public class PasswordGenerator {
    private final PasswordValidator passwordValidator;
    private final SecureRandom random = new SecureRandom();
    private final String allChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxy0123456789z!@#$%^&*()_+-=[]{}|;':,.<>/?";

    public String generatePassword() {
        int passwordLength = 8;

        while (true) {
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < passwordLength; i++) {
                sb.append(allChars.charAt(random.nextInt(allChars.length())));
            }

            String password = sb.toString();

            if (passwordValidator.isValidPassword(password)) {
                return password;
            }
        }
    }
}