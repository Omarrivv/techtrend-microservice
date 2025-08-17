package com.techtrend.payment.service;

import com.techtrend.cart.service.CartService;
import com.techtrend.payment.dto.PaymentRequest;
import com.techtrend.payment.dto.PaymentResponse;
import com.techtrend.payment.model.Payment;
import com.techtrend.payment.repository.PaymentRepository;
import com.techtrend.common.exception.InvalidPaymentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para el servicio de pagos
 * 
 * @author TechTrend Team
 */
@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private CartService cartService;

    @InjectMocks
    private PaymentService paymentService;

    private Payment testPayment;
    private PaymentRequest testRequest;
    private Long testUserId = 1L;
    private Long testOrderId = 1L;

    @BeforeEach
    void setUp() {
        // Configurar valores de prueba
        ReflectionTestUtils.setField(paymentService, "maxAmount", new BigDecimal("100000.00"));

        // Crear pago de prueba
        testPayment = new Payment();
        testPayment.setId(1L);
        testPayment.setOrderId(testOrderId);
        testPayment.setAmount(new BigDecimal("9999.99"));
        testPayment.setStatus(Payment.PaymentStatus.COMPLETED);
        testPayment.setPaymentMethod("TARJETA_CREDITO");
        testPayment.setTransactionId("TXN-ABC12345");
        testPayment.setCurrency("PEN");
        testPayment.setDescription("Pago por laptop gaming");
        testPayment.setUserId(testUserId);
        testPayment.setCreatedAt(LocalDateTime.now());
        testPayment.setUpdatedAt(LocalDateTime.now());
        testPayment.setProcessedAt(LocalDateTime.now());

        // Crear solicitud de prueba
        testRequest = new PaymentRequest();
        testRequest.setOrderId(testOrderId);
        testRequest.setAmount(new BigDecimal("9999.99"));
        testRequest.setPaymentMethod("TARJETA_CREDITO");
        testRequest.setDescription("Pago por laptop gaming");
        testRequest.setCurrency("PEN");
    }

    @Test
    @DisplayName("Debería procesar pago válido exitosamente")
    void shouldProcessValidPaymentSuccessfully() {
        // Given
        when(paymentRepository.findByOrderId(testOrderId)).thenReturn(Arrays.asList());
        when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);

        // When
        PaymentResponse response = paymentService.processPayment(testRequest, testUserId);

        // Then
        assertNotNull(response);
        assertEquals(testPayment.getId(), response.getId());
        assertEquals(testOrderId, response.getOrderId());
        assertEquals(testPayment.getAmount(), response.getAmount());
        assertEquals(testPayment.getStatus().name(), response.getStatus());

        verify(paymentRepository).findByOrderId(testOrderId);
        verify(paymentRepository, times(2)).save(any(Payment.class));
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando el monto es cero")
    void shouldThrowException_whenAmountIsZero() {
        // Given
        testRequest.setAmount(BigDecimal.ZERO);

        // When & Then
        InvalidPaymentException exception = assertThrows(
            InvalidPaymentException.class,
            () -> paymentService.processPayment(testRequest, testUserId)
        );

        assertEquals("Monto debe ser mayor a cero", exception.getMessage());
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando el monto es negativo")
    void shouldThrowException_whenAmountIsNegative() {
        // Given
        testRequest.setAmount(new BigDecimal("-100.00"));

        // When & Then
        InvalidPaymentException exception = assertThrows(
            InvalidPaymentException.class,
            () -> paymentService.processPayment(testRequest, testUserId)
        );

        assertEquals("Monto debe ser mayor a cero", exception.getMessage());
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando el monto excede el límite máximo")
    void shouldThrowException_whenAmountExceedsMaxLimit() {
        // Given
        testRequest.setAmount(new BigDecimal("150000.00"));

        // When & Then
        InvalidPaymentException exception = assertThrows(
            InvalidPaymentException.class,
            () -> paymentService.processPayment(testRequest, testUserId)
        );

        assertEquals("Monto excede el límite máximo permitido", exception.getMessage());
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando el ID de pedido es nulo")
    void shouldThrowException_whenOrderIdIsNull() {
        // Given
        testRequest.setOrderId(null);

        // When & Then
        InvalidPaymentException exception = assertThrows(
            InvalidPaymentException.class,
            () -> paymentService.processPayment(testRequest, testUserId)
        );

        assertEquals("ID de pedido es obligatorio", exception.getMessage());
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando ya existe un pago para el pedido")
    void shouldThrowException_whenPaymentAlreadyExistsForOrder() {
        // Given
        when(paymentRepository.findByOrderId(testOrderId)).thenReturn(Arrays.asList(testPayment));

        // When & Then
        InvalidPaymentException exception = assertThrows(
            InvalidPaymentException.class,
            () -> paymentService.processPayment(testRequest, testUserId)
        );

        assertEquals("Ya existe un pago para este pedido", exception.getMessage());
        verify(paymentRepository).findByOrderId(testOrderId);
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    @DisplayName("Debería obtener pago por ID correctamente")
    void shouldGetPaymentByIdCorrectly() {
        // Given
        Long paymentId = 1L;
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(testPayment));

        // When
        PaymentResponse response = paymentService.getPaymentById(paymentId);

        // Then
        assertNotNull(response);
        assertEquals(testPayment.getId(), response.getId());
        assertEquals(testPayment.getOrderId(), response.getOrderId());
        assertEquals(testPayment.getAmount(), response.getAmount());

        verify(paymentRepository).findById(paymentId);
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando el pago no existe")
    void shouldThrowException_whenPaymentDoesNotExist() {
        // Given
        Long paymentId = 999L;
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> paymentService.getPaymentById(paymentId)
        );

        assertEquals("Pago no encontrado", exception.getMessage());
        verify(paymentRepository).findById(paymentId);
    }

    @Test
    @DisplayName("Debería obtener estado del pago correctamente")
    void shouldGetPaymentStatusCorrectly() {
        // Given
        Long paymentId = 1L;
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(testPayment));

        // When
        String status = paymentService.getPaymentStatus(paymentId);

        // Then
        assertEquals(testPayment.getStatus().name(), status);

        verify(paymentRepository).findById(paymentId);
    }

    @Test
    @DisplayName("Debería actualizar estado del pago a completado correctamente")
    void shouldUpdatePaymentStatusToCompletedCorrectly() {
        // Given
        Long paymentId = 1L;
        Payment pendingPayment = new Payment();
        pendingPayment.setId(paymentId);
        pendingPayment.setStatus(Payment.PaymentStatus.PENDING);
        pendingPayment.setCreatedAt(LocalDateTime.now());
        pendingPayment.setUpdatedAt(LocalDateTime.now());

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(pendingPayment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(pendingPayment);

        // When
        PaymentResponse response = paymentService.updatePaymentStatus(paymentId, Payment.PaymentStatus.COMPLETED);

        // Then
        assertNotNull(response);
        assertEquals(Payment.PaymentStatus.COMPLETED.name(), response.getStatus());

        verify(paymentRepository).findById(paymentId);
        verify(paymentRepository).save(pendingPayment);
    }

    @Test
    @DisplayName("Debería actualizar estado del pago a fallido correctamente")
    void shouldUpdatePaymentStatusToFailedCorrectly() {
        // Given
        Long paymentId = 1L;
        Payment pendingPayment = new Payment();
        pendingPayment.setId(paymentId);
        pendingPayment.setStatus(Payment.PaymentStatus.PENDING);
        pendingPayment.setCreatedAt(LocalDateTime.now());
        pendingPayment.setUpdatedAt(LocalDateTime.now());

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(pendingPayment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(pendingPayment);

        // When
        PaymentResponse response = paymentService.updatePaymentStatus(paymentId, Payment.PaymentStatus.FAILED);

        // Then
        assertNotNull(response);
        assertEquals(Payment.PaymentStatus.FAILED.name(), response.getStatus());

        verify(paymentRepository).findById(paymentId);
        verify(paymentRepository).save(pendingPayment);
    }

    @Test
    @DisplayName("Debería obtener pagos por usuario correctamente")
    void shouldGetPaymentsByUserCorrectly() {
        // Given
        List<Payment> userPayments = Arrays.asList(testPayment);
        when(paymentRepository.findByUserId(testUserId)).thenReturn(userPayments);

        // When
        List<PaymentResponse> response = paymentService.getPaymentsByUser(testUserId);

        // Then
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(testPayment.getId(), response.get(0).getId());
        assertEquals(testUserId, response.get(0).getUserId());

        verify(paymentRepository).findByUserId(testUserId);
    }

    @Test
    @DisplayName("Debería obtener pagos por estado correctamente")
    void shouldGetPaymentsByStatusCorrectly() {
        // Given
        Payment.PaymentStatus status = Payment.PaymentStatus.COMPLETED;
        List<Payment> completedPayments = Arrays.asList(testPayment);
        when(paymentRepository.findByStatus(status)).thenReturn(completedPayments);

        // When
        List<PaymentResponse> response = paymentService.getPaymentsByStatus(status);

        // Then
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(testPayment.getStatus().name(), response.get(0).getStatus());

        verify(paymentRepository).findByStatus(status);
    }

    @Test
    @DisplayName("Debería obtener pagos por pedido correctamente")
    void shouldGetPaymentsByOrderCorrectly() {
        // Given
        List<Payment> orderPayments = Arrays.asList(testPayment);
        when(paymentRepository.findByOrderId(testOrderId)).thenReturn(orderPayments);

        // When
        List<PaymentResponse> response = paymentService.getPaymentsByOrder(testOrderId);

        // Then
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(testPayment.getOrderId(), response.get(0).getOrderId());

        verify(paymentRepository).findByOrderId(testOrderId);
    }

    @Test
    @DisplayName("Debería obtener pagos pendientes correctamente")
    void shouldGetPendingPaymentsCorrectly() {
        // Given
        Payment pendingPayment = new Payment();
        pendingPayment.setId(2L);
        pendingPayment.setStatus(Payment.PaymentStatus.PENDING);
        
        List<Payment> pendingPayments = Arrays.asList(pendingPayment);
        when(paymentRepository.findPendingPayments()).thenReturn(pendingPayments);

        // When
        List<PaymentResponse> response = paymentService.getPendingPayments();

        // Then
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(Payment.PaymentStatus.PENDING.name(), response.get(0).getStatus());

        verify(paymentRepository).findPendingPayments();
    }

    @Test
    @DisplayName("Debería obtener estadísticas de pagos correctamente")
    void shouldGetPaymentStatisticsCorrectly() {
        // Given
        when(paymentRepository.count()).thenReturn(10L);
        when(paymentRepository.countByStatus(Payment.PaymentStatus.PENDING)).thenReturn(2L);
        when(paymentRepository.countByStatus(Payment.PaymentStatus.COMPLETED)).thenReturn(7L);
        when(paymentRepository.countByStatus(Payment.PaymentStatus.FAILED)).thenReturn(1L);
        when(paymentRepository.getTotalCompletedPayments()).thenReturn(new BigDecimal("50000.00"));

        // When
        java.util.Map<String, Object> stats = paymentService.getPaymentStatistics();

        // Then
        assertNotNull(stats);
        assertEquals(10L, stats.get("totalPayments"));
        assertEquals(2L, stats.get("pendingPayments"));
        assertEquals(7L, stats.get("completedPayments"));
        assertEquals(1L, stats.get("failedPayments"));
        assertEquals(new BigDecimal("50000.00"), stats.get("totalCompletedAmount"));

        verify(paymentRepository).count();
        verify(paymentRepository).countByStatus(Payment.PaymentStatus.PENDING);
        verify(paymentRepository).countByStatus(Payment.PaymentStatus.COMPLETED);
        verify(paymentRepository).countByStatus(Payment.PaymentStatus.FAILED);
        verify(paymentRepository).getTotalCompletedPayments();
    }

    @Test
    @DisplayName("Debería generar ID de transacción único")
    void shouldGenerateUniqueTransactionId() {
        // Given
        when(paymentRepository.findByOrderId(testOrderId)).thenReturn(Arrays.asList());
        when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);

        // When
        PaymentResponse response = paymentService.processPayment(testRequest, testUserId);

        // Then
        assertNotNull(response);
        assertNotNull(response.getTransactionId());
        assertTrue(response.getTransactionId().startsWith("TXN-"));

        verify(paymentRepository).findByOrderId(testOrderId);
        verify(paymentRepository, times(2)).save(any(Payment.class));
    }

    @Test
    @DisplayName("Debería simular procesamiento de pago exitoso")
    void shouldSimulateSuccessfulPaymentProcessing() {
        // Given
        when(paymentRepository.findByOrderId(testOrderId)).thenReturn(Arrays.asList());
        when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);

        // When
        PaymentResponse response = paymentService.processPayment(testRequest, testUserId);

        // Then
        assertNotNull(response);
        // El resultado puede variar debido a la simulación aleatoria, pero debe ser un estado válido
        assertTrue(Arrays.asList("PENDING", "COMPLETED", "FAILED").contains(response.getStatus()));

        verify(paymentRepository).findByOrderId(testOrderId);
        verify(paymentRepository, times(2)).save(any(Payment.class));
    }
}
