package com.hospital.proyectoHospital.services;

import com.hospital.proyectoHospital.models.*;
import com.hospital.proyectoHospital.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private UsuarioRepository usuarioRepository;

    private static final Logger log = LoggerFactory.getLogger(CitaService.class);


    public boolean agendarCita(UUID usuarioId, UUID pacienteId, UUID doctorId, LocalDateTime fechaHora) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
        if (usuario == null || (!esAdmin(usuario) && !usuario.getId().equals(pacienteId))) {
            return false; // Validar si el usuario es administrativo o el paciente
        }

        Optional<Paciente> pacienteOpt = pacientesRepository.findById(pacienteId);
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);

        if (pacienteOpt.isEmpty() || doctorOpt.isEmpty()) {
            return false; // Validar que el paciente y el doctor existan
        }

        Optional<HorarioDoctor> horarioOpt = horarioDoctorRepository
                .findFirstByDoctorAndInicioBeforeAndFinAfterAndDisponibleTrue(doctorOpt.get(), fechaHora, fechaHora);

        if (horarioOpt.isPresent()) {
            // Crear y guardar la cita
            Cita cita = new Cita();
            cita.setPaciente(pacienteOpt.get());
            cita.setDoctor(doctorOpt.get());
            cita.setFechaHora(fechaHora);
            cita.setEstado(Cita.EstadoCita.PENDIENTE);
            citaRepository.save(cita);

            // Actualizar disponibilidad del horario
            HorarioDoctor horario = horarioOpt.get();
            horario.setDisponible(false);
            horarioDoctorRepository.save(horario);

            return true;
        }

        return false;
    }

    public List<Cita> obtenerCitasPorDoctor(Doctor doctor) {
        return citaRepository.findByDoctorOrderByFechaHoraDesc(doctor);
    }

    public Optional<Cita> obtenerCitaPorId(UUID id) {
        return citaRepository.findById(id);
    }

    public Cita completarCita(Cita cita, String observaciones) {
        if (!cita.getEstado().equals(Cita.EstadoCita.CONFIRMADA)) {
            throw new IllegalStateException("Solo se pueden completar citas confirmadas");
        }

        cita.setEstado(Cita.EstadoCita.COMPLETADA);
        cita.setObservaciones(observaciones);
        cita.setFechaCompletada(LocalDateTime.now());

        return citaRepository.save(cita);
    }

    public List<Cita> obtenerCitasPorPaciente(UUID pacienteId) {
        return citaRepository.findByPaciente(new Paciente(pacienteId));
    }

    public boolean cancelarCita(UUID citaId) {
        Optional<Cita> citaOpt = citaRepository.findById(citaId);
        if (citaOpt.isEmpty()) {
            return false; // Validar que la cita exista
        }

        Cita cita = citaOpt.get();
        cita.setEstado(Cita.EstadoCita.CANCELADA);
        citaRepository.save(cita);

        Optional<HorarioDoctor> horarioOpt = horarioDoctorRepository
                .findFirstByDoctorAndInicioBeforeAndFinAfter(cita.getDoctor(), cita.getFechaHora(), cita.getFechaHora());

        if (horarioOpt.isPresent()) {
            HorarioDoctor horario = horarioOpt.get();
            horario.setDisponible(true);
            horarioDoctorRepository.save(horario);
        }

        return true;
    }

    private boolean esAdmin(Usuario usuario) {
        return usuario.getRol() == Usuario.Rol.ADMINISTRATIVO;
    }

}
