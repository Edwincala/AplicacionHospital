package com.hospital.proyectoHospital.services;

import com.hospital.proyectoHospital.controllers.PacientesController;
import com.hospital.proyectoHospital.models.HistoriaClinica;
import com.hospital.proyectoHospital.models.Paciente;
import com.hospital.proyectoHospital.repositories.PacientesRepository;
import com.hospital.proyectoHospital.security.PasswordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PacienteService {

    @Autowired
    private PacientesRepository pacienteRepository;

    @Autowired
    private PasswordUtils passwordUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final Logger log = LoggerFactory.getLogger(PacientesController.class);

    public boolean createOrUpdatePaciente(Paciente paciente) {
        try {
            if (paciente.getId() == null) {
                return createPaciente(paciente);
            } else {
                return updatePaciente(paciente);
            }
        } catch (Exception e) {
            log.error("Error al procesar el paciente: {}", e.getMessage(), e);
            return false;
        }
    }

    private boolean createPaciente(Paciente paciente) {
        log.info("Creando un nuevo paciente: {}", paciente);

        if (!passwordUtils.isPasswordStrong(paciente.getPassword())) {
            log.warn("Contraseña débil para el paciente con username: {}", paciente.getUsername());
            return false;
        }

        paciente.setPassword(passwordEncoder.encode(paciente.getPassword()));

        if (paciente.getHistoriaClinica() == null) {
            HistoriaClinica nuevaHistoriaClinica = new HistoriaClinica();
            nuevaHistoriaClinica.setDetalles("Detalles iniciales de la historia clínica");
            nuevaHistoriaClinica.setFechaCreacion(new Date());
            nuevaHistoriaClinica.setPaciente(paciente);
            paciente.setHistoriaClinica(nuevaHistoriaClinica);
        }

        pacienteRepository.save(paciente);
        log.info("Paciente creado exitosamente: {}", paciente);
        return true;
    }

    private boolean updatePaciente(Paciente paciente) {
        log.info("Actualizando paciente con ID: {}", paciente.getId());

        Optional<Paciente> pacienteExistente = pacienteRepository.findById(paciente.getId());
        if (pacienteExistente.isEmpty()) {
            log.warn("No se encontró un paciente con ID: {}", paciente.getId());
            return false;
        }

        Paciente pacienteActualizado = pacienteExistente.get();


        pacienteActualizado.setNombre(paciente.getNombre());
        pacienteActualizado.setApellido(paciente.getApellido());
        pacienteActualizado.setDireccion(paciente.getDireccion());
        pacienteActualizado.setTelefono(paciente.getTelefono());

        if (paciente.getPassword() != null && !paciente.getPassword().isEmpty()) {
            if (!passwordUtils.isPasswordStrong(paciente.getPassword())) {
                log.warn("Contraseña débil para el paciente con username: {}", paciente.getUsername());
                return false;
            }
            pacienteActualizado.setPassword(passwordEncoder.encode(paciente.getPassword()));
        }

        if (paciente.getHistoriaClinica() != null) {
            pacienteActualizado.setHistoriaClinica(paciente.getHistoriaClinica());
        } else if (pacienteActualizado.getHistoriaClinica() == null) {
            HistoriaClinica nuevaHistoriaClinica = new HistoriaClinica();
            nuevaHistoriaClinica.setDetalles("Detalles iniciales de la historia clínica");
            nuevaHistoriaClinica.setFechaCreacion(new Date());
            nuevaHistoriaClinica.setPaciente(pacienteActualizado);
            pacienteActualizado.setHistoriaClinica(nuevaHistoriaClinica);
        }

        pacienteRepository.save(pacienteActualizado);
        log.info("Paciente actualizado exitosamente: {}", pacienteActualizado);
        return true;
    }

    public List<Paciente> findAllPacientes() {
        return pacienteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Paciente> findById(UUID id) {
        return pacienteRepository.findById(id);
    }

    public void deletePaciente(UUID id) {
        if (pacienteRepository.existsById(id)) {
            pacienteRepository.deleteById(id);
            log.info("Paciente con ID {} eliminado exitosamente.", id);
        } else {
            log.warn("No se encontró un paciente con ID: {}", id);
        }
    }
}
