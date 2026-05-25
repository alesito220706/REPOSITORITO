package com.organization.Auto_TEC.controller;

import com.organization.Auto_TEC.Entities.cotizacionEntitie;
import com.organization.Auto_TEC.Entities.cotizacionEstado;
import com.organization.Auto_TEC.Repository.CotizacionRepository;
import com.organization.Auto_TEC.Service.ModeloService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cotizaciones") // Prefijo de API obligatorio
public class CotizacionController {

    private final CotizacionRepository cotizacionRepository;
    private final ModeloService modeloService;

    public CotizacionController(CotizacionRepository cotizacionRepository, ModeloService modeloService) {
        this.cotizacionRepository = cotizacionRepository;
        this.modeloService = modeloService;
    }

    @PostMapping("/enviar")
    public ResponseEntity<?> enviarCotizacion(@RequestBody Map<String, Object> payload) {
        try {
            cotizacionEntitie ent = new cotizacionEntitie();
            ent.setNombreSolicitante(payload.get("nombre").toString());
            ent.setEmailSolicitante(payload.get("email").toString());
            ent.setModelo_interes(payload.get("modelo").toString());
            ent.setEstado(cotizacionEstado.PENDIENTE);
            
            cotizacionRepository.save(ent);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Solicitud enviada"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Error al procesar"));
        }
    }
}