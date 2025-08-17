package com.techtrend.authentication.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.techtrend.authentication.model.User.UserRole;

/**
 * DTO para las respuestas de autenticación
 * 
 * @author TechTrend Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String email;
    private UserRole role;
    private String message;
    private boolean success;

    /**
     * Constructor para respuesta exitosa
     */
    public static AuthResponse success(String token, String email, UserRole role) {
        return new AuthResponse(token, email, role, "Autenticación exitosa", true);
    }

    /**
     * Constructor para respuesta de error
     */
    public static AuthResponse error(String message) {
        return new AuthResponse(null, null, null, message, false);
    }
}
