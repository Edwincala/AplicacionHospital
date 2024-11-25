package com.hospital.proyectoHospital.services;

import com.hospital.proyectoHospital.controllers.PacientesController;
import com.hospital.proyectoHospital.models.Doctor;
import com.hospital.proyectoHospital.models.Empleado;
import com.hospital.proyectoHospital.repositories.DoctorRepository;
import com.hospital.proyectoHospital.security.PasswordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmpleadoService empleadoService;

    @Autowired
    private PasswordUtils passwordUtils;

    private static final Logger log = LoggerFactory.getLogger(PacientesController.class);

    public List<Doctor> findAllDoctors(){
        return doctorRepository.findAll();
    }

    public Optional<Doctor> findDoctorById(UUID id) {
        return doctorRepository.findById(id);
    }

    public List<Doctor> findDoctorByEspecialidad(String especialidadFragment) {
        return doctorRepository.findByEspecialidadContainingIgnoreCase(especialidadFragment);
    }

    public List<Doctor> findDoctorByNombre(String nombreFragment) {
        return doctorRepository.findByNombreContainingIgnoreCase(nombreFragment);
    }

    public boolean deleteDoctor(UUID doctorId) {
        try {
            Optional<Doctor> doctorExistente = doctorRepository.findById(doctorId);

            if (doctorExistente.isPresent()) {
                doctorRepository.delete(doctorExistente.get());
                log.info("Doctor eliminado con éxito: {}", doctorId);
                return true;
            } else {
                log.warn("No se encontró un doctor con ID: {}", doctorId);
                return false;
            }
        } catch (Exception e) {
            log.error("Error al eliminar el doctor: {}", e.getMessage(), e);
            return false;
        }
    }

    public boolean createOrUpdateDoctor(UUID doctorId, String contrasena, String nombre, String apellido, String username, String especialidad) {
        try {
            if (!isValidUsername(username)) {
                log.warn("El nombre de usuario no tiene un formato válido: {}", username);
                return false;
            }

            if (usernameExists(username, doctorId)) {
                log.warn("Ya existe un doctor con el nombre de usuario proporcionado: {}", username);
                return false;
            }

            Doctor doctor;

            if (doctorId != null) {
                doctor = doctorRepository.findById(doctorId)
                        .orElseThrow(() -> new IllegalArgumentException("No se encontró un doctor con el ID proporcionado: " + doctorId));
            } else {
                doctor = new Doctor();
            }


            doctor.setNombre(nombre);
            doctor.setApellido(apellido);
            doctor.setUsername(username);
            doctor.setEspecialidad(especialidad);

            if (contrasena != null && !contrasena.isEmpty()) {
                doctor.setPassword(passwordEncoder.encode(contrasena));
            }

            doctor.setRol(Empleado.Rol.DOCTOR);

            doctorRepository.save(doctor);
            log.info("Doctor {} con éxito: {}", doctorId == null ? "creado" : "actualizado", doctor.getUsername());
            return true;
        } catch (Exception e) {
            log.error("Error al crear o actualizar el doctor: {}", e.getMessage(), e);
            return false;
        }
    }

    private boolean isValidUsername(String username) {
        String usernameRegex = "^[a-zA-Z0-9._-]{3,20}$";
        Pattern pattern = Pattern.compile(usernameRegex);
        return pattern.matcher(username).matches();
    }

    private boolean usernameExists(String username, UUID doctorId) {
        return doctorRepository.findByNombreContainingIgnoreCase(username)
                .stream()
                .anyMatch(existingDoctor -> !existingDoctor.getId().equals(doctorId));
    }
}
