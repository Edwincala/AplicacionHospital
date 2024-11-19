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

    public boolean createOrUpdateDoctor(UUID doctorId, String contrasena, String nombre, String apellido, String email, String especialidad) {
        try {
            Doctor doctor;

            // Caso de actualización
            if (doctorId != null) {
                Optional<Doctor> doctorExistente = doctorRepository.findById(doctorId);
                if (doctorExistente.isPresent()) {
                    doctor = doctorExistente.get();
                } else {
                    throw new IllegalArgumentException("El ID proporcionado no corresponde a ningún doctor existente.");
                }
            }
            // Caso de creación
            else {
                doctor = new Doctor();
            }

            // Configurar campos comunes y específicos
            doctor.setNombre(nombre);
            doctor.setApellido(apellido);
            doctor.setEmail(email);
            doctor.setEspecialidad(especialidad);

            // Configurar o actualizar la contraseña
            if (contrasena != null && !contrasena.isEmpty()) {
                doctor.setContrasena(passwordEncoder.encode(contrasena));
            }

            // Asegurar el rol como DOCTOR
            doctor.setRol(Empleado.Rol.DOCTOR);

            doctorRepository.save(doctor);
            return true;
        } catch (Exception e) {
            log.error("Error al crear o actualizar el doctor: {}", e.getMessage(), e);
            return false;
        }
    }
}
