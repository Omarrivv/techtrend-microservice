package com.techtrend.payment.controller;

import com.techtrend.payment.dto.PaymentRequest;
import com.techtrend.payment.dto.PaymentResponse;
import com.techtrend.payment.model.Payment;
import com.techtrend.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para el microservicio de pagos
 * 
 * Proporciona endpoints para procesar y gestionar pagos
 * 
 * @author TechTrend Team
 */
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Endpoint para procesar un pago
     * 
     * @param request Solicitud de pago
     * @param userId ID del usuario
     * @return Pago procesado
     */
    @PostMapping("/process")
    public ResponseEntity<PaymentResponse> processPayment(
            @Valid @RequestBody PaymentRequest request,
            @RequestParam Long userId) {
        log.info("Solicitud para procesar pago: pedido {}, usuario {}, monto {}", 
                request.getOrderId(), userId, request.getAmount());
        
        try {
            PaymentResponse response = paymentService.processPayment(request, userId);
            log.info("Pago procesado exitosamente: ID {}", response.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error procesando pago: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Endpoint para obtener un pago por ID
     * 
     * @param id ID del pago
     * @return Pago encontrado
     */
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable Long id) {
        log.info("Solicitud para obtener pago con ID: {}", id);
        
        try {
            PaymentResponse payment = paymentService.getPaymentById(id);
            log.info("Pago obtenido exitosamente: ID {}", id);
            return ResponseEntity.ok(payment);
        } catch (IllegalArgumentException e) {
            log.warn("Pago no encontrado: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error obteniendo pago: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint para obtener el estado de un pago
     * 
     * @param id ID del pago
     * @return Estado del pago
     */
    @GetMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> getPaymentStatus(@PathVariable Long id) {
        log.info("Solicitud para obtener estado del pago: {}", id);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            String status = paymentService.getPaymentStatus(id);
            
            response.put("paymentId", id);
            response.put("status", status);
            
            log.info("Estado del pago obtenido: ID {}, estado {}", id, status);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("Pago no encontrado: {}", e.getMessage());
            response.put("error", "Pago no encontrado");
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error obteniendo estado del pago: {}", e.getMessage());
            response.put("error", "Error obteniendo estado del pago");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Endpoint para actualizar el estado de un pago
     * 
     * @param id ID del pago
     * @param status Nuevo estado
     * @return Pago actualizado
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<PaymentResponse> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        log.info("Solicitud para actualizar estado del pago {} a: {}", id, status);
        
        try {
            Payment.PaymentStatus paymentStatus = Payment.PaymentStatus.valueOf(status.toUpperCase());
            PaymentResponse response = paymentService.updatePaymentStatus(id, paymentStatus);
            log.info("Estado del pago actualizado exitosamente: ID {}, nuevo estado {}", id, status);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("Estado inválido o pago no encontrado: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error actualizando estado del pago: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint para obtener pagos por usuario
     * 
     * @param userId ID del usuario
     * @return Lista de pagos del usuario
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByUser(@PathVariable Long userId) {
        log.info("Solicitud para obtener pagos del usuario: {}", userId);
        
        try {
            List<PaymentResponse> payments = paymentService.getPaymentsByUser(userId);
            log.info("Pagos del usuario obtenidos exitosamente: usuario {}, {} pagos", userId, payments.size());
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            log.error("Error obteniendo pagos del usuario: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint para obtener pagos por estado
     * 
     * @param status Estado del pago
     * @return Lista de pagos con el estado especificado
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByStatus(@PathVariable String status) {
        log.info("Solicitud para obtener pagos con estado: {}", status);
        
        try {
            Payment.PaymentStatus paymentStatus = Payment.PaymentStatus.valueOf(status.toUpperCase());
            List<PaymentResponse> payments = paymentService.getPaymentsByStatus(paymentStatus);
            log.info("Pagos por estado obtenidos exitosamente: estado {}, {} pagos", status, payments.size());
            return ResponseEntity.ok(payments);
        } catch (IllegalArgumentException e) {
            log.warn("Estado inválido: {}", status);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error obteniendo pagos por estado: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint para obtener pagos por pedido
     * 
     * @param orderId ID del pedido
     * @return Lista de pagos del pedido
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByOrder(@PathVariable Long orderId) {
        log.info("Solicitud para obtener pagos del pedido: {}", orderId);
        
        try {
            List<PaymentResponse> payments = paymentService.getPaymentsByOrder(orderId);
            log.info("Pagos del pedido obtenidos exitosamente: pedido {}, {} pagos", orderId, payments.size());
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            log.error("Error obteniendo pagos del pedido: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint para obtener pagos pendientes
     * 
     * @return Lista de pagos pendientes
     */
    @GetMapping("/pending")
    public ResponseEntity<List<PaymentResponse>> getPendingPayments() {
        log.info("Solicitud para obtener pagos pendientes");
        
        try {
            List<PaymentResponse> payments = paymentService.getPendingPayments();
            log.info("Pagos pendientes obtenidos exitosamente: {} pagos", payments.size());
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            log.error("Error obteniendo pagos pendientes: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint para obtener estadísticas de pagos
     * 
     * @return Estadísticas de pagos
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getPaymentStatistics() {
        log.info("Solicitud para obtener estadísticas de pagos");
        
        try {
            Map<String, Object> stats = paymentService.getPaymentStatistics();
            log.info("Estadísticas de pagos obtenidas exitosamente");
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error obteniendo estadísticas de pagos: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint de health check para el microservicio
     * 
     * @return Estado del servicio
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Payment Service");
        response.put("timestamp", java.time.LocalDateTime.now());
        
        log.info("Health check solicitado");
        return ResponseEntity.ok(response);
    }
}
