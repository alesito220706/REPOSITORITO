package com.organization.Auto_TEC.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.organization.Auto_TEC.Entities.*;
import com.organization.Auto_TEC.Repository.*;
import com.organization.Auto_TEC.Service.*;

@Controller
@RequestMapping("/mecanico")
public class MecanicoController {

    private static final Logger logger = LoggerFactory.getLogger(MecanicoController.class);

    @Autowired private CitaService citaService;
    @Autowired private RepuestosRepository repuestosRepository;
    @Autowired private DiagnosticoRepository diagnosticoRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<citaEntitie> todasLasCitas = citaService.obtenerTodas();
        List<DiagnosticoEntitie> diagnosticos = diagnosticoRepository.findAll();
        long diagnosticosPendientes = diagnosticos.stream().filter(d -> "BORRADOR".equals(d.getEstado())).count();
        long citasPendientesCount = todasLasCitas.stream().filter(c -> c.getEstado() == citaEstado.PENDIENTE).count();
        long repuestosBajos = repuestosRepository.findDisponibles().stream().filter(r -> r.getStock() < 5).count();

        model.addAttribute("citasPendientes", citasPendientesCount);
        model.addAttribute("citasAsignadas", todasLasCitas.size());
        model.addAttribute("diagnosticos", diagnosticos);
        model.addAttribute("diagnosticosPendientes", diagnosticosPendientes);
        model.addAttribute("repuestosBajos", repuestosBajos);
        model.addAttribute("totalRepuestos", repuestosRepository.count());
        model.addAttribute("citasRecientes", todasLasCitas.stream().limit(5).toList());

        return "mecanico/dashboard";
    }

    @GetMapping("/repuestos")
    public String repuestos(Model model, @RequestParam(required = false) String q) {
        List<RepuestosEntitie> repuestos;
        if (q != null && !q.isBlank()) {
            repuestos = repuestosRepository.buscar(q);
            model.addAttribute("query", q);
        } else {
            repuestos = repuestosRepository.findAll();
        }
        model.addAttribute("repuestos", repuestos);
        model.addAttribute("repuesto", new RepuestosEntitie());
        return "mecanico/repuestos";
    }

    @PostMapping("/repuestos/guardar")
    public String guardarRepuesto(@ModelAttribute RepuestosEntitie repuesto, RedirectAttributes ra) {
        try {
            repuestosRepository.save(repuesto);
            ra.addFlashAttribute("success", "Repuesto guardado exitosamente");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/mecanico/repuestos";
    }

    @PostMapping("/repuestos/actualizar/{id}")
    public String actualizarRepuesto(@PathVariable Long id, @ModelAttribute RepuestosEntitie repuesto, RedirectAttributes ra) {
        try {
            repuesto.setId(id);
            repuestosRepository.save(repuesto);
            ra.addFlashAttribute("success", "Repuesto actualizado");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/mecanico/repuestos";
    }

    @GetMapping("/repuestos/editar/{id}")
    @ResponseBody
    public RepuestosEntitie editarRepuesto(@PathVariable Long id) {
        return repuestosRepository.findById(id).orElse(null);
    }

    @PostMapping("/repuestos/eliminar/{id}")
    public String eliminarRepuesto(@PathVariable Long id, RedirectAttributes ra) {
        try {
            repuestosRepository.deleteById(id);
            ra.addFlashAttribute("success", "Repuesto eliminado");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/mecanico/repuestos";
    }

    @GetMapping("/diagnostico/nuevo/{citaId}")
    public String nuevoDiagnostico(@PathVariable Long citaId, Model model) {
        var citaOpt = citaService.obtenerPorId(citaId);
        if (citaOpt.isEmpty()) return "redirect:/mecanico/dashboard";
        model.addAttribute("cita", citaOpt.get());
        model.addAttribute("diagnostico", new DiagnosticoEntitie());
        model.addAttribute("repuestos", repuestosRepository.findDisponibles());
        return "mecanico/diagnostico";
    }

    @PostMapping("/diagnostico/guardar")
    public String guardarDiagnostico(@RequestParam Long citaId,
            @RequestParam String observaciones,
            @RequestParam(required = false) String fallasEncontradas,
            @RequestParam(required = false) String recomendaciones,
            @RequestParam(required = false) String accionesSeguir,
            @RequestParam(required = false) String accion,
            @RequestParam(required = false) List<Long> repuestoIds,
            @RequestParam(required = false) List<Integer> repuestoCantidades,
            RedirectAttributes ra) {
        try {
            var citaOpt = citaService.obtenerPorId(citaId);
            if (citaOpt.isEmpty()) {
                ra.addFlashAttribute("error", "Cita no encontrada");
                return "redirect:/mecanico/dashboard";
            }

            DiagnosticoEntitie diag = new DiagnosticoEntitie();
            diag.setCita(citaOpt.get());
            diag.setObservaciones(observaciones);
            diag.setFallasEncontradas(fallasEncontradas);
            diag.setRecomendaciones(recomendaciones);
            diag.setAccionesSeguir(accionesSeguir);
            diag.setEstado("COMPLETADO".equals(accion) ? "COMPLETADO" : "BORRADOR");

            if (repuestoIds != null && repuestoCantidades != null) {
                for (int i = 0; i < repuestoIds.size(); i++) {
                    Long rId = repuestoIds.get(i);
                    int cant = (i < repuestoCantidades.size()) ? repuestoCantidades.get(i) : 1;
                    var rOpt = repuestosRepository.findById(rId);
                    if (rOpt.isPresent()) {
                        RepuestoAsignadoEntitie rae = new RepuestoAsignadoEntitie();
                        rae.setDiagnostico(diag);
                        rae.setRepuesto(rOpt.get());
                        rae.setCantidad(cant);
                        rae.setPrecioUnitario(rOpt.get().getPrecio());
                        diag.getRepuestosAsignados().add(rae);
                    }
                }
            }

            diagnosticoRepository.save(diag);
            ra.addFlashAttribute("success", "Diagnóstico guardado exitosamente");
            return "redirect:/mecanico/dashboard";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al guardar diagnóstico: " + e.getMessage());
            return "redirect:/mecanico/dashboard";
        }
    }

    @GetMapping("/diagnostico/ver/{id}")
    public String verDiagnostico(@PathVariable Long id, Model model) {
        var diagOpt = diagnosticoRepository.findById(id);
        if (diagOpt.isEmpty()) return "redirect:/mecanico/dashboard";
        model.addAttribute("diag", diagOpt.get());
        return "mecanico/diagnostico_ver";
    }

    @GetMapping("/orden/{diagnosticoId}")
    public String ordenServicio(@PathVariable Long diagnosticoId, Model model) {
        var diagOpt = diagnosticoRepository.findById(diagnosticoId);
        if (diagOpt.isEmpty()) return "redirect:/mecanico/dashboard";

        DiagnosticoEntitie diag = diagOpt.get();
        BigDecimal totalRepuestos = diag.getRepuestosAsignados().stream()
            .map(RepuestoAsignadoEntitie::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("diag", diag);
        model.addAttribute("totalRepuestos", totalRepuestos);
        return "mecanico/orden_servicio";
    }

    @GetMapping("/venta")
    public String ventaRepuestos(Model model) {
        model.addAttribute("repuestos", repuestosRepository.findDisponibles());
        return "mecanico/venta_repuestos";
    }

    @PostMapping("/venta/generar")
    public String generarBoletaVenta(@RequestParam List<Long> repuestoIds,
            @RequestParam List<Integer> cantidades,
            Model model) {
        List<RepuestoAsignadoEntitie> items = new ArrayList<>();
        for (int i = 0; i < repuestoIds.size(); i++) {
            var rOpt = repuestosRepository.findById(repuestoIds.get(i));
            if (rOpt.isPresent()) {
                RepuestoAsignadoEntitie rae = new RepuestoAsignadoEntitie();
                rae.setRepuesto(rOpt.get());
                rae.setCantidad(i < cantidades.size() ? cantidades.get(i) : 1);
                rae.setPrecioUnitario(rOpt.get().getPrecio());
                items.add(rae);
            }
        }
        BigDecimal total = items.stream().map(RepuestoAsignadoEntitie::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

        model.addAttribute("items", items);
        model.addAttribute("total", total);
        model.addAttribute("fecha", fecha);
        return "mecanico/boleta_venta";
    }
}