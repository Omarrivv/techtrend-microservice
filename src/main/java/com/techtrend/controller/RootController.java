package com.techtrend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador raíz para manejar las peticiones a la raíz de la aplicación
 */
@RestController
public class RootController {

    /**
     * Endpoint raíz de la aplicación
     * @return Información de bienvenida y endpoints disponibles
     */
    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> root() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "¡Bienvenido a TechTrend E-commerce! 🚀");
        response.put("timestamp", LocalDateTime.now());
        response.put("version", "1.0.0");
        response.put("status", "running");
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("health", "/health");
        endpoints.put("authentication", "/api/auth");
        endpoints.put("catalog", "/api/catalog");
        endpoints.put("cart", "/api/cart");
        endpoints.put("payment", "/api/payment");
        endpoints.put("h2-console", "/h2-console");
        
        response.put("endpoints", endpoints);
        response.put("description", "Plataforma de e-commerce con arquitectura de microservicios");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para la raíz de la API
     * @return Información de la API
     */
    @GetMapping("/api")
    public ResponseEntity<Map<String, Object>> apiRoot() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "TechTrend API v1.0.0");
        response.put("timestamp", LocalDateTime.now());
        response.put("status", "active");
        
        Map<String, String> services = new HashMap<>();
        services.put("auth", "/api/auth");
        services.put("catalog", "/api/catalog");
        services.put("cart", "/api/cart");
        services.put("payment", "/api/payment");
        
        response.put("services", services);
        
        return ResponseEntity.ok(response);
    }
}
