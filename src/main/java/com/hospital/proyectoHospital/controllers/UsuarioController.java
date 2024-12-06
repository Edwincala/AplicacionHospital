package com.hospital.proyectoHospital.controllers;

import com.hospital.proyectoHospital.exceptions.UsuarioNotFoundException;
import com.hospital.proyectoHospital.models.Doctor;
import com.hospital.proyectoHospital.models.Paciente;
import com.hospital.proyectoHospital.models.PerfilDto;
import com.hospital.proyectoHospital.models.Usuario;
import com.hospital.proyectoHospital.services.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PutMapping("/perfil")
    public ResponseEntity<?> updateProfile(@RequestBody PerfilDto profileData) {
        try {
            return ResponseEntity.ok(profileData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar el perfil");
        }
    }

    @GetMapping("/perfil")
    public ResponseEntity<?> getProfile() {
        Map<String, String> profile = new HashMap<>();
        profile.put("username", "admin");
        profile.put("nombre", "Administrador");
        profile.put("apellido", "García");
        profile.put("role", "ROLE_ADMINISTRATIVO");

        return ResponseEntity.ok(profile);
    }

    @PostMapping("/crear")
    public ResponseEntity<?> createUser(@RequestBody PerfilDto perfilDto) {
        try {
            Usuario usuario = usuarioService.createUserFromPerfilDto(perfilDto);
            return ResponseEntity.status(HttpStatus.CREATED).body("Usuario creado con éxito: " + usuario.getUsername());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}