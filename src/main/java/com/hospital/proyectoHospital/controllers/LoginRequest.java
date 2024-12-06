package com.hospital.proyectoHospital.controllers;

import com.hospital.proyectoHospital.models.Usuario;

public record LoginRequest(
        String username,
        String password
) {
}
