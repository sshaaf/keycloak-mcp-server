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
public class SetRealmEnabledCommand extends AbstractCommand {

    @Inject
    RealmService realmService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.SET_REALM_ENABLED;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realmName", "enabled"};
    }

    @Override
    public String getDescription() {
        return "Enable or disable a realm";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        String realmName = requireString(params, "realmName");
        boolean enabled = requireBoolean(params, "enabled");
        return realmService.setRealmEnabled(realmName, enabled);
    }
}
