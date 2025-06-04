
package com.academiaenlinea.academiaenlinea.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String descripcion;

    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    @Enumerated(EnumType.STRING)
    private Nivel nivel;

    @Enumerated(EnumType.STRING)
    private EstadoCurso estado;

    @ManyToOne
    private Usuario instructor;

@OneToMany(fetch = FetchType.EAGER, mappedBy = "curso")
private List<Modulo> modulos;
    public enum Nivel {
        BASICO, INTERMEDIO, AVANZADO
    }

    public enum EstadoCurso {
        BORRADOR, PUBLICADO
    }
}
