package dev.shaaf.keycloak.mcp.server.event;

import dev.shaaf.keycloak.mcp.server.KeycloakClientFactory;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.AdminEventRepresentation;
import org.keycloak.representations.idm.EventRepresentation;

import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class EventService {

    @Inject
    KeycloakClientFactory clientFactory;

    public List<AdminEventRepresentation> getAdminEvents(String realm) {
        Keycloak keycloak = clientFactory.createClient();
        try {
            return keycloak.realm(realm).getAdminEvents();
        } catch (Exception e) {
            Log.error("Failed to get admin events: " + realm, e);
            return Collections.emptyList();
        }
    }

    public List<EventRepresentation> getUserEvents(String realm) {
        Keycloak keycloak = clientFactory.createClient();
        try {
            return keycloak.realm(realm).getEvents();
        } catch (Exception e) {
            Log.error("Failed to get user events: " + realm, e);
            return Collections.emptyList();
        }
    }

    public String clearAdminEvents(String realm) {
        Keycloak keycloak = clientFactory.createClient();
        try {
            keycloak.realm(realm).clearAdminEvents();
            return "Successfully cleared admin events for realm: " + realm;
        } catch (Exception e) {
            Log.error("Failed to clear admin events: " + realm, e);
            return "Error clearing admin events: " + realm + " - " + e.getMessage();
        }
    }

    public String clearUserEvents(String realm) {
        Keycloak keycloak = clientFactory.createClient();
        try {
            keycloak.realm(realm).clearEvents();
            return "Successfully cleared user events for realm: " + realm;
        } catch (Exception e) {
            Log.error("Failed to clear user events: " + realm, e);
            return "Error clearing user events: " + realm + " - " + e.getMessage();
        }
    }
}
