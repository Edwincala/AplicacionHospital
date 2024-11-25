package com.hospital.proyectoHospital.models;

import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "doctores")
public class Doctor extends Empleado{

    @Column(nullable = false)
    private String especialidad;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<HorarioDoctor> horarios;

    public Doctor() {
        super();
    }

    public Doctor(UUID id, String nombre, String apellido, String username, String password, String especialidad, List<HorarioDoctor> horarios, List<Token> tokens) {
        super(id, nombre, apellido, username, password, Rol.DOCTOR, tokens);
        this.especialidad = especialidad;
        this.horarios = horarios != null ? horarios : List.of();
    }

    public Doctor(UUID id, String nombre, String apellido, String email, String password, String especialidad) {
        this(id, nombre, apellido, email, password, especialidad, null, null);
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public List<HorarioDoctor> getHorarios() {
        return horarios;
    }

    public void setHorarios(List<HorarioDoctor> horarios) {
        this.horarios = horarios;
    }
}
