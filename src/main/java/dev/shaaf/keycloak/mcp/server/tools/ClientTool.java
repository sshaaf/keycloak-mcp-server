package dev.shaaf.keycloak.mcp.server.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.shaaf.keycloak.mcp.server.service.ClientService;
import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolArg;
import io.quarkiverse.mcp.server.ToolCallException;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.ProtocolMapperRepresentation;

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
                     @ToolArg(description = "A String denoting the clientId of the client to be created") String clientId) {
        return clientService.createClient(realm, clientId);
    }


    // CONSIDER: this really feels like an overkill. UX is bad when its json in this context.
    // the issue or maybe not; is that there are alot of fields in ClientRepresentation.
    @Tool(description = "Update a client in a keycloak real using its client json")
    String updateClient(@ToolArg(description = "A String denoting the name of the realm where the client resides") String realm,
                       @ToolArg(description = "A String denoting the ID of the client to update") String clientId,
                       @ToolArg(description = "A String denoting the updated client representation in JSON format") String clientJson) {
        try {
            ClientRepresentation clientRepresentation = mapper.readValue(clientJson, ClientRepresentation.class);
            return clientService.updateClient(realm, clientId, clientRepresentation);
        } catch (Exception e) {
            Log.error("Failed to update client: " + clientId, e);
            throw new ToolCallException("Failed to update client: " + clientId + " - " + e.getMessage());
        }
    }

    @Tool(description = "Delete a client from a keycloak realm")
    String deleteClient(@ToolArg(description = "A String denoting the name of the realm where the client resides") String realm,
                       @ToolArg(description = "A String denoting the ID of the client to delete") String clientId) {
        return clientService.deleteClient(realm, clientId);
    }

    @Tool(description = "Get client secret in a keycloak realm")
    String getClientSecret(@ToolArg(description = "A String denoting the name of the realm where the client resides") String realm,
                          @ToolArg(description = "A String denoting the ID of the client") String clientId) {
        return clientService.getClientSecret(realm, clientId);
    }

    @Tool(description = "Generate new client secret from a keycloak realm")
    String generateNewClientSecret(@ToolArg(description = "A String denoting the name of the realm where the client resides") String realm,
                                 @ToolArg(description = "A String denoting the ID of the client") String clientId) {
        return clientService.generateNewClientSecret(realm, clientId);
    }

    @Tool(description = "Get client roles from a keycloak realm")
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

    @Tool(description = "Get service account user from a keycloak realm")
    String getServiceAccountUser(@ToolArg(description = "A String denoting the name of the realm where the client resides") String realm,
                               @ToolArg(description = "A String denoting the ID of the client") String clientId) {
        try {
            return mapper.writeValueAsString(clientService.getServiceAccountUser(realm, clientId));
        } catch (Exception e) {
            Log.error("Failed to get service account user: " + clientId, e);
            throw new ToolCallException("Failed to get service account user: " + clientId);
        }
    }

    @Tool(description = "Get client protocol mappers from a keycloak realm")
    String getClientProtocolMappers(@ToolArg(description = "A String denoting the name of the realm where the client resides") String realm,
                                  @ToolArg(description = "A String denoting the ID of the client") String clientId) {
        try {
            return mapper.writeValueAsString(clientService.getClientProtocolMappers(realm, clientId));
        } catch (Exception e) {
            Log.error("Failed to get client protocol mappers: " + clientId, e);
            throw new ToolCallException("Failed to get client protocol mappers: " + clientId);
        }
    }

    @Tool(description = "Add protocol mapper to client from a keycloak realm")
    String addProtocolMapperToClient(@ToolArg(description = "A String denoting the name of the realm where the client resides") String realm,
                                   @ToolArg(description = "A String denoting the ID of the client") String clientId,
                                   @ToolArg(description = "A String denoting the protocol mapper representation in JSON format") String mapperJson) {
        try {
            ProtocolMapperRepresentation protocolMapper = mapper.readValue(mapperJson, ProtocolMapperRepresentation.class);
            return clientService.addProtocolMapperToClient(realm, clientId, protocolMapper);
        } catch (Exception e) {
            Log.error("Failed to add protocol mapper to client: " + clientId, e);
            throw new ToolCallException("Failed to add protocol mapper to client: " + clientId + " - " + e.getMessage());
        }
    }
}
