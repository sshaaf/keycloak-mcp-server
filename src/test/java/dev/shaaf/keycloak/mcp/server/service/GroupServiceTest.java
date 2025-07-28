package dev.shaaf.keycloak.mcp.server.service;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.GroupResource;
import org.keycloak.admin.client.resource.GroupsResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.RoleMappingResource;
import org.keycloak.admin.client.resource.RoleScopeResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

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
public class GroupServiceTest {

    @InjectMock
    Keycloak keycloak;

    @Inject
    GroupService groupService;

    private RealmResource realmResource;
    private GroupsResource groupsResource;
    private GroupResource groupResource;
    private RolesResource rolesResource;
    private RoleResource roleResource;
    private RoleMappingResource roleMappingResource;
    private RoleScopeResource roleScopeResource;
    private Response response;

    private static final String TEST_REALM = "test-realm";
    private static final String TEST_GROUP_ID = "test-group-id";
    private static final String TEST_GROUP_NAME = "test-group";
    private static final String TEST_PARENT_GROUP_ID = "parent-group-id";
    private static final String TEST_SUBGROUP_NAME = "test-subgroup";
    private static final String TEST_ROLE_NAME = "test-role";

    @BeforeEach
    public void setup() {
        // Mock the Keycloak client chain
        realmResource = mock(RealmResource.class);
        groupsResource = mock(GroupsResource.class);
        groupResource = mock(GroupResource.class);
        rolesResource = mock(RolesResource.class);
        roleResource = mock(RoleResource.class);
        roleMappingResource = mock(RoleMappingResource.class);
        roleScopeResource = mock(RoleScopeResource.class);
        response = mock(Response.class);

        when(keycloak.realm(anyString())).thenReturn(realmResource);
        when(realmResource.groups()).thenReturn(groupsResource);
        when(groupsResource.group(anyString())).thenReturn(groupResource);
        when(realmResource.roles()).thenReturn(rolesResource);
        when(rolesResource.get(anyString())).thenReturn(roleResource);
        when(groupResource.roles()).thenReturn(roleMappingResource);
        when(roleMappingResource.realmLevel()).thenReturn(roleScopeResource);
    }

    @Test
    public void testGetGroups_Success_ReturnsGroupList() {
        // Arrange
        List<GroupRepresentation> expectedGroups = new ArrayList<>();
        GroupRepresentation group = new GroupRepresentation();
        group.setId(TEST_GROUP_ID);
        group.setName(TEST_GROUP_NAME);
        expectedGroups.add(group);

        when(groupsResource.groups()).thenReturn(expectedGroups);

        // Act
        List<GroupRepresentation> actualGroups = groupService.getGroups(TEST_REALM);

        // Assert
        assertEquals(expectedGroups, actualGroups);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).groups();
        verify(groupsResource).groups();
    }

    @Test
    public void testGetGroups_Exception_ReturnsEmptyList() {
        // Arrange
        when(groupsResource.groups()).thenThrow(new RuntimeException("Test exception"));

        // Act
        List<GroupRepresentation> actualGroups = groupService.getGroups(TEST_REALM);

        // Assert
        assertTrue(actualGroups.isEmpty());
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).groups();
        verify(groupsResource).groups();
    }

    @Test
    public void testGetGroup_Success_ReturnsGroup() {
        // Arrange
        GroupRepresentation expectedGroup = new GroupRepresentation();
        expectedGroup.setId(TEST_GROUP_ID);
        expectedGroup.setName(TEST_GROUP_NAME);

        when(groupResource.toRepresentation()).thenReturn(expectedGroup);

        // Act
        GroupRepresentation actualGroup = groupService.getGroup(TEST_REALM, TEST_GROUP_ID);

        // Assert
        assertNotNull(actualGroup);
        assertEquals(TEST_GROUP_ID, actualGroup.getId());
        assertEquals(TEST_GROUP_NAME, actualGroup.getName());
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).groups();
        verify(groupsResource).group(TEST_GROUP_ID);
        verify(groupResource).toRepresentation();
    }

    @Test
    public void testGetGroup_NotFound_ReturnsNull() {
        // Arrange
        when(groupResource.toRepresentation()).thenThrow(new NotFoundException("Group not found"));

        // Act
        GroupRepresentation actualGroup = groupService.getGroup(TEST_REALM, TEST_GROUP_ID);

        // Assert
        assertNull(actualGroup);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).groups();
        verify(groupsResource).group(TEST_GROUP_ID);
        verify(groupResource).toRepresentation();
    }

    @Test
    public void testGetGroup_Exception_ReturnsNull() {
        // Arrange
        when(groupResource.toRepresentation()).thenThrow(new RuntimeException("Test exception"));

        // Act
        GroupRepresentation actualGroup = groupService.getGroup(TEST_REALM, TEST_GROUP_ID);

        // Assert
        assertNull(actualGroup);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).groups();
        verify(groupsResource).group(TEST_GROUP_ID);
        verify(groupResource).toRepresentation();
    }

    @Test
    public void testCreateGroup_Success_ReturnsSuccessMessage() {
        // Arrange
        when(groupsResource.add(any(GroupRepresentation.class))).thenReturn(response);
        when(response.getStatus()).thenReturn(Response.Status.CREATED.getStatusCode());

        // Act
        String result = groupService.createGroup(TEST_REALM, TEST_GROUP_NAME);

        // Assert
        assertEquals("Successfully created group: " + TEST_GROUP_NAME, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).groups();
        verify(groupsResource).add(any(GroupRepresentation.class));
        verify(response).getStatus();
    }

    @Test
    public void testCreateGroup_Failure_ReturnsErrorMessage() {
        // Arrange
        when(groupsResource.add(any(GroupRepresentation.class))).thenReturn(response);
        when(response.getStatus()).thenReturn(Response.Status.BAD_REQUEST.getStatusCode());

        // Act
        String result = groupService.createGroup(TEST_REALM, TEST_GROUP_NAME);

        // Assert
        assertEquals("Error creating group: " + TEST_GROUP_NAME, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).groups();
        verify(groupsResource).add(any(GroupRepresentation.class));
        verify(response).getStatus();
        verify(response).close();
    }

    @Test
    public void testCreateGroup_Exception_ReturnsErrorMessage() {
        // Arrange
        when(groupsResource.add(any(GroupRepresentation.class))).thenThrow(new RuntimeException("Test exception"));

        // Act
        String result = groupService.createGroup(TEST_REALM, TEST_GROUP_NAME);

        // Assert
        assertTrue(result.startsWith("Error creating group: " + TEST_GROUP_NAME));
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).groups();
        verify(groupsResource).add(any(GroupRepresentation.class));
    }

    @Test
    public void testUpdateGroup_Success_ReturnsSuccessMessage() {
        // Arrange
        GroupRepresentation groupRepresentation = new GroupRepresentation();
        groupRepresentation.setId(TEST_GROUP_ID);
        groupRepresentation.setName(TEST_GROUP_NAME);

        doNothing().when(groupResource).update(any(GroupRepresentation.class));

        // Act
        String result = groupService.updateGroup(TEST_REALM, TEST_GROUP_ID, groupRepresentation);

        // Assert
        assertEquals("Successfully updated group: " + TEST_GROUP_ID, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).groups();
        verify(groupsResource).group(TEST_GROUP_ID);
        verify(groupResource).update(groupRepresentation);
    }

    @Test
    public void testUpdateGroup_NotFound_ReturnsErrorMessage() {
        // Arrange
        GroupRepresentation groupRepresentation = new GroupRepresentation();
        groupRepresentation.setId(TEST_GROUP_ID);
        groupRepresentation.setName(TEST_GROUP_NAME);

        doThrow(new NotFoundException("Group not found")).when(groupResource).update(any(GroupRepresentation.class));

        // Act
        String result = groupService.updateGroup(TEST_REALM, TEST_GROUP_ID, groupRepresentation);

        // Assert
        assertEquals("Group not found: " + TEST_GROUP_ID, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).groups();
        verify(groupsResource).group(TEST_GROUP_ID);
        verify(groupResource).update(groupRepresentation);
    }

    @Test
    public void testUpdateGroup_Exception_ReturnsErrorMessage() {
        // Arrange
        GroupRepresentation groupRepresentation = new GroupRepresentation();
        groupRepresentation.setId(TEST_GROUP_ID);
        groupRepresentation.setName(TEST_GROUP_NAME);

        doThrow(new RuntimeException("Test exception")).when(groupResource).update(any(GroupRepresentation.class));

        // Act
        String result = groupService.updateGroup(TEST_REALM, TEST_GROUP_ID, groupRepresentation);

        // Assert
        assertTrue(result.startsWith("Error updating group: " + TEST_GROUP_ID));
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).groups();
        verify(groupsResource).group(TEST_GROUP_ID);
        verify(groupResource).update(groupRepresentation);
    }

    @Test
    public void testDeleteGroup_Success_ReturnsSuccessMessage() {
        // Arrange
        GroupRepresentation group = new GroupRepresentation();
        group.setId(TEST_GROUP_ID);
        group.setName(TEST_GROUP_NAME);

        when(groupResource.toRepresentation()).thenReturn(group);
        doNothing().when(groupResource).remove();

        // Act
        String result = groupService.deleteGroup(TEST_REALM, TEST_GROUP_ID);

        // Assert
        assertEquals("Successfully deleted group: " + TEST_GROUP_ID, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).groups();
        verify(groupsResource).group(TEST_GROUP_ID);
        verify(groupResource).toRepresentation();
        verify(groupResource).remove();
    }

    @Test
    public void testDeleteGroup_GroupNotFound_ReturnsErrorMessage() {
        // Arrange
        when(groupResource.toRepresentation()).thenThrow(new NotFoundException("Group not found"));

        // Act
        String result = groupService.deleteGroup(TEST_REALM, TEST_GROUP_ID);

        // Assert
        assertEquals("Group not found: " + TEST_GROUP_ID, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).groups();
        verify(groupsResource).group(TEST_GROUP_ID);
        verify(groupResource).toRepresentation();
    }

    @Test
    public void testDeleteGroup_Exception_ReturnsErrorMessage() {
        // Arrange
        GroupRepresentation group = new GroupRepresentation();
        group.setId(TEST_GROUP_ID);
        group.setName(TEST_GROUP_NAME);

        when(groupResource.toRepresentation()).thenReturn(group);
        doThrow(new RuntimeException("Test exception")).when(groupResource).remove();

        // Act
        String result = groupService.deleteGroup(TEST_REALM, TEST_GROUP_ID);

        // Assert
        assertTrue(result.startsWith("Error deleting group: " + TEST_GROUP_ID));
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).groups();
        verify(groupsResource).group(TEST_GROUP_ID);
        verify(groupResource).toRepresentation();
        verify(groupResource).remove();
    }

    @Test
    public void testGetGroupMembers_Success_ReturnsUserList() {
        // Arrange
        List<UserRepresentation> expectedUsers = new ArrayList<>();
        UserRepresentation user = new UserRepresentation();
        user.setId("user-id");
        user.setUsername("username");
        expectedUsers.add(user);

        when(groupResource.members()).thenReturn(expectedUsers);

        // Act
        List<UserRepresentation> actualUsers = groupService.getGroupMembers(TEST_REALM, TEST_GROUP_ID);

        // Assert
        assertEquals(expectedUsers, actualUsers);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).groups();
        verify(groupsResource).group(TEST_GROUP_ID);
        verify(groupResource).members();
    }

    @Test
    public void testGetGroupMembers_NotFound_ReturnsEmptyList() {
        // Arrange
        when(groupResource.members()).thenThrow(new NotFoundException("Group not found"));

        // Act
        List<UserRepresentation> actualUsers = groupService.getGroupMembers(TEST_REALM, TEST_GROUP_ID);

        // Assert
        assertTrue(actualUsers.isEmpty());
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).groups();
        verify(groupsResource).group(TEST_GROUP_ID);
        verify(groupResource).members();
    }

    @Test
    public void testGetGroupMembers_Exception_ReturnsEmptyList() {
        // Arrange
        when(groupResource.members()).thenThrow(new RuntimeException("Test exception"));

        // Act
        List<UserRepresentation> actualUsers = groupService.getGroupMembers(TEST_REALM, TEST_GROUP_ID);

        // Assert
        assertTrue(actualUsers.isEmpty());
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).groups();
        verify(groupsResource).group(TEST_GROUP_ID);
        verify(groupResource).members();
    }

    @Test
    public void testGetGroupRoles_Success_ReturnsRoleList() {
        // Arrange
        List<RoleRepresentation> expectedRoles = new ArrayList<>();
        RoleRepresentation role = new RoleRepresentation();
        role.setId("role-id");
        role.setName(TEST_ROLE_NAME);
        expectedRoles.add(role);

        when(roleScopeResource.listEffective()).thenReturn(expectedRoles);

        // Act
        List<RoleRepresentation> actualRoles = groupService.getGroupRoles(TEST_REALM, TEST_GROUP_ID);

        // Assert
        assertEquals(expectedRoles, actualRoles);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).groups();
        verify(groupsResource).group(TEST_GROUP_ID);
        verify(groupResource).roles();
        verify(roleMappingResource).realmLevel();
        verify(roleScopeResource).listEffective();
    }

    @Test
    public void testGetGroupRoles_NotFound_ReturnsEmptyList() {
        // Arrange
        when(roleScopeResource.listEffective()).thenThrow(new NotFoundException("Group not found"));

        // Act
        List<RoleRepresentation> actualRoles = groupService.getGroupRoles(TEST_REALM, TEST_GROUP_ID);

        // Assert
        assertTrue(actualRoles.isEmpty());
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).groups();
        verify(groupsResource).group(TEST_GROUP_ID);
        verify(groupResource).roles();
        verify(roleMappingResource).realmLevel();
        verify(roleScopeResource).listEffective();
    }

    @Test
    public void testGetGroupRoles_Exception_ReturnsEmptyList() {
        // Arrange
        when(roleScopeResource.listEffective()).thenThrow(new RuntimeException("Test exception"));

        // Act
        List<RoleRepresentation> actualRoles = groupService.getGroupRoles(TEST_REALM, TEST_GROUP_ID);

        // Assert
        assertTrue(actualRoles.isEmpty());
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).groups();
        verify(groupsResource).group(TEST_GROUP_ID);
        verify(groupResource).roles();
        verify(roleMappingResource).realmLevel();
        verify(roleScopeResource).listEffective();
    }

    @Test
    public void testAddRoleToGroup_Success_ReturnsSuccessMessage() {
        // Arrange
        RoleRepresentation role = new RoleRepresentation();
        role.setId("role-id");
        role.setName(TEST_ROLE_NAME);

        when(roleResource.toRepresentation()).thenReturn(role);
        doNothing().when(roleScopeResource).add(any(List.class));

        // Act
        String result = groupService.addRoleToGroup(TEST_REALM, TEST_GROUP_ID, TEST_ROLE_NAME);

        // Assert
        assertEquals("Successfully added role to group: " + TEST_GROUP_ID + " -> " + TEST_ROLE_NAME, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).roles();
        verify(rolesResource).get(TEST_ROLE_NAME);
        verify(roleResource).toRepresentation();
        verify(realmResource).groups();
        verify(groupsResource).group(TEST_GROUP_ID);
        verify(groupResource).roles();
        verify(roleMappingResource).realmLevel();
        verify(roleScopeResource).add(any(List.class));
    }

    @Test
    public void testAddRoleToGroup_NotFound_ReturnsErrorMessage() {
        // Arrange
        when(roleResource.toRepresentation()).thenThrow(new NotFoundException("Role not found"));

        // Act
        String result = groupService.addRoleToGroup(TEST_REALM, TEST_GROUP_ID, TEST_ROLE_NAME);

        // Assert
        assertEquals("Group or role not found: " + TEST_GROUP_ID + " -> " + TEST_ROLE_NAME, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).roles();
        verify(rolesResource).get(TEST_ROLE_NAME);
        verify(roleResource).toRepresentation();
    }

    @Test
    public void testAddRoleToGroup_Exception_ReturnsErrorMessage() {
        // Arrange
        RoleRepresentation role = new RoleRepresentation();
        role.setId("role-id");
        role.setName(TEST_ROLE_NAME);

        when(roleResource.toRepresentation()).thenReturn(role);
        doThrow(new RuntimeException("Test exception")).when(roleScopeResource).add(any(List.class));

        // Act
        String result = groupService.addRoleToGroup(TEST_REALM, TEST_GROUP_ID, TEST_ROLE_NAME);

        // Assert
        assertTrue(result.startsWith("Error adding role to group: " + TEST_GROUP_ID + " -> " + TEST_ROLE_NAME));
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).roles();
        verify(rolesResource).get(TEST_ROLE_NAME);
        verify(roleResource).toRepresentation();
        verify(realmResource).groups();
        verify(groupsResource).group(TEST_GROUP_ID);
        verify(groupResource).roles();
        verify(roleMappingResource).realmLevel();
        verify(roleScopeResource).add(any(List.class));
    }

    @Test
    public void testRemoveRoleFromGroup_Success_ReturnsSuccessMessage() {
        // Arrange
        RoleRepresentation role = new RoleRepresentation();
        role.setId("role-id");
        role.setName(TEST_ROLE_NAME);

        when(roleResource.toRepresentation()).thenReturn(role);
        doNothing().when(roleScopeResource).remove(any(List.class));

        // Act
        String result = groupService.removeRoleFromGroup(TEST_REALM, TEST_GROUP_ID, TEST_ROLE_NAME);

        // Assert
        assertEquals("Successfully removed role from group: " + TEST_GROUP_ID + " -> " + TEST_ROLE_NAME, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).roles();
        verify(rolesResource).get(TEST_ROLE_NAME);
        verify(roleResource).toRepresentation();
        verify(realmResource).groups();
        verify(groupsResource).group(TEST_GROUP_ID);
        verify(groupResource).roles();
        verify(roleMappingResource).realmLevel();
        verify(roleScopeResource).remove(any(List.class));
    }

    @Test
    public void testRemoveRoleFromGroup_NotFound_ReturnsErrorMessage() {
        // Arrange
        when(roleResource.toRepresentation()).thenThrow(new NotFoundException("Role not found"));

        // Act
        String result = groupService.removeRoleFromGroup(TEST_REALM, TEST_GROUP_ID, TEST_ROLE_NAME);

        // Assert
        assertEquals("Group or role not found: " + TEST_GROUP_ID + " -> " + TEST_ROLE_NAME, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).roles();
        verify(rolesResource).get(TEST_ROLE_NAME);
        verify(roleResource).toRepresentation();
    }

    @Test
    public void testRemoveRoleFromGroup_Exception_ReturnsErrorMessage() {
        // Arrange
        RoleRepresentation role = new RoleRepresentation();
        role.setId("role-id");
        role.setName(TEST_ROLE_NAME);

        when(roleResource.toRepresentation()).thenReturn(role);
        doThrow(new RuntimeException("Test exception")).when(roleScopeResource).remove(any(List.class));

        // Act
        String result = groupService.removeRoleFromGroup(TEST_REALM, TEST_GROUP_ID, TEST_ROLE_NAME);

        // Assert
        assertTrue(result.startsWith("Error removing role from group: " + TEST_GROUP_ID + " -> " + TEST_ROLE_NAME));
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).roles();
        verify(rolesResource).get(TEST_ROLE_NAME);
        verify(roleResource).toRepresentation();
        verify(realmResource).groups();
        verify(groupsResource).group(TEST_GROUP_ID);
        verify(groupResource).roles();
        verify(roleMappingResource).realmLevel();
        verify(roleScopeResource).remove(any(List.class));
    }

    @Test
    public void testCreateSubGroup_Success_ReturnsSuccessMessage() {
        // Arrange
        when(groupResource.subGroup(any(GroupRepresentation.class))).thenReturn(response);
        when(response.getStatus()).thenReturn(Response.Status.CREATED.getStatusCode());

        // Act
        String result = groupService.createSubGroup(TEST_REALM, TEST_PARENT_GROUP_ID, TEST_SUBGROUP_NAME);

        // Assert
        assertEquals("Successfully created subgroup: " + TEST_PARENT_GROUP_ID + " -> " + TEST_SUBGROUP_NAME, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).groups();
        verify(groupsResource).group(TEST_PARENT_GROUP_ID);
        verify(groupResource).subGroup(any(GroupRepresentation.class));
        verify(response).getStatus();
    }

    @Test
    public void testCreateSubGroup_Failure_ReturnsErrorMessage() {
        // Arrange
        when(groupResource.subGroup(any(GroupRepresentation.class))).thenReturn(response);
        when(response.getStatus()).thenReturn(Response.Status.BAD_REQUEST.getStatusCode());

        // Act
        String result = groupService.createSubGroup(TEST_REALM, TEST_PARENT_GROUP_ID, TEST_SUBGROUP_NAME);

        // Assert
        assertEquals("Error creating subgroup: " + TEST_PARENT_GROUP_ID + " -> " + TEST_SUBGROUP_NAME, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).groups();
        verify(groupsResource).group(TEST_PARENT_GROUP_ID);
        verify(groupResource).subGroup(any(GroupRepresentation.class));
        verify(response).getStatus();
        verify(response).close();
    }

    @Test
    public void testCreateSubGroup_NotFound_ReturnsErrorMessage() {
        // Arrange
        when(groupResource.subGroup(any(GroupRepresentation.class))).thenThrow(new NotFoundException("Parent group not found"));

        // Act
        String result = groupService.createSubGroup(TEST_REALM, TEST_PARENT_GROUP_ID, TEST_SUBGROUP_NAME);

        // Assert
        assertEquals("Parent group not found: " + TEST_PARENT_GROUP_ID, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).groups();
        verify(groupsResource).group(TEST_PARENT_GROUP_ID);
        verify(groupResource).subGroup(any(GroupRepresentation.class));
    }

    @Test
    public void testCreateSubGroup_Exception_ReturnsErrorMessage() {
        // Arrange
        when(groupResource.subGroup(any(GroupRepresentation.class))).thenThrow(new RuntimeException("Test exception"));

        // Act
        String result = groupService.createSubGroup(TEST_REALM, TEST_PARENT_GROUP_ID, TEST_SUBGROUP_NAME);

        // Assert
        assertTrue(result.startsWith("Error creating subgroup: " + TEST_PARENT_GROUP_ID + " -> " + TEST_SUBGROUP_NAME));
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).groups();
        verify(groupsResource).group(TEST_PARENT_GROUP_ID);
        verify(groupResource).subGroup(any(GroupRepresentation.class));
    }
}