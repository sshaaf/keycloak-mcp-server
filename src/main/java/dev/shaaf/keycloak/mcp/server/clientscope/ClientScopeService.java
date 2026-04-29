package dev.shaaf.keycloak.mcp.server.clientscope;

import dev.shaaf.keycloak.mcp.server.KeycloakClientFactory;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientScopeResource;
import org.keycloak.admin.client.resource.ClientScopesResource;
import org.keycloak.admin.client.resource.ProtocolMappersResource;
import org.keycloak.representations.idm.ClientScopeRepresentation;
import org.keycloak.representations.idm.ProtocolMapperRepresentation;

import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class ClientScopeService {

    @Inject
    KeycloakClientFactory clientFactory;

    /**
     * Get all client scopes
     */
    public List<ClientScopeRepresentation> getClientScopes(String realm) {
        Keycloak keycloak = clientFactory.createClient();
        try {
            return keycloak.realm(realm).clientScopes().findAll();
        } catch (Exception e) {
            Log.error("Failed to get client scopes: " + realm, e);
            return Collections.emptyList();
        }
    }

    /**
     * Get a specific client scope
     */
    public ClientScopeRepresentation getClientScope(String realm, String clientScopeId) {
        Keycloak keycloak = clientFactory.createClient();
        try {
            return keycloak.realm(realm).clientScopes().get(clientScopeId).toRepresentation();
        } catch (NotFoundException e) {
            Log.error("Client scope not found: " + clientScopeId, e);
            return null;
        } catch (Exception e) {
            Log.error("Failed to get client scope: " + clientScopeId, e);
            return null;
        }
    }

    /**
     * Create a client scope
     */
    public String createClientScope(String realm, ClientScopeRepresentation clientScope) {
        Keycloak keycloak = clientFactory.createClient();
        try {
            Response response = keycloak.realm(realm).clientScopes().create(clientScope);
            if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
                return "Successfully created client scope: " + clientScope.getName();
            } else {
                Log.error("Failed to create client scope. Status: " + response.getStatus());
                response.close();
                return "Error creating client scope: " + clientScope.getName();
            }
        } catch (Exception e) {
            Log.error("Failed to create client scope: " + clientScope.getName(), e);
            return "Error creating client scope: " + clientScope.getName() + " - " + e.getMessage();
        }
    }

    /**
     * Update a client scope
     */
    public String updateClientScope(String realm, String clientScopeId, ClientScopeRepresentation clientScope) {
        Keycloak keycloak = clientFactory.createClient();
        try {
            ClientScopeResource resource = keycloak.realm(realm).clientScopes().get(clientScopeId);
            resource.update(clientScope);
            return "Successfully updated client scope: " + clientScopeId;
        } catch (NotFoundException e) {
            return "Client scope not found: " + clientScopeId;
        } catch (Exception e) {
            Log.error("Failed to update client scope: " + clientScopeId, e);
            return "Error updating client scope: " + clientScopeId + " - " + e.getMessage();
        }
    }

    /**
     * Delete a client scope
     */
    public String deleteClientScope(String realm, String clientScopeId) {
        Keycloak keycloak = clientFactory.createClient();
        try {
            keycloak.realm(realm).clientScopes().get(clientScopeId).remove();
            return "Successfully deleted client scope: " + clientScopeId;
        } catch (NotFoundException e) {
            return "Client scope not found: " + clientScopeId;
        } catch (Exception e) {
            Log.error("Failed to delete client scope: " + clientScopeId, e);
            return "Error deleting client scope: " + clientScopeId + " - " + e.getMessage();
        }
    }

    /**
     * Get protocol mappers for a client scope
     */
    public List<ProtocolMapperRepresentation> getClientScopeProtocolMappers(String realm, String clientScopeId) {
        Keycloak keycloak = clientFactory.createClient();
        try {
            ProtocolMappersResource mappers = keycloak.realm(realm).clientScopes().get(clientScopeId).getProtocolMappers();
            return mappers.getMappers();
        } catch (NotFoundException e) {
            Log.error("Client scope not found: " + clientScopeId, e);
            return Collections.emptyList();
        } catch (Exception e) {
            Log.error("Failed to get protocol mappers: " + clientScopeId, e);
            return Collections.emptyList();
        }
    }

    /**
     * Add protocol mapper to client scope
     */
    public String addProtocolMapperToClientScope(String realm, String clientScopeId, ProtocolMapperRepresentation mapper) {
        Keycloak keycloak = clientFactory.createClient();
        try {
            ProtocolMappersResource mappers = keycloak.realm(realm).clientScopes().get(clientScopeId).getProtocolMappers();
            Response response = mappers.createMapper(mapper);
            if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
                return "Successfully added protocol mapper: " + mapper.getName();
            } else {
                Log.error("Failed to add protocol mapper. Status: " + response.getStatus());
                response.close();
                return "Error adding protocol mapper: " + mapper.getName();
            }
        } catch (NotFoundException e) {
            return "Client scope not found: " + clientScopeId;
        } catch (Exception e) {
            Log.error("Failed to add protocol mapper: " + mapper.getName(), e);
            return "Error adding protocol mapper: " + mapper.getName() + " - " + e.getMessage();
        }
    }

    /**
     * Update protocol mapper
     */
    public String updateProtocolMapper(String realm, String clientScopeId, String mapperId, ProtocolMapperRepresentation mapper) {
        Keycloak keycloak = clientFactory.createClient();
        try {
            ProtocolMappersResource mappers = keycloak.realm(realm).clientScopes().get(clientScopeId).getProtocolMappers();
            mappers.update(mapperId, mapper);
            return "Successfully updated protocol mapper: " + mapperId;
        } catch (NotFoundException e) {
            return "Client scope or mapper not found: " + clientScopeId + " -> " + mapperId;
        } catch (Exception e) {
            Log.error("Failed to update protocol mapper: " + mapperId, e);
            return "Error updating protocol mapper: " + mapperId + " - " + e.getMessage();
        }
    }

    /**
     * Delete protocol mapper
     */
    public String deleteProtocolMapper(String realm, String clientScopeId, String mapperId) {
        Keycloak keycloak = clientFactory.createClient();
        try {
            ProtocolMappersResource mappers = keycloak.realm(realm).clientScopes().get(clientScopeId).getProtocolMappers();
            mappers.delete(mapperId);
            return "Successfully deleted protocol mapper: " + mapperId;
        } catch (NotFoundException e) {
            return "Client scope or mapper not found: " + clientScopeId + " -> " + mapperId;
        } catch (Exception e) {
            Log.error("Failed to delete protocol mapper: " + mapperId, e);
            return "Error deleting protocol mapper: " + mapperId + " - " + e.getMessage();
        }
    }

    /**
     * Add default client scope to realm
     */
    public String addDefaultClientScope(String realm, String clientScopeId) {
        Keycloak keycloak = clientFactory.createClient();
        try {
            keycloak.realm(realm).addDefaultDefaultClientScope(clientScopeId);
            return "Successfully added default client scope: " + clientScopeId;
        } catch (Exception e) {
            Log.error("Failed to add default client scope: " + clientScopeId, e);
            return "Error adding default client scope: " + clientScopeId + " - " + e.getMessage();
        }
    }

    /**
     * Remove default client scope from realm
     */
    public String removeDefaultClientScope(String realm, String clientScopeId) {
        Keycloak keycloak = clientFactory.createClient();
        try {
            keycloak.realm(realm).removeDefaultDefaultClientScope(clientScopeId);
            return "Successfully removed default client scope: " + clientScopeId;
        } catch (Exception e) {
            Log.error("Failed to remove default client scope: " + clientScopeId, e);
            return "Error removing default client scope: " + clientScopeId + " - " + e.getMessage();
        }
    }

    /**
     * Add optional client scope to realm
     */
    public String addOptionalClientScope(String realm, String clientScopeId) {
        Keycloak keycloak = clientFactory.createClient();
        try {
            keycloak.realm(realm).addDefaultOptionalClientScope(clientScopeId);
            return "Successfully added optional client scope: " + clientScopeId;
        } catch (Exception e) {
            Log.error("Failed to add optional client scope: " + clientScopeId, e);
            return "Error adding optional client scope: " + clientScopeId + " - " + e.getMessage();
        }
    }

    /**
     * Remove optional client scope from realm
     */
    public String removeOptionalClientScope(String realm, String clientScopeId) {
        Keycloak keycloak = clientFactory.createClient();
        try {
            keycloak.realm(realm).removeDefaultOptionalClientScope(clientScopeId);
            return "Successfully removed optional client scope: " + clientScopeId;
        } catch (Exception e) {
            Log.error("Failed to remove optional client scope: " + clientScopeId, e);
            return "Error removing optional client scope: " + clientScopeId + " - " + e.getMessage();
        }
    }

    /**
     * Get default client scopes
     */
    public List<ClientScopeRepresentation> getDefaultClientScopes(String realm) {
        Keycloak keycloak = clientFactory.createClient();
        try {
            return keycloak.realm(realm).getDefaultDefaultClientScopes();
        } catch (Exception e) {
            Log.error("Failed to get default client scopes: " + realm, e);
            return Collections.emptyList();
        }
    }

    /**
     * Get optional client scopes
     */
    public List<ClientScopeRepresentation> getOptionalClientScopes(String realm) {
        Keycloak keycloak = clientFactory.createClient();
        try {
            return keycloak.realm(realm).getDefaultOptionalClientScopes();
        } catch (Exception e) {
            Log.error("Failed to get optional client scopes: " + realm, e);
            return Collections.emptyList();
        }
    }
}
