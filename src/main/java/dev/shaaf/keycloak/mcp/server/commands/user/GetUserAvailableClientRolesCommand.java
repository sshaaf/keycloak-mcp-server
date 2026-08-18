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
public class GetUserAvailableClientRolesCommand extends AbstractCommand {

    @Inject
    UserService userService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.GET_USER_AVAILABLE_CLIENT_ROLES;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "userId", "clientId"};
    }

    @Override
    public String getDescription() {
        return "List client roles the user can still be assigned (not yet assigned at client scope)";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        return toJson(userService.getUserAvailableClientRoles(
                requireString(params, "realm"),
                requireString(params, "userId"),
                requireString(params, "clientId")));
    }
}
