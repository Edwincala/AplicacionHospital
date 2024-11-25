package com.hospital.proyectoHospital.models;


import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name="empleados")
public class Empleado extends Usuario {


    public Empleado() {
        super();
    }

    public Empleado(UUID id, String nombre, String apellido, String username, String password, Rol rol, List<Token> tokens) {
        super(id, nombre, apellido, username, password, rol, tokens);
        if (rol != Rol.DOCTOR && rol != Rol.ADMINISTRATIVO) {
            throw new IllegalArgumentException("El rol de un empleado debe ser DOCTOR o ADMINISTRATIVO.");
        }
    }

    public Empleado(UUID id, String nombre, String apellido, String email, String password, Rol rol) {
        this(id, nombre, apellido, email, password, rol, null);
    }
}
