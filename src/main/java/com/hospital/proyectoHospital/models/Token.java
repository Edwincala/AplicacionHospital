package com.hospital.proyectoHospital.models;

import jakarta.persistence.*;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "tokens")
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, unique = true)
    private String token;

    @Enumerated(EnumType.STRING)
    private TokenType tokenType;

    private boolean expired;
    private boolean revoked;

    public enum TokenType {
        BEARER
    }

    // Constructor vacío
    public Token() {}

    // Constructor privado para el builder
    private Token(TokenBuilder builder) {
        this.id = builder.id;
        this.usuario = builder.usuario;
        this.token = builder.token;
        this.tokenType = builder.tokenType;
        this.expired = builder.expired;
        this.revoked = builder.revoked;
    }

    // Getters y setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public void setTokenType(TokenType tokenType) {
        this.tokenType = tokenType;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }

    // Implementación del Builder manual
    public static class TokenBuilder {
        private UUID id;
        private Usuario usuario;
        private String token;
        private TokenType tokenType;
        private boolean expired;
        private boolean revoked;

        public TokenBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public TokenBuilder usuario(Usuario usuario) {
            this.usuario = usuario;
            return this;
        }

        public TokenBuilder token(String token) {
            this.token = token;
            return this;
        }

        public TokenBuilder tokenType(TokenType tokenType) {
            this.tokenType = tokenType;
            return this;
        }

        public TokenBuilder expired(boolean expired) {
            this.expired = expired;
            return this;
        }

        public TokenBuilder revoked(boolean revoked) {
            this.revoked = revoked;
            return this;
        }

        public Token build() {
            return new Token(this);
        }
    }

    // Sobreescritura de equals y hashCode (opcional)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token1 = (Token) o;
        return Objects.equals(id, token1.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
