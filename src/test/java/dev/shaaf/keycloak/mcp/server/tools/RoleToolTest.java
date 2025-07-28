package dev.shaaf.keycloak.mcp.server.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.shaaf.keycloak.mcp.server.service.RoleService;
import io.quarkiverse.mcp.server.ToolCallException;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.RoleRepresentation;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
public class RoleToolTest {

    @InjectMock
    RoleService roleService;

    @InjectMock
    ObjectMapper mapper;

    @Inject
    RoleTool roleTool;

    private static final String TEST_REALM = "test-realm";
    private static final String TEST_ROLE_NAME = "test-role";
    private static final String TEST_DESCRIPTION = "Test role description";

    @Test
    public void testGetRealmRoles_Success_ReturnsJsonString() throws Exception {
        // Arrange
        List<RoleRepresentation> roles = new ArrayList<>();
        RoleRepresentation role = new RoleRepresentation();
        role.setName(TEST_ROLE_NAME);
        role.setDescription(TEST_DESCRIPTION);
        roles.add(role);

        when(roleService.getRealmRoles(TEST_REALM)).thenReturn(roles);
        when(mapper.writeValueAsString(roles)).thenReturn("[{\"name\":\"test-role\",\"description\":\"Test role description\"}]");

        // Act
        String result = roleTool.getRealmRoles(TEST_REALM);

        // Assert
        assertEquals("[{\"name\":\"test-role\",\"description\":\"Test role description\"}]", result);
        verify(roleService).getRealmRoles(TEST_REALM);
        verify(mapper).writeValueAsString(roles);
    }

    @Test
    public void testGetRealmRoles_MappingException_ThrowsToolCallException() throws Exception {
        // Arrange
        List<RoleRepresentation> roles = new ArrayList<>();
        when(roleService.getRealmRoles(TEST_REALM)).thenReturn(roles);
        when(mapper.writeValueAsString(roles)).thenThrow(new RuntimeException("Mapping error"));

        // Act & Assert
        assertThrows(ToolCallException.class, () -> roleTool.getRealmRoles(TEST_REALM));
        verify(roleService).getRealmRoles(TEST_REALM);
        verify(mapper).writeValueAsString(roles);
    }

    @Test
    public void testGetRealmRole_Success_ReturnsJsonString() throws Exception {
        // Arrange
        RoleRepresentation role = new RoleRepresentation();
        role.setName(TEST_ROLE_NAME);
        role.setDescription(TEST_DESCRIPTION);

        when(roleService.getRealmRole(TEST_REALM, TEST_ROLE_NAME)).thenReturn(role);
        when(mapper.writeValueAsString(role)).thenReturn("{\"name\":\"test-role\",\"description\":\"Test role description\"}");

        // Act
        String result = roleTool.getRealmRole(TEST_REALM, TEST_ROLE_NAME);

        // Assert
        assertEquals("{\"name\":\"test-role\",\"description\":\"Test role description\"}", result);
        verify(roleService).getRealmRole(TEST_REALM, TEST_ROLE_NAME);
        verify(mapper).writeValueAsString(role);
    }

    @Test
    public void testGetRealmRole_RoleNotFound_ReturnsNullJson() throws Exception {
        // Arrange
        when(roleService.getRealmRole(TEST_REALM, TEST_ROLE_NAME)).thenReturn(null);
        when(mapper.writeValueAsString(null)).thenReturn("null");

        // Act
        String result = roleTool.getRealmRole(TEST_REALM, TEST_ROLE_NAME);

        // Assert
        assertEquals("null", result);
        verify(roleService).getRealmRole(TEST_REALM, TEST_ROLE_NAME);
        verify(mapper).writeValueAsString(null);
    }

    @Test
    public void testGetRealmRole_MappingException_ThrowsToolCallException() throws Exception {
        // Arrange
        RoleRepresentation role = new RoleRepresentation();
        role.setName(TEST_ROLE_NAME);
        role.setDescription(TEST_DESCRIPTION);

        when(roleService.getRealmRole(TEST_REALM, TEST_ROLE_NAME)).thenReturn(role);
        when(mapper.writeValueAsString(role)).thenThrow(new RuntimeException("Mapping error"));

        // Act & Assert
        assertThrows(ToolCallException.class, () -> roleTool.getRealmRole(TEST_REALM, TEST_ROLE_NAME));
        verify(roleService).getRealmRole(TEST_REALM, TEST_ROLE_NAME);
        verify(mapper).writeValueAsString(role);
    }

    @Test
    public void testCreateRealmRole_Success_ReturnsSuccessMessage() {
        // Arrange
        String successMessage = "Successfully created role: " + TEST_ROLE_NAME;

        when(roleService.createRealmRole(TEST_REALM, TEST_ROLE_NAME, TEST_DESCRIPTION)).thenReturn(successMessage);

        // Act
        String result = roleTool.createRealmRole(TEST_REALM, TEST_ROLE_NAME, TEST_DESCRIPTION);

        // Assert
        assertEquals(successMessage, result);
        verify(roleService).createRealmRole(TEST_REALM, TEST_ROLE_NAME, TEST_DESCRIPTION);
    }

    @Test
    public void testUpdateRealmRole_Success_ReturnsSuccessMessage() throws Exception {
        // Arrange
        String roleJson = "{\"name\":\"test-role\",\"description\":\"Test role description\"}";
        RoleRepresentation roleRepresentation = new RoleRepresentation();
        roleRepresentation.setName(TEST_ROLE_NAME);
        roleRepresentation.setDescription(TEST_DESCRIPTION);
        String successMessage = "Successfully updated role: " + TEST_ROLE_NAME;

        when(mapper.readValue(roleJson, RoleRepresentation.class)).thenReturn(roleRepresentation);
        when(roleService.updateRealmRole(TEST_REALM, TEST_ROLE_NAME, roleRepresentation)).thenReturn(successMessage);

        // Act
        String result = roleTool.updateRealmRole(TEST_REALM, TEST_ROLE_NAME, roleJson);

        // Assert
        assertEquals(successMessage, result);
        verify(mapper).readValue(roleJson, RoleRepresentation.class);
        verify(roleService).updateRealmRole(TEST_REALM, TEST_ROLE_NAME, roleRepresentation);
    }

    @Test
    public void testUpdateRealmRole_MappingException_ThrowsToolCallException() throws Exception {
        // Arrange
        String roleJson = "{\"name\":\"test-role\",\"description\":\"Test role description\"}";

        when(mapper.readValue(roleJson, RoleRepresentation.class)).thenThrow(new RuntimeException("Mapping error"));

        // Act & Assert
        assertThrows(ToolCallException.class, () -> roleTool.updateRealmRole(TEST_REALM, TEST_ROLE_NAME, roleJson));
        verify(mapper).readValue(roleJson, RoleRepresentation.class);
    }

    @Test
    public void testDeleteRealmRole_Success_ReturnsSuccessMessage() {
        // Arrange
        String successMessage = "Successfully deleted role: " + TEST_ROLE_NAME;

        when(roleService.deleteRealmRole(TEST_REALM, TEST_ROLE_NAME)).thenReturn(successMessage);

        // Act
        String result = roleTool.deleteRealmRole(TEST_REALM, TEST_ROLE_NAME);

        // Assert
        assertEquals(successMessage, result);
        verify(roleService).deleteRealmRole(TEST_REALM, TEST_ROLE_NAME);
    }

    @Test
    public void testGetRoleComposites_Success_ReturnsJsonString() throws Exception {
        // Arrange
        List<RoleRepresentation> composites = new ArrayList<>();
        RoleRepresentation composite = new RoleRepresentation();
        composite.setName("composite-role");
        composite.setDescription("Composite role description");
        composites.add(composite);

        when(roleService.getRoleComposites(TEST_REALM, TEST_ROLE_NAME)).thenReturn(composites);
        when(mapper.writeValueAsString(composites)).thenReturn("[{\"name\":\"composite-role\",\"description\":\"Composite role description\"}]");

        // Act
        String result = roleTool.getRoleComposites(TEST_REALM, TEST_ROLE_NAME);

        // Assert
        assertEquals("[{\"name\":\"composite-role\",\"description\":\"Composite role description\"}]", result);
        verify(roleService).getRoleComposites(TEST_REALM, TEST_ROLE_NAME);
        verify(mapper).writeValueAsString(composites);
    }

    @Test
    public void testGetRoleComposites_EmptyList_ReturnsEmptyJsonArray() throws Exception {
        // Arrange
        List<RoleRepresentation> composites = new ArrayList<>();

        when(roleService.getRoleComposites(TEST_REALM, TEST_ROLE_NAME)).thenReturn(composites);
        when(mapper.writeValueAsString(composites)).thenReturn("[]");

        // Act
        String result = roleTool.getRoleComposites(TEST_REALM, TEST_ROLE_NAME);

        // Assert
        assertEquals("[]", result);
        verify(roleService).getRoleComposites(TEST_REALM, TEST_ROLE_NAME);
        verify(mapper).writeValueAsString(composites);
    }

    @Test
    public void testGetRoleComposites_MappingException_ThrowsToolCallException() throws Exception {
        // Arrange
        List<RoleRepresentation> composites = new ArrayList<>();
        when(roleService.getRoleComposites(TEST_REALM, TEST_ROLE_NAME)).thenReturn(composites);
        when(mapper.writeValueAsString(composites)).thenThrow(new RuntimeException("Mapping error"));

        // Act & Assert
        assertThrows(ToolCallException.class, () -> roleTool.getRoleComposites(TEST_REALM, TEST_ROLE_NAME));
        verify(roleService).getRoleComposites(TEST_REALM, TEST_ROLE_NAME);
        verify(mapper).writeValueAsString(composites);
    }

    @Test
    public void testAddCompositeToRole_Success_ReturnsSuccessMessage() {
        // Arrange
        String compositeRoleName = "composite-role";
        String successMessage = "Successfully added composite role: " + TEST_ROLE_NAME + " -> " + compositeRoleName;

        when(roleService.addCompositeToRole(TEST_REALM, TEST_ROLE_NAME, compositeRoleName)).thenReturn(successMessage);

        // Act
        String result = roleTool.addCompositeToRole(TEST_REALM, TEST_ROLE_NAME, compositeRoleName);

        // Assert
        assertEquals(successMessage, result);
        verify(roleService).addCompositeToRole(TEST_REALM, TEST_ROLE_NAME, compositeRoleName);
    }

    @Test
    public void testRemoveCompositeFromRole_Success_ReturnsSuccessMessage() {
        // Arrange
        String compositeRoleName = "composite-role";
        String successMessage = "Successfully removed composite role: " + TEST_ROLE_NAME + " -> " + compositeRoleName;

        when(roleService.removeCompositeFromRole(TEST_REALM, TEST_ROLE_NAME, compositeRoleName)).thenReturn(successMessage);

        // Act
        String result = roleTool.removeCompositeFromRole(TEST_REALM, TEST_ROLE_NAME, compositeRoleName);

        // Assert
        assertEquals(successMessage, result);
        verify(roleService).removeCompositeFromRole(TEST_REALM, TEST_ROLE_NAME, compositeRoleName);
    }
}