package br.com.arquivolivre.otelquarkus.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for User model class. Tests constructors, getters, equals, hashCode, and toString
 * methods.
 */
class UserTest {

    @Test
    void testDefaultConstructor() {
        // When
        User user = new User();

        // Then
        assertThat(user).isNotNull();
        assertThat(user.name).isNull();
        assertThat(user.email).isNull();
        assertThat(user.bio).isNull();
    }

    @Test
    void testParameterizedConstructor() {
        // When
        User user = new User("John Doe", "john@example.com", "Software Developer");

        // Then
        assertThat(user).isNotNull();
        assertThat(user.name).isEqualTo("John Doe");
        assertThat(user.email).isEqualTo("john@example.com");
        assertThat(user.bio).isEqualTo("Software Developer");
    }

    @Test
    void testParameterizedConstructorWithNullBio() {
        // When
        User user = new User("Jane Doe", "jane@example.com", null);

        // Then
        assertThat(user).isNotNull();
        assertThat(user.name).isEqualTo("Jane Doe");
        assertThat(user.email).isEqualTo("jane@example.com");
        assertThat(user.bio).isNull();
    }

    @Test
    void testGetId() {
        // Given
        User user = new User("John Doe", "john@example.com", "Bio");
        user.id = 42L;

        // When
        Long id = user.getId();

        // Then
        assertThat(id).isEqualTo(42L);
    }

    @Test
    void testGetIdWhenNull() {
        // Given
        User user = new User();

        // When
        Long id = user.getId();

        // Then
        assertThat(id).isNull();
    }

    @Test
    void testEqualsSameObject() {
        // Given
        User user = new User("John Doe", "john@example.com", "Bio");
        user.id = 1L;

        // When/Then
        assertThat(user).isEqualTo(user);
    }

    @Test
    void testEqualsEqualUsers() {
        // Given
        User user1 = new User("John Doe", "john@example.com", "Bio");
        user1.id = 1L;

        User user2 = new User("John Doe", "john@example.com", "Bio");
        user2.id = 1L;

        // When/Then
        assertThat(user1).isEqualTo(user2);
    }

    @Test
    void testEqualsDifferentId() {
        // Given
        User user1 = new User("John Doe", "john@example.com", "Bio");
        user1.id = 1L;

        User user2 = new User("John Doe", "john@example.com", "Bio");
        user2.id = 2L;

        // When/Then
        assertThat(user1).isNotEqualTo(user2);
    }

    @Test
    void testEqualsDifferentEmail() {
        // Given
        User user1 = new User("John Doe", "john@example.com", "Bio");
        user1.id = 1L;

        User user2 = new User("John Doe", "jane@example.com", "Bio");
        user2.id = 1L;

        // When/Then
        assertThat(user1).isNotEqualTo(user2);
    }

    @Test
    void testEqualsNullObject() {
        // Given
        User user = new User("John Doe", "john@example.com", "Bio");

        // When/Then
        assertThat(user).isNotEqualTo(null);
    }

    @Test
    void testEqualsDifferentClass() {
        // Given
        User user = new User("John Doe", "john@example.com", "Bio");
        String notAUser = "Not a user";

        // When/Then
        assertThat(user).isNotEqualTo(notAUser);
    }

    @Test
    void testEqualsBothNullIds() {
        // Given
        User user1 = new User("John Doe", "john@example.com", "Bio");
        User user2 = new User("John Doe", "john@example.com", "Bio");

        // When/Then
        assertThat(user1).isEqualTo(user2);
    }

    @Test
    void testEqualsNullEmails() {
        // Given
        User user1 = new User("John Doe", null, "Bio");
        user1.id = 1L;

        User user2 = new User("John Doe", null, "Bio");
        user2.id = 1L;

        // When/Then
        assertThat(user1).isEqualTo(user2);
    }

    @Test
    void testHashCodeConsistentForSameObject() {
        // Given
        User user = new User("John Doe", "john@example.com", "Bio");
        user.id = 1L;

        // When
        int hash1 = user.hashCode();
        int hash2 = user.hashCode();

        // Then
        assertThat(hash1).isEqualTo(hash2);
    }

    @Test
    void testHashCodeEqualForEqualObjects() {
        // Given
        User user1 = new User("John Doe", "john@example.com", "Bio");
        user1.id = 1L;

        User user2 = new User("John Doe", "john@example.com", "Bio");
        user2.id = 1L;

        // When/Then
        assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
    }

    @Test
    void testHashCodeDifferentForDifferentIds() {
        // Given
        User user1 = new User("John Doe", "john@example.com", "Bio");
        user1.id = 1L;

        User user2 = new User("John Doe", "john@example.com", "Bio");
        user2.id = 2L;

        // When/Then
        assertThat(user1.hashCode()).isNotEqualTo(user2.hashCode());
    }

    @Test
    void testHashCodeWithNullFields() {
        // Given
        User user = new User();

        // When
        int hashCode = user.hashCode();

        // Then - should not throw NPE
        assertThat(hashCode).isNotNull();
    }

    @Test
    void testToStringWithAllFields() {
        // Given
        User user = new User("John Doe", "john@example.com", "Bio");
        user.id = 1L;
        user.createdAt = LocalDateTime.of(2024, 1, 1, 12, 0);
        user.updatedAt = LocalDateTime.of(2024, 1, 2, 12, 0);

        // When
        String result = user.toString();

        // Then
        assertThat(result).contains("User{");
        assertThat(result).contains("id=1");
        assertThat(result).contains("name='John Doe'");
        assertThat(result).contains("email='john@example.com'");
        assertThat(result).contains("createdAt=2024-01-01T12:00");
        assertThat(result).contains("updatedAt=2024-01-02T12:00");
    }

    @Test
    void testToStringWithNullFields() {
        // Given
        User user = new User();

        // When
        String result = user.toString();

        // Then
        assertThat(result).contains("User{");
        assertThat(result).contains("id=null");
        assertThat(result).contains("name='null'");
        assertThat(result).contains("email='null'");
    }

    @Test
    void testToStringWithIdOnly() {
        // Given
        User user = new User();
        user.id = 99L;

        // When
        String result = user.toString();

        // Then
        assertThat(result).contains("id=99");
    }

    @Test
    void testFieldsArePublic() {
        // Given
        User user = new User();

        // When - direct field access should work (Panache pattern)
        user.name = "Direct Name";
        user.email = "direct@example.com";
        user.bio = "Direct Bio";
        user.createdAt = LocalDateTime.now();
        user.updatedAt = LocalDateTime.now();

        // Then
        assertThat(user.name).isEqualTo("Direct Name");
        assertThat(user.email).isEqualTo("direct@example.com");
        assertThat(user.bio).isEqualTo("Direct Bio");
        assertThat(user.createdAt).isNotNull();
        assertThat(user.updatedAt).isNotNull();
    }
}
