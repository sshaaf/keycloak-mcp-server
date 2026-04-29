package dev.shaaf.keycloak.mcp.server.commands.credential;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import dev.shaaf.keycloak.mcp.server.commands.AbstractCommand;
import dev.shaaf.keycloak.mcp.server.commands.RegisteredCommand;
import dev.shaaf.keycloak.mcp.server.credential.CredentialService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
@RegisteredCommand
public class MoveUserCredentialCommand extends AbstractCommand {

    @Inject
    CredentialService credentialService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.MOVE_USER_CREDENTIAL;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "userId", "credentialId", "newPosition"};
    }

    @Override
    public String getDescription() {
        return "Change credential order (0-based index; uses move-to-first/after on the server)";
    }

    @Override
    public String execute(JsonNode params) {
        int pos = params.get("newPosition").asInt();
        return credentialService.moveCredentialToPosition(
                requireString(params, "realm"),
                requireString(params, "userId"),
                requireString(params, "credentialId"),
                pos);
    }
}
