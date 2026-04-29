package dev.shaaf.keycloak.mcp.server.commands.role;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import dev.shaaf.keycloak.mcp.server.commands.AbstractCommand;
import dev.shaaf.keycloak.mcp.server.commands.RegisteredCommand;
import dev.shaaf.keycloak.mcp.server.role.RoleService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.keycloak.representations.idm.RoleRepresentation;

@ApplicationScoped
@RegisteredCommand
public class UpdateRealmRoleCommand extends AbstractCommand {

    @Inject
    RoleService roleService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.UPDATE_REALM_ROLE;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "roleName", "roleRepresentation"};
    }

    @Override
    public String getDescription() {
        return "Update an existing realm role";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        String realm = requireString(params, "realm");
        String roleName = requireString(params, "roleName");
        RoleRepresentation roleRep = extractObject(params, "roleRepresentation", RoleRepresentation.class);
        return roleService.updateRealmRole(realm, roleName, roleRep);
    }
}
