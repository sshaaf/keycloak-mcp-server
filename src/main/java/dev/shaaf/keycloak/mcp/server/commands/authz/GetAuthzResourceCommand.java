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
public class GetAuthzResourceCommand extends AbstractCommand {

    @Inject
    AuthorizationAdminService authz;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.GET_AUTHZ_RESOURCE;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "clientId", "resourceId"};
    }

    @Override
    public String getDescription() {
        return "Get an authorization resource by id";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        ResourceRepresentation r = authz.getResource(
                requireString(params, "realm"),
                requireString(params, "clientId"),
                requireString(params, "resourceId"));
        if (r == null) {
            return "null";
        }
        return toJson(r);
    }
}
