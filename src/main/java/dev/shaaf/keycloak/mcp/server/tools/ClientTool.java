package dev.shaaf.keycloak.mcp.server.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.shaaf.keycloak.mcp.server.service.ClientService;
import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolArg;
import io.quarkiverse.mcp.server.ToolCallException;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import org.keycloak.representations.idm.ClientRepresentation;

import java.util.Optional;

public class ClientTool {

    @Inject
    ClientService clientService;

    @Inject
    ObjectMapper mapper;

    @Tool(description = "Get all clients from a keycloak realm")
    String getClients(@ToolArg(description = "A String denoting the name of the realm where the clients reside") String realm) {
        try {
            return mapper.writeValueAsString(clientService.getClients(realm));
        } catch (Exception e) {
            Log.error("Unable to get clients from realm", e);
            throw new ToolCallException("Unable to get clients from realm");
        }
    }

    @Tool(description = "Get a client by its ID in a keycloak realm")
    String getClient(@ToolArg(description = "A String denoting the name of the realm where the client resides") String realm,
                     @ToolArg(description = "A String denoting the clientId of the client to be retrieved") String clientId) {
        try {
            Optional<ClientRepresentation> client = clientService.findClientByClientId(realm, clientId);
            return mapper.writeValueAsString(client.orElse(null));
        } catch (Exception e) {
            Log.error("Failed to get client by client ID: " + clientId, e);
            throw new ToolCallException("Failed to get client by client ID: " + clientId);
        }
    }

    @Tool(description = "Create a new client in a keycloak realm")
    String addClient(@ToolArg(description = "A String denoting the name of the realm where the client resides") String realm,
                     @ToolArg(description = "A String denoting the clientId of the client to be created") String clientId,
                     @ToolArg(description = "A String denoting the redirect uri for the client") String redirectUris) {
        return clientService.createClient(realm, clientId, redirectUris);
    }


    @Tool(description = "Delete a client in a keycloak realm")
    String deleteClient(@ToolArg(description = "A String denoting the name of the realm where the client resides") String realm,
                       @ToolArg(description = "A String denoting the ID of the client to delete") String clientId) {
        return clientService.deleteClient(realm, clientId);
    }

    @Tool(description = "Generate new client secret for a client in a keycloak realm")
    String generateNewClientSecret(@ToolArg(description = "A String denoting the name of the realm where the client resides") String realm,
                                 @ToolArg(description = "A String denoting the ID of the client") String clientId) {
        return clientService.generateNewClientSecret(realm, clientId);
    }

    @Tool(description = "Get client roles in a keycloak realm")
    String getClientRoles(@ToolArg(description = "A String denoting the name of the realm where the client resides") String realm,
                         @ToolArg(description = "A String denoting the ID of the client") String clientId) {
        try {
            return mapper.writeValueAsString(clientService.getClientRoles(realm, clientId));
        } catch (Exception e) {
            Log.error("Failed to get client roles: " + clientId, e);
            throw new ToolCallException("Failed to get client roles: " + clientId);
        }
    }

    @Tool(description = "Create client role from a keycloak realm")
    String createClientRole(@ToolArg(description = "A String denoting the name of the realm where the client resides") String realm,
                           @ToolArg(description = "A String denoting the ID of the client") String clientId,
                           @ToolArg(description = "A String denoting the name of the role") String roleName,
                           @ToolArg(description = "A String denoting the description of the role") String description) {
        return clientService.createClientRole(realm, clientId, roleName, description);
    }

    @Tool(description = "Delete client role from a keycloak realm")
    String deleteClientRole(@ToolArg(description = "A String denoting the name of the realm where the client resides") String realm,
                           @ToolArg(description = "A String denoting the ID of the client") String clientId,
                           @ToolArg(description = "A String denoting the name of the role") String roleName) {
        return clientService.deleteClientRole(realm, clientId, roleName);
    }
}
