package com.academiaenlinea.academiaenlinea.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Archivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private String tipo; // MIME type o extensión, ej. "application/pdf", "video/mp4"

    private String url; // Ruta donde se almacena o URL externa

    @ManyToOne
    private Modulo modulo;
}
