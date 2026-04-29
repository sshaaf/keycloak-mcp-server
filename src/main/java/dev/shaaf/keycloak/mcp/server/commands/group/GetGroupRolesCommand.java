package dev.shaaf.keycloak.mcp.server.commands.group;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import dev.shaaf.keycloak.mcp.server.commands.AbstractCommand;
import dev.shaaf.keycloak.mcp.server.commands.RegisteredCommand;
import dev.shaaf.keycloak.mcp.server.group.GroupService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
@RegisteredCommand
public class GetGroupRolesCommand extends AbstractCommand {

    @Inject
    GroupService groupService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.GET_GROUP_ROLES;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "groupId"};
    }

    @Override
    public String getDescription() {
        return "Get roles assigned to a group";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        String realm = requireString(params, "realm");
        String groupId = requireString(params, "groupId");
        return toJson(groupService.getGroupRoles(realm, groupId));
    }
}
