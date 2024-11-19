package com.hospital.proyectoHospital.repositories;

import com.hospital.proyectoHospital.models.HistoriaClinica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface HistoriaClinicaRepository extends JpaRepository<HistoriaClinica, Long> {
    Optional<HistoriaClinica> findByPacienteId(UUID pacienteId);
}
