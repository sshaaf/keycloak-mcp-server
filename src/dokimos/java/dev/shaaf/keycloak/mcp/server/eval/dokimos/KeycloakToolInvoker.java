package dev.shaaf.keycloak.mcp.server.eval.dokimos;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.dokimos.core.agents.ToolCall;
import dev.langchain4j.agent.tool.Tool;
import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import dev.shaaf.keycloak.mcp.server.KeycloakTool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * LangChain4j tool bean that records invocations for Dokimos {@link dev.dokimos.core.evaluators.agents.ToolCorrectnessEvaluator}
 * and delegates to {@link KeycloakTool}.
 */
public final class KeycloakToolInvoker {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final KeycloakTool keycloakTool;
    private final List<ToolCall> recorded = new ArrayList<>();

    public KeycloakToolInvoker(KeycloakTool keycloakTool) {
        this.keycloakTool = keycloakTool;
    }

    public void clear() {
        recorded.clear();
    }

    public List<ToolCall> getRecordedToolCalls() {
        return Collections.unmodifiableList(recorded);
    }

    /**
     * Named {@code invokeKeycloakOperation} so Quarkus MCP does not treat this LangChain4j {@link Tool}
     * as a duplicate of {@link KeycloakTool#executeKeycloakOperation}.
     */
    @Tool("Execute a Keycloak admin API operation. operation: KeycloakOperation enum name. "
            + "params: JSON object as a string, e.g. {} or {\"realmName\":\"quarkus\"}.")
    public String invokeKeycloakOperation(String operation, String params) {
        String normalized = normalizeParams(params);
        recorded.add(
                ToolCall.of(
                        "invokeKeycloakOperation",
                        Map.of("operation", operation.trim(), "params", normalized)));
        try {
            KeycloakOperation op = KeycloakOperation.valueOf(operation.trim());
            return keycloakTool.executeKeycloakOperation(op, normalized);
        } catch (IllegalArgumentException e) {
            return "{\"error\":\"unknown operation: " + operation + "\"}";
        }
    }

    private static String normalizeParams(String params) {
        if (params == null || params.isBlank()) {
            return "{}";
        }
        try {
            var node = MAPPER.readTree(params);
            return MAPPER.writeValueAsString(node);
        } catch (Exception e) {
            return params.trim();
        }
    }
}
