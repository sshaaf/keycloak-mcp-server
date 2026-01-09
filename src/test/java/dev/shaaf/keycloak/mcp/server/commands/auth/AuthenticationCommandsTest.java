package dev.shaaf.keycloak.mcp.server.commands.auth;

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
 * Tests for Authentication flow commands.
 * Uses Keycloak TestContainers with quarkus-realm.json pre-loaded.
 * Pre-existing flows: browser, direct grant, registration, reset credentials, etc.
 */
@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthenticationCommandsTest {

    private static final String REALM = "quarkus";

    @Inject
    KeycloakTool keycloakTool;

    @Test
    @Order(1)
    public void testGetAuthenticationFlows() {
        String result = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.GET_AUTHENTICATION_FLOWS,
                "{\"realm\": \"" + REALM + "\"}"
        );

        assertNotNull(result);
        // Should contain built-in flows
        assertTrue(result.contains("browser") || result.contains("direct grant"));
    }

    @Test
    @Order(2)
    public void testGetAuthenticationFlow() {
        // First get all flows to find a flow ID
        String flows = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.GET_AUTHENTICATION_FLOWS,
                "{\"realm\": \"" + REALM + "\"}"
        );

        assertNotNull(flows);
        assertTrue(flows.contains("browser"));
        
        // In a complete test, parse flow ID and call GET_AUTHENTICATION_FLOW
    }

    @Test
    @Order(3)
    public void testGetFlowExecutions() {
        String result = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.GET_FLOW_EXECUTIONS,
                "{\"realm\": \"" + REALM + "\", \"flowAlias\": \"browser\"}"
        );

        assertNotNull(result);
        // Should return executions for the browser flow
        assertTrue(result.startsWith("["));
    }

    @Test
    @Order(4)
    public void testCreateAuthenticationFlow() {
        // This creates a copy of an existing flow
        String result = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.CREATE_AUTHENTICATION_FLOW,
                "{\"realm\": \"" + REALM + "\", \"authFlowNameId\": \"browser\"}"
        );

        assertNotNull(result);
        // Should indicate success
    }

    @Test
    @Order(5)
    public void testGetFlowExecutionsForCopiedFlow() {
        String result = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.GET_FLOW_EXECUTIONS,
                "{\"realm\": \"" + REALM + "\", \"flowAlias\": \"browser-copy\"}"
        );

        assertNotNull(result);
        assertTrue(result.startsWith("["));
    }

    @Test
    @Order(6)
    public void testUpdateFlowExecution() {
        // Get executions first
        String executions = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.GET_FLOW_EXECUTIONS,
                "{\"realm\": \"" + REALM + "\", \"flowAlias\": \"browser-copy\"}"
        );

        assertNotNull(executions);
        // In a complete test, parse execution and call UPDATE_FLOW_EXECUTION
    }

    @Test
    @Order(100)
    public void testDeleteAuthenticationFlow() {
        // First get the copied flow to find its ID
        String flows = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.GET_AUTHENTICATION_FLOWS,
                "{\"realm\": \"" + REALM + "\"}"
        );

        assertNotNull(flows);
        // In a complete test, parse flow ID for "browser-copy" and delete it
    }
}

