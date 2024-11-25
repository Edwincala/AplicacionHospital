package com.hospital.proyectoHospital.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name="pacientes")
public class Paciente extends Usuario{

    @Column(nullable = false)
    private String direccion;

    @Column(nullable = false)
    private String telefono;

    @OneToOne(mappedBy = "paciente", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonManagedReference
    private HistoriaClinica historiaClinica;

    public Paciente() {
    }

    public Paciente(UUID id) {
        super.setId(id);
    }

    public Paciente(UUID id, String nombre, String apellido, String username, String password, String direccion, String telefono, HistoriaClinica historiaClinica) {
        super(id, nombre, apellido, username, password, Rol.PACIENTE, null);
        this.direccion = direccion;
        this.telefono = telefono;
        this.historiaClinica = historiaClinica;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public HistoriaClinica getHistoriaClinica() {
        return historiaClinica;
    }

    public void setHistoriaClinica(HistoriaClinica historiaClinica) {
        this.historiaClinica = historiaClinica;
    }
}
