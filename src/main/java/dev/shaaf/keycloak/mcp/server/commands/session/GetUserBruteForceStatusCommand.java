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
public class GetUserBruteForceStatusCommand extends AbstractCommand {

    @Inject
    SessionService sessionService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.GET_USER_BRUTE_FORCE_STATUS;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "userId"};
    }

    @Override
    public String getDescription() {
        return "Get brute-force / lockout status for a user (attack detection)";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        return toJson(sessionService.getBruteForceUserStatus(
                requireString(params, "realm"), requireString(params, "userId")));
    }
}
