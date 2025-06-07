package com.academiaenlinea.academiaenlinea.service;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import com.academiaenlinea.academiaenlinea.model.Archivo;
import com.academiaenlinea.academiaenlinea.model.Modulo;
import com.academiaenlinea.academiaenlinea.repository.ArchivoRepository;
import com.academiaenlinea.academiaenlinea.repository.ModuloRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class ArchivoService {

    private final ArchivoRepository archivoRepo;
    private final ModuloRepository moduloRepo;

    public ArchivoService(ArchivoRepository archivoRepo, ModuloRepository moduloRepo) {
        this.archivoRepo = archivoRepo;
        this.moduloRepo = moduloRepo;
    }

    public Archivo guardarArchivo(Long moduloId, String nombre, String tipo, String url) {
        Modulo modulo = moduloRepo.findById(moduloId)
            .orElseThrow(() -> new EntityNotFoundException("Módulo no encontrado"));

        Archivo archivo = new Archivo();
        archivo.setNombre(nombre);
        archivo.setTipo(tipo);
        archivo.setUrl(url);
        archivo.setModulo(modulo);

        return archivoRepo.save(archivo);
    }

    public List<Archivo> obtenerArchivosPorModulo(Long moduloId) {
        Modulo modulo = moduloRepo.findById(moduloId)
            .orElseThrow(() -> new EntityNotFoundException("Módulo no encontrado"));

        return archivoRepo.findByModulo(modulo);
    }
}
