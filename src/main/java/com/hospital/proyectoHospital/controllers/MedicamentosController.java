package com.hospital.proyectoHospital.controllers;

import com.hospital.proyectoHospital.models.Medicamentos;
import com.hospital.proyectoHospital.services.MedicamentosService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/medicamentos")
public class MedicamentosController {

    @Autowired
    private MedicamentosService medicamentosService;

    private static final Logger log = LoggerFactory.getLogger(MedicamentosController.class);

    @PostMapping
    public ResponseEntity<String> createOrUpdateMedicamento(@RequestBody Medicamentos medicamentos) {
        log.info("Datos recibidos: {}", medicamentos);

        boolean result = medicamentosService.createOrUpdateMedicamento(medicamentos);

        if (result) {
            String mensaje = medicamentos.getId() != null ?
                    "Medicamento actualizado con éxito." :
                    "Medicamento creado con éxito.";
            return ResponseEntity.ok(mensaje);
        } else {
            log.warn("No se pudo procesar la solicitud para el medicamento: {}", medicamentos);
            return ResponseEntity.badRequest().body("No se pudo procesar la solicitud.");
        }
    }

    @GetMapping
    public List<Medicamentos> getAllMedicamentos() {
        return medicamentosService.findAllMedicamentos();
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Medicamentos> getMedicamentosById(@PathVariable UUID id) {
        Optional<Medicamentos> medicamento = medicamentosService.findMedicamentosById(id);

        if (medicamento.isPresent()) {
            return ResponseEntity.ok(medicamento.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/nombre/{nombre}")
    public Page<Medicamentos> getMedicamentosByNombre(@PathVariable String nombre, Pageable pageable) {
        return medicamentosService.findMedicamentosByNombre(nombre, pageable);
    }

    @GetMapping("/laboratorio/{laboratorio}")
    public Page<Medicamentos> getMedicamentosByLaboratorio(@PathVariable String laboratorio, Pageable pageable) {
        return medicamentosService.findMedicamentosByLaboratorio(laboratorio, pageable);
    }

    @GetMapping("/buscar/precio")
    public ResponseEntity<List<Medicamentos>> buscarPorRangoDePrecio(@RequestParam int precioMin, @RequestParam int precioMax) {
        log.info("Recibida solicitud para buscar medicamentos por rango de precios entre {} y {}", precioMin, precioMax);
        List<Medicamentos> medicamentos = medicamentosService.findMedicamentosByPrecioBetween(precioMin, precioMax);
        return ResponseEntity.ok(medicamentos);
    }

    @GetMapping("/buscar/laboratorio/asc")
    public ResponseEntity<List<Medicamentos>> buscarPorLaboratorioOrdenAsc(@RequestParam String laboratorio) {
        log.info("Recibida solicitud para buscar medicamentos del laboratorio '{}' ordenados por precio ascendente", laboratorio);
        List<Medicamentos> medicamentos = medicamentosService.buscarPorLaboratorioOrdenAsc(laboratorio);
        return ResponseEntity.ok(medicamentos);
    }

    @GetMapping("/buscar/laboratorio/desc")
    public ResponseEntity<List<Medicamentos>> buscarPorLaboratorioOrdenDesc(@RequestParam String laboratorio) {
        log.info("Recibida solicitud para buscar medicamentos del laboratorio '{}' ordenados por precio descendente", laboratorio);
        List<Medicamentos> medicamentos = medicamentosService.buscarPorLaboratorioOrdenDesc(laboratorio);
        return ResponseEntity.ok(medicamentos);
    }

    @DeleteMapping("/id/{id}")
    public ResponseEntity<Void> deleteMedicamento(@PathVariable UUID id) {
        medicamentosService.deleteMedicamentos(id);
        return ResponseEntity.ok().build();
    }
}
