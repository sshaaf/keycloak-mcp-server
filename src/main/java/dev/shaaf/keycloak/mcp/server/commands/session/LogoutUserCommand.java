package dev.shaaf.keycloak.mcp.server.commands.session;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import dev.shaaf.keycloak.mcp.server.commands.AbstractCommand;
import dev.shaaf.keycloak.mcp.server.commands.RegisteredCommand;
import dev.shaaf.keycloak.mcp.server.session.SessionService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
@RegisteredCommand
public class LogoutUserCommand extends AbstractCommand {
    @Inject
    SessionService sessionService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.LOGOUT_USER;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "userId"};
    }

    @Override
    public String getDescription() {
        return "Logout a user from all sessions";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        String realm = requireString(params, "realm");
        String userId = requireString(params, "userId");
        return sessionService.logoutUser(realm, userId);
    }
}
