package dev.shaaf.keycloak.mcp.server.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.shaaf.keycloak.mcp.server.service.RealmService;
import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolArg;
import io.quarkiverse.mcp.server.ToolCallException;
import io.quarkus.logging.Log;
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
    
    @Tool(description = "Get a specific realm by name")
    String getRealm(@ToolArg(description = "A String denoting the name of the realm to retrieve") String realmName) {
        try {
            return mapper.writeValueAsString(realmsService.getRealm(realmName));
        } catch (Exception e) {
            Log.error("Failed to get realm: " + realmName, e);
            throw new ToolCallException("Failed to get realm: " + realmName);
        }
    }
}
