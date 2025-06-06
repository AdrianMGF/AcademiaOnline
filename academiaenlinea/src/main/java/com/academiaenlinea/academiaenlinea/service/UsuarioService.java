package com.academiaenlinea.academiaenlinea.service;

import com.academiaenlinea.academiaenlinea.model.Usuario;
import com.academiaenlinea.academiaenlinea.model.TokenVerificacion;
import com.academiaenlinea.academiaenlinea.repository.UsuarioRepository;
import com.academiaenlinea.academiaenlinea.repository.TokenVerificacionRepository;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepo;
    private final TokenVerificacionRepository tokenRepo;
    private final JavaMailSender mailSender;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepo, TokenVerificacionRepository tokenRepo, JavaMailSender mailSender) {
        this.usuarioRepo = usuarioRepo;
        this.tokenRepo = tokenRepo;
        this.mailSender = mailSender;
    }

    @Transactional
    public void registrarUsuario(String nombre, String email, String password) {
        if (usuarioRepo.existsByEmail(email)) {
            throw new IllegalArgumentException("Correo ya registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNombreCompleto(nombre);
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setActivo(false);
        usuario.setRol(Usuario.Rol.valueOf("ALUMNO"));
        usuarioRepo.save(usuario);

        String token = UUID.randomUUID().toString();
        TokenVerificacion verificacion = new TokenVerificacion();
        verificacion.setToken(token);
        verificacion.setUsuario(usuario);
        verificacion.setFechaExpiracion(LocalDateTime.now().plusHours(24));
        tokenRepo.save(verificacion);

        enviarCorreoActivacion(email, token);
    }

    private void enviarCorreoActivacion(String email, String token) {
        String url = "http://localhost:8080/activar?token=" + token;
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(email);
        mensaje.setSubject("Activación de cuenta");
        mensaje.setText("Haz clic en este enlace para activar tu cuenta:\n" + url);
        mailSender.send(mensaje);
    }

    public boolean activarUsuario(String token) {
        Optional<TokenVerificacion> opt = tokenRepo.findByToken(token);
        if (opt.isEmpty()) return false;

        TokenVerificacion tv = opt.get();

        if (tv.getFechaExpiracion().isBefore(LocalDateTime.now())) return false;

        Usuario usuario = tv.getUsuario();
        usuario.setActivo(true);
        usuarioRepo.save(usuario);

        tokenRepo.delete(tv);
        return true;
    }

    public boolean login(String email, String password) {
    Optional<Usuario> usuarioOpt = usuarioRepo.findByEmail(email);
    if (usuarioOpt.isPresent()) {
        Usuario usuario = usuarioOpt.get();
        boolean contraseñaCoincide = passwordEncoder.matches(password, usuario.getPassword());

            return contraseñaCoincide && usuario.isActivo();
    }
    return false;
    
}
public Optional<Usuario> findByEmail(String email) {
    return usuarioRepo.findByEmail(email);
}
public Usuario buscarPorUsername(String username) {
    return usuarioRepo.findByEmail(username)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + username));
}

}
