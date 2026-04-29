package dev.shaaf.keycloak.mcp.server.commands.authz;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import dev.shaaf.keycloak.mcp.server.authz.AuthorizationAdminService;
import dev.shaaf.keycloak.mcp.server.commands.AbstractCommand;
import dev.shaaf.keycloak.mcp.server.commands.RegisteredCommand;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.keycloak.representations.idm.authorization.ScopeRepresentation;

@ApplicationScoped
@RegisteredCommand
public class GetAuthzScopeCommand extends AbstractCommand {

    @Inject
    AuthorizationAdminService authz;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.GET_AUTHZ_SCOPE;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "clientId", "scopeId"};
    }

    @Override
    public String getDescription() {
        return "Get an authorization scope by id";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        ScopeRepresentation s = authz.getScope(
                requireString(params, "realm"),
                requireString(params, "clientId"),
                requireString(params, "scopeId"));
        if (s == null) {
            return "null";
        }
        return toJson(s);
    }
}
