package com.hospital.proyectoHospital.repositories;

import com.hospital.proyectoHospital.models.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TokenRepository extends JpaRepository<Token, Long> {
    List<Token> findByUsuarioIdAndExpiredIsFalseAndRevokedIsFalse(UUID id);
    Optional<Token> findByToken(String token);
}
