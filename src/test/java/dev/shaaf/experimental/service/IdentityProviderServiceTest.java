package dev.shaaf.experimental.service;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.IdentityProviderResource;
import org.keycloak.admin.client.resource.IdentityProvidersResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.IdentityProviderMapperRepresentation;
import org.keycloak.representations.idm.IdentityProviderRepresentation;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
public class IdentityProviderServiceTest {

    @InjectMock
    Keycloak keycloak;

    @Inject
    IdentityProviderService identityProviderService;

    private RealmResource realmResource;
    private IdentityProvidersResource identityProvidersResource;
    private IdentityProviderResource identityProviderResource;
    private Response response;

    private static final String TEST_REALM = "test-realm";
    private static final String TEST_IDP_ALIAS = "test-idp";
    private static final String TEST_IDP_DISPLAY_NAME = "Test Identity Provider";
    private static final String TEST_MAPPER_NAME = "test-mapper";

    @BeforeEach
    public void setup() {
        // Mock the Keycloak client chain
        realmResource = mock(RealmResource.class);
        identityProvidersResource = mock(IdentityProvidersResource.class);
        identityProviderResource = mock(IdentityProviderResource.class);
        response = mock(Response.class);

        when(keycloak.realm(anyString())).thenReturn(realmResource);
        when(realmResource.identityProviders()).thenReturn(identityProvidersResource);
        when(identityProvidersResource.get(anyString())).thenReturn(identityProviderResource);
    }

    @Test
    public void testGetIdentityProviders_Success_ReturnsIdpList() {
        // Arrange
        List<IdentityProviderRepresentation> expectedIdps = new ArrayList<>();
        IdentityProviderRepresentation idp = new IdentityProviderRepresentation();
        idp.setAlias(TEST_IDP_ALIAS);
        idp.setDisplayName(TEST_IDP_DISPLAY_NAME);
        expectedIdps.add(idp);

        when(identityProvidersResource.findAll()).thenReturn(expectedIdps);

        // Act
        List<IdentityProviderRepresentation> actualIdps = identityProviderService.getIdentityProviders(TEST_REALM);

        // Assert
        assertEquals(expectedIdps, actualIdps);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).identityProviders();
        verify(identityProvidersResource).findAll();
    }

    @Test
    public void testGetIdentityProviders_Exception_ReturnsEmptyList() {
        // Arrange
        when(identityProvidersResource.findAll()).thenThrow(new RuntimeException("Test exception"));

        // Act
        List<IdentityProviderRepresentation> actualIdps = identityProviderService.getIdentityProviders(TEST_REALM);

        // Assert
        assertTrue(actualIdps.isEmpty());
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).identityProviders();
        verify(identityProvidersResource).findAll();
    }

    @Test
    public void testGetIdentityProvider_Success_ReturnsIdp() {
        // Arrange
        IdentityProviderRepresentation expectedIdp = new IdentityProviderRepresentation();
        expectedIdp.setAlias(TEST_IDP_ALIAS);
        expectedIdp.setDisplayName(TEST_IDP_DISPLAY_NAME);

        when(identityProviderResource.toRepresentation()).thenReturn(expectedIdp);

        // Act
        IdentityProviderRepresentation actualIdp = identityProviderService.getIdentityProvider(TEST_REALM, TEST_IDP_ALIAS);

        // Assert
        assertNotNull(actualIdp);
        assertEquals(TEST_IDP_ALIAS, actualIdp.getAlias());
        assertEquals(TEST_IDP_DISPLAY_NAME, actualIdp.getDisplayName());
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).identityProviders();
        verify(identityProvidersResource).get(TEST_IDP_ALIAS);
        verify(identityProviderResource).toRepresentation();
    }

    @Test
    public void testGetIdentityProvider_NotFound_ReturnsNull() {
        // Arrange
        when(identityProviderResource.toRepresentation()).thenThrow(new NotFoundException("Identity provider not found"));

        // Act
        IdentityProviderRepresentation actualIdp = identityProviderService.getIdentityProvider(TEST_REALM, TEST_IDP_ALIAS);

        // Assert
        assertNull(actualIdp);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).identityProviders();
        verify(identityProvidersResource).get(TEST_IDP_ALIAS);
        verify(identityProviderResource).toRepresentation();
    }

    @Test
    public void testGetIdentityProvider_Exception_ReturnsNull() {
        // Arrange
        when(identityProviderResource.toRepresentation()).thenThrow(new RuntimeException("Test exception"));

        // Act
        IdentityProviderRepresentation actualIdp = identityProviderService.getIdentityProvider(TEST_REALM, TEST_IDP_ALIAS);

        // Assert
        assertNull(actualIdp);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).identityProviders();
        verify(identityProvidersResource).get(TEST_IDP_ALIAS);
        verify(identityProviderResource).toRepresentation();
    }

    @Test
    public void testCreateIdentityProvider_Success_ReturnsSuccessMessage() {
        // Arrange
        IdentityProviderRepresentation idp = new IdentityProviderRepresentation();
        idp.setAlias(TEST_IDP_ALIAS);
        idp.setDisplayName(TEST_IDP_DISPLAY_NAME);

        when(identityProvidersResource.create(any(IdentityProviderRepresentation.class))).thenReturn(response);
        when(response.getStatus()).thenReturn(Response.Status.CREATED.getStatusCode());

        // Act
        String result = identityProviderService.createIdentityProvider(TEST_REALM, idp);

        // Assert
        assertEquals("Successfully created identity provider: " + TEST_IDP_ALIAS, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).identityProviders();
        verify(identityProvidersResource).create(idp);
        verify(response).getStatus();
    }

    @Test
    public void testCreateIdentityProvider_Failure_ReturnsErrorMessage() {
        // Arrange
        IdentityProviderRepresentation idp = new IdentityProviderRepresentation();
        idp.setAlias(TEST_IDP_ALIAS);
        idp.setDisplayName(TEST_IDP_DISPLAY_NAME);

        when(identityProvidersResource.create(any(IdentityProviderRepresentation.class))).thenReturn(response);
        when(response.getStatus()).thenReturn(Response.Status.BAD_REQUEST.getStatusCode());

        // Act
        String result = identityProviderService.createIdentityProvider(TEST_REALM, idp);

        // Assert
        assertEquals("Error creating identity provider: " + TEST_IDP_ALIAS, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).identityProviders();
        verify(identityProvidersResource).create(idp);
        verify(response).getStatus();
        verify(response).close();
    }

    @Test
    public void testCreateIdentityProvider_Exception_ReturnsErrorMessage() {
        // Arrange
        IdentityProviderRepresentation idp = new IdentityProviderRepresentation();
        idp.setAlias(TEST_IDP_ALIAS);
        idp.setDisplayName(TEST_IDP_DISPLAY_NAME);

        when(identityProvidersResource.create(any(IdentityProviderRepresentation.class))).thenThrow(new RuntimeException("Test exception"));

        // Act
        String result = identityProviderService.createIdentityProvider(TEST_REALM, idp);

        // Assert
        assertTrue(result.startsWith("Error creating identity provider: " + TEST_IDP_ALIAS));
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).identityProviders();
        verify(identityProvidersResource).create(idp);
    }

    @Test
    public void testUpdateIdentityProvider_Success_ReturnsSuccessMessage() {
        // Arrange
        IdentityProviderRepresentation idp = new IdentityProviderRepresentation();
        idp.setAlias(TEST_IDP_ALIAS);
        idp.setDisplayName(TEST_IDP_DISPLAY_NAME);

        doNothing().when(identityProviderResource).update(any(IdentityProviderRepresentation.class));

        // Act
        String result = identityProviderService.updateIdentityProvider(TEST_REALM, TEST_IDP_ALIAS, idp);

        // Assert
        assertEquals("Successfully updated identity provider: " + TEST_IDP_ALIAS, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).identityProviders();
        verify(identityProvidersResource).get(TEST_IDP_ALIAS);
        verify(identityProviderResource).update(idp);
    }

    @Test
    public void testUpdateIdentityProvider_NotFound_ReturnsErrorMessage() {
        // Arrange
        IdentityProviderRepresentation idp = new IdentityProviderRepresentation();
        idp.setAlias(TEST_IDP_ALIAS);
        idp.setDisplayName(TEST_IDP_DISPLAY_NAME);

        doThrow(new NotFoundException("Identity provider not found")).when(identityProviderResource).update(any(IdentityProviderRepresentation.class));

        // Act
        String result = identityProviderService.updateIdentityProvider(TEST_REALM, TEST_IDP_ALIAS, idp);

        // Assert
        assertEquals("Identity provider not found: " + TEST_IDP_ALIAS, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).identityProviders();
        verify(identityProvidersResource).get(TEST_IDP_ALIAS);
        verify(identityProviderResource).update(idp);
    }

    @Test
    public void testUpdateIdentityProvider_Exception_ReturnsErrorMessage() {
        // Arrange
        IdentityProviderRepresentation idp = new IdentityProviderRepresentation();
        idp.setAlias(TEST_IDP_ALIAS);
        idp.setDisplayName(TEST_IDP_DISPLAY_NAME);

        doThrow(new RuntimeException("Test exception")).when(identityProviderResource).update(any(IdentityProviderRepresentation.class));

        // Act
        String result = identityProviderService.updateIdentityProvider(TEST_REALM, TEST_IDP_ALIAS, idp);

        // Assert
        assertTrue(result.startsWith("Error updating identity provider: " + TEST_IDP_ALIAS));
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).identityProviders();
        verify(identityProvidersResource).get(TEST_IDP_ALIAS);
        verify(identityProviderResource).update(idp);
    }

    @Test
    public void testDeleteIdentityProvider_Success_ReturnsSuccessMessage() {
        // Arrange
        IdentityProviderRepresentation idp = new IdentityProviderRepresentation();
        idp.setAlias(TEST_IDP_ALIAS);
        idp.setDisplayName(TEST_IDP_DISPLAY_NAME);

        when(identityProviderResource.toRepresentation()).thenReturn(idp);
        doNothing().when(identityProviderResource).remove();

        // Act
        String result = identityProviderService.deleteIdentityProvider(TEST_REALM, TEST_IDP_ALIAS);

        // Assert
        assertEquals("Successfully deleted identity provider: " + TEST_IDP_ALIAS, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).identityProviders();
        verify(identityProvidersResource).get(TEST_IDP_ALIAS);
        verify(identityProviderResource).toRepresentation();
        verify(identityProviderResource).remove();
    }

    @Test
    public void testDeleteIdentityProvider_IdpNotFound_ReturnsErrorMessage() {
        // Arrange
        when(identityProviderResource.toRepresentation()).thenThrow(new NotFoundException("Identity provider not found"));

        // Act
        String result = identityProviderService.deleteIdentityProvider(TEST_REALM, TEST_IDP_ALIAS);

        // Assert
        assertEquals("Identity provider not found: " + TEST_IDP_ALIAS, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).identityProviders();
        verify(identityProvidersResource).get(TEST_IDP_ALIAS);
        verify(identityProviderResource).toRepresentation();
    }

    @Test
    public void testDeleteIdentityProvider_Exception_ReturnsErrorMessage() {
        // Arrange
        IdentityProviderRepresentation idp = new IdentityProviderRepresentation();
        idp.setAlias(TEST_IDP_ALIAS);
        idp.setDisplayName(TEST_IDP_DISPLAY_NAME);

        when(identityProviderResource.toRepresentation()).thenReturn(idp);
        doThrow(new RuntimeException("Test exception")).when(identityProviderResource).remove();

        // Act
        String result = identityProviderService.deleteIdentityProvider(TEST_REALM, TEST_IDP_ALIAS);

        // Assert
        assertTrue(result.startsWith("Error deleting identity provider: " + TEST_IDP_ALIAS));
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).identityProviders();
        verify(identityProvidersResource).get(TEST_IDP_ALIAS);
        verify(identityProviderResource).toRepresentation();
        verify(identityProviderResource).remove();
    }

    @Test
    public void testGetIdentityProviderMappers_Success_ReturnsMapperList() {
        // Arrange
        List<IdentityProviderMapperRepresentation> expectedMappers = new ArrayList<>();
        IdentityProviderMapperRepresentation mapper = new IdentityProviderMapperRepresentation();
        mapper.setId("mapper-id");
        mapper.setName(TEST_MAPPER_NAME);
        expectedMappers.add(mapper);

        when(identityProviderResource.getMappers()).thenReturn(expectedMappers);

        // Act
        List<IdentityProviderMapperRepresentation> actualMappers = identityProviderService.getIdentityProviderMappers(TEST_REALM, TEST_IDP_ALIAS);

        // Assert
        assertEquals(expectedMappers, actualMappers);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).identityProviders();
        verify(identityProvidersResource).get(TEST_IDP_ALIAS);
        verify(identityProviderResource).getMappers();
    }

    @Test
    public void testGetIdentityProviderMappers_NotFound_ReturnsEmptyList() {
        // Arrange
        when(identityProviderResource.getMappers()).thenThrow(new NotFoundException("Identity provider not found"));

        // Act
        List<IdentityProviderMapperRepresentation> actualMappers = identityProviderService.getIdentityProviderMappers(TEST_REALM, TEST_IDP_ALIAS);

        // Assert
        assertTrue(actualMappers.isEmpty());
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).identityProviders();
        verify(identityProvidersResource).get(TEST_IDP_ALIAS);
        verify(identityProviderResource).getMappers();
    }

    @Test
    public void testGetIdentityProviderMappers_Exception_ReturnsEmptyList() {
        // Arrange
        when(identityProviderResource.getMappers()).thenThrow(new RuntimeException("Test exception"));

        // Act
        List<IdentityProviderMapperRepresentation> actualMappers = identityProviderService.getIdentityProviderMappers(TEST_REALM, TEST_IDP_ALIAS);

        // Assert
        assertTrue(actualMappers.isEmpty());
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).identityProviders();
        verify(identityProvidersResource).get(TEST_IDP_ALIAS);
        verify(identityProviderResource).getMappers();
    }

    @Test
    public void testCreateIdentityProviderMapper_Success_ReturnsSuccessMessage() {
        // Arrange
        IdentityProviderMapperRepresentation mapper = new IdentityProviderMapperRepresentation();
        mapper.setId("mapper-id");
        mapper.setName(TEST_MAPPER_NAME);

        when(identityProviderResource.addMapper(any(IdentityProviderMapperRepresentation.class))).thenReturn(response);
        when(response.getStatus()).thenReturn(Response.Status.CREATED.getStatusCode());

        // Act
        String result = identityProviderService.createIdentityProviderMapper(TEST_REALM, TEST_IDP_ALIAS, mapper);

        // Assert
        assertEquals("Successfully created identity provider mapper: " + TEST_MAPPER_NAME, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).identityProviders();
        verify(identityProvidersResource).get(TEST_IDP_ALIAS);
        verify(identityProviderResource).addMapper(mapper);
        verify(response).getStatus();
    }

    @Test
    public void testCreateIdentityProviderMapper_Failure_ReturnsErrorMessage() {
        // Arrange
        IdentityProviderMapperRepresentation mapper = new IdentityProviderMapperRepresentation();
        mapper.setId("mapper-id");
        mapper.setName(TEST_MAPPER_NAME);

        when(identityProviderResource.addMapper(any(IdentityProviderMapperRepresentation.class))).thenReturn(response);
        when(response.getStatus()).thenReturn(Response.Status.BAD_REQUEST.getStatusCode());

        // Act
        String result = identityProviderService.createIdentityProviderMapper(TEST_REALM, TEST_IDP_ALIAS, mapper);

        // Assert
        assertEquals("Error creating identity provider mapper: " + TEST_MAPPER_NAME, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).identityProviders();
        verify(identityProvidersResource).get(TEST_IDP_ALIAS);
        verify(identityProviderResource).addMapper(mapper);
        verify(response).getStatus();
        verify(response).close();
    }

    @Test
    public void testCreateIdentityProviderMapper_NotFound_ReturnsErrorMessage() {
        // Arrange
        IdentityProviderMapperRepresentation mapper = new IdentityProviderMapperRepresentation();
        mapper.setId("mapper-id");
        mapper.setName(TEST_MAPPER_NAME);

        when(identityProviderResource.addMapper(any(IdentityProviderMapperRepresentation.class))).thenThrow(new NotFoundException("Identity provider not found"));

        // Act
        String result = identityProviderService.createIdentityProviderMapper(TEST_REALM, TEST_IDP_ALIAS, mapper);

        // Assert
        assertEquals("Identity provider not found: " + TEST_IDP_ALIAS, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).identityProviders();
        verify(identityProvidersResource).get(TEST_IDP_ALIAS);
        verify(identityProviderResource).addMapper(mapper);
    }

    @Test
    public void testCreateIdentityProviderMapper_Exception_ReturnsErrorMessage() {
        // Arrange
        IdentityProviderMapperRepresentation mapper = new IdentityProviderMapperRepresentation();
        mapper.setId("mapper-id");
        mapper.setName(TEST_MAPPER_NAME);

        when(identityProviderResource.addMapper(any(IdentityProviderMapperRepresentation.class))).thenThrow(new RuntimeException("Test exception"));

        // Act
        String result = identityProviderService.createIdentityProviderMapper(TEST_REALM, TEST_IDP_ALIAS, mapper);

        // Assert
        assertTrue(result.startsWith("Error creating identity provider mapper: " + TEST_MAPPER_NAME));
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).identityProviders();
        verify(identityProvidersResource).get(TEST_IDP_ALIAS);
        verify(identityProviderResource).addMapper(mapper);
    }
}