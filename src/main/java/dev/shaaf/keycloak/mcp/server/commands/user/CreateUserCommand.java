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
public class CreateUserCommand extends AbstractCommand {

    @Inject
    UserService userService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.CREATE_USER;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "username", "email", "password"};
    }

    @Override
    public String getDescription() {
        return "Create a new user in a realm";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        return userService.addUser(
                requireString(params, "realm"),
                requireString(params, "username"),
                optionalString(params, "firstName", ""),
                optionalString(params, "lastName", ""),
                requireString(params, "email"),
                requireString(params, "password")
        );
    }
}

