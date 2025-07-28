package dev.shaaf.keycloak.mcp.server.service;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.admin.client.resource.ProtocolMappersResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.ProtocolMapperRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class ClientService {

    @Inject
    Keycloak keycloak;

    /**
     * Get all clients from a realm
     * @param realm The realm to get clients from
     * @return List of all client representations in the realm
     */
    public List<ClientRepresentation> getClients(String realm) {
        return keycloak.realm(realm).clients().findAll();
    }

    /**
     * Find a client by client ID in a realm
     * @param realm The realm where the client resides
     * @param clientId The client ID to search for
     * @return Optional containing the client representation if found, or empty if not found
     */
    public Optional<ClientRepresentation> findClientByClientId(String realm, String clientId) {
        List<ClientRepresentation> clients = keycloak.realm(realm).clients().findByClientId(clientId);
        if (clients != null && !clients.isEmpty()) {
            return Optional.of(clients.get(0));
        }
        return Optional.empty();
    }
    
    /**
     * Get a specific client by ID
     * @param realm The realm where the client resides
     * @param id The ID of the client
     * @return The client representation or null if not found
     */
    public ClientRepresentation getClient(String realm, String id) {
        try {
            return keycloak.realm(realm).clients().get(id).toRepresentation();
        } catch (NotFoundException e) {
            Log.error("Client not found: " + id, e);
            return null;
        } catch (Exception e) {
            Log.error("Failed to get client: " + id, e);
            return null;
        }
    }

    /**
     * Create a new client
     * @param realm The realm where the client will be created
     * @param clientName The name of the client
     * @return Success or error message
     */
    public String createClient(String realm, String clientName) {
        ClientsResource clientsResource = keycloak.realm(realm).clients();
        ClientRepresentation clientRepresentation = new ClientRepresentation();
        clientRepresentation.setClientId(clientName);
        clientRepresentation.setName(clientName);
        clientRepresentation.setProtocol("openid-connect");

        clientRepresentation.setPublicClient(false);
        clientRepresentation.setStandardFlowEnabled(true);
        clientRepresentation.setDirectAccessGrantsEnabled(false);
        clientRepresentation.setServiceAccountsEnabled(false);
        clientRepresentation.setEnabled(true);
        clientRepresentation.setRedirectUris(Collections.singletonList("http://localhost:8080/redirect/*"));


        Response response = clientsResource.create(clientRepresentation);
        if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
            return "Successfully created client: " + clientName;
        } else {
            Log.error("Failed to create client. Status: " + response.getStatus());
            response.close();
            return "Error creating client: " + clientName;
        }
    }
    
    /**
     * Update a client
     * @param realm The realm where the client resides
     * @param clientId The ID of the client
     * @param clientRepresentation The updated client representation
     * @return Success or error message
     */
    public String updateClient(String realm, String clientId, ClientRepresentation clientRepresentation) {
        try {
            ClientResource clientResource = keycloak.realm(realm).clients().get(clientId);
            clientResource.update(clientRepresentation);
            return "Successfully updated client: " + clientId;
        } catch (NotFoundException e) {
            return "Client not found: " + clientId;
        } catch (Exception e) {
            Log.error("Failed to update client: " + clientId, e);
            return "Error updating client: " + clientId + " - " + e.getMessage();
        }
    }
    
    /**
     * Delete a client
     * @param realm The realm where the client resides
     * @param clientId The ID of the client
     * @return Success or error message
     */
    public String deleteClient(String realm, String clientId) {
        try {
            // Check if client exists before attempting to delete
            ClientRepresentation client = getClient(realm, clientId);
            if (client == null) {
                return "Client not found: " + clientId;
            }
            
            // The remove() method returns void, so we rely on exception handling
            keycloak.realm(realm).clients().get(clientId).remove();
            return "Successfully deleted client: " + clientId;
        } catch (NotFoundException e) {
            return "Client not found: " + clientId;
        } catch (Exception e) {
            Log.error("Failed to delete client: " + clientId, e);
            return "Error deleting client: " + clientId + " - " + e.getMessage();
        }
    }
    
    /**
     * Get client secret
     * @param realm The realm where the client resides
     * @param clientId The ID of the client
     * @return The client secret or error message
     */
    public String getClientSecret(String realm, String clientId) {
        try {
            CredentialRepresentation credential = keycloak.realm(realm).clients().get(clientId).getSecret();
            if (credential != null && credential.getValue() != null) {
                return credential.getValue();
            } else {
                return "Client does not have a secret or is not a confidential client";
            }
        } catch (NotFoundException e) {
            return "Client not found: " + clientId;
        } catch (Exception e) {
            Log.error("Failed to get client secret: " + clientId, e);
            return "Error getting client secret: " + clientId + " - " + e.getMessage();
        }
    }
    
    /**
     * Generate new client secret
     * @param realm The realm where the client resides
     * @param clientId The ID of the client
     * @return The new client secret or error message
     */
    public String generateNewClientSecret(String realm, String clientId) {
        try {
            CredentialRepresentation credential = keycloak.realm(realm).clients().get(clientId).generateNewSecret();
            if (credential != null && credential.getValue() != null) {
                return credential.getValue();
            } else {
                return "Failed to generate new secret for client";
            }
        } catch (NotFoundException e) {
            return "Client not found: " + clientId;
        } catch (Exception e) {
            Log.error("Failed to generate new client secret: " + clientId, e);
            return "Error generating new client secret: " + clientId + " - " + e.getMessage();
        }
    }
    
    /**
     * Get client roles
     * @param realm The realm where the client resides
     * @param clientId The ID of the client
     * @return List of roles for the client or empty list if not found
     */
    public List<RoleRepresentation> getClientRoles(String realm, String clientId) {
        try {
            return keycloak.realm(realm).clients().get(clientId).roles().list();
        } catch (NotFoundException e) {
            Log.error("Client not found: " + clientId, e);
            return Collections.emptyList();
        } catch (Exception e) {
            Log.error("Failed to get client roles: " + clientId, e);
            return Collections.emptyList();
        }
    }
    
    /**
     * Create client role
     * @param realm The realm where the client resides
     * @param clientId The ID of the client
     * @param roleName The name of the role
     * @param description The description of the role
     * @return Success or error message
     */
    public String createClientRole(String realm, String clientId, String roleName, String description) {
        try {
            RoleRepresentation role = new RoleRepresentation();
            role.setName(roleName);
            role.setDescription(description);
            role.setClientRole(true);
            
            RolesResource rolesResource = keycloak.realm(realm).clients().get(clientId).roles();
            rolesResource.create(role);
            
            return "Successfully created client role: " + roleName;
        } catch (NotFoundException e) {
            return "Client not found: " + clientId;
        } catch (Exception e) {
            Log.error("Failed to create client role: " + roleName, e);
            return "Error creating client role: " + roleName + " - " + e.getMessage();
        }
    }
    
    /**
     * Delete client role
     * @param realm The realm where the client resides
     * @param clientId The ID of the client
     * @param roleName The name of the role
     * @return Success or error message
     */
    public String deleteClientRole(String realm, String clientId, String roleName) {
        try {
            keycloak.realm(realm).clients().get(clientId).roles().deleteRole(roleName);
            return "Successfully deleted client role: " + roleName;
        } catch (NotFoundException e) {
            return "Client or role not found: " + clientId + " -> " + roleName;
        } catch (Exception e) {
            Log.error("Failed to delete client role: " + roleName, e);
            return "Error deleting client role: " + roleName + " - " + e.getMessage();
        }
    }
    
    /**
     * Get service account user
     * @param realm The realm where the client resides
     * @param clientId The ID of the client
     * @return The service account user or null if not found
     */
    public UserRepresentation getServiceAccountUser(String realm, String clientId) {
        try {
            return keycloak.realm(realm).clients().get(clientId).getServiceAccountUser();
        } catch (NotFoundException e) {
            Log.error("Client not found: " + clientId, e);
            return null;
        } catch (Exception e) {
            Log.error("Failed to get service account user: " + clientId, e);
            return null;
        }
    }
    
    /**
     * Get client protocol mappers
     * @param realm The realm where the client resides
     * @param clientId The ID of the client
     * @return List of protocol mappers for the client or empty list if not found
     */
    public List<ProtocolMapperRepresentation> getClientProtocolMappers(String realm, String clientId) {
        try {
            ClientResource clientResource = keycloak.realm(realm).clients().get(clientId);
            ProtocolMappersResource protocolMappers = clientResource.getProtocolMappers();
            return protocolMappers.getMappers();
        } catch (NotFoundException e) {
            Log.error("Client not found: " + clientId, e);
            return Collections.emptyList();
        } catch (Exception e) {
            Log.error("Failed to get client protocol mappers: " + clientId, e);
            return Collections.emptyList();
        }
    }
    
    /**
     * Add protocol mapper to client
     * @param realm The realm where the client resides
     * @param clientId The ID of the client
     * @param mapper The protocol mapper to add
     * @return Success or error message
     */
    public String addProtocolMapperToClient(String realm, String clientId, ProtocolMapperRepresentation mapper) {
        try {
            ClientResource clientResource = keycloak.realm(realm).clients().get(clientId);
            ProtocolMappersResource protocolMappers = clientResource.getProtocolMappers();
            
            Response response = protocolMappers.createMapper(mapper);
            if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
                return "Successfully added protocol mapper to client: " + mapper.getName();
            } else {
                Log.error("Failed to add protocol mapper. Status: " + response.getStatus());
                response.close();
                return "Error adding protocol mapper: " + mapper.getName();
            }
        } catch (NotFoundException e) {
            return "Client not found: " + clientId;
        } catch (Exception e) {
            Log.error("Failed to add protocol mapper: " + mapper.getName(), e);
            return "Error adding protocol mapper: " + mapper.getName() + " - " + e.getMessage();
        }
    }

}
