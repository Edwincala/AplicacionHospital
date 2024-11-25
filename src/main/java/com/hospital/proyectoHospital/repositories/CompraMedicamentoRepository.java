package com.hospital.proyectoHospital.repositories;

import com.hospital.proyectoHospital.models.CompraMedicamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CompraMedicamentoRepository extends JpaRepository<CompraMedicamento, UUID> {
}
