package dev.shaaf.experimental.service;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.admin.client.resource.ProtocolMappersResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.ProtocolMapperRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
public class ClientServiceTest {

    @InjectMock
    Keycloak keycloak;

    @Inject
    ClientService clientService;

    private RealmResource realmResource;
    private ClientsResource clientsResource;
    private ClientResource clientResource;
    private RolesResource rolesResource;
    private ProtocolMappersResource protocolMappersResource;
    private Response response;

    private static final String TEST_REALM = "test-realm";
    private static final String TEST_CLIENT_ID = "test-client";
    private static final String TEST_CLIENT_UUID = "client-uuid";
    private static final String TEST_CLIENT_NAME = "Test Client";
    private static final String TEST_ROLE_NAME = "test-role";
    private static final String TEST_ROLE_DESCRIPTION = "Test Role Description";
    private static final String TEST_SECRET = "test-secret";

    @BeforeEach
    public void setup() {
        // Mock the Keycloak client chain
        realmResource = mock(RealmResource.class);
        clientsResource = mock(ClientsResource.class);
        clientResource = mock(ClientResource.class);
        rolesResource = mock(RolesResource.class);
        protocolMappersResource = mock(ProtocolMappersResource.class);
        response = mock(Response.class);

        when(keycloak.realm(anyString())).thenReturn(realmResource);
        when(realmResource.clients()).thenReturn(clientsResource);
        when(clientsResource.get(anyString())).thenReturn(clientResource);
        when(clientResource.roles()).thenReturn(rolesResource);
        when(clientResource.getProtocolMappers()).thenReturn(protocolMappersResource);
    }

    @Test
    public void testGetClients_ReturnsClientList() {
        // Arrange
        List<ClientRepresentation> expectedClients = new ArrayList<>();
        ClientRepresentation client = new ClientRepresentation();
        client.setId("client-id");
        client.setClientId(TEST_CLIENT_ID);
        client.setName(TEST_CLIENT_NAME);
        expectedClients.add(client);

        when(clientsResource.findAll()).thenReturn(expectedClients);

        // Act
        List<ClientRepresentation> actualClients = clientService.getClients(TEST_REALM);

        // Assert
        assertEquals(expectedClients, actualClients);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).clients();
        verify(clientsResource).findAll();
    }

    @Test
    public void testFindClientByClientId_ClientExists_ReturnsClient() {
        // Arrange
        List<ClientRepresentation> clients = new ArrayList<>();
        ClientRepresentation client = new ClientRepresentation();
        client.setId("client-id");
        client.setClientId(TEST_CLIENT_ID);
        client.setName(TEST_CLIENT_NAME);
        clients.add(client);

        when(clientsResource.findByClientId(TEST_CLIENT_ID)).thenReturn(clients);

        // Act
        Optional<ClientRepresentation> result = clientService.findClientByClientId(TEST_REALM, TEST_CLIENT_ID);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(TEST_CLIENT_ID, result.get().getClientId());
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).clients();
        verify(clientsResource).findByClientId(TEST_CLIENT_ID);
    }

    @Test
    public void testFindClientByClientId_ClientDoesNotExist_ReturnsEmptyOptional() {
        // Arrange
        when(clientsResource.findByClientId(TEST_CLIENT_ID)).thenReturn(new ArrayList<>());

        // Act
        Optional<ClientRepresentation> result = clientService.findClientByClientId(TEST_REALM, TEST_CLIENT_ID);

        // Assert
        assertFalse(result.isPresent());
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).clients();
        verify(clientsResource).findByClientId(TEST_CLIENT_ID);
    }

    @Test
    public void testCreateClient_Success_ReturnsSuccessMessage() {
        // Arrange
        when(clientsResource.create(any(ClientRepresentation.class))).thenReturn(response);
        when(response.getStatus()).thenReturn(Response.Status.CREATED.getStatusCode());

        // Act
        String result = clientService.createClient(TEST_REALM, TEST_CLIENT_ID);

        // Assert
        assertEquals("Successfully created client: " + TEST_CLIENT_ID, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).clients();
        verify(clientsResource).create(any(ClientRepresentation.class));
        verify(response).getStatus();
    }

    @Test
    public void testCreateClient_Failure_ReturnsErrorMessage() {
        // Arrange
        when(clientsResource.create(any(ClientRepresentation.class))).thenReturn(response);
        when(response.getStatus()).thenReturn(Response.Status.BAD_REQUEST.getStatusCode());

        // Act
        String result = clientService.createClient(TEST_REALM, TEST_CLIENT_ID);

        // Assert
        assertEquals("Error creating client: " + TEST_CLIENT_ID, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).clients();
        verify(clientsResource).create(any(ClientRepresentation.class));
        verify(response, times(2)).getStatus();
        verify(response).close();
    }
    
    @Test
    public void testGetClient_Success_ReturnsClient() {
        // Arrange
        ClientRepresentation expectedClient = new ClientRepresentation();
        expectedClient.setId(TEST_CLIENT_UUID);
        expectedClient.setClientId(TEST_CLIENT_ID);
        expectedClient.setName(TEST_CLIENT_NAME);
        
        when(clientResource.toRepresentation()).thenReturn(expectedClient);
        
        // Act
        ClientRepresentation actualClient = clientService.getClient(TEST_REALM, TEST_CLIENT_UUID);
        
        // Assert
        assertNotNull(actualClient);
        assertEquals(TEST_CLIENT_UUID, actualClient.getId());
        assertEquals(TEST_CLIENT_ID, actualClient.getClientId());
        assertEquals(TEST_CLIENT_NAME, actualClient.getName());
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).clients();
        verify(clientsResource).get(TEST_CLIENT_UUID);
        verify(clientResource).toRepresentation();
    }
    
    @Test
    public void testGetClient_NotFound_ReturnsNull() {
        // Arrange
        when(clientResource.toRepresentation()).thenThrow(new NotFoundException("Client not found"));
        
        // Act
        ClientRepresentation actualClient = clientService.getClient(TEST_REALM, TEST_CLIENT_UUID);
        
        // Assert
        assertNull(actualClient);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).clients();
        verify(clientsResource).get(TEST_CLIENT_UUID);
        verify(clientResource).toRepresentation();
    }
    
    @Test
    public void testGetClient_Exception_ReturnsNull() {
        // Arrange
        when(clientResource.toRepresentation()).thenThrow(new RuntimeException("Test exception"));
        
        // Act
        ClientRepresentation actualClient = clientService.getClient(TEST_REALM, TEST_CLIENT_UUID);
        
        // Assert
        assertNull(actualClient);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).clients();
        verify(clientsResource).get(TEST_CLIENT_UUID);
        verify(clientResource).toRepresentation();
    }
    
    @Test
    public void testUpdateClient_Success_ReturnsSuccessMessage() {
        // Arrange
        ClientRepresentation clientRepresentation = new ClientRepresentation();
        clientRepresentation.setId(TEST_CLIENT_UUID);
        clientRepresentation.setClientId(TEST_CLIENT_ID);
        clientRepresentation.setName(TEST_CLIENT_NAME);
        
        doNothing().when(clientResource).update(any(ClientRepresentation.class));
        
        // Act
        String result = clientService.updateClient(TEST_REALM, TEST_CLIENT_UUID, clientRepresentation);
        
        // Assert
        assertEquals("Successfully updated client: " + TEST_CLIENT_UUID, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).clients();
        verify(clientsResource).get(TEST_CLIENT_UUID);
        verify(clientResource).update(clientRepresentation);
    }
    
    @Test
    public void testUpdateClient_NotFound_ReturnsErrorMessage() {
        // Arrange
        ClientRepresentation clientRepresentation = new ClientRepresentation();
        clientRepresentation.setId(TEST_CLIENT_UUID);
        clientRepresentation.setClientId(TEST_CLIENT_ID);
        clientRepresentation.setName(TEST_CLIENT_NAME);
        
        doThrow(new NotFoundException("Client not found"))
            .when(clientResource).update(any(ClientRepresentation.class));
        
        // Act
        String result = clientService.updateClient(TEST_REALM, TEST_CLIENT_UUID, clientRepresentation);
        
        // Assert
        assertEquals("Client not found: " + TEST_CLIENT_UUID, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).clients();
        verify(clientsResource).get(TEST_CLIENT_UUID);
        verify(clientResource).update(clientRepresentation);
    }
    
    @Test
    public void testUpdateClient_Exception_ReturnsErrorMessage() {
        // Arrange
        ClientRepresentation clientRepresentation = new ClientRepresentation();
        clientRepresentation.setId(TEST_CLIENT_UUID);
        clientRepresentation.setClientId(TEST_CLIENT_ID);
        clientRepresentation.setName(TEST_CLIENT_NAME);
        
        doThrow(new RuntimeException("Test exception"))
            .when(clientResource).update(any(ClientRepresentation.class));
        
        // Act
        String result = clientService.updateClient(TEST_REALM, TEST_CLIENT_UUID, clientRepresentation);
        
        // Assert
        assertTrue(result.startsWith("Error updating client: " + TEST_CLIENT_UUID));
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).clients();
        verify(clientsResource).get(TEST_CLIENT_UUID);
        verify(clientResource).update(clientRepresentation);
    }
    
    @Test
    public void testDeleteClient_Success_ReturnsSuccessMessage() {
        // Arrange
        ClientRepresentation client = new ClientRepresentation();
        client.setId(TEST_CLIENT_UUID);
        client.setClientId(TEST_CLIENT_ID);
        client.setName(TEST_CLIENT_NAME);
        
        when(clientResource.toRepresentation()).thenReturn(client);
        doNothing().when(clientResource).remove();
        
        // Act
        String result = clientService.deleteClient(TEST_REALM, TEST_CLIENT_UUID);
        
        // Assert
        assertEquals("Successfully deleted client: " + TEST_CLIENT_UUID, result);
        verify(keycloak, times(2)).realm(TEST_REALM);
        verify(realmResource, times(2)).clients();
        verify(clientsResource, times(2)).get(TEST_CLIENT_UUID);
        verify(clientResource).toRepresentation();
        verify(clientResource).remove();
    }
    
    @Test
    public void testDeleteClient_ClientNotFound_ReturnsErrorMessage() {
        // Arrange
        when(clientResource.toRepresentation()).thenReturn(null);
        
        // Act
        String result = clientService.deleteClient(TEST_REALM, TEST_CLIENT_UUID);
        
        // Assert
        assertEquals("Client not found: " + TEST_CLIENT_UUID, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).clients();
        verify(clientsResource).get(TEST_CLIENT_UUID);
        verify(clientResource).toRepresentation();
    }
    
    @Test
    public void testDeleteClient_NotFoundException_ReturnsErrorMessage() {
        // Arrange
        ClientRepresentation client = new ClientRepresentation();
        client.setId(TEST_CLIENT_UUID);
        client.setClientId(TEST_CLIENT_ID);
        client.setName(TEST_CLIENT_NAME);
        
        when(clientResource.toRepresentation()).thenReturn(client);
        doThrow(new NotFoundException("Client not found"))
            .when(clientResource).remove();
        
        // Act
        String result = clientService.deleteClient(TEST_REALM, TEST_CLIENT_UUID);
        
        // Assert
        assertEquals("Client not found: " + TEST_CLIENT_UUID, result);
        verify(keycloak, times(2)).realm(TEST_REALM);
        verify(realmResource, times(2)).clients();
        verify(clientsResource, times(2)).get(TEST_CLIENT_UUID);
        verify(clientResource).toRepresentation();
        verify(clientResource).remove();
    }
    
    @Test
    public void testDeleteClient_Exception_ReturnsErrorMessage() {
        // Arrange
        ClientRepresentation client = new ClientRepresentation();
        client.setId(TEST_CLIENT_UUID);
        client.setClientId(TEST_CLIENT_ID);
        client.setName(TEST_CLIENT_NAME);
        
        when(clientResource.toRepresentation()).thenReturn(client);
        doThrow(new RuntimeException("Test exception"))
            .when(clientResource).remove();
        
        // Act
        String result = clientService.deleteClient(TEST_REALM, TEST_CLIENT_UUID);
        
        // Assert
        assertTrue(result.startsWith("Error deleting client: " + TEST_CLIENT_UUID));
        verify(keycloak, times(2)).realm(TEST_REALM);
        verify(realmResource, times(2)).clients();
        verify(clientsResource, times(2)).get(TEST_CLIENT_UUID);
        verify(clientResource).toRepresentation();
        verify(clientResource).remove();
    }
    
    @Test
    public void testGetClientSecret_Success_ReturnsSecret() {
        // Arrange
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setValue(TEST_SECRET);
        
        when(clientResource.getSecret()).thenReturn(credential);
        
        // Act
        String result = clientService.getClientSecret(TEST_REALM, TEST_CLIENT_UUID);
        
        // Assert
        assertEquals(TEST_SECRET, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).clients();
        verify(clientsResource).get(TEST_CLIENT_UUID);
        verify(clientResource).getSecret();
    }
    
    @Test
    public void testGetClientSecret_NullCredential_ReturnsErrorMessage() {
        // Arrange
        when(clientResource.getSecret()).thenReturn(null);
        
        // Act
        String result = clientService.getClientSecret(TEST_REALM, TEST_CLIENT_UUID);
        
        // Assert
        assertEquals("Client does not have a secret or is not a confidential client", result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).clients();
        verify(clientsResource).get(TEST_CLIENT_UUID);
        verify(clientResource).getSecret();
    }
    
    @Test
    public void testGetClientSecret_NullValue_ReturnsErrorMessage() {
        // Arrange
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setValue(null);
        
        when(clientResource.getSecret()).thenReturn(credential);
        
        // Act
        String result = clientService.getClientSecret(TEST_REALM, TEST_CLIENT_UUID);
        
        // Assert
        assertEquals("Client does not have a secret or is not a confidential client", result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).clients();
        verify(clientsResource).get(TEST_CLIENT_UUID);
        verify(clientResource).getSecret();
    }
    
    @Test
    public void testGetClientSecret_NotFound_ReturnsErrorMessage() {
        // Arrange
        when(clientResource.getSecret()).thenThrow(new NotFoundException("Client not found"));
        
        // Act
        String result = clientService.getClientSecret(TEST_REALM, TEST_CLIENT_UUID);
        
        // Assert
        assertEquals("Client not found: " + TEST_CLIENT_UUID, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).clients();
        verify(clientsResource).get(TEST_CLIENT_UUID);
        verify(clientResource).getSecret();
    }
    
    @Test
    public void testGetClientSecret_Exception_ReturnsErrorMessage() {
        // Arrange
        when(clientResource.getSecret()).thenThrow(new RuntimeException("Test exception"));
        
        // Act
        String result = clientService.getClientSecret(TEST_REALM, TEST_CLIENT_UUID);
        
        // Assert
        assertTrue(result.startsWith("Error getting client secret: " + TEST_CLIENT_UUID));
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).clients();
        verify(clientsResource).get(TEST_CLIENT_UUID);
        verify(clientResource).getSecret();
    }
    
    @Test
    public void testGenerateNewClientSecret_Success_ReturnsSecret() {
        // Arrange
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setValue(TEST_SECRET);
        
        when(clientResource.generateNewSecret()).thenReturn(credential);
        
        // Act
        String result = clientService.generateNewClientSecret(TEST_REALM, TEST_CLIENT_UUID);
        
        // Assert
        assertEquals(TEST_SECRET, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).clients();
        verify(clientsResource).get(TEST_CLIENT_UUID);
        verify(clientResource).generateNewSecret();
    }
    
    @Test
    public void testGenerateNewClientSecret_NullCredential_ReturnsErrorMessage() {
        // Arrange
        when(clientResource.generateNewSecret()).thenReturn(null);
        
        // Act
        String result = clientService.generateNewClientSecret(TEST_REALM, TEST_CLIENT_UUID);
        
        // Assert
        assertEquals("Failed to generate new secret for client", result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).clients();
        verify(clientsResource).get(TEST_CLIENT_UUID);
        verify(clientResource).generateNewSecret();
    }
    
    @Test
    public void testGenerateNewClientSecret_NullValue_ReturnsErrorMessage() {
        // Arrange
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setValue(null);
        
        when(clientResource.generateNewSecret()).thenReturn(credential);
        
        // Act
        String result = clientService.generateNewClientSecret(TEST_REALM, TEST_CLIENT_UUID);
        
        // Assert
        assertEquals("Failed to generate new secret for client", result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).clients();
        verify(clientsResource).get(TEST_CLIENT_UUID);
        verify(clientResource).generateNewSecret();
    }
    
    @Test
    public void testGenerateNewClientSecret_NotFound_ReturnsErrorMessage() {
        // Arrange
        when(clientResource.generateNewSecret()).thenThrow(new NotFoundException("Client not found"));
        
        // Act
        String result = clientService.generateNewClientSecret(TEST_REALM, TEST_CLIENT_UUID);
        
        // Assert
        assertEquals("Client not found: " + TEST_CLIENT_UUID, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).clients();
        verify(clientsResource).get(TEST_CLIENT_UUID);
        verify(clientResource).generateNewSecret();
    }
    
    @Test
    public void testGenerateNewClientSecret_Exception_ReturnsErrorMessage() {
        // Arrange
        when(clientResource.generateNewSecret()).thenThrow(new RuntimeException("Test exception"));
        
        // Act
        String result = clientService.generateNewClientSecret(TEST_REALM, TEST_CLIENT_UUID);
        
        // Assert
        assertTrue(result.startsWith("Error generating new client secret: " + TEST_CLIENT_UUID));
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).clients();
        verify(clientsResource).get(TEST_CLIENT_UUID);
        verify(clientResource).generateNewSecret();
    }
    
    @Test
    public void testGetClientRoles_Success_ReturnsRolesList() {
        // Arrange
        List<RoleRepresentation> expectedRoles = new ArrayList<>();
        RoleRepresentation role = new RoleRepresentation();
        role.setName(TEST_ROLE_NAME);
        role.setDescription(TEST_ROLE_DESCRIPTION);
        expectedRoles.add(role);
        
        when(rolesResource.list()).thenReturn(expectedRoles);
        
        // Act
        List<RoleRepresentation> actualRoles = clientService.getClientRoles(TEST_REALM, TEST_CLIENT_UUID);
        
        // Assert
        assertEquals(1, actualRoles.size());
        assertEquals(TEST_ROLE_NAME, actualRoles.get(0).getName());
        assertEquals(TEST_ROLE_DESCRIPTION, actualRoles.get(0).getDescription());
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).clients();
        verify(clientsResource).get(TEST_CLIENT_UUID);
        verify(clientResource).roles();
        verify(rolesResource).list();
    }
    
    @Test
    public void testGetClientRoles_EmptyList_ReturnsEmptyList() {
        // Arrange
        List<RoleRepresentation> expectedRoles = new ArrayList<>();
        
        when(rolesResource.list()).thenReturn(expectedRoles);
        
        // Act
        List<RoleRepresentation> actualRoles = clientService.getClientRoles(TEST_REALM, TEST_CLIENT_UUID);
        
        // Assert
        assertTrue(actualRoles.isEmpty());
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).clients();
        verify(clientsResource).get(TEST_CLIENT_UUID);
        verify(clientResource).roles();
        verify(rolesResource).list();
    }
    
    @Test
    public void testGetClientRoles_NotFound_ReturnsEmptyList() {
        // Arrange
        when(rolesResource.list()).thenThrow(new NotFoundException("Client not found"));
        
        // Act
        List<RoleRepresentation> actualRoles = clientService.getClientRoles(TEST_REALM, TEST_CLIENT_UUID);
        
        // Assert
        assertTrue(actualRoles.isEmpty());
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).clients();
        verify(clientsResource).get(TEST_CLIENT_UUID);
        verify(clientResource).roles();
        verify(rolesResource).list();
    }
    
    @Test
    public void testGetClientRoles_Exception_ReturnsEmptyList() {
        // Arrange
        when(rolesResource.list()).thenThrow(new RuntimeException("Test exception"));
        
        // Act
        List<RoleRepresentation> actualRoles = clientService.getClientRoles(TEST_REALM, TEST_CLIENT_UUID);
        
        // Assert
        assertTrue(actualRoles.isEmpty());
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).clients();
        verify(clientsResource).get(TEST_CLIENT_UUID);
        verify(clientResource).roles();
        verify(rolesResource).list();
    }
    
    @Test
    public void testCreateClientRole_Success_ReturnsSuccessMessage() {
        // Arrange
        doNothing().when(rolesResource).create(any(RoleRepresentation.class));
        
        // Act
        String result = clientService.createClientRole(TEST_REALM, TEST_CLIENT_UUID, TEST_ROLE_NAME, TEST_ROLE_DESCRIPTION);
        
        // Assert
        assertEquals("Successfully created client role: " + TEST_ROLE_NAME, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).clients();
        verify(clientsResource).get(TEST_CLIENT_UUID);
        verify(clientResource).roles();
        verify(rolesResource).create(any(RoleRepresentation.class));
    }
    
    @Test
    public void testCreateClientRole_NotFound_ReturnsErrorMessage() {
        // Arrange
        doThrow(new NotFoundException("Client not found"))
            .when(rolesResource).create(any(RoleRepresentation.class));
        
        // Act
        String result = clientService.createClientRole(TEST_REALM, TEST_CLIENT_UUID, TEST_ROLE_NAME, TEST_ROLE_DESCRIPTION);
        
        // Assert
        assertEquals("Client not found: " + TEST_CLIENT_UUID, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).clients();
        verify(clientsResource).get(TEST_CLIENT_UUID);
        verify(clientResource).roles();
        verify(rolesResource).create(any(RoleRepresentation.class));
    }
    
    @Test
    public void testCreateClientRole_Exception_ReturnsErrorMessage() {
        // Arrange
        doThrow(new RuntimeException("Test exception"))
            .when(rolesResource).create(any(RoleRepresentation.class));
        
        // Act
        String result = clientService.createClientRole(TEST_REALM, TEST_CLIENT_UUID, TEST_ROLE_NAME, TEST_ROLE_DESCRIPTION);
        
        // Assert
        assertTrue(result.startsWith("Error creating client role: " + TEST_ROLE_NAME));
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).clients();
        verify(clientsResource).get(TEST_CLIENT_UUID);
        verify(clientResource).roles();
        verify(rolesResource).create(any(RoleRepresentation.class));
    }
    
    @Test
    public void testDeleteClientRole_Success_ReturnsSuccessMessage() {
        // Arrange
        doNothing().when(rolesResource).deleteRole(TEST_ROLE_NAME);
        
        // Act
        String result = clientService.deleteClientRole(TEST_REALM, TEST_CLIENT_UUID, TEST_ROLE_NAME);
        
        // Assert
        assertEquals("Successfully deleted client role: " + TEST_ROLE_NAME, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).clients();
        verify(clientsResource).get(TEST_CLIENT_UUID);
        verify(clientResource).roles();
        verify(rolesResource).deleteRole(TEST_ROLE_NAME);
    }
    
    @Test
    public void testDeleteClientRole_NotFound_ReturnsErrorMessage() {
        // Arrange
        doThrow(new NotFoundException("Role not found"))
            .when(rolesResource).deleteRole(TEST_ROLE_NAME);
        
        // Act
        String result = clientService.deleteClientRole(TEST_REALM, TEST_CLIENT_UUID, TEST_ROLE_NAME);
        
        // Assert
        assertEquals("Client or role not found: " + TEST_CLIENT_UUID + " -> " + TEST_ROLE_NAME, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).clients();
        verify(clientsResource).get(TEST_CLIENT_UUID);
        verify(clientResource).roles();
        verify(rolesResource).deleteRole(TEST_ROLE_NAME);
    }
    
    @Test
    public void testDeleteClientRole_Exception_ReturnsErrorMessage() {
        // Arrange
        doThrow(new RuntimeException("Test exception"))
            .when(rolesResource).deleteRole(TEST_ROLE_NAME);
        
        // Act
        String result = clientService.deleteClientRole(TEST_REALM, TEST_CLIENT_UUID, TEST_ROLE_NAME);
        
        // Assert
        assertTrue(result.startsWith("Error deleting client role: " + TEST_ROLE_NAME));
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).clients();
        verify(clientsResource).get(TEST_CLIENT_UUID);
        verify(clientResource).roles();
        verify(rolesResource).deleteRole(TEST_ROLE_NAME);
    }
    
    @Test
    public void testGetServiceAccountUser_Success_ReturnsUser() {
        // Arrange
        UserRepresentation expectedUser = new UserRepresentation();
        expectedUser.setId("user-id");
        expectedUser.setUsername("service-account-" + TEST_CLIENT_ID);
        
        when(clientResource.getServiceAccountUser()).thenReturn(expectedUser);
        
        // Act
        UserRepresentation actualUser = clientService.getServiceAccountUser(TEST_REALM, TEST_CLIENT_UUID);
        
        // Assert
        assertNotNull(actualUser);
        assertEquals("user-id", actualUser.getId());
        assertEquals("service-account-" + TEST_CLIENT_ID, actualUser.getUsername());
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).clients();
        verify(clientsResource).get(TEST_CLIENT_UUID);
        verify(clientResource).getServiceAccountUser();
    }
    
    @Test
    public void testGetServiceAccountUser_NotFound_ReturnsNull() {
        // Arrange
        when(clientResource.getServiceAccountUser()).thenThrow(new NotFoundException("Client not found"));
        
        // Act
        UserRepresentation actualUser = clientService.getServiceAccountUser(TEST_REALM, TEST_CLIENT_UUID);
        
        // Assert
        assertNull(actualUser);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).clients();
        verify(clientsResource).get(TEST_CLIENT_UUID);
        verify(clientResource).getServiceAccountUser();
    }
    
    @Test
    public void testGetServiceAccountUser_Exception_ReturnsNull() {
        // Arrange
        when(clientResource.getServiceAccountUser()).thenThrow(new RuntimeException("Test exception"));
        
        // Act
        UserRepresentation actualUser = clientService.getServiceAccountUser(TEST_REALM, TEST_CLIENT_UUID);
        
        // Assert
        assertNull(actualUser);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).clients();
        verify(clientsResource).get(TEST_CLIENT_UUID);
        verify(clientResource).getServiceAccountUser();
    }
    
    @Test
    public void testGetClientProtocolMappers_Success_ReturnsMappersList() {
        // Arrange
        List<ProtocolMapperRepresentation> expectedMappers = new ArrayList<>();
        ProtocolMapperRepresentation mapper = new ProtocolMapperRepresentation();
        mapper.setId("mapper-id");
        mapper.setName("test-mapper");
        mapper.setProtocol("openid-connect");
        expectedMappers.add(mapper);
        
        when(protocolMappersResource.getMappers()).thenReturn(expectedMappers);
        
        // Act
        List<ProtocolMapperRepresentation> actualMappers = clientService.getClientProtocolMappers(TEST_REALM, TEST_CLIENT_UUID);
        
        // Assert
        assertEquals(1, actualMappers.size());
        assertEquals("mapper-id", actualMappers.get(0).getId());
        assertEquals("test-mapper", actualMappers.get(0).getName());
        assertEquals("openid-connect", actualMappers.get(0).getProtocol());
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).clients();
        verify(clientsResource).get(TEST_CLIENT_UUID);
        verify(clientResource).getProtocolMappers();
        verify(protocolMappersResource).getMappers();
    }
    
    @Test
    public void testGetClientProtocolMappers_EmptyList_ReturnsEmptyList() {
        // Arrange
        List<ProtocolMapperRepresentation> expectedMappers = new ArrayList<>();
        
        when(protocolMappersResource.getMappers()).thenReturn(expectedMappers);
        
        // Act
        List<ProtocolMapperRepresentation> actualMappers = clientService.getClientProtocolMappers(TEST_REALM, TEST_CLIENT_UUID);
        
        // Assert
        assertTrue(actualMappers.isEmpty());
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).clients();
        verify(clientsResource).get(TEST_CLIENT_UUID);
        verify(clientResource).getProtocolMappers();
        verify(protocolMappersResource).getMappers();
    }
    
    @Test
    public void testGetClientProtocolMappers_NotFound_ReturnsEmptyList() {
        // Arrange
        when(protocolMappersResource.getMappers()).thenThrow(new NotFoundException("Client not found"));
        
        // Act
        List<ProtocolMapperRepresentation> actualMappers = clientService.getClientProtocolMappers(TEST_REALM, TEST_CLIENT_UUID);
        
        // Assert
        assertTrue(actualMappers.isEmpty());
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).clients();
        verify(clientsResource).get(TEST_CLIENT_UUID);
        verify(clientResource).getProtocolMappers();
        verify(protocolMappersResource).getMappers();
    }
    
    @Test
    public void testGetClientProtocolMappers_Exception_ReturnsEmptyList() {
        // Arrange
        when(protocolMappersResource.getMappers()).thenThrow(new RuntimeException("Test exception"));
        
        // Act
        List<ProtocolMapperRepresentation> actualMappers = clientService.getClientProtocolMappers(TEST_REALM, TEST_CLIENT_UUID);
        
        // Assert
        assertTrue(actualMappers.isEmpty());
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).clients();
        verify(clientsResource).get(TEST_CLIENT_UUID);
        verify(clientResource).getProtocolMappers();
        verify(protocolMappersResource).getMappers();
    }
    
    @Test
    public void testAddProtocolMapperToClient_Success_ReturnsSuccessMessage() {
        // Arrange
        ProtocolMapperRepresentation mapper = new ProtocolMapperRepresentation();
        mapper.setId("mapper-id");
        mapper.setName("test-mapper");
        mapper.setProtocol("openid-connect");
        
        when(protocolMappersResource.createMapper(any(ProtocolMapperRepresentation.class))).thenReturn(response);
        when(response.getStatus()).thenReturn(Response.Status.CREATED.getStatusCode());
        
        // Act
        String result = clientService.addProtocolMapperToClient(TEST_REALM, TEST_CLIENT_UUID, mapper);
        
        // Assert
        assertEquals("Successfully added protocol mapper to client: test-mapper", result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).clients();
        verify(clientsResource).get(TEST_CLIENT_UUID);
        verify(clientResource).getProtocolMappers();
        verify(protocolMappersResource).createMapper(mapper);
        verify(response).getStatus();
    }
    
    @Test
    public void testAddProtocolMapperToClient_Failure_ReturnsErrorMessage() {
        // Arrange
        ProtocolMapperRepresentation mapper = new ProtocolMapperRepresentation();
        mapper.setId("mapper-id");
        mapper.setName("test-mapper");
        mapper.setProtocol("openid-connect");
        
        when(protocolMappersResource.createMapper(any(ProtocolMapperRepresentation.class))).thenReturn(response);
        when(response.getStatus()).thenReturn(Response.Status.BAD_REQUEST.getStatusCode());
        
        // Act
        String result = clientService.addProtocolMapperToClient(TEST_REALM, TEST_CLIENT_UUID, mapper);
        
        // Assert
        assertEquals("Error adding protocol mapper: test-mapper", result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).clients();
        verify(clientsResource).get(TEST_CLIENT_UUID);
        verify(clientResource).getProtocolMappers();
        verify(protocolMappersResource).createMapper(mapper);
        verify(response, times(2)).getStatus();
        verify(response).close();
    }
    
    @Test
    public void testAddProtocolMapperToClient_NotFound_ReturnsErrorMessage() {
        // Arrange
        ProtocolMapperRepresentation mapper = new ProtocolMapperRepresentation();
        mapper.setId("mapper-id");
        mapper.setName("test-mapper");
        mapper.setProtocol("openid-connect");
        
        when(protocolMappersResource.createMapper(any(ProtocolMapperRepresentation.class)))
            .thenThrow(new NotFoundException("Client not found"));
        
        // Act
        String result = clientService.addProtocolMapperToClient(TEST_REALM, TEST_CLIENT_UUID, mapper);
        
        // Assert
        assertEquals("Client not found: " + TEST_CLIENT_UUID, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).clients();
        verify(clientsResource).get(TEST_CLIENT_UUID);
        verify(clientResource).getProtocolMappers();
        verify(protocolMappersResource).createMapper(mapper);
    }
    
    @Test
    public void testAddProtocolMapperToClient_Exception_ReturnsErrorMessage() {
        // Arrange
        ProtocolMapperRepresentation mapper = new ProtocolMapperRepresentation();
        mapper.setId("mapper-id");
        mapper.setName("test-mapper");
        mapper.setProtocol("openid-connect");
        
        when(protocolMappersResource.createMapper(any(ProtocolMapperRepresentation.class)))
            .thenThrow(new RuntimeException("Test exception"));
        
        // Act
        String result = clientService.addProtocolMapperToClient(TEST_REALM, TEST_CLIENT_UUID, mapper);
        
        // Assert
        assertTrue(result.startsWith("Error adding protocol mapper: test-mapper"));
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).clients();
        verify(clientsResource).get(TEST_CLIENT_UUID);
        verify(clientResource).getProtocolMappers();
        verify(protocolMappersResource).createMapper(mapper);
    }
}