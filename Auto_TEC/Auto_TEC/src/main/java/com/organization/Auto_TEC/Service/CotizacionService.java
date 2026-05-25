package com.organization.Auto_TEC.Service;

import com.organization.Auto_TEC.DTO.CotizacionDTO;
import com.organization.Auto_TEC.Entities.cotizacionEntitie;
import com.organization.Auto_TEC.Repository.CotizacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CotizacionService {

    @Autowired
    private CotizacionRepository cotizacionRepository;

    public void guardarCotizacion(CotizacionDTO dto) {
        cotizacionEntitie entidad = new cotizacionEntitie();
        
        // Mapeamos los datos del formulario (DTO) a la Entidad
        entidad.setNombreSolicitante(dto.getNombre());
        entidad.setEmailSolicitante(dto.getEmail());
        entidad.setModelo_interes(dto.getModelo());
        entidad.setNotas("Solicitud desde sitio web"); // Nota por defecto
        
        cotizacionRepository.save(entidad);
    }
}