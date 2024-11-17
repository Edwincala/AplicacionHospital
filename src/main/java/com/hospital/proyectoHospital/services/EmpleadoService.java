package com.hospital.proyectoHospital.services;

import com.hospital.proyectoHospital.models.Empleado;
import com.hospital.proyectoHospital.repositories.EmpleadoRepository;
import com.hospital.proyectoHospital.security.PasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EmpleadoService {
    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private PasswordUtils passwordUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Empleado> findAllEmpleados() {
        return empleadoRepository.findAll();
    }

    public Optional<Empleado> findEmpleadoById(UUID id) {
        return empleadoRepository.findById(id);
    }

    public boolean createOrUpdateEmpleado(UUID empleadoId, String contrasena, String nombre, String apellido, String email, Empleado.Rol rol) {
        if(!passwordUtils.isPasswordStrong(contrasena)) {
            return false;
        }

        Optional<Empleado> empleadoExistente = empleadoRepository.findById(empleadoId);
        Empleado empleado = empleadoExistente.orElse(new Empleado());

        empleado.setContrasena(passwordEncoder.encode(contrasena));
        empleado.setNombre(nombre);
        empleado.setApellido(apellido);
        empleado.setEmail(email);
        empleado.setRol(rol);

        empleadoRepository.save(empleado);
        return true;
    }

    public void deleteEmpleado(UUID id) {
        empleadoRepository.deleteById(id);
    }

    public List<Empleado> findEmpleadoByRol(Empleado.Rol rol) {
        return empleadoRepository.findByRol(rol);
    }

    public List<Empleado> findEmpleadoByNombre(String nombre) {
        return empleadoRepository.findByNombreContaining(nombre);
    }
}
