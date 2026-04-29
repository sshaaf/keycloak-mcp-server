package dev.shaaf.keycloak.mcp.server.commands.key;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import dev.shaaf.keycloak.mcp.server.commands.AbstractCommand;
import dev.shaaf.keycloak.mcp.server.commands.RegisteredCommand;
import dev.shaaf.keycloak.mcp.server.key.KeyService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.keycloak.representations.idm.KeysMetadataRepresentation;

@ApplicationScoped
@RegisteredCommand
public class GetRealmKeysCommand extends AbstractCommand {

    @Inject
    KeyService keyService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.GET_REALM_KEYS;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm"};
    }

    @Override
    public String getDescription() {
        return "Get realm key metadata (signing, encryption, etc.)";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        KeysMetadataRepresentation keys = keyService.getRealmKeys(requireString(params, "realm"));
        if (keys == null) {
            return "null";
        }
        return toJson(keys);
    }
}
