package dev.shaaf.keycloak.mcp.server.commands.user;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import dev.shaaf.keycloak.mcp.server.commands.AbstractCommand;
import dev.shaaf.keycloak.mcp.server.commands.RegisteredCommand;
import dev.shaaf.keycloak.mcp.server.user.UserService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
@RegisteredCommand
public class ResetPasswordCommand extends AbstractCommand {

    @Inject
    UserService userService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.RESET_PASSWORD;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "userId", "newPassword"};
    }

    @Override
    public String getDescription() {
        return "Reset a user's password";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        return userService.resetPassword(
                requireString(params, "realm"),
                requireString(params, "userId"),
                requireString(params, "newPassword"),
                optionalBoolean(params, "temporary", false)
        );
    }
}

