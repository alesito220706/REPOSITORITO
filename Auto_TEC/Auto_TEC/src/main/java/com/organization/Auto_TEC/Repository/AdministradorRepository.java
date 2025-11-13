package com.organization.Auto_TEC.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.organization.Auto_TEC.Entities.administradorEntitie;

@Repository
public interface AdministradorRepository extends JpaRepository<administradorEntitie, Long> {
    
    @Query("SELECT a FROM administradorEntitie a WHERE a.username = :login OR a.email = :login")
    Optional<administradorEntitie> findByUsernameOrEmail(@Param("login") String login, @Param("login") String login2);
    
    Optional<administradorEntitie> findByUsername(String username);
    Optional<administradorEntitie> findByEmail(String email);
}
