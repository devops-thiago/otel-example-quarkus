package br.com.arquivolivre.otelquarkus.repository;

import br.com.arquivolivre.otelquarkus.model.User;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserRepositoryTest {

    @Inject
    UserRepository userRepository;

    @BeforeEach
    @Transactional
    void setUp() {
        // Clean database before each test
        userRepository.deleteAll();
    }

    @Test
    @Order(1)
    @Transactional
    void testPersistUser() {
        // Given
        User user = new User("Test User", "test@example.com", "Test bio");

        // When
        userRepository.persist(user);

        // Then
        assertThat(user.id).isNotNull();
        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    @Order(2)
    @Transactional
    void testFindByEmail() {
        // Given
        User user = new User("John Doe", "john@example.com", "Bio");
        userRepository.persist(user);

        // When
        Optional<User> found = userRepository.findByEmail("john@example.com");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().name).isEqualTo("John Doe");
        assertThat(found.get().email).isEqualTo("john@example.com");
    }

    @Test
    @Order(3)
    @Transactional
    void testFindByEmailNotFound() {
        // When
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @Order(4)
    @Transactional
    void testSearchByName() {
        // Given
        userRepository.persist(new User("John Doe", "john@example.com", "Bio1"));
        userRepository.persist(new User("Jane Doe", "jane@example.com", "Bio2"));
        userRepository.persist(new User("Bob Smith", "bob@example.com", "Bio3"));

        // When
        List<User> results = userRepository.searchByName("Doe");

        // Then
        assertThat(results).hasSize(2);
        assertThat(results).extracting("name").contains("John Doe", "Jane Doe");
    }

    @Test
    @Order(5)
    @Transactional
    void testSearchByNameCaseInsensitive() {
        // Given
        userRepository.persist(new User("John Doe", "john@example.com", "Bio"));

        // When
        List<User> results = userRepository.searchByName("john");

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).name).isEqualTo("John Doe");
    }

    @Test
    @Order(6)
    @Transactional
    void testSearchByNameNoResults() {
        // Given
        userRepository.persist(new User("John Doe", "john@example.com", "Bio"));

        // When
        List<User> results = userRepository.searchByName("NonExistent");

        // Then
        assertThat(results).isEmpty();
    }

    @Test
    @Order(7)
    @Transactional
    void testFindRecentUsers() {
        // Given - Test finding users created in the last 1 day
        User recentUser = new User("Recent User", "recent@example.com", "Recent bio");
        userRepository.persist(recentUser);
        
        // Flush to ensure persistence and wait a tiny bit
        userRepository.flush();

        // When - search for users from last 1 day
        List<User> recentUsers = userRepository.findRecentUsers(1);

        // Then - should find the recently created user
        assertThat(recentUsers).hasSizeGreaterThanOrEqualTo(1);
        assertThat(recentUsers).anyMatch(u -> u.name.equals("Recent User"));
    }

    @Test
    @Order(8)
    @Transactional
    void testExistsByEmail() {
        // Given
        userRepository.persist(new User("John Doe", "john@example.com", "Bio"));

        // When
        boolean exists = userRepository.existsByEmail("john@example.com");
        boolean notExists = userRepository.existsByEmail("nonexistent@example.com");

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @Order(9)
    @Transactional
    void testExistsByEmailAndIdNot() {
        // Given
        User user1 = new User("John Doe", "john@example.com", "Bio1");
        userRepository.persist(user1);
        User user2 = new User("Jane Doe", "jane@example.com", "Bio2");
        userRepository.persist(user2);

        // When
        boolean exists = userRepository.existsByEmailAndIdNot("john@example.com", user2.id);
        boolean notExists = userRepository.existsByEmailAndIdNot("john@example.com", user1.id);

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @Order(10)
    @Transactional
    void testCountUsers() {
        // Given
        userRepository.persist(new User("User1", "user1@example.com", "Bio1"));
        userRepository.persist(new User("User2", "user2@example.com", "Bio2"));
        userRepository.persist(new User("User3", "user3@example.com", "Bio3"));

        // When
        long count = userRepository.countUsers();

        // Then
        assertThat(count).isEqualTo(3);
    }

    @Test
    @Order(11)
    @Transactional
    void testDeleteUser() {
        // Given
        User user = new User("John Doe", "john@example.com", "Bio");
        userRepository.persist(user);
        Long userId = user.id;

        // When
        boolean deleted = userRepository.deleteUser(userId);

        // Then
        assertThat(deleted).isTrue();
        assertThat(userRepository.findByIdOptional(userId)).isEmpty();
    }

    @Test
    @Order(12)
    @Transactional
    void testDeleteUserNotFound() {
        // When
        boolean deleted = userRepository.deleteUser(999L);

        // Then
        assertThat(deleted).isFalse();
    }

    @Test
    @Order(13)
    @Transactional
    void testListAll() {
        // Given
        userRepository.persist(new User("User1", "user1@example.com", "Bio1"));
        userRepository.persist(new User("User2", "user2@example.com", "Bio2"));

        // When
        List<User> users = userRepository.listAll();

        // Then
        assertThat(users).hasSize(2);
    }

    @Test
    @Order(14)
    @Transactional
    void testFindByIdOptional() {
        // Given
        User user = new User("John Doe", "john@example.com", "Bio");
        userRepository.persist(user);

        // When
        Optional<User> found = userRepository.findByIdOptional(user.id);

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().id).isEqualTo(user.id);
    }
}