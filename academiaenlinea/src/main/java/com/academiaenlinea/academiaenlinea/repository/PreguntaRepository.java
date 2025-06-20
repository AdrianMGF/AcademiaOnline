package com.academiaenlinea.academiaenlinea.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.academiaenlinea.academiaenlinea.model.Pregunta;

@Repository
public interface PreguntaRepository extends JpaRepository<Pregunta, Long> {
      @EntityGraph(attributePaths = "respuestas")
    List<Pregunta> findByModuloId(Long moduloId);
}