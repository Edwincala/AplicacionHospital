package com.hospital.proyectoHospital.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "doctores")
public class Doctor extends Empleado{
    @Column(nullable = false)
    private String especialidad;

    public Doctor() {
        super();
    }

    public Doctor(UUID id, String nombre, String email, String contrasena, Rol rol, String especialidad) {
        super(id, nombre, email, contrasena, rol);
        this.especialidad = especialidad;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }
}
