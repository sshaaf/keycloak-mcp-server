package dev.shaaf.keycloak.mcp.server.commands.authz;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import dev.shaaf.keycloak.mcp.server.authz.AuthorizationAdminService;
import dev.shaaf.keycloak.mcp.server.commands.AbstractCommand;
import dev.shaaf.keycloak.mcp.server.commands.RegisteredCommand;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.keycloak.representations.idm.authorization.ResourceServerRepresentation;

@ApplicationScoped
@RegisteredCommand
public class UpdateAuthzResourceServerCommand extends AbstractCommand {

    @Inject
    AuthorizationAdminService authz;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.UPDATE_AUTHZ_RESOURCE_SERVER;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "clientId", "resourceServer"};
    }

    @Override
    public String getDescription() {
        return "Update authorization service settings for a client (resourceServer object)";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        return authz.updateResourceServer(
                requireString(params, "realm"),
                requireString(params, "clientId"),
                extractObject(params, "resourceServer", ResourceServerRepresentation.class));
    }
}
