package dev.shaaf.keycloak.mcp.server.scopemapping;

import dev.shaaf.keycloak.mcp.server.KeycloakClientFactory;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.MappingsRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;

import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class ScopeMappingService {

    @Inject
    KeycloakClientFactory clientFactory;

    public MappingsRepresentation getScopeMappings(String realm, String clientScopeId) {
        Keycloak keycloak = clientFactory.createClient();
        try {
            return keycloak.realm(realm).clientScopes().get(clientScopeId).getScopeMappings().getAll();
        } catch (NotFoundException e) {
            Log.error("Client scope not found: " + clientScopeId, e);
            return null;
        } catch (Exception e) {
            Log.error("Failed to get scope mappings: " + clientScopeId, e);
            return null;
        }
    }

    public List<RoleRepresentation> getRealmScopeMappings(String realm, String clientScopeId) {
        Keycloak keycloak = clientFactory.createClient();
        try {
            return keycloak.realm(realm).clientScopes().get(clientScopeId).getScopeMappings().realmLevel().listAll();
        } catch (NotFoundException e) {
            Log.error("Client scope not found: " + clientScopeId, e);
            return Collections.emptyList();
        } catch (Exception e) {
            Log.error("Failed to get realm scope mappings: " + clientScopeId, e);
            return Collections.emptyList();
        }
    }

    public String addRealmScopeMapping(String realm, String clientScopeId, List<RoleRepresentation> roles) {
        Keycloak keycloak = clientFactory.createClient();
        try {
            keycloak.realm(realm).clientScopes().get(clientScopeId).getScopeMappings().realmLevel().add(roles);
            return "Successfully added realm scope mappings";
        } catch (NotFoundException e) {
            return "Client scope not found: " + clientScopeId;
        } catch (Exception e) {
            Log.error("Failed to add realm scope mappings: " + clientScopeId, e);
            return "Error adding realm scope mappings: " + e.getMessage();
        }
    }

    public String removeRealmScopeMapping(String realm, String clientScopeId, List<RoleRepresentation> roles) {
        Keycloak keycloak = clientFactory.createClient();
        try {
            keycloak.realm(realm).clientScopes().get(clientScopeId).getScopeMappings().realmLevel().remove(roles);
            return "Successfully removed realm scope mappings";
        } catch (NotFoundException e) {
            return "Client scope not found: " + clientScopeId;
        } catch (Exception e) {
            Log.error("Failed to remove realm scope mappings: " + clientScopeId, e);
            return "Error removing realm scope mappings: " + e.getMessage();
        }
    }

    public List<RoleRepresentation> getClientScopeMappings(String realm, String clientScopeId, String targetClientId) {
        Keycloak keycloak = clientFactory.createClient();
        try {
            return keycloak.realm(realm).clientScopes().get(clientScopeId).getScopeMappings().clientLevel(targetClientId).listAll();
        } catch (NotFoundException e) {
            Log.error("Client scope or target client not found: " + clientScopeId, e);
            return Collections.emptyList();
        } catch (Exception e) {
            Log.error("Failed to get client scope mappings: " + clientScopeId, e);
            return Collections.emptyList();
        }
    }

    public String addClientScopeMapping(String realm, String clientScopeId, String targetClientId, List<RoleRepresentation> roles) {
        Keycloak keycloak = clientFactory.createClient();
        try {
            keycloak.realm(realm).clientScopes().get(clientScopeId).getScopeMappings().clientLevel(targetClientId).add(roles);
            return "Successfully added client scope mappings";
        } catch (NotFoundException e) {
            return "Client scope or target client not found: " + clientScopeId;
        } catch (Exception e) {
            Log.error("Failed to add client scope mappings: " + clientScopeId, e);
            return "Error adding client scope mappings: " + e.getMessage();
        }
    }

    public String removeClientScopeMapping(String realm, String clientScopeId, String targetClientId, List<RoleRepresentation> roles) {
        Keycloak keycloak = clientFactory.createClient();
        try {
            keycloak.realm(realm).clientScopes().get(clientScopeId).getScopeMappings().clientLevel(targetClientId).remove(roles);
            return "Successfully removed client scope mappings";
        } catch (NotFoundException e) {
            return "Client scope or target client not found: " + clientScopeId;
        } catch (Exception e) {
            Log.error("Failed to remove client scope mappings: " + clientScopeId, e);
            return "Error removing client scope mappings: " + e.getMessage();
        }
    }
}
