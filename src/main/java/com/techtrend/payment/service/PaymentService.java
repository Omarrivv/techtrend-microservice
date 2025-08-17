package com.techtrend.payment.service;

import com.techtrend.cart.service.CartService;
import com.techtrend.payment.dto.PaymentRequest;
import com.techtrend.payment.dto.PaymentResponse;
import com.techtrend.payment.model.Payment;
import com.techtrend.payment.repository.PaymentRepository;
import com.techtrend.common.exception.InvalidPaymentException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Servicio principal de pagos para TechTrend
 * 
 * Maneja el procesamiento de pagos, validación de montos y estados
 * 
 * @author TechTrend Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final CartService cartService;

    @Value("${app.payment.max-amount:100000.00}")
    private BigDecimal maxAmount;

    /**
     * Procesa un pago para un pedido
     * 
     * @param request Solicitud de pago
     * @param userId ID del usuario
     * @return Pago procesado
     */
    public PaymentResponse processPayment(PaymentRequest request, Long userId) {
        log.info("Procesando pago para pedido {}: monto {}", request.getOrderId(), request.getAmount());

        // Validar el pago
        validatePayment(request);

        // Crear el pago
        Payment payment = new Payment();
        payment.setOrderId(request.getOrderId());
        payment.setAmount(request.getAmount());
        payment.setStatus(Payment.PaymentStatus.PENDING);
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setDescription(request.getDescription());
        payment.setCurrency(request.getCurrency() != null ? request.getCurrency() : "PEN");
        payment.setUserId(userId);

        // Generar ID de transacción único
        String transactionId = generateTransactionId();
        payment.setTransactionId(transactionId);

        // Guardar el pago
        Payment savedPayment = paymentRepository.save(payment);
        log.info("Pago creado con ID: {}", savedPayment.getId());

        // Simular procesamiento del pago
        boolean paymentSuccess = simulatePaymentProcessing(savedPayment);
        
        if (paymentSuccess) {
            savedPayment.markAsCompleted();
            log.info("Pago completado exitosamente: {}", savedPayment.getId());
        } else {
            savedPayment.markAsFailed("Error en procesamiento de pago");
            log.warn("Pago falló: {}", savedPayment.getId());
        }

        Payment finalPayment = paymentRepository.save(savedPayment);
        
        return convertToResponse(finalPayment);
    }

    /**
     * Obtiene un pago por su ID
     * 
     * @param paymentId ID del pago
     * @return Pago encontrado
     */
    public PaymentResponse getPaymentById(Long paymentId) {
        log.info("Obteniendo pago con ID: {}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> {
                    log.warn("Pago no encontrado con ID: {}", paymentId);
                    return new IllegalArgumentException("Pago no encontrado");
                });

        return convertToResponse(payment);
    }

    /**
     * Obtiene el estado de un pago
     * 
     * @param paymentId ID del pago
     * @return Estado del pago
     */
    public String getPaymentStatus(Long paymentId) {
        log.info("Obteniendo estado del pago: {}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> {
                    log.warn("Pago no encontrado con ID: {}", paymentId);
                    return new IllegalArgumentException("Pago no encontrado");
                });

        return payment.getStatus().name();
    }

    /**
     * Actualiza el estado de un pago
     * 
     * @param paymentId ID del pago
     * @param status Nuevo estado
     * @return Pago actualizado
     */
    public PaymentResponse updatePaymentStatus(Long paymentId, Payment.PaymentStatus status) {
        log.info("Actualizando estado del pago {} a: {}", paymentId, status);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> {
                    log.warn("Pago no encontrado con ID: {}", paymentId);
                    return new IllegalArgumentException("Pago no encontrado");
                });

        switch (status) {
            case COMPLETED:
                payment.markAsCompleted();
                break;
            case FAILED:
                payment.markAsFailed("Estado actualizado manualmente a fallido");
                break;
            case PENDING:
                payment.setStatus(Payment.PaymentStatus.PENDING);
                payment.setUpdatedAt(LocalDateTime.now());
                break;
        }

        Payment updatedPayment = paymentRepository.save(payment);
        log.info("Estado del pago actualizado: {} -> {}", paymentId, status);

        return convertToResponse(updatedPayment);
    }

    /**
     * Obtiene pagos por usuario
     * 
     * @param userId ID del usuario
     * @return Lista de pagos del usuario
     */
    public List<PaymentResponse> getPaymentsByUser(Long userId) {
        log.info("Obteniendo pagos del usuario: {}", userId);

        List<Payment> payments = paymentRepository.findByUserId(userId);
        
        log.info("Pagos obtenidos para usuario {}: {} pagos", userId, payments.size());
        
        return payments.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene pagos por estado
     * 
     * @param status Estado del pago
     * @return Lista de pagos con el estado especificado
     */
    public List<PaymentResponse> getPaymentsByStatus(Payment.PaymentStatus status) {
        log.info("Obteniendo pagos con estado: {}", status);

        List<Payment> payments = paymentRepository.findByStatus(status);
        
        log.info("Pagos obtenidos con estado {}: {} pagos", status, payments.size());
        
        return payments.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene pagos por pedido
     * 
     * @param orderId ID del pedido
     * @return Lista de pagos del pedido
     */
    public List<PaymentResponse> getPaymentsByOrder(Long orderId) {
        log.info("Obteniendo pagos del pedido: {}", orderId);

        List<Payment> payments = paymentRepository.findByOrderId(orderId);
        
        log.info("Pagos obtenidos para pedido {}: {} pagos", orderId, payments.size());
        
        return payments.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene pagos pendientes
     * 
     * @return Lista de pagos pendientes
     */
    public List<PaymentResponse> getPendingPayments() {
        log.info("Obteniendo pagos pendientes");

        List<Payment> payments = paymentRepository.findPendingPayments();
        
        log.info("Pagos pendientes obtenidos: {} pagos", payments.size());
        
        return payments.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene estadísticas de pagos
     * 
     * @return Mapa con estadísticas
     */
    public java.util.Map<String, Object> getPaymentStatistics() {
        log.info("Obteniendo estadísticas de pagos");

        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        
        stats.put("totalPayments", paymentRepository.count());
        stats.put("pendingPayments", paymentRepository.countByStatus(Payment.PaymentStatus.PENDING));
        stats.put("completedPayments", paymentRepository.countByStatus(Payment.PaymentStatus.COMPLETED));
        stats.put("failedPayments", paymentRepository.countByStatus(Payment.PaymentStatus.FAILED));
        stats.put("totalCompletedAmount", paymentRepository.getTotalCompletedPayments());
        
        log.info("Estadísticas de pagos obtenidas");
        
        return stats;
    }

    /**
     * Valida un pago antes de procesarlo
     * 
     * @param request Solicitud de pago
     * @throws InvalidPaymentException si el pago es inválido
     */
    private void validatePayment(PaymentRequest request) {
        // Validar monto
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Monto inválido: {}", request.getAmount());
            throw new InvalidPaymentException("Monto debe ser mayor a cero");
        }

        // Validar monto máximo
        if (request.getAmount().compareTo(maxAmount) > 0) {
            log.warn("Monto excede el límite máximo: {} > {}", request.getAmount(), maxAmount);
            throw new InvalidPaymentException("Monto excede el límite máximo permitido");
        }

        // Validar ID de pedido
        if (request.getOrderId() == null) {
            log.warn("ID de pedido nulo");
            throw new InvalidPaymentException("ID de pedido es obligatorio");
        }

        // Verificar si ya existe un pago para este pedido
        List<Payment> existingPayments = paymentRepository.findByOrderId(request.getOrderId());
        if (!existingPayments.isEmpty()) {
            log.warn("Ya existe un pago para el pedido: {}", request.getOrderId());
            throw new InvalidPaymentException("Ya existe un pago para este pedido");
        }
    }

    /**
     * Genera un ID de transacción único
     * 
     * @return ID de transacción
     */
    private String generateTransactionId() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * Simula el procesamiento de un pago
     * 
     * @param payment Pago a procesar
     * @return true si el pago fue exitoso, false en caso contrario
     */
    private boolean simulatePaymentProcessing(Payment payment) {
        // Simulación simple: 90% de éxito
        double random = Math.random();
        boolean success = random < 0.9;
        
        log.info("Simulando procesamiento de pago {}: {}", payment.getId(), success ? "EXITOSO" : "FALLIDO");
        
        return success;
    }

    /**
     * Convierte una entidad Payment a PaymentResponse
     * 
     * @param payment Entidad Payment
     * @return PaymentResponse
     */
    private PaymentResponse convertToResponse(Payment payment) {
        return new PaymentResponse(
            payment.getId(),
            payment.getOrderId(),
            payment.getAmount(),
            payment.getStatus().name(),
            payment.getPaymentMethod(),
            payment.getTransactionId(),
            payment.getCurrency(),
            payment.getDescription(),
            payment.getUserId(),
            payment.getCreatedAt(),
            payment.getUpdatedAt(),
            payment.getProcessedAt(),
            payment.getFailureReason()
        );
    }
}
