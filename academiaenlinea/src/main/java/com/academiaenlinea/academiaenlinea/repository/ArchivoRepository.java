package com.academiaenlinea.academiaenlinea.repository;

import com.academiaenlinea.academiaenlinea.model.Archivo;
import com.academiaenlinea.academiaenlinea.model.Modulo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArchivoRepository extends JpaRepository<Archivo, Long> {
    List<Archivo> findByModulo(Modulo modulo);
}
