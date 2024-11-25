package com.hospital.proyectoHospital.models;

import java.util.UUID;

public class CompraMedicamentoRequest {
    private UUID medicamentoId;
    private UUID pacienteId;
    private int cantidad;

    public CompraMedicamentoRequest() {
    }

    public CompraMedicamentoRequest(UUID medicamentoId, UUID pacienteId, int cantidad) {
        this.medicamentoId = medicamentoId;
        this.pacienteId = pacienteId;
        this.cantidad = cantidad;
    }

    public UUID getMedicamentoId() {
        return medicamentoId;
    }

    public void setMedicamentoId(UUID medicamentoId) {
        this.medicamentoId = medicamentoId;
    }

    public UUID getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(UUID pacienteId) {
        this.pacienteId = pacienteId;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}
