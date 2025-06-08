package com.academiaenlinea.academiaenlinea.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.academiaenlinea.academiaenlinea.model.Usuario;
import com.academiaenlinea.academiaenlinea.model.Usuario.Rol;
import com.academiaenlinea.academiaenlinea.service.UsuarioService;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;


@Route(value = "admin/usuarios")
@RolesAllowed("ADMIN")
public class AdminUsuariosView extends VerticalLayout {

    private final UsuarioService usuarioService;
    private final Grid<Usuario> grid;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public AdminUsuariosView(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
        this.grid = new Grid<>(Usuario.class, false);

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        H2 titulo = new H2("Gestión de Usuarios");
Button addButton = new Button("Añadir Usuario", e -> abrirFormularioNuevoUsuario());
HorizontalLayout header = new HorizontalLayout(titulo, addButton);
        header.setWidthFull();
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);

        add(header);
        
        configurarGrid();
        add(grid);

        cargarUsuarios();
    }

    private void configurarGrid() {
        grid.addColumn(Usuario::getId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(Usuario::getNombreCompleto).setHeader("Nombre");
        grid.addColumn(Usuario::getEmail).setHeader("Correo");
        grid.addColumn(usuario -> usuario.getRol().name()).setHeader("Rol");
        grid.addColumn(usuario -> usuario.isBloqueado() ? "Sí" : "No").setHeader("Bloqueado");

        grid.addComponentColumn(usuario -> {
            Button bloquearBtn = new Button(usuario.isBloqueado() ? "Desbloquear" : "Bloquear", e -> {
                usuario.setBloqueado(!usuario.isBloqueado());
                usuarioService.guardar(usuario);
                cargarUsuarios();
            });
            return bloquearBtn;
        });

        grid.addComponentColumn(usuario -> {
    HorizontalLayout layout = new HorizontalLayout();
    layout.setSpacing(true);
    layout.setAlignItems(Alignment.CENTER);

    ComboBox<Rol> rolCombo = new ComboBox<>();
    rolCombo.setItems(Rol.values());
    rolCombo.setValue(usuario.getRol());
    rolCombo.setWidth("150px");

    Button cambiarRolBtn = new Button("Cambiar Rol");
    cambiarRolBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
    cambiarRolBtn.addClickListener(e -> {
        usuario.setRol(rolCombo.getValue());
        usuarioService.guardarRol(usuario);
        cargarUsuarios();
    });

    layout.add(rolCombo, cambiarRolBtn);
    return layout;
});
        grid.addComponentColumn(usuario -> {
            Button eliminarBtn = new Button("Eliminar", e -> {
                usuarioService.eliminar(usuario);
                cargarUsuarios();
            });
            eliminarBtn.getStyle().set("color", "red");
            return eliminarBtn;
        });
    }

    private void cargarUsuarios() {
        grid.setItems(usuarioService.obtenerTodos());
    }
     private void abrirFormularioNuevoUsuario() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Nuevo Usuario");

        TextField nombre = new TextField("Nombre");
        TextField correo = new TextField("Correo");
        PasswordField contrasena = new PasswordField("Contraseña");
        ComboBox<Rol> rolCombo = new ComboBox<>("Rol");
        rolCombo.setItems(Rol.values());
        rolCombo.setValue(Rol.INSTRUCTOR); 

        Checkbox bloqueado = new Checkbox("¿Bloqueado?");
        bloqueado.setValue(false); 

        Button guardarBtn = new Button("Guardar", e -> {
            Usuario nuevo = new Usuario();
            nuevo.setNombreCompleto(nombre.getValue());
            nuevo.setEmail(correo.getValue());
            nuevo.setPassword(passwordEncoder.encode(contrasena.getValue()));
            nuevo.setRol(rolCombo.getValue());
            nuevo.setBloqueado(bloqueado.getValue());

            usuarioService.guardar(nuevo);
            cargarUsuarios();
            dialog.close();
        });

        Button cancelarBtn = new Button("Cancelar", e -> dialog.close());

        HorizontalLayout botones = new HorizontalLayout(guardarBtn, cancelarBtn);
        VerticalLayout layout = new VerticalLayout(nombre, correo, contrasena, rolCombo, bloqueado, botones);

        dialog.add(layout);
        dialog.open();
    }
}

