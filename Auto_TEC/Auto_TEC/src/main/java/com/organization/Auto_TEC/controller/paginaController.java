package com.organization.Auto_TEC.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class paginaController {

    // Vistas principales
    @GetMapping("/") public String index() { return "page/index"; }
    @GetMapping("/login") public String login() { return "page/login"; }
    @GetMapping("/contacto") public String contacto() { return "page/contacto"; }
    @GetMapping("/gestion") public String gestion() { return "page/gestion"; }
    @GetMapping("/servicios") public String servicios() { return "page/servicios"; }
    @GetMapping("/ventas") public String ventas() { return "page/ventas"; }


}