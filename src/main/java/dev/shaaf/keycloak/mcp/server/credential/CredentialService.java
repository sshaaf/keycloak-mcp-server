package dev.shaaf.keycloak.mcp.server.credential;

import dev.shaaf.keycloak.mcp.server.KeycloakClientFactory;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;

import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class CredentialService {

    @Inject
    KeycloakClientFactory clientFactory;

    public List<CredentialRepresentation> getUserCredentials(String realm, String userId) {
        Keycloak keycloak = clientFactory.createClient();
        try {
            return keycloak.realm(realm).users().get(userId).credentials();
        } catch (NotFoundException e) {
            Log.error("User not found: " + userId, e);
            return Collections.emptyList();
        } catch (Exception e) {
            Log.error("Failed to get user credentials: " + userId, e);
            return Collections.emptyList();
        }
    }

    public String deleteUserCredential(String realm, String userId, String credentialId) {
        Keycloak keycloak = clientFactory.createClient();
        try {
            keycloak.realm(realm).users().get(userId).removeCredential(credentialId);
            return "Successfully deleted credential: " + credentialId;
        } catch (NotFoundException e) {
            return "User or credential not found: " + userId;
        } catch (Exception e) {
            Log.error("Failed to delete credential: " + credentialId, e);
            return "Error deleting credential: " + credentialId + " - " + e.getMessage();
        }
    }

    public String moveCredentialToPosition(String realm, String userId, String credentialId, int newPosition) {
        Keycloak keycloak = clientFactory.createClient();
        try {
            keycloak.realm(realm).users().get(userId).moveCredentialToPosition(credentialId, newPosition);
            return "Successfully moved credential to position: " + newPosition;
        } catch (NotFoundException e) {
            return "User or credential not found: " + userId;
        } catch (Exception e) {
            Log.error("Failed to move credential: " + credentialId, e);
            return "Error moving credential: " + credentialId + " - " + e.getMessage();
        }
    }

    public String resetUserPasswordWithCredential(String realm, String userId, CredentialRepresentation credential) {
        Keycloak keycloak = clientFactory.createClient();
        try {
            keycloak.realm(realm).users().get(userId).resetPassword(credential);
            return "Successfully reset password for user: " + userId;
        } catch (NotFoundException e) {
            return "User not found: " + userId;
        } catch (Exception e) {
            Log.error("Failed to reset password: " + userId, e);
            return "Error resetting password: " + userId + " - " + e.getMessage();
        }
    }
}
