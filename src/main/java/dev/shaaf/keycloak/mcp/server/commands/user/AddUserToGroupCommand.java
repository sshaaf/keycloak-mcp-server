package dev.shaaf.keycloak.mcp.server.commands.user;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import dev.shaaf.keycloak.mcp.server.commands.AbstractCommand;
import dev.shaaf.keycloak.mcp.server.commands.RegisteredCommand;
import dev.shaaf.keycloak.mcp.server.user.UserService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
@RegisteredCommand
public class AddUserToGroupCommand extends AbstractCommand {

    @Inject
    UserService userService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.ADD_USER_TO_GROUP;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "userId", "groupId"};
    }

    @Override
    public String getDescription() {
        return "Add a user to a group";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        return userService.addUserToGroup(
                requireString(params, "realm"),
                requireString(params, "userId"),
                requireString(params, "groupId")
        );
    }
}

