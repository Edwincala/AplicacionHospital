package com.hospital.proyectoHospital.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.proyectoHospital.controllers.PacientesController;
import com.hospital.proyectoHospital.controllers.TokenResponse;
import com.hospital.proyectoHospital.models.Token;
import com.hospital.proyectoHospital.models.Usuario;
import com.hospital.proyectoHospital.repositories.TokenRepository;
import com.hospital.proyectoHospital.services.AuthService;
import com.hospital.proyectoHospital.services.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import java.util.Map;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final TokenRepository tokenRepository;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final AuthService authService;

    private static final Logger log = LoggerFactory.getLogger(PacientesController.class);

    public JwtRequestFilter(JwtService jwtService, TokenRepository tokenRepository, @Lazy UserDetailsService userDetailsService, @Lazy AuthenticationManager authenticationManager, @Lazy AuthService authService) {
        this.jwtService = jwtService;
        this.tokenRepository = tokenRepository;
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
        this.authService = authService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain) throws ServletException, IOException {
        if (shouldNotFilter(request)) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String refreshToken = request.getHeader("Refresh-Token");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        try {
            String jwt = authHeader.substring(7);
            String username = jwtService.extractUsername(jwt);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                Token token = tokenRepository.findByToken(jwt).orElse(null);

                if (token != null && !token.isExpired() && !token.isRevoked() &&
                        jwtService.isTokenValid(jwt, (Usuario) userDetails)) {
                    setAuthentication(userDetails, request);
                } else if (refreshToken != null) {
                    handleTokenRefresh(refreshToken, response);
                }
            }
        } catch (ExpiredJwtException e) {
            if (refreshToken != null) {
                handleTokenRefresh(refreshToken, response);
            } else {
                sendErrorResponse(response, "Token expired");
            }
            return;
        } catch (Exception e) {
            sendErrorResponse(response, "Invalid token");
            return;
        }

        chain.doFilter(request, response);
    }

    private void handleTokenRefresh(String refreshToken, HttpServletResponse response) throws IOException {
        try {
            TokenResponse newTokens = authService.refreshToken(refreshToken);
            response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + newTokens.accessToken());
            response.setHeader("Refresh-Token", newTokens.refreshToken());
            log.info("Tokens renovados y enviados en la respuesta.");
        } catch (Exception e) {
            log.error("Error al manejar el refresh token: {}", e.getMessage());
            sendErrorResponse(response, "Invalid refresh token. Please reauthenticate.");
        }
    }

    private void setAuthentication(UserDetails userDetails, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(new ObjectMapper().writeValueAsString(
                Map.of("error", message)
        ));
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return "OPTIONS".equalsIgnoreCase(request.getMethod());
    }
}