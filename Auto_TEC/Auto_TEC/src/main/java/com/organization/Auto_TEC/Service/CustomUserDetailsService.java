package com.organization.Auto_TEC.Service;

import java.util.Collections;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.organization.Auto_TEC.Entities.usuarioEntitie;
import com.organization.Auto_TEC.Repository.UsuarioRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        // Buscamos al usuario. Asegúrate de que tu Repository tenga el método findByUsernameOrEmail
        var usuarioOpt = usuarioRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
        
        if (usuarioOpt.isEmpty()) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + usernameOrEmail);
        }

        usuarioEntitie usuario = usuarioOpt.get();
        
        // Validación de seguridad para evitar NullPointerException si el rol es nulo
        String nombreRol = (usuario.getRol() != null) ? usuario.getRol().getNombre() : "CLIENTE";
        String role = "ROLE_" + nombreRol;

        // Construimos el objeto de seguridad de Spring
        return User.builder()
                .username(usuario.getUsername())
                .password(usuario.getPasswordHash()) // Asegúrate de que este campo tenga el hash
                .authorities(Collections.singletonList(new SimpleGrantedAuthority(role)))
                .disabled(!usuario.isActivo()) // Si activo es false, bloquea el acceso
                .build();
    }
}