package com.techtrend.authentication.service;

import com.techtrend.authentication.dto.AuthResponse;
import com.techtrend.authentication.dto.LoginRequest;
import com.techtrend.authentication.dto.UserRegistrationRequest;
import com.techtrend.authentication.model.User;
import com.techtrend.authentication.repository.UserRepository;
import com.techtrend.common.exception.InvalidCredentialsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

/**
 * Servicio principal de autenticación para TechTrend
 * 
 * Maneja el registro de usuarios, validación de credenciales y generación de tokens JWT
 * 
 * @author TechTrend Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Value("${app.authentication.max-login-attempts:3}")
    private int maxLoginAttempts;

    @Value("${app.authentication.lockout-duration:300000}")
    private long lockoutDuration;

    // Patrón para validar formato de email
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );

    /**
     * Registra un nuevo usuario en el sistema
     * 
     * @param request Solicitud de registro
     * @return Respuesta de autenticación con token
     */
    public AuthResponse registerUser(UserRegistrationRequest request) {
        log.info("Iniciando registro de usuario: {}", request.getEmail());

        // Validar formato de email
        if (!isValidEmail(request.getEmail())) {
            log.warn("Formato de email inválido: {}", request.getEmail());
            throw new InvalidCredentialsException("Formato de email inválido");
        }

        // Verificar si el usuario ya existe
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Usuario ya existe: {}", request.getEmail());
            throw new InvalidCredentialsException("El usuario ya existe");
        }

        // Validar contraseña
        if (!isValidPassword(request.getPassword())) {
            log.warn("Contraseña inválida para usuario: {}", request.getEmail());
            throw new InvalidCredentialsException("La contraseña debe tener al menos 6 caracteres");
        }

        // Crear nuevo usuario
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setIsActive(true);

        // Guardar usuario
        User savedUser = userRepository.save(user);
        log.info("Usuario registrado exitosamente: {}", savedUser.getEmail());

        // Generar token
        String token = jwtService.generateToken(savedUser.getEmail(), savedUser.getRole().name());

        return AuthResponse.success(token, savedUser.getEmail(), savedUser.getRole());
    }

    /**
     * Valida las credenciales de un usuario y genera un token de acceso
     * 
     * @param request Solicitud de login
     * @return Respuesta de autenticación con token
     */
    public AuthResponse login(LoginRequest request) {
        log.info("Iniciando login para usuario: {}", request.getEmail());

        // Validar formato de email
        if (!isValidEmail(request.getEmail())) {
            log.warn("Formato de email inválido en login: {}", request.getEmail());
            throw new InvalidCredentialsException("Formato de email inválido");
        }

        // Buscar usuario
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.warn("Usuario no encontrado: {}", request.getEmail());
                    return new InvalidCredentialsException("Credenciales inválidas");
                });

        // Verificar si la cuenta está bloqueada
        if (user.isAccountLocked()) {
            log.warn("Cuenta bloqueada para usuario: {}", request.getEmail());
            throw new InvalidCredentialsException("Cuenta temporalmente bloqueada");
        }

        // Verificar si el usuario está activo
        if (!user.isUserActive()) {
            log.warn("Usuario inactivo: {}", request.getEmail());
            throw new InvalidCredentialsException("Usuario inactivo");
        }

        // Validar contraseña
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            handleFailedLogin(user);
            log.warn("Contraseña incorrecta para usuario: {}", request.getEmail());
            throw new InvalidCredentialsException("Credenciales inválidas");
        }

        // Login exitoso - resetear intentos fallidos
        user.resetLoginAttempts();
        user.updateLastLogin();
        userRepository.save(user);

        // Generar token
        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());
        log.info("Login exitoso para usuario: {}", user.getEmail());

        return AuthResponse.success(token, user.getEmail(), user.getRole());
    }

    /**
     * Valida un token JWT
     * 
     * @param token Token a validar
     * @return true si el token es válido, false en caso contrario
     */
    public boolean validateToken(String token) {
        try {
            return jwtService.isTokenValid(token);
        } catch (Exception e) {
            log.warn("Error validando token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene el email del usuario desde un token
     * 
     * @param token Token JWT
     * @return Email del usuario
     */
    public String getEmailFromToken(String token) {
        try {
            return jwtService.extractEmail(token);
        } catch (Exception e) {
            log.warn("Error extrayendo email del token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Obtiene el rol del usuario desde un token
     * 
     * @param token Token JWT
     * @return Rol del usuario
     */
    public String getRoleFromToken(String token) {
        try {
            return jwtService.extractRole(token);
        } catch (Exception e) {
            log.warn("Error extrayendo rol del token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Valida el formato de un email
     * 
     * @param email Email a validar
     * @return true si el formato es válido, false en caso contrario
     */
    private boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Valida la contraseña
     * 
     * @param password Contraseña a validar
     * @return true si la contraseña es válida, false en caso contrario
     */
    private boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    /**
     * Maneja un intento de login fallido
     * 
     * @param user Usuario que falló el login
     */
    private void handleFailedLogin(User user) {
        user.incrementLoginAttempts();
        
        // Bloquear cuenta si se exceden los intentos
        if (user.getLoginAttempts() >= maxLoginAttempts) {
            LocalDateTime lockUntil = LocalDateTime.now().plusSeconds(lockoutDuration / 1000);
            user.lockAccount(lockUntil);
            log.warn("Cuenta bloqueada para usuario: {} hasta {}", user.getEmail(), lockUntil);
        }
        
        userRepository.save(user);
    }

    /**
     * Verifica si un usuario existe por email
     * 
     * @param email Email del usuario
     * @return true si existe, false en caso contrario
     */
    public boolean userExists(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Obtiene un usuario por email
     * 
     * @param email Email del usuario
     * @return Usuario si existe, null en caso contrario
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
}
