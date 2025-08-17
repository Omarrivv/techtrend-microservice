package com.techtrend.cart.controller;

import com.techtrend.cart.dto.CartItemRequest;
import com.techtrend.cart.dto.CartItemResponse;
import com.techtrend.cart.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para el microservicio de carrito
 * 
 * Proporciona endpoints para gestionar carritos de compra
 * 
 * @author TechTrend Team
 */
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class CartController {

    private final CartService cartService;

    /**
     * Endpoint para agregar un producto al carrito
     * 
     * @param userId ID del usuario
     * @param request Solicitud con información del producto
     * @return Item del carrito creado o actualizado
     */
    @PostMapping("/items")
    public ResponseEntity<CartItemResponse> addProductToCart(
            @RequestParam Long userId,
            @Valid @RequestBody CartItemRequest request) {
        log.info("Solicitud para agregar producto al carrito: usuario {}, producto {}", 
                userId, request.getProductId());
        
        try {
            CartItemResponse response = cartService.addProductToCart(userId, request);
            log.info("Producto agregado exitosamente al carrito: usuario {}, producto {}", 
                    userId, request.getProductId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error agregando producto al carrito: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Endpoint para actualizar la cantidad de un item del carrito
     * 
     * @param userId ID del usuario
     * @param itemId ID del item
     * @param quantity Nueva cantidad
     * @return Item del carrito actualizado
     */
    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartItemResponse> updateCartItemQuantity(
            @RequestParam Long userId,
            @PathVariable Long itemId,
            @RequestParam int quantity) {
        log.info("Solicitud para actualizar cantidad: usuario {}, item {}, cantidad {}", 
                userId, itemId, quantity);
        
        try {
            CartItemResponse response = cartService.updateCartItemQuantity(userId, itemId, quantity);
            log.info("Cantidad actualizada exitosamente: usuario {}, item {}, nueva cantidad {}", 
                    userId, itemId, quantity);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("Parámetros inválidos para actualizar cantidad: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error actualizando cantidad: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint para obtener todos los items del carrito de un usuario
     * 
     * @param userId ID del usuario
     * @return Lista de items del carrito
     */
    @GetMapping("/items")
    public ResponseEntity<List<CartItemResponse>> getCartItems(@RequestParam Long userId) {
        log.info("Solicitud para obtener items del carrito: usuario {}", userId);
        
        try {
            List<CartItemResponse> items = cartService.getCartItems(userId);
            log.info("Items del carrito obtenidos exitosamente: usuario {}, {} items", 
                    userId, items.size());
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            log.error("Error obteniendo items del carrito: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint para obtener un item específico del carrito
     * 
     * @param userId ID del usuario
     * @param itemId ID del item
     * @return Item del carrito
     */
    @GetMapping("/items/{itemId}")
    public ResponseEntity<CartItemResponse> getCartItem(
            @RequestParam Long userId,
            @PathVariable Long itemId) {
        log.info("Solicitud para obtener item del carrito: usuario {}, item {}", userId, itemId);
        
        try {
            CartItemResponse item = cartService.getCartItem(userId, itemId);
            log.info("Item del carrito obtenido exitosamente: usuario {}, item {}", userId, itemId);
            return ResponseEntity.ok(item);
        } catch (IllegalArgumentException e) {
            log.warn("Item no encontrado o no pertenece al usuario: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error obteniendo item del carrito: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint para eliminar un item del carrito
     * 
     * @param userId ID del usuario
     * @param itemId ID del item a eliminar
     * @return Respuesta de confirmación
     */
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Map<String, Object>> removeCartItem(
            @RequestParam Long userId,
            @PathVariable Long itemId) {
        log.info("Solicitud para eliminar item del carrito: usuario {}, item {}", userId, itemId);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean removed = cartService.removeCartItem(userId, itemId);
            
            if (removed) {
                response.put("success", true);
                response.put("message", "Item eliminado del carrito");
                log.info("Item eliminado exitosamente del carrito: usuario {}, item {}", userId, itemId);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "No se pudo eliminar el item");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (IllegalArgumentException e) {
            log.warn("Error eliminando item del carrito: {}", e.getMessage());
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            log.error("Error eliminando item del carrito: {}", e.getMessage());
            response.put("success", false);
            response.put("message", "Error interno del servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Endpoint para vaciar el carrito de un usuario
     * 
     * @param userId ID del usuario
     * @return Respuesta de confirmación
     */
    @DeleteMapping("/items")
    public ResponseEntity<Map<String, Object>> clearCart(@RequestParam Long userId) {
        log.info("Solicitud para vaciar carrito: usuario {}", userId);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean cleared = cartService.clearCart(userId);
            
            if (cleared) {
                response.put("success", true);
                response.put("message", "Carrito vaciado exitosamente");
                log.info("Carrito vaciado exitosamente: usuario {}", userId);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "No se pudo vaciar el carrito");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            log.error("Error vaciando carrito: {}", e.getMessage());
            response.put("success", false);
            response.put("message", "Error interno del servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Endpoint para obtener el total del carrito
     * 
     * @param userId ID del usuario
     * @return Total del carrito
     */
    @GetMapping("/total")
    public ResponseEntity<Map<String, Object>> getCartTotal(@RequestParam Long userId) {
        log.info("Solicitud para obtener total del carrito: usuario {}", userId);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            BigDecimal total = cartService.getCartTotal(userId);
            
            response.put("userId", userId);
            response.put("total", total);
            response.put("currency", "PEN");
            
            log.info("Total del carrito obtenido: usuario {}, total {}", userId, total);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error obteniendo total del carrito: {}", e.getMessage());
            response.put("error", "Error obteniendo total del carrito");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Endpoint para obtener el número de items en el carrito
     * 
     * @param userId ID del usuario
     * @return Número de items
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getCartItemCount(@RequestParam Long userId) {
        log.info("Solicitud para obtener cantidad de items del carrito: usuario {}", userId);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            long count = cartService.getCartItemCount(userId);
            
            response.put("userId", userId);
            response.put("itemCount", count);
            
            log.info("Cantidad de items obtenida: usuario {}, {} items", userId, count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error obteniendo cantidad de items: {}", e.getMessage());
            response.put("error", "Error obteniendo cantidad de items");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Endpoint para verificar si un producto está en el carrito
     * 
     * @param userId ID del usuario
     * @param productId ID del producto
     * @return Respuesta indicando si el producto está en el carrito
     */
    @GetMapping("/check-product")
    public ResponseEntity<Map<String, Object>> checkProductInCart(
            @RequestParam Long userId,
            @RequestParam Long productId) {
        log.info("Solicitud para verificar producto en carrito: usuario {}, producto {}", 
                userId, productId);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean inCart = cartService.isProductInCart(userId, productId);
            
            response.put("userId", userId);
            response.put("productId", productId);
            response.put("inCart", inCart);
            
            log.info("Verificación completada: usuario {}, producto {}, en carrito: {}", 
                    userId, productId, inCart);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error verificando producto en carrito: {}", e.getMessage());
            response.put("error", "Error verificando producto en carrito");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
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
        response.put("service", "Cart Service");
        response.put("timestamp", java.time.LocalDateTime.now());
        
        log.info("Health check solicitado");
        return ResponseEntity.ok(response);
    }
}
