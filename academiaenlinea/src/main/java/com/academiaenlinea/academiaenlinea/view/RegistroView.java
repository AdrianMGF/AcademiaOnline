package com.academiaenlinea.academiaenlinea.view;

import com.academiaenlinea.academiaenlinea.service.UsuarioService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route("registro")
public class RegistroView extends FormLayout {

    private final UsuarioService usuarioService;

    private final TextField nombreCompleto = new TextField("Nombre Completo");
    private final EmailField email = new EmailField("Correo Electrónico");
    private final PasswordField password = new PasswordField("Contraseña");
    private final PasswordField confirmarPassword = new PasswordField("Confirmar Contraseña");
    private final Button botonRegistrar = new Button("Registrarse");

    @Autowired
    public RegistroView(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;

        add(nombreCompleto, email, password, confirmarPassword, botonRegistrar);

        botonRegistrar.addClickListener(event -> {
            if (nombreCompleto.isEmpty() || email.isEmpty() || password.isEmpty() || confirmarPassword.isEmpty()) {
                Notification.show("Por favor, completa todos los campos");
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
                Notification.show("Registro exitoso! Revisa tu correo.");
                clearForm();
            } catch (IllegalArgumentException e) {
                Notification.show(e.getMessage());
            }
        });
    }

    private void clearForm() {
        nombreCompleto.clear();
        email.clear();
        password.clear();
        confirmarPassword.clear();
    }
}