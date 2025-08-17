package com.techtrend.cart.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que representa un item en el carrito de compras
 * 
 * @author TechTrend Team
 */
@Entity
@Table(name = "cart_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El ID del usuario es obligatorio")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull(message = "El ID del producto es obligatorio")
    @Column(name = "product_id", nullable = false)
    private Long productId;

    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a cero")
    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "total_price", precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_sku")
    private String productSku;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_active")
    private Boolean isActive = true;

    /**
     * Calcula el precio total del item
     * 
     * @return Precio total (precio unitario * cantidad)
     */
    public BigDecimal calculateTotalPrice() {
        if (this.unitPrice != null && this.quantity != null) {
            this.totalPrice = this.unitPrice.multiply(BigDecimal.valueOf(this.quantity));
        }
        return this.totalPrice;
    }

    /**
     * Actualiza la cantidad del item
     * 
     * @param newQuantity Nueva cantidad
     */
    public void updateQuantity(int newQuantity) {
        if (newQuantity > 0) {
            this.quantity = newQuantity;
            this.calculateTotalPrice();
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Verifica si el item está activo
     * 
     * @return true si está activo, false en caso contrario
     */
    public boolean isItemActive() {
        return this.isActive != null && this.isActive;
    }

    /**
     * Desactiva el item del carrito
     */
    public void deactivate() {
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Activa el item del carrito
     */
    public void activate() {
        this.isActive = true;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Verifica si el item pertenece a un usuario específico
     * 
     * @param userId ID del usuario
     * @return true si pertenece al usuario, false en caso contrario
     */
    public boolean belongsToUser(Long userId) {
        return this.userId.equals(userId);
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.calculateTotalPrice();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        this.calculateTotalPrice();
    }
}
