package com.organization.Auto_TEC.Service;

import java.util.Collections;

import org.springframework.context.annotation.Primary;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.organization.Auto_TEC.Repository.AdministradorRepository;
import com.organization.Auto_TEC.Repository.UsuarioRepository;

@Service
@Primary
public class CombinedUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final AdministradorRepository administradorRepository;

    public CombinedUserDetailsService(UsuarioRepository usuarioRepository,
            AdministradorRepository administradorRepository) {
        this.usuarioRepository = usuarioRepository;
        this.administradorRepository = administradorRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        System.out.println("🔄 ===== INICIANDO AUTENTICACIÓN COMBINADA =====");
        System.out.println("🔍 Buscando usuario: '" + usernameOrEmail + "'");

        var adminOpt = administradorRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
        if (adminOpt.isPresent()) {
            var admin = adminOpt.get();
            System.out.println("   ADMINISTRADOR ENCONTRADO:");
            System.out.println("   ID: " + admin.getId());
            System.out.println("   Username: " + admin.getUsername());
            System.out.println("   Email: " + admin.getEmail());
            System.out.println("   Rol: " + admin.getRol().getNombre());

            String role = "ROLE_" + admin.getRol().getNombre();

            return User.builder()
                    .username(admin.getUsername())
                    .password(admin.getPasswordHash())
                    .authorities(Collections.singletonList(new SimpleGrantedAuthority(role)))
                    .disabled(!admin.getActivo())
                    .build();
        }

        var usuarioOpt = usuarioRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
        if (usuarioOpt.isPresent()) {
            var usuario = usuarioOpt.get();
            System.out.println("✅ USUARIO NORMAL ENCONTRADO:");
            System.out.println("   ID: " + usuario.getId());
            System.out.println("   Username: " + usuario.getUsername());
            System.out.println("   Email: " + usuario.getEmail());
            System.out.println("   Rol: " + usuario.getRol().getNombre());

            String role = "ROLE_" + usuario.getRol().getNombre();

            return User.builder()
                    .username(usuario.getUsername())
                    .password(usuario.getPasswordHash())
                    .authorities(Collections.singletonList(new SimpleGrantedAuthority(role)))
                    .disabled(!usuario.isActivo())
                    .build();
        }

        System.out.println(" USUARIO NO ENCONTRADO EN NINGUNA TABLA: " + usernameOrEmail);
        throw new UsernameNotFoundException("Usuario no encontrado: " + usernameOrEmail);
    }
}