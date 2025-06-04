package com.academiaenlinea.academiaenlinea.view;

import com.academiaenlinea.academiaenlinea.model.Curso;
import com.academiaenlinea.academiaenlinea.model.Modulo;
import com.academiaenlinea.academiaenlinea.service.CursoService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Route("cursos")
@RolesAllowed({"INSTRUCTOR", "ADMIN", "ALUMNO"})
public class CursoView extends VerticalLayout {

    private final CursoService cursoService;

    private final Grid<Curso> grid = new Grid<>(Curso.class);
    private final TextField titulo = new TextField("Título");
    private final TextArea descripcion = new TextArea("Descripción");
    private final DatePicker fechaInicio = new DatePicker("Fecha de inicio");
    private final DatePicker fechaFin = new DatePicker("Fecha de fin");
    private final ComboBox<Curso.Nivel> nivel = new ComboBox<>("Nivel");
    private final ComboBox<Curso.EstadoCurso> estado = new ComboBox<>("Estado");
    private final VerticalLayout modulosLayout = new VerticalLayout();
    private final Button agregarModuloBtn = new Button("Agregar módulo");
    private final Button guardarBtn = new Button("Guardar curso");
    private final Button eliminarBtn = new Button("Eliminar curso");
    private final Button inscribirseBtn = new Button("Inscribirse");


    private final List<Modulo> modulosTemp = new ArrayList<>();
    private Curso cursoActual;

    public CursoView(CursoService cursoService) {
        this.cursoService = cursoService;
        
        eliminarBtn.setEnabled(false);
        setPadding(true);
        setSpacing(true);

        nivel.setItems(Curso.Nivel.values());
        estado.setItems(Curso.EstadoCurso.values());

        HorizontalLayout botones = new HorizontalLayout(agregarModuloBtn, guardarBtn, eliminarBtn);

        grid.setColumns("titulo", "nivel", "estado", "fechaInicio", "fechaFin");
        grid.asSingleSelect().addValueChangeListener(e -> cargarCurso(e.getValue()));
String rol = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
    .stream()
    .findFirst()
    .map(auth -> auth.getAuthority())
    .orElse("");

if (rol.equals("ROLE_ADMIN") || rol.equals("ROLE_INSTRUCTOR")) {
    grid.setItems(cursoService.listarTodosLosCursos()); // método que debe listar todos los cursos
    agregarModuloBtn.setVisible(true);
    guardarBtn.setVisible(true);
    eliminarBtn.setVisible(true);
} else if (rol.equals("ROLE_ALUMNO")) {
    grid.setItems(cursoService.listarCursosPublicados()); // método para cursos publicados solo
    agregarModuloBtn.setVisible(false);
    guardarBtn.setVisible(false);
    eliminarBtn.setVisible(false);
inscribirseBtn.addClickListener(e -> {
        Curso cursoSeleccionado = grid.asSingleSelect().getValue();
        if (cursoSeleccionado == null) {
            Notification.show("Selecciona un curso para inscribirte");
            return;
        }
        // lógica inscripción
        Notification.show("Te has inscrito en el curso: " + cursoSeleccionado.getTitulo());
    });

    if (rol.equals("ROLE_ALUMNO")) {
        add(inscribirseBtn);
    }}
boolean esAlumno = rol.equals("ROLE_ALUMNO");
titulo.setReadOnly(esAlumno);
descripcion.setReadOnly(esAlumno);
fechaInicio.setReadOnly(esAlumno);
fechaFin.setReadOnly(esAlumno);
nivel.setReadOnly(esAlumno);
estado.setReadOnly(esAlumno);

modulosLayout.setEnabled(!esAlumno);
        agregarModuloBtn.addClickListener(e -> agregarModuloForm(null));
        guardarBtn.addClickListener(e -> guardarCurso());
        eliminarBtn.addClickListener(e -> {
    if (cursoActual != null && cursoActual.getId() != null) {
        cursoService.eliminarCurso(cursoActual.getId());
        Notification.show("Curso eliminado");
        limpiarFormulario();
        actualizarCursosUsuarioActual(); // Refresca el grid
    } else {
        Notification.show("No hay curso seleccionado para eliminar");
    }
});

        add(grid, titulo, descripcion, fechaInicio, fechaFin, nivel, estado, modulosLayout, botones);
    }

    private void actualizarCursosUsuarioActual() {
    String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
    grid.setItems(cursoService.listarCursosPorInstructor(email));
    }
    private void agregarBotonInscribirse() {
    Button inscribirseBtn = new Button("Inscribirse");
    inscribirseBtn.addClickListener(e -> {
        Curso cursoSeleccionado = grid.asSingleSelect().getValue();
        if (cursoSeleccionado == null) {
            Notification.show("Selecciona un curso para inscribirte");
            return;
        }
        // Aquí debes implementar la lógica de inscripción (puede ser llamada a un servicio)
        Notification.show("Te has inscrito en el curso: " + cursoSeleccionado.getTitulo());
    });

    add(inscribirseBtn);
}

    private void agregarModuloForm(Modulo moduloExistente) {
        TextField tituloModulo = new TextField("Título del módulo");
        tituloModulo.setWidthFull();
        if (moduloExistente != null) tituloModulo.setValue(moduloExistente.getTitulo());

        Button eliminar = new Button("Eliminar");
        HorizontalLayout moduloRow = new HorizontalLayout(tituloModulo, eliminar);
        moduloRow.setWidthFull();

        eliminar.addClickListener(e -> {
            modulosLayout.remove(moduloRow);
            modulosTemp.removeIf(m -> m.getTitulo().equals(tituloModulo.getValue()));
        });

        modulosLayout.add(moduloRow);

        if (moduloExistente == null) {
    Modulo nuevo = Modulo.builder()
            .titulo(tituloModulo.getValue())
            .orden(modulosTemp.size() + 1)
            .build();
    modulosTemp.add(nuevo);
} else {
    modulosTemp.add(moduloExistente);
}
    }

    private void cargarCurso(Curso curso) {
        if (curso == null) return;

        cursoActual = curso;
        titulo.setValue(curso.getTitulo());
        descripcion.setValue(curso.getDescripcion());
        fechaInicio.setValue(curso.getFechaInicio());
        fechaFin.setValue(curso.getFechaFin());
        nivel.setValue(curso.getNivel());
        estado.setValue(curso.getEstado());

        modulosLayout.removeAll();
        modulosTemp.clear();

        for (Modulo m : curso.getModulos()) {
            agregarModuloForm(m);
            modulosTemp.add(m);
        }
        eliminarBtn.setEnabled(true);
    }

    private void guardarCurso() {
        if (titulo.isEmpty() || descripcion.isEmpty() || fechaInicio.isEmpty() || fechaFin.isEmpty() || nivel.isEmpty() || estado.isEmpty()) {
            Notification.show("Completa todos los campos del curso");
            return;
        }

        Curso curso = cursoActual != null ? cursoActual : new Curso();
        curso.setTitulo(titulo.getValue());
        curso.setDescripcion(descripcion.getValue());
        curso.setFechaInicio(fechaInicio.getValue());
        curso.setFechaFin(fechaFin.getValue());
        curso.setNivel(nivel.getValue());
        curso.setEstado(estado.getValue());

        curso.setModulos(modulosTemp);

       String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        cursoService.getInstructorPorEmail(email).ifPresent(instructor -> curso.setInstructor(instructor));

        cursoService.crearOCrearCurso(curso);
        Notification.show("Curso guardado exitosamente");
        limpiarFormulario();
actualizarCursosSegunRol();    }

    private void limpiarFormulario() {
        titulo.clear();
        descripcion.clear();
        fechaInicio.setValue(LocalDate.now());
        fechaFin.setValue(null);
        nivel.clear();
        estado.clear();
        modulosLayout.removeAll();
        modulosTemp.clear();
        cursoActual = null;
        eliminarBtn.setEnabled(false);
    }
    private void actualizarCursosSegunRol() {
    String rol = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
        .stream()
        .findFirst()
        .map(auth -> auth.getAuthority())
        .orElse("");

    if (rol.equals("ROLE_ADMIN") || rol.equals("ROLE_INSTRUCTOR")) {
        grid.setItems(cursoService.listarTodosLosCursos());
    } else if (rol.equals("ROLE_ALUMNO")) {
        grid.setItems(cursoService.listarCursosPublicados());
    }
}
}
