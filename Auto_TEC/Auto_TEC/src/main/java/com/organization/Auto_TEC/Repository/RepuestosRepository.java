package com.organization.Auto_TEC.Repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.organization.Auto_TEC.Entities.RepuestosEntitie;

@Repository
public interface RepuestosRepository extends JpaRepository<RepuestosEntitie, Long> {
    List<RepuestosEntitie> findByNombreContainingIgnoreCase(String nombre);
    List<RepuestosEntitie> findByCodigoContainingIgnoreCase(String codigo);
    List<RepuestosEntitie> findByCategoriaContainingIgnoreCase(String categoria);

    @Query("SELECT r FROM RepuestosEntitie r WHERE r.stock > 0 ORDER BY r.nombre")
    List<RepuestosEntitie> findDisponibles();

    @Query("SELECT r FROM RepuestosEntitie r WHERE LOWER(r.nombre) LIKE LOWER(CONCAT('%', :term, '%')) OR LOWER(r.codigo) LIKE LOWER(CONCAT('%', :term, '%')) OR LOWER(r.categoria) LIKE LOWER(CONCAT('%', :term, '%'))")
    List<RepuestosEntitie> buscar(@Param("term") String term);
}