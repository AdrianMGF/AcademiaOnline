package com.academiaenlinea.academiaenlinea.repository;

import com.academiaenlinea.academiaenlinea.model.Curso;
import com.academiaenlinea.academiaenlinea.model.Inscripcion;
import com.academiaenlinea.academiaenlinea.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {
    List<Inscripcion> findByAlumno(Usuario alumno);
    boolean existsByAlumnoAndCurso(Usuario alumno, Curso curso);
    long countByCurso(Curso curso);
    @Query("SELECT i FROM Inscripcion i LEFT JOIN FETCH i.progresos WHERE i.alumno = :alumno")
List<Inscripcion> findByAlumnoConProgresos(@Param("alumno") Usuario alumno);

}
