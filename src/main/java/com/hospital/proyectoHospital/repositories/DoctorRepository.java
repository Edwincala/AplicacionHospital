package com.hospital.proyectoHospital.repositories;

import com.hospital.proyectoHospital.models.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DoctorRepository extends JpaRepository<Doctor, UUID> {
    List<Doctor> findByEspecialidadContaining(String especialidadFragment);
    List<Doctor> findByNombreContaining(String nombreFragment);
}