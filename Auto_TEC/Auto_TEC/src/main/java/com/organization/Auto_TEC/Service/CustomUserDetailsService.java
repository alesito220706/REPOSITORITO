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
        System.out.println(" ===== INICIANDO AUTENTICACIÓN =====");
        System.out.println(" Buscando usuario: '" + usernameOrEmail + "'");
        
        var usuarioOpt = usuarioRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
        
        if (usuarioOpt.isEmpty()) {
            System.out.println(" USUARIO NO ENCONTRADO: " + usernameOrEmail);
            throw new UsernameNotFoundException("Usuario no encontrado: " + usernameOrEmail);
        }

        usuarioEntitie usuario = usuarioOpt.get();
        System.out.println(" USUARIO ENCONTRADO:");
        System.out.println("   ID: " + usuario.getId());
        System.out.println("   Username: " + usuario.getUsername());
        System.out.println("   Email: " + usuario.getEmail());
        System.out.println("   Rol: " + usuario.getRol().getNombre());
        
        String role = "ROLE_" + usuario.getRol().getNombre();
        
        UserDetails userDetails = User.builder()
                .username(usuario.getUsername())
                .password(usuario.getPasswordHash())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority(role)))
                .disabled(!usuario.isActivo())
                .build();

        System.out.println(" UserDetails creado exitosamente");
        return userDetails;
    }
}