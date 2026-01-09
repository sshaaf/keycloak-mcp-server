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
public class CreateSubgroupCommand extends AbstractCommand {

    @Inject
    GroupService groupService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.CREATE_SUBGROUP;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "parentGroupId", "subGroupName"};
    }

    @Override
    public String getDescription() {
        return "Create a subgroup under a parent group";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        return groupService.createSubGroup(
                requireString(params, "realm"),
                requireString(params, "parentGroupId"),
                requireString(params, "subGroupName")
        );
    }
}

