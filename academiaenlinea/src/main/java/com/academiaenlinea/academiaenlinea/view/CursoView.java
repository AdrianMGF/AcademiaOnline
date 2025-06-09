package com.academiaenlinea.academiaenlinea.view;

import com.academiaenlinea.academiaenlinea.model.Curso;
import com.academiaenlinea.academiaenlinea.model.Modulo;
import com.academiaenlinea.academiaenlinea.model.Usuario;
import com.academiaenlinea.academiaenlinea.service.CursoService;
import com.academiaenlinea.academiaenlinea.service.InscripcionService;
import com.academiaenlinea.academiaenlinea.service.UsuarioService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
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
   private final TextField tituloModulo = new TextField("Título del Módulo");
private final IntegerField ordenModulo = new IntegerField("Orden");
private final TextArea descripcionModulo = new TextArea("Descripción");
private final VerticalLayout formularioLayout = new VerticalLayout();
private final TextField filtroTitulo = new TextField("Buscar por título");
private final ComboBox<Curso.EstadoCurso> filtroEstado = new ComboBox<>("Filtrar por estado");
private final Button agregarCursoBtn = new Button("Agregar Curso");
private final Dialog formularioDialog = new Dialog();
private final IntegerField capacidad = new IntegerField("Capacidad");
private final H2 tituloVista = new H2();


    private final List<Modulo> modulosTemp = new ArrayList<>();
    private Curso cursoActual;
    private final UsuarioService usuarioService;
private final InscripcionService inscripcionService;

    public CursoView(CursoService cursoService, UsuarioService usuarioService, InscripcionService inscripcionService) {
    this.cursoService = cursoService;
    this.usuarioService = usuarioService;
    this.inscripcionService = inscripcionService;
        
        eliminarBtn.setEnabled(false);
        setPadding(true);
        setSpacing(true);

        nivel.setItems(Curso.Nivel.values());
        estado.setItems(Curso.EstadoCurso.values());

        HorizontalLayout botones = new HorizontalLayout(agregarModuloBtn, guardarBtn, eliminarBtn, inscribirseBtn);
        add(tituloVista);
        grid.setColumns("titulo", "nivel", "estado", "fechaInicio", "fechaFin","capacidad");
        grid.asSingleSelect().addValueChangeListener(e -> cargarCurso(e.getValue()));
        String rol = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
            .stream()
            .findFirst()
            .map(auth -> auth.getAuthority())
            .orElse("");

        if (rol.equals("ROLE_ADMIN") || rol.equals("ROLE_INSTRUCTOR")) {
            tituloVista.setText("Gestión de Cursos");
            add(agregarCursoBtn);
agregarCursoBtn.addClickListener(e -> {
    limpiarFormulario();  
    formularioLayout.setVisible(true);
    formularioDialog.open();
});
            grid.setItems(cursoService.listarTodosLosCursos()); 
            agregarModuloBtn.setVisible(true);
            guardarBtn.setVisible(true);
            eliminarBtn.setVisible(true);
            inscribirseBtn.setVisible(false);
        } else if (rol.equals("ROLE_ALUMNO")) {
            tituloVista.setText("Catálogo de Cursos Disponibles");
            grid.setItems(cursoService.listarCursosPublicados()
    .stream()
    .filter(c -> c.getCapacidad() != null && c.getCapacidad() > 0)
    .toList());
            agregarModuloBtn.setVisible(false);
            guardarBtn.setVisible(false);
            eliminarBtn.setVisible(false);
            inscribirseBtn.setVisible(true);
        inscribirseBtn.addClickListener(e -> {
    Curso cursoSeleccionado = grid.asSingleSelect().getValue();
    if (cursoSeleccionado == null) {
        Notification.show("Selecciona un curso para inscribirte");
        return;
    }

    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
Usuario alumno = usuarioService.buscarPorUsername(userDetails.getUsername());

boolean exito = inscripcionService.inscribirAlumno(alumno, cursoSeleccionado);

if (exito) {
    Notification.show("Inscripción exitosa");
    grid.setItems(cursoService.listarCursosPublicados());
    formularioDialog.close();
} else {
    Notification.show("Ya estás inscrito o no hay cupo");
}
});

    }
        boolean esAlumno = rol.equals("ROLE_ALUMNO");
        titulo.setReadOnly(esAlumno);
        descripcion.setReadOnly(esAlumno);
        fechaInicio.setReadOnly(esAlumno);
        fechaFin.setReadOnly(esAlumno);
        nivel.setReadOnly(esAlumno);
        estado.setReadOnly(esAlumno);
        capacidad.setReadOnly(esAlumno);

        modulosLayout.setEnabled(!esAlumno);
                agregarModuloBtn.addClickListener(e -> agregarModuloForm(null));
                guardarBtn.addClickListener(e -> guardarCurso());
                eliminarBtn.addClickListener(e -> {
            if (cursoActual != null && cursoActual.getId() != null) {
                cursoService.eliminarCurso(cursoActual.getId());
                Notification.show("Curso eliminado");
                limpiarFormulario();
                actualizarCursosUsuarioActual(); 
            } else {
                Notification.show("No hay curso seleccionado para eliminar");
            }
        });

        formularioLayout.add(titulo, descripcion, fechaInicio, fechaFin, nivel, estado,capacidad, modulosLayout, botones);

        formularioLayout.setVisible(false); 
        filtroTitulo.setPlaceholder("Título...");
        filtroTitulo.setClearButtonVisible(true);

        filtroEstado.setItems(Curso.EstadoCurso.values());
        filtroEstado.setPlaceholder("Estado...");
        filtroEstado.setClearButtonVisible(true);

        filtroTitulo.addValueChangeListener(e -> filtrarCursos());
        filtroEstado.addValueChangeListener(e -> filtrarCursos());

        HorizontalLayout filtrosLayout = new HorizontalLayout(filtroTitulo, filtroEstado);
        if (rol.equals("ROLE_ALUMNO")) {
            filtroEstado.setVisible(false);
        }
        add(filtrosLayout);
        add(grid); 
formularioDialog.add(formularioLayout); 
}

private void filtrarCursos() {
    String tituloFiltro = filtroTitulo.getValue().trim().toLowerCase();
    Curso.EstadoCurso estadoFiltro = filtroEstado.getValue();

    String rol = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
        .stream()
        .findFirst()
        .map(auth -> auth.getAuthority())
        .orElse("");
List<Curso> cursos;
if (rol.equals("ROLE_ADMIN") || rol.equals("ROLE_INSTRUCTOR")) {
    cursos = cursoService.listarTodosLosCursos();
} else {
    cursos = cursoService.listarCursosPublicados();
}
    List<Curso> filtrados = cursos.stream()
        .filter(c -> (tituloFiltro.isEmpty() || c.getTitulo().toLowerCase().contains(tituloFiltro)))
        .filter(c -> (estadoFiltro == null || c.getEstado() == estadoFiltro))
        .toList();

    grid.setItems(filtrados);
}

    private void actualizarCursosUsuarioActual() {
     grid.setItems(cursoService.listarTodosLosCursos());
    }
    

    private void agregarModuloForm(Modulo moduloExistente) {
         String rol = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
        .stream()
        .findFirst()
        .map(auth -> auth.getAuthority())
        .orElse("");
    boolean esAlumno = rol.equals("ROLE_ALUMNO");
    TextField tituloModuloField = new TextField("Título del módulo");
    IntegerField ordenModuloField = new IntegerField("Orden");
    TextArea descripcionModuloField = new TextArea("Descripción");

    tituloModuloField.setWidth("200px");
    ordenModuloField.setWidth("100px");
    descripcionModuloField.setWidth("300px");

    Button eliminar = new Button("Eliminar");
    HorizontalLayout moduloRow;
if (!esAlumno) {
    moduloRow = new HorizontalLayout(tituloModuloField, ordenModuloField, descripcionModuloField, eliminar);
    eliminar.addClickListener(e -> modulosLayout.remove(moduloRow));
} else {
    moduloRow = new HorizontalLayout(tituloModuloField, ordenModuloField, descripcionModuloField);
}
    moduloRow.setWidthFull();

    if (moduloExistente != null) {
        tituloModuloField.setValue(moduloExistente.getTitulo());
        ordenModuloField.setValue(moduloExistente.getOrden());
        descripcionModuloField.setValue(moduloExistente.getDescripcion());
    }

    eliminar.addClickListener(e -> {
        modulosLayout.remove(moduloRow);
    });

    modulosLayout.add(moduloRow);
}


    private void cargarCurso(Curso curso) {
        if (curso == null) return;

        cursoActual = curso;
        formularioLayout.setVisible(true);
        titulo.setValue(curso.getTitulo());
        descripcion.setValue(curso.getDescripcion());
        fechaInicio.setValue(curso.getFechaInicio());
        fechaFin.setValue(curso.getFechaFin());
        nivel.setValue(curso.getNivel());
        estado.setValue(curso.getEstado());
        capacidad.setValue(curso.getCapacidad());

        modulosLayout.removeAll();
        modulosTemp.clear();

        for (Modulo m : curso.getModulos()) {
            agregarModuloForm(m);
            modulosTemp.add(m);
        }
        eliminarBtn.setEnabled(true);
        formularioDialog.open();
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
        curso.setCapacidad(capacidad.getValue());

        List<Modulo> modulosActualizados = new ArrayList<>();

for (var componente : modulosLayout.getChildren().toList()) {
    if (componente instanceof HorizontalLayout layout) {
        TextField tituloField = (TextField) layout.getComponentAt(0);
        IntegerField ordenField = (IntegerField) layout.getComponentAt(1);
        TextArea descripcionField = (TextArea) layout.getComponentAt(2);

        if (!tituloField.isEmpty()) {
            Modulo mod = Modulo.builder()
                    .titulo(tituloField.getValue())
                    .orden(ordenField.getValue() != null ? ordenField.getValue() : 1)
                    .descripcion(descripcionField.getValue())
                    .curso(curso)
                    .build();
                    if (cursoActual != null) {
                        cursoActual.getModulos().stream()
                        .filter(m -> m.getTitulo().equals(tituloField.getValue()))
                        .findFirst()
                        .ifPresent(m -> mod.setId(m.getId()));
                    }
            mod.setCurso(curso);
            modulosActualizados.add(mod);
        }
    }
}

curso.setModulos(modulosActualizados);

       
        cursoService.crearOCrearCurso(curso);
        formularioDialog.close();
        Notification.show("Curso guardado exitosamente");
        limpiarFormulario();
actualizarCursosSegunRol();    
}

    private void limpiarFormulario() {
        titulo.clear();
        descripcion.clear();
        fechaInicio.setValue(LocalDate.now());
        fechaFin.setValue(null);
        nivel.clear();
        estado.clear();
        capacidad.clear();
        modulosLayout.removeAll();
        modulosTemp.clear();
        cursoActual = null;
        eliminarBtn.setEnabled(false);
        formularioLayout.setVisible(false);
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
