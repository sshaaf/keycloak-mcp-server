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
public class AddClientScopeMappedClientRolesCommand extends AbstractCommand {

    @Inject
    ScopeMappingService scopeMappingService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.ADD_CLIENT_SCOPE_MAPPED_CLIENT_ROLES;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "clientScopeId", "targetClientId", "roles"};
    }

    @Override
    public String getDescription() {
        return "Add target-client roles to a client scope's scope mapping";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        return scopeMappingService.addClientScopeMapping(
                requireString(params, "realm"),
                requireString(params, "clientScopeId"),
                requireString(params, "targetClientId"),
                requireRoleList(params, "roles"));
    }
}
