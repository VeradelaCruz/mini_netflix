package com.example.user_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.context.annotation.Bean;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())              // Desactiva CSRF
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()              // Permite todos los endpoints
                )
                .httpBasic(Customizer.withDefaults());     // Mantener Basic Auth desactivado si quieres

        return http.build();
    }
}
