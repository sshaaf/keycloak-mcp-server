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
public class GetClientUserSessionsCommand extends AbstractCommand {
    @Inject
    SessionService sessionService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.GET_CLIENT_USER_SESSIONS;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "clientId"};
    }

    @Override
    public String getDescription() {
        return "Get user sessions for a client";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        String realm = requireString(params, "realm");
        String clientId = requireString(params, "clientId");
        return toJson(sessionService.getClientUserSessions(realm, clientId));
    }
}
