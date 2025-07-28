package dev.shaaf.experimental.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.shaaf.experimental.service.ClientService;
import io.quarkiverse.mcp.server.ToolCallException;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.ProtocolMapperRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
public class ClientToolTest {

    @InjectMock
    ClientService clientService;

    @InjectMock
    ObjectMapper mapper;

    @Inject
    ClientTool clientTool;

    private static final String TEST_REALM = "test-realm";
    private static final String TEST_CLIENT_ID = "test-client";
    private static final String TEST_CLIENT_UUID = "test-client-uuid";

    @Test
    public void testGetClients_Success_ReturnsJsonString() throws Exception {
        // Arrange
        List<ClientRepresentation> clients = new ArrayList<>();
        ClientRepresentation client = new ClientRepresentation();
        client.setId(TEST_CLIENT_UUID);
        client.setClientId(TEST_CLIENT_ID);
        clients.add(client);

        when(clientService.getClients(TEST_REALM)).thenReturn(clients);
        when(mapper.writeValueAsString(clients)).thenReturn("[{\"id\":\"test-client-uuid\",\"clientId\":\"test-client\"}]");

        // Act
        String result = clientTool.getClients(TEST_REALM);

        // Assert
        assertEquals("[{\"id\":\"test-client-uuid\",\"clientId\":\"test-client\"}]", result);
        verify(clientService).getClients(TEST_REALM);
        verify(mapper).writeValueAsString(clients);
    }

    @Test
    public void testGetClients_MappingException_ThrowsToolCallException() throws Exception {
        // Arrange
        List<ClientRepresentation> clients = new ArrayList<>();
        when(clientService.getClients(TEST_REALM)).thenReturn(clients);
        when(mapper.writeValueAsString(clients)).thenThrow(new RuntimeException("Mapping error"));

        // Act & Assert
        assertThrows(ToolCallException.class, () -> clientTool.getClients(TEST_REALM));
        verify(clientService).getClients(TEST_REALM);
        verify(mapper).writeValueAsString(clients);
    }

    @Test
    public void testGetClient_Success_ReturnsJsonString() throws Exception {
        // Arrange
        ClientRepresentation client = new ClientRepresentation();
        client.setId(TEST_CLIENT_UUID);
        client.setClientId(TEST_CLIENT_ID);
        Optional<ClientRepresentation> optionalClient = Optional.of(client);

        when(clientService.findClientByClientId(TEST_REALM, TEST_CLIENT_ID)).thenReturn(optionalClient);
        when(mapper.writeValueAsString(client)).thenReturn("{\"id\":\"test-client-uuid\",\"clientId\":\"test-client\"}");

        // Act
        String result = clientTool.getClient(TEST_REALM, TEST_CLIENT_ID);

        // Assert
        assertEquals("{\"id\":\"test-client-uuid\",\"clientId\":\"test-client\"}", result);
        verify(clientService).findClientByClientId(TEST_REALM, TEST_CLIENT_ID);
        verify(mapper).writeValueAsString(client);
    }

    @Test
    public void testGetClient_ClientNotFound_ReturnsNullJson() throws Exception {
        // Arrange
        Optional<ClientRepresentation> optionalClient = Optional.empty();

        when(clientService.findClientByClientId(TEST_REALM, TEST_CLIENT_ID)).thenReturn(optionalClient);
        when(mapper.writeValueAsString(null)).thenReturn("null");

        // Act
        String result = clientTool.getClient(TEST_REALM, TEST_CLIENT_ID);

        // Assert
        assertEquals("null", result);
        verify(clientService).findClientByClientId(TEST_REALM, TEST_CLIENT_ID);
        verify(mapper).writeValueAsString(null);
    }

    @Test
    public void testGetClient_MappingException_ThrowsToolCallException() throws Exception {
        // Arrange
        ClientRepresentation client = new ClientRepresentation();
        client.setId(TEST_CLIENT_UUID);
        client.setClientId(TEST_CLIENT_ID);
        Optional<ClientRepresentation> optionalClient = Optional.of(client);

        when(clientService.findClientByClientId(TEST_REALM, TEST_CLIENT_ID)).thenReturn(optionalClient);
        when(mapper.writeValueAsString(client)).thenThrow(new RuntimeException("Mapping error"));

        // Act & Assert
        assertThrows(ToolCallException.class, () -> clientTool.getClient(TEST_REALM, TEST_CLIENT_ID));
        verify(clientService).findClientByClientId(TEST_REALM, TEST_CLIENT_ID);
        verify(mapper).writeValueAsString(client);
    }

    @Test
    public void testGetClientById_Success_ReturnsJsonString() throws Exception {
        // Arrange
        ClientRepresentation client = new ClientRepresentation();
        client.setId(TEST_CLIENT_UUID);
        client.setClientId(TEST_CLIENT_ID);

        when(clientService.getClient(TEST_REALM, TEST_CLIENT_UUID)).thenReturn(client);
        when(mapper.writeValueAsString(client)).thenReturn("{\"id\":\"test-client-uuid\",\"clientId\":\"test-client\"}");

        // Act
        String result = clientTool.getClientById(TEST_REALM, TEST_CLIENT_UUID);

        // Assert
        assertEquals("{\"id\":\"test-client-uuid\",\"clientId\":\"test-client\"}", result);
        verify(clientService).getClient(TEST_REALM, TEST_CLIENT_UUID);
        verify(mapper).writeValueAsString(client);
    }

    @Test
    public void testAddClient_Success_ReturnsSuccessMessage() {
        // Arrange
        String successMessage = "Successfully created client: " + TEST_CLIENT_ID;
        when(clientService.createClient(TEST_REALM, TEST_CLIENT_ID)).thenReturn(successMessage);

        // Act
        String result = clientTool.addClient(TEST_REALM, TEST_CLIENT_ID);

        // Assert
        assertEquals(successMessage, result);
        verify(clientService).createClient(TEST_REALM, TEST_CLIENT_ID);
    }

    @Test
    public void testUpdateClient_Success_ReturnsSuccessMessage() throws Exception {
        // Arrange
        String clientJson = "{\"id\":\"test-client-uuid\",\"clientId\":\"test-client\"}";
        ClientRepresentation clientRepresentation = new ClientRepresentation();
        clientRepresentation.setId(TEST_CLIENT_UUID);
        clientRepresentation.setClientId(TEST_CLIENT_ID);
        String successMessage = "Successfully updated client: " + TEST_CLIENT_UUID;

        when(mapper.readValue(clientJson, ClientRepresentation.class)).thenReturn(clientRepresentation);
        when(clientService.updateClient(TEST_REALM, TEST_CLIENT_UUID, clientRepresentation)).thenReturn(successMessage);

        // Act
        String result = clientTool.updateClient(TEST_REALM, TEST_CLIENT_UUID, clientJson);

        // Assert
        assertEquals(successMessage, result);
        verify(mapper).readValue(clientJson, ClientRepresentation.class);
        verify(clientService).updateClient(TEST_REALM, TEST_CLIENT_UUID, clientRepresentation);
    }

    @Test
    public void testDeleteClient_Success_ReturnsSuccessMessage() {
        // Arrange
        String successMessage = "Successfully deleted client: " + TEST_CLIENT_UUID;
        when(clientService.deleteClient(TEST_REALM, TEST_CLIENT_UUID)).thenReturn(successMessage);

        // Act
        String result = clientTool.deleteClient(TEST_REALM, TEST_CLIENT_UUID);

        // Assert
        assertEquals(successMessage, result);
        verify(clientService).deleteClient(TEST_REALM, TEST_CLIENT_UUID);
    }

    @Test
    public void testGetClientSecret_Success_ReturnsSecret() {
        // Arrange
        String secret = "client-secret";
        when(clientService.getClientSecret(TEST_REALM, TEST_CLIENT_UUID)).thenReturn(secret);

        // Act
        String result = clientTool.getClientSecret(TEST_REALM, TEST_CLIENT_UUID);

        // Assert
        assertEquals(secret, result);
        verify(clientService).getClientSecret(TEST_REALM, TEST_CLIENT_UUID);
    }

    @Test
    public void testGenerateNewClientSecret_Success_ReturnsNewSecret() {
        // Arrange
        String newSecret = "new-client-secret";
        when(clientService.generateNewClientSecret(TEST_REALM, TEST_CLIENT_UUID)).thenReturn(newSecret);

        // Act
        String result = clientTool.generateNewClientSecret(TEST_REALM, TEST_CLIENT_UUID);

        // Assert
        assertEquals(newSecret, result);
        verify(clientService).generateNewClientSecret(TEST_REALM, TEST_CLIENT_UUID);
    }

    @Test
    public void testGetClientRoles_Success_ReturnsJsonString() throws Exception {
        // Arrange
        List<RoleRepresentation> roles = new ArrayList<>();
        RoleRepresentation role = new RoleRepresentation();
        role.setId("role-id");
        role.setName("role-name");
        roles.add(role);

        when(clientService.getClientRoles(TEST_REALM, TEST_CLIENT_UUID)).thenReturn(roles);
        when(mapper.writeValueAsString(roles)).thenReturn("[{\"id\":\"role-id\",\"name\":\"role-name\"}]");

        // Act
        String result = clientTool.getClientRoles(TEST_REALM, TEST_CLIENT_UUID);

        // Assert
        assertEquals("[{\"id\":\"role-id\",\"name\":\"role-name\"}]", result);
        verify(clientService).getClientRoles(TEST_REALM, TEST_CLIENT_UUID);
        verify(mapper).writeValueAsString(roles);
    }

    @Test
    public void testCreateClientRole_Success_ReturnsSuccessMessage() {
        // Arrange
        String roleName = "role-name";
        String description = "role-description";
        String successMessage = "Successfully created client role: " + roleName;

        when(clientService.createClientRole(TEST_REALM, TEST_CLIENT_UUID, roleName, description)).thenReturn(successMessage);

        // Act
        String result = clientTool.createClientRole(TEST_REALM, TEST_CLIENT_UUID, roleName, description);

        // Assert
        assertEquals(successMessage, result);
        verify(clientService).createClientRole(TEST_REALM, TEST_CLIENT_UUID, roleName, description);
    }

    @Test
    public void testDeleteClientRole_Success_ReturnsSuccessMessage() {
        // Arrange
        String roleName = "role-name";
        String successMessage = "Successfully deleted client role: " + roleName;

        when(clientService.deleteClientRole(TEST_REALM, TEST_CLIENT_UUID, roleName)).thenReturn(successMessage);

        // Act
        String result = clientTool.deleteClientRole(TEST_REALM, TEST_CLIENT_UUID, roleName);

        // Assert
        assertEquals(successMessage, result);
        verify(clientService).deleteClientRole(TEST_REALM, TEST_CLIENT_UUID, roleName);
    }

    @Test
    public void testGetServiceAccountUser_Success_ReturnsJsonString() throws Exception {
        // Arrange
        UserRepresentation user = new UserRepresentation();
        user.setId("user-id");
        user.setUsername("service-account-" + TEST_CLIENT_ID);

        when(clientService.getServiceAccountUser(TEST_REALM, TEST_CLIENT_UUID)).thenReturn(user);
        when(mapper.writeValueAsString(user)).thenReturn("{\"id\":\"user-id\",\"username\":\"service-account-test-client\"}");

        // Act
        String result = clientTool.getServiceAccountUser(TEST_REALM, TEST_CLIENT_UUID);

        // Assert
        assertEquals("{\"id\":\"user-id\",\"username\":\"service-account-test-client\"}", result);
        verify(clientService).getServiceAccountUser(TEST_REALM, TEST_CLIENT_UUID);
        verify(mapper).writeValueAsString(user);
    }

    @Test
    public void testGetClientProtocolMappers_Success_ReturnsJsonString() throws Exception {
        // Arrange
        List<ProtocolMapperRepresentation> mappers = new ArrayList<>();
        ProtocolMapperRepresentation protocolMapper = new ProtocolMapperRepresentation();
        protocolMapper.setId("mapper-id");
        protocolMapper.setName("mapper-name");
        mappers.add(protocolMapper);

        when(clientService.getClientProtocolMappers(TEST_REALM, TEST_CLIENT_UUID)).thenReturn(mappers);
        when(mapper.writeValueAsString(mappers)).thenReturn("[{\"id\":\"mapper-id\",\"name\":\"mapper-name\"}]");

        // Act
        String result = clientTool.getClientProtocolMappers(TEST_REALM, TEST_CLIENT_UUID);

        // Assert
        assertEquals("[{\"id\":\"mapper-id\",\"name\":\"mapper-name\"}]", result);
        verify(clientService).getClientProtocolMappers(TEST_REALM, TEST_CLIENT_UUID);
        verify(mapper).writeValueAsString(mappers);
    }

    @Test
    public void testAddProtocolMapperToClient_Success_ReturnsSuccessMessage() throws Exception {
        // Arrange
        String mapperJson = "{\"id\":\"mapper-id\",\"name\":\"mapper-name\"}";
        ProtocolMapperRepresentation protocolMapper = new ProtocolMapperRepresentation();
        protocolMapper.setId("mapper-id");
        protocolMapper.setName("mapper-name");
        String successMessage = "Successfully added protocol mapper to client: " + protocolMapper.getName();

        when(mapper.readValue(mapperJson, ProtocolMapperRepresentation.class)).thenReturn(protocolMapper);
        when(clientService.addProtocolMapperToClient(TEST_REALM, TEST_CLIENT_UUID, protocolMapper)).thenReturn(successMessage);

        // Act
        String result = clientTool.addProtocolMapperToClient(TEST_REALM, TEST_CLIENT_UUID, mapperJson);

        // Assert
        assertEquals(successMessage, result);
        verify(mapper).readValue(mapperJson, ProtocolMapperRepresentation.class);
        verify(clientService).addProtocolMapperToClient(TEST_REALM, TEST_CLIENT_UUID, protocolMapper);
    }
}