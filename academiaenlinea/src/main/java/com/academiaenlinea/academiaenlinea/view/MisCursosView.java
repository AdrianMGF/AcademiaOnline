package com.academiaenlinea.academiaenlinea.view;

import com.academiaenlinea.academiaenlinea.model.Archivo;
import com.academiaenlinea.academiaenlinea.model.Curso;
import com.academiaenlinea.academiaenlinea.model.Inscripcion;
import com.academiaenlinea.academiaenlinea.model.Modulo;
import com.academiaenlinea.academiaenlinea.model.Pregunta;
import com.academiaenlinea.academiaenlinea.model.ProgresoModulo;
import com.academiaenlinea.academiaenlinea.model.Respuesta;
import com.academiaenlinea.academiaenlinea.model.Usuario;
import com.academiaenlinea.academiaenlinea.service.PreguntaService;
import com.academiaenlinea.academiaenlinea.service.ProgresoModuloService;
import com.academiaenlinea.academiaenlinea.service.UsuarioService;
import com.academiaenlinea.academiaenlinea.service.UsuarioService.ProgresoCursoResumen;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import jakarta.annotation.security.RolesAllowed;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

@Route("mis-cursos")
@RolesAllowed("ALUMNO")
public class MisCursosView extends VerticalLayout {

    private final UsuarioService usuarioService;
      private final PreguntaService preguntaService;
    private final ProgresoModuloService progresoModuloService;

    public MisCursosView(UsuarioService usuarioService,
                         PreguntaService preguntaService,
                         ProgresoModuloService progresoModuloService) {
        this.usuarioService = usuarioService;
        this.preguntaService = preguntaService;
        this.progresoModuloService = progresoModuloService;
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

    HorizontalLayout filaModulo = new HorizontalLayout();
filaModulo.setAlignItems(Alignment.CENTER);

Paragraph descripcionModulo = new Paragraph(modulo.getOrden() + ". " + modulo.getTitulo() + " - " + estado + nota);
filaModulo.add(descripcionModulo);

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
        filaModulo.add(link);
    }
}
List<Pregunta> preguntas = modulo.getPreguntas(); 

if (preguntas != null && !preguntas.isEmpty()) {
Button resolverCuestionarioBtn = new Button("Resolver cuestionario");
resolverCuestionarioBtn.addClickListener(ev -> {
    CuestionarioDialog dialog = new CuestionarioDialog(modulo, inscripcion);
    dialog.open();
});
if (progreso == null || !progreso.isCompletado()) {
    filaModulo.add(resolverCuestionarioBtn);
}
}

modulosList.add(filaModulo);

}


            contenidoCurso.add(modulosList);
            AccordionPanel panel = accordion.add(curso.getTitulo(), contenidoCurso);
        }

        add(accordion);
    }
     private class CuestionarioDialog extends Dialog {
        private final Modulo modulo;
        private final Inscripcion inscripcion;
        private final List<Pregunta> preguntas;

        private final Map<Long, Long> respuestasSeleccionadas = new HashMap<>();

        public CuestionarioDialog(Modulo modulo, Inscripcion inscripcion) {
            this.modulo = modulo;
            this.inscripcion = inscripcion;
            this.preguntas = preguntaService.obtenerPreguntasPorModulo(modulo.getId());

            setWidth("700px");
            setHeight("600px");

            VerticalLayout layout = new VerticalLayout();
            layout.setPadding(true);
            layout.setSpacing(true);

            Label titulo = new Label("Cuestionario: " + modulo.getTitulo());
            layout.add(titulo);

            for (Pregunta pregunta : preguntas) {
                VerticalLayout preguntaLayout = new VerticalLayout();
                preguntaLayout.add(new Paragraph(pregunta.getEnunciado()));

                RadioButtonGroup<Respuesta> opciones = new RadioButtonGroup<>();
                opciones.setItems(pregunta.getRespuestas());
                opciones.setItemLabelGenerator(Respuesta::getTexto);
                opciones.addValueChangeListener(event -> {
                    if (event.getValue() != null) {
                        respuestasSeleccionadas.put(pregunta.getId(), event.getValue().getId());
                    }
                });

                preguntaLayout.add(opciones);
                layout.add(preguntaLayout);
            }

            Button resolverBtn = new Button("Resolver");
            resolverBtn.addClickListener(e -> {
                if (respuestasSeleccionadas.size() < preguntas.size()) {
                    Notification.show("Debes responder todas las preguntas");
                    return;
                }

                int correctas = 0;
                for (Pregunta pregunta : preguntas) {
                    Long respuestaIdSeleccionada = respuestasSeleccionadas.get(pregunta.getId());
                    if (respuestaIdSeleccionada != null) {
                        for (Respuesta r : pregunta.getRespuestas()) {
                            if (r.getId().equals(respuestaIdSeleccionada) && r.isCorrecta()) {
                                correctas++;
                            }
                        }
                    }
                }

                double porcentaje = ((double) correctas / preguntas.size()) * 100;

                ProgresoModulo progresoModulo = inscripcion.getProgresos().stream()
                    .filter(p -> p.getModulo().getId().equals(modulo.getId()))
                    .findFirst().orElse(null);

                if (progresoModulo == null) {
                    progresoModulo = new ProgresoModulo();
                    progresoModulo.setInscripcion(inscripcion);
                    progresoModulo.setModulo(modulo);
                    inscripcion.getProgresos().add(progresoModulo);
                }

                progresoModulo.setCalificacion(porcentaje);
                progresoModulo.setCompletado(true);
                progresoModuloService.guardar(progresoModulo); 

                Notification.show("Cuestionario resuelto. Puntuación: " + String.format("%.1f", porcentaje) + "%");
                close();

                UI.getCurrent().getPage().reload();
                removeAll();
                
            });

            layout.add(resolverBtn);
            add(layout);
        }
    }
    
}

