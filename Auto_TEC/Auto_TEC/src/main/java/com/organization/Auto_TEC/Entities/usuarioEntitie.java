package com.organization.Auto_TEC.Entities;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "usuarios")
public class usuarioEntitie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Cambiado a EAGER para evitar errores de sesión al consultar el usuario logueado
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "departamento_id")
    private Departamentos departamento;

    @Column(name = "username", length = 50, unique = true, nullable = false)
    private String username;

    @Column(name = "password_hash", length = 255, nullable = false)
    private String passwordHash;

    @Column(name = "email", length = 100, unique = true, nullable = false)
    private String email;

    @Column(name = "nombres", length = 100)
    private String nombres;

    @Column(name = "apellidos", length = 100)
    private String apellidos;

    @Column(name = "activo")
    private boolean activo = true;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @CreationTimestamp
    @Column(name = "fecha_registro", updatable = false)
    private OffsetDateTime fechaRegistro;

    @Column(name = "ultimo_login")
    private OffsetDateTime ultimoLogin;

    // Constructor vacío obligatorio para JPA
    public usuarioEntitie() {
    }

    // Constructor completo
    public usuarioEntitie(Long id, Rol rol, Departamentos departamento, String username, 
                          String passwordHash, String email, String nombres, String apellidos, 
                          String telefono, boolean activo, OffsetDateTime ultimoLogin) {
        this.id = id;
        this.rol = rol;
        this.departamento = departamento;
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
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }

    public Departamentos getDepartamento() { return departamento; }
    public void setDepartamento(Departamentos departamento) { this.departamento = departamento; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public OffsetDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(OffsetDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public OffsetDateTime getUltimoLogin() { return ultimoLogin; }
    public void setUltimoLogin(OffsetDateTime ultimoLogin) { this.ultimoLogin = ultimoLogin; }

    // Métodos equals y hashCode (Recomendado para entidades JPA)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        usuarioEntitie that = (usuarioEntitie) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}