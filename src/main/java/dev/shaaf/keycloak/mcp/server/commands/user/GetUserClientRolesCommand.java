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
public class GetUserClientRolesCommand extends AbstractCommand {

    @Inject
    UserService userService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.GET_USER_CLIENT_ROLES;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "userId", "clientId"};
    }

    @Override
    public String getDescription() {
        return "List client roles assigned to a user (clientId = internal id from GET_CLIENT)";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        return toJson(userService.getUserClientRoles(
                requireString(params, "realm"),
                requireString(params, "userId"),
                requireString(params, "clientId")));
    }
}
