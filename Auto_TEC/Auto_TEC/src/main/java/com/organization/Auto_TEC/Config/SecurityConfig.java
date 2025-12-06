package com.organization.Auto_TEC.Config;

import org.springframework.beans.factory.annotation.Autowired; // Importar
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy; // Importar
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // Importar

import com.organization.Auto_TEC.Service.CombinedUserDetailsService;
// Asegúrate de importar tu filtro JWT (creado en el paso anterior)
import com.organization.Auto_TEC.Config.JwtAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final CombinedUserDetailsService combinedUserDetailsService;

    // Inyectamos el filtro JWT
    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(CombinedUserDetailsService combinedUserDetailsService) {
        this.combinedUserDetailsService = combinedUserDetailsService;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(combinedUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authenticationProvider(authenticationProvider())

                // CONFIGURACIÓN DE FILTRO JWT
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                        .requestMatchers("/", "/animacion", "/index", "/contacto", "/gestion",
                                "/login", "/modelos", "/registro", "/servicios",
                                "/ventas", "/financiamiento", "/error",
                                "/auth/**", "/api/auth/**")
                        .permitAll()
                        .requestMatchers("/citas/**").hasAnyRole("CLIENTE", "ADMIN")
                        .requestMatchers("/dashboard/**", "/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().authenticated())
                // GESTIÓN DE SESIÓN HÍBRIDA
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/auth/login")
                        .successHandler(customAuthenticationSuccessHandler())
                        .failureUrl("/auth/login?error=true")
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessUrl("/")
                        .permitAll());

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return new CustomLoginSuccessHandler();
    }
}