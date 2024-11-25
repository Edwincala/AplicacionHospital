package com.hospital.proyectoHospital.security;

import com.hospital.proyectoHospital.models.Token;
import com.hospital.proyectoHospital.models.Usuario;
import com.hospital.proyectoHospital.repositories.TokenRepository;
import com.hospital.proyectoHospital.repositories.UsuarioRepository;
import com.hospital.proyectoHospital.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final TokenRepository tokenRepository;
    private final UserDetailsService userDetailsService;
    private final UsuarioRepository usuarioRepository;

    public JwtRequestFilter(JwtService jwtService, TokenRepository tokenRepository, UserDetailsService userDetailsService, UsuarioRepository usuarioRepository) {
        this.jwtService = jwtService;
        this.tokenRepository = tokenRepository;
        this.userDetailsService = userDetailsService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain)
            throws ServletException, IOException {
        // Saltar rutas públicas
        if (request.getServletPath().contains("/public")) {
            chain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // Verificar si el encabezado de autorización es válido
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        final String jwtToken = authHeader.substring(7);

        try {
            final String username = jwtService.extractUsername(jwtToken);

            // Validar que el usuario no esté autenticado previamente
            if (username == null || SecurityContextHolder.getContext().getAuthentication() != null) {
                chain.doFilter(request, response);
                return;
            }

            // Validar el token en la base de datos
            final Token token = tokenRepository.findByToken(jwtToken).orElse(null);
            if (token == null || token.isExpired() || token.isRevoked()) {
                chain.doFilter(request, response);
                return;
            }

            // Cargar los detalles del usuario
            final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            final Optional<Usuario> usuario = usuarioRepository.findByUsername(username);

            if (usuario.isEmpty() || !jwtService.isTokenValid(jwtToken, usuario.get())) {
                chain.doFilter(request, response);
                return;
            }

            // Configurar el contexto de seguridad
            var authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        } catch (Exception e) {
            // Manejar cualquier excepción y continuar con el filtro
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        chain.doFilter(request, response);
    }
}
