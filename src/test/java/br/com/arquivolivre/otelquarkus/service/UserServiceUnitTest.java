package br.com.arquivolivre.otelquarkus.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import br.com.arquivolivre.otelquarkus.model.User;
import br.com.arquivolivre.otelquarkus.repository.UserRepository;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Pure unit tests for UserService without Quarkus test framework. This ensures JaCoCo properly
 * captures code coverage.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {

    @Mock private UserRepository userRepository;

    @InjectMocks private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("John Doe", "john@example.com", "Software Developer");
        testUser.id = 1L;
    }

    @Test
    void testGetAllUsersEmptyList() {
        // Given
        when(userRepository.listAll()).thenReturn(Collections.emptyList());

        // When
        List<User> result = userService.getAllUsers();

        // Then
        assertThat(result).isEmpty();
        verify(userRepository).listAll();
    }

    @Test
    void testGetAllUsersMultipleUsers() {
        // Given
        User user2 = new User("Jane Doe", "jane@example.com", "Designer");
        user2.id = 2L;
        List<User> users = Arrays.asList(testUser, user2);
        when(userRepository.listAll()).thenReturn(users);

        // When
        List<User> result = userService.getAllUsers();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(testUser, user2);
        verify(userRepository).listAll();
    }

    @Test
    void testGetUserByIdFound() {
        // Given
        when(userRepository.findByIdOptional(1L)).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userService.getUserById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().id).isEqualTo(1L);
        assertThat(result.get().email).isEqualTo("john@example.com");
        verify(userRepository).findByIdOptional(1L);
    }

    @Test
    void testGetUserByIdNotFound() {
        // Given
        when(userRepository.findByIdOptional(999L)).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.getUserById(999L);

        // Then
        assertThat(result).isEmpty();
        verify(userRepository).findByIdOptional(999L);
    }

    @Test
    void testGetUserByEmailFound() {
        // Given
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userService.getUserByEmail("john@example.com");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().email).isEqualTo("john@example.com");
        assertThat(result.get().name).isEqualTo("John Doe");
        verify(userRepository).findByEmail("john@example.com");
    }

    @Test
    void testGetUserByEmailNotFound() {
        // Given
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.getUserByEmail("notfound@example.com");

        // Then
        assertThat(result).isEmpty();
        verify(userRepository).findByEmail("notfound@example.com");
    }

    @Test
    void testCreateUserSuccess() {
        // Given
        User newUser = new User("Alice Smith", "alice@example.com", "Manager");
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
        doAnswer(
                        invocation -> {
                            newUser.id = 5L;
                            return null;
                        })
                .when(userRepository)
                .persist(any(User.class));

        // When
        User result = userService.createUser(newUser);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id).isEqualTo(5L);
        assertThat(result.name).isEqualTo("Alice Smith");
        verify(userRepository).existsByEmail("alice@example.com");
        verify(userRepository).persist(newUser);
    }

    @Test
    void testCreateUserEmailAlreadyExists() {
        // Given
        User newUser = new User("Duplicate", "john@example.com", "Bio");
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> userService.createUser(newUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email already exists: john@example.com");

        verify(userRepository).existsByEmail("john@example.com");
        verify(userRepository, never()).persist(any(User.class));
    }

    @Test
    void testCreateUserWithNullId() {
        // Given
        User newUser = new User("Test", "test@example.com", "Bio");
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        doNothing().when(userRepository).persist(any(User.class));

        // When
        User result = userService.createUser(newUser);

        // Then
        assertThat(result).isNotNull();
        verify(userRepository).persist(newUser);
    }

    @Test
    void testUpdateUserSuccess() {
        // Given
        User updatedData = new User("John Updated", "john.updated@example.com", "Senior Developer");
        when(userRepository.findByIdOptional(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmailAndIdNot("john.updated@example.com", 1L))
                .thenReturn(false);
        doNothing().when(userRepository).persist(any(User.class));

        // When
        User result = userService.updateUser(1L, updatedData);

        // Then
        assertThat(result.name).isEqualTo("John Updated");
        assertThat(result.email).isEqualTo("john.updated@example.com");
        assertThat(result.bio).isEqualTo("Senior Developer");
        verify(userRepository).findByIdOptional(1L);
        verify(userRepository).existsByEmailAndIdNot("john.updated@example.com", 1L);
        verify(userRepository).persist(testUser);
    }

    @Test
    void testUpdateUserNotFound() {
        // Given
        User updatedData = new User("Test", "test@example.com", "Bio");
        when(userRepository.findByIdOptional(999L)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> userService.updateUser(999L, updatedData))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found with id: 999");

        verify(userRepository).findByIdOptional(999L);
        verify(userRepository, never()).persist(any(User.class));
    }

    @Test
    void testUpdateUserEmailConflict() {
        // Given
        User updatedData = new User("John", "taken@example.com", "Bio");
        when(userRepository.findByIdOptional(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmailAndIdNot("taken@example.com", 1L)).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> userService.updateUser(1L, updatedData))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email already exists: taken@example.com");

        verify(userRepository).findByIdOptional(1L);
        verify(userRepository).existsByEmailAndIdNot("taken@example.com", 1L);
        verify(userRepository, never()).persist(any(User.class));
    }

    @Test
    void testUpdateUserSameEmail() {
        // Given
        User updatedData = new User("John Updated", "john@example.com", "New Bio");
        when(userRepository.findByIdOptional(1L)).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).persist(any(User.class));

        // When
        User result = userService.updateUser(1L, updatedData);

        // Then
        assertThat(result.name).isEqualTo("John Updated");
        assertThat(result.email).isEqualTo("john@example.com");
        assertThat(result.bio).isEqualTo("New Bio");
        verify(userRepository).findByIdOptional(1L);
        verify(userRepository, never()).existsByEmailAndIdNot(anyString(), anyLong());
        verify(userRepository).persist(testUser);
    }

    @Test
    void testDeleteUserSuccess() {
        // Given
        when(userRepository.deleteUser(1L)).thenReturn(true);

        // When
        boolean result = userService.deleteUser(1L);

        // Then
        assertThat(result).isTrue();
        verify(userRepository).deleteUser(1L);
    }

    @Test
    void testDeleteUserNotFound() {
        // Given
        when(userRepository.deleteUser(999L)).thenReturn(false);

        // When
        boolean result = userService.deleteUser(999L);

        // Then
        assertThat(result).isFalse();
        verify(userRepository).deleteUser(999L);
    }

    @Test
    void testSearchUsersWithResults() {
        // Given
        User user2 = new User("Johnny", "johnny@example.com", "Bio");
        user2.id = 2L;
        List<User> users = Arrays.asList(testUser, user2);
        when(userRepository.searchByName("John")).thenReturn(users);

        // When
        List<User> result = userService.searchUsers("John");

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(testUser, user2);
        verify(userRepository).searchByName("John");
    }

    @Test
    void testSearchUsersNoResults() {
        // Given
        when(userRepository.searchByName("Nonexistent")).thenReturn(Collections.emptyList());

        // When
        List<User> result = userService.searchUsers("Nonexistent");

        // Then
        assertThat(result).isEmpty();
        verify(userRepository).searchByName("Nonexistent");
    }

    @Test
    void testGetRecentUsersWithResults() {
        // Given
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findRecentUsers(7)).thenReturn(users);

        // When
        List<User> result = userService.getRecentUsers(7);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).contains(testUser);
        verify(userRepository).findRecentUsers(7);
    }

    @Test
    void testGetRecentUsersEmptyResults() {
        // Given
        when(userRepository.findRecentUsers(30)).thenReturn(Collections.emptyList());

        // When
        List<User> result = userService.getRecentUsers(30);

        // Then
        assertThat(result).isEmpty();
        verify(userRepository).findRecentUsers(30);
    }

    @Test
    void testGetRecentUsersDifferentDays() {
        // Given
        User user2 = new User("Recent User", "recent@example.com", "Bio");
        user2.id = 2L;
        List<User> users = Arrays.asList(testUser, user2);
        when(userRepository.findRecentUsers(1)).thenReturn(users);

        // When
        List<User> result = userService.getRecentUsers(1);

        // Then
        assertThat(result).hasSize(2);
        verify(userRepository).findRecentUsers(1);
    }

    @Test
    void testGetUserCountZero() {
        // Given
        when(userRepository.countUsers()).thenReturn(0L);

        // When
        long result = userService.getUserCount();

        // Then
        assertThat(result).isEqualTo(0L);
        verify(userRepository).countUsers();
    }

    @Test
    void testGetUserCountMultiple() {
        // Given
        when(userRepository.countUsers()).thenReturn(42L);

        // When
        long result = userService.getUserCount();

        // Then
        assertThat(result).isEqualTo(42L);
        verify(userRepository).countUsers();
    }

    @Test
    void testGetUserCountOne() {
        // Given
        when(userRepository.countUsers()).thenReturn(1L);

        // When
        long result = userService.getUserCount();

        // Then
        assertThat(result).isEqualTo(1L);
        verify(userRepository).countUsers();
    }
}
