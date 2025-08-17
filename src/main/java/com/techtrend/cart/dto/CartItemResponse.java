package com.techtrend.cart.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para las respuestas de items del carrito
 * 
 * @author TechTrend Team
 */
@Data
@NoArgsConstructor
public class CartItemResponse {

    private Long id;
    private Long userId;
    private Long productId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private String productName;
    private String productSku;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isActive;

    /**
     * Constructor para crear respuesta desde entidad CartItem
     */
    public CartItemResponse(Long id, Long userId, Long productId, Integer quantity, 
                           BigDecimal unitPrice, BigDecimal totalPrice, String productName, 
                           String productSku, LocalDateTime createdAt, LocalDateTime updatedAt, 
                           Boolean isActive) {
        this.id = id;
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
        this.productName = productName;
        this.productSku = productSku;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isActive = isActive;
    }
}
