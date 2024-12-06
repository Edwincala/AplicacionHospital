package com.hospital.proyectoHospital.models;

import java.util.List;

public class PerfilDto {

    private Usuario.Rol role;
    private String nombre;
    private String apellido;
    private String username;
    private String password;

    private String telefono;
    private String direccion;

    private String especialidad;
    private List<HorarioDoctor> horarios;

    public PerfilDto() {
    }

    public PerfilDto(Usuario.Rol role, String nombre, String apellido, String username, String password, String telefono, String direccion, String especialidad, List<HorarioDoctor> horarios) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.username = username;
        this.password = password;
        this.telefono = telefono;
        this.direccion = direccion;
        this.especialidad = especialidad;
        this.horarios = horarios;
        this.role = role;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
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

    public Usuario.Rol getRole() {
        return role;
    }

    public void setRole(Usuario.Rol role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
