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
public class GetGroupsCommand extends AbstractCommand {

    @Inject
    GroupService groupService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.GET_GROUPS;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm"};
    }

    @Override
    public String getDescription() {
        return "Get all groups in a realm";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        String realm = requireString(params, "realm");
        return toJson(groupService.getGroups(realm));
    }
}

