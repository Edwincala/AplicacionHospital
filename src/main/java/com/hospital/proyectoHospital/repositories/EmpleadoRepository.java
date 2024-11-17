package com.hospital.proyectoHospital.repositories;

import com.hospital.proyectoHospital.models.Empleado;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EmpleadoRepository extends JpaRepository<Empleado, UUID> {
    List<Empleado> findByNombreContaining(String nombreFragment);
    List<Empleado> findByRol(Empleado.Rol rol);
}
