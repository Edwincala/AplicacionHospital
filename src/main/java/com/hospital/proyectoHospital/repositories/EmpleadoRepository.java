package com.hospital.proyectoHospital.repositories;

import com.hospital.proyectoHospital.models.Empleado;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, UUID> {
    List<Empleado> findByRol(Empleado.Rol rol);
    Optional<Empleado> findByUsername(String username);
    List<Empleado> findByNombreContainingIgnoreCase(String nombreFragment);
}
