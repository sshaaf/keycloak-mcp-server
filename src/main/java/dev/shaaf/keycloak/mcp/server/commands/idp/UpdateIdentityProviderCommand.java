package dev.shaaf.keycloak.mcp.server.commands.idp;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import dev.shaaf.keycloak.mcp.server.commands.AbstractCommand;
import dev.shaaf.keycloak.mcp.server.commands.RegisteredCommand;
import dev.shaaf.keycloak.mcp.server.idp.IdentityProviderService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.keycloak.representations.idm.IdentityProviderRepresentation;

@ApplicationScoped
@RegisteredCommand
public class UpdateIdentityProviderCommand extends AbstractCommand {

    @Inject
    IdentityProviderService identityProviderService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.UPDATE_IDENTITY_PROVIDER;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "alias", "identityProvider"};
    }

    @Override
    public String getDescription() {
        return "Update an existing identity provider";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        String realm = requireString(params, "realm");
        String alias = requireString(params, "alias");
        IdentityProviderRepresentation identityProvider = extractObject(params, "identityProvider", IdentityProviderRepresentation.class);
        return identityProviderService.updateIdentityProvider(realm, alias, identityProvider);
    }
}
