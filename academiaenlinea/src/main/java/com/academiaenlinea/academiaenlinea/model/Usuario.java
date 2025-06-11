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
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Usuario{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombreCompleto;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Rol rol;

    @Column(nullable = false)
    private boolean activo = false; 


    private boolean bloqueado = false;

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

    @OneToMany(mappedBy = "alumno", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
private List<Inscripcion> inscripciones = new ArrayList<>();

    public enum Rol {
        ADMIN, INSTRUCTOR, ALUMNO
    }
}



