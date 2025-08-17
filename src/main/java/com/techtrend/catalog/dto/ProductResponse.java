package com.techtrend.catalog.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para las respuestas de productos
 * 
 * @author TechTrend Team
 */
@Data
@NoArgsConstructor
public class ProductResponse {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer quantity;
    private String category;
    private String brand;
    private String model;
    private String sku;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastStockUpdate;

    /**
     * Constructor para crear respuesta desde entidad Product
     */
    public ProductResponse(Long id, String name, String description, BigDecimal price, 
                          Integer quantity, String category, String brand, String model, 
                          String sku, Boolean isActive, LocalDateTime createdAt, 
                          LocalDateTime updatedAt, LocalDateTime lastStockUpdate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
        this.brand = brand;
        this.model = model;
        this.sku = sku;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.lastStockUpdate = lastStockUpdate;
    }
}
