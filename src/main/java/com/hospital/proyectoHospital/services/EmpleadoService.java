package com.hospital.proyectoHospital.services;

import com.hospital.proyectoHospital.controllers.PacientesController;
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
import java.util.regex.Pattern;

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

    public boolean createOrUpdateEmpleado(UUID empleadoId, String contrasena, String nombre, String apellido, String username, Empleado.Rol rol) {
        try {
            if (!isValidUsername(username)) {
                log.warn("El nombre de usuario no tiene un formato válido: {}", username);
                return false;
            }

            if (usernameExists(username, empleadoId)) {
                log.warn("Ya existe un empleado con el nombre de usuario proporcionado: {}", username);
                return false;
            }

            Empleado empleado;

            // Caso de actualización
            if (empleadoId != null) {
                empleado = empleadoRepository.findById(empleadoId)
                        .orElseThrow(() -> new IllegalArgumentException("No se encontró un empleado con el ID proporcionado."));
            } else {
                // Caso de creación
                empleado = new Empleado();
            }

            // Configurar campos comunes
            empleado.setNombre(nombre);
            empleado.setApellido(apellido);
            empleado.setUsername(username);
            empleado.setRol(rol);

            // Configurar o actualizar la contraseña
            if (contrasena != null && !contrasena.isEmpty()) {
                if (!isPasswordStrong(contrasena)) {
                    log.warn("Contraseña débil para el empleado con username: {}", username);
                    return false;
                }
                empleado.setPassword(passwordEncoder.encode(contrasena));
            }

            empleadoRepository.save(empleado);
            log.info("Empleado {} guardado exitosamente.", empleado.getUsername());
            return true;
        } catch (Exception e) {
            log.error("Error al crear o actualizar empleado: {}", e.getMessage(), e);
            return false;
        }
    }

    public boolean deleteEmpleado(UUID empleadoId) {
        try {
            Optional<Empleado> empleadoExistente = empleadoRepository.findById(empleadoId);

            if (empleadoExistente.isPresent()) {
                empleadoRepository.delete(empleadoExistente.get());
                log.info("Empleado eliminado con éxito: {}", empleadoId);
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

    public List<Empleado> filtrarPorNombre(String nombreFragment) {
        return empleadoRepository.findByNombreContainingIgnoreCase(nombreFragment);
    }

    public List<Empleado> filtrarPorRol(Empleado.Rol rol) {
        return empleadoRepository.findByRol(rol);
    }

    private boolean usernameExists(String username, UUID empleadoId) {
        return empleadoRepository.findByNombreContainingIgnoreCase(username)
                .stream()
                .anyMatch(existingEmpleado -> !existingEmpleado.getId().equals(empleadoId));
    }

    private boolean isValidUsername(String username) {
        String usernameRegex = "^[a-zA-Z0-9._-]{3,20}$";
        Pattern pattern = Pattern.compile(usernameRegex);
        return pattern.matcher(username).matches();
    }

    private boolean isPasswordStrong(String password) {
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        Pattern pattern = Pattern.compile(passwordRegex);
        return pattern.matcher(password).matches();
    }

}
