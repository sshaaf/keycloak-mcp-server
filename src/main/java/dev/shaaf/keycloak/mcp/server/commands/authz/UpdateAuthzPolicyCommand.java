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
public class UpdateAuthzPolicyCommand extends AbstractCommand {

    @Inject
    AuthorizationAdminService authz;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.UPDATE_AUTHZ_POLICY;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "clientId", "policyId", "policy"};
    }

    @Override
    public String getDescription() {
        return "Update a policy by id";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        return authz.updatePolicy(
                requireString(params, "realm"),
                requireString(params, "clientId"),
                requireString(params, "policyId"),
                extractObject(params, "policy", PolicyRepresentation.class));
    }
}
