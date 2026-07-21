package com.organization.Auto_TEC.Config;

import com.organization.Auto_TEC.Entities.*;
import com.organization.Auto_TEC.Repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final DepartamentosRepository departamentosRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RolRepository rolRepository, 
                           DepartamentosRepository departamentosRepository,
                           UsuarioRepository usuarioRepository, 
                           PasswordEncoder passwordEncoder) {
        this.rolRepository = rolRepository;
        this.departamentosRepository = departamentosRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        crearRoles();
        crearDepartamentos();
        crearUsuarioAdministrador();
    }

    private void crearRoles() {
        String[] roles = {"ROLE_ADMIN", "ROLE_CLIENTE", "ROLE_MECANICO"};
        for (String nombreRol : roles) {
            if (rolRepository.findByNombre(nombreRol).isEmpty()) {
                Rol r = new Rol();
                r.setNombre(nombreRol);
                rolRepository.save(r);
            }
        }
    }

    private void crearDepartamentos() {
        if (departamentosRepository.count() == 0) {
            Departamentos d = new Departamentos();
            d.setNombre("Administración");
            departamentosRepository.save(d);
        }
    }

    private void crearUsuarioAdministrador() {
        // Buscamos si ya existe, si no, lo creamos
        if (usuarioRepository.findByUsername("superadmin").isEmpty()) {
            
            Rol adminRol = rolRepository.findByNombre("ROLE_ADMIN")
                    .orElseThrow(() -> new RuntimeException("Error: Rol ROLE_ADMIN no encontrado"));

            usuarioEntitie su = new usuarioEntitie();
            su.setUsername("superadmin");
            su.setEmail("superadmin@autotec.com");
            su.setNombres("Admin");
            su.setApellidos("Principal");
            
            // AQUÍ ESTÁ EL SECRETO: El encriptador de Spring hace la magia
            su.setPasswordHash(passwordEncoder.encode("superadmin123"));
            
            su.setRol(adminRol);
            su.setActivo(true);
            
            usuarioRepository.save(su);
            System.out.println("DEBUG: Usuario superadmin creado con éxito.");
        }
    }
}