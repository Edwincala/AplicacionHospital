package com.hospital.proyectoHospital.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class CompraMedicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private Medicamentos medicamento;

    @ManyToOne
    private Paciente paciente;

    private int cantidad;

    private LocalDateTime fechaCompra;

    public CompraMedicamento() {
    }

    public CompraMedicamento(UUID id, Medicamentos medicamento, Paciente paciente, int cantidad, LocalDateTime fechaCompra) {
        this.id = id;
        this.medicamento = medicamento;
        this.paciente = paciente;
        this.cantidad = cantidad;
        this.fechaCompra = fechaCompra;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Medicamentos getMedicamento() {
        return medicamento;
    }

    public void setMedicamento(Medicamentos medicamento) {
        this.medicamento = medicamento;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public LocalDateTime getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(LocalDateTime fechaCompra) {
        this.fechaCompra = fechaCompra;
    }
}
