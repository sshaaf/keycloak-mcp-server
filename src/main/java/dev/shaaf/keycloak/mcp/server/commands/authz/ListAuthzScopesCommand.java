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
public class ListAuthzScopesCommand extends AbstractCommand {

    @Inject
    AuthorizationAdminService authz;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.LIST_AUTHZ_SCOPES;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "clientId"};
    }

    @Override
    public String getDescription() {
        return "List authorization scopes for a client";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        return toJson(authz.listScopes(requireString(params, "realm"), requireString(params, "clientId")));
    }
}
