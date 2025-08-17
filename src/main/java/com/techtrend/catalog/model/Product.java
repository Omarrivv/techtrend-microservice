package com.techtrend.catalog.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que representa un producto en el catálogo de TechTrend
 * 
 * @author TechTrend Team
 */
@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a cero")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @NotNull(message = "La cantidad es obligatoria")
    @PositiveOrZero(message = "La cantidad no puede ser negativa")
    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "category")
    private String category;

    @Column(name = "brand")
    private String brand;

    @Column(name = "model")
    private String model;

    @Column(name = "sku", unique = true)
    private String sku;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "last_stock_update")
    private LocalDateTime lastStockUpdate;

    /**
     * Verifica si el producto tiene stock disponible
     * 
     * @return true si hay stock disponible, false en caso contrario
     */
    public boolean hasStock() {
        return this.quantity > 0;
    }

    /**
     * Verifica si hay suficiente stock para una cantidad específica
     * 
     * @param requestedQuantity Cantidad solicitada
     * @return true si hay suficiente stock, false en caso contrario
     */
    public boolean hasSufficientStock(int requestedQuantity) {
        return this.quantity >= requestedQuantity;
    }

    /**
     * Reduce el stock del producto
     * 
     * @param quantity Cantidad a reducir
     * @throws IllegalArgumentException si no hay suficiente stock
     */
    public void reduceStock(int quantity) {
        if (!hasSufficientStock(quantity)) {
            throw new IllegalArgumentException(
                String.format("Stock insuficiente. Disponible: %d, Solicitado: %d", 
                            this.quantity, quantity)
            );
        }
        this.quantity -= quantity;
        this.lastStockUpdate = LocalDateTime.now();
    }

    /**
     * Aumenta el stock del producto
     * 
     * @param quantity Cantidad a aumentar
     */
    public void increaseStock(int quantity) {
        if (quantity > 0) {
            this.quantity += quantity;
            this.lastStockUpdate = LocalDateTime.now();
        }
    }

    /**
     * Verifica si el producto está activo
     * 
     * @return true si está activo, false en caso contrario
     */
    public boolean isProductActive() {
        return this.isActive != null && this.isActive;
    }

    /**
     * Obtiene el stock disponible
     * 
     * @return Cantidad disponible
     */
    public int getAvailableStock() {
        return this.quantity;
    }

    /**
     * Calcula el valor total del inventario
     * 
     * @return Valor total (precio * cantidad)
     */
    public BigDecimal getInventoryValue() {
        return this.price.multiply(BigDecimal.valueOf(this.quantity));
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.lastStockUpdate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
