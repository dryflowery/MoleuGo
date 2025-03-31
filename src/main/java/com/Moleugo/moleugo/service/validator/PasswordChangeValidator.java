package com.Moleugo.moleugo.service.validator;

import com.Moleugo.moleugo.dto.PasswordChangeRequest;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.regex.Pattern;

@Component
public class PasswordChangeValidator {

    public boolean isValid(PasswordChangeRequest req) {
        String pw = req.getNewPassword().replaceAll("\\s", "");
        String verify = req.getVerifyNewPassword();

        return isValidLength(pw) &&
                hasAtLeastTwoCharTypes(pw) &&
                hasNoTripleConsecutiveChars(pw) &&
                pw.equals(verify);
    }

    private boolean isValidLength(String password) {
        return password.length() >= 8 && password.length() <= 32;
    }

    private boolean hasAtLeastTwoCharTypes(String password) {
        Pattern[] patterns = {
                Pattern.compile("[a-zA-Z]"),
                Pattern.compile("[0-9]"),
                Pattern.compile("[^a-zA-Z0-9\\s]")
        };
        long count = Arrays.stream(patterns)
                .filter(p -> p.matcher(password).find())
                .count();
        return count >= 2;
    }

    private boolean hasNoTripleConsecutiveChars(String password) {
        return !Pattern.compile("(.)\\1\\1").matcher(password).find();
    }
}
