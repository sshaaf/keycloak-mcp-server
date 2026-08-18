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
public class GenerateClientSecretCommand extends AbstractCommand {

    @Inject
    ClientService clientService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.GENERATE_CLIENT_SECRET;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "clientId"};
    }

    @Override
    public String getDescription() {
        return "Generate a new client secret";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        return clientService.generateNewClientSecret(
                requireString(params, "realm"),
                requireString(params, "clientId")
        );
    }
}

