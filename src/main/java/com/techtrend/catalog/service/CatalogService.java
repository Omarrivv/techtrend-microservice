package com.techtrend.catalog.service;

import com.techtrend.catalog.dto.ProductRequest;
import com.techtrend.catalog.dto.ProductResponse;
import com.techtrend.catalog.model.Product;
import com.techtrend.catalog.repository.ProductRepository;
import com.techtrend.common.exception.InsufficientStockException;
import com.techtrend.common.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio principal de catálogo para TechTrend
 * 
 * Maneja la gestión de productos, verificación de stock y consultas del catálogo
 * 
 * @author TechTrend Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CatalogService {

    private final ProductRepository productRepository;

    @Value("${app.catalog.default-page-size:20}")
    private int defaultPageSize;

    @Value("${app.catalog.max-page-size:100}")
    private int maxPageSize;

    /**
     * Lista todos los productos disponibles
     * 
     * @return Lista de productos activos
     */
    public List<ProductResponse> listAvailableProducts() {
        log.info("Obteniendo lista de productos disponibles");
        
        List<Product> products = productRepository.findAvailableProducts();
        return products.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Lista productos con paginación
     * 
     * @param page Número de página (0-based)
     * @param size Tamaño de la página
     * @return Página de productos
     */
    public Page<ProductResponse> listProducts(int page, int size) {
        log.info("Obteniendo productos con paginación: página {}, tamaño {}", page, size);
        
        // Validar y ajustar el tamaño de página
        int adjustedSize = Math.min(Math.max(size, 1), maxPageSize);
        
        Pageable pageable = PageRequest.of(page, adjustedSize);
        Page<Product> products = productRepository.findByIsActiveTrue(pageable);
        
        return products.map(this::convertToResponse);
    }

    /**
     * Obtiene un producto por su ID
     * 
     * @param productId ID del producto
     * @return Producto encontrado
     * @throws ProductNotFoundException si el producto no existe
     */
    public ProductResponse getProductById(Long productId) {
        log.info("Obteniendo producto con ID: {}", productId);
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.warn("Producto no encontrado con ID: {}", productId);
                    return new ProductNotFoundException(productId);
                });
        
        if (!product.isProductActive()) {
            log.warn("Producto inactivo con ID: {}", productId);
            throw new ProductNotFoundException("Producto inactivo");
        }
        
        return convertToResponse(product);
    }

    /**
     * Verifica el stock disponible para un producto
     * 
     * @param productId ID del producto
     * @param requestedQuantity Cantidad solicitada
     * @return true si hay suficiente stock, false en caso contrario
     */
    public boolean checkStockAvailability(Long productId, int requestedQuantity) {
        log.info("Verificando stock para producto {}: cantidad solicitada {}", productId, requestedQuantity);
        
        if (requestedQuantity <= 0) {
            log.warn("Cantidad solicitada inválida: {}", requestedQuantity);
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.warn("Producto no encontrado con ID: {}", productId);
                    return new ProductNotFoundException(productId);
                });
        
        if (!product.isProductActive()) {
            log.warn("Producto inactivo con ID: {}", productId);
            return false;
        }
        
        boolean hasStock = product.hasSufficientStock(requestedQuantity);
        log.info("Stock disponible para producto {}: {} (solicitado: {})", 
                productId, product.getAvailableStock(), requestedQuantity);
        
        return hasStock;
    }

    /**
     * Obtiene los detalles de un producto
     * 
     * @param productId ID del producto
     * @return Detalles del producto
     * @throws ProductNotFoundException si el producto no existe
     */
    public ProductResponse getProductDetails(Long productId) {
        log.info("Obteniendo detalles del producto con ID: {}", productId);
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.warn("Producto no encontrado con ID: {}", productId);
                    return new ProductNotFoundException(productId);
                });
        
        if (!product.isProductActive()) {
            log.warn("Producto inactivo con ID: {}", productId);
            throw new ProductNotFoundException("Producto inactivo");
        }
        
        return convertToResponse(product);
    }

    /**
     * Reduce el stock de un producto
     * 
     * @param productId ID del producto
     * @param quantity Cantidad a reducir
     * @throws ProductNotFoundException si el producto no existe
     * @throws InsufficientStockException si no hay suficiente stock
     */
    public void reduceStock(Long productId, int quantity) {
        log.info("Reduciendo stock del producto {}: cantidad {}", productId, quantity);
        
        if (quantity <= 0) {
            log.warn("Cantidad inválida para reducir stock: {}", quantity);
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.warn("Producto no encontrado con ID: {}", productId);
                    return new ProductNotFoundException(productId);
                });
        
        if (!product.isProductActive()) {
            log.warn("Producto inactivo con ID: {}", productId);
            throw new ProductNotFoundException("Producto inactivo");
        }
        
        if (!product.hasSufficientStock(quantity)) {
            log.warn("Stock insuficiente para producto {}: disponible {}, solicitado {}", 
                    productId, product.getAvailableStock(), quantity);
            throw new InsufficientStockException(productId, quantity, product.getAvailableStock());
        }
        
        product.reduceStock(quantity);
        productRepository.save(product);
        
        log.info("Stock reducido exitosamente para producto {}: nueva cantidad {}", 
                productId, product.getAvailableStock());
    }

    /**
     * Aumenta el stock de un producto
     * 
     * @param productId ID del producto
     * @param quantity Cantidad a aumentar
     * @throws ProductNotFoundException si el producto no existe
     */
    public void increaseStock(Long productId, int quantity) {
        log.info("Aumentando stock del producto {}: cantidad {}", productId, quantity);
        
        if (quantity <= 0) {
            log.warn("Cantidad inválida para aumentar stock: {}", quantity);
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.warn("Producto no encontrado con ID: {}", productId);
                    return new ProductNotFoundException(productId);
                });
        
        if (!product.isProductActive()) {
            log.warn("Producto inactivo con ID: {}", productId);
            throw new ProductNotFoundException("Producto inactivo");
        }
        
        product.increaseStock(quantity);
        productRepository.save(product);
        
        log.info("Stock aumentado exitosamente para producto {}: nueva cantidad {}", 
                productId, product.getAvailableStock());
    }

    /**
     * Busca productos por nombre
     * 
     * @param name Nombre o parte del nombre del producto
     * @return Lista de productos que coinciden con la búsqueda
     */
    public List<ProductResponse> searchProductsByName(String name) {
        log.info("Buscando productos por nombre: {}", name);
        
        if (name == null || name.trim().isEmpty()) {
            log.warn("Nombre de búsqueda vacío o nulo");
            return listAvailableProducts();
        }
        
        List<Product> products = productRepository.findByNameContainingIgnoreCase(name.trim());
        return products.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Busca productos por categoría
     * 
     * @param category Categoría del producto
     * @return Lista de productos de la categoría
     */
    public List<ProductResponse> getProductsByCategory(String category) {
        log.info("Obteniendo productos por categoría: {}", category);
        
        if (category == null || category.trim().isEmpty()) {
            log.warn("Categoría vacía o nula");
            return List.of();
        }
        
        List<Product> products = productRepository.findByCategoryAndIsActiveTrue(category.trim());
        return products.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Busca productos por rango de precios
     * 
     * @param minPrice Precio mínimo
     * @param maxPrice Precio máximo
     * @return Lista de productos en el rango de precios
     */
    public List<ProductResponse> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        log.info("Obteniendo productos por rango de precios: {} - {}", minPrice, maxPrice);
        
        if (minPrice == null || maxPrice == null) {
            log.warn("Precios nulos para búsqueda por rango");
            return List.of();
        }
        
        if (minPrice.compareTo(maxPrice) > 0) {
            log.warn("Precio mínimo mayor que precio máximo: {} > {}", minPrice, maxPrice);
            throw new IllegalArgumentException("El precio mínimo no puede ser mayor que el precio máximo");
        }
        
        List<Product> products = productRepository.findByPriceRange(minPrice, maxPrice);
        return products.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene productos con stock bajo
     * 
     * @return Lista de productos con stock bajo
     */
    public List<ProductResponse> getLowStockProducts() {
        log.info("Obteniendo productos con stock bajo");
        
        List<Product> products = productRepository.findLowStockProducts();
        return products.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene estadísticas del catálogo
     * 
     * @return Mapa con estadísticas
     */
    public java.util.Map<String, Object> getCatalogStatistics() {
        log.info("Obteniendo estadísticas del catálogo");
        
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalProducts", productRepository.countActiveProducts());
        stats.put("availableProducts", productRepository.countAvailableProducts());
        stats.put("lowStockProducts", productRepository.findLowStockProducts().size());
        stats.put("totalInventoryValue", productRepository.getTotalInventoryValue());
        
        return stats;
    }

    /**
     * Convierte una entidad Product a ProductResponse
     * 
     * @param product Entidad Product
     * @return ProductResponse
     */
    private ProductResponse convertToResponse(Product product) {
        return new ProductResponse(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getQuantity(),
            product.getCategory(),
            product.getBrand(),
            product.getModel(),
            product.getSku(),
            product.getIsActive(),
            product.getCreatedAt(),
            product.getUpdatedAt(),
            product.getLastStockUpdate()
        );
    }
}
