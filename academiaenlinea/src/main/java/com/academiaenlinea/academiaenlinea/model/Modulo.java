package com.academiaenlinea.academiaenlinea.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Modulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String descripcion;
    private Integer orden;

    @ManyToOne
    @JoinColumn(name = "curso_id")
    private Curso curso;
}
