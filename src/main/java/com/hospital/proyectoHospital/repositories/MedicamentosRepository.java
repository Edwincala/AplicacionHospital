package com.hospital.proyectoHospital.repositories;

import com.hospital.proyectoHospital.models.Medicamentos;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MedicamentosRepository extends JpaRepository<Medicamentos, UUID> {
    List<Medicamentos> findByNombreContainingIgnoreCase(String nombre);
    List<Medicamentos> findByLaboratorioAndCantidadEnStockLessThan(String laboratorio, int stock);
}
