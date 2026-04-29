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
public class GetClientScopeCommand extends AbstractCommand {

    @Inject
    ClientScopeService clientScopeService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.GET_CLIENT_SCOPE;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "clientScopeId"};
    }

    @Override
    public String getDescription() {
        return "Get a specific client scope";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        String realm = requireString(params, "realm");
        String clientScopeId = requireString(params, "clientScopeId");
        return toJson(clientScopeService.getClientScope(realm, clientScopeId));
    }
}
