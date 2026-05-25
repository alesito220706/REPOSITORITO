package com.organization.Auto_TEC.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Vistas de usuario (Públicas)
        registry.addViewController("/login").setViewName("page/login");
        registry.addViewController("/registro").setViewName("page/registro");
        registry.addViewController("/contacto").setViewName("page/contacto");
        registry.addViewController("/citas").setViewName("page/citas");
        registry.addViewController("/modelos").setViewName("page/modelos");
        registry.addViewController("/servicios").setViewName("page/servicios");
        registry.addViewController("/financiamiento").setViewName("page/financiamiento");
        registry.addViewController("/ventas").setViewName("page/ventas");
        
        // Vistas de administración (Protegidas por SecurityConfig)
        registry.addViewController("/admin/dashboard").setViewName("admin/dashboard");
        registry.addViewController("/admin/gestion_autos").setViewName("admin/gestion_autos");
        registry.addViewController("/admin/gestion_citas").setViewName("admin/gestion_citas");
        registry.addViewController("/admin/gestion_clientes").setViewName("admin/gestion_clientes");
        registry.addViewController("/admin/gestion_empleados").setViewName("admin/gestion_empleados");
        registry.addViewController("/admin/gestion_solicitudes").setViewName("admin/gestion_solicitudes");
        registry.addViewController("/admin/reportes").setViewName("admin/reportes");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String baseDir = System.getProperty("user.home");
        String uploadDir = Paths.get(baseDir, "Auto_TEC_uploads", "images").toUri().toString();
        registry.addResourceHandler("/images/**")
                .addResourceLocations(uploadDir, "classpath:/static/images/");
    }
}