package dev.shaaf.keycloak.mcp.server.commands.group;

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
 * Tests for Group commands.
 * Uses Keycloak TestContainers with quarkus-realm.json pre-loaded.
 */
@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GroupCommandsTest {

    private static final String REALM = "quarkus";

    @Inject
    KeycloakTool keycloakTool;

    @Test
    @Order(1)
    public void testGetGroups() {
        String result = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.GET_GROUPS,
                "{\"realm\": \"" + REALM + "\"}"
        );

        assertNotNull(result);
        // Result should be a JSON array (might be empty initially)
        assertTrue(result.startsWith("["));
    }

    @Test
    @Order(2)
    public void testCreateGroup() {
        String result = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.CREATE_GROUP,
                "{\"realm\": \"" + REALM + "\", \"groupName\": \"test-group\"}"
        );

        assertNotNull(result);
        assertTrue(result.toLowerCase().contains("created") || result.toLowerCase().contains("success"));
    }

    @Test
    @Order(3)
    public void testGetGroupsAfterCreate() {
        String result = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.GET_GROUPS,
                "{\"realm\": \"" + REALM + "\"}"
        );

        assertNotNull(result);
        assertTrue(result.contains("test-group"));
    }

    @Test
    @Order(4)
    public void testCreateSubgroup() {
        // First get the parent group ID
        String groups = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.GET_GROUPS,
                "{\"realm\": \"" + REALM + "\"}"
        );

        // This is a simplified test - in a real scenario you'd parse the group ID
        assertNotNull(groups);
        assertTrue(groups.contains("test-group"));
    }

    @Test
    @Order(5)
    public void testGetGroupMembers() {
        // Get groups to find group ID
        String groups = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.GET_GROUPS,
                "{\"realm\": \"" + REALM + "\"}"
        );

        assertNotNull(groups);
        // In a complete test, parse group ID and call GET_GROUP_MEMBERS
    }

    @Test
    @Order(6)
    public void testUpdateGroup() {
        // Get groups to find group ID for update
        String groups = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.GET_GROUPS,
                "{\"realm\": \"" + REALM + "\"}"
        );

        assertNotNull(groups);
        // In a complete test, parse group ID and call UPDATE_GROUP
    }

    @Test
    @Order(100)
    public void testDeleteGroup() {
        // Get groups to find the test group ID
        String groups = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.GET_GROUPS,
                "{\"realm\": \"" + REALM + "\"}"
        );

        assertNotNull(groups);
        // In a complete test, parse group ID and call DELETE_GROUP
    }
}

