package br.com.arquivolivre.otelquarkus.resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import br.com.arquivolivre.otelquarkus.model.User;
import br.com.arquivolivre.otelquarkus.service.UserService;
import jakarta.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Pure unit tests for UserResource without Quarkus test framework. This ensures JaCoCo properly
 * captures code coverage.
 */
@ExtendWith(MockitoExtension.class)
class UserResourceUnitTest {

    @Mock private UserService userService;

    @InjectMocks private UserResource userResource;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("John Doe", "john@example.com", "Software Developer");
        testUser.id = 1L;
    }

    @Test
    void testGetAllUsersSuccess() {
        // Given
        User user2 = new User("Jane Doe", "jane@example.com", "Designer");
        user2.id = 2L;
        List<User> users = Arrays.asList(testUser, user2);
        when(userService.getAllUsers()).thenReturn(users);

        // When
        Response response = userResource.getAllUsers();

        // Then
        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
        @SuppressWarnings("unchecked")
        List<User> result = (List<User>) response.getEntity();
        assertThat(result).hasSize(2);
        verify(userService).getAllUsers();
    }

    @Test
    void testGetAllUsersEmptyList() {
        // Given
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        // When
        Response response = userResource.getAllUsers();

        // Then
        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
        @SuppressWarnings("unchecked")
        List<User> result = (List<User>) response.getEntity();
        assertThat(result).isEmpty();
        verify(userService).getAllUsers();
    }

    @Test
    void testGetUserByIdFound() {
        // Given
        when(userService.getUserById(1L)).thenReturn(Optional.of(testUser));

        // When
        Response response = userResource.getUserById(1L);

        // Then
        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
        User result = (User) response.getEntity();
        assertThat(result.id).isEqualTo(1L);
        assertThat(result.email).isEqualTo("john@example.com");
        verify(userService).getUserById(1L);
    }

    @Test
    void testGetUserByIdNotFound() {
        // Given
        when(userService.getUserById(999L)).thenReturn(Optional.empty());

        // When
        Response response = userResource.getUserById(999L);

        // Then
        assertThat(response.getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, String> error = (Map<String, String>) response.getEntity();
        assertThat(error).containsEntry("error", "User not found with id: 999");
        assertThat(error).containsKey("timestamp");
        verify(userService).getUserById(999L);
    }

    @Test
    void testGetUserByEmailFound() {
        // Given
        when(userService.getUserByEmail("john@example.com")).thenReturn(Optional.of(testUser));

        // When
        Response response = userResource.getUserByEmail("john@example.com");

        // Then
        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
        User result = (User) response.getEntity();
        assertThat(result.email).isEqualTo("john@example.com");
        verify(userService).getUserByEmail("john@example.com");
    }

    @Test
    void testGetUserByEmailNotFound() {
        // Given
        when(userService.getUserByEmail("notfound@example.com")).thenReturn(Optional.empty());

        // When
        Response response = userResource.getUserByEmail("notfound@example.com");

        // Then
        assertThat(response.getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, String> error = (Map<String, String>) response.getEntity();
        assertThat(error).containsEntry("error", "User not found with email: notfound@example.com");
        verify(userService).getUserByEmail("notfound@example.com");
    }

    @Test
    void testCreateUserSuccess() {
        // Given
        User newUser = new User("Alice", "alice@example.com", "Manager");
        User createdUser = new User("Alice", "alice@example.com", "Manager");
        createdUser.id = 5L;
        when(userService.createUser(any(User.class))).thenReturn(createdUser);

        // When
        Response response = userResource.createUser(newUser);

        // Then
        assertThat(response.getStatus()).isEqualTo(Response.Status.CREATED.getStatusCode());
        User result = (User) response.getEntity();
        assertThat(result.id).isEqualTo(5L);
        assertThat(result.email).isEqualTo("alice@example.com");
        verify(userService).createUser(newUser);
    }

    @Test
    void testCreateUserEmailAlreadyExists() {
        // Given
        User newUser = new User("Duplicate", "john@example.com", "Bio");
        when(userService.createUser(any(User.class)))
                .thenThrow(new IllegalArgumentException("Email already exists: john@example.com"));

        // When
        Response response = userResource.createUser(newUser);

        // Then
        assertThat(response.getStatus()).isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, String> error = (Map<String, String>) response.getEntity();
        assertThat(error).containsEntry("error", "Email already exists: john@example.com");
        verify(userService).createUser(newUser);
    }

    @Test
    void testUpdateUserSuccess() {
        // Given
        User updatedData = new User("John Updated", "john.updated@example.com", "Senior Dev");
        User updatedUser = new User("John Updated", "john.updated@example.com", "Senior Dev");
        updatedUser.id = 1L;
        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(updatedUser);

        // When
        Response response = userResource.updateUser(1L, updatedData);

        // Then
        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
        User result = (User) response.getEntity();
        assertThat(result.name).isEqualTo("John Updated");
        verify(userService).updateUser(1L, updatedData);
    }

    @Test
    void testUpdateUserNotFound() {
        // Given
        User updatedData = new User("Test", "test@example.com", "Bio");
        when(userService.updateUser(eq(999L), any(User.class)))
                .thenThrow(new IllegalArgumentException("User not found with id: 999"));

        // When
        Response response = userResource.updateUser(999L, updatedData);

        // Then
        assertThat(response.getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, String> error = (Map<String, String>) response.getEntity();
        assertThat(error).containsEntry("error", "User not found with id: 999");
        verify(userService).updateUser(999L, updatedData);
    }

    @Test
    void testUpdateUserEmailConflict() {
        // Given
        User updatedData = new User("John", "taken@example.com", "Bio");
        when(userService.updateUser(eq(1L), any(User.class)))
                .thenThrow(new IllegalArgumentException("Email already exists: taken@example.com"));

        // When
        Response response = userResource.updateUser(1L, updatedData);

        // Then
        assertThat(response.getStatus()).isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, String> error = (Map<String, String>) response.getEntity();
        assertThat(error).containsEntry("error", "Email already exists: taken@example.com");
        verify(userService).updateUser(1L, updatedData);
    }

    @Test
    void testDeleteUserSuccess() {
        // Given
        when(userService.deleteUser(1L)).thenReturn(true);

        // When
        Response response = userResource.deleteUser(1L);

        // Then
        assertThat(response.getStatus()).isEqualTo(Response.Status.NO_CONTENT.getStatusCode());
        assertThat(response.getEntity()).isNull();
        verify(userService).deleteUser(1L);
    }

    @Test
    void testDeleteUserNotFound() {
        // Given
        when(userService.deleteUser(999L)).thenReturn(false);

        // When
        Response response = userResource.deleteUser(999L);

        // Then
        assertThat(response.getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, String> error = (Map<String, String>) response.getEntity();
        assertThat(error).containsEntry("error", "User not found with id: 999");
        verify(userService).deleteUser(999L);
    }

    @Test
    void testSearchUsersWithResults() {
        // Given
        User user2 = new User("Johnny", "johnny@example.com", "Bio");
        user2.id = 2L;
        List<User> users = Arrays.asList(testUser, user2);
        when(userService.searchUsers("John")).thenReturn(users);

        // When
        Response response = userResource.searchUsers("John");

        // Then
        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
        @SuppressWarnings("unchecked")
        List<User> result = (List<User>) response.getEntity();
        assertThat(result).hasSize(2);
        verify(userService).searchUsers("John");
    }

    @Test
    void testSearchUsersEmptyResults() {
        // Given
        when(userService.searchUsers("Nonexistent")).thenReturn(Collections.emptyList());

        // When
        Response response = userResource.searchUsers("Nonexistent");

        // Then
        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
        @SuppressWarnings("unchecked")
        List<User> result = (List<User>) response.getEntity();
        assertThat(result).isEmpty();
        verify(userService).searchUsers("Nonexistent");
    }

    @Test
    void testSearchUsersNullQuery() {
        // When
        Response response = userResource.searchUsers(null);

        // Then
        assertThat(response.getStatus()).isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, String> error = (Map<String, String>) response.getEntity();
        assertThat(error).containsEntry("error", "Search query 'name' is required");
        verify(userService, never()).searchUsers(anyString());
    }

    @Test
    void testSearchUsersEmptyQuery() {
        // When
        Response response = userResource.searchUsers("   ");

        // Then
        assertThat(response.getStatus()).isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, String> error = (Map<String, String>) response.getEntity();
        assertThat(error).containsEntry("error", "Search query 'name' is required");
        verify(userService, never()).searchUsers(anyString());
    }

    @Test
    void testGetRecentUsersWithDefaultDays() {
        // Given
        List<User> users = Arrays.asList(testUser);
        when(userService.getRecentUsers(7)).thenReturn(users);

        // When
        Response response = userResource.getRecentUsers(7);

        // Then
        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
        @SuppressWarnings("unchecked")
        List<User> result = (List<User>) response.getEntity();
        assertThat(result).hasSize(1);
        verify(userService).getRecentUsers(7);
    }

    @Test
    void testGetRecentUsersCustomDays() {
        // Given
        List<User> users = Arrays.asList(testUser);
        when(userService.getRecentUsers(30)).thenReturn(users);

        // When
        Response response = userResource.getRecentUsers(30);

        // Then
        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
        @SuppressWarnings("unchecked")
        List<User> result = (List<User>) response.getEntity();
        assertThat(result).hasSize(1);
        verify(userService).getRecentUsers(30);
    }

    @Test
    void testGetRecentUsersInvalidDays() {
        // When
        Response response = userResource.getRecentUsers(-1);

        // Then
        assertThat(response.getStatus()).isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, String> error = (Map<String, String>) response.getEntity();
        assertThat(error).containsEntry("error", "Days must be a positive number");
        verify(userService, never()).getRecentUsers(anyInt());
    }

    @Test
    void testGetRecentUsersZeroDays() {
        // When
        Response response = userResource.getRecentUsers(0);

        // Then
        assertThat(response.getStatus()).isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, String> error = (Map<String, String>) response.getEntity();
        assertThat(error).containsEntry("error", "Days must be a positive number");
        verify(userService, never()).getRecentUsers(anyInt());
    }

    @Test
    void testGetUserCountSuccess() {
        // Given
        when(userService.getUserCount()).thenReturn(42L);

        // When
        Response response = userResource.getUserCount();

        // Then
        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Long> result = (Map<String, Long>) response.getEntity();
        assertThat(result).containsEntry("count", 42L);
        verify(userService).getUserCount();
    }

    @Test
    void testGetUserCountZero() {
        // Given
        when(userService.getUserCount()).thenReturn(0L);

        // When
        Response response = userResource.getUserCount();

        // Then
        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Long> result = (Map<String, Long>) response.getEntity();
        assertThat(result).containsEntry("count", 0L);
        verify(userService).getUserCount();
    }

    @Test
    void testHealthCheckSuccess() {
        // When
        Response response = userResource.healthCheck();

        // Then
        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, String> health = (Map<String, String>) response.getEntity();
        assertThat(health).containsEntry("status", "UP");
        assertThat(health).containsEntry("service", "UserService");
        assertThat(health).containsKey("timestamp");
    }
}
