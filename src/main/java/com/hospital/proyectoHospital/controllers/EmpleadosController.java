package com.hospital.proyectoHospital.controllers;

import com.hospital.proyectoHospital.models.Empleado;
import com.hospital.proyectoHospital.services.EmpleadoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/empleados")
public class EmpleadosController {

    @Autowired
    private final EmpleadoService empleadoService;

    private static final Logger log = LoggerFactory.getLogger(PacientesController.class);

    public EmpleadosController(EmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
    }

    @PostMapping
    public ResponseEntity<String> createOrUpdateEmpleado(@RequestBody Empleado empleado) {
        try {
            boolean result = empleadoService.createOrUpdateEmpleado(
                    empleado.getId(),
                    empleado.getPassword(),
                    empleado.getNombre(),
                    empleado.getApellido(),
                    empleado.getUsername(),
                    empleado.getRol()
            );

            if (result) {
                String mensaje = (empleado.getId() != null)
                        ? "Empleado actualizado exitosamente."
                        : "Empleado creado exitosamente.";
                return ResponseEntity.ok(mensaje);
            } else {
                return ResponseEntity.badRequest().body("La contraseña no cumple con los requisitos.");
            }
        } catch (IllegalArgumentException e) {
            log.warn("Error de validación al procesar el empleado: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error al procesar el empleado: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor.");
        }
    }

    @GetMapping
    public ResponseEntity<List<Empleado>> getAllEmpleados() {
        try {
            List<Empleado> empleados = empleadoService.findAllEmpleados();
            if (empleados.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(empleados);
        } catch (Exception e) {
            log.error("Error al obtener la lista de empleados: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Empleado> getEmpleadoById(@PathVariable UUID id) {
        try {
            return empleadoService.findEmpleadoById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error al buscar el empleado con ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmpleado(@PathVariable UUID id) {
        try {
            boolean result = empleadoService.deleteEmpleado(id);
            if (result) {
                return ResponseEntity.ok("Empleado eliminado exitosamente.");
            } else {
                return ResponseEntity.badRequest().body("No se encontró el empleado con el ID proporcionado.");
            }
        } catch (Exception e) {
            log.error("Error al eliminar el empleado con ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor.");
        }
    }

    @GetMapping("/filtrar/nombre")
    public ResponseEntity<List<Empleado>> filtrarPorNombre(@RequestParam String nombre) {
        try {
            List<Empleado> empleados = empleadoService.filtrarPorNombre(nombre);
            return empleados.isEmpty()
                    ? ResponseEntity.noContent().build()
                    : ResponseEntity.ok(empleados);
        } catch (Exception e) {
            log.error("Error al filtrar empleados por nombre '{}': {}", nombre, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/filtrar/rol")
    public ResponseEntity<List<Empleado>> filtrarPorRol(@RequestParam Empleado.Rol rol) {
        try {
            List<Empleado> empleados = empleadoService.filtrarPorRol(rol);
            return empleados.isEmpty()
                    ? ResponseEntity.noContent().build()
                    : ResponseEntity.ok(empleados);
        } catch (Exception e) {
            log.error("Error al filtrar empleados por rol '{}': {}", rol, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
