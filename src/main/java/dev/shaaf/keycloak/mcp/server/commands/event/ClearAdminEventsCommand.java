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
public class ClearAdminEventsCommand extends AbstractCommand {

    @Inject
    EventService eventService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.CLEAR_ADMIN_EVENTS;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm"};
    }

    @Override
    public String getDescription() {
        return "Clear the admin event store for the realm";
    }

    @Override
    public String execute(JsonNode params) {
        return eventService.clearAdminEvents(requireString(params, "realm"));
    }
}
