package dev.shaaf.keycloak.mcp.server.commands.authz;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import dev.shaaf.keycloak.mcp.server.authz.AuthorizationAdminService;
import dev.shaaf.keycloak.mcp.server.commands.AbstractCommand;
import dev.shaaf.keycloak.mcp.server.commands.RegisteredCommand;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.keycloak.representations.idm.authorization.PolicyRepresentation;

@ApplicationScoped
@RegisteredCommand
public class CreateAuthzPolicyCommand extends AbstractCommand {

    @Inject
    AuthorizationAdminService authz;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.CREATE_AUTHZ_POLICY;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "clientId", "policy"};
    }

    @Override
    public String getDescription() {
        return "Create a policy (type, name, config depend on policy provider)";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        return authz.createPolicy(
                requireString(params, "realm"),
                requireString(params, "clientId"),
                extractObject(params, "policy", PolicyRepresentation.class));
    }
}
