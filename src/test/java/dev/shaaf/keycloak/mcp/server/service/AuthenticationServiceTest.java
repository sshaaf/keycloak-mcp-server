package dev.shaaf.keycloak.mcp.server.service;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.AuthenticationManagementResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.AuthenticationExecutionInfoRepresentation;
import org.keycloak.representations.idm.AuthenticationFlowRepresentation;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
public class AuthenticationServiceTest {

    @InjectMock
    Keycloak keycloak;

    @Inject
    AuthenticationService authenticationService;

    private RealmResource realmResource;
    private AuthenticationManagementResource flowsResource;
    private Response response;

    private static final String TEST_REALM = "test-realm";
    private static final String TEST_FLOW_ID = "test-flow-id";
    private static final String TEST_FLOW_ALIAS = "test-flow-alias";
    private static final String TEST_EXECUTION_ID = "test-execution-id";
    private static final String TEST_EXECUTION_DISPLAY_NAME = "Test Execution";

    @BeforeEach
    public void setup() {
        // Mock the Keycloak client chain
        realmResource = mock(RealmResource.class);
        flowsResource = mock(AuthenticationManagementResource.class);
        response = mock(Response.class);

        when(keycloak.realm(anyString())).thenReturn(realmResource);
        when(realmResource.flows()).thenReturn(flowsResource);
    }

    @Test
    public void testGetAuthenticationFlows_Success_ReturnsFlowList() {
        // Arrange
        List<AuthenticationFlowRepresentation> expectedFlows = new ArrayList<>();
        AuthenticationFlowRepresentation flow = new AuthenticationFlowRepresentation();
        flow.setId(TEST_FLOW_ID);
        flow.setAlias(TEST_FLOW_ALIAS);
        expectedFlows.add(flow);

        when(flowsResource.getFlows()).thenReturn(expectedFlows);

        // Act
        List<AuthenticationFlowRepresentation> actualFlows = authenticationService.getAuthenticationFlows(TEST_REALM);

        // Assert
        assertEquals(expectedFlows, actualFlows);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).flows();
        verify(flowsResource).getFlows();
    }

    @Test
    public void testGetAuthenticationFlows_Exception_ReturnsEmptyList() {
        // Arrange
        when(flowsResource.getFlows()).thenThrow(new RuntimeException("Test exception"));

        // Act
        List<AuthenticationFlowRepresentation> actualFlows = authenticationService.getAuthenticationFlows(TEST_REALM);

        // Assert
        assertTrue(actualFlows.isEmpty());
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).flows();
        verify(flowsResource).getFlows();
    }

    @Test
    public void testGetAuthenticationFlow_Success_ReturnsFlow() {
        // Arrange
        List<AuthenticationFlowRepresentation> flows = new ArrayList<>();
        AuthenticationFlowRepresentation expectedFlow = new AuthenticationFlowRepresentation();
        expectedFlow.setId(TEST_FLOW_ID);
        expectedFlow.setAlias(TEST_FLOW_ALIAS);
        flows.add(expectedFlow);

        when(flowsResource.getFlows()).thenReturn(flows);

        // Act
        AuthenticationFlowRepresentation actualFlow = authenticationService.getAuthenticationFlow(TEST_REALM, TEST_FLOW_ID);

        // Assert
        assertNotNull(actualFlow);
        assertEquals(TEST_FLOW_ID, actualFlow.getId());
        assertEquals(TEST_FLOW_ALIAS, actualFlow.getAlias());
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).flows();
        verify(flowsResource).getFlows();
    }

    @Test
    public void testGetAuthenticationFlow_FlowNotFound_ReturnsNull() {
        // Arrange
        List<AuthenticationFlowRepresentation> flows = new ArrayList<>();
        AuthenticationFlowRepresentation flow = new AuthenticationFlowRepresentation();
        flow.setId("other-flow-id");
        flow.setAlias("other-flow-alias");
        flows.add(flow);

        when(flowsResource.getFlows()).thenReturn(flows);

        // Act
        AuthenticationFlowRepresentation actualFlow = authenticationService.getAuthenticationFlow(TEST_REALM, TEST_FLOW_ID);

        // Assert
        assertNull(actualFlow);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).flows();
        verify(flowsResource).getFlows();
    }

    @Test
    public void testGetAuthenticationFlow_Exception_ReturnsNull() {
        // Arrange
        when(flowsResource.getFlows()).thenThrow(new RuntimeException("Test exception"));

        // Act
        AuthenticationFlowRepresentation actualFlow = authenticationService.getAuthenticationFlow(TEST_REALM, TEST_FLOW_ID);

        // Assert
        assertNull(actualFlow);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).flows();
        verify(flowsResource).getFlows();
    }

    @Test
    public void testCreateAuthenticationFlow_Success_ReturnsSuccessMessage() {
        // Arrange
        AuthenticationFlowRepresentation flow = new AuthenticationFlowRepresentation();
        flow.setId(TEST_FLOW_ID);
        flow.setAlias(TEST_FLOW_ALIAS);

        doNothing().when(flowsResource).createFlow(any(AuthenticationFlowRepresentation.class));

        // Act
        String result = authenticationService.createAuthenticationFlow(TEST_REALM, flow);

        // Assert
        assertEquals("Successfully created authentication flow: " + TEST_FLOW_ALIAS, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).flows();
        verify(flowsResource).createFlow(flow);
    }

    @Test
    public void testCreateAuthenticationFlow_Exception_ReturnsErrorMessage() {
        // Arrange
        AuthenticationFlowRepresentation flow = new AuthenticationFlowRepresentation();
        flow.setId(TEST_FLOW_ID);
        flow.setAlias(TEST_FLOW_ALIAS);

        doThrow(new RuntimeException("Test exception")).when(flowsResource).createFlow(any(AuthenticationFlowRepresentation.class));

        // Act
        String result = authenticationService.createAuthenticationFlow(TEST_REALM, flow);

        // Assert
        assertTrue(result.startsWith("Error creating authentication flow: " + TEST_FLOW_ALIAS));
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).flows();
        verify(flowsResource).createFlow(flow);
    }

    @Test
    public void testDeleteAuthenticationFlow_Success_ReturnsSuccessMessage() {
        // Arrange
        List<AuthenticationFlowRepresentation> flows = new ArrayList<>();
        AuthenticationFlowRepresentation flow = new AuthenticationFlowRepresentation();
        flow.setId(TEST_FLOW_ID);
        flow.setAlias(TEST_FLOW_ALIAS);
        flows.add(flow);

        when(flowsResource.getFlows()).thenReturn(flows);
        doNothing().when(flowsResource).deleteFlow(TEST_FLOW_ID);

        // Act
        String result = authenticationService.deleteAuthenticationFlow(TEST_REALM, TEST_FLOW_ID);

        // Assert
        assertEquals("Successfully deleted authentication flow: " + TEST_FLOW_ALIAS, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).flows();
        verify(flowsResource).getFlows();
        verify(flowsResource).deleteFlow(TEST_FLOW_ID);
    }

    @Test
    public void testDeleteAuthenticationFlow_FlowNotFound_ReturnsErrorMessage() {
        // Arrange
        List<AuthenticationFlowRepresentation> flows = new ArrayList<>();
        AuthenticationFlowRepresentation flow = new AuthenticationFlowRepresentation();
        flow.setId("other-flow-id");
        flow.setAlias("other-flow-alias");
        flows.add(flow);

        when(flowsResource.getFlows()).thenReturn(flows);

        // Act
        String result = authenticationService.deleteAuthenticationFlow(TEST_REALM, TEST_FLOW_ID);

        // Assert
        assertEquals("Authentication flow not found: " + TEST_FLOW_ID, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).flows();
        verify(flowsResource).getFlows();
    }

    @Test
    public void testDeleteAuthenticationFlow_NotFound_ReturnsErrorMessage() {
        // Arrange
        List<AuthenticationFlowRepresentation> flows = new ArrayList<>();
        AuthenticationFlowRepresentation flow = new AuthenticationFlowRepresentation();
        flow.setId(TEST_FLOW_ID);
        flow.setAlias(TEST_FLOW_ALIAS);
        flows.add(flow);

        when(flowsResource.getFlows()).thenReturn(flows);
        doThrow(new NotFoundException("Authentication flow not found")).when(flowsResource).deleteFlow(TEST_FLOW_ID);

        // Act
        String result = authenticationService.deleteAuthenticationFlow(TEST_REALM, TEST_FLOW_ID);

        // Assert
        assertEquals("Authentication flow not found: " + TEST_FLOW_ID, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).flows();
        verify(flowsResource).getFlows();
        verify(flowsResource).deleteFlow(TEST_FLOW_ID);
    }

    @Test
    public void testDeleteAuthenticationFlow_Exception_ReturnsErrorMessage() {
        // Arrange
        List<AuthenticationFlowRepresentation> flows = new ArrayList<>();
        AuthenticationFlowRepresentation flow = new AuthenticationFlowRepresentation();
        flow.setId(TEST_FLOW_ID);
        flow.setAlias(TEST_FLOW_ALIAS);
        flows.add(flow);

        when(flowsResource.getFlows()).thenReturn(flows);
        doThrow(new RuntimeException("Test exception")).when(flowsResource).deleteFlow(TEST_FLOW_ID);

        // Act
        String result = authenticationService.deleteAuthenticationFlow(TEST_REALM, TEST_FLOW_ID);

        // Assert
        assertTrue(result.startsWith("Error deleting authentication flow: " + TEST_FLOW_ID));
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).flows();
        verify(flowsResource).getFlows();
        verify(flowsResource).deleteFlow(TEST_FLOW_ID);
    }

    @Test
    public void testGetFlowExecutions_Success_ReturnsExecutionList() {
        // Arrange
        List<AuthenticationExecutionInfoRepresentation> expectedExecutions = new ArrayList<>();
        AuthenticationExecutionInfoRepresentation execution = new AuthenticationExecutionInfoRepresentation();
        execution.setId(TEST_EXECUTION_ID);
        execution.setDisplayName(TEST_EXECUTION_DISPLAY_NAME);
        expectedExecutions.add(execution);

        when(flowsResource.getExecutions(TEST_FLOW_ALIAS)).thenReturn(expectedExecutions);

        // Act
        List<AuthenticationExecutionInfoRepresentation> actualExecutions = authenticationService.getFlowExecutions(TEST_REALM, TEST_FLOW_ALIAS);

        // Assert
        assertEquals(expectedExecutions, actualExecutions);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).flows();
        verify(flowsResource).getExecutions(TEST_FLOW_ALIAS);
    }

    @Test
    public void testGetFlowExecutions_NotFound_ReturnsEmptyList() {
        // Arrange
        when(flowsResource.getExecutions(TEST_FLOW_ALIAS)).thenThrow(new NotFoundException("Authentication flow not found"));

        // Act
        List<AuthenticationExecutionInfoRepresentation> actualExecutions = authenticationService.getFlowExecutions(TEST_REALM, TEST_FLOW_ALIAS);

        // Assert
        assertTrue(actualExecutions.isEmpty());
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).flows();
        verify(flowsResource).getExecutions(TEST_FLOW_ALIAS);
    }

    @Test
    public void testGetFlowExecutions_Exception_ReturnsEmptyList() {
        // Arrange
        when(flowsResource.getExecutions(TEST_FLOW_ALIAS)).thenThrow(new RuntimeException("Test exception"));

        // Act
        List<AuthenticationExecutionInfoRepresentation> actualExecutions = authenticationService.getFlowExecutions(TEST_REALM, TEST_FLOW_ALIAS);

        // Assert
        assertTrue(actualExecutions.isEmpty());
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).flows();
        verify(flowsResource).getExecutions(TEST_FLOW_ALIAS);
    }

    @Test
    public void testUpdateFlowExecution_Success_ReturnsSuccessMessage() {
        // Arrange
        AuthenticationExecutionInfoRepresentation execution = new AuthenticationExecutionInfoRepresentation();
        execution.setId(TEST_EXECUTION_ID);
        execution.setDisplayName(TEST_EXECUTION_DISPLAY_NAME);

        doNothing().when(flowsResource).updateExecutions(anyString(), any(AuthenticationExecutionInfoRepresentation.class));

        // Act
        String result = authenticationService.updateFlowExecution(TEST_REALM, TEST_FLOW_ALIAS, execution);

        // Assert
        assertEquals("Successfully updated flow execution: " + TEST_EXECUTION_DISPLAY_NAME, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).flows();
        verify(flowsResource).updateExecutions(TEST_FLOW_ALIAS, execution);
    }

    @Test
    public void testUpdateFlowExecution_NotFound_ReturnsErrorMessage() {
        // Arrange
        AuthenticationExecutionInfoRepresentation execution = new AuthenticationExecutionInfoRepresentation();
        execution.setId(TEST_EXECUTION_ID);
        execution.setDisplayName(TEST_EXECUTION_DISPLAY_NAME);

        doThrow(new NotFoundException("Authentication flow not found")).when(flowsResource).updateExecutions(anyString(), any(AuthenticationExecutionInfoRepresentation.class));

        // Act
        String result = authenticationService.updateFlowExecution(TEST_REALM, TEST_FLOW_ALIAS, execution);

        // Assert
        assertEquals("Authentication flow not found: " + TEST_FLOW_ALIAS, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).flows();
        verify(flowsResource).updateExecutions(TEST_FLOW_ALIAS, execution);
    }

    @Test
    public void testUpdateFlowExecution_Exception_ReturnsErrorMessage() {
        // Arrange
        AuthenticationExecutionInfoRepresentation execution = new AuthenticationExecutionInfoRepresentation();
        execution.setId(TEST_EXECUTION_ID);
        execution.setDisplayName(TEST_EXECUTION_DISPLAY_NAME);

        doThrow(new RuntimeException("Test exception")).when(flowsResource).updateExecutions(anyString(), any(AuthenticationExecutionInfoRepresentation.class));

        // Act
        String result = authenticationService.updateFlowExecution(TEST_REALM, TEST_FLOW_ALIAS, execution);

        // Assert
        assertTrue(result.startsWith("Error updating flow execution: " + TEST_EXECUTION_DISPLAY_NAME));
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).flows();
        verify(flowsResource).updateExecutions(TEST_FLOW_ALIAS, execution);
    }
}