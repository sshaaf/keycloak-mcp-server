package dev.shaaf.keycloak.mcp.server.commands.scopemapping;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import dev.shaaf.keycloak.mcp.server.commands.AbstractCommand;
import dev.shaaf.keycloak.mcp.server.commands.RegisteredCommand;
import dev.shaaf.keycloak.mcp.server.scopemapping.ScopeMappingService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
@RegisteredCommand
public class GetClientScopeMappedRealmRolesCommand extends AbstractCommand {

    @Inject
    ScopeMappingService scopeMappingService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.GET_CLIENT_SCOPE_MAPPED_REALM_ROLES;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "clientScopeId"};
    }

    @Override
    public String getDescription() {
        return "Get realm role mappings for a client scope";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        return toJson(scopeMappingService.getRealmScopeMappings(
                requireString(params, "realm"), requireString(params, "clientScopeId")));
    }
}
