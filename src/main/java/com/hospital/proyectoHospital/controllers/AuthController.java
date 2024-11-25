package com.hospital.proyectoHospital.controllers;

import com.hospital.proyectoHospital.models.Usuario;
import com.hospital.proyectoHospital.services.AuthService;
import com.hospital.proyectoHospital.services.JwtService;
import com.hospital.proyectoHospital.services.UsuarioService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final UsuarioService usuarioService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, UsuarioService usuarioService, JwtService jwtService) {
        this.authService = authService;
        this.usuarioService = usuarioService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@RequestBody Usuario usuario) {
        if (usuarioService.existsByUsername(usuario.getUsername())) {
            return ResponseEntity.badRequest().body(null);
        }

        Usuario savedUsuario = authService.registerUser(usuario);
        String accessToken = jwtService.generateToken(savedUsuario);
        String refreshToken = jwtService.generateRefreshToken(savedUsuario);

        return ResponseEntity.ok(new TokenResponse(accessToken, refreshToken));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> authenticate(@RequestBody LoginRequest loginRequest) {
        try {
            authService.login(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.username(),
                            loginRequest.password()
                    )
            );

            Optional<Usuario> usuario = usuarioService.findByUsername(loginRequest.username());

            if (usuario.isEmpty()) {
                return ResponseEntity.status(404).body(null);
            }

            String accessToken = jwtService.generateToken(usuario.get());
            String refreshToken = jwtService.generateRefreshToken(usuario.get());

            return ResponseEntity.ok(new TokenResponse(accessToken, refreshToken));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body(null);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(@RequestHeader(HttpHeaders.AUTHORIZATION) final String authHeader) {
        try {
            TokenResponse response = authService.refreshToken(authHeader);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(null);
        }
    }
}
