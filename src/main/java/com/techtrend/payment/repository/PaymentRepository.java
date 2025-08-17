package com.techtrend.payment.repository;

import com.techtrend.payment.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Payment
 * 
 * Proporciona métodos para acceder y manipular datos de pagos
 * 
 * @author TechTrend Team
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * Busca pagos por ID de pedido
     * 
     * @param orderId ID del pedido
     * @return Lista de pagos del pedido
     */
    List<Payment> findByOrderId(Long orderId);

    /**
     * Busca pagos por estado
     * 
     * @param status Estado del pago
     * @return Lista de pagos con el estado especificado
     */
    List<Payment> findByStatus(Payment.PaymentStatus status);

    /**
     * Busca pagos por usuario
     * 
     * @param userId ID del usuario
     * @return Lista de pagos del usuario
     */
    List<Payment> findByUserId(Long userId);

    /**
     * Busca pagos por usuario y estado
     * 
     * @param userId ID del usuario
     * @param status Estado del pago
     * @return Lista de pagos del usuario con el estado especificado
     */
    List<Payment> findByUserIdAndStatus(Long userId, Payment.PaymentStatus status);

    /**
     * Busca un pago por ID de transacción
     * 
     * @param transactionId ID de transacción
     * @return Optional con el pago si existe
     */
    Optional<Payment> findByTransactionId(String transactionId);

    /**
     * Verifica si existe un pago con el ID de transacción especificado
     * 
     * @param transactionId ID de transacción
     * @return true si existe, false en caso contrario
     */
    boolean existsByTransactionId(String transactionId);

    /**
     * Busca pagos pendientes
     * 
     * @return Lista de pagos pendientes
     */
    @Query("SELECT p FROM Payment p WHERE p.status = 'PENDING' ORDER BY p.createdAt ASC")
    List<Payment> findPendingPayments();

    /**
     * Busca pagos completados en un rango de fechas
     * 
     * @param startDate Fecha de inicio
     * @param endDate Fecha de fin
     * @return Lista de pagos completados en el rango
     */
    @Query("SELECT p FROM Payment p WHERE p.status = 'COMPLETED' AND p.processedAt BETWEEN :startDate AND :endDate")
    List<Payment> findCompletedPaymentsInDateRange(@Param("startDate") LocalDateTime startDate, 
                                                  @Param("endDate") LocalDateTime endDate);

    /**
     * Calcula el total de pagos completados
     * 
     * @return Total de pagos completados
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = 'COMPLETED'")
    BigDecimal getTotalCompletedPayments();

    /**
     * Calcula el total de pagos completados por usuario
     * 
     * @param userId ID del usuario
     * @return Total de pagos completados del usuario
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.userId = :userId AND p.status = 'COMPLETED'")
    BigDecimal getTotalCompletedPaymentsByUser(@Param("userId") Long userId);

    /**
     * Cuenta pagos por estado
     * 
     * @param status Estado del pago
     * @return Número de pagos con el estado especificado
     */
    long countByStatus(Payment.PaymentStatus status);

    /**
     * Cuenta pagos por usuario
     * 
     * @param userId ID del usuario
     * @return Número de pagos del usuario
     */
    long countByUserId(Long userId);

    /**
     * Busca pagos fallidos con razón específica
     * 
     * @param failureReason Razón del fallo
     * @return Lista de pagos fallidos con la razón especificada
     */
    List<Payment> findByStatusAndFailureReasonContaining(Payment.PaymentStatus status, String failureReason);

    /**
     * Busca pagos por método de pago
     * 
     * @param paymentMethod Método de pago
     * @return Lista de pagos con el método especificado
     */
    List<Payment> findByPaymentMethod(String paymentMethod);

    /**
     * Busca pagos por moneda
     * 
     * @param currency Moneda
     * @return Lista de pagos con la moneda especificada
     */
    List<Payment> findByCurrency(String currency);

    /**
     * Busca pagos con monto mayor a un valor específico
     * 
     * @param minAmount Monto mínimo
     * @return Lista de pagos con monto mayor al especificado
     */
    @Query("SELECT p FROM Payment p WHERE p.amount >= :minAmount ORDER BY p.amount DESC")
    List<Payment> findPaymentsWithAmountGreaterThan(@Param("minAmount") BigDecimal minAmount);

    /**
     * Busca pagos recientes (últimas 24 horas)
     * 
     * @return Lista de pagos recientes
     */
    @Query("SELECT p FROM Payment p WHERE p.createdAt >= :yesterday ORDER BY p.createdAt DESC")
    List<Payment> findRecentPayments(@Param("yesterday") LocalDateTime yesterday);

    /**
     * Busca pagos por rango de montos
     * 
     * @param minAmount Monto mínimo
     * @param maxAmount Monto máximo
     * @return Lista de pagos en el rango de montos
     */
    @Query("SELECT p FROM Payment p WHERE p.amount BETWEEN :minAmount AND :maxAmount")
    List<Payment> findPaymentsByAmountRange(@Param("minAmount") BigDecimal minAmount, 
                                           @Param("maxAmount") BigDecimal maxAmount);
}
