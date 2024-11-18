package com.hospital.proyectoHospital.services;

import com.hospital.proyectoHospital.models.HistoriaClinica;
import com.hospital.proyectoHospital.repositories.HistoriaClinicaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public Optional<HistoriaClinica> findHistoriaClinicaById(Long id) {
        return historiaClinicaRepository.findById(id);
    }

    public void deleteHistoriaClinica(Long id) {
        historiaClinicaRepository.deleteById(id);
    }

}
