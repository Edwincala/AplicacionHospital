package com.hospital.proyectoHospital.controllers;

import com.hospital.proyectoHospital.models.Doctor;
import com.hospital.proyectoHospital.models.Usuario;
import com.hospital.proyectoHospital.repositories.DoctorRepository;
import com.hospital.proyectoHospital.repositories.UsuarioRepository;
import com.hospital.proyectoHospital.services.DoctorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.print.Doc;
import java.util.*;

@RestController
@RequestMapping("/doctores")
public class DoctorController {

    @Autowired
    private final DoctorService doctorService;

    @Autowired
    private final DoctorRepository doctorRepository;

    @Autowired
    private final UsuarioRepository usuarioRepository;

    private static final Logger log = LoggerFactory.getLogger(PacientesController.class);

    public DoctorController(DoctorService doctorService, DoctorRepository doctorRepository, UsuarioRepository usuarioRepository) {
        this.doctorService = doctorService;
        this.doctorRepository = doctorRepository;
        this.usuarioRepository = usuarioRepository;
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

    @GetMapping("/perfil/{username}")
    public ResponseEntity<Map<String, Object>> obtenerPerfilDoctor(@PathVariable String username) {
        try {
            Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Usuario usuario = usuarioOpt.get();
            Optional<Doctor> doctorOpt = doctorRepository.findByUsername(usuario);
            if (doctorOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Doctor doctor = doctorOpt.get();
            Map<String, Object> response = new HashMap<>();
            response.put("id", doctor.getId());
            response.put("nombre", doctor.getNombre());
            response.put("apellido", doctor.getApellido());
            response.put("especialidad", doctor.getEspecialidad());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al obtener perfil del doctor {}: {}", username, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }

    }
}
