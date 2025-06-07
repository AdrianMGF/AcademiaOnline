package com.academiaenlinea.academiaenlinea.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ProgresoModulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean completado;

    private Double calificacion;

    @ManyToOne
    private Modulo modulo;

    @ManyToOne
    private Inscripcion inscripcion;

}
