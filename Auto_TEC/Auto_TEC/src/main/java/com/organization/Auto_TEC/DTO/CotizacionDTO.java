package com.organization.Auto_TEC.DTO;

import java.time.LocalDateTime;
import com.organization.Auto_TEC.Entities.cotizacionEstado;

public class CotizacionDTO {
    // Estos nombres deben coincidir con el JSON del JS: {nombre, email, modelo}
    private String nombre; 
    private String email;
    private String modelo; 
    
    // Campos para el Panel Administrativo
    private Long id;
    private LocalDateTime fechaSolicitud;
    private cotizacionEstado estado;
    private String notas;

    // Constructor vacío (Obligatorio para Jackson/Spring)
    public CotizacionDTO() {
        this.fechaSolicitud = LocalDateTime.now();
        this.estado = cotizacionEstado.PENDIENTE;
    }

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(LocalDateTime fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }

    public cotizacionEstado getEstado() { return estado; }
    public void setEstado(cotizacionEstado estado) { this.estado = estado; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
}