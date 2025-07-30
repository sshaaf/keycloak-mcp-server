package dev.shaaf.keycloak.mcp.server.service;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.RealmEventsConfigRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;

import java.util.List;

@ApplicationScoped
public class RealmService {

    @Inject
    Keycloak keycloak;

    /**
     * Get all realms
     * @return List of all realm representations
     */
    public List<RealmRepresentation> getRealms() {
        return keycloak.realms().findAll();
    }

    /**
     * Get a specific realm by name
     * @param realmName The name of the realm to retrieve
     * @return The realm representation or null if not found
     */
    public RealmRepresentation getRealm(String realmName) {
        try {
            return keycloak.realm(realmName).toRepresentation();
        } catch (NotFoundException e) {
            Log.error("Realm not found: " + realmName, e);
            return null;
        }
    }

    /**
     * Create a new realm
     * @param realmName The name of the realm to create
     * @param displayName The display name for the realm
     * @param enabled Whether the realm should be enabled
     * @return Success or error message
     */
    public String createRealm(String realmName, String displayName, boolean enabled) {
        RealmRepresentation realm = new RealmRepresentation();
        realm.setRealm(realmName);
        realm.setDisplayName(displayName);
        realm.setEnabled(enabled);
        
        try {
            keycloak.realms().create(realm);
            return "Successfully created realm: " + realmName;
        } catch (Exception e) {
            Log.error("Exception creating realm: " + realmName, e);
            return "Error creating realm: " + realmName + " - " + e.getMessage();
        }
    }

    /**
     * Update a realm
     * @param realmName The name of the realm to update
     * @param realmRepresentation The updated realm representation
     * @return Success or error message
     */
    public String updateRealm(String realmName, RealmRepresentation realmRepresentation) {
        try {
            keycloak.realm(realmName).update(realmRepresentation);
            return "Successfully updated realm: " + realmName;
        } catch (Exception e) {
            Log.error("Failed to update realm: " + realmName, e);
            return "Error updating realm: " + realmName + " - " + e.getMessage();
        }
    }

    /**
     * Delete a realm
     * @param realmName The name of the realm to delete
     * @return Success or error message
     */
    public String deleteRealm(String realmName) {
        try {
            // Check if realm exists before attempting to delete
            RealmRepresentation realm = getRealm(realmName);
            if (realm == null) {
                return "Realm not found: " + realmName;
            }
            
            // The remove() method returns void, so we rely on exception handling
            keycloak.realms().realm(realmName).remove();
            return "Successfully deleted realm: " + realmName;
        } catch (NotFoundException e) {
            return "Realm not found: " + realmName;
        } catch (Exception e) {
            Log.error("Exception deleting realm: " + realmName, e);
            return "Error deleting realm: " + realmName + " - " + e.getMessage();
        }
    }

    /**
     * Enable or disable a realm
     * @param realmName The name of the realm to update
     * @param enabled Whether the realm should be enabled
     * @return Success or error message
     */
    public String setRealmEnabled(String realmName, boolean enabled) {
        try {
            RealmResource realmResource = keycloak.realm(realmName);
            RealmRepresentation realm = realmResource.toRepresentation();
            realm.setEnabled(enabled);
            realmResource.update(realm);
            return "Successfully " + (enabled ? "enabled" : "disabled") + " realm: " + realmName;
        } catch (NotFoundException e) {
            return "Realm not found: " + realmName;
        } catch (Exception e) {
            Log.error("Failed to " + (enabled ? "enable" : "disable") + " realm: " + realmName, e);
            return "Error " + (enabled ? "enabling" : "disabling") + " realm: " + realmName + " - " + e.getMessage();
        }
    }

    /**
     * Get realm events configuration
     * @param realmName The name of the realm
     * @return The realm events configuration or null if not found
     */
    public RealmEventsConfigRepresentation getRealmEventsConfig(String realmName) {
        try {
            return keycloak.realm(realmName).getRealmEventsConfig();
        } catch (NotFoundException e) {
            Log.error("Realm not found: " + realmName, e);
            return null;
        } catch (Exception e) {
            Log.error("Failed to get realm events config: " + realmName, e);
            return null;
        }
    }

    /**
     * Update realm events configuration
     * @param realmName The name of the realm
     * @param eventsConfig The updated events configuration
     * @return Success or error message
     */
    public String updateRealmEventsConfig(String realmName, RealmEventsConfigRepresentation eventsConfig) {
        try {
            keycloak.realm(realmName).updateRealmEventsConfig(eventsConfig);
            return "Successfully updated realm events config: " + realmName;
        } catch (NotFoundException e) {
            return "Realm not found: " + realmName;
        } catch (Exception e) {
            Log.error("Failed to update realm events config: " + realmName, e);
            return "Error updating realm events config: " + realmName + " - " + e.getMessage();
        }
    }
}
