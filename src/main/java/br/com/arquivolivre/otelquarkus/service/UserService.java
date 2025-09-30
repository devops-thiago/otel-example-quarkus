package br.com.arquivolivre.otelquarkus.service;

import br.com.arquivolivre.otelquarkus.model.User;
import br.com.arquivolivre.otelquarkus.repository.UserRepository;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.instrumentation.annotations.SpanAttribute;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.jboss.logging.Logger;

/**
 * Service layer for User business logic. Includes OpenTelemetry instrumentation for distributed
 * tracing.
 */
@ApplicationScoped
public class UserService {

    private static final Logger LOG = Logger.getLogger(UserService.class);

    @Inject UserRepository userRepository;

    /**
     * Get all users
     *
     * @return List of all users
     */
    @WithSpan("UserService.getAllUsers")
    public List<User> getAllUsers() {
        LOG.info("Fetching all users");
        Span span = Span.current();

        List<User> users = userRepository.listAll();
        span.setAttribute("user.count", users.size());

        LOG.infof("Retrieved %d users", users.size());
        return users;
    }

    /**
     * Get user by ID
     *
     * @param id User ID
     * @return Optional containing user if found
     */
    @WithSpan("UserService.getUserById")
    public Optional<User> getUserById(@SpanAttribute("user.id") Long id) {
        LOG.infof("Fetching user with id: %d", id);
        Span span = Span.current();

        Optional<User> user = userRepository.findByIdOptional(id);
        span.setAttribute("user.found", user.isPresent());

        if (user.isPresent()) {
            LOG.infof("Found user: %s", user.get().email);
        } else {
            LOG.warnf("User not found with id: %d", id);
        }

        return user;
    }

    /**
     * Get user by email
     *
     * @param email User email
     * @return Optional containing user if found
     */
    @WithSpan("UserService.getUserByEmail")
    public Optional<User> getUserByEmail(@SpanAttribute("user.email") String email) {
        LOG.infof("Fetching user with email: %s", email);
        Span span = Span.current();

        Optional<User> user = userRepository.findByEmail(email);
        span.setAttribute("user.found", user.isPresent());

        if (user.isPresent()) {
            LOG.infof("Found user with email: %s", email);
        } else {
            LOG.warnf("User not found with email: %s", email);
        }

        return user;
    }

    /**
     * Create a new user
     *
     * @param user User to create
     * @return Created user
     * @throws IllegalArgumentException if email already exists
     */
    @Transactional
    @WithSpan("UserService.createUser")
    public User createUser(@SpanAttribute("user.email") User user) {
        LOG.infof("Creating new user with email: %s", user.email);
        Span span = Span.current();
        span.setAttribute("user.name", user.name);

        // Check if email already exists
        if (userRepository.existsByEmail(user.email)) {
            LOG.errorf("Email already exists: %s", user.email);
            span.setAttribute("error", true);
            span.setAttribute("error.type", "duplicate_email");
            throw new IllegalArgumentException("Email already exists: " + user.email);
        }

        userRepository.persist(user);
        if (user.id != null) {
            span.setAttribute("user.id", user.id);
        }
        span.setAttribute("user.created", true);

        LOG.infof("User created successfully with id: %d", user.id);
        return user;
    }

    /**
     * Update an existing user
     *
     * @param id User ID
     * @param updatedUser Updated user data
     * @return Updated user
     * @throws IllegalArgumentException if user not found or email conflict
     */
    @Transactional
    @WithSpan("UserService.updateUser")
    public User updateUser(@SpanAttribute("user.id") Long id, User updatedUser) {
        LOG.infof("Updating user with id: %d", id);
        Span span = Span.current();
        span.setAttribute("user.email", updatedUser.email);

        User existingUser =
                userRepository
                        .findByIdOptional(id)
                        .orElseThrow(
                                () -> {
                                    LOG.errorf("User not found with id: %d", id);
                                    span.setAttribute("error", true);
                                    span.setAttribute("error.type", "not_found");
                                    return new IllegalArgumentException(
                                            "User not found with id: " + id);
                                });

        // Check if email is being changed and if new email already exists
        if (!existingUser.email.equals(updatedUser.email)
                && userRepository.existsByEmailAndIdNot(updatedUser.email, id)) {
            LOG.errorf("Email already exists: %s", updatedUser.email);
            span.setAttribute("error", true);
            span.setAttribute("error.type", "duplicate_email");
            throw new IllegalArgumentException("Email already exists: " + updatedUser.email);
        }

        // Update fields
        existingUser.name = updatedUser.name;
        existingUser.email = updatedUser.email;
        existingUser.bio = updatedUser.bio;

        userRepository.persist(existingUser);
        span.setAttribute("user.updated", true);

        LOG.infof("User updated successfully with id: %d", id);
        return existingUser;
    }

    /**
     * Delete a user
     *
     * @param id User ID
     * @return true if deleted, false if not found
     */
    @Transactional
    @WithSpan("UserService.deleteUser")
    public boolean deleteUser(@SpanAttribute("user.id") Long id) {
        LOG.infof("Deleting user with id: %d", id);
        Span span = Span.current();

        boolean deleted = userRepository.deleteUser(id);
        span.setAttribute("user.deleted", deleted);

        if (deleted) {
            LOG.infof("User deleted successfully with id: %d", id);
        } else {
            LOG.warnf("User not found for deletion with id: %d", id);
            span.setAttribute("error.type", "not_found");
        }

        return deleted;
    }

    /**
     * Search users by name
     *
     * @param name Name to search for
     * @return List of matching users
     */
    @WithSpan("UserService.searchUsers")
    public List<User> searchUsers(@SpanAttribute("search.query") String name) {
        LOG.infof("Searching users with name: %s", name);
        Span span = Span.current();

        List<User> users = userRepository.searchByName(name);
        span.setAttribute("search.results", users.size());

        LOG.infof("Found %d users matching name: %s", users.size(), name);
        return users;
    }

    /**
     * Get recent users
     *
     * @param days Number of days to look back
     * @return List of recent users
     */
    @WithSpan("UserService.getRecentUsers")
    public List<User> getRecentUsers(@SpanAttribute("days") int days) {
        LOG.infof("Fetching users from last %d days", days);
        Span span = Span.current();

        List<User> users = userRepository.findRecentUsers(days);
        span.setAttribute("user.count", users.size());

        LOG.infof("Found %d users from last %d days", users.size(), days);
        return users;
    }

    /**
     * Get total user count
     *
     * @return Total number of users
     */
    @WithSpan("UserService.getUserCount")
    public long getUserCount() {
        LOG.info("Fetching user count");

        long count = userRepository.countUsers();
        Span.current().setAttribute("user.count", count);

        LOG.infof("Total user count: %d", count);
        return count;
    }
}
