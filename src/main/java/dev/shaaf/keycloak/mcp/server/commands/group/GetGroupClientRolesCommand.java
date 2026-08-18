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
public class GetGroupClientRolesCommand extends AbstractCommand {

    @Inject
    GroupService groupService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.GET_GROUP_CLIENT_ROLES;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "groupId", "clientId"};
    }

    @Override
    public String getDescription() {
        return "List client roles assigned to a group";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        return toJson(groupService.getGroupClientRoles(
                requireString(params, "realm"),
                requireString(params, "groupId"),
                requireString(params, "clientId")));
    }
}
