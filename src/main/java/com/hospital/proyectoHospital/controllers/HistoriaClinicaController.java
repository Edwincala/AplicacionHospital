package com.hospital.proyectoHospital.controllers;

import com.hospital.proyectoHospital.models.Empleado;
import com.hospital.proyectoHospital.models.HistoriaClinica;
import com.hospital.proyectoHospital.models.Paciente;
import com.hospital.proyectoHospital.models.Usuario;
import com.hospital.proyectoHospital.repositories.EmpleadoRepository;
import com.hospital.proyectoHospital.repositories.PacientesRepository;
import com.hospital.proyectoHospital.repositories.UsuarioRepository;
import com.hospital.proyectoHospital.services.ConsejoSaludService;
import com.hospital.proyectoHospital.services.HistoriaClinicaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/historiasClinicas")
public class HistoriaClinicaController {
    @Autowired
    private HistoriaClinicaService historiaClinicaService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PacientesRepository pacienteRepository;

    @Autowired
    private ConsejoSaludService consejoSaludService;

    private final static Logger log = LoggerFactory.getLogger(HistoriaClinicaController.class);

    @PostMapping
    public ResponseEntity<HistoriaClinica> crearHistoriaClinica(
            @RequestBody Map<String, String> request,
            Principal principal
    ) {
        try {
            UUID usuarioId = UUID.fromString(principal.getName());
            Usuario usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

            // Solo los doctores pueden crear historias clínicas
            if (usuario.getRol() != Usuario.Rol.DOCTOR) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            UUID pacienteId = UUID.fromString(request.get("pacienteId"));
            Paciente paciente = pacienteRepository.findById(pacienteId)
                    .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

            // Verificar si el paciente ya tiene historia clínica
            if (historiaClinicaService.findHistoriaClinica(pacienteId, usuario.getRol(), pacienteId).isPresent()) {
                return ResponseEntity.badRequest().build();
            }

            HistoriaClinica nuevaHistoria = new HistoriaClinica();
            nuevaHistoria.setPaciente(paciente);
            nuevaHistoria.setDetalles(request.get("detalles"));
            nuevaHistoria.setFechaCreacion(new Date());

            HistoriaClinica historiaGuardada = historiaClinicaService.saveOrUpdateHistoriaClinica(nuevaHistoria);
            return new ResponseEntity<>(historiaGuardada, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error al crear historia clínica: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/historia/{pacienteId}")
    public ResponseEntity<?> obtenerHistoriaClinica(@PathVariable UUID pacienteId, Principal principal) {
        try {
            UUID usuarioId = UUID.fromString(principal.getName());
            Usuario usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

            Optional<HistoriaClinica> historiaClinica = historiaClinicaService.findHistoriaClinica(
                    usuarioId,
                    usuario.getRol(),
                    pacienteId
            );

            return historiaClinica
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error al obtener historia clínica del paciente {}: {}", pacienteId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}/consejo-salud")
    public ResponseEntity<Map<String, String>> obtenerConsejoSalud(
            @PathVariable Long id,
            Principal principal
    ) {
        try {
            UUID usuarioId = UUID.fromString(principal.getName());
            Usuario usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

            Optional<HistoriaClinica> historiaOpt = historiaClinicaService.findById(id, usuarioId, usuario.getRol());
            if (historiaOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            String consejo = consejoSaludService.generarConsejoDeSalud(historiaOpt.get());
            Map<String, String> response = new HashMap<>();
            response.put("consejo", consejo);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al obtener consejo de salud: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }


    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllHistoriasClinicas() {
        try {
            List<HistoriaClinica> historiasClinicas = historiaClinicaService.findAllHistoriasClinicas();
            if (historiasClinicas.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            List<Map<String, Object>> response = historiasClinicas.stream()
                    .map(historia -> {
                        Map<String, Object> historiaMap = new HashMap<>();
                        historiaMap.put("id", historia.getId());
                        historiaMap.put("detalles", historia.getDetalles());
                        historiaMap.put("fechaCreacion", historia.getFechaCreacion());

                        Map<String, Object> pacienteMap = new HashMap<>();
                        pacienteMap.put("id", historia.getPaciente().getId());
                        pacienteMap.put("nombre", historia.getPaciente().getNombre());
                        pacienteMap.put("apellido", historia.getPaciente().getApellido());

                        historiaMap.put("paciente", pacienteMap);
                        return historiaMap;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al obtener historias clínicas", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<HistoriaClinica> actualizarHistoriaClinica(
            @PathVariable Long id,
            @RequestBody Map<String, String> request,
            Principal principal
    ) {
        try {
            UUID usuarioId = UUID.fromString(principal.getName());
            Usuario usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

            // Solo los doctores pueden actualizar historias clínicas
            if (usuario.getRol() != Usuario.Rol.DOCTOR) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            Optional<HistoriaClinica> historiaOpt = historiaClinicaService.findById(id, usuarioId, usuario.getRol());
            if (historiaOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            HistoriaClinica historia = historiaOpt.get();
            historia.setDetalles(request.get("detalles"));

            HistoriaClinica historiaActualizada = historiaClinicaService.saveOrUpdateHistoriaClinica(historia);
            return ResponseEntity.ok(historiaActualizada);
        } catch (Exception e) {
            log.error("Error al actualizar historia clínica {}: {}", id, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
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
