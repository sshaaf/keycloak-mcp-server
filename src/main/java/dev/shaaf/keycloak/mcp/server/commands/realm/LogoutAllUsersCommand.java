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
public class LogoutAllUsersCommand extends AbstractCommand {

    @Inject
    RealmService realmService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.LOGOUT_ALL_USERS;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm"};
    }

    @Override
    public String getDescription() {
        return "Logout all users in the realm (stateful clients receive logout)";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        GlobalRequestResult r = realmService.logoutAllUsers(requireString(params, "realm"));
        if (r == null) {
            return "null";
        }
        return toJson(r);
    }
}
