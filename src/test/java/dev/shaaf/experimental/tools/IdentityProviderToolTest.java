package dev.shaaf.experimental.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.shaaf.experimental.service.IdentityProviderService;
import io.quarkiverse.mcp.server.ToolCallException;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.IdentityProviderMapperRepresentation;
import org.keycloak.representations.idm.IdentityProviderRepresentation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
public class IdentityProviderToolTest {

    @InjectMock
    IdentityProviderService identityProviderService;

    @InjectMock
    ObjectMapper mapper;

    @Inject
    IdentityProviderTool identityProviderTool;

    private static final String TEST_REALM = "test-realm";
    private static final String TEST_ALIAS = "test-idp";
    private static final String EXPECTED_JSON = "[{\"alias\":\"test-idp\",\"displayName\":\"Test IDP\"}]";
    private static final String SUCCESS_MESSAGE = "Identity provider created successfully";
    private static final String DELETE_SUCCESS_MESSAGE = "Identity provider deleted successfully";

    @Test
    public void testGetIdentityProviders_Success_ReturnsJsonString() throws Exception {
        // Arrange
        List<IdentityProviderRepresentation> idps = new ArrayList<>();
        IdentityProviderRepresentation idp = new IdentityProviderRepresentation();
        idp.setAlias(TEST_ALIAS);
        idp.setDisplayName("Test IDP");
        idps.add(idp);

        when(identityProviderService.getIdentityProviders(TEST_REALM)).thenReturn(idps);
        when(mapper.writeValueAsString(idps)).thenReturn(EXPECTED_JSON);

        // Act
        String result = identityProviderTool.getIdentityProviders(TEST_REALM);

        // Assert
        assertEquals(EXPECTED_JSON, result);
        verify(identityProviderService).getIdentityProviders(TEST_REALM);
        verify(mapper).writeValueAsString(idps);
    }

    @Test
    public void testGetIdentityProviders_MappingException_ThrowsToolCallException() throws Exception {
        // Arrange
        List<IdentityProviderRepresentation> idps = new ArrayList<>();
        when(identityProviderService.getIdentityProviders(TEST_REALM)).thenReturn(idps);
        when(mapper.writeValueAsString(idps)).thenThrow(new RuntimeException("Mapping error"));

        // Act & Assert
        assertThrows(ToolCallException.class, () -> identityProviderTool.getIdentityProviders(TEST_REALM));
        verify(identityProviderService).getIdentityProviders(TEST_REALM);
        verify(mapper).writeValueAsString(idps);
    }

    @Test
    public void testGetIdentityProvider_Success_ReturnsJsonString() throws Exception {
        // Arrange
        IdentityProviderRepresentation idp = new IdentityProviderRepresentation();
        idp.setAlias(TEST_ALIAS);
        idp.setDisplayName("Test IDP");

        when(identityProviderService.getIdentityProvider(TEST_REALM, TEST_ALIAS)).thenReturn(idp);
        when(mapper.writeValueAsString(idp)).thenReturn("{\"alias\":\"test-idp\",\"displayName\":\"Test IDP\"}");

        // Act
        String result = identityProviderTool.getIdentityProvider(TEST_REALM, TEST_ALIAS);

        // Assert
        assertEquals("{\"alias\":\"test-idp\",\"displayName\":\"Test IDP\"}", result);
        verify(identityProviderService).getIdentityProvider(TEST_REALM, TEST_ALIAS);
        verify(mapper).writeValueAsString(idp);
    }

    @Test
    public void testGetIdentityProvider_MappingException_ThrowsToolCallException() throws Exception {
        // Arrange
        IdentityProviderRepresentation idp = new IdentityProviderRepresentation();
        idp.setAlias(TEST_ALIAS);
        idp.setDisplayName("Test IDP");

        when(identityProviderService.getIdentityProvider(TEST_REALM, TEST_ALIAS)).thenReturn(idp);
        when(mapper.writeValueAsString(idp)).thenThrow(new RuntimeException("Mapping error"));

        // Act & Assert
        assertThrows(ToolCallException.class, () -> identityProviderTool.getIdentityProvider(TEST_REALM, TEST_ALIAS));
        verify(identityProviderService).getIdentityProvider(TEST_REALM, TEST_ALIAS);
        verify(mapper).writeValueAsString(idp);
    }

    @Test
    public void testCreateIdentityProvider_Success_ReturnsSuccessMessage() throws Exception {
        // Arrange
        String idpJson = "{\"alias\":\"test-idp\",\"displayName\":\"Test IDP\"}";
        IdentityProviderRepresentation idp = new IdentityProviderRepresentation();
        idp.setAlias(TEST_ALIAS);
        idp.setDisplayName("Test IDP");

        when(mapper.readValue(idpJson, IdentityProviderRepresentation.class)).thenReturn(idp);
        when(identityProviderService.createIdentityProvider(eq(TEST_REALM), any(IdentityProviderRepresentation.class)))
                .thenReturn(SUCCESS_MESSAGE);

        // Act
        String result = identityProviderTool.createIdentityProvider(TEST_REALM, idpJson);

        // Assert
        assertEquals(SUCCESS_MESSAGE, result);
        verify(mapper).readValue(idpJson, IdentityProviderRepresentation.class);
        verify(identityProviderService).createIdentityProvider(eq(TEST_REALM), any(IdentityProviderRepresentation.class));
    }

    @Test
    public void testCreateIdentityProvider_MappingException_ThrowsToolCallException() throws Exception {
        // Arrange
        String idpJson = "{\"alias\":\"test-idp\",\"displayName\":\"Test IDP\"}";
        when(mapper.readValue(anyString(), eq(IdentityProviderRepresentation.class)))
                .thenThrow(new RuntimeException("Mapping error"));

        // Act & Assert
        assertThrows(ToolCallException.class, () -> identityProviderTool.createIdentityProvider(TEST_REALM, idpJson));
        verify(mapper).readValue(idpJson, IdentityProviderRepresentation.class);
    }

    @Test
    public void testUpdateIdentityProvider_Success_ReturnsSuccessMessage() throws Exception {
        // Arrange
        String idpJson = "{\"alias\":\"test-idp\",\"displayName\":\"Updated IDP\"}";
        IdentityProviderRepresentation idp = new IdentityProviderRepresentation();
        idp.setAlias(TEST_ALIAS);
        idp.setDisplayName("Updated IDP");

        when(mapper.readValue(idpJson, IdentityProviderRepresentation.class)).thenReturn(idp);
        when(identityProviderService.updateIdentityProvider(eq(TEST_REALM), eq(TEST_ALIAS), any(IdentityProviderRepresentation.class)))
                .thenReturn("Successfully updated identity provider: " + TEST_ALIAS);

        // Act
        String result = identityProviderTool.updateIdentityProvider(TEST_REALM, TEST_ALIAS, idpJson);

        // Assert
        assertEquals("Successfully updated identity provider: " + TEST_ALIAS, result);
        verify(mapper).readValue(idpJson, IdentityProviderRepresentation.class);
        verify(identityProviderService).updateIdentityProvider(eq(TEST_REALM), eq(TEST_ALIAS), any(IdentityProviderRepresentation.class));
    }

    @Test
    public void testUpdateIdentityProvider_MappingException_ThrowsToolCallException() throws Exception {
        // Arrange
        String idpJson = "{\"alias\":\"test-idp\",\"displayName\":\"Updated IDP\"}";
        when(mapper.readValue(anyString(), eq(IdentityProviderRepresentation.class)))
                .thenThrow(new RuntimeException("Mapping error"));

        // Act & Assert
        assertThrows(ToolCallException.class, () -> identityProviderTool.updateIdentityProvider(TEST_REALM, TEST_ALIAS, idpJson));
        verify(mapper).readValue(idpJson, IdentityProviderRepresentation.class);
    }

    @Test
    public void testDeleteIdentityProvider_Success_ReturnsSuccessMessage() {
        // Arrange
        when(identityProviderService.deleteIdentityProvider(TEST_REALM, TEST_ALIAS)).thenReturn(DELETE_SUCCESS_MESSAGE);

        // Act
        String result = identityProviderTool.deleteIdentityProvider(TEST_REALM, TEST_ALIAS);

        // Assert
        assertEquals(DELETE_SUCCESS_MESSAGE, result);
        verify(identityProviderService).deleteIdentityProvider(TEST_REALM, TEST_ALIAS);
    }

    @Test
    public void testGetIdentityProviderMappers_Success_ReturnsJsonString() throws Exception {
        // Arrange
        List<IdentityProviderMapperRepresentation> mappers = new ArrayList<>();
        IdentityProviderMapperRepresentation mapperRep = new IdentityProviderMapperRepresentation();
        mapperRep.setId("mapper-id");
        mapperRep.setName("Test Mapper");
        mappers.add(mapperRep);

        when(identityProviderService.getIdentityProviderMappers(TEST_REALM, TEST_ALIAS)).thenReturn(mappers);
        when(mapper.writeValueAsString(mappers)).thenReturn("[{\"id\":\"mapper-id\",\"name\":\"Test Mapper\"}]");

        // Act
        String result = identityProviderTool.getIdentityProviderMappers(TEST_REALM, TEST_ALIAS);

        // Assert
        assertEquals("[{\"id\":\"mapper-id\",\"name\":\"Test Mapper\"}]", result);
        verify(identityProviderService).getIdentityProviderMappers(TEST_REALM, TEST_ALIAS);
        verify(mapper).writeValueAsString(mappers);
    }

    @Test
    public void testGetIdentityProviderMappers_MappingException_ThrowsToolCallException() throws Exception {
        // Arrange
        List<IdentityProviderMapperRepresentation> mappers = new ArrayList<>();
        when(identityProviderService.getIdentityProviderMappers(TEST_REALM, TEST_ALIAS)).thenReturn(mappers);
        when(mapper.writeValueAsString(mappers)).thenThrow(new RuntimeException("Mapping error"));

        // Act & Assert
        assertThrows(ToolCallException.class, () -> identityProviderTool.getIdentityProviderMappers(TEST_REALM, TEST_ALIAS));
        verify(identityProviderService).getIdentityProviderMappers(TEST_REALM, TEST_ALIAS);
        verify(mapper).writeValueAsString(mappers);
    }

    @Test
    public void testCreateIdentityProviderMapper_Success_ReturnsSuccessMessage() throws Exception {
        // Arrange
        String mapperJson = "{\"id\":\"mapper-id\",\"name\":\"Test Mapper\"}";
        IdentityProviderMapperRepresentation mapperRep = new IdentityProviderMapperRepresentation();
        mapperRep.setId("mapper-id");
        mapperRep.setName("Test Mapper");

        when(mapper.readValue(mapperJson, IdentityProviderMapperRepresentation.class)).thenReturn(mapperRep);
        when(identityProviderService.createIdentityProviderMapper(eq(TEST_REALM), eq(TEST_ALIAS), any(IdentityProviderMapperRepresentation.class)))
                .thenReturn("Successfully created identity provider mapper: Test Mapper");

        // Act
        String result = identityProviderTool.createIdentityProviderMapper(TEST_REALM, TEST_ALIAS, mapperJson);

        // Assert
        assertEquals("Successfully created identity provider mapper: Test Mapper", result);
        verify(mapper).readValue(mapperJson, IdentityProviderMapperRepresentation.class);
        verify(identityProviderService).createIdentityProviderMapper(eq(TEST_REALM), eq(TEST_ALIAS), any(IdentityProviderMapperRepresentation.class));
    }

    @Test
    public void testCreateIdentityProviderMapper_MappingException_ThrowsToolCallException() throws Exception {
        // Arrange
        String mapperJson = "{\"id\":\"mapper-id\",\"name\":\"Test Mapper\"}";
        when(mapper.readValue(anyString(), eq(IdentityProviderMapperRepresentation.class)))
                .thenThrow(new RuntimeException("Mapping error"));

        // Act & Assert
        assertThrows(ToolCallException.class, () -> identityProviderTool.createIdentityProviderMapper(TEST_REALM, TEST_ALIAS, mapperJson));
        verify(mapper).readValue(mapperJson, IdentityProviderMapperRepresentation.class);
    }
}