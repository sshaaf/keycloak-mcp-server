package dev.shaaf.keycloak.mcp.server;

import io.quarkus.logging.Log;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;

/**
 * Factory for creating Keycloak admin clients with user JWT token authentication.
 * 
 * This factory creates a request-scoped Keycloak admin client that uses the
 * authenticated user's JWT token to call the Keycloak Admin API.
 * 
 * Benefits:
 * - Each user has their own permissions (no shared admin access)
 * - Keycloak enforces its native permission system (realm access, roles, etc.)
 * - Full audit trail by user
 * - No service account or shared credentials needed
 */
@RequestScoped
public class KeycloakClientFactory {

    @Inject
    SecurityIdentity securityIdentity;
    
    @Inject
    JsonWebToken jwt;

    /**
     * Creates a Keycloak admin client using the authenticated user's JWT token.
     * 
     * This method requires the user to be authenticated with a valid JWT token.
     * The token is obtained from the Authorization header and used to call
     * the Keycloak Admin API with the user's permissions.
     * 
     * @return Keycloak admin client configured with user's JWT token
     * @throws IllegalStateException if user is not authenticated
     */
    public Keycloak createClient() {
        String serverUrl = System.getenv().getOrDefault("KC_URL", "http://localhost:8180");
        String realm = System.getenv().getOrDefault("KC_REALM", "master");
        
        // Use authenticated user's token
        if (!securityIdentity.isAnonymous() && jwt != null) {
            String userToken = jwt.getRawToken();
            Log.infof("Creating Keycloak client with user token for: %s", 
                     securityIdentity.getPrincipal().getName());
            
            return KeycloakBuilder.builder()
                    .serverUrl(serverUrl)
                    .realm(realm)
                    .authorization("Bearer " + userToken)
                    .build();
        }
        
        // No authentication
        throw new IllegalStateException(
            "Authentication required. Please provide a valid JWT token in the Authorization header.\n" +
            "Obtain a token from Keycloak:\n" +
            "  curl -X POST " + serverUrl + "/realms/" + realm + "/protocol/openid-connect/token \\\n" +
            "    -d grant_type=password \\\n" +
            "    -d client_id=admin-cli \\\n" +
            "    -d username=<your-username> \\\n" +
            "    -d password=<your-password>\n" +
            "Or use the helper script: ./scripts/get-mcp-token.sh"
        );
    }
    
    /**
     * Returns the current authenticated user's principal name.
     * 
     * @return username if authenticated, "anonymous" otherwise
     */
    public String getCurrentUser() {
        if (!securityIdentity.isAnonymous()) {
            return securityIdentity.getPrincipal().getName();
        }
        return "anonymous";
    }
    
    /**
     * Checks if the current request is authenticated with a user token.
     * 
     * @return true if user is authenticated, false otherwise
     */
    public boolean isUserAuthenticated() {
        return !securityIdentity.isAnonymous() && jwt != null;
    }
}

