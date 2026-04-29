package dev.shaaf.keycloak.mcp.server.commands.role;

import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import dev.shaaf.keycloak.mcp.server.KeycloakTool;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Role commands.
 * Uses Keycloak TestContainers with quarkus-realm.json pre-loaded.
 * Pre-existing roles: admin, user, confidential, offline_access, uma_authorization
 */
@QuarkusTest
public class RoleCommandsTest {

    private static final String REALM = "quarkus";

    @Inject
    KeycloakTool keycloakTool;

    @Test
    public void testGetRealmRoles() {
        String result = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.GET_REALM_ROLES,
                "{\"realm\": \"" + REALM + "\"}"
        );

        assertNotNull(result);
        // Should contain pre-existing roles from quarkus-realm.json
        assertTrue(result.contains("admin") || result.contains("user"));
    }

    @Test
    public void testGetRealmRole() {
        String result = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.GET_REALM_ROLE,
                "{\"realm\": \"" + REALM + "\", \"roleName\": \"admin\"}"
        );

        assertNotNull(result);
        assertTrue(result.contains("admin"));
    }

    @Test
    public void testCreateAndDeleteRealmRolePhase1() {
        String role = "mcp-phase1-temp-role";
        String create = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.CREATE_REALM_ROLE,
                """
                {
                    "realm": "%s",
                    "roleName": "%s",
                    "description": "Phase 1 test"
                }
                """.formatted(REALM, role)
        );
        assertNotNull(create);
        assertTrue(create.toLowerCase().contains("success") || create.toLowerCase().contains("created"), create);

        String del = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.DELETE_REALM_ROLE,
                "{\"realm\": \"" + REALM + "\", \"roleName\": \"" + role + "\"}"
        );
        assertNotNull(del);
    }
}

