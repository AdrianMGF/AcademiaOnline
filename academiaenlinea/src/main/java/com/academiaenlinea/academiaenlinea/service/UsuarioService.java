package com.academiaenlinea.academiaenlinea.service;

import com.academiaenlinea.academiaenlinea.model.Usuario;
import com.academiaenlinea.academiaenlinea.model.Inscripcion;
import com.academiaenlinea.academiaenlinea.model.ProgresoModulo;
import com.academiaenlinea.academiaenlinea.model.TokenVerificacion;
import com.academiaenlinea.academiaenlinea.repository.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;

import com.academiaenlinea.academiaenlinea.repository.TokenVerificacionRepository;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.academiaenlinea.academiaenlinea.repository.InscripcionRepository;


@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepo;
    private final TokenVerificacionRepository tokenRepo;
    private final JavaMailSender mailSender;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private final InscripcionRepository inscripcionRepository;

    public UsuarioService(UsuarioRepository usuarioRepo, TokenVerificacionRepository tokenRepo, JavaMailSender mailSender, InscripcionRepository inscripcionRepository) {
        this.usuarioRepo = usuarioRepo;
        this.tokenRepo = tokenRepo;
        this.mailSender = mailSender;
        this.inscripcionRepository = inscripcionRepository;
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
    public void enviarCorreoConTexto(String email, String text) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(email);
        mensaje.setSubject(text);
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
@Transactional(readOnly = true)
public Usuario buscarPorUsername(String username) {
    return usuarioRepo.findByEmail(username)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + username));
}

@Transactional(readOnly = true)
    public List<Inscripcion> obtenerInscripcionesDelUsuario(String email) {
        Usuario usuario = usuarioRepo.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        usuario.getInscripciones().size();

        return usuario.getInscripciones();
    }

    @Transactional(readOnly = true)
public Usuario buscarAlumnoConInscripciones(String username) {
    Usuario alumno = usuarioRepo.findByEmail(username)
        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

    alumno.getInscripciones().size();
    alumno.getInscripciones().forEach(i -> {
        i.getCurso().getTitulo(); 
        i.getCurso().getModulos().size();
        i.getProgresos().size();
    });

    return alumno;
}
public record ProgresoCursoResumen(double porcentajeCompletado, double promedioCalificacion) {}
@Transactional
public ProgresoCursoResumen calcularResumen(Long inscripcionId) {
    Inscripcion inscripcion = inscripcionRepository.findById(inscripcionId)
        .orElseThrow(() -> new EntityNotFoundException("Inscripcion no encontrada"));

    Hibernate.initialize(inscripcion.getProgresos());

    List<ProgresoModulo> progresos = inscripcion.getProgresos();
    if (progresos.isEmpty()) {
        return new ProgresoCursoResumen(0.0, 0.0);
    }

    long completados = progresos.stream().filter(ProgresoModulo::isCompletado).count();
    double promedio = progresos.stream()
        .filter(p -> p.getCalificacion() != null)
        .mapToDouble(ProgresoModulo::getCalificacion)
        .average()
        .orElse(0.0);

    double porcentaje = (completados * 100.0) / progresos.size();

    return new ProgresoCursoResumen(porcentaje, promedio);
}
public List<Usuario> obtenerTodos() {
        return usuarioRepo.findAll();
    }

    public void guardar(Usuario usuario) {
        usuarioRepo.save(usuario);
        String token = UUID.randomUUID().toString();
        TokenVerificacion verificacion = new TokenVerificacion();
        verificacion.setToken(token);
        verificacion.setUsuario(usuario);
        verificacion.setFechaExpiracion(LocalDateTime.now().plusHours(24));
        tokenRepo.save(verificacion);

        enviarCorreoActivacion(usuario.getEmail(), token);
    }
    public void guardarRol(Usuario usuario) {
        usuarioRepo.save(usuario);
        
    }

    public Optional<Usuario> buscarPorCorreo(String email) {
        return usuarioRepo.findByEmail(email);
    }
     @Transactional
    public void eliminar(Usuario usuario) {
    tokenRepo.deleteByUsuario(usuario);
    usuarioRepo.delete(usuario);
}

}

