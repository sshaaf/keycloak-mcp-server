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
    @Tool(description = "Execute Keycloak administration via a single parametric tool. " +
            "Set operation to a value from the KeycloakOperation enum (178 operations across users, realms, clients, " +
            "client scopes, roles, groups, IDPs, auth flows, sessions, events, components, UMA, organizations, " +
            "localization, and more). Parameters are a JSON object; required fields depend on the operation. " +
            "Enable or disable specific operations in application properties (keycloak.mcp.commands.*). " +
            "At startup, available operations are listed in the server log when log-on-startup is enabled.")
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
