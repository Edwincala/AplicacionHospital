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
    private CitaService citaService;

    private static final Logger log = LoggerFactory.getLogger(PacientesController.class);

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
        List<Cita> citas = citaService.obtenerCitasPorPaciente(pacienteId);
        return ResponseEntity.ok(citas);
    }

    @DeleteMapping("/cancelar/{citaId}")
    public ResponseEntity<String> cancelarCita(@PathVariable UUID citaId) {
        boolean result = citaService.cancelarCita(citaId);
        if (result) {
            return ResponseEntity.ok("Cita cancelada exitosamente.");
        } else {
            return ResponseEntity.badRequest().body("No se pudo cancelarla cita");
        }
    }
}
