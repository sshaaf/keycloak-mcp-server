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
public class DeleteClientScopeProtocolMapperCommand extends AbstractCommand {

    @Inject
    ClientScopeService clientScopeService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.DELETE_CLIENT_SCOPE_PROTOCOL_MAPPER;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "clientScopeId", "mapperId"};
    }

    @Override
    public String getDescription() {
        return "Delete a protocol mapper from a client scope";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        String realm = requireString(params, "realm");
        String clientScopeId = requireString(params, "clientScopeId");
        String mapperId = requireString(params, "mapperId");
        return clientScopeService.deleteProtocolMapper(realm, clientScopeId, mapperId);
    }
}
