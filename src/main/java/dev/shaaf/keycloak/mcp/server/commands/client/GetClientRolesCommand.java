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
public class GetClientRolesCommand extends AbstractCommand {

    @Inject
    ClientService clientService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.GET_CLIENT_ROLES;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "clientId"};
    }

    @Override
    public String getDescription() {
        return "Get roles defined for a client";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        String realm = requireString(params, "realm");
        String clientId = requireString(params, "clientId");
        return toJson(clientService.getClientRoles(realm, clientId));
    }
}

