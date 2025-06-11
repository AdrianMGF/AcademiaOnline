package com.academiaenlinea.academiaenlinea.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import lombok.*;

@Entity
@EntityListeners(AuditingEntityListener.class)
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

     @CreatedDate
    @Column(updatable = false)
    private LocalDateTime fechaCreacion;

     @LastModifiedDate
    private LocalDateTime fechaModificacion;

    @CreatedBy
    @Column(updatable = false)
    private String creadoPor;

    @LastModifiedBy
    private String modificadoPor;

    @ManyToOne
    @JoinColumn(name = "modulo_id")
    private Modulo modulo;

    @ToString.Exclude
    @OneToMany(mappedBy = "pregunta", cascade = CascadeType.ALL, orphanRemoval = true,  fetch = FetchType.EAGER)
    private List<Respuesta> respuestas = new ArrayList<>();

    private Integer puntaje = 1; 
}
