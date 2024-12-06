package com.hospital.proyectoHospital.controllers;

import com.hospital.proyectoHospital.exceptions.TokenExpiredException;
import com.hospital.proyectoHospital.exceptions.TokenRevokedException;
import com.hospital.proyectoHospital.models.PerfilDto;
import com.hospital.proyectoHospital.models.Usuario;
import com.hospital.proyectoHospital.services.AuthService;
import com.hospital.proyectoHospital.services.UsuarioService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {
    private final AuthService authService;
    private final UsuarioService usuarioService;

    public AuthController(AuthService authService, UsuarioService usuarioService) {
        this.authService = authService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/real-register")
    public ResponseEntity<?> register(@RequestBody PerfilDto perfilDto) {
        if (usuarioService.existsByUsername(perfilDto.getUsername())) {
            return ResponseEntity.badRequest().body("El nombre de usuario ya existe.");
        }

        try {
            Usuario usuario = usuarioService.createUserFromPerfilDto(perfilDto);
            TokenResponse response = authService.login(
                    new UsernamePasswordAuthenticationToken(
                            perfilDto.getUsername(),
                            perfilDto.getPassword()
                    )
            );
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Datos inválidos: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // Usuarios de prueba (simulados)
        Map<String, UserTestData> testUsers = Map.of(
                "admin", new UserTestData("ROLE_ADMINISTRATIVO", "Administrador", "García"),
                "doctor", new UserTestData("ROLE_DOCTOR", "Dr. Juan", "Pérez"),
                "paciente", new UserTestData("ROLE_PACIENTE", "María", "López")
        );

        UserTestData user = testUsers.get(request.username());

        if (user != null) { // No validamos password
            return ResponseEntity.ok(new LoginResponse(
                    user.role(),
                    user.nombre(),
                    user.apellido()
            ));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Usuario no encontrado"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        if (request.refreshToken() == null || request.refreshToken().isEmpty()) {
            return ResponseEntity.badRequest().body("El token de refresco es obligatorio.");
        }

        try {
            TokenResponse response = authService.refreshToken(request.refreshToken());
            return ResponseEntity.ok(response);
        } catch (TokenExpiredException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("El token de refresco ha expirado.");
        } catch (TokenRevokedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("El token de refresco ha sido revocado.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar el token de refresco.");
        }
    }


    @GetMapping("/real-me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No se encontró un usuario autenticado.");
        }

        try {
            PerfilDto perfil = usuarioService.getUserProfile(authentication.getName());
            return ResponseEntity.ok(perfil);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al obtener el usuario.");
        }
    }

}
