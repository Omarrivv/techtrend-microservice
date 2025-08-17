package com.techtrend.catalog.service;

import com.techtrend.catalog.dto.ProductResponse;
import com.techtrend.catalog.model.Product;
import com.techtrend.catalog.repository.ProductRepository;
import com.techtrend.common.exception.InsufficientStockException;
import com.techtrend.common.exception.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
 * Pruebas unitarias para el servicio de catálogo
 * 
 * @author TechTrend Team
 */
@ExtendWith(MockitoExtension.class)
class CatalogServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CatalogService catalogService;

    private Product testProduct;
    private Product testProduct2;

    @BeforeEach
    void setUp() {
        // Configurar valores de prueba
        ReflectionTestUtils.setField(catalogService, "defaultPageSize", 20);
        ReflectionTestUtils.setField(catalogService, "maxPageSize", 100);

        // Crear productos de prueba
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Laptop Ryzen 7");
        testProduct.setDescription("Laptop gaming con procesador AMD Ryzen 7");
        testProduct.setPrice(new BigDecimal("9999.99"));
        testProduct.setQuantity(50);
        testProduct.setCategory("Laptops");
        testProduct.setBrand("ASUS");
        testProduct.setModel("ROG Strix");
        testProduct.setSku("LAP001");
        testProduct.setIsActive(true);
        testProduct.setCreatedAt(LocalDateTime.now());
        testProduct.setUpdatedAt(LocalDateTime.now());
        testProduct.setLastStockUpdate(LocalDateTime.now());

        testProduct2 = new Product();
        testProduct2.setId(2L);
        testProduct2.setName("Monitor Gaming");
        testProduct2.setDescription("Monitor gaming 27 pulgadas 144Hz");
        testProduct2.setPrice(new BigDecimal("1500.00"));
        testProduct2.setQuantity(25);
        testProduct2.setCategory("Monitores");
        testProduct2.setBrand("LG");
        testProduct2.setModel("27GL850");
        testProduct2.setSku("MON001");
        testProduct2.setIsActive(true);
        testProduct2.setCreatedAt(LocalDateTime.now());
        testProduct2.setUpdatedAt(LocalDateTime.now());
        testProduct2.setLastStockUpdate(LocalDateTime.now());
    }

    @Test
    @DisplayName("Debería retornar true cuando hay stock suficiente")
    void shouldReturnTrue_whenSufficientStockAvailable() {
        // Given
        Long productId = 1L;
        int requestedQuantity = 10;
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));

        // When
        boolean hasStock = catalogService.checkStockAvailability(productId, requestedQuantity);

        // Then
        assertTrue(hasStock);
        verify(productRepository).findById(productId);
    }

    @Test
    @DisplayName("Debería retornar false cuando el stock es insuficiente")
    void shouldReturnFalse_whenInsufficientStock() {
        // Given
        Long productId = 1L;
        int requestedQuantity = 60; // Más que el stock disponible (50)
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));

        // When
        boolean hasStock = catalogService.checkStockAvailability(productId, requestedQuantity);

        // Then
        assertFalse(hasStock);
        verify(productRepository).findById(productId);
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando la cantidad es negativa")
    void shouldThrowException_whenNegativeQuantity() {
        // Given
        Long productId = 1L;
        int requestedQuantity = -5;

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> catalogService.checkStockAvailability(productId, requestedQuantity)
        );

        assertEquals("La cantidad debe ser mayor a cero", exception.getMessage());
        verify(productRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("Debería retornar false cuando el producto no existe")
    void shouldReturnFalse_whenProductNotFound() {
        // Given
        Long productId = 999L;
        int requestedQuantity = 10;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // When & Then
        ProductNotFoundException exception = assertThrows(
            ProductNotFoundException.class,
            () -> catalogService.checkStockAvailability(productId, requestedQuantity)
        );

        assertEquals("Producto con ID 999 no encontrado", exception.getMessage());
        verify(productRepository).findById(productId);
    }

    @Test
    @DisplayName("Debería listar productos disponibles correctamente")
    void shouldListAvailableProductsCorrectly() {
        // Given
        List<Product> availableProducts = Arrays.asList(testProduct, testProduct2);
        when(productRepository.findAvailableProducts()).thenReturn(availableProducts);

        // When
        List<ProductResponse> result = catalogService.listAvailableProducts();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testProduct.getName(), result.get(0).getName());
        assertEquals(testProduct2.getName(), result.get(1).getName());
        
        verify(productRepository).findAvailableProducts();
    }

    @Test
    @DisplayName("Debería listar productos con paginación correctamente")
    void shouldListProductsWithPaginationCorrectly() {
        // Given
        List<Product> products = Arrays.asList(testProduct, testProduct2);
        Page<Product> productPage = new PageImpl<>(products, PageRequest.of(0, 20), 2);
        when(productRepository.findByIsActiveTrue(any(Pageable.class))).thenReturn(productPage);

        // When
        Page<ProductResponse> result = catalogService.listProducts(0, 20);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(2, result.getTotalElements());
        assertEquals(0, result.getNumber());
        assertEquals(20, result.getSize());
        
        verify(productRepository).findByIsActiveTrue(any(Pageable.class));
    }

    @Test
    @DisplayName("Debería obtener producto por ID correctamente")
    void shouldGetProductByIdCorrectly() {
        // Given
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));

        // When
        ProductResponse result = catalogService.getProductById(productId);

        // Then
        assertNotNull(result);
        assertEquals(testProduct.getId(), result.getId());
        assertEquals(testProduct.getName(), result.getName());
        assertEquals(testProduct.getPrice(), result.getPrice());
        assertEquals(testProduct.getQuantity(), result.getQuantity());
        
        verify(productRepository).findById(productId);
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando el producto no existe")
    void shouldThrowException_whenProductDoesNotExist() {
        // Given
        Long productId = 999L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // When & Then
        ProductNotFoundException exception = assertThrows(
            ProductNotFoundException.class,
            () -> catalogService.getProductById(productId)
        );

        assertEquals("Producto con ID 999 no encontrado", exception.getMessage());
        verify(productRepository).findById(productId);
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando el producto está inactivo")
    void shouldThrowException_whenProductIsInactive() {
        // Given
        Long productId = 1L;
        testProduct.setIsActive(false);
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));

        // When & Then
        ProductNotFoundException exception = assertThrows(
            ProductNotFoundException.class,
            () -> catalogService.getProductById(productId)
        );

        assertEquals("Producto inactivo", exception.getMessage());
        verify(productRepository).findById(productId);
    }

    @Test
    @DisplayName("Debería reducir stock correctamente cuando hay suficiente")
    void shouldReduceStockCorrectly_whenSufficientStock() {
        // Given
        Long productId = 1L;
        int quantityToReduce = 10;
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // When
        catalogService.reduceStock(productId, quantityToReduce);

        // Then
        verify(productRepository).findById(productId);
        verify(productRepository).save(testProduct);
        assertEquals(40, testProduct.getQuantity()); // 50 - 10
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando no hay suficiente stock para reducir")
    void shouldThrowException_whenInsufficientStockForReduction() {
        // Given
        Long productId = 1L;
        int quantityToReduce = 60; // Más que el stock disponible (50)
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));

        // When & Then
        InsufficientStockException exception = assertThrows(
            InsufficientStockException.class,
            () -> catalogService.reduceStock(productId, quantityToReduce)
        );

        assertTrue(exception.getMessage().contains("Stock insuficiente"));
        verify(productRepository).findById(productId);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando la cantidad a reducir es negativa")
    void shouldThrowException_whenNegativeQuantityForReduction() {
        // Given
        Long productId = 1L;
        int quantityToReduce = -5;

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> catalogService.reduceStock(productId, quantityToReduce)
        );

        assertEquals("La cantidad debe ser mayor a cero", exception.getMessage());
        verify(productRepository, never()).findById(anyLong());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Debería aumentar stock correctamente")
    void shouldIncreaseStockCorrectly() {
        // Given
        Long productId = 1L;
        int quantityToIncrease = 10;
        int originalQuantity = testProduct.getQuantity();
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // When
        catalogService.increaseStock(productId, quantityToIncrease);

        // Then
        verify(productRepository).findById(productId);
        verify(productRepository).save(testProduct);
        assertEquals(originalQuantity + quantityToIncrease, testProduct.getQuantity());
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando la cantidad a aumentar es negativa")
    void shouldThrowException_whenNegativeQuantityForIncrease() {
        // Given
        Long productId = 1L;
        int quantityToIncrease = -5;

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> catalogService.increaseStock(productId, quantityToIncrease)
        );

        assertEquals("La cantidad debe ser mayor a cero", exception.getMessage());
        verify(productRepository, never()).findById(anyLong());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Debería buscar productos por nombre correctamente")
    void shouldSearchProductsByNameCorrectly() {
        // Given
        String searchName = "Laptop";
        List<Product> searchResults = Arrays.asList(testProduct);
        when(productRepository.findByNameContainingIgnoreCase(searchName)).thenReturn(searchResults);

        // When
        List<ProductResponse> result = catalogService.searchProductsByName(searchName);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testProduct.getName(), result.get(0).getName());
        
        verify(productRepository).findByNameContainingIgnoreCase(searchName);
    }

    @Test
    @DisplayName("Debería obtener productos por categoría correctamente")
    void shouldGetProductsByCategoryCorrectly() {
        // Given
        String category = "Laptops";
        List<Product> categoryProducts = Arrays.asList(testProduct);
        when(productRepository.findByCategoryAndIsActiveTrue(category)).thenReturn(categoryProducts);

        // When
        List<ProductResponse> result = catalogService.getProductsByCategory(category);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testProduct.getCategory(), result.get(0).getCategory());
        
        verify(productRepository).findByCategoryAndIsActiveTrue(category);
    }

    @Test
    @DisplayName("Debería obtener productos por rango de precios correctamente")
    void shouldGetProductsByPriceRangeCorrectly() {
        // Given
        BigDecimal minPrice = new BigDecimal("1000.00");
        BigDecimal maxPrice = new BigDecimal("2000.00");
        List<Product> priceRangeProducts = Arrays.asList(testProduct2);
        when(productRepository.findByPriceRange(minPrice, maxPrice)).thenReturn(priceRangeProducts);

        // When
        List<ProductResponse> result = catalogService.getProductsByPriceRange(minPrice, maxPrice);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testProduct2.getName(), result.get(0).getName());
        
        verify(productRepository).findByPriceRange(minPrice, maxPrice);
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando el precio mínimo es mayor que el máximo")
    void shouldThrowException_whenMinPriceGreaterThanMaxPrice() {
        // Given
        BigDecimal minPrice = new BigDecimal("2000.00");
        BigDecimal maxPrice = new BigDecimal("1000.00");

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> catalogService.getProductsByPriceRange(minPrice, maxPrice)
        );

        assertEquals("El precio mínimo no puede ser mayor que el precio máximo", exception.getMessage());
        verify(productRepository, never()).findByPriceRange(any(), any());
    }

    @Test
    @DisplayName("Debería obtener productos con stock bajo correctamente")
    void shouldGetLowStockProductsCorrectly() {
        // Given
        Product lowStockProduct = new Product();
        lowStockProduct.setId(3L);
        lowStockProduct.setName("Producto con stock bajo");
        lowStockProduct.setQuantity(5); // Menos de 10
        lowStockProduct.setIsActive(true);
        
        List<Product> lowStockProducts = Arrays.asList(lowStockProduct);
        when(productRepository.findLowStockProducts()).thenReturn(lowStockProducts);

        // When
        List<ProductResponse> result = catalogService.getLowStockProducts();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(lowStockProduct.getName(), result.get(0).getName());
        
        verify(productRepository).findLowStockProducts();
    }

    @Test
    @DisplayName("Debería obtener estadísticas del catálogo correctamente")
    void shouldGetCatalogStatisticsCorrectly() {
        // Given
        when(productRepository.countActiveProducts()).thenReturn(10L);
        when(productRepository.countAvailableProducts()).thenReturn(8L);
        when(productRepository.findLowStockProducts()).thenReturn(Arrays.asList(testProduct));
        when(productRepository.getTotalInventoryValue()).thenReturn(new BigDecimal("50000.00"));

        // When
        java.util.Map<String, Object> stats = catalogService.getCatalogStatistics();

        // Then
        assertNotNull(stats);
        assertEquals(10L, stats.get("totalProducts"));
        assertEquals(8L, stats.get("availableProducts"));
        assertEquals(1, stats.get("lowStockProducts"));
        assertEquals(new BigDecimal("50000.00"), stats.get("totalInventoryValue"));
        
        verify(productRepository).countActiveProducts();
        verify(productRepository).countAvailableProducts();
        verify(productRepository).findLowStockProducts();
        verify(productRepository).getTotalInventoryValue();
    }
}
