package com.organization.Auto_TEC.Service;

import com.organization.Auto_TEC.Entities.usuarioEntitie;
import com.organization.Auto_TEC.Repository.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UserDetailsServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        usuarioEntitie user = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        // Obtenemos el nombre del rol (ejemplo: "ADMIN")
        String nombreRol = user.getRol().getNombre();
        
        // Convertimos a formato "ROLE_ADMIN" para que Spring Security lo acepte
        String authority = nombreRol.startsWith("ROLE_") ? nombreRol : "ROLE_" + nombreRol;

        return new User(
            user.getUsername(),
            user.getPasswordHash(),
            user.isActivo(),
            true, true, true,
            List.of(new SimpleGrantedAuthority(authority))
        );
    }
}