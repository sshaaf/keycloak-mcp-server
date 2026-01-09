package dev.shaaf.keycloak.mcp.server;

import io.quarkus.logging.Log;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;

import java.util.Optional;

/**
 * Factory for creating Keycloak admin clients with user JWT token authentication.
 * 
 * This factory creates a request-scoped Keycloak admin client that uses the
 * authenticated user's JWT token to call the Keycloak Admin API.
 * 
 * Production Mode (OIDC enabled):
 * - Uses authenticated user's JWT token
 * - Each user has their own permissions (no shared admin access)
 * - Keycloak enforces its native permission system (realm access, roles, etc.)
 * - Full audit trail by user
 * 
 * Development/Test Mode (OIDC disabled):
 * - Falls back to kc.dev.user and kc.dev.password config properties
 * - Or KC_DEV_USER and KC_DEV_PASSWORD environment variables
 * - Allows local testing without JWT token setup
 */
@RequestScoped
public class KeycloakClientFactory {

    @Inject
    SecurityIdentity securityIdentity;
    
    @Inject
    Instance<JsonWebToken> jwt;

    @ConfigProperty(name = "kc.dev.user")
    Optional<String> devUserConfig;

    @ConfigProperty(name = "kc.dev.password")
    Optional<String> devPasswordConfig;

    @ConfigProperty(name = "quarkus.keycloak.admin-client.server-url")
    Optional<String> serverUrlConfig;

    @ConfigProperty(name = "kc.dev.realm", defaultValue = "master")
    String devRealm;

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
        // Use config property first (for TestContainers), then env var, then default
        String serverUrl = serverUrlConfig.orElse(
            System.getenv().getOrDefault("KC_URL", "http://localhost:8180")
        );
        String realm = System.getenv().getOrDefault("KC_REALM", devRealm);
        
        // Use authenticated user's token (production mode with OIDC enabled)
        if (!securityIdentity.isAnonymous() && jwt.isResolvable()) {
            String userToken = jwt.get().getRawToken();
            Log.infof("Creating Keycloak client with user token for: %s", 
                     securityIdentity.getPrincipal().getName());
            
            return KeycloakBuilder.builder()
                    .serverUrl(serverUrl)
                    .realm(realm)
                    .authorization("Bearer " + userToken)
                    .build();
        }
        
        // Development/Test mode fallback (when OIDC is disabled)
        // Use config properties first, then environment variables
        String username = devUserConfig.orElse(System.getenv("KC_DEV_USER"));
        String password = devPasswordConfig.orElse(System.getenv("KC_DEV_PASSWORD"));
        
        if (username != null && password != null) {
            Log.warnf("Creating Keycloak client with dev credentials for user: %s (DEV/TEST MODE)", username);
            
            return KeycloakBuilder.builder()
                    .serverUrl(serverUrl)
                    .realm(realm)
                    .username(username)
                    .password(password)
                    .clientId("admin-cli")
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
            "Or use the helper script: ./scripts/get-mcp-token.sh\n\n" +
            "For development mode, set KC_DEV_USER and KC_DEV_PASSWORD environment variables."
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
        return !securityIdentity.isAnonymous() && jwt.isResolvable();
    }
}

