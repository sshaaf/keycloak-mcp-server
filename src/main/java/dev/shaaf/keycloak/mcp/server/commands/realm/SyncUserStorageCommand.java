package dev.shaaf.keycloak.mcp.server.commands.realm;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import dev.shaaf.keycloak.mcp.server.commands.AbstractCommand;
import dev.shaaf.keycloak.mcp.server.commands.RegisteredCommand;
import dev.shaaf.keycloak.mcp.server.realm.RealmService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.keycloak.representations.idm.SynchronizationResultRepresentation;

@ApplicationScoped
@RegisteredCommand
public class SyncUserStorageCommand extends AbstractCommand {

    @Inject
    RealmService realmService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.SYNC_USER_STORAGE;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "userStorageId", "action"};
    }

    @Override
    public String getDescription() {
        return "Trigger user storage / LDAP sync (e.g. action=triggerFullSync; see Keycloak admin API)";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        SynchronizationResultRepresentation r = realmService.syncUserStorage(
                requireString(params, "realm"),
                requireString(params, "userStorageId"),
                requireString(params, "action"));
        if (r == null) {
            return "null";
        }
        return toJson(r);
    }
}
