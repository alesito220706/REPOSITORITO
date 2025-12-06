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
import com.organization.Auto_TEC.Entities.Rol;
import com.organization.Auto_TEC.Service.CitaService;

import java.util.HashMap;
import java.util.List;
import java.time.OffsetDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.io.IOException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Map;
import com.organization.Auto_TEC.Service.CloudinaryService;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CitaService citaService;

    @Autowired
    private EmpleadoService empleadoService;

    @Autowired
    private FinanciamientoService financiamientoService;

    @Autowired
    private ModeloService modeloService;

    @Autowired
    private CloudinaryService cloudinaryService;

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

        if (totalSolicitudes == 0)
            return 0;
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
    public Object crearModelo(@ModelAttribute modelosEntitie modelo,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request) {
        logger.info("Creando nuevo modelo: {}", modelo.getNombre());
        try {
            Map<String, String> errors = new HashMap<>();
            if (modelo.getNombre() == null || modelo.getNombre().trim().isEmpty()) {
                errors.put("nombre", "El nombre es obligatorio");
            }
            if (modelo.getPrecio() == null || modelo.getPrecio().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                errors.put("precio", "El precio debe ser mayor a 0");
            }
            if (!errors.isEmpty()) {
                if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                    return ResponseEntity.status(400).contentType(MediaType.APPLICATION_JSON)
                            .body(Map.of("errors", errors));
                } else {
                    errors.values().forEach(msg -> redirectAttributes.addFlashAttribute("error", msg));
                    return "redirect:/admin/gestion_autos";
                }
            }

            // If an image file was uploaded, save it and set imagenUrl
            if (imagen != null && !imagen.isEmpty()) {
                // En vez de storeImage, usamos Cloudinary
                String urlCloudinary = cloudinaryService.uploadFile(imagen);
                modelo.setImagenUrl(urlCloudinary);
            }
            modelosEntitie saved = modeloService.save(modelo);
            redirectAttributes.addFlashAttribute("success", "Modelo creado exitosamente");
            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(saved);
            }
        } catch (Exception e) {
            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                return ResponseEntity.status(500).contentType(MediaType.APPLICATION_JSON)
                        .body(Map.of("message", "Error al crear el modelo: " + e.getMessage()));
            }
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
    public Object actualizarModelo(@PathVariable Long id,
            @ModelAttribute modelosEntitie modelo,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request) {
        try {
            Map<String, String> errors = new HashMap<>();
            if (modelo.getNombre() == null || modelo.getNombre().trim().isEmpty()) {
                errors.put("nombre", "El nombre es obligatorio");
            }
            if (modelo.getPrecio() == null || modelo.getPrecio().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                errors.put("precio", "El precio debe ser mayor a 0");
            }
            if (!errors.isEmpty()) {
                if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                    return ResponseEntity.status(400).contentType(MediaType.APPLICATION_JSON)
                            .body(Map.of("errors", errors));
                } else {
                    errors.values().forEach(msg -> redirectAttributes.addFlashAttribute("error", msg));
                    return "redirect:/admin/gestion_autos";
                }
            }
            modelo.setId(id);
            Optional<modelosEntitie> existingOpt = modeloService.findById(id);
            if (imagen != null && !imagen.isEmpty()) {
                // Si suben nueva imagen, la mandamos a la nube
                String urlCloudinary = cloudinaryService.uploadFile(imagen);
                modelo.setImagenUrl(urlCloudinary);
            } else if (existingOpt.isPresent()) {
                // Si no suben imagen, mantenemos la URL que ya tenía en la BD
                modelo.setImagenUrl(existingOpt.get().getImagenUrl());
            }

            modelosEntitie saved = modeloService.save(modelo);
            redirectAttributes.addFlashAttribute("success", "Modelo actualizado exitosamente");
            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(saved);
            }
        } catch (Exception e) {
            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                return ResponseEntity.status(500).contentType(MediaType.APPLICATION_JSON)
                        .body(Map.of("message", "Error al actualizar el modelo: " + e.getMessage()));
            }
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el modelo: " + e.getMessage());
        }
        return "redirect:/admin/gestion_autos";
    }

    /*
     * private String storeImage(MultipartFile imagen) throws IOException {
     * if (imagen == null || imagen.isEmpty())
     * return null;
     * String contentType = imagen.getContentType() == null ? "" :
     * imagen.getContentType().toLowerCase();
     * if (!contentType.startsWith("image/")) {
     * throw new IOException("Tipo de archivo no soportado: " + contentType);
     * }
     * // Compute absolute path under the application root to avoid tomcat temp
     * // Prefer a stable folder under user home instead of application/work dir, to
     * // avoid Tomcat temp issues
     * String baseDir = System.getProperty("user.home");
     * Path uploadPath = Paths.get(baseDir, "Auto_TEC_uploads", "images");
     * if (!Files.exists(uploadPath))
     * Files.createDirectories(uploadPath);
     * String original = StringUtils.cleanPath(imagen.getOriginalFilename());
     * if (original.contains("..")) {
     * throw new IOException("Nombre de archivo inválido: " + original);
     * }
     * String filename = System.currentTimeMillis() + "_" +
     * original.replaceAll("\\s+", "_");
     * Path out = uploadPath.resolve(filename);
     * // Use stream-copy (safer than transferTo when the server uses temp dirs)
     * try (java.io.InputStream in = imagen.getInputStream()) {
     * Files.copy(in, out, StandardCopyOption.REPLACE_EXISTING);
     * }
     * // Return a web resource path
     * return "/images/" + filename;
     * }
     */

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

    // ========== MIGRAR IMAGENES (ADMIN) ==========
    @PostMapping("/gestion_autos/migrate-images")
    @ResponseBody
    public ResponseEntity<?> migrateImages(
            @RequestParam(value = "dryRun", required = false, defaultValue = "true") boolean dryRun) {
        List<modelosEntitie> modelos = modeloService.findAll();
        String baseDir = System.getProperty("user.home");
        Path uploadPath = Paths.get(baseDir, "Auto_TEC_uploads", "images");
        try {
            if (!Files.exists(uploadPath))
                Files.createDirectories(uploadPath);
        } catch (IOException e) {
            return ResponseEntity.status(500)
                    .body(Map.of("message", "No se puede crear carpeta Auto_TEC_uploads/images: " + e.getMessage()));
        }
        List<Map<String, Object>> processed = new java.util.ArrayList<>();
        for (modelosEntitie m : modelos) {
            logger.debug("Checking modelo id={} nombre={}", m.getId(), m.getNombre());
            String url = m.getImagenUrl();
            Map<String, Object> entry = new HashMap<>();
            entry.put("id", m.getId());
            entry.put("oldUrl", url);
            if (url == null || url.trim().isEmpty()) {
                entry.put("status", "no-image");
                processed.add(entry);
                continue;
            }
            String lower = url.toLowerCase();
            // skip remote URLs
            if (lower.startsWith("http://") || lower.startsWith("https://")) {
                entry.put("status", "remote_url_skipped");
                processed.add(entry);
                continue;
            }
            // Already in /images/ - skip
            if (url.startsWith("/images/")) {
                entry.put("status", "already_images");
                processed.add(entry);
                continue;
            }
            // normalize file: prefix
            String candidate = url;
            if (candidate.startsWith("file:"))
                candidate = candidate.substring(5);
            // attempt to handle relative or absolute path
            Path sourcePath = Paths.get(candidate);
            if (!sourcePath.isAbsolute()) {
                // often Tomcat work paths contain 'work' or 'Catalina' - try to guess absolute
                // path if not
                if (candidate.contains("work") || candidate.contains("Catalina") || candidate.contains("temp")
                        || candidate.contains("tmp")) {
                    // try to resolve from the JVM temp dir
                    String tmp = System.getProperty("java.io.tmpdir");
                    Path guess = Paths.get(tmp, candidate);
                    if (Files.exists(guess))
                        sourcePath = guess;
                }
            }
            boolean found = Files.exists(sourcePath) && Files.isReadable(sourcePath);
            if (!found) {
                // try to find by filename under Tomcat work or default temp
                String name = sourcePath.getFileName() != null ? sourcePath.getFileName().toString() : null;
                if (name != null) {
                    // search under temp, project root, and upload folder for that filename
                    Path tmp = Paths.get(System.getProperty("java.io.tmpdir"));
                    Path guess1 = tmp.resolve(name);
                    if (Files.exists(guess1)) {
                        sourcePath = guess1;
                        found = true;
                    } else {
                        // check under project 'target/classes/static/images' (project dir) for the
                        // filename
                        Path projectRoot = Paths.get(System.getProperty("user.dir"));
                        Path guess2 = projectRoot.resolve(Paths.get("target", "classes", "static", "images", name));
                        if (Files.exists(guess2)) {
                            sourcePath = guess2;
                            found = true;
                        } else {
                            // also check the new upload folder
                            Path guess3 = uploadPath.resolve(name);
                            if (Files.exists(guess3)) {
                                sourcePath = guess3;
                                found = true;
                            }
                        }
                    }
                }
            }
            if (!found) {
                logger.debug("Source file not found for modelo id={} url={}", m.getId(), url);
                entry.put("status", "not_found");
                processed.add(entry);
                continue;
            }
            // copy file
            String safeName = System.currentTimeMillis() + "_"
                    + sourcePath.getFileName().toString().replaceAll("\\\s+", "_");
            Path dest = uploadPath.resolve(safeName);
            try {
                if (!dryRun)
                    Files.copy(sourcePath, dest, StandardCopyOption.REPLACE_EXISTING);
                String newUrl = "/images/" + safeName;
                entry.put("newUrl", newUrl);
                entry.put("status", "copied");
                if (!dryRun) {
                    m.setImagenUrl(newUrl);
                    modeloService.save(m);
                }
            } catch (IOException ex) {
                logger.error("Error copying image for modelo id={}: {}", m.getId(), ex.getMessage());
                entry.put("status", "error_copying");
                entry.put("message", ex.getMessage());
            }
            processed.add(entry);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("dryRun", dryRun);
        result.put("processed", processed);
        result.put("total", processed.size());
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(result);
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
            usuario.setRol(usuarioService.findRolById(null));

            String rawPassword = usuario.getPasswordHash();
            usuario.setPasswordHash(passwordEncoder.encode(rawPassword));

            if (usuario.getDepartamento() == null) {

            }
            usuarioService.save(usuario);
            redirectAttributes.addFlashAttribute("mensaje", "Administrador guardado exitosamente");
            return "redirect:/admin/gestion_clientes";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar el administrador: " + e.getMessage());
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
        model.addAttribute("tiposCita", citaTipo.values());
        model.addAttribute("estadosCita", citaEstado.values());
        model.addAttribute("usuarios", usuarioService.findAll());
        model.addAttribute("empleados", empleadoService.findAll());
        return "admin/gestion_citas";
    }

    // Prevenir binding automático de fechaCita para formularios de citas
    @org.springframework.web.bind.annotation.InitBinder
    public void initBinderCita(org.springframework.web.bind.WebDataBinder binder) {
        String objectName = binder.getObjectName();
        if ("cita".equals(objectName) || "citaEntitie".equals(objectName)) {
            binder.setDisallowedFields("fechaCita");
        }
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
            @RequestParam(required = false) Long usuarioId,
            @RequestParam(required = false) Long empleadoId,
            @RequestParam String fechaCita,
            @RequestParam String horaCita,
            RedirectAttributes redirectAttributes) {
        try {
            // Obtener la cita existente
            Optional<citaEntitie> citaExistenteOpt = citaService.obtenerPorId(id);
            if (!citaExistenteOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Cita no encontrada");
                return "redirect:/admin/gestion_citas";
            }

            citaEntitie citaExistente = citaExistenteOpt.get();

            // Actualizar usuario si se proporciona
            if (usuarioId != null) {
                Optional<usuarioEntitie> usuarioOpt = usuarioService.findById(usuarioId);
                if (usuarioOpt.isPresent()) {
                    citaExistente.setUsuario(usuarioOpt.get());
                }
            }

            // Actualizar empleado si se proporciona
            if (empleadoId != null) {
                Optional<empleadoEntitie> empleadoOpt = empleadoService.findById(empleadoId);
                if (empleadoOpt.isPresent()) {
                    citaExistente.setEmpleado(empleadoOpt.get());
                }
            }

            // Combinar fecha y hora en LocalDateTime
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime fechaHora = LocalDateTime.parse(fechaCita + " " + horaCita, formatter);
            citaExistente.setFechaCita(fechaHora);

            // Actualizar otros campos
            if (cita.getTipoCita() != null) {
                citaExistente.setTipoCita(cita.getTipoCita());
            }
            if (cita.getDuracionEstimada() != null) {
                citaExistente.setDuracionEstimada(cita.getDuracionEstimada());
            }
            if (cita.getEstado() != null) {
                citaExistente.setEstado(cita.getEstado());
            }
            if (cita.getNotas() != null) {
                citaExistente.setNotas(cita.getNotas());
            }

            // Guardar cambios
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
    public String guardarEmpleado(@ModelAttribute empleadoEntitie empleado,
            @RequestParam(name = "roles_id", required = false) Long rolId,
            RedirectAttributes redirectAttributes) {
        try {
            // Asignar el rol al empleado
            if (rolId != null) {
                Rol rol = usuarioService.findRolById(rolId);
                if (rol != null) {
                    empleado.setRol(rol);
                } else {
                    redirectAttributes.addFlashAttribute("error", "Rol no encontrado");
                    return "redirect:/admin/gestion_empleados";
                }
            } else {
                redirectAttributes.addFlashAttribute("error", "Debe seleccionar un rol");
                return "redirect:/admin/gestion_empleados";
            }

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
        double porcentajeCitasPendientes = totalCitas > 0 ? ((double) citasPendientes / totalCitas) * 100 : 0.0;

        double porcentajeCitasCompletadas = totalCitas > 0 ? ((double) citasCompletadas / totalCitas) * 100 : 0.0;

        double porcentajeSolicitudesAprobadas = totalSolicitudes > 0
                ? ((double) solicitudesAprobadas / totalSolicitudes) * 100
                : 0.0;

        // Agregar porcentajes al modelo
        model.addAttribute("porcentajeCitasPendientes", Math.round(porcentajeCitasPendientes * 10.0) / 10.0);
        model.addAttribute("porcentajeCitasCompletadas", Math.round(porcentajeCitasCompletadas * 10.0) / 10.0);
        model.addAttribute("porcentajeSolicitudesAprobadas", Math.round(porcentajeSolicitudesAprobadas * 10.0) / 10.0);
    }

    // Métodos auxiliares actualizados

    private String calcularTasaAsistencia() {
        int totalCitas = citaService.obtenerTodas() != null ? citaService.obtenerTodas().size() : 0;
        int citasCompletadas = citaService.obtenerCompletadas() != null ? citaService.obtenerCompletadas().size() : 0;

        if (totalCitas == 0)
            return "0%";

        double tasa = ((double) citasCompletadas / totalCitas) * 100;
        return String.format("%.1f%%", tasa);
    }

    private String calcularTasaConversion() {
        int totalSolicitudes = financiamientoService.obtenerTodas() != null
                ? financiamientoService.obtenerTodas().size()
                : 0;
        int solicitudesAprobadas = financiamientoService.obtenerPorEstado("APROBADO") != null
                ? financiamientoService.obtenerPorEstado("APROBADO").size()
                : 0;

        if (totalSolicitudes == 0)
            return "0%";

        double tasa = ((double) solicitudesAprobadas / totalSolicitudes) * 100;
        return String.format("%.1f%%", tasa);
    }
}