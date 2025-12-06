package com.organization.Auto_TEC.Service;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.organization.Auto_TEC.DTO.LoginResponseDTO;
import com.organization.Auto_TEC.DTO.RegistroDTO;
import com.organization.Auto_TEC.Entities.Departamentos;
import com.organization.Auto_TEC.Entities.Rol;
import com.organization.Auto_TEC.Entities.Sesiones;
import com.organization.Auto_TEC.Entities.administradorEntitie;
import com.organization.Auto_TEC.Entities.usuarioEntitie;
import com.organization.Auto_TEC.Repository.AdministradorRepository;
import com.organization.Auto_TEC.Repository.DepartamentosRepository;
import com.organization.Auto_TEC.Repository.RolRepository;
import com.organization.Auto_TEC.Repository.SesionRepository;
import com.organization.Auto_TEC.Repository.UsuarioRepository;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepo;
    private final AdministradorRepository administradorRepo;
    private final SesionRepository sesionRepo;
    private final RolRepository rolRepository;
    private final DepartamentosRepository departamentosRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UsuarioRepository usuarioRepo,
            AdministradorRepository administradorRepo,
            SesionRepository sesionRepo,
            RolRepository rolRepository,
            DepartamentosRepository departamentosRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepo = usuarioRepo;
        this.administradorRepo = administradorRepo;
        this.sesionRepo = sesionRepo;
        this.rolRepository = rolRepository;
        this.departamentosRepository = departamentosRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // MÉTODO DE REGISTRO CORREGIDO
    @Transactional
    public usuarioEntitie registrarUsuario(RegistroDTO registroDTO) {
        // Verificar si el usuario ya existe
        if (usuarioRepo.existsByUsername(registroDTO.getUsername())) {
            throw new RuntimeException("El nombre de usuario ya está en uso");
        }

        if (usuarioRepo.existsByEmail(registroDTO.getEmail())) {
            throw new RuntimeException("El correo electrónico ya está registrado");
        }

        // Buscar el rol CLIENTE
        Rol rolCliente = rolRepository.findByNombre("CLIENTE")
                .orElseThrow(() -> new RuntimeException("Rol CLIENTE no encontrado en la base de datos"));

        // Buscar departamento por defecto
        Departamentos departamentoDefault = departamentosRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No hay departamentos disponibles en la base de datos"));

        // Crear nuevo usuario
        usuarioEntitie usuario = new usuarioEntitie();
        usuario.setUsername(registroDTO.getUsername());
        usuario.setEmail(registroDTO.getEmail());
        usuario.setPasswordHash(passwordEncoder.encode(registroDTO.getPassword()));
        usuario.setNombres(registroDTO.getNombres());
        usuario.setApellidos(registroDTO.getApellidos());
        usuario.setRol(rolCliente);
        usuario.setActivo(true);

        return usuarioRepo.save(usuario);
    }

    @Transactional
    public LoginResponseDTO loginSoloUnaSesion(String usernameOrEmail, String rawPassword, boolean kickPrevious,
            String ip, String userAgent) {

        // Primero intentar buscar en administradores
        Optional<administradorEntitie> adminOpt = administradorRepo.findByUsernameOrEmail(usernameOrEmail,
                usernameOrEmail);

        if (adminOpt.isPresent()) {
            // Es un administrador
            return loginAdmin(adminOpt.get(), rawPassword, ip, userAgent);
        }

        // Si no es admin, buscar en usuarios normales
        usuarioEntitie user = usuarioRepo
                .findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Verificar si el usuario está activo
        if (!user.isActivo()) {
            throw new IllegalArgumentException("Usuario desactivado");
        }

        // Verificar contraseña
        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }

        // Revisar si existe sesión activa
        Optional<Sesiones> actual = sesionRepo.findFirstByUsuario_IdAndActivaTrue(user.getId());
        if (actual.isPresent()) {
            Sesiones s = actual.get();
            if (s.getFechaExpiracion().isAfter(OffsetDateTime.now())) {
                if (!kickPrevious) {
                    LoginResponseDTO response = new LoginResponseDTO(
                            null,
                            s.getFechaExpiracion(),
                            user.getId(),
                            "Ya tienes una sesión activa.");
                    response.setUsername(user.getUsername());
                    return response;
                } else {
                    s.setActiva(false);
                    s.setFechaCierre(OffsetDateTime.now());
                    sesionRepo.save(s);
                }
            } else {
                // Sesión expirada, desactivar
                s.setActiva(false);
                s.setFechaCierre(OffsetDateTime.now());
                sesionRepo.save(s);
            }
        }

        // Crear nueva sesión
        OffsetDateTime ahora = OffsetDateTime.now();
        Sesiones nueva = new Sesiones();
        nueva.setUsuario(user);
        nueva.setSessionToken(UUID.randomUUID().toString());
        nueva.setFechaCreacion(ahora);
        nueva.setFechaExpiracion(ahora.plusHours(1));
        nueva.setActiva(true);
        nueva.setIpAddress(ip);
        nueva.setUserAgent(userAgent);

        try {
            sesionRepo.save(nueva);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("Error al crear la sesión: " + e.getMessage(), e);
        }

        // Actualizar último login del usuario
        user.setUltimoLogin(ahora);
        usuarioRepo.save(user);

        LoginResponseDTO response = new LoginResponseDTO(
                nueva.getSessionToken(),
                nueva.getFechaExpiracion(),
                user.getId(),
                "Login correcto");
        response.setUsername(user.getUsername());
        return response;
    }

    private LoginResponseDTO loginAdmin(administradorEntitie admin, String rawPassword, String ip, String userAgent) {
        // Verificar si el admin está activo
        if (!admin.getActivo()) {
            throw new IllegalArgumentException("Administrador desactivado");
        }

        // Verificar contraseña
        if (!passwordEncoder.matches(rawPassword, admin.getPasswordHash())) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }

        // Para administradores, no usamos la tabla Sesiones
        // Solo actualizamos los campos de último login
        OffsetDateTime ahora = OffsetDateTime.now();
        admin.setUltimoLogin(ahora);
        admin.setUltimoIp(ip);
        admin.setUltimoUserAgent(userAgent);
        administradorRepo.save(admin);

        // Generar token de sesión único para el admin
        String sessionToken = UUID.randomUUID().toString();
        OffsetDateTime expiracion = ahora.plusHours(1);

        LoginResponseDTO response = new LoginResponseDTO(
                sessionToken,
                expiracion,
                admin.getId(),
                "Login correcto");
        response.setUsername(admin.getUsername());
        return response;
    }
}