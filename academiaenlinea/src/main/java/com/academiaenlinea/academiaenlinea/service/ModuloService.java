package com.academiaenlinea.academiaenlinea.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.academiaenlinea.academiaenlinea.model.Modulo;
import com.academiaenlinea.academiaenlinea.repository.CursoRepository;
import com.academiaenlinea.academiaenlinea.repository.ModuloRepository;
import com.academiaenlinea.academiaenlinea.repository.UsuarioRepository;
 
@Service
public class ModuloService {
        private final ModuloRepository moduloRepository;
public ModuloService(ModuloRepository moduloRepository) {
        this.moduloRepository = moduloRepository;
    }
    public List<Modulo> buscarPorCursoId(Long cursoId) {
    return moduloRepository.findByCursoId(cursoId);
}
}
