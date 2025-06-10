package com.academiaenlinea.academiaenlinea.service;

import com.academiaenlinea.academiaenlinea.model.Usuario;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioService usuarioService;

    public CustomUserDetailsService(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioService.findByEmail(email)
    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));
        if (usuario.isBloqueado()) {
            throw new DisabledException("El usuario está bloqueado.");
        }
    if (!usuario.isActivo()) {
            throw new UsernameNotFoundException("Usuario no activado");
        }
        

        return User.withUsername(usuario.getEmail())
            .password(usuario.getPassword())  
            .roles(usuario.getRol().name())         
            .build();
    }
}
