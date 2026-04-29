package dev.shaaf.keycloak.mcp.server.commands.clientscope;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import dev.shaaf.keycloak.mcp.server.KeycloakTool;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Phase 2: client scope operations against the test Keycloak (quarkus-realm / TestContainers).
 */
@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ClientScopeCommandsTest {

    private static final String REALM = "quarkus";
    private static final String SCOPE_NAME = "mcp-phase2-test-scope";

    @Inject
    KeycloakTool keycloakTool;

    @Inject
    ObjectMapper objectMapper;

    @Test
    @Order(1)
    void testGetClientScopes() throws Exception {
        String result = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.GET_CLIENT_SCOPES,
                "{\"realm\": \"" + REALM + "\"}"
        );
        assertNotNull(result);
        assertTrue(result.startsWith("["));
    }

    @Test
    @Order(2)
    void testCreateClientScope() {
        String result = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.CREATE_CLIENT_SCOPE,
                """
                {
                    "realm": "%s",
                    "clientScope": {
                        "name": "%s",
                        "protocol": "openid-connect",
                        "description": "MCP Phase 2 test"
                    }
                }
                """.formatted(REALM, SCOPE_NAME)
        );
        assertNotNull(result);
        assertTrue(
                result.toLowerCase().contains("success")
                        || result.toLowerCase().contains("created"),
                result
        );
    }

    @Test
    @Order(3)
    void testGetClientScopeAndMappers() throws Exception {
        String listJson = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.GET_CLIENT_SCOPES,
                "{\"realm\": \"" + REALM + "\"}"
        );
        JsonNode root = objectMapper.readTree(listJson);
        String scopeId = null;
        for (JsonNode n : root) {
            if (SCOPE_NAME.equals(n.path("name").asText(null))) {
                scopeId = n.path("id").asText(null);
                break;
            }
        }
        assertNotNull(scopeId, "created scope not found: " + listJson);

        String one = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.GET_CLIENT_SCOPE,
                "{\"realm\": \"%s\", \"clientScopeId\": \"%s\"}".formatted(REALM, scopeId)
        );
        assertNotNull(one);
        assertTrue(one.contains(SCOPE_NAME), one);

        String mappers = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.GET_CLIENT_SCOPE_PROTOCOL_MAPPERS,
                "{\"realm\": \"%s\", \"clientScopeId\": \"%s\"}".formatted(REALM, scopeId)
        );
        assertNotNull(mappers);
        assertTrue(mappers.startsWith("["));
    }

    @Test
    @Order(4)
    void testUpdateClientScope() throws Exception {
        String listJson = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.GET_CLIENT_SCOPES,
                "{\"realm\": \"" + REALM + "\"}"
        );
        String scopeId = null;
        for (JsonNode n : objectMapper.readTree(listJson)) {
            if (SCOPE_NAME.equals(n.path("name").asText(null))) {
                scopeId = n.path("id").asText(null);
                break;
            }
        }
        assertNotNull(scopeId);

        String result = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.UPDATE_CLIENT_SCOPE,
                """
                {
                    "realm": "%s",
                    "clientScopeId": "%s",
                    "clientScope": {
                        "name": "%s",
                        "protocol": "openid-connect",
                        "description": "MCP Phase 2 updated"
                    }
                }
                """.formatted(REALM, scopeId, SCOPE_NAME)
        );
        assertNotNull(result);
        assertTrue(result.toLowerCase().contains("success") || result.toLowerCase().contains("updated"), result);
    }

    @Test
    @Order(5)
    void testDeleteClientScope() throws Exception {
        String listJson = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.GET_CLIENT_SCOPES,
                "{\"realm\": \"" + REALM + "\"}"
        );
        String scopeId = null;
        for (JsonNode n : objectMapper.readTree(listJson)) {
            if (SCOPE_NAME.equals(n.path("name").asText(null))) {
                scopeId = n.path("id").asText(null);
                break;
            }
        }
        assertNotNull(scopeId, "scope to delete not found");

        String result = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.DELETE_CLIENT_SCOPE,
                "{\"realm\": \"%s\", \"clientScopeId\": \"%s\"}".formatted(REALM, scopeId)
        );
        assertNotNull(result);
        assertTrue(result.toLowerCase().contains("success") || result.toLowerCase().contains("deleted"), result);
    }
}
