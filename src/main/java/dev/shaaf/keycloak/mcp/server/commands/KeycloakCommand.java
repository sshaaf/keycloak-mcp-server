package dev.shaaf.keycloak.mcp.server.commands;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.keycloak.mcp.server.KeycloakOperation;

/**
 * Interface for all Keycloak MCP commands.
 * Implement this interface and annotate with @RegisteredCommand
 * to auto-register a new command.
 */
public interface KeycloakCommand {

    /**
     * The operation this command handles.
     */
    KeycloakOperation getOperation();

    /**
     * Execute the command with given parameters.
     *
     * @param params JSON parameters from the MCP request
     * @return JSON string result
     * @throws Exception if execution fails
     */
    String execute(JsonNode params) throws Exception;

    /**
     * Human-readable description for documentation.
     */
    default String getDescription() {
        return "Executes " + getOperation().name();
    }

    /**
     * Required parameter names for validation.
     */
    default String[] getRequiredParams() {
        return new String[0];
    }
}

