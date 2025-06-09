package com.academiaenlinea.academiaenlinea.security;

import com.academiaenlinea.academiaenlinea.model.Usuario;
import com.academiaenlinea.academiaenlinea.service.UsuarioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UsuarioService usuarioService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        String correo = authentication.getName(); // El username es el correo
        Optional<Usuario> usuarioOpt = usuarioService.buscarPorCorreo(correo);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();

            String redirectUrl = switch (usuario.getRol()) {
                case ADMIN -> "/admin";
                case INSTRUCTOR -> "/instructor";
                case ALUMNO -> "/alumno";
                default -> "/login?error"; // En caso de que el rol no esté definido
            };

            response.sendRedirect(redirectUrl);

        } else {
            response.sendRedirect("/login?error"); // Usuario no encontrado
        }
    }
}
