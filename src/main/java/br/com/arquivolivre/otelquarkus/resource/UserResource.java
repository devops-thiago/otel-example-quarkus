package br.com.arquivolivre.otelquarkus.resource;

import br.com.arquivolivre.otelquarkus.model.User;
import br.com.arquivolivre.otelquarkus.service.UserService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST endpoint for User CRUD operations.
 * Uses Quarkus REST (RESTEasy Reactive) with JAX-RS annotations.
 * This is the recommended approach for Quarkus 3.x REST APIs.
 */
@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "User Management", description = "CRUD operations for users")
public class UserResource {

    private static final Logger LOG = Logger.getLogger(UserResource.class);

    @Inject
    UserService userService;

    @GET
    @Operation(summary = "Get all users", description = "Retrieve a list of all users")
    @APIResponse(responseCode = "200", description = "Success",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = User.class)))
    public Response getAllUsers() {
        LOG.info("GET /api/users - Fetching all users");
        List<User> users = userService.getAllUsers();
        return Response.ok(users).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieve a specific user by their ID")
    @APIResponse(responseCode = "200", description = "User found",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = User.class)))
    @APIResponse(responseCode = "404", description = "User not found")
    public Response getUserById(@Parameter(description = "User ID", required = true) @PathParam("id") Long id) {
        LOG.infof("GET /api/users/%d - Fetching user by id", id);
        return userService.getUserById(id)
                .map(user -> Response.ok(user).build())
                .orElse(Response.status(Response.Status.NOT_FOUND)
                        .entity(createErrorResponse("User not found with id: " + id))
                        .build());
    }

    @GET
    @Path("/email/{email}")
    @Operation(summary = "Get user by email", description = "Retrieve a specific user by their email address")
    @APIResponse(responseCode = "200", description = "User found",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = User.class)))
    @APIResponse(responseCode = "404", description = "User not found")
    public Response getUserByEmail(@Parameter(description = "User email", required = true) @PathParam("email") String email) {
        LOG.infof("GET /api/users/email/%s - Fetching user by email", email);
        return userService.getUserByEmail(email)
                .map(user -> Response.ok(user).build())
                .orElse(Response.status(Response.Status.NOT_FOUND)
                        .entity(createErrorResponse("User not found with email: " + email))
                        .build());
    }

    @POST
    @Operation(summary = "Create user", description = "Create a new user")
    @APIResponse(responseCode = "201", description = "User created",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = User.class)))
    @APIResponse(responseCode = "400", description = "Invalid input or email already exists")
    public Response createUser(@Valid User user) {
        LOG.infof("POST /api/users - Creating user with email: %s", user.email);
        try {
            User createdUser = userService.createUser(user);
            return Response.status(Response.Status.CREATED).entity(createdUser).build();
        } catch (IllegalArgumentException e) {
            LOG.error("Error creating user", e);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(createErrorResponse(e.getMessage()))
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Update user", description = "Update an existing user")
    @APIResponse(responseCode = "200", description = "User updated",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = User.class)))
    @APIResponse(responseCode = "400", description = "Invalid input or email conflict")
    @APIResponse(responseCode = "404", description = "User not found")
    public Response updateUser(@Parameter(description = "User ID", required = true) @PathParam("id") Long id,
                                @Valid User user) {
        LOG.infof("PUT /api/users/%d - Updating user", id);
        try {
            User updatedUser = userService.updateUser(id, user);
            return Response.ok(updatedUser).build();
        } catch (IllegalArgumentException e) {
            LOG.error("Error updating user", e);
            Response.Status status = e.getMessage().contains("not found") ?
                    Response.Status.NOT_FOUND : Response.Status.BAD_REQUEST;
            return Response.status(status)
                    .entity(createErrorResponse(e.getMessage()))
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete user", description = "Delete a user by ID")
    @APIResponse(responseCode = "204", description = "User deleted")
    @APIResponse(responseCode = "404", description = "User not found")
    public Response deleteUser(@Parameter(description = "User ID", required = true) @PathParam("id") Long id) {
        LOG.infof("DELETE /api/users/%d - Deleting user", id);
        boolean deleted = userService.deleteUser(id);
        if (deleted) {
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(createErrorResponse("User not found with id: " + id))
                    .build();
        }
    }

    @GET
    @Path("/search")
    @Operation(summary = "Search users", description = "Search users by name (partial match)")
    @APIResponse(responseCode = "200", description = "Success",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = User.class)))
    public Response searchUsers(@Parameter(description = "Search query", required = true) @QueryParam("name") String name) {
        LOG.infof("GET /api/users/search?name=%s - Searching users", name);
        if (name == null || name.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(createErrorResponse("Search query 'name' is required"))
                    .build();
        }
        List<User> users = userService.searchUsers(name);
        return Response.ok(users).build();
    }

    @GET
    @Path("/recent")
    @Operation(summary = "Get recent users", description = "Get users created within the specified number of days")
    @APIResponse(responseCode = "200", description = "Success",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = User.class)))
    public Response getRecentUsers(@Parameter(description = "Number of days", required = false) @QueryParam("days") @DefaultValue("7") int days) {
        LOG.infof("GET /api/users/recent?days=%d - Fetching recent users", days);
        if (days <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(createErrorResponse("Days must be a positive number"))
                    .build();
        }
        List<User> users = userService.getRecentUsers(days);
        return Response.ok(users).build();
    }

    @GET
    @Path("/count")
    @Operation(summary = "Get user count", description = "Get the total number of users")
    @APIResponse(responseCode = "200", description = "Success")
    public Response getUserCount() {
        LOG.info("GET /api/users/count - Fetching user count");
        long count = userService.getUserCount();
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return Response.ok(response).build();
    }

    @GET
    @Path("/health")
    @Operation(summary = "Health check", description = "Check if the user service is healthy")
    @APIResponse(responseCode = "200", description = "Service is healthy")
    public Response healthCheck() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "UserService");
        health.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return Response.ok(health).build();
    }

    /**
     * Helper method to create error response
     */
    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        error.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return error;
    }
}