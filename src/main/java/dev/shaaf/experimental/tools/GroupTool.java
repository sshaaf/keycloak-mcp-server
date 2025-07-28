package dev.shaaf.experimental.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.shaaf.experimental.service.GroupService;
import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolArg;
import io.quarkiverse.mcp.server.ToolCallException;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import org.keycloak.representations.idm.GroupRepresentation;

public class GroupTool {

    @Inject
    GroupService groupService;

    @Inject
    ObjectMapper mapper;

    @Tool(description = "Get all groups in a realm")
    String getGroups(@ToolArg(description = "A String denoting the name of the realm") String realm) {
        try {
            return mapper.writeValueAsString(groupService.getGroups(realm));
        } catch (Exception e) {
            Log.error("Failed to get groups: " + realm, e);
            throw new ToolCallException("Failed to get groups: " + realm);
        }
    }

    @Tool(description = "Get a specific group")
    String getGroup(@ToolArg(description = "A String denoting the name of the realm") String realm,
                   @ToolArg(description = "A String denoting the ID of the group") String groupId) {
        try {
            return mapper.writeValueAsString(groupService.getGroup(realm, groupId));
        } catch (Exception e) {
            Log.error("Failed to get group: " + groupId, e);
            throw new ToolCallException("Failed to get group: " + groupId);
        }
    }

    @Tool(description = "Create a group in a realm")
    String createGroup(@ToolArg(description = "A String denoting the name of the realm") String realm,
                      @ToolArg(description = "A String denoting the name of the group") String groupName) {
        return groupService.createGroup(realm, groupName);
    }

    @Tool(description = "Update a group")
    String updateGroup(@ToolArg(description = "A String denoting the name of the realm") String realm,
                      @ToolArg(description = "A String denoting the ID of the group") String groupId,
                      @ToolArg(description = "A String denoting the updated group representation in JSON format") String groupJson) {
        try {
            GroupRepresentation groupRepresentation = mapper.readValue(groupJson, GroupRepresentation.class);
            return groupService.updateGroup(realm, groupId, groupRepresentation);
        } catch (Exception e) {
            Log.error("Failed to update group: " + groupId, e);
            throw new ToolCallException("Failed to update group: " + groupId + " - " + e.getMessage());
        }
    }

    @Tool(description = "Delete a group")
    String deleteGroup(@ToolArg(description = "A String denoting the name of the realm") String realm,
                      @ToolArg(description = "A String denoting the ID of the group") String groupId) {
        return groupService.deleteGroup(realm, groupId);
    }

    @Tool(description = "Get group members")
    String getGroupMembers(@ToolArg(description = "A String denoting the name of the realm") String realm,
                          @ToolArg(description = "A String denoting the ID of the group") String groupId) {
        try {
            return mapper.writeValueAsString(groupService.getGroupMembers(realm, groupId));
        } catch (Exception e) {
            Log.error("Failed to get group members: " + groupId, e);
            throw new ToolCallException("Failed to get group members: " + groupId);
        }
    }

    @Tool(description = "Get group roles")
    String getGroupRoles(@ToolArg(description = "A String denoting the name of the realm") String realm,
                        @ToolArg(description = "A String denoting the ID of the group") String groupId) {
        try {
            return mapper.writeValueAsString(groupService.getGroupRoles(realm, groupId));
        } catch (Exception e) {
            Log.error("Failed to get group roles: " + groupId, e);
            throw new ToolCallException("Failed to get group roles: " + groupId);
        }
    }

    @Tool(description = "Add role to group")
    String addRoleToGroup(@ToolArg(description = "A String denoting the name of the realm") String realm,
                         @ToolArg(description = "A String denoting the ID of the group") String groupId,
                         @ToolArg(description = "A String denoting the name of the role") String roleName) {
        return groupService.addRoleToGroup(realm, groupId, roleName);
    }

    @Tool(description = "Remove role from group")
    String removeRoleFromGroup(@ToolArg(description = "A String denoting the name of the realm") String realm,
                              @ToolArg(description = "A String denoting the ID of the group") String groupId,
                              @ToolArg(description = "A String denoting the name of the role") String roleName) {
        return groupService.removeRoleFromGroup(realm, groupId, roleName);
    }

    @Tool(description = "Create subgroup")
    String createSubGroup(@ToolArg(description = "A String denoting the name of the realm") String realm,
                         @ToolArg(description = "A String denoting the ID of the parent group") String parentGroupId,
                         @ToolArg(description = "A String denoting the name of the subgroup") String subGroupName) {
        return groupService.createSubGroup(realm, parentGroupId, subGroupName);
    }
}