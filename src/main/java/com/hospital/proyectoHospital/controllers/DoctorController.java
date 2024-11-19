package com.hospital.proyectoHospital.controllers;

import com.hospital.proyectoHospital.models.Doctor;
import com.hospital.proyectoHospital.models.Empleado;
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
    private DoctorService doctorService;

    private static final Logger log = LoggerFactory.getLogger(PacientesController.class);

    @PostMapping
    public ResponseEntity<String> createOrUpdateDoctor(@RequestBody Doctor doctor) {
        try {
            boolean result = doctorService.createOrUpdateDoctor(
                    doctor.getId(),
                    doctor.getContrasena(),
                    doctor.getNombre(),
                    doctor.getApellido(),
                    doctor.getEmail(),
                    doctor.getEspecialidad()
            );

            if (result) {
                String mensaje = (doctor.getId() != null) ? "Doctor actualizado exitosamente." : "Doctor creado exitosamente.";
                return ResponseEntity.ok(mensaje);
            } else {
                return ResponseEntity.badRequest().body("No se pudo crear o actualizar el doctor. Verifique los datos proporcionados.");
            }
        } catch (Exception e) {
            log.error("Error al procesar la solicitud: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor.");
        }
    }

    @GetMapping
    public List<Doctor> getAllDoctors() {
        return doctorService.findAllDoctors();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Doctor> getDoctorById(@PathVariable UUID id) {
        return doctorService.findDoctorById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/especialidad/{especialidad}")
    public List<Doctor> getDoctorsByEspecialidad(@PathVariable String especialidad) {
        return doctorService.findDoctorByEspecialidad(especialidad);
    }

    @GetMapping("/nombre/{nombre}")
    public List<Doctor> getDoctorsByNombre(@PathVariable String nombre) {
        return doctorService.findDoctorByNombre(nombre);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDoctor(@PathVariable UUID id) {
        try {
            boolean result = doctorService.deleteDoctor(id);
            if (result) {
                return ResponseEntity.ok("Doctor eliminado exitosamente.");
            } else {
                return ResponseEntity.badRequest().body("No se pudo eliminar el doctor.");
            }
        } catch (Exception e) {
            log.error("Error al eliminar doctor: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor.");
        }
    }
}
