package dev.shaaf.keycloak.mcp.server.commands.authz;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import dev.shaaf.keycloak.mcp.server.authz.AuthorizationAdminService;
import dev.shaaf.keycloak.mcp.server.commands.AbstractCommand;
import dev.shaaf.keycloak.mcp.server.commands.RegisteredCommand;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
@RegisteredCommand
public class DeleteAuthzScopeCommand extends AbstractCommand {

    @Inject
    AuthorizationAdminService authz;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.DELETE_AUTHZ_SCOPE;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "clientId", "scopeId"};
    }

    @Override
    public String getDescription() {
        return "Delete an authorization scope";
    }

    @Override
    public String execute(JsonNode params) {
        return authz.deleteScope(
                requireString(params, "realm"),
                requireString(params, "clientId"),
                requireString(params, "scopeId"));
    }
}
