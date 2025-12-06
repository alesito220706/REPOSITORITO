package com.organization.Auto_TEC.DTO;

import java.time.OffsetDateTime;

public class LoginResponseDTO {

    private String token;
    private String type = "Bearer";
    private String username;
    private Long userId;
    private OffsetDateTime fechaExpiracion;
    private String mensaje;

    // Constructores
    public LoginResponseDTO() {
    }

    public LoginResponseDTO(String token, OffsetDateTime fechaExpiracion, Long userId, String mensaje) {
        this.token = token;
        this.fechaExpiracion = fechaExpiracion;
        this.userId = userId;
        this.mensaje = mensaje;
    }

    public LoginResponseDTO(String token, String username, String mensaje) {
        this.token = token;
        this.username = username;
        this.mensaje = mensaje;
    }

    // Getters y Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public OffsetDateTime getFechaExpiracion() {
        return fechaExpiracion;
    }

    public void setFechaExpiracion(OffsetDateTime fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}