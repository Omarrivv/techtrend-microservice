package com.techtrend.cart.service;

import com.techtrend.cart.dto.CartItemRequest;
import com.techtrend.cart.dto.CartItemResponse;
import com.techtrend.cart.model.CartItem;
import com.techtrend.cart.repository.CartItemRepository;
import com.techtrend.catalog.dto.ProductResponse;
import com.techtrend.catalog.service.CatalogService;
import com.techtrend.common.exception.InsufficientStockException;
import com.techtrend.common.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio principal de carrito para TechTrend
 * 
 * Maneja la gestión de carritos de compra, validación de productos y usuarios
 * 
 * @author TechTrend Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final CatalogService catalogService;

    @Value("${app.cart.max-items-per-cart:50}")
    private int maxItemsPerCart;

    /**
     * Agrega un producto al carrito de un usuario
     * 
     * @param userId ID del usuario
     * @param request Solicitud con información del producto
     * @return Item del carrito creado o actualizado
     */
    public CartItemResponse addProductToCart(Long userId, CartItemRequest request) {
        log.info("Agregando producto {} al carrito del usuario {}: cantidad {}", 
                request.getProductId(), userId, request.getQuantity());

        // Validar que el producto existe y tiene stock
        ProductResponse product = validateProductAndStock(request.getProductId(), request.getQuantity());

        // Verificar si el producto ya está en el carrito
        Optional<CartItem> existingItem = cartItemRepository.findByUserIdAndProductIdAndIsActiveTrue(userId, request.getProductId());

        if (existingItem.isPresent()) {
            // Actualizar cantidad del item existente
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + request.getQuantity();
            
            // Validar stock nuevamente con la cantidad total
            validateProductAndStock(request.getProductId(), newQuantity);
            
            item.updateQuantity(newQuantity);
            CartItem savedItem = cartItemRepository.save(item);
            
            log.info("Producto actualizado en carrito: usuario {}, producto {}, nueva cantidad {}", 
                    userId, request.getProductId(), newQuantity);
            
            return convertToResponse(savedItem);
        } else {
            // Crear nuevo item en el carrito
            CartItem newItem = new CartItem();
            newItem.setUserId(userId);
            newItem.setProductId(request.getProductId());
            newItem.setQuantity(request.getQuantity());
            newItem.setUnitPrice(product.getPrice());
            newItem.setProductName(product.getName());
            newItem.setProductSku(product.getSku());
            newItem.setIsActive(true);

            CartItem savedItem = cartItemRepository.save(newItem);
            
            log.info("Producto agregado al carrito: usuario {}, producto {}, cantidad {}", 
                    userId, request.getProductId(), request.getQuantity());
            
            return convertToResponse(savedItem);
        }
    }

    /**
     * Actualiza la cantidad de un producto en el carrito
     * 
     * @param userId ID del usuario
     * @param itemId ID del item del carrito
     * @param newQuantity Nueva cantidad
     * @return Item del carrito actualizado
     */
    public CartItemResponse updateCartItemQuantity(Long userId, Long itemId, int newQuantity) {
        log.info("Actualizando cantidad del item {} en carrito del usuario {}: nueva cantidad {}", 
                itemId, userId, newQuantity);

        if (newQuantity <= 0) {
            log.warn("Cantidad inválida para actualizar: {}", newQuantity);
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.warn("Item del carrito no encontrado: {}", itemId);
                    return new IllegalArgumentException("Item del carrito no encontrado");
                });

        if (!item.belongsToUser(userId)) {
            log.warn("Item {} no pertenece al usuario {}", itemId, userId);
            throw new IllegalArgumentException("Item no pertenece al usuario");
        }

        if (!item.isItemActive()) {
            log.warn("Item {} está inactivo", itemId);
            throw new IllegalArgumentException("Item del carrito inactivo");
        }

        // Validar stock disponible
        validateProductAndStock(item.getProductId(), newQuantity);

        item.updateQuantity(newQuantity);
        CartItem savedItem = cartItemRepository.save(item);
        
        log.info("Cantidad actualizada en carrito: usuario {}, item {}, nueva cantidad {}", 
                userId, itemId, newQuantity);
        
        return convertToResponse(savedItem);
    }

    /**
     * Obtiene todos los items del carrito de un usuario
     * 
     * @param userId ID del usuario
     * @return Lista de items del carrito
     */
    public List<CartItemResponse> getCartItems(Long userId) {
        log.info("Obteniendo items del carrito para usuario: {}", userId);

        List<CartItem> items = cartItemRepository.findActiveCartItemsByUserId(userId);
        
        log.info("Items obtenidos para usuario {}: {} items", userId, items.size());
        
        return items.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Elimina un item del carrito
     * 
     * @param userId ID del usuario
     * @param itemId ID del item a eliminar
     * @return true si se eliminó correctamente
     */
    public boolean removeCartItem(Long userId, Long itemId) {
        log.info("Eliminando item {} del carrito del usuario {}", itemId, userId);

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.warn("Item del carrito no encontrado: {}", itemId);
                    return new IllegalArgumentException("Item del carrito no encontrado");
                });

        if (!item.belongsToUser(userId)) {
            log.warn("Item {} no pertenece al usuario {}", itemId, userId);
            throw new IllegalArgumentException("Item no pertenece al usuario");
        }

        item.deactivate();
        cartItemRepository.save(item);
        
        log.info("Item eliminado del carrito: usuario {}, item {}", userId, itemId);
        
        return true;
    }

    /**
     * Vacía el carrito de un usuario
     * 
     * @param userId ID del usuario
     * @return true si se vació correctamente
     */
    public boolean clearCart(Long userId) {
        log.info("Vaciando carrito del usuario: {}", userId);

        List<CartItem> items = cartItemRepository.findByUserIdAndIsActiveTrue(userId);
        
        for (CartItem item : items) {
            item.deactivate();
        }
        
        cartItemRepository.saveAll(items);
        
        log.info("Carrito vaciado para usuario {}: {} items desactivados", userId, items.size());
        
        return true;
    }

    /**
     * Obtiene el total del carrito de un usuario
     * 
     * @param userId ID del usuario
     * @return Total del carrito
     */
    public BigDecimal getCartTotal(Long userId) {
        log.info("Calculando total del carrito para usuario: {}", userId);

        BigDecimal total = cartItemRepository.getCartTotalByUserId(userId);
        
        log.info("Total del carrito para usuario {}: {}", userId, total);
        
        return total != null ? total : BigDecimal.ZERO;
    }

    /**
     * Obtiene el número de items en el carrito de un usuario
     * 
     * @param userId ID del usuario
     * @return Número de items
     */
    public long getCartItemCount(Long userId) {
        log.info("Contando items del carrito para usuario: {}", userId);

        long count = cartItemRepository.countByUserIdAndIsActiveTrue(userId);
        
        log.info("Items en carrito para usuario {}: {}", userId, count);
        
        return count;
    }

    /**
     * Verifica si un producto está en el carrito de un usuario
     * 
     * @param userId ID del usuario
     * @param productId ID del producto
     * @return true si está en el carrito, false en caso contrario
     */
    public boolean isProductInCart(Long userId, Long productId) {
        log.info("Verificando si producto {} está en carrito del usuario {}", productId, userId);

        boolean exists = cartItemRepository.existsByUserIdAndProductIdAndIsActiveTrue(userId, productId);
        
        log.info("Producto {} en carrito de usuario {}: {}", productId, userId, exists);
        
        return exists;
    }

    /**
     * Obtiene información de un item específico del carrito
     * 
     * @param userId ID del usuario
     * @param itemId ID del item
     * @return Item del carrito
     */
    public CartItemResponse getCartItem(Long userId, Long itemId) {
        log.info("Obteniendo item {} del carrito del usuario {}", itemId, userId);

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.warn("Item del carrito no encontrado: {}", itemId);
                    return new IllegalArgumentException("Item del carrito no encontrado");
                });

        if (!item.belongsToUser(userId)) {
            log.warn("Item {} no pertenece al usuario {}", itemId, userId);
            throw new IllegalArgumentException("Item no pertenece al usuario");
        }

        if (!item.isItemActive()) {
            log.warn("Item {} está inactivo", itemId);
            throw new IllegalArgumentException("Item del carrito inactivo");
        }

        return convertToResponse(item);
    }

    /**
     * Valida que un producto existe y tiene suficiente stock
     * 
     * @param productId ID del producto
     * @param quantity Cantidad requerida
     * @return Información del producto
     * @throws ProductNotFoundException si el producto no existe
     * @throws InsufficientStockException si no hay suficiente stock
     */
    private ProductResponse validateProductAndStock(Long productId, int quantity) {
        // Obtener información del producto
        ProductResponse product = catalogService.getProductById(productId);
        
        // Verificar stock disponible
        boolean hasStock = catalogService.checkStockAvailability(productId, quantity);
        
        if (!hasStock) {
            log.warn("Stock insuficiente para producto {}: cantidad solicitada {}", productId, quantity);
            throw new InsufficientStockException(productId, quantity, product.getQuantity());
        }
        
        return product;
    }

    /**
     * Convierte una entidad CartItem a CartItemResponse
     * 
     * @param cartItem Entidad CartItem
     * @return CartItemResponse
     */
    private CartItemResponse convertToResponse(CartItem cartItem) {
        return new CartItemResponse(
            cartItem.getId(),
            cartItem.getUserId(),
            cartItem.getProductId(),
            cartItem.getQuantity(),
            cartItem.getUnitPrice(),
            cartItem.getTotalPrice(),
            cartItem.getProductName(),
            cartItem.getProductSku(),
            cartItem.getCreatedAt(),
            cartItem.getUpdatedAt(),
            cartItem.getIsActive()
        );
    }
}
