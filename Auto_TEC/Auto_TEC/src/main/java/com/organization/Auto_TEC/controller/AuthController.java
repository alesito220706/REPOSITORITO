package com.organization.Auto_TEC.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired; // Importar
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails; // Importar
import org.springframework.security.core.userdetails.UserDetailsService; // Importar
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*; // Simplificado

import com.organization.Auto_TEC.DTO.LoginRequestDTO;
import com.organization.Auto_TEC.DTO.LoginResponseDTO;
import com.organization.Auto_TEC.Service.AuthService;
import com.organization.Auto_TEC.Service.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "/api/auth")
@Validated
public class AuthController {

    private final AuthService authService;

    // Inyectamos JwtService y UserDetailsService
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserDetailsService userDetailsService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(path = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO body,
            HttpServletRequest request) {
        String ip = getClientIp(request);
        String userAgent = Optional.ofNullable(request.getHeader("User-Agent")).orElse("unknown");

        // 1. Ejecutar tu lógica de negocio existente (validar pass, kick user, etc.)
        LoginResponseDTO resp = authService.loginSoloUnaSesion(
                body.getUsernameOrEmail(),
                body.getPassword(),
                body.isKickPrevious(),
                ip,
                userAgent);

        final UserDetails userDetails = userDetailsService.loadUserByUsername(body.getUsernameOrEmail());

        String jwtToken = jwtService.generateToken(userDetails);

        resp.setToken(jwtToken);
        resp.setMensaje("Autenticación exitosa vía API");

        return ResponseEntity.ok(resp);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, Object> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", "UNAUTHORIZED");
        error.put("message", ex.getMessage());
        return error;
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, Object> handleIllegalState(IllegalStateException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", "CONFLICT");
        error.put("message", ex.getMessage());
        return error;
    }

    private String getClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp;
        }
        return request.getRemoteAddr();
    }
}