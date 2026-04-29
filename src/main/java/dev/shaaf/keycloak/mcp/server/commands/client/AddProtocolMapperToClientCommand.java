package dev.shaaf.keycloak.mcp.server.commands.client;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import dev.shaaf.keycloak.mcp.server.client.ClientService;
import dev.shaaf.keycloak.mcp.server.commands.AbstractCommand;
import dev.shaaf.keycloak.mcp.server.commands.RegisteredCommand;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.keycloak.representations.idm.ProtocolMapperRepresentation;

@ApplicationScoped
@RegisteredCommand
public class AddProtocolMapperToClientCommand extends AbstractCommand {

    @Inject
    ClientService clientService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.ADD_PROTOCOL_MAPPER_TO_CLIENT;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "clientId", "mapper"};
    }

    @Override
    public String getDescription() {
        return "Add protocol mapper to a client";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        String realm = requireString(params, "realm");
        String clientId = requireString(params, "clientId");
        ProtocolMapperRepresentation mapper = extractObject(params, "mapper", ProtocolMapperRepresentation.class);
        return clientService.addProtocolMapperToClient(realm, clientId, mapper);
    }
}
