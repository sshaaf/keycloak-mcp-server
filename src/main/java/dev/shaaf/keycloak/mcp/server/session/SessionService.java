package dev.shaaf.keycloak.mcp.server.session;

import dev.shaaf.keycloak.mcp.server.KeycloakClientFactory;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserSessionRepresentation;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class SessionService {

    @Inject
    KeycloakClientFactory clientFactory;

    /**
     * Get user sessions
     */
    public List<UserSessionRepresentation> getUserSessions(String realm, String userId) {
        Keycloak keycloak = clientFactory.createClient();
        try {
            return keycloak.realm(realm).users().get(userId).getUserSessions();
        } catch (NotFoundException e) {
            Log.error("User not found: " + userId, e);
            return Collections.emptyList();
        } catch (Exception e) {
            Log.error("Failed to get user sessions: " + userId, e);
            return Collections.emptyList();
        }
    }

    /**
     * Get offline sessions
     */
    public List<UserSessionRepresentation> getOfflineSessions(String realm, String userId, String clientId) {
        Keycloak keycloak = clientFactory.createClient();
        try {
            return keycloak.realm(realm).users().get(userId).getOfflineSessions(clientId);
        } catch (NotFoundException e) {
            Log.error("User not found: " + userId, e);
            return Collections.emptyList();
        } catch (Exception e) {
            Log.error("Failed to get offline sessions: " + userId, e);
            return Collections.emptyList();
        }
    }

    /**
     * Logout user
     */
    public String logoutUser(String realm, String userId) {
        Keycloak keycloak = clientFactory.createClient();
        try {
            keycloak.realm(realm).users().get(userId).logout();
            return "Successfully logged out user: " + userId;
        } catch (NotFoundException e) {
            return "User not found: " + userId;
        } catch (Exception e) {
            Log.error("Failed to logout user: " + userId, e);
            return "Error logging out user: " + userId + " - " + e.getMessage();
        }
    }

    /**
     * Get user consents
     */
    public List<Map<String, Object>> getUserConsents(String realm, String userId) {
        Keycloak keycloak = clientFactory.createClient();
        try {
            return keycloak.realm(realm).users().get(userId).getConsents();
        } catch (NotFoundException e) {
            Log.error("User not found: " + userId, e);
            return Collections.emptyList();
        } catch (Exception e) {
            Log.error("Failed to get user consents: " + userId, e);
            return Collections.emptyList();
        }
    }

    /**
     * Revoke user consent
     */
    public String revokeUserConsent(String realm, String userId, String clientId) {
        Keycloak keycloak = clientFactory.createClient();
        try {
            keycloak.realm(realm).users().get(userId).revokeConsent(clientId);
            return "Successfully revoked consent for user: " + userId + ", client: " + clientId;
        } catch (NotFoundException e) {
            return "User not found: " + userId;
        } catch (Exception e) {
            Log.error("Failed to revoke consent: " + userId, e);
            return "Error revoking consent: " + userId + " - " + e.getMessage();
        }
    }

    /**
     * Get client user sessions
     */
    public List<UserSessionRepresentation> getClientUserSessions(String realm, String clientId) {
        Keycloak keycloak = clientFactory.createClient();
        try {
            return keycloak.realm(realm).clients().get(clientId).getUserSessions(null, null);
        } catch (NotFoundException e) {
            Log.error("Client not found: " + clientId, e);
            return Collections.emptyList();
        } catch (Exception e) {
            Log.error("Failed to get client user sessions: " + clientId, e);
            return Collections.emptyList();
        }
    }

    /**
     * Get client offline sessions
     */
    public List<UserSessionRepresentation> getClientOfflineSessions(String realm, String clientId) {
        Keycloak keycloak = clientFactory.createClient();
        try {
            return keycloak.realm(realm).clients().get(clientId).getOfflineUserSessions(null, null);
        } catch (NotFoundException e) {
            Log.error("Client not found: " + clientId, e);
            return Collections.emptyList();
        } catch (Exception e) {
            Log.error("Failed to get client offline sessions: " + clientId, e);
            return Collections.emptyList();
        }
    }

    /**
     * Clear user login failures
     */
    public String clearUserLoginFailures(String realm, String userId) {
        Keycloak keycloak = clientFactory.createClient();
        try {
            keycloak.realm(realm).attackDetection().clearBruteForceForUser(userId);
            return "Successfully cleared login failures for user: " + userId;
        } catch (Exception e) {
            Log.error("Failed to clear login failures: " + userId, e);
            return "Error clearing login failures: " + userId + " - " + e.getMessage();
        }
    }

    /**
     * Clear all login failures
     */
    public String clearAllLoginFailures(String realm) {
        Keycloak keycloak = clientFactory.createClient();
        try {
            keycloak.realm(realm).attackDetection().clearAllBruteForce();
            return "Successfully cleared all login failures for realm: " + realm;
        } catch (Exception e) {
            Log.error("Failed to clear all login failures: " + realm, e);
            return "Error clearing all login failures: " + realm + " - " + e.getMessage();
        }
    }

    /**
     * Brute-force / temporary lockout status for a user.
     */
    public Map<String, Object> getBruteForceUserStatus(String realm, String userId) {
        Keycloak keycloak = clientFactory.createClient();
        try {
            return keycloak.realm(realm).attackDetection().bruteForceUserStatus(userId);
        } catch (Exception e) {
            Log.error("Failed to get brute-force status: " + userId, e);
            return Collections.emptyMap();
        }
    }
}
