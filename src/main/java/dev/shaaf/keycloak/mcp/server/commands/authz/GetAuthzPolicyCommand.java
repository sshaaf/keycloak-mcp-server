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
public class GetAuthzPolicyCommand extends AbstractCommand {

    @Inject
    AuthorizationAdminService authz;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.GET_AUTHZ_POLICY;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "clientId", "policyId"};
    }

    @Override
    public String getDescription() {
        return "Get a policy by id";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        PolicyRepresentation p = authz.getPolicy(
                requireString(params, "realm"),
                requireString(params, "clientId"),
                requireString(params, "policyId"));
        if (p == null) {
            return "null";
        }
        return toJson(p);
    }
}
