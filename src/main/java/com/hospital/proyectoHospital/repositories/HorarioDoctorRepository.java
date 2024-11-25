package com.hospital.proyectoHospital.repositories;

import com.hospital.proyectoHospital.models.Doctor;
import com.hospital.proyectoHospital.models.HorarioDoctor;
import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HorarioDoctorRepository extends JpaRepository<HorarioDoctor, UUID> {
    List<HorarioDoctor> findByDoctorAndDisponibleTrue(Doctor doctor);
    Optional<HorarioDoctor> findFirstByDoctorAndInicioBeforeAndFinAfterAndDisponibleTrue(
            Doctor doctor, LocalDateTime inicio, LocalDateTime fin);
    Optional<HorarioDoctor> findFirstByDoctorAndInicioBeforeAndFinAfter(
            Doctor doctor, LocalDateTime inicio, LocalDateTime fin);
}
