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
public class AddGroupClientRoleCommand extends AbstractCommand {

    @Inject
    GroupService groupService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.ADD_CLIENT_ROLE_TO_GROUP;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "groupId", "clientId", "roleName"};
    }

    @Override
    public String getDescription() {
        return "Add a client role to a group";
    }

    @Override
    public String execute(JsonNode params) {
        return groupService.addClientRoleToGroup(
                requireString(params, "realm"),
                requireString(params, "groupId"),
                requireString(params, "clientId"),
                requireString(params, "roleName"));
    }
}
