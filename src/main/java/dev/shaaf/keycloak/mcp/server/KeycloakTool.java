package dev.shaaf.keycloak.mcp.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.shaaf.keycloak.mcp.server.commands.CommandRegistry;
import dev.shaaf.keycloak.mcp.server.commands.KeycloakCommand;
import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolArg;
import io.quarkiverse.mcp.server.ToolCallException;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Unified Keycloak Tool implementing the "Parametric Collapse" strategy.
 * <p>
 * This single tool handles all Keycloak operations by routing to the appropriate
 * command based on the operation parameter. Commands are auto-discovered via CDI
 * and can be enabled/disabled via application.properties.
 * </p>
 * <p>
 * Configuration options in application.properties:
 * <ul>
 *   <li>keycloak.mcp.commands.enabled - Comma-separated list of enabled commands (whitelist mode)</li>
 *   <li>keycloak.mcp.commands.disabled - Comma-separated list of disabled commands</li>
 *   <li>keycloak.mcp.commands.enable-all-by-default - Enable all discovered commands (default: true)</li>
 *   <li>keycloak.mcp.commands.log-on-startup - Log available commands on startup (default: true)</li>
 * </ul>
 * </p>
 */
@ApplicationScoped
public class KeycloakTool {

    @Inject
    CommandRegistry registry;

    @Inject
    ObjectMapper mapper;

    /**
     * Single unified tool method that handles all Keycloak operations.
     * Routes to the appropriate command based on the operation parameter.
     *
     * @param operation The type of Keycloak operation to perform
     * @param params    JSON string containing the parameters for the operation
     * @return JSON string result from the operation
     */
    @Tool(description = "Execute Keycloak administration operations. " +
            "Supports user, realm, client, role, group, identity provider, authentication management, and discourse search. " +
            "Pass the operation type and parameters as JSON. " +
            "Available operations: " +
            "User ops: GET_USERS, GET_USER_BY_USERNAME, CREATE_USER, DELETE_USER, UPDATE_USER, GET_USER_BY_ID, GET_USER_GROUPS, ADD_USER_TO_GROUP, REMOVE_USER_FROM_GROUP, GET_USER_ROLES, ADD_ROLE_TO_USER, REMOVE_ROLE_FROM_USER, RESET_PASSWORD, SEND_VERIFICATION_EMAIL, COUNT_USERS; " +
            "Realm ops: GET_REALMS, GET_REALM, CREATE_REALM; " +
            "Client ops: GET_CLIENTS, GET_CLIENT, CREATE_CLIENT, DELETE_CLIENT, GENERATE_CLIENT_SECRET, GET_CLIENT_ROLES, CREATE_CLIENT_ROLE, DELETE_CLIENT_ROLE; " +
            "Role ops: GET_REALM_ROLES, GET_REALM_ROLE; " +
            "Group ops: GET_GROUPS, GET_GROUP_MEMBERS, CREATE_GROUP, UPDATE_GROUP, DELETE_GROUP, CREATE_SUBGROUP; " +
            "IDP ops: GET_IDENTITY_PROVIDERS, GET_IDENTITY_PROVIDER, GET_IDENTITY_PROVIDER_MAPPERS; " +
            "Auth ops: GET_AUTHENTICATION_FLOWS, GET_AUTHENTICATION_FLOW, CREATE_AUTHENTICATION_FLOW, DELETE_AUTHENTICATION_FLOW, GET_FLOW_EXECUTIONS, UPDATE_FLOW_EXECUTION; " +
            "Discourse ops: SEARCH_DISCOURSE")
    public String executeKeycloakOperation(
            @ToolArg(description = "The operation to perform (e.g., GET_USERS, CREATE_USER, GET_REALMS, etc.)") 
            KeycloakOperation operation,
            @ToolArg(description = "JSON object containing operation parameters. Required fields vary by operation. " +
                    "Common fields: realm (String), username (String), userId (String), email (String), " +
                    "firstName (String), lastName (String), password (String), groupId (String), " +
                    "roleName (String), clientId (String), etc.") 
            String params) {

        // Check if operation is available (might be disabled via config)
        if (!registry.isAvailable(operation)) {
            throw new ToolCallException(
                    "Operation " + operation + " is not enabled. " +
                    "Available operations: " + registry.getAvailableOperationsString()
            );
        }

        try {
            JsonNode paramsNode = mapper.readTree(params);
            KeycloakCommand command = registry.getCommand(operation);

            Log.debugf("Executing %s with params: %s", operation, params);

            return command.execute(paramsNode);

        } catch (ToolCallException e) {
            // Re-throw tool call exceptions as-is
            throw e;
        } catch (Exception e) {
            Log.errorf(e, "Failed to execute %s", operation);
            throw new ToolCallException("Failed to execute operation " + operation + ": " + e.getMessage());
        }
    }
}
