package com.academiaenlinea.academiaenlinea.service;

import com.academiaenlinea.academiaenlinea.model.ProgresoModulo;
import com.academiaenlinea.academiaenlinea.repository.ProgresoModuloRepository;
import org.springframework.stereotype.Service;

@Service
public class ProgresoModuloService {

    private final ProgresoModuloRepository progresoModuloRepository;

    public ProgresoModuloService(ProgresoModuloRepository progresoModuloRepository) {
        this.progresoModuloRepository = progresoModuloRepository;
    }

    public ProgresoModulo guardar(ProgresoModulo progresoModulo) {
        return progresoModuloRepository.save(progresoModulo);
    }
}
