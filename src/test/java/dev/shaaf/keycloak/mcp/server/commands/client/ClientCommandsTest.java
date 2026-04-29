package dev.shaaf.keycloak.mcp.server.commands.client;

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
 * Tests for Client commands.
 * Uses Keycloak TestContainers with quarkus-realm.json pre-loaded.
 * Pre-existing clients: backend-service, admin-cli, etc.
 */
@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ClientCommandsTest {

    private static final String REALM = "quarkus";
    private static final String TEST_CLIENT = "test-client";

    @Inject
    KeycloakTool keycloakTool;

    @Test
    @Order(1)
    public void testGetClients() {
        String result = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.GET_CLIENTS,
                "{\"realm\": \"" + REALM + "\"}"
        );

        assertNotNull(result);
        // Should contain pre-existing clients from quarkus-realm.json
        assertTrue(result.contains("backend-service") || result.contains("admin-cli"));
    }

    @Test
    @Order(2)
    public void testGetClient() {
        String result = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.GET_CLIENT,
                "{\"realm\": \"" + REALM + "\", \"clientId\": \"backend-service\"}"
        );

        assertNotNull(result);
        assertTrue(result.contains("backend-service"));
    }

    @Test
    @Order(3)
    public void testGetClientSecretPhase1() {
        String result = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.GET_CLIENT_SECRET,
                "{\"realm\": \"" + REALM + "\", \"clientId\": \"backend-service\"}"
        );
        assertNotNull(result);
    }

    @Test
    @Order(4)
    public void testGetClientProtocolMappersPhase1() {
        String result = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.GET_CLIENT_PROTOCOL_MAPPERS,
                "{\"realm\": \"" + REALM + "\", \"clientId\": \"backend-service\"}"
        );
        assertNotNull(result);
        assertTrue(result.startsWith("["));
    }

    @Test
    @Order(5)
    public void testCreateClient() {
        String result = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.CREATE_CLIENT,
                """
                {
                    "realm": "%s",
                    "clientId": "%s",
                    "redirectUris": "http://localhost:8080/*"
                }
                """.formatted(REALM, TEST_CLIENT)
        );

        assertNotNull(result);
        assertTrue(result.toLowerCase().contains("created") || result.toLowerCase().contains("success"));
    }

    @Test
    @Order(6)
    public void testGetCreatedClient() {
        String result = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.GET_CLIENT,
                "{\"realm\": \"" + REALM + "\", \"clientId\": \"" + TEST_CLIENT + "\"}"
        );

        assertNotNull(result);
        assertTrue(result.contains(TEST_CLIENT));
    }

    @Test
    @Order(7)
    public void testGenerateClientSecret() {
        String result = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.GENERATE_CLIENT_SECRET,
                "{\"realm\": \"" + REALM + "\", \"clientId\": \"" + TEST_CLIENT + "\"}"
        );

        assertNotNull(result);
        // Should return a secret string
        assertFalse(result.isEmpty());
    }

    @Test
    @Order(8)
    public void testGetClientRoles() {
        String result = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.GET_CLIENT_ROLES,
                "{\"realm\": \"" + REALM + "\", \"clientId\": \"backend-service\"}"
        );

        assertNotNull(result);
        // backend-service has uma_protection role in quarkus-realm.json
    }

    @Test
    @Order(9)
    public void testCreateClientRole() {
        String result = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.CREATE_CLIENT_ROLE,
                """
                {
                    "realm": "%s",
                    "clientId": "%s",
                    "roleName": "test-role",
                    "description": "A test role"
                }
                """.formatted(REALM, TEST_CLIENT)
        );

        assertNotNull(result);
        assertTrue(result.toLowerCase().contains("created") || result.toLowerCase().contains("success"));
    }

    @Test
    @Order(10)
    public void testDeleteClientRole() {
        String result = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.DELETE_CLIENT_ROLE,
                """
                {
                    "realm": "%s",
                    "clientId": "%s",
                    "roleName": "test-role"
                }
                """.formatted(REALM, TEST_CLIENT)
        );

        assertNotNull(result);
    }

    @Test
    @Order(100)
    public void testDeleteClient() {
        String result = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.DELETE_CLIENT,
                "{\"realm\": \"" + REALM + "\", \"clientId\": \"" + TEST_CLIENT + "\"}"
        );

        assertNotNull(result);
    }
}

