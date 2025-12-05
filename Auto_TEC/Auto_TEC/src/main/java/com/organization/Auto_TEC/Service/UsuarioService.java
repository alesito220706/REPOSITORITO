package com.organization.Auto_TEC.Service;

import java.util.List;
import java.util.Optional;

import com.organization.Auto_TEC.DTO.RegistroDTO;
import com.organization.Auto_TEC.Entities.Rol;
import com.organization.Auto_TEC.Entities.usuarioEntitie;

public interface UsuarioService {
    List<usuarioEntitie> findAll();
    Optional<usuarioEntitie> findById(Long id);
    usuarioEntitie save(usuarioEntitie usuario);
    void deleteById(Long id);
    List<usuarioEntitie> findClientes();
    List<usuarioEntitie> findVendedores();
    List<usuarioEntitie> findByActivoTrue();
    Rol findRolById(Long id);
    usuarioEntitie registrarUsuario(RegistroDTO registroDTO);
    Optional<usuarioEntitie> findByEmail(String email);
}