package com.organization.Auto_TEC.Service;


import com.organization.Auto_TEC.Entities.financiamientoSolicitud;
import com.organization.Auto_TEC.Entities.financiamientoEstadosolicitud;
import com.organization.Auto_TEC.Repository.FinanciamientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FinanciamientoService {
    
    @Autowired
    private FinanciamientoRepository FinanciamientoRepository;

    public List<financiamientoSolicitud> obtenerTodas() {
        return FinanciamientoRepository.findAll();
    }

    public Optional<financiamientoSolicitud> obtenerPorId(Long id) {
        return FinanciamientoRepository.findById(id);
    }

    public List<financiamientoSolicitud> obtenerPendientes() {
        return FinanciamientoRepository.findByEstado(financiamientoEstadosolicitud.PENDIENTE);
    }

    public List<financiamientoSolicitud> obtenerPorEstado(String estado) {
        financiamientoEstadosolicitud e = financiamientoEstadosolicitud.valueOf(estado.toUpperCase());
        return FinanciamientoRepository.findByEstado(e);
    }

    public financiamientoSolicitud guardar(financiamientoSolicitud solicitud) {
        return FinanciamientoRepository.save(solicitud);
    }

    public void eliminar(Long id) {
        FinanciamientoRepository.deleteById(id);
    }

    public void cambiarEstado(Long id, String nuevoEstado) {
        Optional<financiamientoSolicitud> solicitud = FinanciamientoRepository.findById(id);
        if (solicitud.isPresent()) {
            financiamientoSolicitud s = solicitud.get();
            try {
                financiamientoEstadosolicitud estado = financiamientoEstadosolicitud.valueOf(nuevoEstado.toUpperCase());
                s.setEstado(estado);
                FinanciamientoRepository.save(s);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Estado no válido: " + nuevoEstado);
            }
        } else {
            throw new RuntimeException("Solicitud no encontrada con ID: " + id);
        }
    }

      public int contarPorEstado(String estado) {
    try {
        financiamientoEstadosolicitud estadoEnum = financiamientoEstadosolicitud.valueOf(estado.toUpperCase());
        return FinanciamientoRepository.countByEstado(estadoEnum);
    } catch (IllegalArgumentException e) {
        throw new IllegalArgumentException("Estado no válido: " + estado);
    }
}
    // Método adicional para contar todas las solicitudes (opcional)
    public List<financiamientoSolicitud> obtenerSolicitudesRecientes() {
        // Obtener solicitudes de los últimos 7 días
        LocalDateTime fechaLimite = LocalDateTime.now().minusDays(7);
        return FinanciamientoRepository.findByFechaSolicitudAfter(fechaLimite);
    }

    public long contarSolicitudesRecientes() {
        return obtenerSolicitudesRecientes().size();
    }

    // Método adicional para contar total
    public long contarTotalSolicitudes() {
        return FinanciamientoRepository.count();
    }
}
