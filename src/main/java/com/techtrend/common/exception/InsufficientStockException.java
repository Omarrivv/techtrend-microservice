package com.techtrend.common.exception;

/**
 * Excepción lanzada cuando no hay suficiente stock para un producto
 * 
 * @author TechTrend Team
 */
public class InsufficientStockException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructor con mensaje de error
     * 
     * @param message Mensaje descriptivo del error
     */
    public InsufficientStockException(String message) {
        super(message);
    }
    
    /**
     * Constructor con detalles del producto y stock
     * 
     * @param productId ID del producto
     * @param requestedQuantity Cantidad solicitada
     * @param availableStock Stock disponible
     */
    public InsufficientStockException(Long productId, int requestedQuantity, int availableStock) {
        super(String.format("Stock insuficiente para el producto %d. Solicitado: %d, Disponible: %d", 
                           productId, requestedQuantity, availableStock));
    }
    
    /**
     * Constructor con mensaje y causa
     * 
     * @param message Mensaje descriptivo del error
     * @param cause Causa original de la excepción
     */
    public InsufficientStockException(String message, Throwable cause) {
        super(message, cause);
    }
}
