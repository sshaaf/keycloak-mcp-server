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
public class RemoveRoleFromUserCommand extends AbstractCommand {

    @Inject
    UserService userService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.REMOVE_ROLE_FROM_USER;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "userId", "roleName"};
    }

    @Override
    public String getDescription() {
        return "Remove a realm role from a user";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        return userService.removeRoleFromUser(
                requireString(params, "realm"),
                requireString(params, "userId"),
                requireString(params, "roleName")
        );
    }
}

