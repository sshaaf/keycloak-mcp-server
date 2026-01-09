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
public class DeleteGroupCommand extends AbstractCommand {

    @Inject
    GroupService groupService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.DELETE_GROUP;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "groupId"};
    }

    @Override
    public String getDescription() {
        return "Delete a group from a realm";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        return groupService.deleteGroup(
                requireString(params, "realm"),
                requireString(params, "groupId")
        );
    }
}

