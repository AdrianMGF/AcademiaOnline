package com.academiaenlinea.academiaenlinea.config;

import com.academiaenlinea.academiaenlinea.model.Usuario;
import com.academiaenlinea.academiaenlinea.security.CustomLoginSuccessHandler;
import com.academiaenlinea.academiaenlinea.service.CustomUserDetailsService;
import com.academiaenlinea.academiaenlinea.view.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends VaadinWebSecurity {

 private final CustomUserDetailsService userDetailsService;
  @Autowired
    private CustomLoginSuccessHandler loginSuccessHandler;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);

        setLoginView(http, LoginView.class, "/main");
        http.userDetailsService(userDetailsService);

         http.formLogin(form -> form
            .successHandler(loginSuccessHandler)
        );

    }

    

    

}
