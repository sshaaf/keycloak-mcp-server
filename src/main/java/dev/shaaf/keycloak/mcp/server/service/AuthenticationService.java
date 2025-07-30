package dev.shaaf.keycloak.mcp.server.service;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.AuthenticationManagementResource;
import org.keycloak.representations.idm.AuthenticationExecutionInfoRepresentation;
import org.keycloak.representations.idm.AuthenticationFlowRepresentation;

import java.util.Collections;
import java.util.List;

/**
 * Service for managing Keycloak authentication flows
 */
@ApplicationScoped
public class AuthenticationService {

    @Inject
    Keycloak keycloak;

    /**
     * Get all authentication flows
     * @param realm The realm to get authentication flows from
     * @return List of all authentication flows
     */
    public List<AuthenticationFlowRepresentation> getAuthenticationFlows(String realm) {
        try {
            return keycloak.realm(realm).flows().getFlows();
        } catch (Exception e) {
            Log.error("Failed to get authentication flows: " + realm, e);
            return Collections.emptyList();
        }
    }

    /**
     * Get a specific authentication flow
     * @param realm The realm where the authentication flow resides
     * @param flowId The ID of the authentication flow
     * @return The authentication flow representation or null if not found
     */
    public AuthenticationFlowRepresentation getAuthenticationFlow(String realm, String flowId) {
        try {
            // Get all flows and find the one with the matching ID
            return getAuthenticationFlows(realm).stream()
                    .filter(flow -> flowId.equals(flow.getId()))
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            Log.error("Failed to get authentication flow: " + flowId, e);
            return null;
        }
    }

    /**
     * Create an authentication flow
     * @param realm The realm where the authentication flow will be created
     * @param flow The authentication flow representation
     * @return Success or error message
     */
    public String createAuthenticationFlow(String realm, AuthenticationFlowRepresentation flow) {
        try {
            AuthenticationManagementResource flowsResource = keycloak.realm(realm).flows();
            flowsResource.createFlow(flow);
            return "Successfully created authentication flow: " + flow.getAlias();
        } catch (Exception e) {
            Log.error("Failed to create authentication flow: " + flow.getAlias(), e);
            return "Error creating authentication flow: " + flow.getAlias() + " - " + e.getMessage();
        }
    }

    /**
     * Delete an authentication flow
     * @param realm The realm where the authentication flow resides
     * @param flowId The ID of the authentication flow
     * @return Success or error message
     */
    public String deleteAuthenticationFlow(String realm, String flowId) {
        try {
            // Get the flow to check if it exists and to get its alias
            AuthenticationFlowRepresentation flow = getAuthenticationFlow(realm, flowId);
            if (flow == null) {
                return "Authentication flow not found: " + flowId;
            }
            
            keycloak.realm(realm).flows().deleteFlow(flow.getId());
            return "Successfully deleted authentication flow: " + flow.getAlias();
        } catch (NotFoundException e) {
            return "Authentication flow not found: " + flowId;
        } catch (Exception e) {
            Log.error("Failed to delete authentication flow: " + flowId, e);
            return "Error deleting authentication flow: " + flowId + " - " + e.getMessage();
        }
    }

    /**
     * Get flow executions
     * @param realm The realm where the authentication flow resides
     * @param flowAlias The alias of the authentication flow
     * @return List of flow executions or empty list if not found
     */
    public List<AuthenticationExecutionInfoRepresentation> getFlowExecutions(String realm, String flowAlias) {
        try {
            return keycloak.realm(realm).flows().getExecutions(flowAlias);
        } catch (NotFoundException e) {
            Log.error("Authentication flow not found: " + flowAlias, e);
            return Collections.emptyList();
        } catch (Exception e) {
            Log.error("Failed to get flow executions: " + flowAlias, e);
            return Collections.emptyList();
        }
    }

    /**
     * Update flow execution
     * @param realm The realm where the authentication flow resides
     * @param flowAlias The alias of the authentication flow
     * @param execution The updated execution representation
     * @return Success or error message
     */
    public String updateFlowExecution(String realm, String flowAlias, AuthenticationExecutionInfoRepresentation execution) {
        try {
            keycloak.realm(realm).flows().updateExecutions(flowAlias, execution);
            return "Successfully updated flow execution: " + execution.getDisplayName();
        } catch (NotFoundException e) {
            return "Authentication flow not found: " + flowAlias;
        } catch (Exception e) {
            Log.error("Failed to update flow execution: " + execution.getDisplayName(), e);
            return "Error updating flow execution: " + execution.getDisplayName() + " - " + e.getMessage();
        }
    }
}