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
public class CreateRealmCommand extends AbstractCommand {

    @Inject
    RealmService realmService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.CREATE_REALM;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realmName", "displayName"};
    }

    @Override
    public String getDescription() {
        return "Create a new realm";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        return realmService.createRealm(
                requireString(params, "realmName"),
                requireString(params, "displayName"),
                optionalBoolean(params, "enabled", true)
        );
    }
}

