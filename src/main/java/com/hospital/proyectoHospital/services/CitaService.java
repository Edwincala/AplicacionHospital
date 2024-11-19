package com.hospital.proyectoHospital.services;

import com.hospital.proyectoHospital.models.*;
import com.hospital.proyectoHospital.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CitaService {

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private HorarioDoctorRepository horarioDoctorRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PacientesRepository pacientesRepository;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    public boolean agendarCita(UUID usuarioId, UUID pacienteId, UUID doctorId, LocalDateTime fechaHora) {
        // Validar si el usuario es administrativo o el mismo paciente
        Empleado empleado = empleadoRepository.findById(usuarioId).orElse(null);
        if ((empleado != null && empleado.getRol() == Empleado.Rol.ADMINISTRATIVO) || usuarioId.equals(pacienteId)) {

            // Verificar que el paciente existe
            Optional<Paciente> paciente = pacientesRepository.findById(pacienteId);
            if (paciente.isPresent()) {

                // Verificar que el doctor existe
                Optional<Doctor> doctor = doctorRepository.findById(doctorId);
                if (doctor.isPresent()) {

                    // Verificar que hay disponibilidad en el horario del doctor
                    Optional<HorarioDoctor> horarioDisponible = horarioDoctorRepository.findByDoctorAndInicioBeforeAndFinAfterAndDisponibleTrue(doctor.get(), fechaHora);

                    if (horarioDisponible.isPresent()) {
                        // Crear la cita
                        Cita cita = new Cita();
                        cita.setPaciente(paciente.get());
                        cita.setDoctor(doctor.get());
                        cita.setFechaHora(fechaHora);
                        cita.setEstado(Cita.EstadoCita.PENDIENTE);

                        // Guardar la cita
                        citaRepository.save(cita);

                        // Actualizar disponibilidad del horario
                        HorarioDoctor horario = horarioDisponible.get();
                        horario.setDisponible(false);
                        horarioDoctorRepository.save(horario);

                        return true;
                    }
                }
            }
        }
        return false;
    }


    public List<Cita> obtenerCitasPorPaciente(UUID pacienteId) {
        return citaRepository.findByPaciente(new Paciente(pacienteId));
    }

    public boolean cancelarCita(UUID citaId) {
        Optional<Cita> cita = citaRepository.findById(citaId);

        if (cita.isPresent()) {
            // Marca la cita como cancelada
            Cita citaExistente = cita.get();
            citaExistente.setEstado(Cita.EstadoCita.CANCELADA);
            citaRepository.save(citaExistente);

            // Libera el horario asociado
            HorarioDoctor horario = horarioDoctorRepository
                    .findByDoctorAndInicioBeforeAndFinAfterAndDisponibleTrue(
                            citaExistente.getDoctor(), citaExistente.getFechaHora())
                    .orElse(null);

            if (horario != null) {
                horario.setDisponible(true);
                horarioDoctorRepository.save(horario);
            }

            return true;
        }
        return false;
    }
}
