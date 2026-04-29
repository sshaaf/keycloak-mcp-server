package dev.shaaf.keycloak.mcp.server.commands.client;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import dev.shaaf.keycloak.mcp.server.client.ClientService;
import dev.shaaf.keycloak.mcp.server.commands.AbstractCommand;
import dev.shaaf.keycloak.mcp.server.commands.RegisteredCommand;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.keycloak.representations.idm.ClientRepresentation;

@ApplicationScoped
@RegisteredCommand
public class UpdateClientCommand extends AbstractCommand {

    @Inject
    ClientService clientService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.UPDATE_CLIENT;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "clientId", "clientRepresentation"};
    }

    @Override
    public String getDescription() {
        return "Update an existing client";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        String realm = requireString(params, "realm");
        String clientId = requireString(params, "clientId");
        ClientRepresentation clientRep = extractObject(params, "clientRepresentation", ClientRepresentation.class);
        return clientService.updateClient(realm, clientId, clientRep);
    }
}
