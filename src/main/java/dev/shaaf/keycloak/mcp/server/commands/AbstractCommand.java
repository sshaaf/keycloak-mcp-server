package dev.shaaf.keycloak.mcp.server.commands;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkiverse.mcp.server.ToolCallException;
import jakarta.inject.Inject;
import org.keycloak.representations.idm.RoleRepresentation;

import java.util.List;

/**
 * Base class for commands with common utilities.
 * Extend this class to get helper methods for parameter extraction and JSON serialization.
 */
public abstract class AbstractCommand implements KeycloakCommand {

    @Inject
    protected ObjectMapper mapper;

    /**
     * Safely extract a required string parameter.
     *
     * @param params JSON parameters
     * @param field  Field name to extract
     * @return The string value
     * @throws ToolCallException if the field is missing or null
     */
    protected String requireString(JsonNode params, String field) {
        JsonNode node = params.get(field);
        if (node == null || node.isNull()) {
            throw new ToolCallException("Missing required parameter: " + field);
        }
        return node.asText();
    }

    /**
     * Safely extract a required boolean parameter.
     */
    protected boolean requireBoolean(JsonNode params, String field) {
        JsonNode node = params.get(field);
        if (node == null || node.isNull()) {
            throw new ToolCallException("Missing required parameter: " + field);
        }
        if (!node.isBoolean() && !node.isNumber() && !node.isTextual()) {
            throw new ToolCallException("Parameter must be a boolean: " + field);
        }
        return node.asBoolean();
    }

    /**
     * Safely extract an optional string parameter with a default value.
     *
     * @param params       JSON parameters
     * @param field        Field name to extract
     * @param defaultValue Default value if field is missing
     * @return The string value or default
     */
    protected String optionalString(JsonNode params, String field, String defaultValue) {
        JsonNode node = params.get(field);
        return (node == null || node.isNull()) ? defaultValue : node.asText();
    }

    /**
     * Safely extract an optional boolean parameter with a default value.
     *
     * @param params       JSON parameters
     * @param field        Field name to extract
     * @param defaultValue Default value if field is missing
     * @return The boolean value or default
     */
    protected boolean optionalBoolean(JsonNode params, String field, boolean defaultValue) {
        JsonNode node = params.get(field);
        return (node == null || node.isNull()) ? defaultValue : node.asBoolean();
    }

    /**
     * Safely extract an optional integer parameter with a default value.
     *
     * @param params       JSON parameters
     * @param field        Field name to extract
     * @param defaultValue Default value if field is missing
     * @return The integer value or default
     */
    protected int optionalInt(JsonNode params, String field, int defaultValue) {
        JsonNode node = params.get(field);
        return (node == null || node.isNull()) ? defaultValue : node.asInt();
    }

    /**
     * Serialize result to JSON string.
     *
     * @param result Object to serialize
     * @return JSON string
     * @throws Exception if serialization fails
     */
    protected String toJson(Object result) throws Exception {
        return mapper.writeValueAsString(result);
    }

    /**
     * Deserialize JSON node to a specific type.
     *
     * @param params JSON parameters
     * @param field  Field name containing the object
     * @param clazz  Target class
     * @param <T>    Target type
     * @return Deserialized object
     * @throws Exception if deserialization fails
     */
    protected <T> T extractObject(JsonNode params, String field, Class<T> clazz) throws Exception {
        JsonNode node = params.get(field);
        if (node == null || node.isNull()) {
            throw new ToolCallException("Missing required parameter: " + field);
        }
        return mapper.treeToValue(node, clazz);
    }

    /**
     * JSON array of {@link RoleRepresentation} (id and/or name typically required for mappings).
     */
    protected List<RoleRepresentation> requireRoleList(JsonNode params, String field) throws Exception {
        JsonNode node = params.get(field);
        if (node == null || !node.isArray()) {
            throw new ToolCallException("Missing or invalid array parameter: " + field);
        }
        return mapper.convertValue(node, new TypeReference<List<RoleRepresentation>>() { });
    }
}

