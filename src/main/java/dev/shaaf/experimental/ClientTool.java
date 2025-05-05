package dev.shaaf.experimental;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.shaaf.experimental.service.ClientService;
import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolArg;
import io.quarkiverse.mcp.server.ToolCallException;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;

public class ClientTool {

    @Inject
    ClientService clientService;

    @Inject
    ObjectMapper mapper;

    @Tool(description = "Get all clients from a realm")
    String getClients(@ToolArg(description = "A String denoting the name of the realm where the clients reside") String realm) {
        try {
            return mapper.writeValueAsString(clientService.getClients(realm));
        } catch (Exception e) {
            Log.error("unable to get clients from realm", e);
            throw new ToolCallException("unable to get clients from realm");
        }
    }

    @Tool(description = "Get a client")
    String getClient(@ToolArg(description = "A String denoting the name of the realm where the clients reside") String realm,
                     @ToolArg(description = "A String denoting the clientId of the client to be retrieved") String clientId) {
        return String.valueOf(clientService.findClientByClientId(realm, clientId));
    }

    @Tool(description = "Create a new client in a realm")
    String addClient(@ToolArg(description = "A String denoting the name of the realm where the client resides") String realm,
                     @ToolArg(description = "A String denoting the clientId of the client to be created") String clientId) {
        return clientService.createClient(realm, clientId);
    }

}
