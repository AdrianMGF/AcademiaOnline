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
public class Modulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String descripcion;
    private Integer orden;

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
    @JoinColumn(name = "curso_id")
    private Curso curso;


    @OneToMany(mappedBy = "modulo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
private List<Archivo> archivos = new ArrayList<>();

@OneToMany(mappedBy = "modulo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
private List<Pregunta> preguntas = new ArrayList<>();
}
