package dev.shaaf.keycloak.mcp.server.realm;

import dev.shaaf.keycloak.mcp.server.KeycloakClientFactory;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.adapters.action.GlobalRequestResult;
import org.keycloak.representations.idm.ClientPoliciesRepresentation;
import org.keycloak.representations.idm.ClientProfilesRepresentation;
import org.keycloak.representations.idm.ComponentTypeRepresentation;
import org.keycloak.representations.idm.RealmEventsConfigRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.SynchronizationResultRepresentation;
import org.keycloak.representations.idm.TestLdapConnectionRepresentation;

import jakarta.ws.rs.core.Response;
import java.util.List;

@ApplicationScoped
public class RealmService {

    @Inject
    KeycloakClientFactory clientFactory;

    /**
     * Get all realms
     * @return List of all realm representations
     */
    public List<RealmRepresentation> getRealms() {
        Keycloak keycloak = clientFactory.createClient();
        return keycloak.realms().findAll();
    }

    /**
     * Get a specific realm by name
     * @param realmName The name of the realm to retrieve
     * @return The realm representation or null if not found
     */
    public RealmRepresentation getRealm(String realmName) {
        try {
            Keycloak keycloak = clientFactory.createClient();
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
            Keycloak keycloak = clientFactory.createClient();
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
            Keycloak keycloak = clientFactory.createClient();
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
            
            Keycloak keycloak = clientFactory.createClient();
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
            Keycloak keycloak = clientFactory.createClient();
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
            Keycloak keycloak = clientFactory.createClient();
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
            Keycloak keycloak = clientFactory.createClient();
            keycloak.realm(realmName).updateRealmEventsConfig(eventsConfig);
            return "Successfully updated realm events config: " + realmName;
        } catch (NotFoundException e) {
            return "Realm not found: " + realmName;
        } catch (Exception e) {
            Log.error("Failed to update realm events config: " + realmName, e);
            return "Error updating realm events config: " + realmName + " - " + e.getMessage();
        }
    }

    public String testLdapConnection(String realm, TestLdapConnectionRepresentation test) {
        try {
            Keycloak keycloak = clientFactory.createClient();
            try (Response r = keycloak.realm(realm).testLDAPConnection(test)) {
                return "HTTP " + r.getStatus() + (r.hasEntity() ? " " + r.readEntity(String.class) : "");
            }
        } catch (Exception e) {
            Log.error("LDAP test failed for realm: " + realm, e);
            return "Error testing LDAP: " + e.getMessage();
        }
    }

    public SynchronizationResultRepresentation syncUserStorage(String realm, String userStorageId, String action) {
        try {
            Keycloak keycloak = clientFactory.createClient();
            return keycloak.realm(realm).userStorage().syncUsers(userStorageId, action);
        } catch (Exception e) {
            Log.error("User storage sync failed: " + realm, e);
            return null;
        }
    }

    public GlobalRequestResult pushRealmRevocation(String realm) {
        try {
            Keycloak keycloak = clientFactory.createClient();
            return keycloak.realm(realm).pushRevocation();
        } catch (Exception e) {
            Log.error("pushRevocation failed: " + realm, e);
            return null;
        }
    }

    public GlobalRequestResult logoutAllUsers(String realm) {
        try {
            Keycloak keycloak = clientFactory.createClient();
            return keycloak.realm(realm).logoutAll();
        } catch (Exception e) {
            Log.error("logoutAll failed: " + realm, e);
            return null;
        }
    }

    public ClientPoliciesRepresentation getRealmClientPolicies(String realm, Boolean includeGlobal) {
        try {
            Keycloak keycloak = clientFactory.createClient();
            return keycloak.realm(realm).clientPoliciesPoliciesResource().getPolicies(includeGlobal);
        } catch (Exception e) {
            Log.error("getRealmClientPolicies failed: " + realm, e);
            return null;
        }
    }

    public String updateRealmClientPolicies(String realm, ClientPoliciesRepresentation policies) {
        try {
            Keycloak keycloak = clientFactory.createClient();
            keycloak.realm(realm).clientPoliciesPoliciesResource().updatePolicies(policies);
            return "Successfully updated client policies for realm: " + realm;
        } catch (Exception e) {
            Log.error("updateRealmClientPolicies failed: " + realm, e);
            return "Error updating client policies: " + e.getMessage();
        }
    }

    public ClientProfilesRepresentation getRealmClientProfiles(String realm, Boolean includeGlobal) {
        try {
            Keycloak keycloak = clientFactory.createClient();
            if (realm == null || realm.isEmpty()) {
                return null;
            }
            return keycloak.realm(realm).clientPoliciesProfilesResource().getProfiles(includeGlobal);
        } catch (Exception e) {
            Log.error("getRealmClientProfiles failed: " + realm, e);
            return null;
        }
    }

    public String updateRealmClientProfiles(String realm, ClientProfilesRepresentation profiles) {
        try {
            Keycloak keycloak = clientFactory.createClient();
            keycloak.realm(realm).clientPoliciesProfilesResource().updateProfiles(profiles);
            return "Successfully updated client profiles for realm: " + realm;
        } catch (Exception e) {
            Log.error("updateRealmClientProfiles failed: " + realm, e);
            return "Error updating client profiles: " + e.getMessage();
        }
    }

    public List<ComponentTypeRepresentation> getClientRegistrationProviders(String realm) {
        try {
            Keycloak keycloak = clientFactory.createClient();
            return keycloak.realm(realm).clientRegistrationPolicy().getProviders();
        } catch (Exception e) {
            Log.error("getClientRegistrationProviders failed: " + realm, e);
            return List.of();
        }
    }
}
