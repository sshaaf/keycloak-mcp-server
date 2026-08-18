package dev.shaaf.keycloak.mcp.server.commands.group;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import dev.shaaf.keycloak.mcp.server.commands.AbstractCommand;
import dev.shaaf.keycloak.mcp.server.commands.RegisteredCommand;
import dev.shaaf.keycloak.mcp.server.group.GroupService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.keycloak.representations.idm.GroupRepresentation;

@ApplicationScoped
@RegisteredCommand
public class UpdateGroupCommand extends AbstractCommand {

    @Inject
    GroupService groupService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.UPDATE_GROUP;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "groupId", "groupRepresentation"};
    }

    @Override
    public String getDescription() {
        return "Update an existing group";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        String realm = requireString(params, "realm");
        String groupId = requireString(params, "groupId");
        GroupRepresentation groupRep = extractObject(params, "groupRepresentation", GroupRepresentation.class);
        return groupService.updateGroup(realm, groupId, groupRep);
    }
}

