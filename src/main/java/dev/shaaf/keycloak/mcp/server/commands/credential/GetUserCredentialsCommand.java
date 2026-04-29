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
public class GetUserCredentialsCommand extends AbstractCommand {

    @Inject
    CredentialService credentialService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.GET_USER_CREDENTIALS;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "userId"};
    }

    @Override
    public String getDescription() {
        return "List credentials (password, OTP, webauthn, etc.) for a user";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        return toJson(credentialService.getUserCredentials(
                requireString(params, "realm"), requireString(params, "userId")));
    }
}
