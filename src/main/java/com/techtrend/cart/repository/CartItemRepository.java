package com.techtrend.cart.repository;

import com.techtrend.cart.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad CartItem
 * 
 * Proporciona métodos para acceder y manipular datos de items del carrito
 * 
 * @author TechTrend Team
 */
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    /**
     * Busca items activos del carrito de un usuario
     * 
     * @param userId ID del usuario
     * @return Lista de items activos del carrito
     */
    List<CartItem> findByUserIdAndIsActiveTrue(Long userId);

    /**
     * Busca un item específico del carrito de un usuario
     * 
     * @param userId ID del usuario
     * @param productId ID del producto
     * @return Optional con el item si existe
     */
    Optional<CartItem> findByUserIdAndProductIdAndIsActiveTrue(Long userId, Long productId);

    /**
     * Verifica si un usuario tiene un producto específico en su carrito
     * 
     * @param userId ID del usuario
     * @param productId ID del producto
     * @return true si existe, false en caso contrario
     */
    boolean existsByUserIdAndProductIdAndIsActiveTrue(Long userId, Long productId);

    /**
     * Cuenta items activos en el carrito de un usuario
     * 
     * @param userId ID del usuario
     * @return Número de items en el carrito
     */
    long countByUserIdAndIsActiveTrue(Long userId);

    /**
     * Calcula el total del carrito de un usuario
     * 
     * @param userId ID del usuario
     * @return Total del carrito
     */
    @Query("SELECT COALESCE(SUM(ci.totalPrice), 0) FROM CartItem ci WHERE ci.userId = :userId AND ci.isActive = true")
    BigDecimal getCartTotalByUserId(@Param("userId") Long userId);

    /**
     * Busca items del carrito por usuario con información del producto
     * 
     * @param userId ID del usuario
     * @return Lista de items con información completa
     */
    @Query("SELECT ci FROM CartItem ci WHERE ci.userId = :userId AND ci.isActive = true ORDER BY ci.createdAt DESC")
    List<CartItem> findActiveCartItemsByUserId(@Param("userId") Long userId);

    /**
     * Busca items del carrito que exceden un límite de cantidad
     * 
     * @param maxQuantity Cantidad máxima permitida
     * @return Lista de items que exceden el límite
     */
    @Query("SELECT ci FROM CartItem ci WHERE ci.quantity > :maxQuantity AND ci.isActive = true")
    List<CartItem> findItemsExceedingQuantityLimit(@Param("maxQuantity") int maxQuantity);

    /**
     * Busca carritos con un valor total alto
     * 
     * @param minTotal Valor mínimo del carrito
     * @return Lista de items de carritos con valor alto
     */
    @Query("SELECT ci FROM CartItem ci WHERE ci.totalPrice >= :minTotal AND ci.isActive = true")
    List<CartItem> findHighValueCartItems(@Param("minTotal") BigDecimal minTotal);

    /**
     * Elimina todos los items del carrito de un usuario
     * 
     * @param userId ID del usuario
     */
    @Query("DELETE FROM CartItem ci WHERE ci.userId = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);

    /**
     * Desactiva todos los items del carrito de un usuario
     * 
     * @param userId ID del usuario
     */
    @Query("UPDATE CartItem ci SET ci.isActive = false WHERE ci.userId = :userId")
    void deactivateAllByUserId(@Param("userId") Long userId);

    /**
     * Busca items del carrito por producto
     * 
     * @param productId ID del producto
     * @return Lista de items que contienen el producto
     */
    List<CartItem> findByProductIdAndIsActiveTrue(Long productId);

    /**
     * Cuenta cuántos usuarios tienen un producto específico en su carrito
     * 
     * @param productId ID del producto
     * @return Número de usuarios con el producto en el carrito
     */
    @Query("SELECT COUNT(DISTINCT ci.userId) FROM CartItem ci WHERE ci.productId = :productId AND ci.isActive = true")
    long countUsersWithProductInCart(@Param("productId") Long productId);
}
