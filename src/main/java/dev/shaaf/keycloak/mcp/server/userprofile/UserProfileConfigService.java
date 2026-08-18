package dev.shaaf.keycloak.mcp.server.userprofile;

import dev.shaaf.keycloak.mcp.server.KeycloakClientFactory;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.userprofile.config.UPConfig;

@ApplicationScoped
public class UserProfileConfigService {

    @Inject
    KeycloakClientFactory clientFactory;

    public UPConfig getConfiguration(String realm) {
        try {
            Keycloak k = clientFactory.createClient();
            return k.realm(realm).users().userProfile().getConfiguration();
        } catch (Exception e) {
            Log.error("getConfiguration user profile " + realm, e);
            return null;
        }
    }

    public String updateConfiguration(String realm, UPConfig config) {
        try {
            Keycloak k = clientFactory.createClient();
            k.realm(realm).users().userProfile().update(config);
            return "OK";
        } catch (Exception e) {
            Log.error("updateConfiguration user profile " + realm, e);
            return "Error: " + e.getMessage();
        }
    }
}
