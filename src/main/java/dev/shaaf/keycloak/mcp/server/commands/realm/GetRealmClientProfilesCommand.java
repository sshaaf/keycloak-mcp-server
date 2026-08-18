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
public class GetRealmClientProfilesCommand extends AbstractCommand {

    @Inject
    RealmService realmService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.GET_REALM_CLIENT_PROFILES;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm"};
    }

    @Override
    public String getDescription() {
        return "Get realm client profiles (includeGlobal optional, default true)";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        boolean include = optionalBoolean(params, "includeGlobal", true);
        ClientProfilesRepresentation r = realmService.getRealmClientProfiles(
                requireString(params, "realm"), include);
        if (r == null) {
            return "null";
        }
        return toJson(r);
    }
}
