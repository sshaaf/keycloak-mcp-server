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
public class SendVerificationEmailCommand extends AbstractCommand {

    @Inject
    UserService userService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.SEND_VERIFICATION_EMAIL;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "userId"};
    }

    @Override
    public String getDescription() {
        return "Send email verification to a user";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        return userService.sendVerificationEmail(
                requireString(params, "realm"),
                requireString(params, "userId")
        );
    }
}

