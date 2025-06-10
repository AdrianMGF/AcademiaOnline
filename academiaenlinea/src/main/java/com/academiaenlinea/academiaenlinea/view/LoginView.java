package com.academiaenlinea.academiaenlinea.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "login", autoLayout = false)
@PageTitle("Login")
@AnonymousAllowed
public class LoginView extends VerticalLayout {

   
    private final LoginForm login;

    @Autowired
    public LoginView() {
        addClassNames(LumoUtility.Display.FLEX, LumoUtility.JustifyContent.CENTER,
            LumoUtility.AlignItems.CENTER);
        setSizeFull();
        login = new LoginForm();
        LoginI18n i18n = LoginI18n.createDefault();
        
        i18n.getForm().setTitle("Inicio de sesión");
        
        i18n.getForm().setUsername("Correo electrónico");
        i18n.getForm().setPassword("Contraseña");
        i18n.getForm().setSubmit("Iniciar sesión");
        i18n.getErrorMessage().setTitle("Error de inicio");
        i18n.getErrorMessage().setMessage("Correo o contraseña incorrectos");
        i18n.getForm().setForgotPassword("");


        login.setI18n(i18n);

        login.setAction("login"); 
        add(login);

        Button registroBtn = new Button("¿No tienes cuenta? Regístrate aquí", e -> {
            UI.getCurrent().navigate("registro");
        });
        add(registroBtn);
    }

    
}
