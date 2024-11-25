package com.hospital.proyectoHospital.security;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class PasswordUtils {
    public boolean isPasswordStrong(String password) {
        if (!StringUtils.hasText(password)) {
            return false;
        }

        if (password.length() < 8) {
            return false;
        }

        boolean hasUpperCase = password.matches(".*[A-Z].*");
        boolean hasLowerCase = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*[0-9].*");
        boolean hasSpecialChar = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");

        return hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar;
    }
}
