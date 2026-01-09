package dev.shaaf.keycloak.mcp.server.commands.realm;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import dev.shaaf.keycloak.mcp.server.commands.AbstractCommand;
import dev.shaaf.keycloak.mcp.server.commands.RegisteredCommand;
import dev.shaaf.keycloak.mcp.server.realm.RealmService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
@RegisteredCommand
public class GetRealmsCommand extends AbstractCommand {

    @Inject
    RealmService realmService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.GET_REALMS;
    }

    @Override
    public String getDescription() {
        return "List all realms accessible to the user";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        return toJson(realmService.getRealms());
    }
}

