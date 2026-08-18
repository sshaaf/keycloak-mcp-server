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
public class DeleteUserCredentialCommand extends AbstractCommand {

    @Inject
    CredentialService credentialService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.DELETE_USER_CREDENTIAL;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "userId", "credentialId"};
    }

    @Override
    public String getDescription() {
        return "Delete a user credential by id";
    }

    @Override
    public String execute(JsonNode params) {
        return credentialService.deleteUserCredential(
                requireString(params, "realm"),
                requireString(params, "userId"),
                requireString(params, "credentialId"));
    }
}
