package com.techtrend.authentication.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad que representa un usuario en el sistema TechTrend
 * 
 * @author TechTrend Team
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Column(nullable = false)
    private String password;

    @NotNull(message = "El rol es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "login_attempts")
    private Integer loginAttempts = 0;

    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;

    /**
     * Enum que define los roles de usuario disponibles
     */
    public enum UserRole {
        CLIENT,
        ADMIN
    }

    /**
     * Método para incrementar los intentos de login fallidos
     */
    public void incrementLoginAttempts() {
        this.loginAttempts++;
    }

    /**
     * Método para resetear los intentos de login
     */
    public void resetLoginAttempts() {
        this.loginAttempts = 0;
        this.lockedUntil = null;
    }

    /**
     * Método para bloquear la cuenta temporalmente
     */
    public void lockAccount(LocalDateTime lockUntil) {
        this.lockedUntil = lockUntil;
    }

    /**
     * Método para verificar si la cuenta está bloqueada
     */
    public boolean isAccountLocked() {
        return this.lockedUntil != null && LocalDateTime.now().isBefore(this.lockedUntil);
    }

    /**
     * Método para actualizar la fecha de último login
     */
    public void updateLastLogin() {
        this.lastLogin = LocalDateTime.now();
    }

    /**
     * Método para verificar si el usuario está activo
     */
    public boolean isUserActive() {
        return this.isActive != null && this.isActive;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
