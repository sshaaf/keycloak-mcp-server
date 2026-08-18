package dev.shaaf.keycloak.mcp.server.commands.idp;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import dev.shaaf.keycloak.mcp.server.commands.AbstractCommand;
import dev.shaaf.keycloak.mcp.server.commands.RegisteredCommand;
import dev.shaaf.keycloak.mcp.server.idp.IdentityProviderService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
@RegisteredCommand
public class DeleteIdentityProviderCommand extends AbstractCommand {

    @Inject
    IdentityProviderService identityProviderService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.DELETE_IDENTITY_PROVIDER;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "alias"};
    }

    @Override
    public String getDescription() {
        return "Delete an identity provider";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        String realm = requireString(params, "realm");
        String alias = requireString(params, "alias");
        return identityProviderService.deleteIdentityProvider(realm, alias);
    }
}
