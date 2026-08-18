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
public class CreateAuthzResourceCommand extends AbstractCommand {

    @Inject
    AuthorizationAdminService authz;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.CREATE_AUTHZ_RESOURCE;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "clientId", "resource"};
    }

    @Override
    public String getDescription() {
        return "Create a protected resource (body in resource)";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        return authz.createResource(
                requireString(params, "realm"),
                requireString(params, "clientId"),
                extractObject(params, "resource", ResourceRepresentation.class));
    }
}
