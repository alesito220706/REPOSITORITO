package com.organization.Auto_TEC.Repository;

import com.organization.Auto_TEC.Entities.financiamientoSolicitud;
import com.organization.Auto_TEC.Entities.financiamientoEntitie;
import com.organization.Auto_TEC.Entities.financiamientoEstadosolicitud;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FinanciamientoRepository extends JpaRepository<financiamientoSolicitud, Long> {
    // Buscar solicitudes por estado (usa el enum)
    List<financiamientoSolicitud> findByEstado(financiamientoEstadosolicitud estado);

     int countByEstado(financiamientoEstadosolicitud estado);
    
    // Buscar solicitudes por fecha reciente
    List<financiamientoSolicitud> findByFechaSolicitudAfter(LocalDateTime fecha);
    
    // Buscar solicitudes por email
    List<financiamientoSolicitud> findByEmailSolicitante(String email);
    
    // Método alternativo si countByEstado no funciona
    @Query("SELECT COUNT(f) FROM financiamientoSolicitud f WHERE f.estado = :estado")
    long contarPorEstado(@Param("estado") financiamientoEstadosolicitud estado);
    
    // Buscar solicitudes por usuario (si existe la relación)
    List<financiamientoSolicitud> findByUsuario_Id(Long usuarioId);
 

}
