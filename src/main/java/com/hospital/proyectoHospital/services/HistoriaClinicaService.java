package com.hospital.proyectoHospital.services;

import com.hospital.proyectoHospital.models.Empleado;
import com.hospital.proyectoHospital.models.HistoriaClinica;
import com.hospital.proyectoHospital.repositories.HistoriaClinicaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class HistoriaClinicaService {

    @Autowired
    private HistoriaClinicaRepository historiaClinicaRepository;

    public HistoriaClinica saveOrUpdateHistoriaClinica(HistoriaClinica historiaClinica) {
        return historiaClinicaRepository.save(historiaClinica);
    }

    public List<HistoriaClinica> findAllHistoriasClinicas() {
        return historiaClinicaRepository.findAll();
    }

    public Optional<HistoriaClinica> findHistoriaClinica(UUID usuarioId, Empleado.Rol rol, UUID pacienteId) {
        if (rol == Empleado.Rol.DOCTOR) {
            return historiaClinicaRepository.findByPacienteId(pacienteId);
        } else if (rol == null) {
            if (usuarioId.equals(pacienteId)) {
                return historiaClinicaRepository.findByPacienteId(pacienteId);
            }
        }
        return Optional.empty();
    }

    public void deleteHistoriaClinica(Long id) {
        historiaClinicaRepository.deleteById(id);
    }

}
