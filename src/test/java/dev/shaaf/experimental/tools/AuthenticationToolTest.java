package dev.shaaf.experimental.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.shaaf.experimental.service.AuthenticationService;
import io.quarkiverse.mcp.server.ToolCallException;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.AuthenticationExecutionInfoRepresentation;
import org.keycloak.representations.idm.AuthenticationFlowRepresentation;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
public class AuthenticationToolTest {

    @InjectMock
    AuthenticationService authenticationService;

    @InjectMock
    ObjectMapper mapper;

    @Inject
    AuthenticationTool authenticationTool;

    private static final String TEST_REALM = "test-realm";
    private static final String TEST_FLOW_ID = "test-flow-id";
    private static final String TEST_FLOW_ALIAS = "test-flow-alias";
    private static final String EXPECTED_JSON = "[{\"id\":\"test-flow-id\",\"alias\":\"test-flow-alias\"}]";
    private static final String SUCCESS_MESSAGE = "Authentication flow created successfully";
    private static final String DELETE_SUCCESS_MESSAGE = "Authentication flow deleted successfully";

    @Test
    public void testGetAuthenticationFlows_Success_ReturnsJsonString() throws Exception {
        // Arrange
        List<AuthenticationFlowRepresentation> flows = new ArrayList<>();
        AuthenticationFlowRepresentation flow = new AuthenticationFlowRepresentation();
        flow.setId(TEST_FLOW_ID);
        flow.setAlias(TEST_FLOW_ALIAS);
        flows.add(flow);

        when(authenticationService.getAuthenticationFlows(TEST_REALM)).thenReturn(flows);
        when(mapper.writeValueAsString(flows)).thenReturn(EXPECTED_JSON);

        // Act
        String result = authenticationTool.getAuthenticationFlows(TEST_REALM);

        // Assert
        assertEquals(EXPECTED_JSON, result);
        verify(authenticationService).getAuthenticationFlows(TEST_REALM);
        verify(mapper).writeValueAsString(flows);
    }

    @Test
    public void testGetAuthenticationFlows_MappingException_ThrowsToolCallException() throws Exception {
        // Arrange
        List<AuthenticationFlowRepresentation> flows = new ArrayList<>();
        when(authenticationService.getAuthenticationFlows(TEST_REALM)).thenReturn(flows);
        when(mapper.writeValueAsString(flows)).thenThrow(new RuntimeException("Mapping error"));

        // Act & Assert
        assertThrows(ToolCallException.class, () -> authenticationTool.getAuthenticationFlows(TEST_REALM));
        verify(authenticationService).getAuthenticationFlows(TEST_REALM);
        verify(mapper).writeValueAsString(flows);
    }

    @Test
    public void testGetAuthenticationFlow_Success_ReturnsJsonString() throws Exception {
        // Arrange
        AuthenticationFlowRepresentation flow = new AuthenticationFlowRepresentation();
        flow.setId(TEST_FLOW_ID);
        flow.setAlias(TEST_FLOW_ALIAS);

        when(authenticationService.getAuthenticationFlow(TEST_REALM, TEST_FLOW_ID)).thenReturn(flow);
        when(mapper.writeValueAsString(flow)).thenReturn("{\"id\":\"test-flow-id\",\"alias\":\"test-flow-alias\"}");

        // Act
        String result = authenticationTool.getAuthenticationFlow(TEST_REALM, TEST_FLOW_ID);

        // Assert
        assertEquals("{\"id\":\"test-flow-id\",\"alias\":\"test-flow-alias\"}", result);
        verify(authenticationService).getAuthenticationFlow(TEST_REALM, TEST_FLOW_ID);
        verify(mapper).writeValueAsString(flow);
    }

    @Test
    public void testGetAuthenticationFlow_MappingException_ThrowsToolCallException() throws Exception {
        // Arrange
        AuthenticationFlowRepresentation flow = new AuthenticationFlowRepresentation();
        flow.setId(TEST_FLOW_ID);
        flow.setAlias(TEST_FLOW_ALIAS);

        when(authenticationService.getAuthenticationFlow(TEST_REALM, TEST_FLOW_ID)).thenReturn(flow);
        when(mapper.writeValueAsString(flow)).thenThrow(new RuntimeException("Mapping error"));

        // Act & Assert
        assertThrows(ToolCallException.class, () -> authenticationTool.getAuthenticationFlow(TEST_REALM, TEST_FLOW_ID));
        verify(authenticationService).getAuthenticationFlow(TEST_REALM, TEST_FLOW_ID);
        verify(mapper).writeValueAsString(flow);
    }

    @Test
    public void testCreateAuthenticationFlow_Success_ReturnsSuccessMessage() throws Exception {
        // Arrange
        String flowJson = "{\"id\":\"test-flow-id\",\"alias\":\"test-flow-alias\"}";
        AuthenticationFlowRepresentation flow = new AuthenticationFlowRepresentation();
        flow.setId(TEST_FLOW_ID);
        flow.setAlias(TEST_FLOW_ALIAS);

        when(mapper.readValue(flowJson, AuthenticationFlowRepresentation.class)).thenReturn(flow);
        when(authenticationService.createAuthenticationFlow(eq(TEST_REALM), any(AuthenticationFlowRepresentation.class)))
                .thenReturn(SUCCESS_MESSAGE);

        // Act
        String result = authenticationTool.createAuthenticationFlow(TEST_REALM, flowJson);

        // Assert
        assertEquals(SUCCESS_MESSAGE, result);
        verify(mapper).readValue(flowJson, AuthenticationFlowRepresentation.class);
        verify(authenticationService).createAuthenticationFlow(eq(TEST_REALM), any(AuthenticationFlowRepresentation.class));
    }

    @Test
    public void testCreateAuthenticationFlow_MappingException_ThrowsToolCallException() throws Exception {
        // Arrange
        String flowJson = "{\"id\":\"test-flow-id\",\"alias\":\"test-flow-alias\"}";
        when(mapper.readValue(anyString(), eq(AuthenticationFlowRepresentation.class)))
                .thenThrow(new RuntimeException("Mapping error"));

        // Act & Assert
        assertThrows(ToolCallException.class, () -> authenticationTool.createAuthenticationFlow(TEST_REALM, flowJson));
        verify(mapper).readValue(flowJson, AuthenticationFlowRepresentation.class);
    }

    @Test
    public void testDeleteAuthenticationFlow_Success_ReturnsSuccessMessage() {
        // Arrange
        when(authenticationService.deleteAuthenticationFlow(TEST_REALM, TEST_FLOW_ID)).thenReturn(DELETE_SUCCESS_MESSAGE);

        // Act
        String result = authenticationTool.deleteAuthenticationFlow(TEST_REALM, TEST_FLOW_ID);

        // Assert
        assertEquals(DELETE_SUCCESS_MESSAGE, result);
        verify(authenticationService).deleteAuthenticationFlow(TEST_REALM, TEST_FLOW_ID);
    }

    @Test
    public void testGetFlowExecutions_Success_ReturnsJsonString() throws Exception {
        // Arrange
        List<AuthenticationExecutionInfoRepresentation> executions = new ArrayList<>();
        AuthenticationExecutionInfoRepresentation execution = new AuthenticationExecutionInfoRepresentation();
        execution.setId("execution-id");
        execution.setDisplayName("Test Execution");
        executions.add(execution);

        when(authenticationService.getFlowExecutions(TEST_REALM, TEST_FLOW_ALIAS)).thenReturn(executions);
        when(mapper.writeValueAsString(executions)).thenReturn("[{\"id\":\"execution-id\",\"displayName\":\"Test Execution\"}]");

        // Act
        String result = authenticationTool.getFlowExecutions(TEST_REALM, TEST_FLOW_ALIAS);

        // Assert
        assertEquals("[{\"id\":\"execution-id\",\"displayName\":\"Test Execution\"}]", result);
        verify(authenticationService).getFlowExecutions(TEST_REALM, TEST_FLOW_ALIAS);
        verify(mapper).writeValueAsString(executions);
    }

    @Test
    public void testGetFlowExecutions_MappingException_ThrowsToolCallException() throws Exception {
        // Arrange
        List<AuthenticationExecutionInfoRepresentation> executions = new ArrayList<>();
        when(authenticationService.getFlowExecutions(TEST_REALM, TEST_FLOW_ALIAS)).thenReturn(executions);
        when(mapper.writeValueAsString(executions)).thenThrow(new RuntimeException("Mapping error"));

        // Act & Assert
        assertThrows(ToolCallException.class, () -> authenticationTool.getFlowExecutions(TEST_REALM, TEST_FLOW_ALIAS));
        verify(authenticationService).getFlowExecutions(TEST_REALM, TEST_FLOW_ALIAS);
        verify(mapper).writeValueAsString(executions);
    }

    @Test
    public void testUpdateFlowExecution_Success_ReturnsSuccessMessage() throws Exception {
        // Arrange
        String executionJson = "{\"id\":\"execution-id\",\"displayName\":\"Test Execution\"}";
        AuthenticationExecutionInfoRepresentation execution = new AuthenticationExecutionInfoRepresentation();
        execution.setId("execution-id");
        execution.setDisplayName("Test Execution");

        when(mapper.readValue(executionJson, AuthenticationExecutionInfoRepresentation.class)).thenReturn(execution);
        when(authenticationService.updateFlowExecution(eq(TEST_REALM), eq(TEST_FLOW_ALIAS), any(AuthenticationExecutionInfoRepresentation.class)))
                .thenReturn("Successfully updated flow execution: Test Execution");

        // Act
        String result = authenticationTool.updateFlowExecution(TEST_REALM, TEST_FLOW_ALIAS, executionJson);

        // Assert
        assertEquals("Successfully updated flow execution: Test Execution", result);
        verify(mapper).readValue(executionJson, AuthenticationExecutionInfoRepresentation.class);
        verify(authenticationService).updateFlowExecution(eq(TEST_REALM), eq(TEST_FLOW_ALIAS), any(AuthenticationExecutionInfoRepresentation.class));
    }

    @Test
    public void testUpdateFlowExecution_MappingException_ThrowsToolCallException() throws Exception {
        // Arrange
        String executionJson = "{\"id\":\"execution-id\",\"displayName\":\"Test Execution\"}";
        when(mapper.readValue(anyString(), eq(AuthenticationExecutionInfoRepresentation.class)))
                .thenThrow(new RuntimeException("Mapping error"));

        // Act & Assert
        assertThrows(ToolCallException.class, () -> authenticationTool.updateFlowExecution(TEST_REALM, TEST_FLOW_ALIAS, executionJson));
        verify(mapper).readValue(executionJson, AuthenticationExecutionInfoRepresentation.class);
    }
}