package com.hospital.proyectoHospital.controllers;

import com.hospital.proyectoHospital.models.Paciente;
import com.hospital.proyectoHospital.services.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/pacientes")
public class PacientesController {

    @Autowired
    private PacienteService pacienteService;

    @PostMapping
    public ResponseEntity<?> createOrUpdatePaciente(@RequestBody Paciente paciente) {
        boolean success = pacienteService.createOrUpdatePaciente(paciente.getId(), paciente.getContrasena(), paciente.getNombre(), paciente.getApellido(), paciente.getEmail(), paciente.getTipoDocumento(), paciente.getDocumentoIdentidad(), paciente.getDireccion(), paciente.getTelefono());
        if (success) {
            return new ResponseEntity<>(HttpStatus.CREATED);
        } else {
            return ResponseEntity.badRequest().body("Failed to create or update paciente. Check the data provided.");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Paciente> getPacienteById(@PathVariable UUID id) {
        return pacienteService.findPacienteById(id)
                .map(paciente -> new ResponseEntity<>(paciente, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public List<Paciente> getAllPacientes() {
        return pacienteService.findAllPacientes();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaciente(@PathVariable UUID id) {
        pacienteService.deletePaciente(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{nombre}")
    public List<Paciente> getPacientesByNombre(@PathVariable String nombre) {
        return pacienteService.findPacientesByNombre(nombre);
    }

    @GetMapping("/{apellido}")
    public List<Paciente> getPacientesByApellido(@PathVariable String apellido) {
        return pacienteService.findPacientesByApellido(apellido);
    }

    @GetMapping("/{documento}")
    public List<Paciente> getPacientesByDocumentoIdentidad(@PathVariable String documento) {
        return pacienteService.findPacientesByDocumentoIdentidad(documento);
    }
}
