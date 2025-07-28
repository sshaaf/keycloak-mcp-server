package dev.shaaf.experimental.service;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RealmsResource;
import org.keycloak.representations.idm.RealmEventsConfigRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;

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
public class RealmServiceTest {

    @InjectMock
    Keycloak keycloak;

    @Inject
    RealmService realmService;

    private RealmsResource realmsResource;
    private RealmResource realmResource;
    private Response response;

    private static final String TEST_REALM_NAME = "test-realm";
    private static final String TEST_DISPLAY_NAME = "Test Realm";

    @BeforeEach
    public void setup() {
        // Mock the Keycloak client chain
        realmsResource = mock(RealmsResource.class);
        realmResource = mock(RealmResource.class);
        response = mock(Response.class);

        when(keycloak.realms()).thenReturn(realmsResource);
        when(keycloak.realm(anyString())).thenReturn(realmResource);
    }

    @Test
    public void testGetRealms_ReturnsRealmList() {
        // Arrange
        List<RealmRepresentation> expectedRealms = new ArrayList<>();
        RealmRepresentation realm = new RealmRepresentation();
        realm.setId("realm-id");
        realm.setRealm(TEST_REALM_NAME);
        expectedRealms.add(realm);

        when(realmsResource.findAll()).thenReturn(expectedRealms);

        // Act
        List<RealmRepresentation> actualRealms = realmService.getRealms();

        // Assert
        assertEquals(expectedRealms, actualRealms);
        verify(keycloak).realms();
        verify(realmsResource).findAll();
    }

    @Test
    public void testGetRealms_EmptyList_ReturnsEmptyList() {
        // Arrange
        List<RealmRepresentation> expectedRealms = new ArrayList<>();
        when(realmsResource.findAll()).thenReturn(expectedRealms);

        // Act
        List<RealmRepresentation> actualRealms = realmService.getRealms();

        // Assert
        assertEquals(0, actualRealms.size());
        verify(keycloak).realms();
        verify(realmsResource).findAll();
    }

    @Test
    public void testGetRealms_MultipleRealms_ReturnsAllRealms() {
        // Arrange
        List<RealmRepresentation> expectedRealms = new ArrayList<>();
        
        RealmRepresentation realm1 = new RealmRepresentation();
        realm1.setId("realm-id-1");
        realm1.setRealm("realm-1");
        expectedRealms.add(realm1);
        
        RealmRepresentation realm2 = new RealmRepresentation();
        realm2.setId("realm-id-2");
        realm2.setRealm("realm-2");
        expectedRealms.add(realm2);

        when(realmsResource.findAll()).thenReturn(expectedRealms);

        // Act
        List<RealmRepresentation> actualRealms = realmService.getRealms();

        // Assert
        assertEquals(2, actualRealms.size());
        assertEquals(expectedRealms, actualRealms);
        verify(keycloak).realms();
        verify(realmsResource).findAll();
    }

    @Test
    public void testGetRealm_Success_ReturnsRealm() {
        // Arrange
        RealmRepresentation expectedRealm = new RealmRepresentation();
        expectedRealm.setRealm(TEST_REALM_NAME);
        expectedRealm.setDisplayName(TEST_DISPLAY_NAME);

        when(realmResource.toRepresentation()).thenReturn(expectedRealm);

        // Act
        RealmRepresentation actualRealm = realmService.getRealm(TEST_REALM_NAME);

        // Assert
        assertNotNull(actualRealm);
        assertEquals(TEST_REALM_NAME, actualRealm.getRealm());
        assertEquals(TEST_DISPLAY_NAME, actualRealm.getDisplayName());
        verify(keycloak).realm(TEST_REALM_NAME);
        verify(realmResource).toRepresentation();
    }

    @Test
    public void testGetRealm_NotFound_ReturnsNull() {
        // Arrange
        when(realmResource.toRepresentation()).thenThrow(new NotFoundException("Realm not found"));

        // Act
        RealmRepresentation actualRealm = realmService.getRealm(TEST_REALM_NAME);

        // Assert
        assertNull(actualRealm);
        verify(keycloak).realm(TEST_REALM_NAME);
        verify(realmResource).toRepresentation();
    }

    @Test
    public void testCreateRealm_Success_ReturnsSuccessMessage() {
        // Arrange
        doNothing().when(realmsResource).create(any(RealmRepresentation.class));

        // Act
        String result = realmService.createRealm(TEST_REALM_NAME, TEST_DISPLAY_NAME, true);

        // Assert
        assertEquals("Successfully created realm: " + TEST_REALM_NAME, result);
        verify(keycloak).realms();
        verify(realmsResource).create(any(RealmRepresentation.class));
    }

    @Test
    public void testCreateRealm_Exception_ReturnsErrorMessage() {
        // Arrange
        doThrow(new RuntimeException("Test exception")).when(realmsResource).create(any(RealmRepresentation.class));

        // Act
        String result = realmService.createRealm(TEST_REALM_NAME, TEST_DISPLAY_NAME, true);

        // Assert
        assertTrue(result.startsWith("Error creating realm: " + TEST_REALM_NAME));
        verify(keycloak).realms();
        verify(realmsResource).create(any(RealmRepresentation.class));
    }

    @Test
    public void testUpdateRealm_Success_ReturnsSuccessMessage() {
        // Arrange
        RealmRepresentation realmRepresentation = new RealmRepresentation();
        realmRepresentation.setRealm(TEST_REALM_NAME);
        realmRepresentation.setDisplayName(TEST_DISPLAY_NAME);

        doNothing().when(realmResource).update(any(RealmRepresentation.class));

        // Act
        String result = realmService.updateRealm(TEST_REALM_NAME, realmRepresentation);

        // Assert
        assertEquals("Successfully updated realm: " + TEST_REALM_NAME, result);
        verify(keycloak).realm(TEST_REALM_NAME);
        verify(realmResource).update(realmRepresentation);
    }

    @Test
    public void testUpdateRealm_Exception_ReturnsErrorMessage() {
        // Arrange
        RealmRepresentation realmRepresentation = new RealmRepresentation();
        realmRepresentation.setRealm(TEST_REALM_NAME);
        realmRepresentation.setDisplayName(TEST_DISPLAY_NAME);

        doThrow(new RuntimeException("Test exception")).when(realmResource).update(any(RealmRepresentation.class));

        // Act
        String result = realmService.updateRealm(TEST_REALM_NAME, realmRepresentation);

        // Assert
        assertTrue(result.startsWith("Error updating realm: " + TEST_REALM_NAME));
        verify(keycloak).realm(TEST_REALM_NAME);
        verify(realmResource).update(realmRepresentation);
    }

    @Test
    public void testDeleteRealm_Success_ReturnsSuccessMessage() {
        // Arrange
        RealmRepresentation realm = new RealmRepresentation();
        realm.setRealm(TEST_REALM_NAME);
        realm.setDisplayName(TEST_DISPLAY_NAME);

        when(realmResource.toRepresentation()).thenReturn(realm);
        when(realmsResource.realm(TEST_REALM_NAME)).thenReturn(realmResource);
        doNothing().when(realmResource).remove();

        // Act
        String result = realmService.deleteRealm(TEST_REALM_NAME);

        // Assert
        assertEquals("Successfully deleted realm: " + TEST_REALM_NAME, result);
        verify(keycloak).realm(TEST_REALM_NAME);
        verify(realmResource).toRepresentation();
        verify(keycloak).realms();
        verify(realmsResource).realm(TEST_REALM_NAME);
        verify(realmResource).remove();
    }

    @Test
    public void testDeleteRealm_RealmNotFound_ReturnsErrorMessage() {
        // Arrange
        when(realmResource.toRepresentation()).thenReturn(null);

        // Act
        String result = realmService.deleteRealm(TEST_REALM_NAME);

        // Assert
        assertEquals("Realm not found: " + TEST_REALM_NAME, result);
        verify(keycloak).realm(TEST_REALM_NAME);
        verify(realmResource).toRepresentation();
    }

    @Test
    public void testDeleteRealm_Exception_ReturnsErrorMessage() {
        // Arrange
        RealmRepresentation realm = new RealmRepresentation();
        realm.setRealm(TEST_REALM_NAME);
        realm.setDisplayName(TEST_DISPLAY_NAME);

        when(realmResource.toRepresentation()).thenReturn(realm);
        when(realmsResource.realm(TEST_REALM_NAME)).thenReturn(realmResource);
        doThrow(new RuntimeException("Test exception")).when(realmResource).remove();

        // Act
        String result = realmService.deleteRealm(TEST_REALM_NAME);

        // Assert
        assertTrue(result.startsWith("Error deleting realm: " + TEST_REALM_NAME));
        verify(keycloak).realm(TEST_REALM_NAME);
        verify(realmResource).toRepresentation();
        verify(keycloak).realms();
        verify(realmsResource).realm(TEST_REALM_NAME);
        verify(realmResource).remove();
    }

    @Test
    public void testSetRealmEnabled_Success_ReturnsSuccessMessage() {
        // Arrange
        RealmRepresentation realm = new RealmRepresentation();
        realm.setRealm(TEST_REALM_NAME);
        realm.setDisplayName(TEST_DISPLAY_NAME);
        realm.setEnabled(false);

        when(realmResource.toRepresentation()).thenReturn(realm);
        doNothing().when(realmResource).update(any(RealmRepresentation.class));

        // Act
        String result = realmService.setRealmEnabled(TEST_REALM_NAME, true);

        // Assert
        assertEquals("Successfully enabled realm: " + TEST_REALM_NAME, result);
        verify(keycloak).realm(TEST_REALM_NAME);
        verify(realmResource).toRepresentation();
        verify(realmResource).update(any(RealmRepresentation.class));
    }

    @Test
    public void testSetRealmEnabled_NotFound_ReturnsErrorMessage() {
        // Arrange
        when(realmResource.toRepresentation()).thenThrow(new NotFoundException("Realm not found"));

        // Act
        String result = realmService.setRealmEnabled(TEST_REALM_NAME, true);

        // Assert
        assertEquals("Realm not found: " + TEST_REALM_NAME, result);
        verify(keycloak).realm(TEST_REALM_NAME);
        verify(realmResource).toRepresentation();
    }

    @Test
    public void testSetRealmEnabled_Exception_ReturnsErrorMessage() {
        // Arrange
        RealmRepresentation realm = new RealmRepresentation();
        realm.setRealm(TEST_REALM_NAME);
        realm.setDisplayName(TEST_DISPLAY_NAME);
        realm.setEnabled(false);

        when(realmResource.toRepresentation()).thenReturn(realm);
        doThrow(new RuntimeException("Test exception")).when(realmResource).update(any(RealmRepresentation.class));

        // Act
        String result = realmService.setRealmEnabled(TEST_REALM_NAME, true);

        // Assert
        assertTrue(result.startsWith("Error enabling realm: " + TEST_REALM_NAME));
        verify(keycloak).realm(TEST_REALM_NAME);
        verify(realmResource).toRepresentation();
        verify(realmResource).update(any(RealmRepresentation.class));
    }

    @Test
    public void testGetRealmEventsConfig_Success_ReturnsEventsConfig() {
        // Arrange
        RealmEventsConfigRepresentation eventsConfig = new RealmEventsConfigRepresentation();
        eventsConfig.setEventsEnabled(true);

        when(realmResource.getRealmEventsConfig()).thenReturn(eventsConfig);

        // Act
        RealmEventsConfigRepresentation actualEventsConfig = realmService.getRealmEventsConfig(TEST_REALM_NAME);

        // Assert
        assertNotNull(actualEventsConfig);
        assertTrue(actualEventsConfig.isEventsEnabled());
        verify(keycloak).realm(TEST_REALM_NAME);
        verify(realmResource).getRealmEventsConfig();
    }

    @Test
    public void testGetRealmEventsConfig_NotFound_ReturnsNull() {
        // Arrange
        when(realmResource.getRealmEventsConfig()).thenThrow(new NotFoundException("Realm not found"));

        // Act
        RealmEventsConfigRepresentation actualEventsConfig = realmService.getRealmEventsConfig(TEST_REALM_NAME);

        // Assert
        assertNull(actualEventsConfig);
        verify(keycloak).realm(TEST_REALM_NAME);
        verify(realmResource).getRealmEventsConfig();
    }

    @Test
    public void testGetRealmEventsConfig_Exception_ReturnsNull() {
        // Arrange
        when(realmResource.getRealmEventsConfig()).thenThrow(new RuntimeException("Test exception"));

        // Act
        RealmEventsConfigRepresentation actualEventsConfig = realmService.getRealmEventsConfig(TEST_REALM_NAME);

        // Assert
        assertNull(actualEventsConfig);
        verify(keycloak).realm(TEST_REALM_NAME);
        verify(realmResource).getRealmEventsConfig();
    }

    @Test
    public void testUpdateRealmEventsConfig_Success_ReturnsSuccessMessage() {
        // Arrange
        RealmEventsConfigRepresentation eventsConfig = new RealmEventsConfigRepresentation();
        eventsConfig.setEventsEnabled(true);

        doNothing().when(realmResource).updateRealmEventsConfig(any(RealmEventsConfigRepresentation.class));

        // Act
        String result = realmService.updateRealmEventsConfig(TEST_REALM_NAME, eventsConfig);

        // Assert
        assertEquals("Successfully updated realm events config: " + TEST_REALM_NAME, result);
        verify(keycloak).realm(TEST_REALM_NAME);
        verify(realmResource).updateRealmEventsConfig(eventsConfig);
    }

    @Test
    public void testUpdateRealmEventsConfig_NotFound_ReturnsErrorMessage() {
        // Arrange
        RealmEventsConfigRepresentation eventsConfig = new RealmEventsConfigRepresentation();
        eventsConfig.setEventsEnabled(true);

        doThrow(new NotFoundException("Realm not found")).when(realmResource).updateRealmEventsConfig(any(RealmEventsConfigRepresentation.class));

        // Act
        String result = realmService.updateRealmEventsConfig(TEST_REALM_NAME, eventsConfig);

        // Assert
        assertEquals("Realm not found: " + TEST_REALM_NAME, result);
        verify(keycloak).realm(TEST_REALM_NAME);
        verify(realmResource).updateRealmEventsConfig(eventsConfig);
    }

    @Test
    public void testUpdateRealmEventsConfig_Exception_ReturnsErrorMessage() {
        // Arrange
        RealmEventsConfigRepresentation eventsConfig = new RealmEventsConfigRepresentation();
        eventsConfig.setEventsEnabled(true);

        doThrow(new RuntimeException("Test exception")).when(realmResource).updateRealmEventsConfig(any(RealmEventsConfigRepresentation.class));

        // Act
        String result = realmService.updateRealmEventsConfig(TEST_REALM_NAME, eventsConfig);

        // Assert
        assertTrue(result.startsWith("Error updating realm events config: " + TEST_REALM_NAME));
        verify(keycloak).realm(TEST_REALM_NAME);
        verify(realmResource).updateRealmEventsConfig(eventsConfig);
    }
}