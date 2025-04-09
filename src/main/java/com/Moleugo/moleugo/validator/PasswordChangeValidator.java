package com.Moleugo.moleugo.validator;

import com.Moleugo.moleugo.dto.PasswordChangeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class PasswordChangeValidator {
    private final PasswordValidator passwordValidator;

    public boolean isValid(PasswordChangeRequest req) {
        String pw = req.getNewPassword().replaceAll("\\s", "");
        String verify = req.getVerifyNewPassword();

        return passwordValidator.isValidPassword(verify) && pw.equals(verify);
    }
}