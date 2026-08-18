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
public class RevokeUserConsentCommand extends AbstractCommand {
    @Inject
    SessionService sessionService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.REVOKE_USER_CONSENT;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "userId", "clientId"};
    }

    @Override
    public String getDescription() {
        return "Revoke user consent for a client";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        String realm = requireString(params, "realm");
        String userId = requireString(params, "userId");
        String clientId = requireString(params, "clientId");
        return sessionService.revokeUserConsent(realm, userId, clientId);
    }
}
