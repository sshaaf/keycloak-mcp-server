package dev.shaaf.keycloak.mcp.server.commands.authz;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import dev.shaaf.keycloak.mcp.server.authz.AuthorizationAdminService;
import dev.shaaf.keycloak.mcp.server.commands.AbstractCommand;
import dev.shaaf.keycloak.mcp.server.commands.RegisteredCommand;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;

@ApplicationScoped
@RegisteredCommand
public class UpdateAuthzResourceCommand extends AbstractCommand {

    @Inject
    AuthorizationAdminService authz;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.UPDATE_AUTHZ_RESOURCE;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "clientId", "resourceId", "resource"};
    }

    @Override
    public String getDescription() {
        return "Update a protected resource";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        return authz.updateResource(
                requireString(params, "realm"),
                requireString(params, "clientId"),
                requireString(params, "resourceId"),
                extractObject(params, "resource", ResourceRepresentation.class));
    }
}
