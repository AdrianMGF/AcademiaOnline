package com.academiaenlinea.academiaenlinea.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
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

    @OneToMany(mappedBy = "alumno", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
private List<Inscripcion> inscripciones = new ArrayList<>();

    public enum Rol {
        ADMIN, INSTRUCTOR, ALUMNO
    }
}



