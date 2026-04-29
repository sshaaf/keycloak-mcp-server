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
public class RemoveRoleFromGroupCommand extends AbstractCommand {

    @Inject
    GroupService groupService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.REMOVE_ROLE_FROM_GROUP;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "groupId", "roleName"};
    }

    @Override
    public String getDescription() {
        return "Remove a role from a group";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        String realm = requireString(params, "realm");
        String groupId = requireString(params, "groupId");
        String roleName = requireString(params, "roleName");
        return groupService.removeRoleFromGroup(realm, groupId, roleName);
    }
}
