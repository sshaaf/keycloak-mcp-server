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
public class DeleteUserCommand extends AbstractCommand {

    @Inject
    UserService userService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.DELETE_USER;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "username"};
    }

    @Override
    public String getDescription() {
        return "Delete a user from a realm";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        return userService.deleteUser(
                requireString(params, "realm"),
                requireString(params, "username")
        );
    }
}

