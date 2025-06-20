package com.academiaenlinea.academiaenlinea.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import com.academiaenlinea.academiaenlinea.security.AuditorAwareImpl;
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class AuditoriaConfig {
    
    @Bean
    public AuditorAware<String> auditorProvider() {
        return new AuditorAwareImpl();
    }
}