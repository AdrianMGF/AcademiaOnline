package com.academiaenlinea.academiaenlinea.view;

import com.academiaenlinea.academiaenlinea.model.Usuario;
import com.academiaenlinea.academiaenlinea.repository.UsuarioRepository;
import com.academiaenlinea.academiaenlinea.service.UsuarioService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.RolesAllowed;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

@Route("instructor")
@PageTitle("Panel de Instructor")
@RolesAllowed("INSTRUCTOR")
public class InstructorHomeView extends VerticalLayout {

    public InstructorHomeView(AuthenticationContext authContext, UsuarioService usuarioService) {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setSpacing(true);

        String username = authContext.getPrincipalName().orElse("Instructor");
        Optional<Usuario> optionalUsuario = usuarioService.buscarPorCorreo(username);
        String nombre = optionalUsuario.map(Usuario::getNombreCompleto).orElse("Instructor");
        H1 bienvenida = new H1("Bienvenido, " + nombre);

        
        Button gestionCursos = new Button("Gestión de Cursos", e ->
                getUI().ifPresent(ui -> ui.navigate("cursos")));
        Button gestionUsuarios = new Button("Subir Archivos", e ->
                getUI().ifPresent(ui -> ui.navigate("instructor/cursos")));

        add(bienvenida, gestionUsuarios, gestionCursos);
    }
}