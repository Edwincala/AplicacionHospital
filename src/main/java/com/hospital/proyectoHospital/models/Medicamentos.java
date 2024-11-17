package com.hospital.proyectoHospital.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "medicamentos")
public class Medicamentos {
    @Id
    @UuidGenerator
    private UUID id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String descripcion;

    @Column(nullable = false)
    private int cantidadEnStock;

    @Column(nullable = false)
    private String laboratoria;

    @Column
    private String urlImagen;

    public Medicamentos() {
    }

    public Medicamentos(UUID id, String nombre, String descripcion, int cantidadEnStock, String laboratoria, String urlImagen) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.cantidadEnStock = cantidadEnStock;
        this.laboratoria = laboratoria;
        this.urlImagen = urlImagen;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getCantidadEnStock() {
        return cantidadEnStock;
    }

    public void setCantidadEnStock(int cantidadEnStock) {
        this.cantidadEnStock = cantidadEnStock;
    }

    public String getLaboratoria() {
        return laboratoria;
    }

    public void setLaboratoria(String laboratoria) {
        this.laboratoria = laboratoria;
    }

    public String getUrlImagen() {
        return urlImagen;
    }

    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen;
    }
}
