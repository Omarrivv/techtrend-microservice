package com.techtrend.cart.service;

import com.techtrend.cart.dto.CartItemRequest;
import com.techtrend.cart.dto.CartItemResponse;
import com.techtrend.cart.model.CartItem;
import com.techtrend.cart.repository.CartItemRepository;
import com.techtrend.catalog.dto.ProductResponse;
import com.techtrend.catalog.service.CatalogService;
import com.techtrend.common.exception.InsufficientStockException;
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
 * Tests simples y legibles para el servicio de carrito
 */
@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private CatalogService catalogService;

    @InjectMocks
    private CartService cartService;

    private CartItem itemCarrito;
    private CartItemRequest solicitudItem;
    private ProductResponse producto;
    private Long idUsuario = 1L;
    private Long idProducto = 1L;

    @BeforeEach
    void configurarPruebas() {
        // Configurar límite de items por carrito
        ReflectionTestUtils.setField(cartService, "maxItemsPerCart", 50);

        // Crear producto de prueba
        producto = new ProductResponse();
        producto.setId(idProducto);
        producto.setName("Laptop Gaming");
        producto.setPrice(new BigDecimal("1500.00"));
        producto.setQuantity(100);
        producto.setSku("LAP001");

        // Crear item de carrito de prueba
        itemCarrito = new CartItem();
        itemCarrito.setId(1L);
        itemCarrito.setUserId(idUsuario);
        itemCarrito.setProductId(idProducto);
        itemCarrito.setQuantity(2);
        itemCarrito.setUnitPrice(producto.getPrice());
        itemCarrito.setTotalPrice(producto.getPrice().multiply(BigDecimal.valueOf(2)));
        itemCarrito.setProductName(producto.getName());
        itemCarrito.setProductSku(producto.getSku());
        itemCarrito.setIsActive(true);
        itemCarrito.setCreatedAt(LocalDateTime.now());
        itemCarrito.setUpdatedAt(LocalDateTime.now());

        // Crear solicitud de prueba
        solicitudItem = new CartItemRequest();
        solicitudItem.setProductId(idProducto);
        solicitudItem.setQuantity(1);
    }

    // ===== TESTS DE AGREGAR PRODUCTOS =====

    @Test
    @DisplayName("✅ Agregar producto nuevo al carrito")
    void agregarProductoNuevo() {
        // PREPARAR
        CartItem itemGuardado = crearItemGuardado(solicitudItem.getQuantity());
        
        when(catalogService.getProductById(idProducto)).thenReturn(producto);
        when(catalogService.checkStockAvailability(idProducto, solicitudItem.getQuantity())).thenReturn(true);
        when(cartItemRepository.findByUserIdAndProductIdAndIsActiveTrue(idUsuario, idProducto))
                .thenReturn(Optional.empty());
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(itemGuardado);

        // EJECUTAR
        CartItemResponse respuesta = cartService.addProductToCart(idUsuario, solicitudItem);

        // VERIFICAR
        assertNotNull(respuesta);
        assertEquals(itemGuardado.getId(), respuesta.getId());
        assertEquals(idUsuario, respuesta.getUserId());
        assertEquals(idProducto, respuesta.getProductId());
        assertEquals(solicitudItem.getQuantity(), respuesta.getQuantity());
    }

    @Test
    @DisplayName("✅ Actualizar cantidad de producto existente")
    void actualizarProductoExistente() {
        // PREPARAR
        int cantidadExistente = 2;
        int cantidadNueva = 1;
        int cantidadTotal = cantidadExistente + cantidadNueva;
        
        itemCarrito.setQuantity(cantidadExistente);
        
        when(catalogService.getProductById(idProducto)).thenReturn(producto);
        when(catalogService.checkStockAvailability(idProducto, solicitudItem.getQuantity())).thenReturn(true);
        when(catalogService.checkStockAvailability(idProducto, cantidadTotal)).thenReturn(true);
        when(cartItemRepository.findByUserIdAndProductIdAndIsActiveTrue(idUsuario, idProducto))
                .thenReturn(Optional.of(itemCarrito));
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(itemCarrito);

        // EJECUTAR
        CartItemResponse respuesta = cartService.addProductToCart(idUsuario, solicitudItem);

        // VERIFICAR
        assertNotNull(respuesta);
        assertEquals(cantidadTotal, itemCarrito.getQuantity());
    }

    @Test
    @DisplayName("❌ Error: Stock insuficiente")
    void errorStockInsuficiente() {
        // PREPARAR
        when(catalogService.getProductById(idProducto)).thenReturn(producto);
        when(catalogService.checkStockAvailability(idProducto, solicitudItem.getQuantity())).thenReturn(false);

        // EJECUTAR Y VERIFICAR
        InsufficientStockException excepcion = assertThrows(
            InsufficientStockException.class,
            () -> cartService.addProductToCart(idUsuario, solicitudItem)
        );

        assertTrue(excepcion.getMessage().contains("Stock insuficiente"));
    }

    // ===== TESTS DE ACTUALIZAR CANTIDADES =====

    @Test
    @DisplayName("✅ Actualizar cantidad de item correctamente")
    void actualizarCantidadItem() {
        // PREPARAR
        Long idItem = 1L;
        int nuevaCantidad = 5;
        
        when(cartItemRepository.findById(idItem)).thenReturn(Optional.of(itemCarrito));
        when(catalogService.getProductById(idProducto)).thenReturn(producto);
        when(catalogService.checkStockAvailability(idProducto, nuevaCantidad)).thenReturn(true);
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(itemCarrito);

        // EJECUTAR
        CartItemResponse respuesta = cartService.updateCartItemQuantity(idUsuario, idItem, nuevaCantidad);

        // VERIFICAR
        assertNotNull(respuesta);
        assertEquals(nuevaCantidad, itemCarrito.getQuantity());
    }

    @Test
    @DisplayName("❌ Error: Cantidad negativa")
    void errorCantidadNegativa() {
        // PREPARAR
        Long idItem = 1L;
        int cantidadNegativa = -1;

        // EJECUTAR Y VERIFICAR
        IllegalArgumentException excepcion = assertThrows(
            IllegalArgumentException.class,
            () -> cartService.updateCartItemQuantity(idUsuario, idItem, cantidadNegativa)
        );

        assertEquals("La cantidad debe ser mayor a cero", excepcion.getMessage());
    }

    @Test
    @DisplayName("❌ Error: Item no pertenece al usuario")
    void errorItemNoPerteneceUsuario() {
        // PREPARAR
        Long idItem = 1L;
        Long idUsuarioDiferente = 999L;
        int nuevaCantidad = 5;
        
        itemCarrito.setUserId(idUsuarioDiferente);
        when(cartItemRepository.findById(idItem)).thenReturn(Optional.of(itemCarrito));

        // EJECUTAR Y VERIFICAR
        IllegalArgumentException excepcion = assertThrows(
            IllegalArgumentException.class,
            () -> cartService.updateCartItemQuantity(idUsuario, idItem, nuevaCantidad)
        );

        assertEquals("Item no pertenece al usuario", excepcion.getMessage());
    }

    // ===== TESTS DE OBTENER ITEMS =====

    @Test
    @DisplayName("✅ Obtener items del carrito")
    void obtenerItemsCarrito() {
        // PREPARAR
        List<CartItem> items = Arrays.asList(itemCarrito);
        when(cartItemRepository.findActiveCartItemsByUserId(idUsuario)).thenReturn(items);

        // EJECUTAR
        List<CartItemResponse> respuesta = cartService.getCartItems(idUsuario);

        // VERIFICAR
        assertNotNull(respuesta);
        assertEquals(1, respuesta.size());
        assertEquals(itemCarrito.getId(), respuesta.get(0).getId());
        assertEquals(idUsuario, respuesta.get(0).getUserId());
    }

    @Test
    @DisplayName("✅ Obtener item específico del carrito")
    void obtenerItemEspecifico() {
        // PREPARAR
        Long idItem = 1L;
        when(cartItemRepository.findById(idItem)).thenReturn(Optional.of(itemCarrito));

        // EJECUTAR
        CartItemResponse respuesta = cartService.getCartItem(idUsuario, idItem);

        // VERIFICAR
        assertNotNull(respuesta);
        assertEquals(itemCarrito.getId(), respuesta.getId());
        assertEquals(idUsuario, respuesta.getUserId());
    }

    @Test
    @DisplayName("❌ Error: Item no encontrado")
    void errorItemNoEncontrado() {
        // PREPARAR
        Long idItem = 999L;
        when(cartItemRepository.findById(idItem)).thenReturn(Optional.empty());

        // EJECUTAR Y VERIFICAR
        IllegalArgumentException excepcion = assertThrows(
            IllegalArgumentException.class,
            () -> cartService.getCartItem(idUsuario, idItem)
        );

        assertEquals("Item del carrito no encontrado", excepcion.getMessage());
    }

    @Test
    @DisplayName("❌ Error: Item inactivo")
    void errorItemInactivo() {
        // PREPARAR
        Long idItem = 1L;
        itemCarrito.setIsActive(false);
        when(cartItemRepository.findById(idItem)).thenReturn(Optional.of(itemCarrito));

        // EJECUTAR Y VERIFICAR
        IllegalArgumentException excepcion = assertThrows(
            IllegalArgumentException.class,
            () -> cartService.getCartItem(idUsuario, idItem)
        );

        assertEquals("Item del carrito inactivo", excepcion.getMessage());
    }

    // ===== TESTS DE ELIMINAR ITEMS =====

    @Test
    @DisplayName("✅ Eliminar item del carrito")
    void eliminarItemCarrito() {
        // PREPARAR
        Long idItem = 1L;
        when(cartItemRepository.findById(idItem)).thenReturn(Optional.of(itemCarrito));
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(itemCarrito);

        // EJECUTAR
        boolean resultado = cartService.removeCartItem(idUsuario, idItem);

        // VERIFICAR
        assertTrue(resultado);
        assertFalse(itemCarrito.getIsActive());
    }

    @Test
    @DisplayName("❌ Error: Item a eliminar no existe")
    void errorItemEliminarNoExiste() {
        // PREPARAR
        Long idItem = 999L;
        when(cartItemRepository.findById(idItem)).thenReturn(Optional.empty());

        // EJECUTAR Y VERIFICAR
        IllegalArgumentException excepcion = assertThrows(
            IllegalArgumentException.class,
            () -> cartService.removeCartItem(idUsuario, idItem)
        );

        assertEquals("Item del carrito no encontrado", excepcion.getMessage());
    }

    @Test
    @DisplayName("✅ Vaciar carrito completo")
    void vaciarCarrito() {
        // PREPARAR
        List<CartItem> items = Arrays.asList(itemCarrito);
        when(cartItemRepository.findByUserIdAndIsActiveTrue(idUsuario)).thenReturn(items);
        when(cartItemRepository.saveAll(anyList())).thenReturn(items);

        // EJECUTAR
        boolean resultado = cartService.clearCart(idUsuario);

        // VERIFICAR
        assertTrue(resultado);
        assertFalse(itemCarrito.getIsActive());
    }

    // ===== TESTS DE CÁLCULOS =====

    @Test
    @DisplayName("✅ Calcular total del carrito")
    void calcularTotalCarrito() {
        // PREPARAR
        BigDecimal totalEsperado = new BigDecimal("3000.00");
        when(cartItemRepository.getCartTotalByUserId(idUsuario)).thenReturn(totalEsperado);

        // EJECUTAR
        BigDecimal resultado = cartService.getCartTotal(idUsuario);

        // VERIFICAR
        assertEquals(totalEsperado, resultado);
    }

    @Test
    @DisplayName("✅ Total cero cuando carrito vacío")
    void totalCeroCarritoVacio() {
        // PREPARAR
        when(cartItemRepository.getCartTotalByUserId(idUsuario)).thenReturn(null);

        // EJECUTAR
        BigDecimal resultado = cartService.getCartTotal(idUsuario);

        // VERIFICAR
        assertEquals(BigDecimal.ZERO, resultado);
    }

    @Test
    @DisplayName("✅ Contar items del carrito")
    void contarItemsCarrito() {
        // PREPARAR
        long cantidadEsperada = 3L;
        when(cartItemRepository.countByUserIdAndIsActiveTrue(idUsuario)).thenReturn(cantidadEsperada);

        // EJECUTAR
        long resultado = cartService.getCartItemCount(idUsuario);

        // VERIFICAR
        assertEquals(cantidadEsperada, resultado);
    }

    // ===== TESTS DE VERIFICACIONES =====

    @Test
    @DisplayName("✅ Verificar si producto está en carrito")
    void verificarProductoEnCarrito() {
        // PREPARAR
        when(cartItemRepository.existsByUserIdAndProductIdAndIsActiveTrue(idUsuario, idProducto))
                .thenReturn(true);

        // EJECUTAR
        boolean resultado = cartService.isProductInCart(idUsuario, idProducto);

        // VERIFICAR
        assertTrue(resultado);
    }

    @Test
    @DisplayName("❌ Producto no está en carrito")
    void productoNoEnCarrito() {
        // PREPARAR
        when(cartItemRepository.existsByUserIdAndProductIdAndIsActiveTrue(idUsuario, idProducto))
                .thenReturn(false);

        // EJECUTAR
        boolean resultado = cartService.isProductInCart(idUsuario, idProducto);

        // VERIFICAR
        assertFalse(resultado);
    }

    // ===== MÉTODOS AUXILIARES =====

    private CartItem crearItemGuardado(int cantidad) {
        CartItem item = new CartItem();
        item.setId(1L);
        item.setUserId(idUsuario);
        item.setProductId(idProducto);
        item.setQuantity(cantidad);
        item.setUnitPrice(producto.getPrice());
        item.setTotalPrice(producto.getPrice().multiply(BigDecimal.valueOf(cantidad)));
        item.setProductName(producto.getName());
        item.setProductSku(producto.getSku());
        item.setIsActive(true);
        item.setCreatedAt(LocalDateTime.now());
        item.setUpdatedAt(LocalDateTime.now());
        return item;
    }
}
