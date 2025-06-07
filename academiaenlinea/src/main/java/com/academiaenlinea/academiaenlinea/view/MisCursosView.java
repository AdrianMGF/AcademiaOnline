package com.academiaenlinea.academiaenlinea.view;

import com.academiaenlinea.academiaenlinea.model.Archivo;
import com.academiaenlinea.academiaenlinea.model.Curso;
import com.academiaenlinea.academiaenlinea.model.Inscripcion;
import com.academiaenlinea.academiaenlinea.model.Modulo;
import com.academiaenlinea.academiaenlinea.model.ProgresoModulo;
import com.academiaenlinea.academiaenlinea.model.Usuario;
import com.academiaenlinea.academiaenlinea.service.UsuarioService;
import com.academiaenlinea.academiaenlinea.service.UsuarioService.ProgresoCursoResumen;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import jakarta.annotation.security.RolesAllowed;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

@Route("mis-cursos")
@RolesAllowed("ALUMNO")
public class MisCursosView extends VerticalLayout {

    private final UsuarioService usuarioService;

    public MisCursosView(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
        setPadding(true);
        setSpacing(true);

        H2 titulo = new H2("Mis cursos inscritos");
        add(titulo);

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
Usuario alumno = usuarioService.buscarAlumnoConInscripciones(userDetails.getUsername());

        if (alumno.getInscripciones() == null || alumno.getInscripciones().isEmpty()) {
            add(new Paragraph("No estás inscrito en ningún curso."));
            return;
        }

        Accordion accordion = new Accordion();

        for (Inscripcion inscripcion : alumno.getInscripciones()) {
            Curso curso = inscripcion.getCurso();

            VerticalLayout contenidoCurso = new VerticalLayout();
            contenidoCurso.add(new Paragraph("Descripción: " + curso.getDescripcion()));
            contenidoCurso.add(new Paragraph("Nivel: " + curso.getNivel()));
            contenidoCurso.add(new Paragraph("Fechas: " + curso.getFechaInicio() + " - " + curso.getFechaFin()));
ProgresoCursoResumen resumen = usuarioService.calcularResumen(inscripcion.getId());

contenidoCurso.add(new Paragraph("Progreso: " + String.format("%.1f", resumen.porcentajeCompletado()) + "%"));
contenidoCurso.add(new Paragraph("Promedio de calificaciones: " + String.format("%.1f", resumen.promedioCalificacion())));

            VerticalLayout modulosList = new VerticalLayout();
            modulosList.add(new H2("Módulos"));
            for (Modulo modulo : curso.getModulos()) {
    ProgresoModulo progreso = inscripcion.getProgresos().stream()
        .filter(p -> p.getModulo().getId().equals(modulo.getId()))
        .findFirst().orElse(null);

    String estado = progreso != null && progreso.isCompletado() ? "Completado" : "Incompleto";
    String nota = progreso != null && progreso.getCalificacion() != null
        ? String.format(" - Calificación: %.1f", progreso.getCalificacion())
        : "";

    Paragraph p = new Paragraph(modulo.getOrden() + ". " + modulo.getTitulo() + " - " + estado + nota);

    // Mostrar enlaces a archivos
    if (!modulo.getArchivos().isEmpty()) {
        for (Archivo archivo : modulo.getArchivos()) {
            Anchor link = new Anchor(new StreamResource(archivo.getNombre(), () -> {
                try {
                    return new FileInputStream(archivo.getUrl());
                } catch (FileNotFoundException e) {
                    Notification.show("Archivo no encontrado");
                    return null;
                }
            }), archivo.getNombre());

            link.setTarget("_blank");
            p.add(link);
        }
    }
    modulosList.add(p);
}


            contenidoCurso.add(modulosList);
            AccordionPanel panel = accordion.add(curso.getTitulo(), contenidoCurso);
        }

        add(accordion);
    }
}
