package com.hospital.proyectoHospital.controllers;

import com.hospital.proyectoHospital.models.Usuario;

public record TokenResponse(
        String accessToken,
        String refreshToken,
        Usuario.Rol rol,
        String username
) {
}
