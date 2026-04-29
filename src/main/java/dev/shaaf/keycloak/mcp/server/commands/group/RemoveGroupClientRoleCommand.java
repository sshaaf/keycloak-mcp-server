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
public class RemoveGroupClientRoleCommand extends AbstractCommand {

    @Inject
    GroupService groupService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.REMOVE_CLIENT_ROLE_FROM_GROUP;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "groupId", "clientId", "roleName"};
    }

    @Override
    public String getDescription() {
        return "Remove a client role from a group";
    }

    @Override
    public String execute(JsonNode params) {
        return groupService.removeClientRoleFromGroup(
                requireString(params, "realm"),
                requireString(params, "groupId"),
                requireString(params, "clientId"),
                requireString(params, "roleName"));
    }
}
