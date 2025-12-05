package com.organization.Auto_TEC.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.validation.BindingResult;
import com.organization.Auto_TEC.DTO.CitaDTO;
import com.organization.Auto_TEC.Entities.citaTipo;
import com.organization.Auto_TEC.Service.CitaService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/citas")
public class CitasController {

    private final CitaService citaService;

    public CitasController(CitaService citaService) {
        this.citaService = citaService;
    }

    @GetMapping
    public String mostrarFormularioCita(Model model) {
        if (!model.containsAttribute("cita")) {
            model.addAttribute("cita", new CitaDTO());
        }
        model.addAttribute("tiposCita", citaTipo.values());

        return "page/citas";
    }

    @PostMapping("/agendar")
    public String agendarCita(@Valid @ModelAttribute("cita") CitaDTO dto,
            BindingResult result,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) {

        try {
            String usernameOrEmail = authentication.getName();

            citaService.agendarCita(dto, usernameOrEmail);

            redirectAttributes.addFlashAttribute("success",
                    "✅ Tu cita ha sido solicitada exitosamente. Revisa tu correo para la confirmación.");

            return "redirect:/citas";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ Error al agendar la cita: " + e.getMessage());

            redirectAttributes.addFlashAttribute("cita", dto);

            return "redirect:/citas";
        }
    }
}