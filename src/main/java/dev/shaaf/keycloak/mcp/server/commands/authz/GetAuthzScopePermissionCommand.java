package dev.shaaf.keycloak.mcp.server.commands.authz;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import dev.shaaf.keycloak.mcp.server.authz.AuthorizationAdminService;
import dev.shaaf.keycloak.mcp.server.commands.AbstractCommand;
import dev.shaaf.keycloak.mcp.server.commands.RegisteredCommand;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.keycloak.representations.idm.authorization.ScopePermissionRepresentation;

@ApplicationScoped
@RegisteredCommand
public class GetAuthzScopePermissionCommand extends AbstractCommand {

    @Inject
    AuthorizationAdminService authz;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.GET_AUTHZ_SCOPE_PERMISSION;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "clientId", "permissionId"};
    }

    @Override
    public String getDescription() {
        return "Get a scope-based permission by id";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        ScopePermissionRepresentation s = authz.getScopePermission(
                requireString(params, "realm"),
                requireString(params, "clientId"),
                requireString(params, "permissionId"));
        if (s == null) {
            return "null";
        }
        return toJson(s);
    }
}
