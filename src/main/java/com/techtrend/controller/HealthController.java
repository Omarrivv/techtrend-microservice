package com.techtrend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para verificar el estado de salud de la aplicación
 */
@RestController
@RequestMapping("/health")
public class HealthController {

    /**
     * Endpoint para verificar el estado de salud de la aplicación
     * @return Información del estado de la aplicación
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("application", "TechTrend Microservice");
        response.put("version", "1.0.0");
        response.put("message", "¡TechTrend está funcionando correctamente! 🚀");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint raíz para verificar que la aplicación responde
     * @return Mensaje de bienvenida
     */
    @GetMapping("/")
    public ResponseEntity<Map<String, String>> root() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "¡Bienvenido a TechTrend! 🎉");
        response.put("status", "running");
        response.put("api", "/api");
        response.put("health", "/health");
        response.put("h2-console", "/h2-console");
        
        return ResponseEntity.ok(response);
    }
}
