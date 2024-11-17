package com.hospital.proyectoHospital.repositories;

import com.hospital.proyectoHospital.models.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PacientesRepository extends JpaRepository<Paciente, UUID> {
    List<Paciente> findByNombreContaining(String nombreFragment);
    List<Paciente> findByApellidoContaining(String apellidoFragment);
    List<Paciente> findByDocumentoIdentidadStartingWith(String documentoPrefix);
}
