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
    
    // LOG DE SEGURIDAD
    System.out.println("DEBUG - Usuario autenticado: " + authentication.getName());
    System.out.println("DEBUG - Autoridades: " + authentication.getAuthorities());

    String targetUrl = "/"; // Si esto es lo que ves, aquí está el problema

    for (GrantedAuthority authority : authentication.getAuthorities()) {
        String role = authority.getAuthority();
        System.out.println("DEBUG - Comparando rol: " + role); 
        
        if (role.contains("ADMIN")) {
            targetUrl = "/admin/dashboard";
            break;
            
        }
    }
    
    getRedirectStrategy().sendRedirect(request, response, targetUrl);
}
}