package com.organization.Auto_TEC.controller;

import com.organization.Auto_TEC.Entities.financiamientoSolicitud;
import com.organization.Auto_TEC.Entities.financiamientoEstadosolicitud;
import com.organization.Auto_TEC.Entities.usuarioEntitie;
import com.organization.Auto_TEC.Service.FinanciamientoService;
import com.organization.Auto_TEC.Service.UsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/financiamiento")
public class FinanciamientoPublicController {

    @Autowired
    private FinanciamientoService financiamientoService;

    @Autowired
    private UsuarioService usuarioService;

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

            // Crear nueva solicitud de financiamiento
            financiamientoSolicitud solicitud = new financiamientoSolicitud();
            solicitud.setNombreSolicitante(nombre.trim());
            solicitud.setEmailSolicitante(email.trim());
            solicitud.setModeloInteres(modelo);
            solicitud.setMensaje(mensaje);
            solicitud.setPlanFinanciamiento(plan);
            solicitud.setEstado(financiamientoEstadosolicitud.PENDIENTE);

            // Intentar asociar con usuario autenticado si existe
            try {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
                    String username = auth.getName();
                    Optional<usuarioEntitie> usuarioOpt = usuarioService.findByEmail(username);
                    usuarioOpt.ifPresent(solicitud::setUsuario);
                }
            } catch (Exception e) {
                // Si falla la autenticación, continuar sin asociar usuario
                System.out.println("No se pudo asociar usuario autenticado: " + e.getMessage());
            }

            // Guardar en la base de datos
            financiamientoService.guardar(solicitud);

            System.out.println("Solicitud guardada exitosamente en la base de datos:");
            System.out.println("Nombre: " + nombre);
            System.out.println("Email: " + email);
            System.out.println("Modelo: " + modelo);

            redirectAttributes.addFlashAttribute("success",
                    "¡Solicitud enviada correctamente! Nos pondremos en contacto contigo pronto.");
        } catch (Exception e) {
            System.err.println("Error al guardar solicitud: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error",
                    "Error al enviar la solicitud. Por favor, intenta nuevamente.");
        }

        return "redirect:/financiamiento";
    }
}