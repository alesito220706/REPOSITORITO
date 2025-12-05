package com.organization.Auto_TEC.Entities;

import java.time.OffsetDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuarios")
public class usuarioEntitie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "rol_id", nullable = true)
    private Rol rol;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departamento_id", nullable = true)
    private Departamentos departamento;

    @Column(name = "username", nullable = true, length = 50, unique = true)
    private String username;

    @Column(name = "password_hash", nullable = true, length = 255)
    private String passwordHash;

    @Column(name = "email", nullable = true, length = 100, unique = true)
    private String email;

    @Column(name = "nombres", nullable = true, length = 100)
    private String nombres;

    @Column(name = "apellidos", nullable = true, length = 100)
    private String apellidos;

    @Column(name = "activo", nullable = true)
    private boolean activo = true;
    @Column(name = "telefono", length = 20)
    private String telefono;
    @CreationTimestamp
    @Column(name = "fecha_registro", updatable = false)
    private OffsetDateTime fechaRegistro;

    @Column(name = "ultimo_login")
    private OffsetDateTime ultimoLogin;

    public usuarioEntitie() {
    }

    public usuarioEntitie(Long id,
            Rol rol,
            Departamentos departamento, // Corregido el nombre
            String username,
            String passwordHash,
            String email,
            String nombres,
            String apellidos,
            String telefono,
            boolean activo,
            OffsetDateTime ultimoLogin) {
        this.id = id;
        this.rol = rol;
        this.departamento = departamento; // Corregido el nombre
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.activo = activo;
        this.ultimoLogin = ultimoLogin;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public Departamentos getDepartamento() { // Corregido el nombre del getter
        return departamento;
    }

    public void setDepartamento(Departamentos departamento) { // Corregido el nombre del setter
        this.departamento = departamento;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;

    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public OffsetDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(OffsetDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public OffsetDateTime getUltimoLogin() {
        return ultimoLogin;
    }

    public void setUltimoLogin(OffsetDateTime ultimoLogin) {
        this.ultimoLogin = ultimoLogin;
    }
}