package com.organization.Auto_TEC.DTO;

import jakarta.validation.constraints.NotBlank;

public class LoginRequestDTO {

    @NotBlank(message = "El nombre de usuario o email es requerido")
    private String usernameOrEmail;

    @NotBlank(message = "La contraseña es requerida")
    private String password;

    private boolean kickPrevious;

    // Constructores
    public LoginRequestDTO() {
    }

    public LoginRequestDTO(String usernameOrEmail, String password, boolean kickPrevious) {
        this.usernameOrEmail = usernameOrEmail;
        this.password = password;
        this.kickPrevious = kickPrevious;
    }

    // Getters y Setters
    public String getUsernameOrEmail() {
        return usernameOrEmail;
    }

    public void setUsernameOrEmail(String usernameOrEmail) {
        this.usernameOrEmail = usernameOrEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isKickPrevious() {
        return kickPrevious;
    }

    public void setKickPrevious(boolean kickPrevious) {
        this.kickPrevious = kickPrevious;
    }
}