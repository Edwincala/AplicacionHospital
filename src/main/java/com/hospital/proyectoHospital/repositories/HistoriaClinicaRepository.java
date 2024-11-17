package com.hospital.proyectoHospital.repositories;

import com.hospital.proyectoHospital.models.HistoriaClinica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoriaClinicaRepository extends JpaRepository<HistoriaClinica, Long> {
    List<HistoriaClinica> findByDetallescontaining(String detallesFragment);
}
