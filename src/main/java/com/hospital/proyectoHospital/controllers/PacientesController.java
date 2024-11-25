package com.hospital.proyectoHospital.controllers;

import com.hospital.proyectoHospital.models.Paciente;
import com.hospital.proyectoHospital.models.TipoDocumento;
import com.hospital.proyectoHospital.services.PacienteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/pacientes")
public class PacientesController {

    @Autowired
    private PacienteService pacienteService;

    private static final Logger log = LoggerFactory.getLogger(PacientesController.class);

    @PostMapping
    public ResponseEntity<String> createOrUpdatePaciente(@RequestBody Paciente paciente) {
        log.info("Solicitud para procesar paciente: {}", paciente);

        try {
            boolean result = pacienteService.createOrUpdatePaciente(paciente);

            if (result) {
                String mensaje = (paciente.getId() != null) ?
                        "Paciente actualizado con éxito." :
                        "Paciente creado con éxito.";
                return ResponseEntity.ok(mensaje);
            } else {
                return ResponseEntity.badRequest().body("No se pudo procesar la solicitud. Verifica los datos proporcionados.");
            }
        } catch (Exception e) {
            log.error("Error al procesar paciente: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor.");
        }
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Paciente> getPacienteById(@PathVariable UUID id) {
        try {
            Optional<Paciente> paciente = pacienteService.findById(id);
            return paciente.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error al obtener paciente por ID: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Paciente>> getAllPacientes() {
        try {
            List<Paciente> pacientes = pacienteService.findAllPacientes();
            return pacientes.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(pacientes);
        } catch (Exception e) {
            log.error("Error al obtener pacientes: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/id/{id}")
    public ResponseEntity<String> deletePaciente(@PathVariable UUID id) {
        try {
            pacienteService.deletePaciente(id);
            return ResponseEntity.ok("Paciente eliminado con éxito.");
        } catch (Exception e) {
            log.error("Error al eliminar paciente: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor.");
        }
    }
}
