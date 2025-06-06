package com.academiaenlinea.academiaenlinea.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Usuario alumno;

    @ManyToOne
    private Curso curso;

    private LocalDate fechaInscripcion;

    private Integer progreso = 0; 
    private Double calificacion;
}
