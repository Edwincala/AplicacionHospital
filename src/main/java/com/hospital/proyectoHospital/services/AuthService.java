package com.hospital.proyectoHospital.services;

import com.hospital.proyectoHospital.controllers.TokenResponse;
import com.hospital.proyectoHospital.models.Token;
import com.hospital.proyectoHospital.models.Usuario;
import com.hospital.proyectoHospital.repositories.TokenRepository;
import com.hospital.proyectoHospital.repositories.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    public AuthService(TokenRepository tokenRepository,
                       PasswordEncoder passwordEncoder,
                       UsuarioRepository usuarioRepository,
                       JwtService jwtService,
                       @Lazy AuthenticationManager authenticationManager, UserDetailsService userDetailsService) {
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    public TokenResponse login(UsernamePasswordAuthenticationToken authRequest) {
        try {
            authenticationManager.authenticate(authRequest);

            Usuario user = usuarioRepository.findByUsername(authRequest.getName())
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

            String jwtToken = jwtService.generateToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            revokeAllUserTokens(user);
            saveUserToken(user, jwtToken);

            return new TokenResponse(jwtToken, refreshToken, user.getRol(), user.getUsername());
        } catch (Exception e) {
            throw new BadCredentialsException("Error en la autenticación", e);
        }
    }

    @Transactional
    public TokenResponse refreshToken(String refreshToken) {
        String username = jwtService.extractUsername(refreshToken);
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        if (!jwtService.isTokenValid(refreshToken, usuario)) {
            throw new IllegalArgumentException("Token inválido o expirado");
        }

        String accessToken = jwtService.generateToken(usuario);
        String newRefreshToken = jwtService.generateRefreshToken(usuario);

        revokeAllUserTokens(usuario);
        saveUserToken(usuario, accessToken);

        return new TokenResponse(accessToken, newRefreshToken, usuario.getRol(), usuario.getUsername());
    }

    @Transactional
    private void saveUserToken(Usuario usuario, String jwtToken) {
        var token = new Token.TokenBuilder()
                .usuario(usuario)
                .token(jwtToken)
                .tokenType(Token.TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();

        // Borrar cualquier token existente antes de guardar el nuevo
        tokenRepository.deleteByToken(jwtToken);
        tokenRepository.save(token);
    }

    @Transactional
    private void revokeAllUserTokens(Usuario usuario) {
        var validTokens = tokenRepository.findByUsuarioIdAndExpiredIsFalseAndRevokedIsFalse(usuario.getId());
        if (validTokens.isEmpty()) return;

        validTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });

        tokenRepository.saveAll(validTokens);
    }
}
