package dev.shaaf.keycloak.mcp.server.role;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolArg;
import io.quarkiverse.mcp.server.ToolCallException;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;

public class RoleTool {

    @Inject
    RoleService roleService;

    @Inject
    ObjectMapper mapper;

    @Tool(description = "Get all roles from a keycloak realm")
    String getRealmRoles(@ToolArg(description = "A String denoting the name of the realm") String realm) {
        try {
            return mapper.writeValueAsString(roleService.getRealmRoles(realm));
        } catch (Exception e) {
            Log.error("Failed to get realm roles: " + realm, e);
            throw new ToolCallException("Failed to get realm roles: " + realm);
        }
    }

    @Tool(description = "Get a specific role from a keycloak realm")
    String getRealmRole(@ToolArg(description = "A String denoting the name of the realm") String realm,
                       @ToolArg(description = "A String denoting the name of the role") String roleName) {
        try {
            return mapper.writeValueAsString(roleService.getRealmRole(realm, roleName));
        } catch (Exception e) {
            Log.error("Failed to get realm role: " + roleName, e);
            throw new ToolCallException("Failed to get realm role: " + roleName);
        }
    }



}