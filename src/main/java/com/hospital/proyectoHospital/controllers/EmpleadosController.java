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
    private EmpleadoService empleadoService;

    private static final Logger log = LoggerFactory.getLogger(PacientesController.class);

    @PostMapping
    public ResponseEntity<String> createOrUpdateEmpleado(@RequestBody Empleado empleado) {
        try {
            boolean result = empleadoService.createOrUpdateEmpleado(empleado.getId(),
                    empleado.getContrasena(),
                    empleado.getNombre(),
                    empleado.getApellido(),
                    empleado.getEmail(),
                    empleado.getRol());

            if (result) {
                return ResponseEntity.ok("Empleado o actualizado exitosamente.");
            } else {
                return ResponseEntity.badRequest().body("La contraseña no cumple con los requisitos.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar el empleado.");
        }
    }

    @GetMapping
    public List<Empleado> getAllEmpleados() {
        return empleadoService.findAllEmpleados();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Empleado> getEmpleadoById(@PathVariable UUID id) {
        return empleadoService.findEmpleadoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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
            log.error("Error al eliminar el empleado: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor.");
        }
    }

    @GetMapping("/filtrar/nombre")
    public ResponseEntity<List<Empleado>> filtrarPorNombre(@RequestParam String nombre) {
        List<Empleado> empleados = empleadoService.filtrarPorNombre(nombre);
        return empleados.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(empleados);
    }

    @GetMapping("/filtrar/rol")
    public ResponseEntity<List<Empleado>> filtrarPorRol(@RequestParam Empleado.Rol rol) {
        List<Empleado> empleados = empleadoService.filtrarPorRol(rol);
        return empleados.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(empleados);
    }
}
