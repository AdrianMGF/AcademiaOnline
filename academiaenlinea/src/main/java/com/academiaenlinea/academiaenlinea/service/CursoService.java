package com.academiaenlinea.academiaenlinea.service;

import com.academiaenlinea.academiaenlinea.model.Curso;
import com.academiaenlinea.academiaenlinea.model.Usuario;
import com.academiaenlinea.academiaenlinea.repository.CursoRepository;
import com.academiaenlinea.academiaenlinea.repository.UsuarioRepository;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CursoService {

    private final CursoRepository cursoRepository;
    private final UsuarioRepository usuarioRepository;

    public CursoService(CursoRepository cursoRepository, UsuarioRepository usuarioRepository) {
        this.cursoRepository = cursoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Curso> listarTodosLosCursos() {
    return cursoRepository.findAll();
}

public List<Curso> listarCursosPublicados() {
    return cursoRepository.findByEstado(Curso.EstadoCurso.PUBLICADO).stream()
            .filter(curso -> curso.getCapacidad() != null && curso.getCapacidad() > 0)
            .toList();
}

    public List<Curso> listarCursos() {
        return cursoRepository.findAll();
    }

   
    public List<Curso> obtenerCursosConModulos() {
    return cursoRepository.findAllWithModulos();
}

    public Curso crearOCrearCurso(Curso curso) {
        return cursoRepository.save(curso);
    }

    public void eliminarCurso(Long id) {
        cursoRepository.deleteById(id);
    }

    public Optional<Usuario> getInstructorPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .filter(u -> u.getRol().equals("INSTRUCTOR") || u.getRol().equals("ADMIN"));
    }
    
    
}

