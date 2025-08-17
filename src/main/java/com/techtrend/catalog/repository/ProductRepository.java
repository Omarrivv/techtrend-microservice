package com.techtrend.catalog.repository;

import com.techtrend.catalog.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Product
 * 
 * Proporciona métodos para acceder y manipular datos de productos
 * 
 * @author TechTrend Team
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Busca productos activos
     * 
     * @return Lista de productos activos
     */
    List<Product> findByIsActiveTrue();

    /**
     * Busca productos activos con paginación
     * 
     * @param pageable Configuración de paginación
     * @return Página de productos activos
     */
    Page<Product> findByIsActiveTrue(Pageable pageable);

    /**
     * Busca productos por categoría
     * 
     * @param category Categoría del producto
     * @return Lista de productos de la categoría
     */
    List<Product> findByCategoryAndIsActiveTrue(String category);

    /**
     * Busca productos por marca
     * 
     * @param brand Marca del producto
     * @return Lista de productos de la marca
     */
    List<Product> findByBrandAndIsActiveTrue(String brand);

    /**
     * Busca productos con stock disponible
     * 
     * @return Lista de productos con stock > 0
     */
    @Query("SELECT p FROM Product p WHERE p.quantity > 0 AND p.isActive = true")
    List<Product> findAvailableProducts();

    /**
     * Busca productos con stock bajo (menos de 10 unidades)
     * 
     * @return Lista de productos con stock bajo
     */
    @Query("SELECT p FROM Product p WHERE p.quantity < 10 AND p.quantity > 0 AND p.isActive = true")
    List<Product> findLowStockProducts();

    /**
     * Busca productos por rango de precios
     * 
     * @param minPrice Precio mínimo
     * @param maxPrice Precio máximo
     * @return Lista de productos en el rango de precios
     */
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice AND p.isActive = true")
    List<Product> findByPriceRange(@Param("minPrice") BigDecimal minPrice, 
                                  @Param("maxPrice") BigDecimal maxPrice);

    /**
     * Busca productos por nombre (búsqueda parcial)
     * 
     * @param name Nombre o parte del nombre del producto
     * @return Lista de productos que coinciden con el nombre
     */
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) AND p.isActive = true")
    List<Product> findByNameContainingIgnoreCase(@Param("name") String name);

    /**
     * Busca producto por SKU
     * 
     * @param sku SKU del producto
     * @return Optional con el producto si existe
     */
    Optional<Product> findBySku(String sku);

    /**
     * Verifica si existe un producto con el SKU especificado
     * 
     * @param sku SKU a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsBySku(String sku);

    /**
     * Cuenta productos activos
     * 
     * @return Número de productos activos
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.isActive = true")
    long countActiveProducts();

    /**
     * Cuenta productos con stock disponible
     * 
     * @return Número de productos con stock > 0
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.quantity > 0 AND p.isActive = true")
    long countAvailableProducts();

    /**
     * Obtiene el valor total del inventario
     * 
     * @return Valor total del inventario
     */
    @Query("SELECT SUM(p.price * p.quantity) FROM Product p WHERE p.isActive = true")
    BigDecimal getTotalInventoryValue();

    /**
     * Busca productos por múltiples categorías
     * 
     * @param categories Lista de categorías
     * @return Lista de productos de las categorías especificadas
     */
    @Query("SELECT p FROM Product p WHERE p.category IN :categories AND p.isActive = true")
    List<Product> findByCategories(@Param("categories") List<String> categories);
}
