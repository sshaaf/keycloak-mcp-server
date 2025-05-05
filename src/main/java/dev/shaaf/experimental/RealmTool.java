package dev.shaaf.experimental;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.shaaf.experimental.service.RealmService;
import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolCallException;
import jakarta.inject.Inject;

public class RealmTool {

    @Inject
    RealmService realmsService;

    @Inject
    ObjectMapper mapper;

    @Tool(description = "Get all realms from keycloak")
    String getRealms() {
        try {
            return mapper.writeValueAsString(realmsService.getRealms());
        } catch (Exception e) {
            throw new ToolCallException("Failed to get realms from keycloak");
        }
    }
}
