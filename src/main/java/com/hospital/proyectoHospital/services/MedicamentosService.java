package com.hospital.proyectoHospital.services;

import com.hospital.proyectoHospital.controllers.MedicamentosController;
import com.hospital.proyectoHospital.models.Medicamentos;
import com.hospital.proyectoHospital.repositories.MedicamentosRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MedicamentosService {

    @Autowired
    private MedicamentosRepository medicamentosRepository;

    private static final Logger log = LoggerFactory.getLogger(MedicamentosController.class);

    public boolean createOrUpdateMedicamento(Medicamentos medicamentos) {
        try {
            medicamentosRepository.save(medicamentos);
            log.info("Medicamento procesado correctamente: {}", medicamentos);
            return true;
        } catch (Exception e) {
            log.error("Error al procesar el medicamento: {}", e.getMessage(), e);
            return false;
        }
    }

    public List<Medicamentos> findAllMedicamentos() {
        return medicamentosRepository.findAll();
    }

    public Optional<Medicamentos> findMedicamentosById(UUID id) {
        return medicamentosRepository.findById(id);
    }

    public Page<Medicamentos> findMedicamentosByNombre(String nombre, Pageable pageable) {
        log.info("Buscando medicamentos por nombre: {}", nombre);
        return medicamentosRepository.findByNombreContainingIgnoreCase(nombre, pageable);
    }

    public Page<Medicamentos> findMedicamentosByLaboratorio(String laboratorio, Pageable pageable) {
        log.info("Buscando medicamentos del laboratorio: {}", laboratorio);
        return medicamentosRepository.findByLaboratorioIgnoreCase(laboratorio, pageable);
    }

    public List<Medicamentos> findMedicamentosByPrecioBetween(int precioMin, int precioMax) {
        log.info("Buscando medicamentos con precio entre {} y {}", precioMin, precioMax);
        return medicamentosRepository.findByPrecioBetween(precioMin, precioMax);
    }

    public List<Medicamentos> buscarPorLaboratorioOrdenAsc(String laboratorio) {
        log.info("Buscando medicamentos del laboratorio '{}' ordenados por precio ascendente", laboratorio);
        return medicamentosRepository.findByLaboratorioOrderByPrecioAsc(laboratorio);
    }

    public List<Medicamentos> buscarPorLaboratorioOrdenDesc(String laboratorio) {
        log.info("Buscando medicamentos del laboratorio '{}' ordenados por precio descendente", laboratorio);
        return medicamentosRepository.findByLaboratorioOrderByPrecioDesc(laboratorio);
    }

    public boolean deleteMedicamentos(UUID id) {
        try {
            if (medicamentosRepository.existsById(id)) {
                medicamentosRepository.deleteById(id);
                log.info("Medicamento con ID {} eliminado correctamente.", id);
                return true;
            } else {
                log.warn("No se encontró un medicamento con ID: {}", id);
                return false;
            }
        } catch (Exception e) {
            log.error("Error al eliminar el medicamento con ID {}: {}", id, e.getMessage(), e);
            return false;
        }
    }
}
