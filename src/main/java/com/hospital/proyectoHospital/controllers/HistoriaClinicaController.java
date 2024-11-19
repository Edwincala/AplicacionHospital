package com.hospital.proyectoHospital.controllers;

import com.hospital.proyectoHospital.models.Empleado;
import com.hospital.proyectoHospital.models.HistoriaClinica;
import com.hospital.proyectoHospital.repositories.EmpleadoRepository;
import com.hospital.proyectoHospital.services.HistoriaClinicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private EmpleadoRepository empleadoRepository;

    @PostMapping
    public ResponseEntity<HistoriaClinica> createHistoriaClinica(@RequestBody HistoriaClinica historiaClinica) {
        HistoriaClinica savedHistoriaClinica = historiaClinicaService.saveOrUpdateHistoriaClinica(historiaClinica);
        return new ResponseEntity<>(savedHistoriaClinica, HttpStatus.CREATED);
    }

    @GetMapping("/historia/{pacienteId}")
    public ResponseEntity<?> obtenerHistoriaClinica(@PathVariable UUID pacienteId, Principal principal) {
        UUID usuarioId = UUID.fromString(principal.getName()); // Supongamos que el ID del usuario está en `principal`
        Empleado empleado = empleadoRepository.findById(usuarioId).orElse(null);

        Empleado.Rol rol = (empleado != null) ? empleado.getRol() : null;
        Optional<HistoriaClinica> historiaClinica = historiaClinicaService.findHistoriaClinica(usuarioId, rol, pacienteId);

        if (historiaClinica.isPresent()) {
            return ResponseEntity.ok(historiaClinica.get());
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso no permitido.");
        }
    }

    @GetMapping
    public List<HistoriaClinica> getAllHistoriasClinicas() {
        return historiaClinicaService.findAllHistoriasClinicas();
    }

    @PutMapping("/{id}")
    public ResponseEntity<HistoriaClinica> updateHistoriaClinica(@PathVariable Long id, @RequestBody HistoriaClinica historiaClinica) {
        historiaClinica.setId(id);
        HistoriaClinica updatedHistoriaClinica = historiaClinicaService.saveOrUpdateHistoriaClinica(historiaClinica);
        return new ResponseEntity<>(updatedHistoriaClinica, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHistoriaClinica(@PathVariable Long id) {
        historiaClinicaService.deleteHistoriaClinica(id);
        return ResponseEntity.ok().build();
    }

}
