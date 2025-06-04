// CursoRepository.java
package com.academiaenlinea.academiaenlinea.repository;

import com.academiaenlinea.academiaenlinea.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CursoRepository extends JpaRepository<Curso, Long> {
    List<Curso> findByEstado(Curso.EstadoCurso estado);
    List<Curso> findByTituloContainingIgnoreCase(String titulo);
    List<Curso> findByNivel(Curso.Nivel nivel);
    @Query("SELECT DISTINCT c FROM Curso c LEFT JOIN FETCH c.modulos")
    List<Curso> findAllWithModulos();
}
