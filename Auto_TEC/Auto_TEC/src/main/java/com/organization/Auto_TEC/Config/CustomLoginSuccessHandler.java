package com.organization.Auto_TEC.Config;

import com.organization.Auto_TEC.Entities.administradorEntitie;
import com.organization.Auto_TEC.Repository.AdministradorRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Optional;

@Component
public class CustomLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AdministradorRepository adminRepo;

    public CustomLoginSuccessHandler(AdministradorRepository adminRepo) {
        this.adminRepo = adminRepo;
    }

    @Override
public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, 
                                    Authentication authentication) throws IOException, ServletException {
    
    // DEBUG: Imprime qué roles está viendo realmente el sistema
    System.out.println("Roles del usuario: " + authentication.getAuthorities());

    // 1. Auditoría
    String login = authentication.getName();
    // ... tu lógica de adminRepo ...

    // 2. Determinar destino
    String targetUrl = "/"; 
    for (GrantedAuthority authority : authentication.getAuthorities()) {
        String role = authority.getAuthority();
        System.out.println("Evaluando rol: " + role); // DEBUG
        
        if (role.equals("ROLE_ADMIN") || role.equals("ADMIN")) {
            targetUrl = "/admin/dashboard";
            break;
        }
        if (role.equals("ROLE_CLIENTE") || role.equals("CLIENTE")) {
            targetUrl = "/citas";
            break;
        }
    }

    getRedirectStrategy().sendRedirect(request, response, targetUrl);
}
}