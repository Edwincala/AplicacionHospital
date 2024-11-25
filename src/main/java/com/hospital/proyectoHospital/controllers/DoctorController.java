package com.hospital.proyectoHospital.controllers;

import com.hospital.proyectoHospital.models.Doctor;
import com.hospital.proyectoHospital.services.DoctorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/doctores")
public class DoctorController {

    @Autowired
    private final DoctorService doctorService;

    private static final Logger log = LoggerFactory.getLogger(PacientesController.class);

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @PostMapping
    public ResponseEntity<String> createOrUpdateDoctor(@RequestBody Doctor doctor) {
        try {
            boolean result = doctorService.createOrUpdateDoctor(
                    doctor.getId(),
                    doctor.getPassword(),
                    doctor.getNombre(),
                    doctor.getApellido(),
                    doctor.getUsername(),
                    doctor.getEspecialidad()
            );

            if (result) {
                String mensaje = (doctor.getId() != null) ? "Doctor actualizado exitosamente." : "Doctor creado exitosamente.";
                return ResponseEntity.ok(mensaje);
            } else {
                return ResponseEntity.badRequest().body("No se pudo crear o actualizar el doctor. Verifique los datos proporcionados.");
            }
        } catch (IllegalArgumentException e) {
            log.warn("Error de validación al procesar el doctor: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error al procesar la solicitud: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor.");
        }
    }

    @GetMapping
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        try {
            List<Doctor> doctors = doctorService.findAllDoctors();
            if (doctors.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(doctors);
        } catch (Exception e) {
            log.error("Error al obtener la lista de doctores: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Doctor> getDoctorById(@PathVariable UUID id) {
        try {
            return doctorService.findDoctorById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error al buscar el doctor con ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/especialidad/{especialidad}")
    public ResponseEntity<List<Doctor>> getDoctorsByEspecialidad(@PathVariable String especialidad) {
        try {
            List<Doctor> doctors = doctorService.findDoctorByEspecialidad(especialidad);
            if (doctors.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(doctors);
        } catch (Exception e) {
            log.error("Error al buscar doctores por especialidad '{}': {}", especialidad, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<List<Doctor>> getDoctorsByNombre(@PathVariable String nombre) {
        try {
            List<Doctor> doctors = doctorService.findDoctorByNombre(nombre);
            if (doctors.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(doctors);
        } catch (Exception e) {
            log.error("Error al buscar doctores por nombre '{}': {}", nombre, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDoctor(@PathVariable UUID id) {
        try {
            boolean result = doctorService.deleteDoctor(id);
            if (result) {
                return ResponseEntity.ok("Doctor eliminado exitosamente.");
            } else {
                return ResponseEntity.badRequest().body("No se pudo eliminar el doctor. Verifica si existe.");
            }
        } catch (Exception e) {
            log.error("Error al eliminar el doctor con ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor.");
        }
    }
}
