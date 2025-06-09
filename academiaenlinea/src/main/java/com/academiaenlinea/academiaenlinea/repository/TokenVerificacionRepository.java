package com.academiaenlinea.academiaenlinea.repository;


import com.academiaenlinea.academiaenlinea.model.TokenVerificacion;
import com.academiaenlinea.academiaenlinea.model.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenVerificacionRepository extends JpaRepository<TokenVerificacion, Long> {
    Optional<TokenVerificacion> findByToken(String token);
    void deleteByUsuario(Usuario usuario);
}