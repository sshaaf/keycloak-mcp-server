package dev.shaaf.experimental.service;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.representations.idm.RoleRepresentation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
public class RoleServiceTest {

    @InjectMock
    Keycloak keycloak;

    @Inject
    RoleService roleService;

    private RealmResource realmResource;
    private RolesResource rolesResource;
    private RoleResource roleResource;

    private static final String TEST_REALM = "test-realm";
    private static final String TEST_ROLE_NAME = "test-role";
    private static final String TEST_ROLE_DESCRIPTION = "Test role description";
    private static final String TEST_COMPOSITE_ROLE_NAME = "composite-role";

    @BeforeEach
    public void setup() {
        // Mock the Keycloak client chain
        realmResource = mock(RealmResource.class);
        rolesResource = mock(RolesResource.class);
        roleResource = mock(RoleResource.class);

        when(keycloak.realm(anyString())).thenReturn(realmResource);
        when(realmResource.roles()).thenReturn(rolesResource);
        when(rolesResource.get(anyString())).thenReturn(roleResource);
    }

    @Test
    public void testGetRealmRoles_Success_ReturnsRoleList() {
        // Arrange
        List<RoleRepresentation> expectedRoles = new ArrayList<>();
        RoleRepresentation role = new RoleRepresentation();
        role.setName(TEST_ROLE_NAME);
        role.setDescription(TEST_ROLE_DESCRIPTION);
        expectedRoles.add(role);

        when(rolesResource.list()).thenReturn(expectedRoles);

        // Act
        List<RoleRepresentation> actualRoles = roleService.getRealmRoles(TEST_REALM);

        // Assert
        assertEquals(expectedRoles, actualRoles);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).roles();
        verify(rolesResource).list();
    }

    @Test
    public void testGetRealmRoles_Exception_ReturnsEmptyList() {
        // Arrange
        when(rolesResource.list()).thenThrow(new RuntimeException("Test exception"));

        // Act
        List<RoleRepresentation> actualRoles = roleService.getRealmRoles(TEST_REALM);

        // Assert
        assertTrue(actualRoles.isEmpty());
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).roles();
        verify(rolesResource).list();
    }

    @Test
    public void testGetRealmRole_Success_ReturnsRole() {
        // Arrange
        RoleRepresentation expectedRole = new RoleRepresentation();
        expectedRole.setName(TEST_ROLE_NAME);
        expectedRole.setDescription(TEST_ROLE_DESCRIPTION);

        when(roleResource.toRepresentation()).thenReturn(expectedRole);

        // Act
        RoleRepresentation actualRole = roleService.getRealmRole(TEST_REALM, TEST_ROLE_NAME);

        // Assert
        assertNotNull(actualRole);
        assertEquals(TEST_ROLE_NAME, actualRole.getName());
        assertEquals(TEST_ROLE_DESCRIPTION, actualRole.getDescription());
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).roles();
        verify(rolesResource).get(TEST_ROLE_NAME);
        verify(roleResource).toRepresentation();
    }

    @Test
    public void testGetRealmRole_NotFound_ReturnsNull() {
        // Arrange
        when(roleResource.toRepresentation()).thenThrow(new NotFoundException("Role not found"));

        // Act
        RoleRepresentation actualRole = roleService.getRealmRole(TEST_REALM, TEST_ROLE_NAME);

        // Assert
        assertNull(actualRole);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).roles();
        verify(rolesResource).get(TEST_ROLE_NAME);
        verify(roleResource).toRepresentation();
    }

    @Test
    public void testGetRealmRole_Exception_ReturnsNull() {
        // Arrange
        when(roleResource.toRepresentation()).thenThrow(new RuntimeException("Test exception"));

        // Act
        RoleRepresentation actualRole = roleService.getRealmRole(TEST_REALM, TEST_ROLE_NAME);

        // Assert
        assertNull(actualRole);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).roles();
        verify(rolesResource).get(TEST_ROLE_NAME);
        verify(roleResource).toRepresentation();
    }

    @Test
    public void testCreateRealmRole_Success_ReturnsSuccessMessage() {
        // Arrange
        doNothing().when(rolesResource).create(any(RoleRepresentation.class));

        // Act
        String result = roleService.createRealmRole(TEST_REALM, TEST_ROLE_NAME, TEST_ROLE_DESCRIPTION);

        // Assert
        assertEquals("Successfully created role: " + TEST_ROLE_NAME, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).roles();
        verify(rolesResource).create(any(RoleRepresentation.class));
    }

    @Test
    public void testCreateRealmRole_Exception_ReturnsErrorMessage() {
        // Arrange
        doThrow(new RuntimeException("Test exception")).when(rolesResource).create(any(RoleRepresentation.class));

        // Act
        String result = roleService.createRealmRole(TEST_REALM, TEST_ROLE_NAME, TEST_ROLE_DESCRIPTION);

        // Assert
        assertTrue(result.startsWith("Error creating role: " + TEST_ROLE_NAME));
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).roles();
        verify(rolesResource).create(any(RoleRepresentation.class));
    }

    @Test
    public void testUpdateRealmRole_Success_ReturnsSuccessMessage() {
        // Arrange
        RoleRepresentation roleRepresentation = new RoleRepresentation();
        roleRepresentation.setName(TEST_ROLE_NAME);
        roleRepresentation.setDescription(TEST_ROLE_DESCRIPTION);

        doNothing().when(roleResource).update(any(RoleRepresentation.class));

        // Act
        String result = roleService.updateRealmRole(TEST_REALM, TEST_ROLE_NAME, roleRepresentation);

        // Assert
        assertEquals("Successfully updated role: " + TEST_ROLE_NAME, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).roles();
        verify(rolesResource).get(TEST_ROLE_NAME);
        verify(roleResource).update(roleRepresentation);
    }

    @Test
    public void testUpdateRealmRole_NotFound_ReturnsErrorMessage() {
        // Arrange
        RoleRepresentation roleRepresentation = new RoleRepresentation();
        roleRepresentation.setName(TEST_ROLE_NAME);
        roleRepresentation.setDescription(TEST_ROLE_DESCRIPTION);

        doThrow(new NotFoundException("Role not found")).when(roleResource).update(any(RoleRepresentation.class));

        // Act
        String result = roleService.updateRealmRole(TEST_REALM, TEST_ROLE_NAME, roleRepresentation);

        // Assert
        assertEquals("Role not found: " + TEST_ROLE_NAME, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).roles();
        verify(rolesResource).get(TEST_ROLE_NAME);
        verify(roleResource).update(roleRepresentation);
    }

    @Test
    public void testUpdateRealmRole_Exception_ReturnsErrorMessage() {
        // Arrange
        RoleRepresentation roleRepresentation = new RoleRepresentation();
        roleRepresentation.setName(TEST_ROLE_NAME);
        roleRepresentation.setDescription(TEST_ROLE_DESCRIPTION);

        doThrow(new RuntimeException("Test exception")).when(roleResource).update(any(RoleRepresentation.class));

        // Act
        String result = roleService.updateRealmRole(TEST_REALM, TEST_ROLE_NAME, roleRepresentation);

        // Assert
        assertTrue(result.startsWith("Error updating role: " + TEST_ROLE_NAME));
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).roles();
        verify(rolesResource).get(TEST_ROLE_NAME);
        verify(roleResource).update(roleRepresentation);
    }

    @Test
    public void testDeleteRealmRole_Success_ReturnsSuccessMessage() {
        // Arrange
        doNothing().when(rolesResource).deleteRole(TEST_ROLE_NAME);

        // Act
        String result = roleService.deleteRealmRole(TEST_REALM, TEST_ROLE_NAME);

        // Assert
        assertEquals("Successfully deleted role: " + TEST_ROLE_NAME, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).roles();
        verify(rolesResource).deleteRole(TEST_ROLE_NAME);
    }

    @Test
    public void testDeleteRealmRole_NotFound_ReturnsErrorMessage() {
        // Arrange
        doThrow(new NotFoundException("Role not found")).when(rolesResource).deleteRole(TEST_ROLE_NAME);

        // Act
        String result = roleService.deleteRealmRole(TEST_REALM, TEST_ROLE_NAME);

        // Assert
        assertEquals("Role not found: " + TEST_ROLE_NAME, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).roles();
        verify(rolesResource).deleteRole(TEST_ROLE_NAME);
    }

    @Test
    public void testDeleteRealmRole_Exception_ReturnsErrorMessage() {
        // Arrange
        doThrow(new RuntimeException("Test exception")).when(rolesResource).deleteRole(TEST_ROLE_NAME);

        // Act
        String result = roleService.deleteRealmRole(TEST_REALM, TEST_ROLE_NAME);

        // Assert
        assertTrue(result.startsWith("Error deleting role: " + TEST_ROLE_NAME));
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).roles();
        verify(rolesResource).deleteRole(TEST_ROLE_NAME);
    }

    @Test
    public void testGetRoleComposites_Success_ReturnsCompositeList() {
        // Arrange
        Set<RoleRepresentation> composites = new HashSet<>();
        RoleRepresentation compositeRole = new RoleRepresentation();
        compositeRole.setName(TEST_COMPOSITE_ROLE_NAME);
        composites.add(compositeRole);

        when(roleResource.getRoleComposites()).thenReturn(composites);

        // Act
        List<RoleRepresentation> result = roleService.getRoleComposites(TEST_REALM, TEST_ROLE_NAME);

        // Assert
        assertEquals(1, result.size());
        assertEquals(TEST_COMPOSITE_ROLE_NAME, result.get(0).getName());
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).roles();
        verify(rolesResource).get(TEST_ROLE_NAME);
        verify(roleResource).getRoleComposites();
    }

    @Test
    public void testGetRoleComposites_NotFound_ReturnsEmptyList() {
        // Arrange
        when(roleResource.getRoleComposites()).thenThrow(new NotFoundException("Role not found"));

        // Act
        List<RoleRepresentation> result = roleService.getRoleComposites(TEST_REALM, TEST_ROLE_NAME);

        // Assert
        assertTrue(result.isEmpty());
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).roles();
        verify(rolesResource).get(TEST_ROLE_NAME);
        verify(roleResource).getRoleComposites();
    }

    @Test
    public void testGetRoleComposites_Exception_ReturnsEmptyList() {
        // Arrange
        when(roleResource.getRoleComposites()).thenThrow(new RuntimeException("Test exception"));

        // Act
        List<RoleRepresentation> result = roleService.getRoleComposites(TEST_REALM, TEST_ROLE_NAME);

        // Assert
        assertTrue(result.isEmpty());
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).roles();
        verify(rolesResource).get(TEST_ROLE_NAME);
        verify(roleResource).getRoleComposites();
    }

    @Test
    public void testAddCompositeToRole_Success_ReturnsSuccessMessage() {
        // Arrange
        RoleRepresentation compositeRole = new RoleRepresentation();
        compositeRole.setName(TEST_COMPOSITE_ROLE_NAME);

        // Mock the getRealmRole method to return the composite role
        when(roleResource.toRepresentation()).thenReturn(compositeRole);
        
        // Mock the addComposites method
        doNothing().when(roleResource).addComposites(any(List.class));

        // Act
        String result = roleService.addCompositeToRole(TEST_REALM, TEST_ROLE_NAME, TEST_COMPOSITE_ROLE_NAME);

        // Assert
        assertEquals("Successfully added composite role: " + TEST_ROLE_NAME + " -> " + TEST_COMPOSITE_ROLE_NAME, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).roles();
        verify(rolesResource).get(TEST_COMPOSITE_ROLE_NAME);
        verify(roleResource).toRepresentation();
        verify(rolesResource).get(TEST_ROLE_NAME);
        verify(roleResource).addComposites(any(List.class));
    }

    @Test
    public void testAddCompositeToRole_CompositeRoleNotFound_ReturnsErrorMessage() {
        // Arrange
        // Mock the getRealmRole method to return null for the composite role
        when(roleResource.toRepresentation()).thenThrow(new NotFoundException("Role not found"));

        // Act
        String result = roleService.addCompositeToRole(TEST_REALM, TEST_ROLE_NAME, TEST_COMPOSITE_ROLE_NAME);

        // Assert
        assertEquals("Composite role not found: " + TEST_COMPOSITE_ROLE_NAME, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).roles();
        verify(rolesResource).get(TEST_COMPOSITE_ROLE_NAME);
        verify(roleResource).toRepresentation();
    }

    @Test
    public void testAddCompositeToRole_RoleNotFound_ReturnsErrorMessage() {
        // Arrange
        RoleRepresentation compositeRole = new RoleRepresentation();
        compositeRole.setName(TEST_COMPOSITE_ROLE_NAME);

        // Mock the getRealmRole method to return the composite role
        when(roleResource.toRepresentation()).thenReturn(compositeRole);
        
        // Mock the addComposites method to throw NotFoundException
        doThrow(new NotFoundException("Role not found")).when(roleResource).addComposites(any(List.class));

        // Act
        String result = roleService.addCompositeToRole(TEST_REALM, TEST_ROLE_NAME, TEST_COMPOSITE_ROLE_NAME);

        // Assert
        assertEquals("Role not found: " + TEST_ROLE_NAME, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).roles();
        verify(rolesResource).get(TEST_COMPOSITE_ROLE_NAME);
        verify(roleResource).toRepresentation();
        verify(rolesResource).get(TEST_ROLE_NAME);
        verify(roleResource).addComposites(any(List.class));
    }

    @Test
    public void testAddCompositeToRole_Exception_ReturnsErrorMessage() {
        // Arrange
        RoleRepresentation compositeRole = new RoleRepresentation();
        compositeRole.setName(TEST_COMPOSITE_ROLE_NAME);

        // Mock the getRealmRole method to return the composite role
        when(roleResource.toRepresentation()).thenReturn(compositeRole);
        
        // Mock the addComposites method to throw RuntimeException
        doThrow(new RuntimeException("Test exception")).when(roleResource).addComposites(any(List.class));

        // Act
        String result = roleService.addCompositeToRole(TEST_REALM, TEST_ROLE_NAME, TEST_COMPOSITE_ROLE_NAME);

        // Assert
        assertTrue(result.startsWith("Error adding composite role: " + TEST_ROLE_NAME + " -> " + TEST_COMPOSITE_ROLE_NAME));
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).roles();
        verify(rolesResource).get(TEST_COMPOSITE_ROLE_NAME);
        verify(roleResource).toRepresentation();
        verify(rolesResource).get(TEST_ROLE_NAME);
        verify(roleResource).addComposites(any(List.class));
    }

    @Test
    public void testRemoveCompositeFromRole_Success_ReturnsSuccessMessage() {
        // Arrange
        RoleRepresentation compositeRole = new RoleRepresentation();
        compositeRole.setName(TEST_COMPOSITE_ROLE_NAME);

        // Mock the getRealmRole method to return the composite role
        when(roleResource.toRepresentation()).thenReturn(compositeRole);
        
        // Mock the deleteComposites method
        doNothing().when(roleResource).deleteComposites(any(List.class));

        // Act
        String result = roleService.removeCompositeFromRole(TEST_REALM, TEST_ROLE_NAME, TEST_COMPOSITE_ROLE_NAME);

        // Assert
        assertEquals("Successfully removed composite role: " + TEST_ROLE_NAME + " -> " + TEST_COMPOSITE_ROLE_NAME, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).roles();
        verify(rolesResource).get(TEST_COMPOSITE_ROLE_NAME);
        verify(roleResource).toRepresentation();
        verify(rolesResource).get(TEST_ROLE_NAME);
        verify(roleResource).deleteComposites(any(List.class));
    }

    @Test
    public void testRemoveCompositeFromRole_CompositeRoleNotFound_ReturnsErrorMessage() {
        // Arrange
        // Mock the getRealmRole method to return null for the composite role
        when(roleResource.toRepresentation()).thenThrow(new NotFoundException("Role not found"));

        // Act
        String result = roleService.removeCompositeFromRole(TEST_REALM, TEST_ROLE_NAME, TEST_COMPOSITE_ROLE_NAME);

        // Assert
        assertEquals("Composite role not found: " + TEST_COMPOSITE_ROLE_NAME, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).roles();
        verify(rolesResource).get(TEST_COMPOSITE_ROLE_NAME);
        verify(roleResource).toRepresentation();
    }

    @Test
    public void testRemoveCompositeFromRole_RoleNotFound_ReturnsErrorMessage() {
        // Arrange
        RoleRepresentation compositeRole = new RoleRepresentation();
        compositeRole.setName(TEST_COMPOSITE_ROLE_NAME);

        // Mock the getRealmRole method to return the composite role
        when(roleResource.toRepresentation()).thenReturn(compositeRole);
        
        // Mock the deleteComposites method to throw NotFoundException
        doThrow(new NotFoundException("Role not found")).when(roleResource).deleteComposites(any(List.class));

        // Act
        String result = roleService.removeCompositeFromRole(TEST_REALM, TEST_ROLE_NAME, TEST_COMPOSITE_ROLE_NAME);

        // Assert
        assertEquals("Role not found: " + TEST_ROLE_NAME, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).roles();
        verify(rolesResource).get(TEST_COMPOSITE_ROLE_NAME);
        verify(roleResource).toRepresentation();
        verify(rolesResource).get(TEST_ROLE_NAME);
        verify(roleResource).deleteComposites(any(List.class));
    }

    @Test
    public void testRemoveCompositeFromRole_Exception_ReturnsErrorMessage() {
        // Arrange
        RoleRepresentation compositeRole = new RoleRepresentation();
        compositeRole.setName(TEST_COMPOSITE_ROLE_NAME);

        // Mock the getRealmRole method to return the composite role
        when(roleResource.toRepresentation()).thenReturn(compositeRole);
        
        // Mock the deleteComposites method to throw RuntimeException
        doThrow(new RuntimeException("Test exception")).when(roleResource).deleteComposites(any(List.class));

        // Act
        String result = roleService.removeCompositeFromRole(TEST_REALM, TEST_ROLE_NAME, TEST_COMPOSITE_ROLE_NAME);

        // Assert
        assertTrue(result.startsWith("Error removing composite role: " + TEST_ROLE_NAME + " -> " + TEST_COMPOSITE_ROLE_NAME));
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).roles();
        verify(rolesResource).get(TEST_COMPOSITE_ROLE_NAME);
        verify(roleResource).toRepresentation();
        verify(rolesResource).get(TEST_ROLE_NAME);
        verify(roleResource).deleteComposites(any(List.class));
    }
}