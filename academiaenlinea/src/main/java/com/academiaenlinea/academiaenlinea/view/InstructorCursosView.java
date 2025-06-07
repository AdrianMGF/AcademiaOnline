package com.academiaenlinea.academiaenlinea.view;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.StandardCopyOption;
import java.util.List;

import com.academiaenlinea.academiaenlinea.model.Curso;
import com.academiaenlinea.academiaenlinea.model.Modulo;
import com.academiaenlinea.academiaenlinea.repository.CursoRepository;
import com.academiaenlinea.academiaenlinea.service.ArchivoService;
import com.academiaenlinea.academiaenlinea.service.UsuarioService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.Route;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;


import jakarta.annotation.security.RolesAllowed;

@Route("instructor/cursos")
@RolesAllowed("INSTRUCTOR")
public class InstructorCursosView extends VerticalLayout {

    private final CursoRepository cursoRepo;
    private final ArchivoService archivoService;

    private Grid<Curso> cursosGrid = new Grid<>(Curso.class, false);
    private Grid<Modulo> modulosGrid = new Grid<>(Modulo.class, false);

    private Upload upload;
    private Button subirArchivoBtn;

    private Modulo moduloSeleccionado;

    public InstructorCursosView(CursoRepository cursoRepo, ArchivoService archivoService) {
        this.cursoRepo = cursoRepo;
        this.archivoService = archivoService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        add(new H2("Cursos y módulos - Subir material didáctico"));

        configurarGrids();

        add(cursosGrid, modulosGrid);

        crearComponenteSubida();

        add(subirArchivoBtn, upload);
    }

    private void configurarGrids() {
        // Cursos
        cursosGrid.addColumn(Curso::getTitulo).setHeader("Curso");
        cursosGrid.addColumn(Curso::getNivel).setHeader("Nivel");
        cursosGrid.setHeight("200px");

        List<Curso> cursos = cursoRepo.findAll(); // aquí podrías filtrar por instructor
        cursosGrid.setItems(cursos);

        cursosGrid.asSingleSelect().addValueChangeListener(e -> {
            moduloSeleccionado = null;
            modulosGrid.deselectAll();
            if (e.getValue() != null) {
                modulosGrid.setItems(e.getValue().getModulos());
            } else {
                modulosGrid.setItems();
            }
        });

        // Módulos
        modulosGrid.addColumn(Modulo::getOrden).setHeader("Orden");
        modulosGrid.addColumn(Modulo::getTitulo).setHeader("Módulo");
        modulosGrid.setHeight("200px");
        modulosGrid.setSelectionMode(Grid.SelectionMode.SINGLE);

        modulosGrid.asSingleSelect().addValueChangeListener(event -> {
            moduloSeleccionado = event.getValue();
        });
    }

    private void crearComponenteSubida() {
    MemoryBuffer buffer = new MemoryBuffer();
    upload = new Upload(buffer);
    upload.setDropLabel(new Label("Arrastra o selecciona archivo para subir"));
    upload.setMaxFiles(1);
    upload.setAcceptedFileTypes(".pdf", ".pptx", "video/mp4", "video/webm");

    subirArchivoBtn = new Button("Subir archivo");
    subirArchivoBtn.setEnabled(false);

    // Variables para guardar datos del archivo subido
    final String[] nombreArchivoSubido = {null};
    final String[] tipoArchivoSubido = {null};

    // Cuando el archivo se sube con éxito, guardamos su info
    upload.addSucceededListener(event -> {
        nombreArchivoSubido[0] = event.getFileName();
        tipoArchivoSubido[0] = event.getMIMEType();

        // Habilitar el botón sólo si hay módulo seleccionado
        subirArchivoBtn.setEnabled(moduloSeleccionado != null);
    });

    // Si cambia el módulo seleccionado, habilitar o deshabilitar botón según si hay archivo cargado
    modulosGrid.asSingleSelect().addValueChangeListener(event -> {
        moduloSeleccionado = event.getValue();
        subirArchivoBtn.setEnabled(moduloSeleccionado != null && nombreArchivoSubido[0] != null);
    });

    subirArchivoBtn.addClickListener(click -> {
        if (moduloSeleccionado == null) {
            Notification.show("Selecciona un módulo primero");
            return;
        }
        if (nombreArchivoSubido[0] == null) {
            Notification.show("Selecciona un archivo primero");
            return;
        }

        try (InputStream inputStream = buffer.getInputStream()) {
            archivoService.guardarArchivo(
                moduloSeleccionado.getId(),
                nombreArchivoSubido[0],
                inputStream,
                tipoArchivoSubido[0]
            );

            Notification.show("Archivo subido con éxito");

            // Limpiar la selección de archivo
            upload.getElement().callJsFunction("clear");

            // Reset variables y botón
            nombreArchivoSubido[0] = null;
            tipoArchivoSubido[0] = null;
            subirArchivoBtn.setEnabled(false);

        } catch (IOException e) {
            Notification.show("Error subiendo archivo: " + e.getMessage());
        }
    });
}
}


