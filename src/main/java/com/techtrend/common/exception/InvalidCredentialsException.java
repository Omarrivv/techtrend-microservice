package com.techtrend.common.exception;

/**
 * Excepción lanzada cuando las credenciales de autenticación son inválidas
 * 
 * @author TechTrend Team
 */
public class InvalidCredentialsException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructor con mensaje de error
     * 
     * @param message Mensaje descriptivo del error
     */
    public InvalidCredentialsException(String message) {
        super(message);
    }
    
    /**
     * Constructor con mensaje y causa
     * 
     * @param message Mensaje descriptivo del error
     * @param cause Causa original de la excepción
     */
    public InvalidCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }
}
