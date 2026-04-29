package dev.shaaf.keycloak.mcp.server.commands.realm;

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
 * Tests for Realm commands.
 * Uses Keycloak TestContainers with quarkus-realm.json pre-loaded.
 */
@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RealmCommandsTest {

    @Inject
    KeycloakTool keycloakTool;

    @Test
    @Order(1)
    public void testGetRealms() {
        String result = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.GET_REALMS,
                "{}"
        );

        assertNotNull(result);
        // Should contain at least the quarkus realm from our test config
        assertTrue(result.contains("quarkus") || result.contains("master"));
    }

    @Test
    @Order(2)
    public void testGetRealm() {
        String result = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.GET_REALM,
                "{\"realmName\": \"quarkus\"}"
        );

        assertNotNull(result);
        assertTrue(result.contains("quarkus"));
    }

    @Test
    @Order(3)
    public void testCreateRealm() {
        String result = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.CREATE_REALM,
                """
                {
                    "realmName": "test-realm",
                    "displayName": "Test Realm",
                    "enabled": true
                }
                """
        );

        assertNotNull(result);
        assertTrue(result.toLowerCase().contains("created") || result.toLowerCase().contains("success"));
    }

    @Test
    @Order(4)
    public void testGetCreatedRealm() {
        String result = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.GET_REALM,
                "{\"realmName\": \"test-realm\"}"
        );

        assertNotNull(result);
        assertTrue(result.contains("test-realm") || result.contains("Test Realm"));
    }

    @Test
    @Order(5)
    public void testGetRealmEventsConfigPhase1() {
        String result = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.GET_REALM_EVENTS_CONFIG,
                "{\"realmName\": \"quarkus\"}"
        );
        assertNotNull(result);
    }

    @Test
    @Order(6)
    public void testSetRealmEnabledPhase1() {
        String result = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.SET_REALM_ENABLED,
                "{\"realmName\": \"test-realm\", \"enabled\": true}"
        );
        assertNotNull(result);
        assertTrue(
                result.toLowerCase().contains("success")
                        || result.toLowerCase().contains("enable")
                        || result.toLowerCase().contains("update"),
                result
        );
    }
}

