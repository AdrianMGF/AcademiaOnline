package com.academiaenlinea.academiaenlinea.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.academiaenlinea.academiaenlinea.model.Pregunta;
import com.academiaenlinea.academiaenlinea.repository.PreguntaRepository;
import com.academiaenlinea.academiaenlinea.repository.RespuestaRepository;

@Service
public class PreguntaService {

    private final PreguntaRepository preguntaRepository;
    private final RespuestaRepository respuestaRepository;

    public PreguntaService(PreguntaRepository preguntaRepository, RespuestaRepository respuestaRepository) {
        this.preguntaRepository = preguntaRepository;
        this.respuestaRepository = respuestaRepository;
    }

    public Pregunta guardarPreguntaConRespuestas(Pregunta pregunta) {
        Pregunta guardada = preguntaRepository.save(pregunta);

        // Asociar la pregunta a cada respuesta y guardarlas
        if (pregunta.getRespuestas() != null) {
            pregunta.getRespuestas().forEach(respuesta -> {
                respuesta.setPregunta(guardada);
            });
            respuestaRepository.saveAll(pregunta.getRespuestas());
        }
        return guardada;
    }

    public List<Pregunta> obtenerPreguntasPorModulo(Long moduloId) {
        return preguntaRepository.findByModuloId(moduloId);
    }
    
    public void eliminarPregunta(Long preguntaId) {
        preguntaRepository.deleteById(preguntaId);
    }

}

