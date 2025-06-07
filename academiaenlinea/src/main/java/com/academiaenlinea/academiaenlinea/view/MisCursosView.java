package com.academiaenlinea.academiaenlinea.view;

import com.academiaenlinea.academiaenlinea.model.Curso;
import com.academiaenlinea.academiaenlinea.model.Inscripcion;
import com.academiaenlinea.academiaenlinea.model.Modulo;
import com.academiaenlinea.academiaenlinea.model.Usuario;
import com.academiaenlinea.academiaenlinea.service.UsuarioService;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
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

            VerticalLayout modulosList = new VerticalLayout();
            modulosList.add(new H2("Módulos"));
            for (Modulo modulo : curso.getModulos()) {
                modulosList.add(new Paragraph(modulo.getOrden() + ". " + modulo.getTitulo()));
            }

            contenidoCurso.add(modulosList);
            AccordionPanel panel = accordion.add(curso.getTitulo(), contenidoCurso);
        }

        add(accordion);
    }
}
