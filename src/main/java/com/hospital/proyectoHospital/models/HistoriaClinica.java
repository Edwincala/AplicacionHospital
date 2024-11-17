package com.hospital.proyectoHospital.models;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class HistoriaClinica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String detalles;

    @Temporal(TemporalType.DATE)
    private Date fechaCreacion;

    @OneToOne
    @JoinColumn(name = "paciente_id", referencedColumnName = "id")
    private Paciente paciente;

    public HistoriaClinica() {
    }

    public HistoriaClinica(Long id, String detalles, Date fechaCreacion, Paciente paciente) {
        this.id = id;
        this.detalles = detalles;
        this.fechaCreacion = fechaCreacion;
        this.paciente = paciente;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getDetalles() {
        return detalles;
    }

    public void setDetalles(String detalles) {
        this.detalles = detalles;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
