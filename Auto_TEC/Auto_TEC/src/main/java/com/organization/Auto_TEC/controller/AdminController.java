package com.organization.Auto_TEC.controller;

import com.organization.Auto_TEC.Service.EmpleadoService;
import com.organization.Auto_TEC.Service.FinanciamientoService;
import com.organization.Auto_TEC.Service.ModeloService;
import com.organization.Auto_TEC.Service.UsuarioService;
import com.organization.Auto_TEC.Entities.citaEntitie;
import com.organization.Auto_TEC.Entities.citaEstado;
import com.organization.Auto_TEC.Entities.citaTipo;
import com.organization.Auto_TEC.Entities.empleadoEntitie;
import com.organization.Auto_TEC.Entities.financiamientoSolicitud;
import com.organization.Auto_TEC.Entities.financiamientoEstadosolicitud;
import com.organization.Auto_TEC.Entities.modelosEntitie;
import com.organization.Auto_TEC.Entities.usuarioEntitie;
import com.organization.Auto_TEC.Service.CitaService;

import java.util.HashMap;
import java.util.List;
import java.time.OffsetDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Map;
@Controller
@RequestMapping("/admin")
public class AdminController { 
    
    @Autowired
    private UsuarioService usuarioService;
    
  @Autowired
private CitaService citaService;

    @Autowired
    private EmpleadoService empleadoService;
    
    @Autowired
    private FinanciamientoService financiamientoService;

 @Autowired
    private ModeloService modeloService;
    // ========== DASHBOARD ==========
    // ========== DASHBOARD MEJORADO ==========
@GetMapping("/dashboard")
public String dashboard(Model model) {
    // Estadísticas básicas
    model.addAttribute("totalModelos", modeloService.findAll().size());
    model.addAttribute("totalClientes", usuarioService.findAll().size());
    model.addAttribute("totalCitas", citaService.obtenerTodas().size());
    model.addAttribute("totalEmpleados", empleadoService.findAll().size());
    model.addAttribute("citasPendientes", citaService.obtenerPendientes().size());
    model.addAttribute("solicitudesPendientes", financiamientoService.obtenerPendientes().size());
    
    // Métricas adicionales para el dashboard mejorado
    model.addAttribute("modelosActivos", modeloService.findByActivoTrue().size());
    model.addAttribute("citasHoy", obtenerCitasHoy());
    model.addAttribute("solicitudesNuevas", obtenerSolicitudesNuevas());
    model.addAttribute("tasaConversion", calcularTasaConversionDashboard());
    model.addAttribute("fechaUltimoAcceso", obtenerFechaUltimoAcceso());
    
    return "admin/dashboard";
}

// Métodos auxiliares para el dashboard
private int obtenerCitasHoy() {
    // Implementar lógica para obtener citas de hoy
    return citaService.obtenerCitasHoy().size();
}

private int obtenerSolicitudesNuevas() {
    // Implementar lógica para obtener solicitudes de los últimos 7 días
    return financiamientoService.obtenerSolicitudesRecientes().size();
}

private int calcularTasaConversionDashboard() {
    // Lógica simplificada para tasa de conversión
    int totalSolicitudes = financiamientoService.obtenerTodas().size();
    int aprobadas = financiamientoService.contarPorEstado("APROBADO");
    
    if (totalSolicitudes == 0) return 0;
    return (aprobadas * 100) / totalSolicitudes;
}

private String obtenerFechaUltimoAcceso() {
    // Formatear fecha de último acceso
    return java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
}
    
    // ========== GESTIÓN DE MODELOS ==========
    
     @GetMapping("/gestion_autos")
public String adminModelos(Model model) {
    List<modelosEntitie> modelos = modeloService.findAll();
    model.addAttribute("modelos", modelos);
    model.addAttribute("modelo", new modelosEntitie()); 
    return "admin/gestion_autos";
}
@PostMapping("/guardar")
public String guardar(modelosEntitie modelo) {
    modelo.setActivo(true); 
    modeloService.save(modelo);
    return "redirect:admin/gestion_autos";
}
    @PostMapping("/gestion_autos/crear")
    public String crearModelo(@ModelAttribute modelosEntitie modelo, 
                            RedirectAttributes redirectAttributes) {
        try {
if (modelo.getNombre() == null || modelo.getNombre().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "El nombre es obligatorio");
                return "redirect:/admin/gestion_autos";
            }

            if (modelo.getPrecio() == null || modelo.getPrecio().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                redirectAttributes.addFlashAttribute("error", "El precio debe ser mayor a 0");
                return "redirect:/admin/gestion_autos";
            }
            
            modeloService.save(modelo);
  redirectAttributes.addFlashAttribute("success", "Modelo creado exitosamente");
        } catch (Exception e) {
 redirectAttributes.addFlashAttribute("error", "Error al crear el modelo: " + e.getMessage());
        }
        return "redirect:/admin/gestion_autos";
    }

  @GetMapping("/gestion_autos/editar/{id}")
    public String paginaEditar(@PathVariable Long id, Model model) {
        Optional<modelosEntitie> modeloOpt = modeloService.findById(id);
        if (modeloOpt.isPresent()) {
            model.addAttribute("modelo", modeloOpt.get());
            return "admin/editar_modelo"; // Página separada para editar
        }
      
        return "redirect:/admin/gestion_autos";
    }
 @PostMapping("/gestion_autos/actualizar/{id}")
    public String actualizarModelo(@PathVariable Long id, 
                                 @ModelAttribute modelosEntitie modelo,
                                 RedirectAttributes redirectAttributes) {
        try {
 modelo.setId(id);
            modeloService.save(modelo);
            redirectAttributes.addFlashAttribute("success", "Modelo actualizado exitosamente");
        } catch (Exception e) {
  redirectAttributes.addFlashAttribute("error", "Error al actualizar el modelo: " + e.getMessage());
        }
        return "redirect:/admin/gestion_autos";
    }
// Eliminar modelo
    @PostMapping("/gestion_autos/eliminar/{id}")
    public String eliminarModelo(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            modeloService.deleteById(id);
  redirectAttributes.addFlashAttribute("success", "Modelo eliminado exitosamente");
        } catch (Exception e) {
 redirectAttributes.addFlashAttribute("error", "Error al eliminar el modelo: " + e.getMessage());
        }
        return "redirect:/admin/gestion_autos";
    }

  // Cambiar estado activo/inactivo
    @PostMapping("/gestion_autos/toggle-activo/{id}")
    public String toggleActivo(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
Optional<modelosEntitie> modeloOpt = modeloService.findById(id);
            if (modeloOpt.isPresent()) {
                modelosEntitie modelo = modeloOpt.get();
                modelo.setActivo(!modelo.isActivo());
                modeloService.save(modelo);
                String estado = modelo.isActivo() ? "activado" : "desactivado";
                redirectAttributes.addFlashAttribute("success", "Modelo " + estado + " exitosamente");
            }
 } catch (Exception e) {

redirectAttributes.addFlashAttribute("error", "Error al cambiar estado del modelo");
        }
        return "redirect:/admin/gestion_autos";
    }
    
    // ========== GESTIÓN DE CLIENTES ==========
    
    @GetMapping("/gestion_clientes")
    public String gestionClientes(Model model) {
        model.addAttribute("usuarios", usuarioService.findAll());
        return "admin/gestion_clientes";
    }
    // Agregar cliente
@PostMapping("/clientes/guardar")
public String guardarCliente(@ModelAttribute usuarioEntitie usuario, RedirectAttributes redirectAttributes) {
    try {
        usuarioService.save(usuario);
        redirectAttributes.addFlashAttribute("mensaje", "Cliente guardado exitosamente");
        return "redirect:/admin/gestion_clientes";
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        return "redirect:/admin/gestion_clientes";
    }
}


    @DeleteMapping("/clientes/{id}")
    @ResponseBody
    public Map<String, Boolean> eliminarCliente(@PathVariable Long id) {
        try {
            usuarioService.deleteById(id);
            return Map.of("success", true);
        } catch (Exception e) {
            return Map.of("success", false);
        }
    }
    
    @PutMapping("/clientes/{id}/estado")
@ResponseBody
public Map<String, Boolean> cambiarEstadoCliente(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
    try {
        Optional<usuarioEntitie> usuarioOpt = usuarioService.findById(id);
        if (usuarioOpt.isPresent()) {
            usuarioEntitie usuario = usuarioOpt.get();
            usuario.setActivo(body.get("activo"));
            usuarioService.save(usuario);
            return Map.of("success", true);
        } else {
            return Map.of("success", false);
        }
    } catch (Exception e) {
        return Map.of("success", false);
    }
}
    // ========== GESTIÓN DE CITAS ==========
    

    @GetMapping("/gestion_citas")
    public String gestionCitas(Model model) {
        List<citaEntitie> citas = citaService.obtenerTodas();
        model.addAttribute("citas", citas);
        model.addAttribute("cita", new citaEntitie());
        model.addAttribute("tiposCita", citaTipo.values());
        model.addAttribute("estadosCita", citaEstado.values());
        model.addAttribute("usuarios", usuarioService.findAll());
        model.addAttribute("empleados", empleadoService.findAll());
        return "admin/gestion_citas";
    }

    // ========== CREAR CITA DESDE ADMIN ==========

    @PostMapping("/ventas/crear")
    public String crearCitaDesdeAdmin(@ModelAttribute citaEntitie cita,
                                    @RequestParam Long usuarioId,
                                    @RequestParam(required = false) Long empleadoId,
                                    @RequestParam String fechaCita,
                                    @RequestParam String horaCita,
                                    RedirectAttributes redirectAttributes) {
        try {
            // Buscar usuario
            Optional<usuarioEntitie> usuarioOpt = usuarioService.findById(usuarioId);
            if (!usuarioOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
                return "redirect:/admin/gestion_citas";
            }

            // Buscar empleado (opcional)
            Optional<empleadoEntitie> empleadoOpt = Optional.empty();
            if (empleadoId != null) {
                empleadoOpt = empleadoService.findById(empleadoId);
            }

            // Configurar cita
            cita.setUsuario(usuarioOpt.get());
            empleadoOpt.ifPresent(cita::setEmpleado);

            // Convertir fecha y hora
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime fechaHora = LocalDateTime.parse(fechaCita + " " + horaCita, formatter);
            cita.setFechaCita(fechaHora);

            // Valores por defecto
            if (cita.getDuracionEstimada() == null) {
                cita.setDuracionEstimada(60);
            }
            if (cita.getEstado() == null) {
                cita.setEstado(citaEstado.PENDIENTE);
            }

            citaService.guardar(cita);
            redirectAttributes.addFlashAttribute("success", "Cita creada exitosamente");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear cita: " + e.getMessage());
        }
        return "redirect:/admin/gestion_citas";
    }

    // ========== ACTUALIZAR CITA ==========

    @PostMapping("/ventas/actualizar/{id}")
    public String actualizarCita(@PathVariable Long id,
                               @ModelAttribute citaEntitie cita,
                               @RequestParam Long usuarioId,
                               @RequestParam(required = false) Long empleadoId,
                               @RequestParam String fechaCita,
                               @RequestParam String horaCita,
                               RedirectAttributes redirectAttributes) {
        try {
            Optional<citaEntitie> citaExistenteOpt = citaService.obtenerPorId(id);
            if (!citaExistenteOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Cita no encontrada");
                return "redirect:/admin/gestion_citas";
            }

            // Buscar usuario y empleado
            Optional<usuarioEntitie> usuarioOpt = usuarioService.findById(usuarioId);
            if (!usuarioOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
                return "redirect:/admin/gestion_citas";
            }

            Optional<empleadoEntitie> empleadoOpt = Optional.empty();
            if (empleadoId != null) {
                empleadoOpt = empleadoService.findById(empleadoId);
            }

            citaEntitie citaExistente = citaExistenteOpt.get();
            
            // Actualizar datos
            citaExistente.setUsuario(usuarioOpt.get());
            empleadoOpt.ifPresent(citaExistente::setEmpleado);
            citaExistente.setTipoCita(cita.getTipoCita());
            
            // Convertir fecha y hora
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime fechaHora = LocalDateTime.parse(fechaCita + " " + horaCita, formatter);
            citaExistente.setFechaCita(fechaHora);
            
            citaExistente.setDuracionEstimada(cita.getDuracionEstimada());
            citaExistente.setEstado(cita.getEstado());
            citaExistente.setNotas(cita.getNotas());

            citaService.guardar(citaExistente);
            redirectAttributes.addFlashAttribute("success", "Cita actualizada exitosamente");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar cita: " + e.getMessage());
        }
        return "redirect:/admin/gestion_citas";
    }

    // ========== ELIMINAR CITA ==========

    @PostMapping("/ventas/eliminar/{id}")
    public String eliminarCita(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            citaService.eliminar(id);
            redirectAttributes.addFlashAttribute("success", "Cita eliminada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar cita: " + e.getMessage());
        }
        return "redirect:/admin/gestion_citas";
    }

    // ========== CAMBIAR ESTADO CITA ==========

    @PostMapping("/ventas/cambiar-estado/{id}")
    public String cambiarEstadoCita(@PathVariable Long id,
                                  @RequestParam citaEstado nuevoEstado,
                                  RedirectAttributes redirectAttributes) {
        try {
            citaService.cambiarEstado(id, nuevoEstado);
            redirectAttributes.addFlashAttribute("success", "Estado de cita actualizado a: " + nuevoEstado);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cambiar estado: " + e.getMessage());
        }
        return "redirect:/admin/gestion_citas";
    }

    // ========== OBTENER CITA PARA EDITAR (AJAX) ==========

    @GetMapping("/ventas/editar/{id}")
    @ResponseBody
    public citaEntitie obtenerCitaParaEditar(@PathVariable Long id) {
        return citaService.obtenerPorId(id).orElse(null);
    }

    // ========== GESTIÓN DE EMPLEADOS ==========
    
    @GetMapping("/gestion_empleados")
    public String gestionEmpleados(Model model) {
        model.addAttribute("empleados", empleadoService.findAll());
        return "admin/gestion_empleados";
    }
    
    @DeleteMapping("/empleados/{id}")
    @ResponseBody
    public Map<String, Boolean> eliminarEmpleado(@PathVariable Long id) {
        try {
            empleadoService.deleteById(id);
            return Map.of("success", true);
        } catch (Exception e) {
            return Map.of("success", false);
        }
    }
    // Agregar empleado
@PostMapping("/empleados/guardar")
public String guardarEmpleado(@ModelAttribute empleadoEntitie empleado, RedirectAttributes redirectAttributes) {
    try {
        empleadoService.save(empleado);
        redirectAttributes.addFlashAttribute("mensaje", "Empleado guardado exitosamente");
        return "redirect:/admin/gestion_empleados";
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        return "redirect:/admin/gestion_empleados";
    }
}
    
    @PutMapping("/empleados/{id}/estado")
    @ResponseBody
    public Map<String, Boolean> cambiarEstadoEmpleado(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        try {
            empleadoService.cambiarEstado(id, body.get("activo"));
            return Map.of("success", true);
        } catch (Exception e) {
            return Map.of("success", false);
        }
    }
    
    // ========== GESTIÓN DE SOLICITUDES ==========
    
    @GetMapping("/gestion_solicitudes")
    public String gestionSolicitudes(Model model) {
        List<financiamientoSolicitud> solicitudes = financiamientoService.obtenerTodas();
        model.addAttribute("solicitudes", solicitudes);
        model.addAttribute("totalSolicitudes", solicitudes.size());
        model.addAttribute("pendientesCount", financiamientoService.contarPorEstado("PENDIENTE"));
        model.addAttribute("evaluandoCount", financiamientoService.contarPorEstado("EVALUANDO"));
        model.addAttribute("aprobadosCount", financiamientoService.contarPorEstado("APROBADO"));
        model.addAttribute("rechazadosCount", financiamientoService.contarPorEstado("RECHAZADO"));
        
        return "admin/gestion_solicitudes";
    }

    @PostMapping("/solicitudes/cambiar-estado/{id}")
    public String cambiarEstadoSolicitud(@PathVariable Long id, 
                                       @RequestParam String estado,
                                       RedirectAttributes redirectAttributes) {
        try {
            financiamientoService.cambiarEstado(id, estado);
            redirectAttributes.addFlashAttribute("success", "Estado actualizado a: " + estado);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cambiar estado: " + e.getMessage());
        }
        return "redirect:/admin/gestion_solicitudes";
    }

     @PostMapping("/solicitudes/eliminar/{id}")
    public String eliminarSolicitud(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            financiamientoService.eliminar(id);
            redirectAttributes.addFlashAttribute("success", "Solicitud eliminada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar solicitud: " + e.getMessage());
        }
        return "redirect:/admin/gestion_solicitudes";
    }
     @GetMapping("/solicitudes/detalle/{id}")
    @ResponseBody
    public financiamientoSolicitud verDetalleSolicitud(@PathVariable Long id) {
        return financiamientoService.obtenerPorId(id).orElse(null);
    }

    
    // ========== REPORTES ==========
    
 @GetMapping("/reportes")
public String reportes(Model model) {
    // Estadísticas básicas con manejo de null
    List<modelosEntitie> todosModelos = modeloService.findAll();
    List<usuarioEntitie> todosUsuarios = usuarioService.findAll();
    List<citaEntitie> todasCitas = citaService.obtenerTodas();
    List<empleadoEntitie> todosEmpleados = empleadoService.findAll();
    
    model.addAttribute("totalModelos", todosModelos != null ? todosModelos.size() : 0);
    model.addAttribute("totalClientes", todosUsuarios != null ? todosUsuarios.size() : 0);
    model.addAttribute("totalCitas", todasCitas != null ? todasCitas.size() : 0);
    model.addAttribute("totalEmpleados", todosEmpleados != null ? todosEmpleados.size() : 0);
    
    // Modelos con manejo de null
    List<modelosEntitie> modelosActivos = modeloService.findByActivoTrue();
    List<modelosEntitie> modelosDestacados = modeloService.findDestacados();
    
    model.addAttribute("modelosActivos", modelosActivos != null ? modelosActivos.size() : 0);
    model.addAttribute("modelosDestacados", modelosDestacados != null ? modelosDestacados.size() : 0);
   
    
    // Citas con manejo de null
    List<citaEntitie> citasPendientes = citaService.obtenerPendientes();
    List<citaEntitie> citasConfirmadas = citaService.obtenerConfirmadas();
    List<citaEntitie> citasCompletadas = citaService.obtenerCompletadas();
    List<citaEntitie> citasCanceladas = citaService.obtenerCanceladas();
    
    model.addAttribute("citasPendientes", citasPendientes != null ? citasPendientes.size() : 0);
    model.addAttribute("citasConfirmadas", citasConfirmadas != null ? citasConfirmadas.size() : 0);
    model.addAttribute("citasCompletadas", citasCompletadas != null ? citasCompletadas.size() : 0);
    model.addAttribute("citasCanceladas", citasCanceladas != null ? citasCanceladas.size() : 0);
    model.addAttribute("tasaAsistencia", calcularTasaAsistencia());
    
    // Solicitudes de financiamiento con manejo de null
    List<financiamientoSolicitud> todasSolicitudes = financiamientoService.obtenerTodas();
    
    model.addAttribute("totalSolicitudes", todasSolicitudes != null ? todasSolicitudes.size() : 0);
    model.addAttribute("solicitudesPendientes", financiamientoService.contarPorEstado("PENDIENTE"));
    model.addAttribute("solicitudesEvaluando", financiamientoService.contarPorEstado("EVALUANDO"));
    model.addAttribute("solicitudesAprobadas", financiamientoService.contarPorEstado("APROBADO"));
    model.addAttribute("solicitudesRechazadas", financiamientoService.contarPorEstado("RECHAZADO"));
    
    // Métricas adicionales - CALCULAR PORCENTAJES DE FORMA SEGURA
    model.addAttribute("tasaConversion", calcularTasaConversion());
    
    // NUEVO: Calcular porcentajes de forma segura y agregarlos al modelo
    calcularYAgregarPorcentajesSeguros(model);
    
    return "admin/reportes";
}

// Método nuevo para calcular porcentajes de forma segura
private void calcularYAgregarPorcentajesSeguros(Model model) {
    // Obtener valores del modelo
    Integer totalCitas = (Integer) model.getAttribute("totalCitas");
    Integer citasPendientes = (Integer) model.getAttribute("citasPendientes");
    Integer citasCompletadas = (Integer) model.getAttribute("citasCompletadas");
    Integer totalSolicitudes = (Integer) model.getAttribute("totalSolicitudes");
    Integer solicitudesAprobadas = (Integer) model.getAttribute("solicitudesAprobadas");
    
    // Asegurar que no sean null
    totalCitas = totalCitas != null ? totalCitas : 0;
    citasPendientes = citasPendientes != null ? citasPendientes : 0;
    citasCompletadas = citasCompletadas != null ? citasCompletadas : 0;
    totalSolicitudes = totalSolicitudes != null ? totalSolicitudes : 0;
    solicitudesAprobadas = solicitudesAprobadas != null ? solicitudesAprobadas : 0;
    
    // Calcular porcentajes de forma segura
    double porcentajeCitasPendientes = totalCitas > 0 ? 
        ((double) citasPendientes / totalCitas) * 100 : 0.0;
    
    double porcentajeCitasCompletadas = totalCitas > 0 ? 
        ((double) citasCompletadas / totalCitas) * 100 : 0.0;
    
    double porcentajeSolicitudesAprobadas = totalSolicitudes > 0 ? 
        ((double) solicitudesAprobadas / totalSolicitudes) * 100 : 0.0;
    
    // Agregar porcentajes al modelo
    model.addAttribute("porcentajeCitasPendientes", Math.round(porcentajeCitasPendientes * 10.0) / 10.0);
    model.addAttribute("porcentajeCitasCompletadas", Math.round(porcentajeCitasCompletadas * 10.0) / 10.0);
    model.addAttribute("porcentajeSolicitudesAprobadas", Math.round(porcentajeSolicitudesAprobadas * 10.0) / 10.0);
}

// Métodos auxiliares actualizados


private String calcularTasaAsistencia() {
    int totalCitas = citaService.obtenerTodas() != null ? citaService.obtenerTodas().size() : 0;
    int citasCompletadas = citaService.obtenerCompletadas() != null ? citaService.obtenerCompletadas().size() : 0;
    
    if (totalCitas == 0) return "0%";
    
    double tasa = ((double) citasCompletadas / totalCitas) * 100;
    return String.format("%.1f%%", tasa);
}

private String calcularTasaConversion() {
    int totalSolicitudes = financiamientoService.obtenerTodas() != null ? financiamientoService.obtenerTodas().size() : 0;
    int solicitudesAprobadas = financiamientoService.obtenerPorEstado("APROBADO") != null ? 
        financiamientoService.obtenerPorEstado("APROBADO").size() : 0;
    
    if (totalSolicitudes == 0) return "0%";

    double tasa = ((double) solicitudesAprobadas / totalSolicitudes) * 100;
    return String.format("%.1f%%", tasa);
}}