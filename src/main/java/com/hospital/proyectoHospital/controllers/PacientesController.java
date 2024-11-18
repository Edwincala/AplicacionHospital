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
    public ResponseEntity<?> createOrUpdatePaciente(@RequestBody Paciente paciente) {
        log.info("Datos recibidos en la solicitud: {}", paciente);

        try {
            boolean result = pacienteService.createOrUpdatePaciente(paciente);

            if (result) {
                String mensaje = paciente.getId() != null ?
                        "Paciente actualizado con éxito." :
                        "Paciente creado con éxito.";
                return ResponseEntity.ok().body(mensaje);
            } else {
                return ResponseEntity.badRequest()
                        .body("La operación no pudo completarse. Verifica los datos proporcionados.");
            }
        } catch (Exception e) {
            log.error("Error al procesar paciente: ", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor.");
        }
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Paciente> getPacienteById(@PathVariable UUID id) {
        Optional<Paciente> paciente = pacienteService.findById(id);
        if (paciente.isPresent()) {
            return ResponseEntity.ok(paciente.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public List<Paciente> getAllPacientes() {
            return pacienteService.findAllPacientes();
        }

    @DeleteMapping("/id/{id}")
    public ResponseEntity<Void> deletePaciente(@PathVariable UUID id) {
        pacienteService.deletePaciente(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/nombre/{nombre}")
    public List<Paciente> getPacientesByNombre(@PathVariable String nombre) {
        return pacienteService.findPacientesByNombre(nombre);
    }

    @GetMapping("/apellido/{apellido}")
    public List<Paciente> getPacientesByApellido(@PathVariable String apellido) {
        return pacienteService.findPacientesByApellido(apellido);
    }

    @GetMapping("/documento/{documento}")
    public List<Paciente> getPacientesByDocumentoIdentidad(@PathVariable String documento) {
        return pacienteService.findPacientesByDocumentoIdentidad(documento);
    }
}
