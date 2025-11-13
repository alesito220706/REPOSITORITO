package com.organization.Auto_TEC.controller;

import com.organization.Auto_TEC.Entities.cotizacionEntitie;
import com.organization.Auto_TEC.Entities.modelosEntitie;
import com.organization.Auto_TEC.Repository.CotizacionRepository;
import com.organization.Auto_TEC.Service.ModeloService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
public class CotizacionController {

    private final CotizacionRepository cotizacionRepository;
    private final ModeloService modeloService;

    public CotizacionController(CotizacionRepository cotizacionRepository, ModeloService modeloService) {
        this.cotizacionRepository = cotizacionRepository;
        this.modeloService = modeloService;
    }

    @PostMapping(path = "/cotizar")
    public ResponseEntity<?> recibirCotizacion(@RequestBody Map<String, Object> payload) {
        String nombre = payload.getOrDefault("nombre", payload.getOrDefault("nombreSolicitante", "")).toString().trim();
        String email = payload.getOrDefault("email", payload.getOrDefault("emailSolicitante", "")).toString().trim();
        String modeloStr = payload.getOrDefault("modelo", payload.getOrDefault("modeloInteres", "")).toString().trim();

        // Validaciones simples
        if (nombre.length() < 3) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Nombre inválido"));
        }
        if (!email.contains("@") || email.length() < 5) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Email inválido"));
        }
        if (modeloStr.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Modelo requerido"));
        }

        // Intentar encontrar modelo por nombre (case-insensitive). Si hay un service para ello, usarlo.
        modelosEntitie modeloEntity = null;
        try {
            // buscar por todos los modelos y comparar por nombre
            Optional<modelosEntitie> found = modeloService.findAll().stream()
                    .filter(m -> m.getNombre() != null && m.getNombre().equalsIgnoreCase(modeloStr))
                    .findFirst();
            if (found.isPresent()) modeloEntity = found.get();
        } catch (Exception ignored) {}

        cotizacionEntitie ent = new cotizacionEntitie();
        ent.setUsuario(null);
        ent.setModelo(modeloEntity);
        ent.setNombreSolicitante(nombre);
        ent.setEmailSolicitante(email);
        ent.setModelo_interes(modeloStr);
        ent.setNotas(payload.getOrDefault("notas", "").toString());

        cotizacionEntitie saved = cotizacionRepository.save(ent);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", saved.getId(), "status", "created"));
    }

    @GetMapping(path = "/cotizaciones")
    public ResponseEntity<?> listarCotizaciones() {
        return ResponseEntity.ok(cotizacionRepository.findAll());
    }

}
