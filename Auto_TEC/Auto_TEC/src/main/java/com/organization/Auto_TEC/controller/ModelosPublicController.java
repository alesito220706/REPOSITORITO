package com.organization.Auto_TEC.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.organization.Auto_TEC.Service.ModeloService;

@Controller
@RequestMapping("/modelos")
public class ModelosPublicController {

    private final ModeloService modeloService;

    public ModelosPublicController(ModeloService modeloService) {
        this.modeloService = modeloService;
    }

    @GetMapping
    public String mostrarModelos(Model model) {

        // Solo modelos activos
        model.addAttribute("modelos", modeloService.findByActivoTrue());

        // Retorna la vista ubicada en /templates/page/modelos.html
        return "page/modelos";
    }
}
