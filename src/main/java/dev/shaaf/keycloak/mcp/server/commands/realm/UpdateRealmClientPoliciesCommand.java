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
public class UpdateRealmClientPoliciesCommand extends AbstractCommand {

    @Inject
    RealmService realmService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.UPDATE_REALM_CLIENT_POLICIES;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "policies"};
    }

    @Override
    public String getDescription() {
        return "Update realm client policies JSON (ClientPoliciesRepresentation in policies)";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        return realmService.updateRealmClientPolicies(
                requireString(params, "realm"),
                extractObject(params, "policies", ClientPoliciesRepresentation.class));
    }
}
