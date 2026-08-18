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
public class AddRoleToUserCommand extends AbstractCommand {

    @Inject
    UserService userService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.ADD_ROLE_TO_USER;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "userId", "roleName"};
    }

    @Override
    public String getDescription() {
        return "Add a realm role to a user";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        return userService.addRoleToUser(
                requireString(params, "realm"),
                requireString(params, "userId"),
                requireString(params, "roleName")
        );
    }
}

