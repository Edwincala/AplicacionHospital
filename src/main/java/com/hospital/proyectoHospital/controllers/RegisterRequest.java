package com.hospital.proyectoHospital.controllers;

public record RegisterRequest(
        String username,
        String password,
        String nombre,
        String apelllido
) {
}
