package com.organization.Auto_TEC.Repository;

import com.organization.Auto_TEC.Entities.cotizacionEntitie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CotizacionRepository extends JpaRepository<cotizacionEntitie, Long> {
    // Aquí puedes añadir métodos de búsqueda personalizados para tu panel
}