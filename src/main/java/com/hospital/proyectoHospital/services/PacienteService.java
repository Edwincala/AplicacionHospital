package com.hospital.proyectoHospital.services;

import com.hospital.proyectoHospital.models.HistoriaClinica;
import com.hospital.proyectoHospital.models.Paciente;
import com.hospital.proyectoHospital.models.TipoDocumento;
import com.hospital.proyectoHospital.repositories.PacientesRepository;
import com.hospital.proyectoHospital.security.PasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PacienteService {

    @Autowired
    private PacientesRepository pacienteRepository;

    @Autowired
    private PasswordUtils passwordUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public boolean createOrUpdatePaciente(UUID pacienteId, String contrasena, String nombre, String apellido, String email, TipoDocumento tipoDocumento, String documentoIdentidad, String direccion, String telefono) {
        if(!passwordUtils.isPasswordStrong(contrasena)) {
            return false;
        }

        Optional<Paciente> pacienteExistente = pacienteRepository.findById(pacienteId);
        Paciente paciente = pacienteExistente.orElse(new Paciente());

        paciente.setNombre(nombre);
        paciente.setContrasena(passwordEncoder.encode(contrasena));
        paciente.setApellido(apellido);
        paciente.setEmail(email);
        paciente.setTipoDocumento(tipoDocumento);
        paciente.setDocumentoIdentidad(documentoIdentidad);
        paciente.setDireccion(direccion);
        paciente.setTelefono(telefono);

        if(!pacienteExistente.isPresent()) {
            HistoriaClinica nuevaHistoriaClinica = new HistoriaClinica();
            nuevaHistoriaClinica.setDetalles("Detalles iniciales de la historia Clínica");
            nuevaHistoriaClinica.setFechaCreacion(new Date());
            nuevaHistoriaClinica.setPaciente(paciente);
            paciente.setHistoriaClinica(nuevaHistoriaClinica);
        } else if (pacienteExistente.isPresent()) {
            HistoriaClinica historiaClinica = paciente.getHistoriaClinica();
            historiaClinica.setDetalles("Actualización de los detalles de la historia clínica");
        }

        pacienteRepository.save(paciente);
        return true;
    }


    public List<Paciente> findAllPacientes() {
        return pacienteRepository.findAll();
    }

    public Optional<Paciente> findPacienteById(UUID id) {
        return pacienteRepository.findById(id);
    }

    public void deletePaciente(UUID id) {
        pacienteRepository.deleteById(id);
    }

    public List<Paciente> findPacientesByNombre(String nombre) {
        return pacienteRepository.findByNombreContaining(nombre);
    }

    public List<Paciente> findPacientesByApellido(String apellidoFragment) {
        return pacienteRepository.findByApellidoContaining(apellidoFragment);
    }

    public List<Paciente> findPacientesByDocumentoIdentidad(String documentoPrefix) {
        return pacienteRepository.findByDocumentoIdentidadStartingWith(documentoPrefix);
    }
}
