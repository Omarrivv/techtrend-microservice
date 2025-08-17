package com.techtrend.payment.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que representa un pago en el sistema TechTrend
 * 
 * @author TechTrend Team
 */
@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El ID del pedido es obligatorio")
    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser mayor a cero")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @NotNull(message = "El estado es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "currency", length = 3)
    private String currency = "PEN";

    @Column(name = "description")
    private String description;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "failure_reason")
    private String failureReason;

    /**
     * Enum que define los estados de pago disponibles
     */
    public enum PaymentStatus {
        PENDING,    // Pago pendiente de procesamiento
        COMPLETED,  // Pago completado exitosamente
        FAILED      // Pago fallido
    }

    /**
     * Marca el pago como completado
     */
    public void markAsCompleted() {
        this.status = PaymentStatus.COMPLETED;
        this.processedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Marca el pago como fallido
     * 
     * @param reason Razón del fallo
     */
    public void markAsFailed(String reason) {
        this.status = PaymentStatus.FAILED;
        this.failureReason = reason;
        this.processedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Verifica si el pago está pendiente
     * 
     * @return true si está pendiente, false en caso contrario
     */
    public boolean isPending() {
        return PaymentStatus.PENDING.equals(this.status);
    }

    /**
     * Verifica si el pago está completado
     * 
     * @return true si está completado, false en caso contrario
     */
    public boolean isCompleted() {
        return PaymentStatus.COMPLETED.equals(this.status);
    }

    /**
     * Verifica si el pago falló
     * 
     * @return true si falló, false en caso contrario
     */
    public boolean isFailed() {
        return PaymentStatus.FAILED.equals(this.status);
    }

    /**
     * Obtiene el monto formateado con la moneda
     * 
     * @return String con el monto formateado
     */
    public String getFormattedAmount() {
        return String.format("%s %.2f", this.currency, this.amount);
    }

    /**
     * Verifica si el pago es válido para procesar
     * 
     * @return true si es válido, false en caso contrario
     */
    public boolean isValidForProcessing() {
        return this.isPending() && this.amount != null && this.amount.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Actualiza el método de pago
     * 
     * @param paymentMethod Nuevo método de pago
     */
    public void updatePaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Asigna un ID de transacción
     * 
     * @param transactionId ID de transacción
     */
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = PaymentStatus.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
