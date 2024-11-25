package com.hospital.proyectoHospital.services;

import com.hospital.proyectoHospital.models.CompraMedicamento;
import com.hospital.proyectoHospital.models.Medicamentos;
import com.hospital.proyectoHospital.models.Paciente;
import com.hospital.proyectoHospital.repositories.CompraMedicamentoRepository;
import com.hospital.proyectoHospital.repositories.MedicamentosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CompraService {
    @Autowired
    private CompraMedicamentoRepository compraRepository;

    @Autowired
    private MedicamentosRepository medicamentosRepository;

    public boolean procesarCompra(UUID medicamentoId, UUID pacienteId, int cantidad) {
        Medicamentos medicamento = medicamentosRepository.findById(medicamentoId)
                .orElseThrow(() -> new IllegalArgumentException("Medicamento no encontrado"));
        if (medicamento.getCantidadEnStock() < cantidad) {
            return false;
        }
        medicamento.setCantidadEnStock(medicamento.getCantidadEnStock() - cantidad);
        medicamentosRepository.save(medicamento);

        CompraMedicamento compra = new CompraMedicamento();
        compra.setMedicamento(medicamento);
        compra.setPaciente(new Paciente(pacienteId));
        compra.setCantidad(cantidad);
        compra.setFechaCompra(LocalDateTime.now());
        compraRepository.save(compra);
        return true;
    }
}
