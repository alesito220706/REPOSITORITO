package com.organization.Auto_TEC.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final CustomLoginSuccessHandler successHandler;

    public SecurityConfig(UserDetailsService userDetailsService, CustomLoginSuccessHandler successHandler) {
        this.userDetailsService = userDetailsService;
        this.successHandler = successHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userDetailsService);
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Desactivado para facilitar pruebas en API
                .authorizeHttpRequests(auth -> auth
                        // 1. Recursos estáticos siempre públicos
                        .requestMatchers("/css/**", "/js/**", "/img/**", "/images/**", "/webjars/**").permitAll()

                        // 2. Rutas de Vistas (Páginas HTML) públicas
                        .requestMatchers("/", "/index", "/login", "/registro", "/auth/**", "/contacto", "/citas", "/modelos",
                                "/servicios", "/ventas")
                        .permitAll()

                        // 3. APIs públicas (Ahora bajo el prefijo /api/)
                        .requestMatchers("/api/auth/**", "/api/cotizaciones/**").permitAll()

                        // 4. Panel de administración solo para ADMIN
// En tu SecurityConfig.java, cambia la línea 4 en el authorizeHttpRequests:
.requestMatchers("/admin/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_ROLE_ADMIN", "ADMIN")
                        // 5. Cualquier otra cosa requiere autenticación
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .successHandler(successHandler) // ¡Necesitas esto para que el Admin vaya al Dashboard!
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll());

        return http.build();
    }
}