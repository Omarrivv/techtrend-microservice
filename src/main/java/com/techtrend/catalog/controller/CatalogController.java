package com.techtrend.catalog.controller;

import com.techtrend.catalog.dto.ProductResponse;
import com.techtrend.catalog.service.CatalogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para el microservicio de catálogo
 * 
 * Proporciona endpoints para consultar productos y verificar stock
 * 
 * @author TechTrend Team
 */
@RestController
@RequestMapping("/catalog")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class CatalogController {

    private final CatalogService catalogService;

    /**
     * Endpoint para listar productos disponibles
     * 
     * @return Lista de productos disponibles
     */
    @GetMapping("/products")
    public ResponseEntity<List<ProductResponse>> getAvailableProducts() {
        log.info("Solicitud para obtener productos disponibles");
        
        try {
            List<ProductResponse> products = catalogService.listAvailableProducts();
            log.info("Productos obtenidos exitosamente: {} productos", products.size());
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            log.error("Error obteniendo productos: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint para listar productos con paginación
     * 
     * @param page Número de página (0-based)
     * @param size Tamaño de la página
     * @return Página de productos
     */
    @GetMapping("/products/paged")
    public ResponseEntity<Page<ProductResponse>> getProductsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Solicitud para obtener productos paginados: página {}, tamaño {}", page, size);
        
        try {
            Page<ProductResponse> products = catalogService.listProducts(page, size);
            log.info("Productos paginados obtenidos exitosamente: {} productos en página {}", 
                    products.getContent().size(), page);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            log.error("Error obteniendo productos paginados: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint para obtener un producto por ID
     * 
     * @param id ID del producto
     * @return Producto encontrado
     */
    @GetMapping("/products/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        log.info("Solicitud para obtener producto con ID: {}", id);
        
        try {
            ProductResponse product = catalogService.getProductById(id);
            log.info("Producto obtenido exitosamente: {}", product.getName());
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            log.error("Error obteniendo producto con ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Endpoint para verificar stock de un producto
     * 
     * @param id ID del producto
     * @param quantity Cantidad solicitada
     * @return Respuesta con disponibilidad de stock
     */
    @GetMapping("/products/{id}/stock")
    public ResponseEntity<Map<String, Object>> checkStock(
            @PathVariable Long id,
            @RequestParam int quantity) {
        log.info("Solicitud para verificar stock del producto {}: cantidad {}", id, quantity);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean hasStock = catalogService.checkStockAvailability(id, quantity);
            
            response.put("productId", id);
            response.put("requestedQuantity", quantity);
            response.put("hasStock", hasStock);
            response.put("message", hasStock ? "Stock disponible" : "Stock insuficiente");
            
            log.info("Stock verificado para producto {}: {}", id, hasStock);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("Parámetros inválidos para verificar stock: {}", e.getMessage());
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            log.error("Error verificando stock del producto {}: {}", id, e.getMessage());
            response.put("error", "Error verificando stock");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Endpoint para obtener detalles de un producto
     * 
     * @param id ID del producto
     * @return Detalles del producto
     */
    @GetMapping("/products/{id}/details")
    public ResponseEntity<ProductResponse> getProductDetails(@PathVariable Long id) {
        log.info("Solicitud para obtener detalles del producto con ID: {}", id);
        
        try {
            ProductResponse product = catalogService.getProductDetails(id);
            log.info("Detalles del producto obtenidos exitosamente: {}", product.getName());
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            log.error("Error obteniendo detalles del producto con ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Endpoint para buscar productos por nombre
     * 
     * @param name Nombre o parte del nombre del producto
     * @return Lista de productos que coinciden con la búsqueda
     */
    @GetMapping("/products/search")
    public ResponseEntity<List<ProductResponse>> searchProducts(@RequestParam String name) {
        log.info("Solicitud para buscar productos por nombre: {}", name);
        
        try {
            List<ProductResponse> products = catalogService.searchProductsByName(name);
            log.info("Búsqueda completada: {} productos encontrados", products.size());
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            log.error("Error buscando productos: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint para obtener productos por categoría
     * 
     * @param category Categoría del producto
     * @return Lista de productos de la categoría
     */
    @GetMapping("/products/category/{category}")
    public ResponseEntity<List<ProductResponse>> getProductsByCategory(@PathVariable String category) {
        log.info("Solicitud para obtener productos por categoría: {}", category);
        
        try {
            List<ProductResponse> products = catalogService.getProductsByCategory(category);
            log.info("Productos por categoría obtenidos: {} productos en categoría {}", 
                    products.size(), category);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            log.error("Error obteniendo productos por categoría: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint para obtener productos por rango de precios
     * 
     * @param minPrice Precio mínimo
     * @param maxPrice Precio máximo
     * @return Lista de productos en el rango de precios
     */
    @GetMapping("/products/price-range")
    public ResponseEntity<List<ProductResponse>> getProductsByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice) {
        log.info("Solicitud para obtener productos por rango de precios: {} - {}", minPrice, maxPrice);
        
        try {
            List<ProductResponse> products = catalogService.getProductsByPriceRange(minPrice, maxPrice);
            log.info("Productos por rango de precios obtenidos: {} productos", products.size());
            return ResponseEntity.ok(products);
        } catch (IllegalArgumentException e) {
            log.warn("Rango de precios inválido: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error obteniendo productos por rango de precios: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint para obtener productos con stock bajo
     * 
     * @return Lista de productos con stock bajo
     */
    @GetMapping("/products/low-stock")
    public ResponseEntity<List<ProductResponse>> getLowStockProducts() {
        log.info("Solicitud para obtener productos con stock bajo");
        
        try {
            List<ProductResponse> products = catalogService.getLowStockProducts();
            log.info("Productos con stock bajo obtenidos: {} productos", products.size());
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            log.error("Error obteniendo productos con stock bajo: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint para obtener estadísticas del catálogo
     * 
     * @return Estadísticas del catálogo
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getCatalogStatistics() {
        log.info("Solicitud para obtener estadísticas del catálogo");
        
        try {
            Map<String, Object> stats = catalogService.getCatalogStatistics();
            log.info("Estadísticas del catálogo obtenidas exitosamente");
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error obteniendo estadísticas del catálogo: {}", e.getMessage());
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
        response.put("service", "Catalog Service");
        response.put("timestamp", java.time.LocalDateTime.now());
        
        log.info("Health check solicitado");
        return ResponseEntity.ok(response);
    }
}
