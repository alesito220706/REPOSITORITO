package com.organization.Auto_TEC.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/financiamiento")
public class FinanciamientoPublicController {

    @GetMapping
    public String mostrarFormularioFinanciamiento(Model model) {
        System.out.println("=== ACCEDIENDO A /financiamiento ===");
        return "page/financiamiento"; // ← Cambiado para buscar en subcarpeta
    }

    @PostMapping("/solicitar")
    public String procesarSolicitud(
            @RequestParam String nombre,
            @RequestParam String email,
            @RequestParam String modelo,
            @RequestParam String mensaje,
            @RequestParam(required = false) String plan,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Validaciones básicas
            if (nombre == null || nombre.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "El nombre es obligatorio");
                return "redirect:/financiamiento";
            }
            
            if (email == null || email.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "El email es obligatorio");
                return "redirect:/financiamiento";
            }

            // Aquí iría la lógica para guardar en la base de datos
            System.out.println("Solicitud recibida:");
            System.out.println("Nombre: " + nombre);
            System.out.println("Email: " + email);
            System.out.println("Modelo: " + modelo);
            System.out.println("Mensaje: " + mensaje);
            System.out.println("Plan: " + plan);
            
            redirectAttributes.addFlashAttribute("success", "¡Solicitud enviada correctamente! Nos pondremos en contacto contigo pronto.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al enviar la solicitud. Por favor, intenta nuevamente.");
        }
        
        return "redirect:/financiamiento";
    }
}