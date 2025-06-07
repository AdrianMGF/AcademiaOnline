package com.academiaenlinea.academiaenlinea.service;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import com.academiaenlinea.academiaenlinea.model.Archivo;
import com.academiaenlinea.academiaenlinea.model.Modulo;
import com.academiaenlinea.academiaenlinea.repository.ArchivoRepository;
import com.academiaenlinea.academiaenlinea.repository.ModuloRepository;
import jakarta.persistence.EntityNotFoundException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

@Service
public class ArchivoService {

    private final ArchivoRepository archivoRepo;
    private final ModuloRepository moduloRepo;

    private final String uploadRoot = "uploads";

    public ArchivoService(ArchivoRepository archivoRepo, ModuloRepository moduloRepo) {
        this.archivoRepo = archivoRepo;
        this.moduloRepo = moduloRepo;
    }

    public Archivo guardarArchivo(Long moduloId, String nombreArchivo, InputStream contenido, String tipoArchivo) throws IOException {
        Modulo modulo = moduloRepo.findById(moduloId)
            .orElseThrow(() -> new EntityNotFoundException("Módulo no encontrado"));

        Path carpetaModulo = Paths.get(uploadRoot, "modulo-" + modulo.getId());
        Files.createDirectories(carpetaModulo);

        Path archivoPath = carpetaModulo.resolve(nombreArchivo);

        Files.copy(contenido, archivoPath, StandardCopyOption.REPLACE_EXISTING);

        Archivo archivo = new Archivo();
        archivo.setNombre(nombreArchivo);
        archivo.setTipo(tipoArchivo);
        archivo.setUrl(archivoPath.toString());
        archivo.setModulo(modulo);

        return archivoRepo.save(archivo);
    }

    public List<Archivo> obtenerArchivosPorModulo(Long moduloId) {
        Modulo modulo = moduloRepo.findById(moduloId)
            .orElseThrow(() -> new EntityNotFoundException("Módulo no encontrado"));
        return modulo.getArchivos();
    }
}
