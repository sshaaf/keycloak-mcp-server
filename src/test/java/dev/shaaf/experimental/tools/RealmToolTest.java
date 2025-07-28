package dev.shaaf.experimental.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.shaaf.experimental.service.RealmService;
import io.quarkiverse.mcp.server.ToolCallException;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.RealmEventsConfigRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
public class RealmToolTest {

    @InjectMock
    RealmService realmService;

    @InjectMock
    ObjectMapper mapper;

    @Inject
    RealmTool realmTool;

    private static final String TEST_REALM_NAME = "test-realm";
    private static final String TEST_DISPLAY_NAME = "Test Realm";

    @Test
    public void testGetRealms_Success_ReturnsJsonString() throws Exception {
        // Arrange
        List<RealmRepresentation> realms = new ArrayList<>();
        RealmRepresentation realm = new RealmRepresentation();
        realm.setRealm(TEST_REALM_NAME);
        realm.setDisplayName(TEST_DISPLAY_NAME);
        realms.add(realm);

        when(realmService.getRealms()).thenReturn(realms);
        when(mapper.writeValueAsString(realms)).thenReturn("[{\"realm\":\"test-realm\",\"displayName\":\"Test Realm\"}]");

        // Act
        String result = realmTool.getRealms();

        // Assert
        assertEquals("[{\"realm\":\"test-realm\",\"displayName\":\"Test Realm\"}]", result);
        verify(realmService).getRealms();
        verify(mapper).writeValueAsString(realms);
    }

    @Test
    public void testGetRealms_MappingException_ThrowsToolCallException() throws Exception {
        // Arrange
        List<RealmRepresentation> realms = new ArrayList<>();
        when(realmService.getRealms()).thenReturn(realms);
        when(mapper.writeValueAsString(realms)).thenThrow(new RuntimeException("Mapping error"));

        // Act & Assert
        assertThrows(ToolCallException.class, () -> realmTool.getRealms());
        verify(realmService).getRealms();
        verify(mapper).writeValueAsString(realms);
    }

    @Test
    public void testGetRealm_Success_ReturnsJsonString() throws Exception {
        // Arrange
        RealmRepresentation realm = new RealmRepresentation();
        realm.setRealm(TEST_REALM_NAME);
        realm.setDisplayName(TEST_DISPLAY_NAME);

        when(realmService.getRealm(TEST_REALM_NAME)).thenReturn(realm);
        when(mapper.writeValueAsString(realm)).thenReturn("{\"realm\":\"test-realm\",\"displayName\":\"Test Realm\"}");

        // Act
        String result = realmTool.getRealm(TEST_REALM_NAME);

        // Assert
        assertEquals("{\"realm\":\"test-realm\",\"displayName\":\"Test Realm\"}", result);
        verify(realmService).getRealm(TEST_REALM_NAME);
        verify(mapper).writeValueAsString(realm);
    }

    @Test
    public void testGetRealm_RealmNotFound_ReturnsNullJson() throws Exception {
        // Arrange
        when(realmService.getRealm(TEST_REALM_NAME)).thenReturn(null);
        when(mapper.writeValueAsString(null)).thenReturn("null");

        // Act
        String result = realmTool.getRealm(TEST_REALM_NAME);

        // Assert
        assertEquals("null", result);
        verify(realmService).getRealm(TEST_REALM_NAME);
        verify(mapper).writeValueAsString(null);
    }

    @Test
    public void testGetRealm_MappingException_ThrowsToolCallException() throws Exception {
        // Arrange
        RealmRepresentation realm = new RealmRepresentation();
        realm.setRealm(TEST_REALM_NAME);
        realm.setDisplayName(TEST_DISPLAY_NAME);

        when(realmService.getRealm(TEST_REALM_NAME)).thenReturn(realm);
        when(mapper.writeValueAsString(realm)).thenThrow(new RuntimeException("Mapping error"));

        // Act & Assert
        assertThrows(ToolCallException.class, () -> realmTool.getRealm(TEST_REALM_NAME));
        verify(realmService).getRealm(TEST_REALM_NAME);
        verify(mapper).writeValueAsString(realm);
    }

    @Test
    public void testCreateRealm_Success_ReturnsSuccessMessage() {
        // Arrange
        boolean enabled = true;
        String successMessage = "Successfully created realm: " + TEST_REALM_NAME;

        when(realmService.createRealm(TEST_REALM_NAME, TEST_DISPLAY_NAME, enabled)).thenReturn(successMessage);

        // Act
        String result = realmTool.createRealm(TEST_REALM_NAME, TEST_DISPLAY_NAME, enabled);

        // Assert
        assertEquals(successMessage, result);
        verify(realmService).createRealm(TEST_REALM_NAME, TEST_DISPLAY_NAME, enabled);
    }

    @Test
    public void testUpdateRealm_Success_ReturnsSuccessMessage() throws Exception {
        // Arrange
        String realmJson = "{\"realm\":\"test-realm\",\"displayName\":\"Test Realm\"}";
        RealmRepresentation realmRepresentation = new RealmRepresentation();
        realmRepresentation.setRealm(TEST_REALM_NAME);
        realmRepresentation.setDisplayName(TEST_DISPLAY_NAME);
        String successMessage = "Successfully updated realm: " + TEST_REALM_NAME;

        when(mapper.readValue(realmJson, RealmRepresentation.class)).thenReturn(realmRepresentation);
        when(realmService.updateRealm(TEST_REALM_NAME, realmRepresentation)).thenReturn(successMessage);

        // Act
        String result = realmTool.updateRealm(TEST_REALM_NAME, realmJson);

        // Assert
        assertEquals(successMessage, result);
        verify(mapper).readValue(realmJson, RealmRepresentation.class);
        verify(realmService).updateRealm(TEST_REALM_NAME, realmRepresentation);
    }

    @Test
    public void testUpdateRealm_MappingException_ThrowsToolCallException() throws Exception {
        // Arrange
        String realmJson = "{\"realm\":\"test-realm\",\"displayName\":\"Test Realm\"}";

        when(mapper.readValue(realmJson, RealmRepresentation.class)).thenThrow(new RuntimeException("Mapping error"));

        // Act & Assert
        assertThrows(ToolCallException.class, () -> realmTool.updateRealm(TEST_REALM_NAME, realmJson));
        verify(mapper).readValue(realmJson, RealmRepresentation.class);
    }

    @Test
    public void testDeleteRealm_Success_ReturnsSuccessMessage() {
        // Arrange
        String successMessage = "Successfully deleted realm: " + TEST_REALM_NAME;

        when(realmService.deleteRealm(TEST_REALM_NAME)).thenReturn(successMessage);

        // Act
        String result = realmTool.deleteRealm(TEST_REALM_NAME);

        // Assert
        assertEquals(successMessage, result);
        verify(realmService).deleteRealm(TEST_REALM_NAME);
    }

    @Test
    public void testSetRealmEnabled_Success_ReturnsSuccessMessage() {
        // Arrange
        boolean enabled = true;
        String successMessage = "Successfully enabled realm: " + TEST_REALM_NAME;

        when(realmService.setRealmEnabled(TEST_REALM_NAME, enabled)).thenReturn(successMessage);

        // Act
        String result = realmTool.setRealmEnabled(TEST_REALM_NAME, enabled);

        // Assert
        assertEquals(successMessage, result);
        verify(realmService).setRealmEnabled(TEST_REALM_NAME, enabled);
    }

    @Test
    public void testGetRealmEventsConfig_Success_ReturnsJsonString() throws Exception {
        // Arrange
        RealmEventsConfigRepresentation eventsConfig = new RealmEventsConfigRepresentation();
        eventsConfig.setEventsEnabled(true);

        when(realmService.getRealmEventsConfig(TEST_REALM_NAME)).thenReturn(eventsConfig);
        when(mapper.writeValueAsString(eventsConfig)).thenReturn("{\"eventsEnabled\":true}");

        // Act
        String result = realmTool.getRealmEventsConfig(TEST_REALM_NAME);

        // Assert
        assertEquals("{\"eventsEnabled\":true}", result);
        verify(realmService).getRealmEventsConfig(TEST_REALM_NAME);
        verify(mapper).writeValueAsString(eventsConfig);
    }

    @Test
    public void testGetRealmEventsConfig_ConfigNotFound_ReturnsNullJson() throws Exception {
        // Arrange
        when(realmService.getRealmEventsConfig(TEST_REALM_NAME)).thenReturn(null);
        when(mapper.writeValueAsString(null)).thenReturn("null");

        // Act
        String result = realmTool.getRealmEventsConfig(TEST_REALM_NAME);

        // Assert
        assertEquals("null", result);
        verify(realmService).getRealmEventsConfig(TEST_REALM_NAME);
        verify(mapper).writeValueAsString(null);
    }

    @Test
    public void testGetRealmEventsConfig_MappingException_ThrowsToolCallException() throws Exception {
        // Arrange
        RealmEventsConfigRepresentation eventsConfig = new RealmEventsConfigRepresentation();
        eventsConfig.setEventsEnabled(true);

        when(realmService.getRealmEventsConfig(TEST_REALM_NAME)).thenReturn(eventsConfig);
        when(mapper.writeValueAsString(eventsConfig)).thenThrow(new RuntimeException("Mapping error"));

        // Act & Assert
        assertThrows(ToolCallException.class, () -> realmTool.getRealmEventsConfig(TEST_REALM_NAME));
        verify(realmService).getRealmEventsConfig(TEST_REALM_NAME);
        verify(mapper).writeValueAsString(eventsConfig);
    }

    @Test
    public void testUpdateRealmEventsConfig_Success_ReturnsSuccessMessage() throws Exception {
        // Arrange
        String eventsConfigJson = "{\"eventsEnabled\":true}";
        RealmEventsConfigRepresentation eventsConfig = new RealmEventsConfigRepresentation();
        eventsConfig.setEventsEnabled(true);
        String successMessage = "Successfully updated realm events config: " + TEST_REALM_NAME;

        when(mapper.readValue(eventsConfigJson, RealmEventsConfigRepresentation.class)).thenReturn(eventsConfig);
        when(realmService.updateRealmEventsConfig(TEST_REALM_NAME, eventsConfig)).thenReturn(successMessage);

        // Act
        String result = realmTool.updateRealmEventsConfig(TEST_REALM_NAME, eventsConfigJson);

        // Assert
        assertEquals(successMessage, result);
        verify(mapper).readValue(eventsConfigJson, RealmEventsConfigRepresentation.class);
        verify(realmService).updateRealmEventsConfig(TEST_REALM_NAME, eventsConfig);
    }

    @Test
    public void testUpdateRealmEventsConfig_MappingException_ThrowsToolCallException() throws Exception {
        // Arrange
        String eventsConfigJson = "{\"eventsEnabled\":true}";

        when(mapper.readValue(eventsConfigJson, RealmEventsConfigRepresentation.class)).thenThrow(new RuntimeException("Mapping error"));

        // Act & Assert
        assertThrows(ToolCallException.class, () -> realmTool.updateRealmEventsConfig(TEST_REALM_NAME, eventsConfigJson));
        verify(mapper).readValue(eventsConfigJson, RealmEventsConfigRepresentation.class);
    }
}