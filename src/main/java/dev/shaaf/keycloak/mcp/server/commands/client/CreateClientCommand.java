package dev.shaaf.keycloak.mcp.server.commands.client;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import dev.shaaf.keycloak.mcp.server.client.ClientService;
import dev.shaaf.keycloak.mcp.server.commands.AbstractCommand;
import dev.shaaf.keycloak.mcp.server.commands.RegisteredCommand;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
@RegisteredCommand
public class CreateClientCommand extends AbstractCommand {

    @Inject
    ClientService clientService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.CREATE_CLIENT;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "clientId", "redirectUris"};
    }

    @Override
    public String getDescription() {
        return "Create a new client in a realm";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        return clientService.createClient(
                requireString(params, "realm"),
                requireString(params, "clientId"),
                requireString(params, "redirectUris")
        );
    }
}

