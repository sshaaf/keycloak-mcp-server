package dev.shaaf.keycloak.mcp.server.requiredaction;

import dev.shaaf.keycloak.mcp.server.KeycloakClientFactory;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.RequiredActionProviderRepresentation;

import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class RequiredActionService {

    @Inject
    KeycloakClientFactory clientFactory;

    public List<RequiredActionProviderRepresentation> getRequiredActions(String realm) {
        Keycloak keycloak = clientFactory.createClient();
        try {
            return keycloak.realm(realm).flows().getRequiredActions();
        } catch (Exception e) {
            Log.error("Failed to get required actions: " + realm, e);
            return Collections.emptyList();
        }
    }

    public RequiredActionProviderRepresentation getRequiredAction(String realm, String alias) {
        Keycloak keycloak = clientFactory.createClient();
        try {
            return keycloak.realm(realm).flows().getRequiredAction(alias);
        } catch (NotFoundException e) {
            Log.error("Required action not found: " + alias, e);
            return null;
        } catch (Exception e) {
            Log.error("Failed to get required action: " + alias, e);
            return null;
        }
    }

    public String updateRequiredAction(String realm, String alias, RequiredActionProviderRepresentation rep) {
        Keycloak keycloak = clientFactory.createClient();
        try {
            keycloak.realm(realm).flows().updateRequiredAction(alias, rep);
            return "Successfully updated required action: " + alias;
        } catch (NotFoundException e) {
            return "Required action not found: " + alias;
        } catch (Exception e) {
            Log.error("Failed to update required action: " + alias, e);
            return "Error updating required action: " + alias + " - " + e.getMessage();
        }
    }

    public String executeActionsEmail(String realm, String userId, List<String> actions) {
        Keycloak keycloak = clientFactory.createClient();
        try {
            keycloak.realm(realm).users().get(userId).executeActionsEmail(actions);
            return "Successfully sent actions email to user: " + userId;
        } catch (NotFoundException e) {
            return "User not found: " + userId;
        } catch (Exception e) {
            Log.error("Failed to send actions email: " + userId, e);
            return "Error sending actions email: " + userId + " - " + e.getMessage();
        }
    }
}
