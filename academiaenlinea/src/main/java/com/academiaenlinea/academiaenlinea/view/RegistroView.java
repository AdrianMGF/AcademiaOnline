package com.academiaenlinea.academiaenlinea.view;

import com.academiaenlinea.academiaenlinea.service.UsuarioService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.*;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

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

        add(nombreCompleto, email, password, confirmarPassword, registrar);

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
    }

    private void limpiar() {
        nombreCompleto.clear();
        email.clear();
        password.clear();
        confirmarPassword.clear();
    }
}
