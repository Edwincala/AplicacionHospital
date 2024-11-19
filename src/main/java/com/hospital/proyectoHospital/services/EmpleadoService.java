package com.hospital.proyectoHospital.services;

import com.hospital.proyectoHospital.controllers.PacientesController;
import com.hospital.proyectoHospital.models.Doctor;
import com.hospital.proyectoHospital.models.Empleado;
import com.hospital.proyectoHospital.repositories.EmpleadoRepository;
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
public class EmpleadoService {
    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private PasswordUtils passwordUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final Logger log = LoggerFactory.getLogger(PacientesController.class);

    public List<Empleado> findAllEmpleados() {
        return empleadoRepository.findAll();
    }

    public Optional<Empleado> findEmpleadoById(UUID id) {
        return empleadoRepository.findById(id);
    }

    public boolean createOrUpdateEmpleado(UUID empleadoId, String contrasena, String nombre, String apellido, String email, Empleado.Rol rol) {
        try {
            Empleado empleado;

            if (empleadoId != null) {
                Optional<Empleado> empleadoExistente = empleadoRepository.findById(empleadoId);
                if (empleadoExistente.isPresent()) {
                    empleado = empleadoExistente.get();
                } else {
                    throw new IllegalArgumentException("No se encontró un empleado con el ID proporcionado.");
                }
            } else {
                empleado = new Empleado();
            }

            if (contrasena != null && !contrasena.isEmpty()) {
                if (!passwordUtils.isPasswordStrong(contrasena)) {
                    log.warn("Contraseña débil para el empleado con email: {}", email);
                    return false;
                }
                empleado.setContrasena(passwordEncoder.encode(contrasena));
            }

            empleado.setNombre(nombre);
            empleado.setApellido(apellido);
            empleado.setEmail(email);
            empleado.setRol(rol);

            empleadoRepository.save(empleado);
            log.info("Empleado {} guardado exitosamente.", empleado.getNombre());
            return true;
        } catch (Exception e) {
            log.error("Error al crear o actualizar empleado: {}", e.getMessage());
            return false;
        }
    }

    public boolean deleteEmpleado(UUID empleadoId) {
        try {
            Optional<Empleado> empleadoExistente = empleadoRepository.findById(empleadoId);

            if (empleadoExistente.isPresent()) {
                Empleado empleado = empleadoExistente.get();
                empleadoRepository.delete(empleado);
                return true;
            } else {
                log.warn("No se encontró un empleado con ID: {}", empleadoId);
                return false;
            }
        } catch (Exception e) {
            log.error("Error al eliminar el empleado: {}", e.getMessage(), e);
            return false;
        }
    }

    public List<Empleado> findEmpleadoByRol(Empleado.Rol rol) {
        return empleadoRepository.findByRol(rol);
    }

    public List<Empleado> findEmpleadoByNombre(String nombre) {
        return empleadoRepository.findByNombreContainingIgnoreCase(nombre);
    }
}
