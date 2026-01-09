package dev.shaaf.keycloak.mcp.server.commands.idp;

import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import dev.shaaf.keycloak.mcp.server.KeycloakTool;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Identity Provider commands.
 * Uses Keycloak TestContainers with quarkus-realm.json pre-loaded.
 */
@QuarkusTest
public class IdentityProviderCommandsTest {

    private static final String REALM = "quarkus";

    @Inject
    KeycloakTool keycloakTool;

    @Test
    public void testGetIdentityProviders() {
        String result = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.GET_IDENTITY_PROVIDERS,
                "{\"realm\": \"" + REALM + "\"}"
        );

        assertNotNull(result);
        // Result should be a JSON array (might be empty if no IDPs configured)
        assertTrue(result.startsWith("["));
    }

    @Test
    public void testGetIdentityProvider_NotFound() {
        // Test with a non-existent IDP - should handle gracefully
        try {
            String result = keycloakTool.executeKeycloakOperation(
                    KeycloakOperation.GET_IDENTITY_PROVIDER,
                    "{\"realm\": \"" + REALM + "\", \"alias\": \"non-existent-idp\"}"
            );
            // Either returns null/empty or throws exception
            assertTrue(result == null || result.equals("null") || result.isEmpty());
        } catch (Exception e) {
            // Expected - IDP not found
            assertTrue(e.getMessage().toLowerCase().contains("not found") || 
                       e.getMessage().toLowerCase().contains("404"));
        }
    }

    @Test
    public void testGetIdentityProviderMappers_NotFound() {
        // Test with a non-existent IDP - should handle gracefully
        try {
            String result = keycloakTool.executeKeycloakOperation(
                    KeycloakOperation.GET_IDENTITY_PROVIDER_MAPPERS,
                    "{\"realm\": \"" + REALM + "\", \"alias\": \"non-existent-idp\"}"
            );
            // Either returns empty array or throws exception
            assertTrue(result == null || result.equals("null") || result.equals("[]"));
        } catch (Exception e) {
            // Expected - IDP not found
            assertTrue(e.getMessage().toLowerCase().contains("not found") || 
                       e.getMessage().toLowerCase().contains("404"));
        }
    }
}

