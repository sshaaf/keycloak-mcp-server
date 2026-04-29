package dev.shaaf.keycloak.mcp.server.commands.clientscope;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import dev.shaaf.keycloak.mcp.server.clientscope.ClientScopeService;
import dev.shaaf.keycloak.mcp.server.commands.AbstractCommand;
import dev.shaaf.keycloak.mcp.server.commands.RegisteredCommand;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
@RegisteredCommand
public class GetDefaultClientScopesCommand extends AbstractCommand {

    @Inject
    ClientScopeService clientScopeService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.GET_DEFAULT_CLIENT_SCOPES;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm"};
    }

    @Override
    public String getDescription() {
        return "Get all default client scopes for realm";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        String realm = requireString(params, "realm");
        return toJson(clientScopeService.getDefaultClientScopes(realm));
    }
}
