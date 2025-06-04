package com.academiaenlinea.academiaenlinea.controller;

import com.academiaenlinea.academiaenlinea.service.UsuarioService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
public class ActivacionController {

    private final UsuarioService usuarioService;

    public ActivacionController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/activar")
    public String activar(@RequestParam String token, RedirectAttributes redirectAttributes) {
        boolean ok = usuarioService.activarUsuario(token);
        if (ok) {
            redirectAttributes.addFlashAttribute("mensaje", "Cuenta activada correctamente. Ya puedes iniciar sesión.");
            return "redirect:/login";
        } else {
            redirectAttributes.addFlashAttribute("error", "Token inválido o expirado.");
            return "redirect:/registro"; // o a otra vista donde mostrar error
        }
    }
}
