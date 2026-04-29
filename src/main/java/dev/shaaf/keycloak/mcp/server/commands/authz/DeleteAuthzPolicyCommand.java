package dev.shaaf.keycloak.mcp.server.commands.authz;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import dev.shaaf.keycloak.mcp.server.authz.AuthorizationAdminService;
import dev.shaaf.keycloak.mcp.server.commands.AbstractCommand;
import dev.shaaf.keycloak.mcp.server.commands.RegisteredCommand;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
@RegisteredCommand
public class DeleteAuthzPolicyCommand extends AbstractCommand {

    @Inject
    AuthorizationAdminService authz;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.DELETE_AUTHZ_POLICY;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "clientId", "policyId"};
    }

    @Override
    public String getDescription() {
        return "Delete a policy by id";
    }

    @Override
    public String execute(JsonNode params) {
        return authz.deletePolicy(
                requireString(params, "realm"),
                requireString(params, "clientId"),
                requireString(params, "policyId"));
    }
}
