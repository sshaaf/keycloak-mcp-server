package dev.shaaf.keycloak.mcp.server.service;

import dev.shaaf.keycloak.mcp.server.testcontainers.AbstractKeycloakTest;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.keycloak.representations.idm.AuthenticationFlowRepresentation;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration tests for AuthenticationService using Testcontainers.
 * This class tests the AuthenticationService against a real Keycloak instance running in a Docker container.
 * It uses the quarkus realm imported from deploy/quarkus-realm.json.
 */
@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthenticationServiceTestWithTestcontainers extends AbstractKeycloakTest {

    private AuthenticationService authenticationService;

    private static final String TEST_FLOW_ALIAS = "test-flow-" + UUID.randomUUID().toString();

    /**
     * Sets up the test environment before each test.
     */
    @BeforeEach
    public void setup() {
        // Override the keycloakClient in the service with our testcontainer client
        authenticationService = new AuthenticationService();
        
        // Use reflection to set the keycloakClient field in the service
        try {
            java.lang.reflect.Field field = AuthenticationService.class.getDeclaredField("keycloak");
            field.setAccessible(true);
            field.set(authenticationService, keycloakClient);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set keycloakClient in AuthenticationService", e);
        }
    }

    /**
     * Tests that getAuthenticationFlows returns a list of flows.
     */
    @Test
    public void testGetAuthenticationFlows_Success_ReturnsFlowList() {
        // Act
        List<AuthenticationFlowRepresentation> flows = authenticationService.getAuthenticationFlows(QUARKUS_REALM);

        // Assert
        assertNotNull(flows);
        // The quarkus realm should have some default flows
        assertTrue(flows.size() > 0);
    }

    /**
     * Tests the creation and deletion of an authentication flow.
     */
    @Test
    public void testCreateAndDeleteAuthenticationFlow() {
        // Arrange
        AuthenticationFlowRepresentation flow = new AuthenticationFlowRepresentation();
        flow.setAlias(TEST_FLOW_ALIAS);
        flow.setDescription("Test flow created by Testcontainers");
        flow.setProviderId("basic-flow");
        flow.setBuiltIn(false);
        flow.setTopLevel(true);

        // Act - Create flow
        String createResult = authenticationService.createAuthenticationFlow(QUARKUS_REALM, flow);

        // Assert - Create flow
        assertEquals("Successfully created authentication flow: " + TEST_FLOW_ALIAS, createResult);

        // Verify flow exists
        List<AuthenticationFlowRepresentation> flows = authenticationService.getAuthenticationFlows(QUARKUS_REALM);
        assertTrue(flows.stream().anyMatch(f -> f.getAlias().equals(TEST_FLOW_ALIAS)));

        // Get the flow ID
        String flowId = flows.stream()
                .filter(f -> f.getAlias().equals(TEST_FLOW_ALIAS))
                .findFirst()
                .map(AuthenticationFlowRepresentation::getId)
                .orElseThrow(() -> new AssertionError("Flow not found"));

        // Act - Delete flow
        String deleteResult = authenticationService.deleteAuthenticationFlow(QUARKUS_REALM, flowId);

        // Assert - Delete flow
        assertEquals("Successfully deleted authentication flow: " + TEST_FLOW_ALIAS, deleteResult);

        // Verify flow no longer exists
        flows = authenticationService.getAuthenticationFlows(QUARKUS_REALM);
        assertTrue(flows.stream().noneMatch(f -> f.getAlias().equals(TEST_FLOW_ALIAS)));
    }
}