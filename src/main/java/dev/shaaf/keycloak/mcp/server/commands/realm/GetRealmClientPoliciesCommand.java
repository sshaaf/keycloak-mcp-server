package dev.shaaf.keycloak.mcp.server.commands.realm;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import dev.shaaf.keycloak.mcp.server.commands.AbstractCommand;
import dev.shaaf.keycloak.mcp.server.commands.RegisteredCommand;
import dev.shaaf.keycloak.mcp.server.realm.RealmService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.keycloak.representations.idm.ClientPoliciesRepresentation;

@ApplicationScoped
@RegisteredCommand
public class GetRealmClientPoliciesCommand extends AbstractCommand {

    @Inject
    RealmService realmService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.GET_REALM_CLIENT_POLICIES;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm"};
    }

    @Override
    public String getDescription() {
        return "Get realm client policies (includeGlobal optional, default true)";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        boolean include = optionalBoolean(params, "includeGlobal", true);
        ClientPoliciesRepresentation r = realmService.getRealmClientPolicies(
                requireString(params, "realm"), include);
        if (r == null) {
            return "null";
        }
        return toJson(r);
    }
}
