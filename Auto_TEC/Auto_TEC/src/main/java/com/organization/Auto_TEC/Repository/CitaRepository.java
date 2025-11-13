package com.organization.Auto_TEC.Repository;

import com.organization.Auto_TEC.Entities.citaEntitie;
import com.organization.Auto_TEC.Entities.citaEstado;
import com.organization.Auto_TEC.Entities.citaTipo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CitaRepository extends JpaRepository<citaEntitie, Long> {

    // ======= BÚSQUEDAS BÁSICAS =======
    List<citaEntitie> findByEstado(citaEstado estado);
    List<citaEntitie> findByUsuarioId(Long usuarioId);
    List<citaEntitie> findByTipoCita(citaTipo tipoCita);
    List<citaEntitie> findByEmpleadoId(Long empleadoId);

    // ======= COMBINADAS =======
    List<citaEntitie> findByEstadoAndUsuarioId(citaEstado estado, Long usuarioId);
    List<citaEntitie> findByEstadoAndTipoCita(citaEstado estado, citaTipo tipoCita);
List<citaEntitie> findByFechaCitaBetween(LocalDateTime inicio, LocalDateTime fin);
    // ======= CONTADORES =======
    long countByEstado(citaEstado estado);
    long countByUsuarioId(Long usuarioId);
    long countByTipoCita(citaTipo tipoCita);

    // ======= CONSULTAS PERSONALIZADAS =======
    @Query("SELECT c FROM citaEntitie c WHERE c.estado = com.organization.Auto_TEC.Entities.citaEstado.PENDIENTE ORDER BY c.fechaCita ASC")
    List<citaEntitie> findPendientes();

    @Query("SELECT c FROM citaEntitie c WHERE c.estado = com.organization.Auto_TEC.Entities.citaEstado.CONFIRMADA ORDER BY c.fechaCita ASC")
    List<citaEntitie> findConfirmadas();

    @Query("SELECT c FROM citaEntitie c WHERE c.estado = com.organization.Auto_TEC.Entities.citaEstado.COMPLETADA ORDER BY c.fechaCita DESC")
    List<citaEntitie> findCompletadas();

    @Query("SELECT c FROM citaEntitie c WHERE c.estado = com.organization.Auto_TEC.Entities.citaEstado.CANCELADA ORDER BY c.fechaCita DESC")
    List<citaEntitie> findCanceladas();

    // ======= RANGO DE FECHAS =======
    @Query("SELECT c FROM citaEntitie c WHERE c.fechaCita BETWEEN :fechaInicio AND :fechaFin")
    List<citaEntitie> findByFechaBetween(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT c FROM citaEntitie c WHERE c.usuario.id = :usuarioId AND c.fechaCita BETWEEN :fechaInicio AND :fechaFin")
    List<citaEntitie> findByUsuarioAndFechaBetween(
            @Param("usuarioId") Long usuarioId,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);

    // ======= PRÓXIMAS Y VENCIDAS =======
    @Query("SELECT c FROM citaEntitie c WHERE c.estado IN (com.organization.Auto_TEC.Entities.citaEstado.PENDIENTE, com.organization.Auto_TEC.Entities.citaEstado.CONFIRMADA) AND c.fechaCita >= CURRENT_TIMESTAMP ORDER BY c.fechaCita ASC")
    List<citaEntitie> findProximas();

    @Query("SELECT c FROM citaEntitie c WHERE c.estado = com.organization.Auto_TEC.Entities.citaEstado.PENDIENTE AND c.fechaCita < CURRENT_TIMESTAMP")
    List<citaEntitie> findVencidas();

    // ======= ORDENADAS =======
    @Query("SELECT c FROM citaEntitie c WHERE c.usuario.id = :usuarioId ORDER BY c.fechaCita DESC")
    List<citaEntitie> findByUsuarioIdOrderByFecha(@Param("usuarioId") Long usuarioId);

    @Query("SELECT c FROM citaEntitie c ORDER BY c.fechaCita DESC")
    List<citaEntitie> findAllOrderByFechaDesc();

    // ======= RESUMEN DE ESTADOS =======
    @Query("SELECT c.estado, COUNT(c) FROM citaEntitie c GROUP BY c.estado")
    List<Object[]> getResumenPorEstado();

    // ======= BÚSQUEDA GENERAL =======
    @Query("""
           SELECT c FROM citaEntitie c
           WHERE LOWER(c.usuario.nombres) LIKE LOWER(CONCAT('%', :filtro, '%'))
              OR LOWER(c.tipoCita) LIKE LOWER(CONCAT('%', :filtro, '%'))
              OR LOWER(c.estado) LIKE LOWER(CONCAT('%', :filtro, '%'))
           """)
    List<citaEntitie> buscar(@Param("filtro") String filtro);

    // ======= EXISTENCIA DE CITA =======
    @Query("""
           SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END
           FROM citaEntitie c
           WHERE c.usuario.id = :usuarioId AND c.fechaCita = :fecha
           """)
    boolean existeCitaEnFecha(@Param("usuarioId") Long usuarioId, @Param("fecha") LocalDateTime fecha);

    // ======= PRÓXIMA CITA DE USUARIO =======
    @Query("""
           SELECT c FROM citaEntitie c
           WHERE c.usuario.id = :usuarioId
             AND c.estado <> com.organization.Auto_TEC.Entities.citaEstado.CANCELADA
           ORDER BY c.fechaCita ASC
           """)
    Optional<citaEntitie> findProximaCitaUsuario(@Param("usuarioId") Long usuarioId);
}
