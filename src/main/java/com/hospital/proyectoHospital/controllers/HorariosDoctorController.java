package com.hospital.proyectoHospital.controllers;

import com.hospital.proyectoHospital.models.Doctor;
import com.hospital.proyectoHospital.models.HorarioDoctor;
import com.hospital.proyectoHospital.repositories.DoctorRepository;
import com.hospital.proyectoHospital.services.HorarioDoctorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/horarios")
public class HorariosDoctorController {
    private static final Logger log = LoggerFactory.getLogger(HorariosDoctorController.class);

    @Autowired
    private HorarioDoctorService horarioDoctorService;

    @Autowired
    private DoctorRepository doctorRepository;

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<HorarioDoctor>> obtenerHorariosPorDoctor(@PathVariable UUID doctorId) {
        try {
            Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
            if (doctorOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            List<HorarioDoctor> horarios = horarioDoctorService.obtenerHorariosPorDoctor(doctorOpt.get());
            if (horarios.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(horarios);
        } catch (Exception e) {
            log.error("Error al obtener horarios del doctor {}: {}", doctorId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/crear")
    public ResponseEntity<HorarioDoctor> crearHorario(
            @RequestParam UUID doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        try {
            Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
            if (doctorOpt.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            HorarioDoctor nuevoHorario = new HorarioDoctor();
            nuevoHorario.setInicio(inicio);
            nuevoHorario.setFin(fin);

            HorarioDoctor horarioCreado = horarioDoctorService.crearHorario(doctorOpt.get(), nuevoHorario, inicio, fin);
            return ResponseEntity.ok(horarioCreado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error al crear horario para el doctor {}: {}", doctorId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{horarioId}")
    public ResponseEntity<Void> eliminarHorario(@PathVariable UUID horarioId) {
        try {
            boolean resultado = horarioDoctorService.eliminarHorario(horarioId);
            if (resultado) {
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error al eliminar horario {}: {}", horarioId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/disponibles/{doctorId}")
    public ResponseEntity<List<HorarioDoctor>> obtenerHorariosDisponibles(
            @PathVariable UUID doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha) {
        try {
            Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
            if (doctorOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            List<HorarioDoctor> horariosDisponibles = horarioDoctorService
                    .obtenerHorariosDisponibles(doctorOpt.get(), fecha);

            return ResponseEntity.ok(horariosDisponibles);
        } catch (Exception e) {
            log.error("Error al obtener horarios disponibles del doctor {}: {}", doctorId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
