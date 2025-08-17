package com.techtrend.authentication.repository;

import com.techtrend.authentication.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad User
 * 
 * Proporciona métodos para acceder y manipular datos de usuarios
 * 
 * @author TechTrend Team
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca un usuario por su email
     * 
     * @param email Email del usuario
     * @return Optional con el usuario si existe
     */
    Optional<User> findByEmail(String email);

    /**
     * Verifica si existe un usuario con el email especificado
     * 
     * @param email Email a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsByEmail(String email);

    /**
     * Busca usuarios activos por rol
     * 
     * @param role Rol de usuario
     * @return Lista de usuarios activos con el rol especificado
     */
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.isActive = true")
    java.util.List<User> findActiveUsersByRole(@Param("role") User.UserRole role);

    /**
     * Busca usuarios bloqueados
     * 
     * @return Lista de usuarios que están actualmente bloqueados
     */
    @Query("SELECT u FROM User u WHERE u.lockedUntil IS NOT NULL AND u.lockedUntil > CURRENT_TIMESTAMP")
    java.util.List<User> findLockedUsers();

    /**
     * Cuenta usuarios activos
     * 
     * @return Número de usuarios activos
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.isActive = true")
    long countActiveUsers();

    /**
     * Busca usuarios con múltiples intentos de login fallidos
     * 
     * @param maxAttempts Número máximo de intentos permitidos
     * @return Lista de usuarios que han excedido los intentos
     */
    @Query("SELECT u FROM User u WHERE u.loginAttempts >= :maxAttempts")
    java.util.List<User> findUsersWithExcessiveLoginAttempts(@Param("maxAttempts") int maxAttempts);
}
