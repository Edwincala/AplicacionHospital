package com.hospital.proyectoHospital.security;

import com.hospital.proyectoHospital.controllers.PacientesController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class PasswordUtils {
    public boolean isPasswordStrong(String password) {

        final Logger log = LoggerFactory.getLogger(PacientesController.class);

        if (!StringUtils.hasText(password)) {
            log.warn("La contraseña está vacía o nula.");
            return false;
        }

        if (password.length() < 8) {
            log.warn("La contraseña es demasiado corta. Longitud: {}", password.length());
            return false;
        }

        boolean hasUpperCase = password.matches(".*[A-Z].*");
        boolean hasLowerCase = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*[0-9].*");
        boolean hasSpecialChar = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");

        if (!hasUpperCase) {
            log.warn("La contraseña no contiene al menos una letra mayúscula.");
        }
        if (!hasLowerCase) {
            log.warn("La contraseña no contiene al menos una letra minúscula.");
        }
        if (!hasDigit) {
            log.warn("La contraseña no contiene al menos un número.");
        }
        if (!hasSpecialChar) {
            log.warn("La contraseña no contiene al menos un carácter especial.");
        }

        return hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar;
    }
}
