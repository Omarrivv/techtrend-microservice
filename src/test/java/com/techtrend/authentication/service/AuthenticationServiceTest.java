package com.techtrend.authentication.service;

import com.techtrend.authentication.dto.AuthResponse;
import com.techtrend.authentication.dto.LoginRequest;
import com.techtrend.authentication.dto.UserRegistrationRequest;
import com.techtrend.authentication.model.User;
import com.techtrend.authentication.repository.UserRepository;
import com.techtrend.common.exception.InvalidCredentialsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests simples y legibles para el servicio de autenticación
 */
@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthenticationService authenticationService;

    private User usuarioPrueba;
    private UserRegistrationRequest solicitudRegistro;
    private LoginRequest solicitudLogin;

    @BeforeEach
    void configurarPruebas() {
        // Configurar límites de intentos de login
        ReflectionTestUtils.setField(authenticationService, "maxLoginAttempts", 3);
        ReflectionTestUtils.setField(authenticationService, "lockoutDuration", 300000L);

        // Crear usuario de prueba
        usuarioPrueba = new User();
        usuarioPrueba.setId(1L);
        usuarioPrueba.setEmail("usuario@techtrend.com");
        usuarioPrueba.setPassword("passwordEncriptada");
        usuarioPrueba.setRole(User.UserRole.CLIENT);
        usuarioPrueba.setIsActive(true);
        usuarioPrueba.setLoginAttempts(0);

        // Crear solicitud de registro
        solicitudRegistro = new UserRegistrationRequest();
        solicitudRegistro.setEmail("nuevo@techtrend.com");
        solicitudRegistro.setPassword("password123");
        solicitudRegistro.setRole(User.UserRole.CLIENT);

        // Crear solicitud de login
        solicitudLogin = new LoginRequest();
        solicitudLogin.setEmail("usuario@techtrend.com");
        solicitudLogin.setPassword("password123");
    }

    // ===== TESTS DE REGISTRO =====

    @Test
    @DisplayName("✅ Registro exitoso con datos válidos")
    void registroExitoso() {
        // PREPARAR
        User usuarioGuardado = crearUsuarioGuardado(solicitudRegistro.getEmail());
        
        when(userRepository.existsByEmail(solicitudRegistro.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(solicitudRegistro.getPassword())).thenReturn("passwordEncriptada");
        when(userRepository.save(any(User.class))).thenReturn(usuarioGuardado);
        when(jwtService.generateToken(anyString(), anyString())).thenReturn("tokenJWT");

        // EJECUTAR
        AuthResponse respuesta = authenticationService.registerUser(solicitudRegistro);

        // VERIFICAR
        assertNotNull(respuesta);
        assertTrue(respuesta.isSuccess());
        assertEquals("tokenJWT", respuesta.getToken());
        assertEquals(solicitudRegistro.getEmail(), respuesta.getEmail());
        assertEquals("Autenticación exitosa", respuesta.getMessage());
    }

    @Test
    @DisplayName("❌ Error: Email con formato inválido")
    void errorEmailInvalido() {
        // PREPARAR
        solicitudRegistro.setEmail("email-invalido");

        // EJECUTAR Y VERIFICAR
        InvalidCredentialsException excepcion = assertThrows(
            InvalidCredentialsException.class,
            () -> authenticationService.registerUser(solicitudRegistro)
        );

        assertEquals("Formato de email inválido", excepcion.getMessage());
    }

    @Test
    @DisplayName("❌ Error: Usuario ya existe")
    void errorUsuarioYaExiste() {
        // PREPARAR
        when(userRepository.existsByEmail(solicitudRegistro.getEmail())).thenReturn(true);

        // EJECUTAR Y VERIFICAR
        InvalidCredentialsException excepcion = assertThrows(
            InvalidCredentialsException.class,
            () -> authenticationService.registerUser(solicitudRegistro)
        );

        assertEquals("El usuario ya existe", excepcion.getMessage());
    }

    @Test
    @DisplayName("❌ Error: Contraseña muy corta")
    void errorPasswordMuyCorta() {
        // PREPARAR
        solicitudRegistro.setPassword("123");
        when(userRepository.existsByEmail(solicitudRegistro.getEmail())).thenReturn(false);

        // EJECUTAR Y VERIFICAR
        InvalidCredentialsException excepcion = assertThrows(
            InvalidCredentialsException.class,
            () -> authenticationService.registerUser(solicitudRegistro)
        );

        assertEquals("La contraseña debe tener al menos 6 caracteres", excepcion.getMessage());
    }

    // ===== TESTS DE LOGIN =====

    @Test
    @DisplayName("✅ Login exitoso con credenciales válidas")
    void loginExitoso() {
        // PREPARAR
        when(userRepository.findByEmail(solicitudLogin.getEmail())).thenReturn(Optional.of(usuarioPrueba));
        when(passwordEncoder.matches(solicitudLogin.getPassword(), usuarioPrueba.getPassword())).thenReturn(true);
        when(jwtService.generateToken(usuarioPrueba.getEmail(), usuarioPrueba.getRole().name())).thenReturn("tokenJWT");
        when(userRepository.save(any(User.class))).thenReturn(usuarioPrueba);

        // EJECUTAR
        AuthResponse respuesta = authenticationService.login(solicitudLogin);

        // VERIFICAR
        assertNotNull(respuesta);
        assertTrue(respuesta.isSuccess());
        assertEquals("tokenJWT", respuesta.getToken());
        assertEquals(usuarioPrueba.getEmail(), respuesta.getEmail());
    }

    @Test
    @DisplayName("❌ Error: Usuario no existe")
    void errorUsuarioNoExiste() {
        // PREPARAR
        when(userRepository.findByEmail(solicitudLogin.getEmail())).thenReturn(Optional.empty());

        // EJECUTAR Y VERIFICAR
        InvalidCredentialsException excepcion = assertThrows(
            InvalidCredentialsException.class,
            () -> authenticationService.login(solicitudLogin)
        );

        assertEquals("Credenciales inválidas", excepcion.getMessage());
    }

    @Test
    @DisplayName("❌ Error: Contraseña incorrecta")
    void errorPasswordIncorrecta() {
        // PREPARAR
        when(userRepository.findByEmail(solicitudLogin.getEmail())).thenReturn(Optional.of(usuarioPrueba));
        when(passwordEncoder.matches(solicitudLogin.getPassword(), usuarioPrueba.getPassword())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(usuarioPrueba);

        // EJECUTAR Y VERIFICAR
        InvalidCredentialsException excepcion = assertThrows(
            InvalidCredentialsException.class,
            () -> authenticationService.login(solicitudLogin)
        );

        assertEquals("Credenciales inválidas", excepcion.getMessage());
    }

    @Test
    @DisplayName("❌ Error: Cuenta bloqueada")
    void errorCuentaBloqueada() {
        // PREPARAR
        usuarioPrueba.setLockedUntil(LocalDateTime.now().plusMinutes(5));
        when(userRepository.findByEmail(solicitudLogin.getEmail())).thenReturn(Optional.of(usuarioPrueba));

        // EJECUTAR Y VERIFICAR
        InvalidCredentialsException excepcion = assertThrows(
            InvalidCredentialsException.class,
            () -> authenticationService.login(solicitudLogin)
        );

        assertEquals("Cuenta temporalmente bloqueada", excepcion.getMessage());
    }

    @Test
    @DisplayName("❌ Error: Usuario inactivo")
    void errorUsuarioInactivo() {
        // PREPARAR
        usuarioPrueba.setIsActive(false);
        when(userRepository.findByEmail(solicitudLogin.getEmail())).thenReturn(Optional.of(usuarioPrueba));

        // EJECUTAR Y VERIFICAR
        InvalidCredentialsException excepcion = assertThrows(
            InvalidCredentialsException.class,
            () -> authenticationService.login(solicitudLogin)
        );

        assertEquals("Usuario inactivo", excepcion.getMessage());
    }

    @Test
    @DisplayName("🔒 Bloquear cuenta después de 3 intentos fallidos")
    void bloquearCuentaPorIntentosFallidos() {
        // PREPARAR
        usuarioPrueba.setLoginAttempts(2); // Ya tiene 2 intentos fallidos
        when(userRepository.findByEmail(solicitudLogin.getEmail())).thenReturn(Optional.of(usuarioPrueba));
        when(passwordEncoder.matches(solicitudLogin.getPassword(), usuarioPrueba.getPassword())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(usuarioPrueba);

        // EJECUTAR
        InvalidCredentialsException excepcion = assertThrows(
            InvalidCredentialsException.class,
            () -> authenticationService.login(solicitudLogin)
        );

        // VERIFICAR
        assertEquals("Credenciales inválidas", excepcion.getMessage());
        
        // Verificar que la cuenta se bloqueó
        verify(userRepository).save(argThat(user -> 
            user.getLoginAttempts() == 3 && user.getLockedUntil() != null
        ));
    }

    // ===== TESTS DE TOKENS =====

    @Test
    @DisplayName("✅ Validar token correctamente")
    void validarToken() {
        // PREPARAR
        String token = "tokenValido";
        when(jwtService.isTokenValid(token)).thenReturn(true);

        // EJECUTAR
        boolean esValido = authenticationService.validateToken(token);

        // VERIFICAR
        assertTrue(esValido);
        verify(jwtService).isTokenValid(token);
    }

    @Test
    @DisplayName("✅ Extraer email del token")
    void extraerEmailDelToken() {
        // PREPARAR
        String token = "tokenValido";
        String emailEsperado = "usuario@techtrend.com";
        when(jwtService.extractEmail(token)).thenReturn(emailEsperado);

        // EJECUTAR
        String email = authenticationService.getEmailFromToken(token);

        // VERIFICAR
        assertEquals(emailEsperado, email);
        verify(jwtService).extractEmail(token);
    }

    @Test
    @DisplayName("✅ Extraer rol del token")
    void extraerRolDelToken() {
        // PREPARAR
        String token = "tokenValido";
        String rolEsperado = "CLIENT";
        when(jwtService.extractRole(token)).thenReturn(rolEsperado);

        // EJECUTAR
        String rol = authenticationService.getRoleFromToken(token);

        // VERIFICAR
        assertEquals(rolEsperado, rol);
        verify(jwtService).extractRole(token);
    }

    // ===== TESTS DE USUARIOS =====

    @Test
    @DisplayName("✅ Verificar si usuario existe")
    void verificarUsuarioExiste() {
        // PREPARAR
        String email = "usuario@techtrend.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);

        // EJECUTAR
        boolean existe = authenticationService.userExists(email);

        // VERIFICAR
        assertTrue(existe);
        verify(userRepository).existsByEmail(email);
    }

    @Test
    @DisplayName("✅ Obtener usuario por email")
    void obtenerUsuarioPorEmail() {
        // PREPARAR
        String email = "usuario@techtrend.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(usuarioPrueba));

        // EJECUTAR
        User usuario = authenticationService.getUserByEmail(email);

        // VERIFICAR
        assertNotNull(usuario);
        assertEquals(usuarioPrueba, usuario);
        verify(userRepository).findByEmail(email);
    }

    @Test
    @DisplayName("❌ Usuario no encontrado")
    void usuarioNoEncontrado() {
        // PREPARAR
        String email = "noexiste@techtrend.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // EJECUTAR
        User usuario = authenticationService.getUserByEmail(email);

        // VERIFICAR
        assertNull(usuario);
        verify(userRepository).findByEmail(email);
    }

    // ===== MÉTODOS AUXILIARES =====

    private User crearUsuarioGuardado(String email) {
        User usuario = new User();
        usuario.setId(1L);
        usuario.setEmail(email);
        usuario.setPassword("passwordEncriptada");
        usuario.setRole(User.UserRole.CLIENT);
        usuario.setIsActive(true);
        usuario.setLoginAttempts(0);
        return usuario;
    }
}
