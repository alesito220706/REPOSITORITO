package com.organization.Auto_TEC.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Use the same uploads folder used by AdminController: user.home/Auto_TEC_uploads/images
        String baseDir = System.getProperty("user.home");
        String uploadDir = Paths.get(baseDir, "Auto_TEC_uploads", "images").toUri().toString();
        // Serve uploaded images first and fall back to classpath resources (static/images)
        registry.addResourceHandler("/images/**")
            .addResourceLocations(uploadDir, "classpath:/static/images/");
    }
}
