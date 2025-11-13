package com.organization.Auto_TEC.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.organization.Auto_TEC.Entities.modelosEntitie;

public interface ModeloRepository extends JpaRepository<modelosEntitie, Long> {
    List<modelosEntitie> findByActivo(boolean activo);

    List<modelosEntitie> findByMarcaContainingIgnoreCase(String marca);

    List<modelosEntitie> findByDestacadoTrue();

    List<modelosEntitie> findByActivoTrue();
}