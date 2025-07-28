package dev.shaaf.experimental.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.shaaf.experimental.service.RoleService;
import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolArg;
import io.quarkiverse.mcp.server.ToolCallException;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import org.keycloak.representations.idm.RoleRepresentation;

public class RoleTool {

    @Inject
    RoleService roleService;

    @Inject
    ObjectMapper mapper;

    @Tool(description = "Get all realm roles")
    String getRealmRoles(@ToolArg(description = "A String denoting the name of the realm") String realm) {
        try {
            return mapper.writeValueAsString(roleService.getRealmRoles(realm));
        } catch (Exception e) {
            Log.error("Failed to get realm roles: " + realm, e);
            throw new ToolCallException("Failed to get realm roles: " + realm);
        }
    }

    @Tool(description = "Get a specific realm role")
    String getRealmRole(@ToolArg(description = "A String denoting the name of the realm") String realm,
                       @ToolArg(description = "A String denoting the name of the role") String roleName) {
        try {
            return mapper.writeValueAsString(roleService.getRealmRole(realm, roleName));
        } catch (Exception e) {
            Log.error("Failed to get realm role: " + roleName, e);
            throw new ToolCallException("Failed to get realm role: " + roleName);
        }
    }

    @Tool(description = "Create a realm role")
    String createRealmRole(@ToolArg(description = "A String denoting the name of the realm") String realm,
                          @ToolArg(description = "A String denoting the name of the role") String roleName,
                          @ToolArg(description = "A String denoting the description of the role") String description) {
        return roleService.createRealmRole(realm, roleName, description);
    }

    @Tool(description = "Update a realm role")
    String updateRealmRole(@ToolArg(description = "A String denoting the name of the realm") String realm,
                          @ToolArg(description = "A String denoting the name of the role") String roleName,
                          @ToolArg(description = "A String denoting the updated role representation in JSON format") String roleJson) {
        try {
            RoleRepresentation roleRepresentation = mapper.readValue(roleJson, RoleRepresentation.class);
            return roleService.updateRealmRole(realm, roleName, roleRepresentation);
        } catch (Exception e) {
            Log.error("Failed to update realm role: " + roleName, e);
            throw new ToolCallException("Failed to update realm role: " + roleName + " - " + e.getMessage());
        }
    }

    @Tool(description = "Delete a realm role")
    String deleteRealmRole(@ToolArg(description = "A String denoting the name of the realm") String realm,
                          @ToolArg(description = "A String denoting the name of the role") String roleName) {
        return roleService.deleteRealmRole(realm, roleName);
    }

    @Tool(description = "Get role composites")
    String getRoleComposites(@ToolArg(description = "A String denoting the name of the realm") String realm,
                            @ToolArg(description = "A String denoting the name of the role") String roleName) {
        try {
            return mapper.writeValueAsString(roleService.getRoleComposites(realm, roleName));
        } catch (Exception e) {
            Log.error("Failed to get role composites: " + roleName, e);
            throw new ToolCallException("Failed to get role composites: " + roleName);
        }
    }

    @Tool(description = "Add composite to role")
    String addCompositeToRole(@ToolArg(description = "A String denoting the name of the realm") String realm,
                             @ToolArg(description = "A String denoting the name of the role") String roleName,
                             @ToolArg(description = "A String denoting the name of the composite role") String compositeRoleName) {
        return roleService.addCompositeToRole(realm, roleName, compositeRoleName);
    }

    @Tool(description = "Remove composite from role")
    String removeCompositeFromRole(@ToolArg(description = "A String denoting the name of the realm") String realm,
                                  @ToolArg(description = "A String denoting the name of the role") String roleName,
                                  @ToolArg(description = "A String denoting the name of the composite role") String compositeRoleName) {
        return roleService.removeCompositeFromRole(realm, roleName, compositeRoleName);
    }
}