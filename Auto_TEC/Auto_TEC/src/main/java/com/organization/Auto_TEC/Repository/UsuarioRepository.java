package com.organization.Auto_TEC.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.organization.Auto_TEC.Entities.usuarioEntitie;

@Repository
public interface UsuarioRepository extends JpaRepository<usuarioEntitie, Long> {

    Optional<usuarioEntitie> findByUsername(String username);

    Optional<usuarioEntitie> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM usuarioEntitie u WHERE u.rol.nombre = :nombreRol")
    List<usuarioEntitie> findByRolNombre(@Param("nombreRol") String nombreRol);

    List<usuarioEntitie> findByActivoTrue();

    @EntityGraph(attributePaths = { "rol" })
    Optional<usuarioEntitie> findByUsernameOrEmail(String username, String email);

    @Query("SELECT u FROM usuarioEntitie u WHERE u.username = :username AND u.email = :email")
    Optional<usuarioEntitie> findByUsernameAndEmail(@Param("username") String username, @Param("email") String email);
}