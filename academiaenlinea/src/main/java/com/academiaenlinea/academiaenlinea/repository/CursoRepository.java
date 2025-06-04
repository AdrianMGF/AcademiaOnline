// CursoRepository.java
package com.academiaenlinea.academiaenlinea.repository;

import com.academiaenlinea.academiaenlinea.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CursoRepository extends JpaRepository<Curso, Long> {
    List<Curso> findByEstado(Curso.EstadoCurso estado);
    List<Curso> findByTituloContainingIgnoreCase(String titulo);
    List<Curso> findByNivel(Curso.Nivel nivel);
    List<Curso> findByInstructorEmail(String email);
}
