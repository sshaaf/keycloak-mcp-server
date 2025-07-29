package dev.shaaf.keycloak.mcp.server.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.shaaf.keycloak.mcp.server.service.AuthenticationService;
import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolArg;
import io.quarkiverse.mcp.server.ToolCallException;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import org.keycloak.representations.idm.AuthenticationExecutionInfoRepresentation;
import org.keycloak.representations.idm.AuthenticationFlowRepresentation;

public class AuthenticationTool {

    @Inject
    AuthenticationService authenticationService;

    @Inject
    ObjectMapper mapper;

    @Tool(description = "Get all authentication flows from a keycloak realm")
    String getAuthenticationFlows(@ToolArg(description = "A String denoting the name of the realm") String realm) {
        try {
            return mapper.writeValueAsString(authenticationService.getAuthenticationFlows(realm));
        } catch (Exception e) {
            Log.error("Failed to get authentication flows: " + realm, e);
            throw new ToolCallException("Failed to get authentication flows: " + realm);
        }
    }

    @Tool(description = "Get a specific authentication flow from a keycloak realm")
    String getAuthenticationFlow(@ToolArg(description = "A String denoting the name of the realm") String realm,
                                @ToolArg(description = "A String denoting the ID of the flow") String flowId) {
        try {
            return mapper.writeValueAsString(authenticationService.getAuthenticationFlow(realm, flowId));
        } catch (Exception e) {
            Log.error("Failed to get authentication flow: " + flowId, e);
            throw new ToolCallException("Failed to get authentication flow: " + flowId);
        }
    }

    @Tool(description = "Create an authentication flow from a keycloak realm")
    String createAuthenticationFlow(@ToolArg(description = "A String denoting the name of the realm") String realm,
                                   @ToolArg(description = "A String denoting the authentication flow representation in JSON format") String flowJson) {
        try {
            AuthenticationFlowRepresentation flow = mapper.readValue(flowJson, AuthenticationFlowRepresentation.class);
            return authenticationService.createAuthenticationFlow(realm, flow);
        } catch (Exception e) {
            Log.error("Failed to create authentication flow", e);
            throw new ToolCallException("Failed to create authentication flow - " + e.getMessage());
        }
    }

    @Tool(description = "Get flow executions for a flow alias from a keycloak realm")
    String getFlowExecutions(@ToolArg(description = "A String denoting the name of the realm") String realm,
                            @ToolArg(description = "A String denoting the alias of the flow") String flowAlias) {
        try {
            return mapper.writeValueAsString(authenticationService.getFlowExecutions(realm, flowAlias));
        } catch (Exception e) {
            Log.error("Failed to get flow executions: " + flowAlias, e);
            throw new ToolCallException("Failed to get flow executions: " + flowAlias);
        }
    }
}