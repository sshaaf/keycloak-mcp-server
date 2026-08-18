package dev.shaaf.keycloak.mcp.server.key;

import dev.shaaf.keycloak.mcp.server.KeycloakClientFactory;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.KeysMetadataRepresentation;

@ApplicationScoped
public class KeyService {

    @Inject
    KeycloakClientFactory clientFactory;

    public KeysMetadataRepresentation getRealmKeys(String realm) {
        Keycloak keycloak = clientFactory.createClient();
        try {
            return keycloak.realm(realm).keys().getKeyMetadata();
        } catch (Exception e) {
            Log.error("Failed to get realm keys: " + realm, e);
            return null;
        }
    }
}
