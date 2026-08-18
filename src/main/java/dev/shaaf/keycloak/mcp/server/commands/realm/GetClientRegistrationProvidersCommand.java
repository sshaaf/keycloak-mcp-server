package dev.shaaf.keycloak.mcp.server.commands.realm;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import dev.shaaf.keycloak.mcp.server.commands.AbstractCommand;
import dev.shaaf.keycloak.mcp.server.commands.RegisteredCommand;
import dev.shaaf.keycloak.mcp.server.realm.RealmService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.keycloak.representations.idm.ComponentTypeRepresentation;

import java.util.List;

@ApplicationScoped
@RegisteredCommand
public class GetClientRegistrationProvidersCommand extends AbstractCommand {

    @Inject
    RealmService realmService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.GET_CLIENT_REGISTRATION_PROVIDERS;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm"};
    }

    @Override
    public String getDescription() {
        return "List client registration policy component types (providers) for the realm";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        List<ComponentTypeRepresentation> list = realmService.getClientRegistrationProviders(
                requireString(params, "realm"));
        return toJson(list);
    }
}
