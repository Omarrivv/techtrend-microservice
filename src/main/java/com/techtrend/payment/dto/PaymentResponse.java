package com.techtrend.payment.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para las respuestas de pagos
 * 
 * @author TechTrend Team
 */
@Data
public class PaymentResponse {

    private Long id;
    private Long orderId;
    private BigDecimal amount;
    private String status;
    private String paymentMethod;
    private String transactionId;
    private String currency;
    private String description;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime processedAt;
    private String failureReason;

    /**
     * Constructor para crear respuesta desde entidad Payment
     */
    public PaymentResponse(Long id, Long orderId, BigDecimal amount, String status, 
                          String paymentMethod, String transactionId, String currency, 
                          String description, Long userId, LocalDateTime createdAt, 
                          LocalDateTime updatedAt, LocalDateTime processedAt, String failureReason) {
        this.id = id;
        this.orderId = orderId;
        this.amount = amount;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.transactionId = transactionId;
        this.currency = currency;
        this.description = description;
        this.userId = userId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.processedAt = processedAt;
        this.failureReason = failureReason;
    }
}
