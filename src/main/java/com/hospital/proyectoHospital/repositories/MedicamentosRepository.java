package com.hospital.proyectoHospital.repositories;

import com.hospital.proyectoHospital.models.Medicamentos;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MedicamentosRepository extends JpaRepository<Medicamentos, UUID> {
    List<Medicamentos> findByPrecioBetween(int precioMin, int precioMax);
    List<Medicamentos> findByLaboratorioOrderByPrecioAsc(String laboratorio);
    List<Medicamentos> findByLaboratorioOrderByPrecioDesc(String laboratorio);
    Page<Medicamentos> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);
    Page<Medicamentos> findByLaboratorioIgnoreCase(String laboratorio, Pageable pageable);
}
