package dev.shaaf.keycloak.mcp.server.commands.user;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import dev.shaaf.keycloak.mcp.server.commands.AbstractCommand;
import dev.shaaf.keycloak.mcp.server.commands.RegisteredCommand;
import dev.shaaf.keycloak.mcp.server.user.UserService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.keycloak.representations.idm.UserRepresentation;

@ApplicationScoped
@RegisteredCommand
public class UpdateUserCommand extends AbstractCommand {

    @Inject
    UserService userService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.UPDATE_USER;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "userId", "userRepresentation"};
    }

    @Override
    public String getDescription() {
        return "Update an existing user";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        String realm = requireString(params, "realm");
        String userId = requireString(params, "userId");
        UserRepresentation userRep = extractObject(params, "userRepresentation", UserRepresentation.class);
        return userService.updateUser(realm, userId, userRep);
    }
}

