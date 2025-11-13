package com.organization.Auto_TEC.Service;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.organization.Auto_TEC.Entities.empleadoEntitie;
import com.organization.Auto_TEC.Repository.EmpleadoRepository;

@Service
public class EmpleadoServiceImpl implements EmpleadoService {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Override
    public List<empleadoEntitie> findAll() {
        return empleadoRepository.findAll();
    }

    @Override
    public Optional<empleadoEntitie> findById(Long id) {
        return empleadoRepository.findById(id);
    }

    @Override
    public empleadoEntitie save(empleadoEntitie empleado) {
        return empleadoRepository.save(empleado);
    }

    @Override
    public void deleteById(Long id) {
        empleadoRepository.deleteById(id);
    }

    @Override
    public List<empleadoEntitie> findByActivoTrue() {
        return empleadoRepository.findByActivoTrue();
    }

    @Override
    public void cambiarEstado(Long id, Boolean activo) {
        Optional<empleadoEntitie> empleadoOpt = empleadoRepository.findById(id);
        if (empleadoOpt.isPresent()) {
            empleadoEntitie empleado = empleadoOpt.get();
            empleado.setActivo(activo);
            empleadoRepository.save(empleado);
        }
    }
}
