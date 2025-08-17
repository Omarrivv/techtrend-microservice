package com.techtrend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Clase principal de la aplicación TechTrend Microservice
 * 
 * Esta aplicación implementa una arquitectura de microservicios para
 * la plataforma de e-commerce TechTrend, especializada en equipos informáticos.
 * 
 * @author TechTrend Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableJpaRepositories
public class TechTrendApplication {

    /**
     * Método principal que inicia la aplicación Spring Boot
     * 
     * @param args Argumentos de línea de comandos
     */
    public static void main(String[] args) {
        SpringApplication.run(TechTrendApplication.class, args);
        
        System.out.println("🚀 TechTrend Microservice iniciado exitosamente!");
        System.out.println("📊 API disponible en: http://localhost:8080/api");
        System.out.println("🗄️  H2 Console disponible en: http://localhost:8080/api/h2-console");
        System.out.println("📚 Documentación: http://localhost:8080/api/swagger-ui.html");
    }
}
