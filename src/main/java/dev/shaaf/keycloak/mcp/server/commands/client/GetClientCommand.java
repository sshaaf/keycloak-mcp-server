package dev.shaaf.keycloak.mcp.server.commands.client;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import dev.shaaf.keycloak.mcp.server.client.ClientService;
import dev.shaaf.keycloak.mcp.server.commands.AbstractCommand;
import dev.shaaf.keycloak.mcp.server.commands.RegisteredCommand;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.keycloak.representations.idm.ClientRepresentation;

import java.util.Optional;

@ApplicationScoped
@RegisteredCommand
public class GetClientCommand extends AbstractCommand {

    @Inject
    ClientService clientService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.GET_CLIENT;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "clientId"};
    }

    @Override
    public String getDescription() {
        return "Get a specific client by client ID";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        String realm = requireString(params, "realm");
        String clientId = requireString(params, "clientId");
        Optional<ClientRepresentation> client = clientService.findClientByClientId(realm, clientId);
        return toJson(client.orElse(null));
    }
}

