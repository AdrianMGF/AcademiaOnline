package com.academiaenlinea.academiaenlinea.view;

import com.academiaenlinea.academiaenlinea.model.Usuario;
import com.academiaenlinea.academiaenlinea.repository.UsuarioRepository;
import com.academiaenlinea.academiaenlinea.service.UsuarioService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

@Route("admin")
@PageTitle("Panel de Administración")
@RolesAllowed("ADMIN")
public class AdminHomeView extends VerticalLayout {

    public AdminHomeView(AuthenticationContext authContext, UsuarioService usuarioService) {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setSpacing(true);

        String username = authContext.getPrincipalName().orElse("Administrador");
        Optional<Usuario> optionalUsuario = usuarioService.buscarPorCorreo(username);
        String nombre = optionalUsuario.map(Usuario::getNombreCompleto).orElse("Administrador");
        H1 bienvenida = new H1("Bienvenido, " + nombre);

        Button gestionUsuarios = new Button("Gestión de Usuarios", e ->
                getUI().ifPresent(ui -> ui.navigate("admin/usuarios")));

        Button gestionCursos = new Button("Gestión de Cursos", e ->
                getUI().ifPresent(ui -> ui.navigate("cursos")));
Button gestionCuestionarios = new Button("Gestión de Cuestionarios", e ->
                getUI().ifPresent(ui -> ui.navigate("cuestionarios")));
        add(bienvenida, gestionUsuarios, gestionCursos, gestionCuestionarios);

        Button logoutButton = new Button("Cerrar Sesión");
        logoutButton.addClickListener(event -> {
            HttpServletRequest request = (HttpServletRequest) VaadinServletRequest.getCurrent().getHttpServletRequest();
            try {
                request.logout(); 
            } catch (jakarta.servlet.ServletException e) { 
                e.printStackTrace();
            }
        });
        add(logoutButton);
    }
}
