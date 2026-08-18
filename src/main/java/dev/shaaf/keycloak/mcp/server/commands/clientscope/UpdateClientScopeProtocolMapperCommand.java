package dev.shaaf.keycloak.mcp.server.commands.clientscope;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import dev.shaaf.keycloak.mcp.server.clientscope.ClientScopeService;
import dev.shaaf.keycloak.mcp.server.commands.AbstractCommand;
import dev.shaaf.keycloak.mcp.server.commands.RegisteredCommand;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.keycloak.representations.idm.ProtocolMapperRepresentation;

@ApplicationScoped
@RegisteredCommand
public class UpdateClientScopeProtocolMapperCommand extends AbstractCommand {

    @Inject
    ClientScopeService clientScopeService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.UPDATE_CLIENT_SCOPE_PROTOCOL_MAPPER;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "clientScopeId", "mapperId", "mapper"};
    }

    @Override
    public String getDescription() {
        return "Update a protocol mapper in a client scope";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        String realm = requireString(params, "realm");
        String clientScopeId = requireString(params, "clientScopeId");
        String mapperId = requireString(params, "mapperId");
        ProtocolMapperRepresentation mapper = extractObject(params, "mapper", ProtocolMapperRepresentation.class);
        return clientScopeService.updateProtocolMapper(realm, clientScopeId, mapperId, mapper);
    }
}
