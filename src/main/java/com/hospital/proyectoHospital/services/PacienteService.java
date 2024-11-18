package com.hospital.proyectoHospital.services;

import com.hospital.proyectoHospital.controllers.PacientesController;
import com.hospital.proyectoHospital.models.HistoriaClinica;
import com.hospital.proyectoHospital.models.Paciente;
import com.hospital.proyectoHospital.models.TipoDocumento;
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
        log.info("Iniciando createOrUpdatePaciente para paciente: {}", paciente);

        if ((paciente.getId() == null || paciente.getContrasena() != null)
                && !passwordUtils.isPasswordStrong(paciente.getContrasena())) {
            log.warn("Contraseña débil para el paciente con email: {}", paciente.getEmail());
            return false;
        }

        try {
            if (paciente.getId() != null) {
                Optional<Paciente> pacienteExistente = pacienteRepository.findById(paciente.getId());

                if (pacienteExistente.isPresent()) {
                    Paciente pacienteActualizado = pacienteExistente.get();

                    // Mantener o actualizar la historia clínica
                    if (paciente.getHistoriaClinica() != null) {
                        pacienteActualizado.setHistoriaClinica(paciente.getHistoriaClinica());
                    } else if (pacienteActualizado.getHistoriaClinica() == null) {
                        HistoriaClinica nuevaHistoriaClinica = new HistoriaClinica();
                        nuevaHistoriaClinica.setDetalles("Detalles iniciales de la historia clínica");
                        nuevaHistoriaClinica.setFechaCreacion(new Date());
                        nuevaHistoriaClinica.setPaciente(pacienteActualizado);
                        pacienteActualizado.setHistoriaClinica(nuevaHistoriaClinica);
                    }

                    // Guardar cambios
                    pacienteRepository.save(pacienteActualizado);
                    log.info("Paciente actualizado exitosamente");
                    return true;
                } else {
                    log.warn("No se encontró un paciente con ID: {}", paciente.getId());
                    return false;
                }
            }
            else {
                paciente.setContrasena(passwordEncoder.encode(paciente.getContrasena()));
                pacienteRepository.save(paciente);
                if (paciente.getHistoriaClinica() == null) {
                    HistoriaClinica nuevaHistoriaClinica = new HistoriaClinica();
                    nuevaHistoriaClinica.setDetalles("Detalles iniciales de la historia clínica");
                    nuevaHistoriaClinica.setFechaCreacion(new Date());
                    nuevaHistoriaClinica.setPaciente(paciente);
                    paciente.setHistoriaClinica(nuevaHistoriaClinica);
                }
                log.info("Nuevo paciente creado exitosamente");
                return true;
            }
        } catch (Exception e) {
            log.error("Error al procesar el paciente: {}", e.getMessage());
            return false;
        }
    }

    public List<Paciente> findAllPacientes() {
        return pacienteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Paciente> findById(UUID id) {
        return pacienteRepository.findById(id);
    }

    public void deletePaciente(UUID id) {
        pacienteRepository.deleteById(id);
    }

    public List<Paciente> findPacientesByNombre(String nombre) {
        return pacienteRepository.findByNombreContaining(nombre);
    }

    public List<Paciente> findPacientesByApellido(String apellidoFragment) {
        return pacienteRepository.findByApellidoContaining(apellidoFragment);
    }

    public List<Paciente> findPacientesByDocumentoIdentidad(String documentoPrefix) {
        return pacienteRepository.findByDocumentoIdentidadStartingWith(documentoPrefix);
    }
}
