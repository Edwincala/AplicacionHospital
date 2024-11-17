package com.hospital.proyectoHospital.security;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class PasswordUtils {
    public boolean isPasswordStrong(String password) {
        return StringUtils.hasText(password)
                && password.length() >= 6
                && password.matches(".*[A-Z].*")
                && password.matches(".*[a-z].*")
                && password.matches(".*[0-9].*")
                && password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");
    }
}
