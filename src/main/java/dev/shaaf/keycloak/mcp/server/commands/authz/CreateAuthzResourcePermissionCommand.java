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
public class CreateAuthzResourcePermissionCommand extends AbstractCommand {

    @Inject
    AuthorizationAdminService authz;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.CREATE_AUTHZ_RESOURCE_PERMISSION;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "clientId", "permission"};
    }

    @Override
    public String getDescription() {
        return "Create a resource-based permission (permission object)";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        return authz.createResourcePermission(
                requireString(params, "realm"),
                requireString(params, "clientId"),
                extractObject(params, "permission", ResourcePermissionRepresentation.class));
    }
}
