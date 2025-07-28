package dev.shaaf.experimental.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.shaaf.experimental.service.RealmService;
import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolArg;
import io.quarkiverse.mcp.server.ToolCallException;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import org.keycloak.representations.idm.RealmEventsConfigRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;

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
    
    @Tool(description = "Create a new realm")
    String createRealm(@ToolArg(description = "A String denoting the name of the realm to create") String realmName,
                      @ToolArg(description = "A String denoting the display name for the realm") String displayName,
                      @ToolArg(description = "A boolean indicating whether the realm should be enabled") boolean enabled) {
        return realmsService.createRealm(realmName, displayName, enabled);
    }
    
    @Tool(description = "Update a realm")
    String updateRealm(@ToolArg(description = "A String denoting the name of the realm to update") String realmName,
                      @ToolArg(description = "A String denoting the updated realm representation in JSON format") String realmJson) {
        try {
            RealmRepresentation realmRepresentation = mapper.readValue(realmJson, RealmRepresentation.class);
            return realmsService.updateRealm(realmName, realmRepresentation);
        } catch (Exception e) {
            Log.error("Failed to update realm: " + realmName, e);
            throw new ToolCallException("Failed to update realm: " + realmName + " - " + e.getMessage());
        }
    }
    
    @Tool(description = "Delete a realm")
    String deleteRealm(@ToolArg(description = "A String denoting the name of the realm to delete") String realmName) {
        return realmsService.deleteRealm(realmName);
    }
    
    @Tool(description = "Enable or disable a realm")
    String setRealmEnabled(@ToolArg(description = "A String denoting the name of the realm to update") String realmName,
                          @ToolArg(description = "A boolean indicating whether the realm should be enabled") boolean enabled) {
        return realmsService.setRealmEnabled(realmName, enabled);
    }
    
    @Tool(description = "Get realm events configuration")
    String getRealmEventsConfig(@ToolArg(description = "A String denoting the name of the realm") String realmName) {
        try {
            return mapper.writeValueAsString(realmsService.getRealmEventsConfig(realmName));
        } catch (Exception e) {
            Log.error("Failed to get realm events config: " + realmName, e);
            throw new ToolCallException("Failed to get realm events config: " + realmName);
        }
    }
    
    @Tool(description = "Update realm events configuration")
    String updateRealmEventsConfig(@ToolArg(description = "A String denoting the name of the realm") String realmName,
                                  @ToolArg(description = "A String denoting the updated events configuration in JSON format") String eventsConfigJson) {
        try {
            RealmEventsConfigRepresentation eventsConfig = mapper.readValue(eventsConfigJson, RealmEventsConfigRepresentation.class);
            return realmsService.updateRealmEventsConfig(realmName, eventsConfig);
        } catch (Exception e) {
            Log.error("Failed to update realm events config: " + realmName, e);
            throw new ToolCallException("Failed to update realm events config: " + realmName + " - " + e.getMessage());
        }
    }
}
