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
public class GetUserByUsernameCommand extends AbstractCommand {

    @Inject
    UserService userService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.GET_USER_BY_USERNAME;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "username"};
    }

    @Override
    public String getDescription() {
        return "Get a user by their username";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        String realm = requireString(params, "realm");
        String username = requireString(params, "username");
        return toJson(userService.getUserByUsername(realm, username));
    }
}

