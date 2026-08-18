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
public class GetIdentityProviderCommand extends AbstractCommand {

    @Inject
    IdentityProviderService identityProviderService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.GET_IDENTITY_PROVIDER;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "alias"};
    }

    @Override
    public String getDescription() {
        return "Get a specific identity provider by alias";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        String realm = requireString(params, "realm");
        String alias = requireString(params, "alias");
        return toJson(identityProviderService.getIdentityProvider(realm, alias));
    }
}

