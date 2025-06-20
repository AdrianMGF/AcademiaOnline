package com.academiaenlinea.academiaenlinea.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.academiaenlinea.academiaenlinea.model.Curso;
import com.academiaenlinea.academiaenlinea.model.Modulo;
import com.academiaenlinea.academiaenlinea.service.CursoService;
import com.academiaenlinea.academiaenlinea.service.ModuloService;
import com.academiaenlinea.academiaenlinea.model.Pregunta;
import com.academiaenlinea.academiaenlinea.model.Respuesta;
import com.academiaenlinea.academiaenlinea.service.PreguntaService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;

@Route("cuestionarios")
@RolesAllowed({"ADMIN", "INSTRUCTOR"})
public class CuestionarioView extends VerticalLayout {

    private final CursoService cursoService;

    private final ModuloService moduloService;
    private final PreguntaService preguntaService;
private final ComboBox<Curso> cursoComboBox = new ComboBox<>("Selecciona Curso");
    private final ComboBox<Modulo> moduloComboBox = new ComboBox<>("Selecciona Módulo");
    private final Grid<Pregunta> preguntaGrid = new Grid<>(Pregunta.class, false);
    private final Button agregarPreguntaBtn = new Button("Agregar Pregunta");


    public CuestionarioView(CursoService cursoService, ModuloService moduloService, PreguntaService preguntaService) {

        this.cursoService = cursoService;
        this.moduloService = moduloService;
        this.preguntaService = preguntaService;

        configurarCombos();
        configurarGridPreguntas();

        agregarPreguntaBtn.addClickListener(e -> agregarPreguntaForm(null));
        

        HorizontalLayout seleccionLayout = new HorizontalLayout(cursoComboBox, moduloComboBox);
        seleccionLayout.setSpacing(true);
        seleccionLayout.setAlignItems(Alignment.BASELINE);

        add(new H2("Gestión de Cuestionarios"));
        add(seleccionLayout, preguntaGrid, agregarPreguntaBtn);
        setPadding(true);
        setSpacing(true);
        
        Button backButton = new Button("Volver");
        backButton.addClickListener(event -> {
            UI.getCurrent().getPage().getHistory().back();
        });
        add(backButton);
    }
 private void configurarCombos() {
        cursoComboBox.setItems(cursoService.listarCursos());
        cursoComboBox.setItemLabelGenerator(Curso::getTitulo);
        cursoComboBox.setWidth("300px");

        moduloComboBox.setItemLabelGenerator(Modulo::getTitulo);
        moduloComboBox.setWidth("300px");

        cursoComboBox.addValueChangeListener(event -> {
    Curso cursoSeleccionado = event.getValue();
    if (cursoSeleccionado != null) {
        List<Modulo> modulos = moduloService.buscarPorCursoId(cursoSeleccionado.getId());
        moduloComboBox.setItems(modulos);
        moduloComboBox.setValue(null);
        preguntaGrid.setItems(Collections.emptyList()); 
        
    } else {
        moduloComboBox.clear();
        moduloComboBox.setItems(Collections.emptyList());
        preguntaGrid.setItems(Collections.emptyList()); 
    }
});

       moduloComboBox.addValueChangeListener(event -> {
    Modulo modulo = event.getValue();
    if (modulo != null) {
        List<Pregunta> preguntas = preguntaService.obtenerPreguntasPorModulo(modulo.getId());
        preguntaGrid.setItems(preguntas);
    } else {
        preguntaGrid.setItems(Collections.emptyList());
    }
});
    }
    private void configurarGridPreguntas() {
        preguntaGrid.removeAllColumns();
        preguntaGrid.addColumn(Pregunta::getEnunciado).setHeader("Pregunta").setAutoWidth(true);

        preguntaGrid.addComponentColumn(pregunta -> {
            Button editar = new Button("Editar");
           editar.addClickListener(e -> agregarPreguntaForm(pregunta));
            return editar;
        }).setHeader("Acciones").setAutoWidth(true);
    }

    private void cargarPreguntasModulo(Modulo modulo) {
         List<Pregunta> preguntas = preguntaService.obtenerPreguntasPorModulo(modulo.getId());
    preguntaGrid.setItems(preguntas);
    }

    private void agregarPreguntaForm(Pregunta preguntaExistente) {
        if (moduloComboBox.getValue() == null) {
        Notification.show("Debes seleccionar un módulo");
        return;
    }
    PreguntaFormDialog dialog = new PreguntaFormDialog(preguntaExistente, moduloComboBox.getValue());
    dialog.open();
    }



    private class PreguntaFormDialog extends Dialog {
        private final TextArea enunciadoField = new TextArea("Enunciado");
        private final VerticalLayout respuestasLayout = new VerticalLayout();
        private final Button agregarRespuestaBtn = new Button("Agregar respuesta");
        private final Button guardarPreguntaBtn = new Button("Guardar");
        private final Button cancelarBtn = new Button("Cancelar");

        private Pregunta pregunta;
private final Modulo moduloSeleccionado;
        public PreguntaFormDialog(Pregunta preguntaExistente, Modulo moduloSeleccionado) {
            this.moduloSeleccionado = moduloSeleccionado;
            this.pregunta = preguntaExistente != null ? preguntaExistente : new Pregunta();
            setWidth("700px");
            setHeight("500px");

            enunciadoField.setWidthFull();
            enunciadoField.setValue(pregunta.getEnunciado() != null ? pregunta.getEnunciado() : "");

            agregarRespuestaBtn.addClickListener(e -> agregarRespuestaForm(null));

            guardarPreguntaBtn.addClickListener(e -> guardarPregunta());
            cancelarBtn.addClickListener(e -> this.close());

            add(enunciadoField, respuestasLayout, agregarRespuestaBtn, new HorizontalLayout(guardarPreguntaBtn, cancelarBtn));

            List<Respuesta> respuestas = pregunta.getRespuestas();
if (respuestas != null && !respuestas.isEmpty()) {
    respuestas.forEach(this::agregarRespuestaForm);
}
        }

        private void agregarRespuestaForm(Respuesta respuestaExistente) {
            HorizontalLayout fila = new HorizontalLayout();
            TextField textoRespuesta = new TextField();
            textoRespuesta.setWidth("400px");
            textoRespuesta.setValue(respuestaExistente != null ? respuestaExistente.getTexto() : "");

            RadioButtonGroup<Boolean> correctaRadio = new RadioButtonGroup<>();
            correctaRadio.setItems(true, false);
            correctaRadio.setItemLabelGenerator(b -> b ? "Correcta" : "Incorrecta");
            correctaRadio.setValue(respuestaExistente != null ? respuestaExistente.isCorrecta() : false);

            Button eliminarBtn = new Button("Eliminar");
            eliminarBtn.addClickListener(e -> respuestasLayout.remove(fila));

            fila.add(textoRespuesta, correctaRadio, eliminarBtn);
            fila.setAlignItems(Alignment.CENTER);
            respuestasLayout.add(fila);
        }

        private void guardarPregunta() {
    String enunciado = enunciadoField.getValue().trim();
    if (enunciado.isEmpty()) {
        Notification.show("El enunciado es obligatorio");
        return;
    }

    List<Respuesta> respuestas = new ArrayList<>();
    int correctas = 0;

    for (var comp : respuestasLayout.getChildren().toList()) {
        if (comp instanceof HorizontalLayout fila) {
            TextField texto = (TextField) fila.getComponentAt(0);
            RadioButtonGroup<Boolean> correcta = (RadioButtonGroup<Boolean>) fila.getComponentAt(1);

            String textoResp = texto.getValue().trim();
            if (textoResp.isEmpty()) {
                Notification.show("Todas las respuestas deben tener texto");
                return;
            }

            boolean esCorrecta = Boolean.TRUE.equals(correcta.getValue());
            if (esCorrecta) correctas++;

            Respuesta r = Respuesta.builder()
                    .texto(textoResp)
                    .correcta(esCorrecta)
                    .pregunta(pregunta)
                    .build();
            respuestas.add(r);
        }
    }

    if (correctas == 0) {
        Notification.show("Debe marcar al menos una respuesta como correcta");
        return;
    }

    pregunta.setEnunciado(enunciado);
    pregunta.setRespuestas(respuestas);
    pregunta.setModulo(moduloSeleccionado);

    preguntaService.guardarPreguntaConRespuestas(pregunta);

    cargarPreguntasModulo(moduloSeleccionado);

    Notification.show("Pregunta guardada correctamente");
    this.close();
}

    }
}

