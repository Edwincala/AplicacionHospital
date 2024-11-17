package com.hospital.proyectoHospital.services;

import com.hospital.proyectoHospital.models.Doctor;
import com.hospital.proyectoHospital.models.Empleado;
import com.hospital.proyectoHospital.repositories.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmpleadoService empleadoService;

    public List<Doctor> findAllDoctors(){
        return doctorRepository.findAll();
    }

    public Optional<Doctor> findDoctorById(UUID id) {
        return doctorRepository.findById(id);
    }

    public List<Doctor> findDoctorByEspecialidad(String especialidadFragment) {
        return doctorRepository.findByEspecialidadContaining(especialidadFragment);
    }

    public List<Doctor> findDoctorByNombre(String nombreFragment) {
        return doctorRepository.findByNombreContaining(nombreFragment);
    }

    public void deleteDoctor(UUID doctorId) {
        doctorRepository.deleteById(doctorId);
    }

    public boolean createOrUpdateDoctor(UUID doctorId, String contrasena, String nombre, String apellido, String email, Empleado.Rol rol, String especialidad) {

        if (!empleadoService.createOrUpdateEmpleado(doctorId, contrasena, nombre, apellido, email, rol)) {
            return false;
        }
        Optional<Doctor> doctorExistente = doctorRepository.findById(doctorId);
        Doctor doctor = doctorExistente.orElse(new Doctor());

        doctor.setEspecialidad(especialidad);

        doctorRepository.save(doctor);
        return true;
    }

}
