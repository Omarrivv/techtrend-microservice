package com.techtrend.common.exception;

/**
 * Excepción lanzada cuando un producto no se encuentra en el catálogo
 * 
 * @author TechTrend Team
 */
public class ProductNotFoundException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructor con mensaje de error
     * 
     * @param message Mensaje descriptivo del error
     */
    public ProductNotFoundException(String message) {
        super(message);
    }
    
    /**
     * Constructor con ID del producto
     * 
     * @param productId ID del producto no encontrado
     */
    public ProductNotFoundException(Long productId) {
        super("Producto con ID " + productId + " no encontrado");
    }
    
    /**
     * Constructor con mensaje y causa
     * 
     * @param message Mensaje descriptivo del error
     * @param cause Causa original de la excepción
     */
    public ProductNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
