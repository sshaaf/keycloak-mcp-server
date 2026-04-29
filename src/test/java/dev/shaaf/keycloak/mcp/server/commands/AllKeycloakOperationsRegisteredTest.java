package dev.shaaf.keycloak.mcp.server.commands;

import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Every {@link KeycloakOperation} must have a CDI {@link KeycloakCommand} when
 * {@code keycloak.mcp.commands.enable-all-by-default=true} (default in tests).
 */
@QuarkusTest
class AllKeycloakOperationsRegisteredTest {

    @Inject
    CommandRegistry registry;

    @Test
    void everyOperationHasRegisteredCommand() {
        for (KeycloakOperation op : KeycloakOperation.values()) {
            assertTrue(registry.isAvailable(op), "No command for: " + op);
            assertNotNull(registry.getCommand(op), "Null command for: " + op);
        }
    }
}
