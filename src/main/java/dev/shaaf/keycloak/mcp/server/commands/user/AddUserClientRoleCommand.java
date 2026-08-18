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
public class AddUserClientRoleCommand extends AbstractCommand {

    @Inject
    UserService userService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.ADD_CLIENT_ROLE_TO_USER;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "userId", "clientId", "roleName"};
    }

    @Override
    public String getDescription() {
        return "Add a client role to a user (adds a role of the target client, not a realm role)";
    }

    @Override
    public String execute(JsonNode params) {
        return userService.addClientRoleToUser(
                requireString(params, "realm"),
                requireString(params, "userId"),
                requireString(params, "clientId"),
                requireString(params, "roleName"));
    }
}
