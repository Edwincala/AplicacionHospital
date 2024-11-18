package com.hospital.proyectoHospital.controllers;

import com.hospital.proyectoHospital.models.HistoriaClinica;
import com.hospital.proyectoHospital.services.HistoriaClinicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/historiasClinicas")
public class HistoriaClinicaController {
    @Autowired
    private HistoriaClinicaService historiaClinicaService;

    @PostMapping
    public ResponseEntity<HistoriaClinica> createHistoriaClinica(@RequestBody HistoriaClinica historiaClinica) {
        HistoriaClinica savedHistoriaClinica = historiaClinicaService.saveOrUpdateHistoriaClinica(historiaClinica);
        return new ResponseEntity<>(savedHistoriaClinica, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HistoriaClinica> getHistoriaClinicaById(@PathVariable Long id) {
        return historiaClinicaService.findHistoriaClinicaById(id)
                .map(historiaClinica -> new ResponseEntity<>(historiaClinica, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
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
