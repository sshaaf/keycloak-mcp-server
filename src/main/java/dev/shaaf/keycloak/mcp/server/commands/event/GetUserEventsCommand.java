package dev.shaaf.keycloak.mcp.server.commands.event;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import dev.shaaf.keycloak.mcp.server.commands.AbstractCommand;
import dev.shaaf.keycloak.mcp.server.commands.RegisteredCommand;
import dev.shaaf.keycloak.mcp.server.event.EventService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
@RegisteredCommand
public class GetUserEventsCommand extends AbstractCommand {

    @Inject
    EventService eventService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.GET_USER_EVENTS;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm"};
    }

    @Override
    public String getDescription() {
        return "List stored user login events for the realm";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        return toJson(eventService.getUserEvents(requireString(params, "realm")));
    }
}
