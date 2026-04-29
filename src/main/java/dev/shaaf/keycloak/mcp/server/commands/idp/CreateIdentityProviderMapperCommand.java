package dev.shaaf.keycloak.mcp.server.commands.idp;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import dev.shaaf.keycloak.mcp.server.commands.AbstractCommand;
import dev.shaaf.keycloak.mcp.server.commands.RegisteredCommand;
import dev.shaaf.keycloak.mcp.server.idp.IdentityProviderService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.keycloak.representations.idm.IdentityProviderMapperRepresentation;

@ApplicationScoped
@RegisteredCommand
public class CreateIdentityProviderMapperCommand extends AbstractCommand {

    @Inject
    IdentityProviderService identityProviderService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.CREATE_IDENTITY_PROVIDER_MAPPER;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "alias", "mapper"};
    }

    @Override
    public String getDescription() {
        return "Create an identity provider mapper";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        String realm = requireString(params, "realm");
        String alias = requireString(params, "alias");
        IdentityProviderMapperRepresentation mapper = extractObject(params, "mapper", IdentityProviderMapperRepresentation.class);
        return identityProviderService.createIdentityProviderMapper(realm, alias, mapper);
    }
}
