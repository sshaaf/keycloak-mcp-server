package dev.shaaf.keycloak.mcp.server.commands.realm;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import dev.shaaf.keycloak.mcp.server.commands.AbstractCommand;
import dev.shaaf.keycloak.mcp.server.commands.RegisteredCommand;
import dev.shaaf.keycloak.mcp.server.realm.RealmService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.keycloak.representations.idm.RealmRepresentation;

@ApplicationScoped
@RegisteredCommand
public class UpdateRealmCommand extends AbstractCommand {

    @Inject
    RealmService realmService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.UPDATE_REALM;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realmName", "realmRepresentation"};
    }

    @Override
    public String getDescription() {
        return "Update an existing realm";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        String realmName = requireString(params, "realmName");
        RealmRepresentation realmRep = extractObject(params, "realmRepresentation", RealmRepresentation.class);
        return realmService.updateRealm(realmName, realmRep);
    }
}
