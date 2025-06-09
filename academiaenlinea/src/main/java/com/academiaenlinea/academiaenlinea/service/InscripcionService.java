package com.academiaenlinea.academiaenlinea.service;

import com.academiaenlinea.academiaenlinea.model.Curso;
import com.academiaenlinea.academiaenlinea.model.Inscripcion;
import com.academiaenlinea.academiaenlinea.model.Usuario;
import com.academiaenlinea.academiaenlinea.repository.CursoRepository;
import com.academiaenlinea.academiaenlinea.repository.InscripcionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.academiaenlinea.academiaenlinea.model.ProgresoModulo;
import java.util.ArrayList;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InscripcionService {

    private final InscripcionRepository inscripcionRepository;
     private final CursoRepository cursoRepository;

    public boolean inscribirAlumno(Usuario alumno, Curso curso) {
        if (inscripcionRepository.existsByAlumnoAndCurso(alumno, curso)) {
            return false;
        }

        if (curso.getCapacidad() != null && inscripcionRepository.countByCurso(curso) >= curso.getCapacidad()) {
            return false;
        }

        Inscripcion inscripcion = Inscripcion.builder()
                .alumno(alumno)
                .curso(curso)
                .fechaInscripcion(LocalDate.now())
                .build();

                inscripcion.setProgresos(new ArrayList<>());

    curso.getModulos().forEach(modulo -> {
        ProgresoModulo progreso = new ProgresoModulo();
        progreso.setModulo(modulo);
        progreso.setInscripcion(inscripcion);
        progreso.setCompletado(false);
        progreso.setCalificacion(null);
        inscripcion.getProgresos().add(progreso);
    });
        inscripcionRepository.save(inscripcion);
        if (curso.getCapacidad() != null) {
        curso.setCapacidad(curso.getCapacidad() - 1);
        cursoRepository.save(curso);
    }
        return true;
    }

    public List<Inscripcion> obtenerInscripcionesPorAlumno(Usuario alumno) {
    return inscripcionRepository.findByAlumnoConProgresos(alumno);
}
}
