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
public class DeleteClientRoleCommand extends AbstractCommand {

    @Inject
    ClientService clientService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.DELETE_CLIENT_ROLE;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "clientId", "roleName"};
    }

    @Override
    public String getDescription() {
        return "Delete a role from a client";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        return clientService.deleteClientRole(
                requireString(params, "realm"),
                requireString(params, "clientId"),
                requireString(params, "roleName")
        );
    }
}

