package com.organization.Auto_TEC.Config;

import java.io.IOException;
import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                      HttpServletResponse response, 
                                      Authentication authentication) throws IOException, ServletException {

                                        String targetUrl = determineTargetUrl(authentication);
        
        // Redirigir al usuario
        response.sendRedirect(request.getContextPath() + targetUrl);
    }


        protected String determineTargetUrl(Authentication authentication) {
        
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        
        // Prioridad 1: ADMIN
        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().equals("ROLE_ADMIN")) {
                // ...
                return "/admin/dashboard";
            }
        }
        
        // Prioridad 2: USER (Clientes) - ESTA ES LA CLAVE
        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().equals("ROLE_CLIENTE")) {
                System.out.println("Redirigiendo a página de citas para clientes");
                return "/citas"; // 👈 Redirección al área de citas
            }
        }
        
        // ...
        return "/index";
    }
        
        
}
    
