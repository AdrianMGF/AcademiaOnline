package com.academiaenlinea.academiaenlinea.model;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Respuesta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String texto;

    private boolean correcta;

    @ManyToOne
    @JoinColumn(name = "pregunta_id")
    @ToString.Exclude
    private Pregunta pregunta;
}
