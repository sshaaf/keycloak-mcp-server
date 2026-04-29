package dev.shaaf.keycloak.mcp.server.commands.realm;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import dev.shaaf.keycloak.mcp.server.commands.AbstractCommand;
import dev.shaaf.keycloak.mcp.server.commands.RegisteredCommand;
import dev.shaaf.keycloak.mcp.server.realm.RealmService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.keycloak.representations.adapters.action.GlobalRequestResult;

@ApplicationScoped
@RegisteredCommand
public class PushRealmRevocationCommand extends AbstractCommand {

    @Inject
    RealmService realmService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.PUSH_REALM_REVOCATION;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm"};
    }

    @Override
    public String getDescription() {
        return "Push a notBefore revocation to all cluster nodes (notBefore policy refresh)";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        GlobalRequestResult r = realmService.pushRealmRevocation(requireString(params, "realm"));
        if (r == null) {
            return "null";
        }
        return toJson(r);
    }
}
