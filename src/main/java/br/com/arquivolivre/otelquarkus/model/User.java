package br.com.arquivolivre.otelquarkus.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Objects;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * User entity representing a user in the system. Uses Panache Entity pattern for simplified
 * database operations.
 */
@Entity
@Table(name = "users")
public class User extends PanacheEntity {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Column(nullable = false, length = 100)
    public String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Column(nullable = false, unique = true, length = 100)
    public String email;

    @Size(max = 500, message = "Bio must not exceed 500 characters")
    @Column(length = 500)
    public String bio;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    public LocalDateTime updatedAt;

    /** Default constructor */
    public User() {}

    /** Constructor with all fields */
    public User(String name, String email, String bio) {
        this.name = name;
        this.email = email;
        this.bio = bio;
    }

    /** Getter method for ID (required for proper Panache functionality) */
    public Long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return Objects.equals(id, user.id) && Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }

    @Override
    public String toString() {
        return "User{"
                + "id="
                + id
                + ", name='"
                + name
                + '\''
                + ", email='"
                + email
                + '\''
                + ", createdAt="
                + createdAt
                + ", updatedAt="
                + updatedAt
                + '}';
    }
}
