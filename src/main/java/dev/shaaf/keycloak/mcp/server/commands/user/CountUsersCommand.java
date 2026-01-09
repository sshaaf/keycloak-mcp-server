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
public class CountUsersCommand extends AbstractCommand {

    @Inject
    UserService userService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.COUNT_USERS;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm"};
    }

    @Override
    public String getDescription() {
        return "Count users in a realm";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        String realm = requireString(params, "realm");
        return String.valueOf(userService.countUsers(realm));
    }
}

