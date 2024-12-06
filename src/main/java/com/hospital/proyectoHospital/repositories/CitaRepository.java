package com.hospital.proyectoHospital.repositories;

import com.hospital.proyectoHospital.models.Cita;
import com.hospital.proyectoHospital.models.Doctor;
import com.hospital.proyectoHospital.models.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface CitaRepository extends JpaRepository<Cita, UUID> {
    List<Cita> findByPaciente(Paciente paciente);
    List<Cita> findByDoctorAndFechaHoraBetween(Doctor doctor, LocalDateTime inicio, LocalDateTime fin);
    List<Cita> findByDoctorOrderByFechaHoraDesc(Doctor doctor);
}
