package dev.shaaf.keycloak.mcp.server.group;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolArg;
import io.quarkiverse.mcp.server.ToolCallException;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;

public class GroupTool {

    @Inject
    GroupService groupService;

    @Inject
    ObjectMapper mapper;

    @Tool(description = "Get all groups from a keycloak realm")
    String getGroups(@ToolArg(description = "A String denoting the name of the realm") String realm) {
        try {
            return mapper.writeValueAsString(groupService.getGroups(realm));
        } catch (Exception e) {
            Log.error("Failed to get groups: " + realm, e);
            throw new ToolCallException("Failed to get groups: " + realm);
        }
    }

    @Tool(description = "Get group members from a keycloak realm")
    String getGroupMembers(@ToolArg(description = "A String denoting the name of the realm") String realm,
                          @ToolArg(description = "A String denoting the ID of the group") String groupId) {
        try {
            return mapper.writeValueAsString(groupService.getGroupMembers(realm, groupId));
        } catch (Exception e) {
            Log.error("Failed to get group members: " + groupId, e);
            throw new ToolCallException("Failed to get group members: " + groupId);
        }
    }
}