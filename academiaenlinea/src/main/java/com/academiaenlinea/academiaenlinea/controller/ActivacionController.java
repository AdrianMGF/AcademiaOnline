package com.academiaenlinea.academiaenlinea.controller;

import com.academiaenlinea.academiaenlinea.service.UsuarioService;
import org.springframework.web.bind.annotation.*;

@RestController
public class ActivacionController {

    private final UsuarioService usuarioService;

    public ActivacionController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/activar")
    public String activar(@RequestParam String token) {
        boolean ok = usuarioService.activarUsuario(token);
        return ok ? "Cuenta activada correctamente. Ya puedes iniciar sesión." :
                    "Token inválido o expirado.";
    }
}
