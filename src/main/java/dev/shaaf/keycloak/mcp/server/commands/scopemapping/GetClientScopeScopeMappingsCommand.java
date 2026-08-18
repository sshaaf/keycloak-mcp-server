package dev.shaaf.keycloak.mcp.server.commands.scopemapping;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import dev.shaaf.keycloak.mcp.server.commands.AbstractCommand;
import dev.shaaf.keycloak.mcp.server.commands.RegisteredCommand;
import dev.shaaf.keycloak.mcp.server.scopemapping.ScopeMappingService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.keycloak.representations.idm.MappingsRepresentation;

@ApplicationScoped
@RegisteredCommand
public class GetClientScopeScopeMappingsCommand extends AbstractCommand {

    @Inject
    ScopeMappingService scopeMappingService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.GET_CLIENT_SCOPE_SCOPE_MAPPINGS;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "clientScopeId"};
    }

    @Override
    public String getDescription() {
        return "Get combined client-scope scope mappings (realm and client role assignments)";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        MappingsRepresentation m = scopeMappingService.getScopeMappings(
                requireString(params, "realm"), requireString(params, "clientScopeId"));
        if (m == null) {
            return "null";
        }
        return toJson(m);
    }
}
