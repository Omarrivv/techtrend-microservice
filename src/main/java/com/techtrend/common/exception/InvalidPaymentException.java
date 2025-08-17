package com.techtrend.common.exception;

/**
 * Excepción lanzada cuando un pago es inválido o no puede ser procesado
 * 
 * @author TechTrend Team
 */
public class InvalidPaymentException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructor con mensaje de error
     * 
     * @param message Mensaje descriptivo del error
     */
    public InvalidPaymentException(String message) {
        super(message);
    }
    
    /**
     * Constructor con detalles del pago
     * 
     * @param orderId ID del pedido
     * @param amount Monto del pago
     * @param reason Razón del error
     */
    public InvalidPaymentException(Long orderId, Double amount, String reason) {
        super(String.format("Pago inválido para el pedido %d con monto %.2f. Razón: %s", 
                           orderId, amount, reason));
    }
    
    /**
     * Constructor con mensaje y causa
     * 
     * @param message Mensaje descriptivo del error
     * @param cause Causa original de la excepción
     */
    public InvalidPaymentException(String message, Throwable cause) {
        super(message, cause);
    }
}
