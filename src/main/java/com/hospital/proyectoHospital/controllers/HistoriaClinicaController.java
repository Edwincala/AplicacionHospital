package com.hospital.proyectoHospital.controllers;

import com.hospital.proyectoHospital.models.Empleado;
import com.hospital.proyectoHospital.models.HistoriaClinica;
import com.hospital.proyectoHospital.models.Usuario;
import com.hospital.proyectoHospital.repositories.EmpleadoRepository;
import com.hospital.proyectoHospital.repositories.UsuarioRepository;
import com.hospital.proyectoHospital.services.HistoriaClinicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/historiasClinicas")
public class HistoriaClinicaController {
    @Autowired
    private HistoriaClinicaService historiaClinicaService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping
    public ResponseEntity<HistoriaClinica> createHistoriaClinica(@RequestBody HistoriaClinica historiaClinica) {
        try {
            HistoriaClinica savedHistoriaClinica = historiaClinicaService.saveOrUpdateHistoriaClinica(historiaClinica);
            return new ResponseEntity<>(savedHistoriaClinica, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/historia/{pacienteId}")
    public ResponseEntity<?> obtenerHistoriaClinica(@PathVariable UUID pacienteId, Principal principal) {
        UUID usuarioId = UUID.fromString(principal.getName());
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        if (usuario.getRol() == Usuario.Rol.PACIENTE && !usuarioId.equals(pacienteId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado.");
        }

        if (usuario.getRol() == Usuario.Rol.ADMINISTRATIVO) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado para administrativos.");
        }

        Optional<HistoriaClinica> historiaClinica = historiaClinicaService.findHistoriaClinica(usuarioId, usuario.getRol(), pacienteId);
        return historiaClinica.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }


    @GetMapping
    public ResponseEntity<List<HistoriaClinica>> getAllHistoriasClinicas() {
        try {
            List<HistoriaClinica> historiasClinicas = historiaClinicaService.findAllHistoriasClinicas();
            if (historiasClinicas.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(historiasClinicas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<HistoriaClinica> updateHistoriaClinica(@PathVariable Long id, @RequestBody HistoriaClinica historiaClinica) {
        try {
            historiaClinica.setId(id);
            HistoriaClinica updatedHistoriaClinica = historiaClinicaService.saveOrUpdateHistoriaClinica(historiaClinica);
            return new ResponseEntity<>(updatedHistoriaClinica, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteHistoriaClinica(@PathVariable Long id) {
        try {
            historiaClinicaService.deleteHistoriaClinica(id);
            return ResponseEntity.ok("Historia clínica eliminada exitosamente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar la historia clínica.");
        }
    }
}
