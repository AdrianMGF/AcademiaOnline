package com.academiaenlinea.academiaenlinea.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Pregunta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     @EqualsAndHashCode.Include
    private Long id;

    private String enunciado;

    @ManyToOne
    @JoinColumn(name = "modulo_id")
    private Modulo modulo;

    @ToString.Exclude
    @OneToMany(mappedBy = "pregunta", cascade = CascadeType.ALL, orphanRemoval = true,  fetch = FetchType.EAGER)
    private List<Respuesta> respuestas = new ArrayList<>();

    private Integer puntaje = 1; 
}
