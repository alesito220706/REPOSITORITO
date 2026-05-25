package com.organization.Auto_TEC.controller;

import com.organization.Auto_TEC.DTO.LoginRequestDTO;
import com.organization.Auto_TEC.DTO.LoginResponseDTO;
import com.organization.Auto_TEC.Service.AuthService;
import com.organization.Auto_TEC.Service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public AuthController(AuthService authService, JwtService jwtService, UserDetailsService userDetailsService) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO body, HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        LoginResponseDTO resp = authService.loginSoloUnaSesion(
                body.getUsernameOrEmail(), body.getPassword(), body.isKickPrevious(), ip, userAgent);

        final UserDetails userDetails = userDetailsService.loadUserByUsername(body.getUsernameOrEmail());
        resp.setToken(jwtService.generateToken(userDetails));
        resp.setMensaje("Autenticación exitosa vía API");

        return ResponseEntity.ok(resp);
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<Map<String, String>> handleExceptions(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", ex.getClass().getSimpleName(), "message", ex.getMessage()));
    }
}