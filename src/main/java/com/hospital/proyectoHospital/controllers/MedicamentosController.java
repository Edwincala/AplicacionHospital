package com.hospital.proyectoHospital.controllers;

import com.hospital.proyectoHospital.models.CompraMedicamentoRequest;
import com.hospital.proyectoHospital.models.Medicamentos;
import com.hospital.proyectoHospital.services.CompraService;
import com.hospital.proyectoHospital.services.MedicamentosService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/medicamentos")
public class MedicamentosController {

    @Autowired
    private MedicamentosService medicamentosService;

    @Autowired
    private CompraService compraService;

    private static final Logger log = LoggerFactory.getLogger(MedicamentosController.class);

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRATIVO')")
    public ResponseEntity<String> createOrUpdateMedicamento(@RequestBody Medicamentos medicamentos) {
        try {
            log.info("Procesando solicitud para el medicamento: {}", medicamentos);

            boolean result = medicamentosService.createOrUpdateMedicamento(medicamentos);
            String mensaje = (medicamentos.getId() != null) ?
                    "Medicamento actualizado con éxito." :
                    "Medicamento creado con éxito.";
            return result ? ResponseEntity.ok(mensaje) : ResponseEntity.badRequest().body("No se pudo procesar la solicitud.");
        } catch (Exception e) {
            log.error("Error al procesar medicamento: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor.");
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<List<Medicamentos>> getAllMedicamentos() {
        try {
            List<Medicamentos> medicamentos = medicamentosService.findAllMedicamentos();
            return medicamentos.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(medicamentos);
        } catch (Exception e) {
            log.error("Error al obtener medicamentos: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Medicamentos> getMedicamentosById(@PathVariable UUID id) {
        try {
            Optional<Medicamentos> medicamento = medicamentosService.findMedicamentosById(id);
            return medicamento.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error al buscar medicamento por ID: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<Page<Medicamentos>> getMedicamentosByNombre(@PathVariable String nombre, Pageable pageable) {
        try {
            Page<Medicamentos> medicamentos = medicamentosService.findMedicamentosByNombre(nombre, pageable);
            return medicamentos.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(medicamentos);
        } catch (Exception e) {
            log.error("Error al buscar medicamentos por nombre: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/laboratorio/{laboratorio}")
    public ResponseEntity<Page<Medicamentos>> getMedicamentosByLaboratorio(@PathVariable String laboratorio, Pageable pageable) {
        try {
            Page<Medicamentos> medicamentos = medicamentosService.findMedicamentosByLaboratorio(laboratorio, pageable);
            return medicamentos.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(medicamentos);
        } catch (Exception e) {
            log.error("Error al buscar medicamentos por laboratorio: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/buscar/precio")
    public ResponseEntity<List<Medicamentos>> buscarPorRangoDePrecio(@RequestParam int precioMin, @RequestParam int precioMax) {
        try {
            log.info("Buscando medicamentos por rango de precios entre {} y {}", precioMin, precioMax);
            List<Medicamentos> medicamentos = medicamentosService.findMedicamentosByPrecioBetween(precioMin, precioMax);
            return medicamentos.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(medicamentos);
        } catch (Exception e) {
            log.error("Error al buscar medicamentos por rango de precios: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/buscar/laboratorio/asc")
    public ResponseEntity<List<Medicamentos>> buscarPorLaboratorioOrdenAsc(@RequestParam String laboratorio) {
        try {
            List<Medicamentos> medicamentos = medicamentosService.buscarPorLaboratorioOrdenAsc(laboratorio);
            return medicamentos.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(medicamentos);
        } catch (Exception e) {
            log.error("Error al buscar medicamentos por laboratorio (asc): {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/buscar/laboratorio/desc")
    public ResponseEntity<List<Medicamentos>> buscarPorLaboratorioOrdenDesc(@RequestParam String laboratorio) {
        try {
            List<Medicamentos> medicamentos = medicamentosService.buscarPorLaboratorioOrdenDesc(laboratorio);
            return medicamentos.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(medicamentos);
        } catch (Exception e) {
            log.error("Error al buscar medicamentos por laboratorio (desc): {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/id/{id}")
    public ResponseEntity<String> deleteMedicamento(@PathVariable UUID id) {
        try {
            medicamentosService.deleteMedicamentos(id);
            return ResponseEntity.ok("Medicamento eliminado con éxito.");
        } catch (Exception e) {
            log.error("Error al eliminar medicamento: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar el medicamento.");
        }
    }

    @PostMapping("/comprar")
    public ResponseEntity<String> comprarMedicamentos(@RequestBody CompraMedicamentoRequest request) {
        boolean result = compraService.procesarCompra(request.getMedicamentoId(), request.getPacienteId(), request.getCantidad());
        if (result) {
            return ResponseEntity.ok("Compra realizada con éxito.");
        } else {
            return ResponseEntity.badRequest().body("Stock insuficiente.");
        }
    }
}
