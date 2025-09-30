package br.com.arquivolivre.otelquarkus.resource;

import br.com.arquivolivre.otelquarkus.model.User;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserResourceTest {

    private static Long createdUserId;

    @Test
    @Order(1)
    void testGetAllUsers() {
        given()
            .when().get("/api/users")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", hasSize(greaterThanOrEqualTo(0)));
    }

    @Test
    @Order(2)
    void testCreateUser() {
        User user = new User("Test User", "test@example.com", "Test bio");

        Integer idInt = given()
            .contentType(ContentType.JSON)
            .body(user)
            .when().post("/api/users")
            .then()
                .statusCode(201)
                .body("name", equalTo("Test User"))
                .body("email", equalTo("test@example.com"))
                .body("id", notNullValue())
                .extract().path("id");
        createdUserId = idInt.longValue();
    }

    @Test
    @Order(3)
    void testCreateUserWithDuplicateEmail() {
        User user = new User("Another User", "test@example.com", "Another bio");

        given()
            .contentType(ContentType.JSON)
            .body(user)
            .when().post("/api/users")
            .then()
                .statusCode(400)
                .body("error", containsString("Email already exists"));
    }

    @Test
    @Order(4)
    void testGetUserById() {
        if (createdUserId == null) {
            createdUserId = 1L; // Default fallback
        }
        given()
            .pathParam("id", createdUserId)
            .when().get("/api/users/{id}")
            .then()
                .statusCode(200)
                .body("id", equalTo(createdUserId.intValue()))
                .body("email", equalTo("test@example.com"));
    }

    @Test
    @Order(5)
    void testGetUserByIdNotFound() {
        given()
            .pathParam("id", 99999)
            .when().get("/api/users/{id}")
            .then()
                .statusCode(404)
                .body("error", containsString("User not found"));
    }

    @Test
    @Order(6)
    void testGetUserByEmail() {
        given()
            .pathParam("email", "test@example.com")
            .when().get("/api/users/email/{email}")
            .then()
                .statusCode(200)
                .body("email", equalTo("test@example.com"));
    }

    @Test
    @Order(7)
    void testUpdateUser() {
        if (createdUserId == null) {
            createdUserId = 1L; // Default fallback
        }
        User updatedUser = new User("Updated User", "updated@example.com", "Updated bio");

        given()
            .pathParam("id", createdUserId)
            .contentType(ContentType.JSON)
            .body(updatedUser)
            .when().put("/api/users/{id}")
            .then()
                .statusCode(200)
                .body("name", equalTo("Updated User"))
                .body("email", equalTo("updated@example.com"));
    }

    @Test
    @Order(8)
    void testUpdateUserNotFound() {
        User user = new User("Non-existent", "nonexistent@example.com", "Bio");

        given()
            .pathParam("id", 99999)
            .contentType(ContentType.JSON)
            .body(user)
            .when().put("/api/users/{id}")
            .then()
                .statusCode(404)
                .body("error", containsString("User not found"));
    }

    @Test
    @Order(9)
    void testSearchUsers() {
        given()
            .queryParam("name", "Updated")
            .when().get("/api/users/search")
            .then()
                .statusCode(200)
                .body("$", hasSize(greaterThanOrEqualTo(1)));
    }

    @Test
    @Order(10)
    void testSearchUsersWithoutQuery() {
        given()
            .when().get("/api/users/search")
            .then()
                .statusCode(400)
                .body("error", containsString("required"));
    }

    @Test
    @Order(11)
    void testGetRecentUsers() {
        given()
            .queryParam("days", 30)
            .when().get("/api/users/recent")
            .then()
                .statusCode(200)
                .body("$", hasSize(greaterThanOrEqualTo(0)));
    }

    @Test
    @Order(12)
    void testGetRecentUsersWithInvalidDays() {
        given()
            .queryParam("days", -1)
            .when().get("/api/users/recent")
            .then()
                .statusCode(400)
                .body("error", containsString("positive"));
    }

    @Test
    @Order(13)
    void testGetUserCount() {
        given()
            .when().get("/api/users/count")
            .then()
                .statusCode(200)
                .body("count", greaterThanOrEqualTo(1));
    }

    @Test
    @Order(14)
    void testHealthCheck() {
        given()
            .when().get("/api/users/health")
            .then()
                .statusCode(200)
                .body("status", equalTo("UP"))
                .body("service", equalTo("UserService"));
    }

    @Test
    @Order(15)
    void testDeleteUser() {
        if (createdUserId == null) {
            createdUserId = 1L; // Default fallback
        }
        given()
            .pathParam("id", createdUserId)
            .when().delete("/api/users/{id}")
            .then()
                .statusCode(204);
    }

    @Test
    @Order(16)
    void testDeleteUserNotFound() {
        given()
            .pathParam("id", 99999)
            .when().delete("/api/users/{id}")
            .then()
                .statusCode(404);
    }

    @Test
    @Order(17)
    void testCreateUserWithInvalidEmail() {
        User user = new User("Invalid User", "invalid-email", "Bio");

        given()
            .contentType(ContentType.JSON)
            .body(user)
            .when().post("/api/users")
            .then()
                .statusCode(400);
    }

    @Test
    @Order(18)
    void testCreateUserWithEmptyName() {
        User user = new User("", "empty@example.com", "Bio");

        given()
            .contentType(ContentType.JSON)
            .body(user)
            .when().post("/api/users")
            .then()
                .statusCode(400);
    }
}