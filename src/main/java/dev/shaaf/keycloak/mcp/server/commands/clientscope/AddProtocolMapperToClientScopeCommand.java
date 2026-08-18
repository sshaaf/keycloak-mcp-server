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
public class AddProtocolMapperToClientScopeCommand extends AbstractCommand {

    @Inject
    ClientScopeService clientScopeService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.ADD_PROTOCOL_MAPPER_TO_CLIENT_SCOPE;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "clientScopeId", "mapper"};
    }

    @Override
    public String getDescription() {
        return "Add a protocol mapper to a client scope";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        String realm = requireString(params, "realm");
        String clientScopeId = requireString(params, "clientScopeId");
        ProtocolMapperRepresentation mapper = extractObject(params, "mapper", ProtocolMapperRepresentation.class);
        return clientScopeService.addProtocolMapperToClientScope(realm, clientScopeId, mapper);
    }
}
