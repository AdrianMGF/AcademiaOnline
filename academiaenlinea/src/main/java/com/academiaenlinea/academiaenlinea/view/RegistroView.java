package com.academiaenlinea.academiaenlinea.view;

import com.academiaenlinea.academiaenlinea.service.UsuarioService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.*;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;

import jakarta.annotation.security.PermitAll;

import org.springframework.beans.factory.annotation.Autowired;

@Route("registro")
@PermitAll
@AnonymousAllowed
public class RegistroView extends VerticalLayout {

    private final UsuarioService usuarioService;

    private final TextField nombreCompleto = new TextField("Nombre completo");
    private final EmailField email = new EmailField("Correo electrónico");
    private final PasswordField password = new PasswordField("Contraseña");
    private final PasswordField confirmarPassword = new PasswordField("Confirmar contraseña");
    private final Button registrar = new Button("Registrarse");

    @Autowired
    public RegistroView(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;

        addClassNames(
            LumoUtility.Display.FLEX,
            LumoUtility.FlexDirection.COLUMN,
            LumoUtility.JustifyContent.CENTER,
            LumoUtility.AlignItems.CENTER
        );
        setSizeFull();
        setPadding(true);

        H1 titulo = new H1("Registro");
        add(titulo);

        FormLayout formLayout = new FormLayout();
        formLayout.add(nombreCompleto, email, password, confirmarPassword, registrar);
        formLayout.setWidth("320px"); 
        setAlignSelf(Alignment.CENTER, formLayout); 

        add(formLayout);

        registrar.addClickListener(event -> {
            if (nombreCompleto.isEmpty() || email.isEmpty() || password.isEmpty() || confirmarPassword.isEmpty()) {
                Notification.show("Completa todos los campos");
                return;
            }

            if (!password.getValue().equals(confirmarPassword.getValue())) {
                Notification.show("Las contraseñas no coinciden");
                return;
            }

            try {
                usuarioService.registrarUsuario(
                    nombreCompleto.getValue(),
                    email.getValue(),
                    password.getValue()
                );
                Notification.show("Registro exitoso. Revisa tu correo para activarlo.");
                limpiar();
            } catch (IllegalArgumentException e) {
                Notification.show(e.getMessage());
            }
        });
        Button registroBtn = new Button("¿Ya tienes cuenta? Inicia sesion aqui", e -> {
            UI.getCurrent().navigate("login");
        });
        add(registroBtn);
    }

    private void limpiar() {
        nombreCompleto.clear();
        email.clear();
        password.clear();
        confirmarPassword.clear();
    }
}
