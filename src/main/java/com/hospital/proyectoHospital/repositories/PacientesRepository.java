package com.hospital.proyectoHospital.repositories;

import com.hospital.proyectoHospital.models.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PacientesRepository extends JpaRepository<Paciente, UUID> {
    Optional<Paciente> findByUsername(String username);
}
