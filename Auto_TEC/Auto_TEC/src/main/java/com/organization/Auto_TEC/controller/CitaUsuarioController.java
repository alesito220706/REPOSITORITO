package com.organization.Auto_TEC.controller;

import com.organization.Auto_TEC.Entities.*;
import com.organization.Auto_TEC.Service.CitaService;
import com.organization.Auto_TEC.Service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Controller
@RequestMapping("/ventas")
public class CitaUsuarioController {

    @Autowired
    private CitaService citaService;

    @Autowired
    private UsuarioService usuarioService;

    // ========== SOLICITAR CITA - VISTA USUARIO ==========

    @GetMapping("/solicitar")
    public String mostrarFormularioCita(Model model) {
        model.addAttribute("tiposCita", citaTipo.values());
        return "ventas";
    }

    @PostMapping("/solicitar")
    public String solicitarCita(@RequestParam String nombre,
                              @RequestParam String email,
                              @RequestParam String telefono,
                              @RequestParam citaTipo tipoCita,
                              @RequestParam String fecha,
                              @RequestParam String hora,
                              @RequestParam(required = false) String notas,
                              RedirectAttributes redirectAttributes) {
        try {
            // Buscar o crear usuario
            Optional<usuarioEntitie> usuarioOpt = usuarioService.findByEmail(email);
            usuarioEntitie usuario;
            
            if (usuarioOpt.isPresent()) {
                usuario = usuarioOpt.get();
                // Actualizar datos si es necesario
                if (usuario.getNombres() == null || !usuario.getNombres().equals(nombre)) {
                    usuario.setNombres(nombre);
                }
                if (usuario.getTelefono() == null || !usuario.getTelefono().equals(telefono)) {
                    usuario.setTelefono(telefono);
                }
                usuario = usuarioService.save(usuario);
            } else {
                // Crear nuevo usuario/cliente
                usuario = new usuarioEntitie();
                usuario.setNombres(nombre);
                usuario.setEmail(email);
                usuario.setTelefono(telefono);
                usuario.setActivo(true);
                usuario = usuarioService.save(usuario);
            }

            // Crear cita
            citaEntitie cita = new citaEntitie();
            cita.setUsuario(usuario);
            cita.setTipoCita(tipoCita);
            
            // Convertir fecha y hora
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime fechaHora = LocalDateTime.parse(fecha + " " + hora, formatter);
            cita.setFechaCita(fechaHora);
            
            cita.setDuracionEstimada(60);
            cita.setEstado(citaEstado.PENDIENTE);
            cita.setNotas(notas);

            citaService.guardar(cita);
            
            redirectAttributes.addFlashAttribute("success", 
                "¡Cita solicitada exitosamente! Te contactaremos para confirmar.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error al solicitar cita: " + e.getMessage());
        }
        return "redirect:/ventas";
    }

    // ========== MIS CITAS - VISTA USUARIO ==========

    @GetMapping("/mis-citas")
    public String misCitas(@RequestParam(required = false) String email, Model model) {
        if (email != null && !email.trim().isEmpty()) {
            Optional<usuarioEntitie> usuarioOpt = usuarioService.findByEmail(email);
            if (usuarioOpt.isPresent()) {
                List<citaEntitie> citas = citaService.obtenerPorUsuario(usuarioOpt.get().getId());
                model.addAttribute("citas", citas);
                model.addAttribute("usuario", usuarioOpt.get());
            } else {
                model.addAttribute("error", "No se encontraron citas para este email");
            }
        }
        return "/ventas ";
    }
}