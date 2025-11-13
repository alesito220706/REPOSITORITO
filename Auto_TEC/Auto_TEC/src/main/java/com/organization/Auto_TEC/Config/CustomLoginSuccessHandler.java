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
        
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        
        System.out.println(" ROLES DEL USUARIO AUTENTICADO:");
        for (GrantedAuthority authority : authorities) {
            System.out.println("   - " + authority.getAuthority());
            if (authority.getAuthority().equals("ROLE_ADMIN")) {
                System.out.println(" Redirigiendo a dashboard admin");
                response.sendRedirect("/admin/dashboard");
                return;
            }
        }
        
        System.out.println(" Redirigiendo a página principal");
        response.sendRedirect("/");
    }
}