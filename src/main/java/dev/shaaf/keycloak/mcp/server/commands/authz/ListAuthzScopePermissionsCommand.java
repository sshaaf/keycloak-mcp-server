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
public class ListAuthzScopePermissionsCommand extends AbstractCommand {

    @Inject
    AuthorizationAdminService authz;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.LIST_AUTHZ_SCOPE_PERMISSIONS;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "clientId"};
    }

    @Override
    public String getDescription() {
        return "List scope permissions; optional: name, resourceId, scopeId, first, max (pagination).";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        String name = optionalString(params, "name", null);
        String res = optionalString(params, "resourceId", null);
        String scope = optionalString(params, "scopeId", null);
        Integer first = params.has("first") && !params.get("first").isNull() ? params.get("first").asInt() : null;
        Integer max = params.has("max") && !params.get("max").isNull() ? params.get("max").asInt() : null;
        return toJson(authz.listScopePermissions(
                requireString(params, "realm"),
                requireString(params, "clientId"),
                name,
                res,
                scope,
                first,
                max));
    }
}
