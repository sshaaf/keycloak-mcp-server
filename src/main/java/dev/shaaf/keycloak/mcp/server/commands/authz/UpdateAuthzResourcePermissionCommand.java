package dev.shaaf.keycloak.mcp.server.commands.authz;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import dev.shaaf.keycloak.mcp.server.authz.AuthorizationAdminService;
import dev.shaaf.keycloak.mcp.server.commands.AbstractCommand;
import dev.shaaf.keycloak.mcp.server.commands.RegisteredCommand;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.keycloak.representations.idm.authorization.ResourcePermissionRepresentation;

@ApplicationScoped
@RegisteredCommand
public class UpdateAuthzResourcePermissionCommand extends AbstractCommand {

    @Inject
    AuthorizationAdminService authz;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.UPDATE_AUTHZ_RESOURCE_PERMISSION;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "clientId", "permissionId", "permission"};
    }

    @Override
    public String getDescription() {
        return "Update a resource permission by id";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        return authz.updateResourcePermission(
                requireString(params, "realm"),
                requireString(params, "clientId"),
                requireString(params, "permissionId"),
                extractObject(params, "permission", ResourcePermissionRepresentation.class));
    }
}
