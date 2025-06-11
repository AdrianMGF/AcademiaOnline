package com.academiaenlinea.academiaenlinea.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.academiaenlinea.academiaenlinea.model.Usuario;
import com.academiaenlinea.academiaenlinea.model.Usuario.Rol;
import com.academiaenlinea.academiaenlinea.repository.UsuarioRepository;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UsuarioRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByEmail("admin@academia.com").isEmpty()) {
                Usuario admin = new Usuario();
                admin.setNombreCompleto("Administrador");
                admin.setEmail("admin@academia.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRol(Rol.ADMIN); 
                admin.setActivo(true);
                userRepository.save(admin);
            }
        };
    }
}
