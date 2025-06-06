package com.academiaenlinea.academiaenlinea.service;

import com.academiaenlinea.academiaenlinea.model.Curso;
import com.academiaenlinea.academiaenlinea.model.Inscripcion;
import com.academiaenlinea.academiaenlinea.model.Usuario;
import com.academiaenlinea.academiaenlinea.repository.InscripcionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InscripcionService {

    private final InscripcionRepository inscripcionRepository;

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

        inscripcionRepository.save(inscripcion);
        return true;
    }

    public List<Inscripcion> obtenerInscripcionesPorAlumno(Usuario alumno) {
        return inscripcionRepository.findByAlumno(alumno);
    }
}
