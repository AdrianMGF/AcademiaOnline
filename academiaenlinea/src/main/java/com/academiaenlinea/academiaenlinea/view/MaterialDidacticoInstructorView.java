package com.academiaenlinea.academiaenlinea.view;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.StandardCopyOption;

import com.academiaenlinea.academiaenlinea.model.Curso;
import com.academiaenlinea.academiaenlinea.model.Modulo;
import com.academiaenlinea.academiaenlinea.repository.CursoRepository;
import com.academiaenlinea.academiaenlinea.service.ArchivoService;
import com.academiaenlinea.academiaenlinea.service.UsuarioService;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.Route;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.io.IOException;
import java.io.InputStream;

import jakarta.annotation.security.RolesAllowed;

@Route("instructor/material")
@RolesAllowed("INSTRUCTOR")
public class MaterialDidacticoInstructorView extends VerticalLayout {

    private final UsuarioService usuarioService;
    private final ArchivoService archivoService;
    private final CursoRepository cursoRepo;

    private ComboBox<Curso> cursosCombo = new ComboBox<>("Selecciona Curso");
    private ComboBox<Modulo> modulosCombo = new ComboBox<>("Selecciona Módulo");
    private Upload upload;

    public MaterialDidacticoInstructorView(UsuarioService usuarioService, ArchivoService archivoService, CursoRepository cursoRepo) {
        this.usuarioService = usuarioService;
        this.archivoService = archivoService;
        this.cursoRepo = cursoRepo;

        cursosCombo.setItemLabelGenerator(Curso::getTitulo);
        modulosCombo.setItemLabelGenerator(Modulo::getTitulo);

        cursosCombo.addValueChangeListener(event -> {
            Curso curso = event.getValue();
            if (curso != null) {
                modulosCombo.setItems(curso.getModulos());
            } else {
                modulosCombo.clear();
            }
        });

        upload = new Upload();
        upload.setAcceptedFileTypes(".pdf", ".pptx", "video/*");
        upload.setMaxFiles(5);

        // Define dónde se guardarán los archivos temporalmente (en memoria o en disco)
        MemoryBuffer buffer = new MemoryBuffer();
        upload.setReceiver(buffer);

        upload.addSucceededListener(event -> {
            Modulo modulo = modulosCombo.getValue();
            if (modulo == null) {
                Notification.show("Por favor, selecciona un módulo antes de subir archivos");
                return;
            }

            String fileName = event.getFileName();
            String mimeType = event.getMIMEType();

            try (InputStream is = buffer.getInputStream()) {
                // Aquí guardamos el archivo en disco, sistema de ficheros, o almacenamiento en la nube.
                // Ejemplo: guardar en carpeta local "uploads"
                Path basePath = Paths.get("uploads", "modulos", String.valueOf(modulo.getId()));
                Files.createDirectories(basePath);

                Path target = basePath.resolve(fileName);
                Files.copy(is, target, StandardCopyOption.REPLACE_EXISTING);

                // Guardar la info del archivo en BD
                String url = "/files/modulos/" + modulo.getId() + "/" + fileName; // url para acceder después
                archivoService.guardarArchivo(modulo.getId(), fileName, mimeType, url);

                Notification.show("Archivo subido correctamente");
            } catch (IOException e) {
                Notification.show("Error subiendo archivo: " + e.getMessage(), 3000, Notification.Position.MIDDLE);
            }
        });

        add(cursosCombo, modulosCombo, upload);
    }
}

