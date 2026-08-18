package dev.shaaf.keycloak.mcp.server.commands.realm;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import dev.shaaf.keycloak.mcp.server.commands.AbstractCommand;
import dev.shaaf.keycloak.mcp.server.commands.RegisteredCommand;
import dev.shaaf.keycloak.mcp.server.realm.RealmService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.keycloak.representations.idm.ClientProfilesRepresentation;

@ApplicationScoped
@RegisteredCommand
public class UpdateRealmClientProfilesCommand extends AbstractCommand {

    @Inject
    RealmService realmService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.UPDATE_REALM_CLIENT_PROFILES;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "profiles"};
    }

    @Override
    public String getDescription() {
        return "Update realm client profiles (ClientProfilesRepresentation in profiles)";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        return realmService.updateRealmClientProfiles(
                requireString(params, "realm"),
                extractObject(params, "profiles", ClientProfilesRepresentation.class));
    }
}
