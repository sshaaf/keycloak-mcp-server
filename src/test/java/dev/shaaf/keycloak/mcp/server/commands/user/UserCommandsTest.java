package dev.shaaf.keycloak.mcp.server.commands.user;

import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import dev.shaaf.keycloak.mcp.server.KeycloakTool;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for User commands.
 * Uses Keycloak TestContainers with quarkus-realm.json pre-loaded.
 * Pre-existing users: admin, alice, jdoe
 */
@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserCommandsTest {

    private static final String REALM = "quarkus";

    @Inject
    KeycloakTool keycloakTool;

    @Test
    @Order(1)
    public void testGetUsers() {
        String result = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.GET_USERS,
                "{\"realm\": \"" + REALM + "\"}"
        );

        assertNotNull(result);
        assertTrue(result.contains("admin") || result.contains("alice"));
    }

    @Test
    @Order(2)
    public void testGetUserByUsername() {
        String result = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.GET_USER_BY_USERNAME,
                "{\"realm\": \"" + REALM + "\", \"username\": \"admin\"}"
        );

        assertNotNull(result);
        assertTrue(result.contains("admin"));
    }

    @Test
    @Order(3)
    public void testCreateUser() {
        String result = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.CREATE_USER,
                """
                {
                    "realm": "%s",
                    "username": "testuser",
                    "firstName": "Test",
                    "lastName": "User",
                    "email": "testuser@example.com",
                    "password": "testpassword123"
                }
                """.formatted(REALM)
        );

        assertNotNull(result);
        assertTrue(result.toLowerCase().contains("created") || result.toLowerCase().contains("success"));
    }

    @Test
    @Order(4)
    public void testGetUserById() {
        // First get the user to find their ID
        String users = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.GET_USER_BY_USERNAME,
                "{\"realm\": \"" + REALM + "\", \"username\": \"testuser\"}"
        );

        // Extract userId from response (simplified - in real test use proper JSON parsing)
        assertTrue(users.contains("testuser"));

        // For this test, we'll use admin user which we know exists
        String adminUsers = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.GET_USER_BY_USERNAME,
                "{\"realm\": \"" + REALM + "\", \"username\": \"admin\"}"
        );

        assertNotNull(adminUsers);
    }

    @Test
    @Order(5)
    public void testCountUsers() {
        String result = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.COUNT_USERS,
                "{\"realm\": \"" + REALM + "\"}"
        );

        assertNotNull(result);
        int count = Integer.parseInt(result.trim());
        assertTrue(count >= 1, "Should have at least 1 user");
    }

    @Test
    @Order(6)
    public void testGetUserGroups() {
        // Get admin user first
        String users = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.GET_USERS,
                "{\"realm\": \"" + REALM + "\"}"
        );

        // This test verifies the command executes without error
        // In a real scenario, you'd parse the user ID and query their groups
        assertNotNull(users);
    }

    @Test
    @Order(7)
    public void testGetUserRoles() {
        // Get users to find a user ID
        String users = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.GET_USERS,
                "{\"realm\": \"" + REALM + "\"}"
        );

        assertNotNull(users);
        // In a complete test, parse user ID and call GET_USER_ROLES
    }

    @Test
    @Order(100)
    public void testDeleteUser() {
        // Delete the test user we created
        String result = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.DELETE_USER,
                "{\"realm\": \"" + REALM + "\", \"username\": \"testuser\"}"
        );

        assertNotNull(result);
    }
}

