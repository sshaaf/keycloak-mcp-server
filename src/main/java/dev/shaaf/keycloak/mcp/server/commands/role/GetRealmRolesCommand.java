package dev.shaaf.keycloak.mcp.server.commands.role;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import dev.shaaf.keycloak.mcp.server.commands.AbstractCommand;
import dev.shaaf.keycloak.mcp.server.commands.RegisteredCommand;
import dev.shaaf.keycloak.mcp.server.role.RoleService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
@RegisteredCommand
public class GetRealmRolesCommand extends AbstractCommand {

    @Inject
    RoleService roleService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.GET_REALM_ROLES;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm"};
    }

    @Override
    public String getDescription() {
        return "Get all realm-level roles";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        String realm = requireString(params, "realm");
        return toJson(roleService.getRealmRoles(realm));
    }
}

