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
        } else if (rol == Empleado.Rol.PACIENTE) {
            if (usuarioId.equals(pacienteId)) {
                return historiaClinicaRepository.findByPacienteId(pacienteId);
            }
        }
        return Optional.empty();
    }

    public boolean deleteHistoriaClinica(Long id) {
        try {
            if (historiaClinicaRepository.existsById(id)) {
                historiaClinicaRepository.deleteById(id);
                return true;
            }
            return false;
        } catch (Exception e) {
            // Logging del error (si se necesita)
            return false;
        }
    }

    public Optional<HistoriaClinica> findById(Long id, UUID usuarioId, Empleado.Rol rol) {
        Optional<HistoriaClinica> historiaOpt = historiaClinicaRepository.findById(id);

        if (historiaOpt.isEmpty()) {
            return Optional.empty();
        }

        HistoriaClinica historia = historiaOpt.get();
        UUID pacienteId = historia.getPaciente().getId();

        // Usar la lógica existente de verificación de permisos
        Optional<HistoriaClinica> historiaConPermisos = findHistoriaClinica(usuarioId, rol, pacienteId);

        // Si findHistoriaClinica retorna un valor, significa que el usuario tiene permisos
        return historiaConPermisos.isPresent() ? historiaOpt : Optional.empty();
    }

}
