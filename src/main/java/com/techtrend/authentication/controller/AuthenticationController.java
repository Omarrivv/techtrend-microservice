package com.techtrend.authentication.controller;

import com.techtrend.authentication.dto.AuthResponse;
import com.techtrend.authentication.dto.LoginRequest;
import com.techtrend.authentication.dto.UserRegistrationRequest;
import com.techtrend.authentication.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador REST para el microservicio de autenticación
 * 
 * Proporciona endpoints para registro, login y validación de tokens
 * 
 * @author TechTrend Team
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@Tag(name = "Autenticación", description = "Endpoints para registro, login y validación de usuarios")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    /**
     * Endpoint para registrar un nuevo usuario
     * 
     * @param request Solicitud de registro
     * @return Respuesta con token de autenticación
     */
    @PostMapping("/register")
    @Operation(
        summary = "Registrar nuevo usuario",
        description = "Crea una nueva cuenta de usuario con email, contraseña y rol"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos de registro inválidos",
            content = @Content(schema = @Schema(implementation = AuthResponse.class)))
    })
    public ResponseEntity<AuthResponse> registerUser(
        @Parameter(description = "Datos de registro del usuario", required = true)
        @Valid @RequestBody UserRegistrationRequest request) {
        log.info("Recibida solicitud de registro para: {}", request.getEmail());
        
        try {
            AuthResponse response = authenticationService.registerUser(request);
            log.info("Usuario registrado exitosamente: {}", request.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error en registro de usuario: {}", e.getMessage());
            AuthResponse errorResponse = AuthResponse.error(e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Endpoint para iniciar sesión
     * 
     * @param request Solicitud de login
     * @return Respuesta con token de autenticación
     */
    @PostMapping("/login")
    @Operation(
        summary = "Iniciar sesión",
        description = "Autentica un usuario con email y contraseña"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login exitoso",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas",
            content = @Content(schema = @Schema(implementation = AuthResponse.class)))
    })
    public ResponseEntity<AuthResponse> login(
        @Parameter(description = "Credenciales de login", required = true)
        @Valid @RequestBody LoginRequest request) {
        log.info("Recibida solicitud de login para: {}", request.getEmail());
        
        try {
            AuthResponse response = authenticationService.login(request);
            log.info("Login exitoso para: {}", request.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error en login: {}", e.getMessage());
            AuthResponse errorResponse = AuthResponse.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    /**
     * Endpoint para validar un token JWT
     * 
     * @param token Token a validar
     * @return Respuesta con el estado de validación
     */
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestParam String token) {
        log.info("Recibida solicitud de validación de token");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean isValid = authenticationService.validateToken(token);
            
            if (isValid) {
                String email = authenticationService.getEmailFromToken(token);
                String role = authenticationService.getRoleFromToken(token);
                
                response.put("valid", true);
                response.put("email", email);
                response.put("role", role);
                response.put("message", "Token válido");
                
                log.info("Token válido para usuario: {}", email);
                return ResponseEntity.ok(response);
            } else {
                response.put("valid", false);
                response.put("message", "Token inválido o expirado");
                
                log.warn("Token inválido");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            log.error("Error validando token: {}", e.getMessage());
            response.put("valid", false);
            response.put("message", "Error validando token");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Endpoint para obtener información del usuario desde un token
     * 
     * @param token Token JWT
     * @return Información del usuario
     */
    @GetMapping("/user-info")
    public ResponseEntity<Map<String, Object>> getUserInfo(@RequestParam String token) {
        log.info("Recibida solicitud de información de usuario");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (authenticationService.validateToken(token)) {
                String email = authenticationService.getEmailFromToken(token);
                String role = authenticationService.getRoleFromToken(token);
                
                response.put("email", email);
                response.put("role", role);
                response.put("valid", true);
                
                log.info("Información de usuario obtenida: {}", email);
                return ResponseEntity.ok(response);
            } else {
                response.put("valid", false);
                response.put("message", "Token inválido");
                
                log.warn("Token inválido para obtener información de usuario");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            log.error("Error obteniendo información de usuario: {}", e.getMessage());
            response.put("valid", false);
            response.put("message", "Error obteniendo información de usuario");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Endpoint de health check para el microservicio
     * 
     * @return Estado del servicio
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Authentication Service");
        response.put("timestamp", java.time.LocalDateTime.now());
        
        log.info("Health check solicitado");
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para verificar si un email ya está registrado
     * 
     * @param email Email a verificar
     * @return Respuesta indicando si el email existe
     */
    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Object>> checkEmailExists(@RequestParam String email) {
        log.info("Verificando existencia de email: {}", email);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean exists = authenticationService.userExists(email);
            response.put("exists", exists);
            response.put("email", email);
            
            log.info("Verificación de email completada: {} - {}", email, exists);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error verificando email: {}", e.getMessage());
            response.put("error", "Error verificando email");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
