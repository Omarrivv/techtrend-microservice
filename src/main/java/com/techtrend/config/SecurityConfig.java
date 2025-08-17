package com.techtrend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de seguridad para TechTrend
 * Define beans necesarios y configura la seguridad de la aplicación
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Bean para encriptar contraseñas usando BCrypt
     * @return PasswordEncoder configurado con BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * Configuración de seguridad HTTP
     * Permite acceso a endpoints públicos y configura JWT
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable())) // Para H2 console
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos
                .requestMatchers("/").permitAll()
                .requestMatchers("/api").permitAll()
                .requestMatchers("/api/").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/catalog/**").permitAll()
                .requestMatchers("/api/cart/**").permitAll()
                .requestMatchers("/api/payment/**").permitAll()
                .requestMatchers("/api/health/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/h22-console/**").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/swagger-ui.html").permitAll()
                .requestMatchers("/v3/api-docs/**").permitAll()
                .requestMatchers("/error").permitAll()
                // Cualquier otra petición requiere autenticación
                .anyRequest().authenticated()
            );
        
        return http.build();
    }
}
