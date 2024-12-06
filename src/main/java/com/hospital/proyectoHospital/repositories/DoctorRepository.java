package com.hospital.proyectoHospital.repositories;

import com.hospital.proyectoHospital.models.Doctor;
import com.hospital.proyectoHospital.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DoctorRepository extends JpaRepository<Doctor, UUID> {
    List<Doctor> findByEspecialidadContainingIgnoreCase(String especialidadFragment);
    List<Doctor> findByNombreContainingIgnoreCase(String nombreFragment);

    Optional<Doctor> findByUsername(Usuario usuario);
}
