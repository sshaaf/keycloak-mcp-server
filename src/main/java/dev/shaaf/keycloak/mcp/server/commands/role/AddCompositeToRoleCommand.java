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
public class AddCompositeToRoleCommand extends AbstractCommand {

    @Inject
    RoleService roleService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.ADD_COMPOSITE_TO_ROLE;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "roleName", "compositeRoleName"};
    }

    @Override
    public String getDescription() {
        return "Add a composite role to a realm role";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        String realm = requireString(params, "realm");
        String roleName = requireString(params, "roleName");
        String compositeRoleName = requireString(params, "compositeRoleName");
        return roleService.addCompositeToRole(realm, roleName, compositeRoleName);
    }
}
