package dev.shaaf.keycloak.mcp.server;

import dev.shaaf.keycloak.mcp.server.commands.CommandRegistry;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Smoke test: Quarkus and CDI start; the MCP command layer is available.
 * <p>
 * HTTP {@code /q/health} checks are brittle across Quarkus versions and random ports;
 * use {@code curl} against a known port in CI, or a dedicated health IT with a fixed
 * {@link io.quarkus.test.junit.TestProfile}.
 */
@QuarkusTest
public class KeycloakMcpServerTest {

    @Inject
    CommandRegistry commandRegistry;

    @Test
    public void applicationStartsWithCommandRegistry() {
        assertNotNull(commandRegistry);
        assertTrue(
                commandRegistry.getCommandCount() > 0,
                "At least one Keycloak command should be registered (Phase 1+2 + later phases)"
        );
    }
}
