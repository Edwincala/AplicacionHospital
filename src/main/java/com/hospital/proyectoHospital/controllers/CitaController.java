package com.hospital.proyectoHospital.controllers;

import com.hospital.proyectoHospital.models.Cita;
import com.hospital.proyectoHospital.services.CitaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/citas")
public class CitaController {

    @Autowired
    private final CitaService citaService;

    private static final Logger log = LoggerFactory.getLogger(PacientesController.class);

    public CitaController(CitaService citaService) {
        this.citaService = citaService;
    }

    @PostMapping("/agendar")
    public ResponseEntity<String> agendarCita(
            @RequestParam UUID usuarioId,
            @RequestParam UUID pacienteId,
            @RequestParam UUID doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHora) {
        try {
            boolean resultado = citaService.agendarCita(usuarioId, pacienteId, doctorId, fechaHora);

            if (resultado) {
                return ResponseEntity.ok("Cita agendada exitosamente.");
            } else {
                return ResponseEntity.badRequest().body("No se pudo agendar la cita. Verifica los datos ingresados o la disponibilidad del doctor.");
            }
        } catch (Exception e) {
            log.error("Error al agendar la cita: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocurrió un error interno. Inténtalo nuevamente.");
        }
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<Cita>> obtenerCitasPorPaciente(@PathVariable UUID pacienteId) {
        try {
            List<Cita> citas = citaService.obtenerCitasPorPaciente(pacienteId);
            if (citas.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            return ResponseEntity.ok(citas);
        } catch (Exception e) {
            log.error("Error al obtener las citas para el paciente {}: {}", pacienteId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/cancelar/{citaId}")
    public ResponseEntity<String> cancelarCita(@PathVariable UUID citaId) {
        try {
            boolean result = citaService.cancelarCita(citaId);
            if (result) {
                return ResponseEntity.ok("Cita cancelada exitosamente.");
            } else {
                return ResponseEntity.badRequest().body("No se pudo cancelar la cita. Verifica si existe o su estado actual.");
            }
        } catch (Exception e) {
            log.error("Error al cancelar la cita: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocurrió un error interno. Inténtalo nuevamente.");
        }
    }
}
