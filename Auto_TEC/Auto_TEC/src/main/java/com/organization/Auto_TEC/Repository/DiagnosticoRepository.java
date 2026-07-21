package com.organization.Auto_TEC.Repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.organization.Auto_TEC.Entities.DiagnosticoEntitie;

@Repository
public interface DiagnosticoRepository extends JpaRepository<DiagnosticoEntitie, Long> {
    List<DiagnosticoEntitie> findByMecanicoIdOrderByFechaCreacionDesc(Long mecanicoId);
    List<DiagnosticoEntitie> findByCitaId(Long citaId);
    List<DiagnosticoEntitie> findByEstadoOrderByFechaCreacionDesc(String estado);
    long countByMecanicoId(Long mecanicoId);
    long countByEstado(String estado);
}