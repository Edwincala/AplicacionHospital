package com.hospital.proyectoHospital.repositories;

import com.hospital.proyectoHospital.models.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TokenRepository extends JpaRepository<Token, UUID> {
    List<Token> findByUsuarioIdAndExpiredIsFalseAndRevokedIsFalse(UUID userId);
    Optional<Token> findByToken(String token);
    void deleteAllByUsuarioId(UUID userId);

    @Modifying
    @Query("UPDATE Token t SET t.expired = true, t.revoked = true WHERE t.usuario.id = :userId")
    void revokeAllUserTokens(@Param("userId") UUID userId);

    void deleteByToken(String token);
}
