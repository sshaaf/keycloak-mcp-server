package dev.shaaf.keycloak.mcp.server.commands.realm;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import dev.shaaf.keycloak.mcp.server.commands.AbstractCommand;
import dev.shaaf.keycloak.mcp.server.commands.RegisteredCommand;
import dev.shaaf.keycloak.mcp.server.realm.RealmService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.keycloak.representations.idm.RealmEventsConfigRepresentation;

@ApplicationScoped
@RegisteredCommand
public class UpdateRealmEventsConfigCommand extends AbstractCommand {

    @Inject
    RealmService realmService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.UPDATE_REALM_EVENTS_CONFIG;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realmName", "eventsConfig"};
    }

    @Override
    public String getDescription() {
        return "Update realm events configuration";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        String realmName = requireString(params, "realmName");
        RealmEventsConfigRepresentation eventsConfig = extractObject(params, "eventsConfig", RealmEventsConfigRepresentation.class);
        return realmService.updateRealmEventsConfig(realmName, eventsConfig);
    }
}
