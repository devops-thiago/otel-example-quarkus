package br.com.arquivolivre.otelquarkus.repository;

import br.com.arquivolivre.otelquarkus.model.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for User entity operations.
 * Uses Panache Repository pattern for database operations.
 */
@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {

    /**
     * Find user by email
     * @param email User's email address
     * @return Optional containing user if found
     */
    public Optional<User> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }

    /**
     * Search users by name (case-insensitive partial match)
     * @param name Name to search for
     * @return List of matching users
     */
    public List<User> searchByName(String name) {
        return list("LOWER(name) LIKE LOWER(?1)", "%" + name + "%");
    }

    /**
     * Find users created within the specified number of days
     * @param days Number of days to look back
     * @return List of recent users
     */
    public List<User> findRecentUsers(int days) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        return list("createdAt >= ?1", cutoffDate);
    }

    /**
     * Check if email already exists
     * @param email Email to check
     * @return true if email exists, false otherwise
     */
    public boolean existsByEmail(String email) {
        return count("email", email) > 0;
    }

    /**
     * Check if email exists for a different user (for update validation)
     * @param email Email to check
     * @param excludeId ID to exclude from check
     * @return true if email exists for a different user
     */
    public boolean existsByEmailAndIdNot(String email, Long excludeId) {
        return count("email = ?1 and id != ?2", email, excludeId) > 0;
    }

    /**
     * Count total number of users
     * @return Total user count
     */
    public long countUsers() {
        return count();
    }

    /**
     * Delete user by ID
     * @param id User ID
     * @return true if deleted, false if not found
     */
    public boolean deleteUser(Long id) {
        return deleteById(id);
    }
}