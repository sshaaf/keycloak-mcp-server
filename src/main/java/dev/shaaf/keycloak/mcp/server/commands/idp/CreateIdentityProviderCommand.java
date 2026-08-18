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
public class CreateIdentityProviderCommand extends AbstractCommand {

    @Inject
    IdentityProviderService identityProviderService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.CREATE_IDENTITY_PROVIDER;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "identityProvider"};
    }

    @Override
    public String getDescription() {
        return "Create a new identity provider";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        String realm = requireString(params, "realm");
        IdentityProviderRepresentation identityProvider = extractObject(params, "identityProvider", IdentityProviderRepresentation.class);
        return identityProviderService.createIdentityProvider(realm, identityProvider);
    }
}
