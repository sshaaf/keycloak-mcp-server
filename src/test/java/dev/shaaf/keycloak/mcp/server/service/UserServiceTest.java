package dev.shaaf.keycloak.mcp.server.service;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.admin.client.resource.RoleMappingResource;
import org.keycloak.admin.client.resource.RoleScopeResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
public class UserServiceTest {

    @InjectMock
    Keycloak keycloak;

    @Inject
    UserService userService;

    private RealmResource realmResource;
    private UsersResource usersResource;
    private Response response;
    private UserResource userResource;
    private RoleMappingResource roleMappingResource;
    private RoleScopeResource roleScopeResource;

    private static final String TEST_REALM = "test-realm";
    private static final String TEST_USERNAME = "test-user";
    private static final String TEST_USER_ID = "test-user-id";
    private static final String TEST_FIRST_NAME = "Test";
    private static final String TEST_LAST_NAME = "User";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "password";

    @BeforeEach
    public void setup() {
        // Mock the Keycloak client chain
        realmResource = mock(RealmResource.class);
        usersResource = mock(UsersResource.class);
        response = mock(Response.class);
        userResource = mock(UserResource.class);
        roleMappingResource = mock(RoleMappingResource.class);
        roleScopeResource = mock(RoleScopeResource.class);

        when(keycloak.realm(anyString())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(userResource.roles()).thenReturn(roleMappingResource);
        when(roleMappingResource.realmLevel()).thenReturn(roleScopeResource);
    }

    @Test
    public void testGetUsers_ReturnsUserList() {
        // Arrange
        List<UserRepresentation> expectedUsers = new ArrayList<>();
        UserRepresentation user = new UserRepresentation();
        user.setId(TEST_USER_ID);
        user.setUsername(TEST_USERNAME);
        expectedUsers.add(user);

        when(usersResource.list()).thenReturn(expectedUsers);

        // Act
        List<UserRepresentation> actualUsers = userService.getUsers(TEST_REALM);

        // Assert
        assertEquals(expectedUsers, actualUsers);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).users();
        verify(usersResource).list();
    }

    @Test
    public void testAddUser_Success_ReturnsSuccessMessage() {
        // Arrange
        when(usersResource.create(any(UserRepresentation.class))).thenReturn(response);
        when(response.getStatus()).thenReturn(Response.Status.CREATED.getStatusCode());

        // Act
        String result = userService.addUser(TEST_REALM, TEST_USERNAME, TEST_FIRST_NAME, TEST_LAST_NAME, TEST_EMAIL, TEST_PASSWORD);

        // Assert
        assertEquals("Successfully created user: " + TEST_USERNAME, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).users();
        verify(usersResource).create(any(UserRepresentation.class));
        verify(response).getStatus();
    }

    @Test
    public void testAddUser_Failure_ReturnsErrorMessage() {
        // Arrange
        when(usersResource.create(any(UserRepresentation.class))).thenReturn(response);
        when(response.getStatus()).thenReturn(Response.Status.BAD_REQUEST.getStatusCode());

        // Act
        String result = userService.addUser(TEST_REALM, TEST_USERNAME, TEST_FIRST_NAME, TEST_LAST_NAME, TEST_EMAIL, TEST_PASSWORD);

        // Assert
        assertEquals("Error creating user: " + " " + TEST_USERNAME, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).users();
        verify(usersResource).create(any(UserRepresentation.class));
        verify(response).getStatus();
        verify(response).close();
    }

    @Test
    public void testDeleteUser_UserExists_ReturnsSuccessMessage() {
        // Arrange
        UserRepresentation user = new UserRepresentation();
        user.setId(TEST_USER_ID);
        user.setUsername(TEST_USERNAME);
        List<UserRepresentation> userList = List.of(user);

        when(usersResource.search(TEST_USERNAME)).thenReturn(userList);
        when(usersResource.delete(TEST_USER_ID)).thenReturn(response);
        when(response.getStatus()).thenReturn(Response.Status.NO_CONTENT.getStatusCode());

        // Act
        String result = userService.deleteUser(TEST_REALM, TEST_USERNAME);

        // Assert
        assertEquals("successfully deleted: " + TEST_USER_ID, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).users();
        verify(usersResource).search(TEST_USERNAME);
        verify(usersResource).delete(TEST_USER_ID);
        verify(response).getStatus();
    }

    @Test
    public void testDeleteUser_UserExists_DeleteFails_ReturnsErrorMessage() {
        // Arrange
        UserRepresentation user = new UserRepresentation();
        user.setId(TEST_USER_ID);
        user.setUsername(TEST_USERNAME);
        List<UserRepresentation> userList = List.of(user);

        when(usersResource.search(TEST_USERNAME)).thenReturn(userList);
        when(usersResource.delete(TEST_USER_ID)).thenReturn(response);
        when(response.getStatus()).thenReturn(Response.Status.BAD_REQUEST.getStatusCode());

        // Act
        String result = userService.deleteUser(TEST_REALM, TEST_USERNAME);

        // Assert
        assertEquals("failed to delete: " + TEST_USER_ID, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).users();
        verify(usersResource).search(TEST_USERNAME);
        verify(usersResource).delete(TEST_USER_ID);
        verify(response).getStatus();
    }

    @Test
    public void testGetUserByUsername_UserExists_ReturnsUser() {
        // Arrange
        UserRepresentation expectedUser = new UserRepresentation();
        expectedUser.setId(TEST_USER_ID);
        expectedUser.setUsername(TEST_USERNAME);
        List<UserRepresentation> userList = List.of(expectedUser);

        when(usersResource.search(TEST_USERNAME)).thenReturn(userList);

        // Act
        UserRepresentation actualUser = userService.getUserByUsername(TEST_REALM, TEST_USERNAME);

        // Assert
        assertNotNull(actualUser);
        assertEquals(TEST_USER_ID, actualUser.getId());
        assertEquals(TEST_USERNAME, actualUser.getUsername());
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).users();
        verify(usersResource).search(TEST_USERNAME);
    }

    @Test
    public void testGetUserByUsername_UserDoesNotExist_ReturnsNull() {
        // Arrange
        when(usersResource.search(TEST_USERNAME)).thenReturn(new ArrayList<>());

        // Act
        UserRepresentation actualUser = userService.getUserByUsername(TEST_REALM, TEST_USERNAME);

        // Assert
        assertNull(actualUser);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).users();
        verify(usersResource).search(TEST_USERNAME);
    }

    @Test
    public void testGetUserByUsername_MultipleUsersFound_ReturnsMatchingUser() {
        // Arrange
        UserRepresentation expectedUser = new UserRepresentation();
        expectedUser.setId(TEST_USER_ID);
        expectedUser.setUsername(TEST_USERNAME);
        
        UserRepresentation otherUser = new UserRepresentation();
        otherUser.setId("other-id");
        otherUser.setUsername("other-username");
        
        List<UserRepresentation> userList = List.of(otherUser, expectedUser);

        when(usersResource.search(TEST_USERNAME)).thenReturn(userList);

        // Act
        UserRepresentation actualUser = userService.getUserByUsername(TEST_REALM, TEST_USERNAME);

        // Assert
        assertNotNull(actualUser);
        assertEquals(TEST_USER_ID, actualUser.getId());
        assertEquals(TEST_USERNAME, actualUser.getUsername());
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).users();
        verify(usersResource).search(TEST_USERNAME);
    }
    
    @Test
    public void testGetUserById_UserExists_ReturnsUser() {
        // Arrange
        UserRepresentation expectedUser = new UserRepresentation();
        expectedUser.setId(TEST_USER_ID);
        expectedUser.setUsername(TEST_USERNAME);
        
        when(usersResource.get(TEST_USER_ID)).thenReturn(userResource);
        when(userResource.toRepresentation()).thenReturn(expectedUser);

        // Act
        UserRepresentation actualUser = userService.getUserById(TEST_REALM, TEST_USER_ID);

        // Assert
        assertNotNull(actualUser);
        assertEquals(TEST_USER_ID, actualUser.getId());
        assertEquals(TEST_USERNAME, actualUser.getUsername());
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).users();
        verify(usersResource).get(TEST_USER_ID);
        verify(userResource).toRepresentation();
    }
    
    @Test
    public void testGetUserById_UserNotFound_ReturnsNull() {
        // Arrange
        when(usersResource.get(TEST_USER_ID)).thenReturn(userResource);
        when(userResource.toRepresentation()).thenThrow(new jakarta.ws.rs.NotFoundException("User not found"));

        // Act
        UserRepresentation actualUser = userService.getUserById(TEST_REALM, TEST_USER_ID);

        // Assert
        assertNull(actualUser);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).users();
        verify(usersResource).get(TEST_USER_ID);
        verify(userResource).toRepresentation();
    }
    
    @Test
    public void testGetUserById_Exception_ReturnsNull() {
        // Arrange
        when(usersResource.get(TEST_USER_ID)).thenReturn(userResource);
        when(userResource.toRepresentation()).thenThrow(new RuntimeException("Unexpected error"));

        // Act
        UserRepresentation actualUser = userService.getUserById(TEST_REALM, TEST_USER_ID);

        // Assert
        assertNull(actualUser);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).users();
        verify(usersResource).get(TEST_USER_ID);
        verify(userResource).toRepresentation();
    }
    
    @Test
    public void testUpdateUser_Success_ReturnsSuccessMessage() {
        // Arrange
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(TEST_USERNAME);
        
        when(usersResource.get(TEST_USER_ID)).thenReturn(userResource);
        
        // Act
        String result = userService.updateUser(TEST_REALM, TEST_USER_ID, userRepresentation);
        
        // Assert
        assertEquals("Successfully updated user: " + TEST_USER_ID, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).users();
        verify(usersResource).get(TEST_USER_ID);
        verify(userResource).update(userRepresentation);
    }
    
    @Test
    public void testUpdateUser_UserNotFound_ReturnsErrorMessage() {
        // Arrange
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(TEST_USERNAME);
        
        when(usersResource.get(TEST_USER_ID)).thenReturn(userResource);
        Mockito.doThrow(new jakarta.ws.rs.NotFoundException("User not found"))
            .when(userResource).update(userRepresentation);
        
        // Act
        String result = userService.updateUser(TEST_REALM, TEST_USER_ID, userRepresentation);
        
        // Assert
        assertEquals("User not found: " + TEST_USER_ID, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).users();
        verify(usersResource).get(TEST_USER_ID);
        verify(userResource).update(userRepresentation);
    }
    
    @Test
    public void testUpdateUser_Exception_ReturnsErrorMessage() {
        // Arrange
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(TEST_USERNAME);
        
        when(usersResource.get(TEST_USER_ID)).thenReturn(userResource);
        Mockito.doThrow(new RuntimeException("Unexpected error"))
            .when(userResource).update(userRepresentation);
        
        // Act
        String result = userService.updateUser(TEST_REALM, TEST_USER_ID, userRepresentation);
        
        // Assert
        assertTrue(result.startsWith("Error updating user: " + TEST_USER_ID));
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).users();
        verify(usersResource).get(TEST_USER_ID);
        verify(userResource).update(userRepresentation);
    }
    
    @Test
    public void testGetUserGroups_Success_ReturnsGroupList() {
        // Arrange
        List<GroupRepresentation> expectedGroups = new ArrayList<>();
        GroupRepresentation group = new GroupRepresentation();
        group.setId("group-id");
        group.setName("group-name");
        expectedGroups.add(group);
        
        when(usersResource.get(TEST_USER_ID)).thenReturn(userResource);
        when(userResource.groups()).thenReturn(expectedGroups);
        
        // Act
        List<GroupRepresentation> actualGroups = userService.getUserGroups(TEST_REALM, TEST_USER_ID);
        
        // Assert
        assertEquals(expectedGroups, actualGroups);
        assertEquals(1, actualGroups.size());
        assertEquals("group-name", actualGroups.get(0).getName());
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).users();
        verify(usersResource).get(TEST_USER_ID);
        verify(userResource).groups();
    }
    
    @Test
    public void testGetUserGroups_UserNotFound_ReturnsEmptyList() {
        // Arrange
        when(usersResource.get(TEST_USER_ID)).thenReturn(userResource);
        when(userResource.groups()).thenThrow(new jakarta.ws.rs.NotFoundException("User not found"));
        
        // Act
        List<GroupRepresentation> actualGroups = userService.getUserGroups(TEST_REALM, TEST_USER_ID);
        
        // Assert
        assertTrue(actualGroups.isEmpty());
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).users();
        verify(usersResource).get(TEST_USER_ID);
        verify(userResource).groups();
    }
    
    @Test
    public void testGetUserGroups_Exception_ReturnsEmptyList() {
        // Arrange
        when(usersResource.get(TEST_USER_ID)).thenReturn(userResource);
        when(userResource.groups()).thenThrow(new RuntimeException("Unexpected error"));
        
        // Act
        List<GroupRepresentation> actualGroups = userService.getUserGroups(TEST_REALM, TEST_USER_ID);
        
        // Assert
        assertTrue(actualGroups.isEmpty());
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).users();
        verify(usersResource).get(TEST_USER_ID);
        verify(userResource).groups();
    }
    
    @Test
    public void testAddUserToGroup_Success_ReturnsSuccessMessage() {
        // Arrange
        String groupId = "group-id";
        when(usersResource.get(TEST_USER_ID)).thenReturn(userResource);
        
        // Act
        String result = userService.addUserToGroup(TEST_REALM, TEST_USER_ID, groupId);
        
        // Assert
        assertEquals("Successfully added user to group: " + TEST_USER_ID + " -> " + groupId, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).users();
        verify(usersResource).get(TEST_USER_ID);
        verify(userResource).joinGroup(groupId);
    }
    
    @Test
    public void testAddUserToGroup_UserOrGroupNotFound_ReturnsErrorMessage() {
        // Arrange
        String groupId = "group-id";
        when(usersResource.get(TEST_USER_ID)).thenReturn(userResource);
        Mockito.doThrow(new jakarta.ws.rs.NotFoundException("User or group not found"))
            .when(userResource).joinGroup(groupId);
        
        // Act
        String result = userService.addUserToGroup(TEST_REALM, TEST_USER_ID, groupId);
        
        // Assert
        assertEquals("User or group not found: " + TEST_USER_ID + " -> " + groupId, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).users();
        verify(usersResource).get(TEST_USER_ID);
        verify(userResource).joinGroup(groupId);
    }
    
    @Test
    public void testAddUserToGroup_Exception_ReturnsErrorMessage() {
        // Arrange
        String groupId = "group-id";
        when(usersResource.get(TEST_USER_ID)).thenReturn(userResource);
        Mockito.doThrow(new RuntimeException("Unexpected error"))
            .when(userResource).joinGroup(groupId);
        
        // Act
        String result = userService.addUserToGroup(TEST_REALM, TEST_USER_ID, groupId);
        
        // Assert
        assertTrue(result.startsWith("Error adding user to group: " + TEST_USER_ID + " -> " + groupId));
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).users();
        verify(usersResource).get(TEST_USER_ID);
        verify(userResource).joinGroup(groupId);
    }
    
    @Test
    public void testRemoveUserFromGroup_Success_ReturnsSuccessMessage() {
        // Arrange
        String groupId = "group-id";
        when(usersResource.get(TEST_USER_ID)).thenReturn(userResource);
        
        // Act
        String result = userService.removeUserFromGroup(TEST_REALM, TEST_USER_ID, groupId);
        
        // Assert
        assertEquals("Successfully removed user from group: " + TEST_USER_ID + " -> " + groupId, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).users();
        verify(usersResource).get(TEST_USER_ID);
        verify(userResource).leaveGroup(groupId);
    }
    
    @Test
    public void testRemoveUserFromGroup_UserOrGroupNotFound_ReturnsErrorMessage() {
        // Arrange
        String groupId = "group-id";
        when(usersResource.get(TEST_USER_ID)).thenReturn(userResource);
        Mockito.doThrow(new jakarta.ws.rs.NotFoundException("User or group not found"))
            .when(userResource).leaveGroup(groupId);
        
        // Act
        String result = userService.removeUserFromGroup(TEST_REALM, TEST_USER_ID, groupId);
        
        // Assert
        assertEquals("User or group not found: " + TEST_USER_ID + " -> " + groupId, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).users();
        verify(usersResource).get(TEST_USER_ID);
        verify(userResource).leaveGroup(groupId);
    }
    
    @Test
    public void testRemoveUserFromGroup_Exception_ReturnsErrorMessage() {
        // Arrange
        String groupId = "group-id";
        when(usersResource.get(TEST_USER_ID)).thenReturn(userResource);
        Mockito.doThrow(new RuntimeException("Unexpected error"))
            .when(userResource).leaveGroup(groupId);
        
        // Act
        String result = userService.removeUserFromGroup(TEST_REALM, TEST_USER_ID, groupId);
        
        // Assert
        assertTrue(result.startsWith("Error removing user from group: " + TEST_USER_ID + " -> " + groupId));
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).users();
        verify(usersResource).get(TEST_USER_ID);
        verify(userResource).leaveGroup(groupId);
    }
    
    @Test
    public void testGetUserRoles_Success_ReturnsRoleList() {
        // Arrange
        List<RoleRepresentation> expectedRoles = new ArrayList<>();
        RoleRepresentation role = new RoleRepresentation();
        role.setId("role-id");
        role.setName("role-name");
        expectedRoles.add(role);
        
        when(usersResource.get(TEST_USER_ID)).thenReturn(userResource);
        when(roleScopeResource.listEffective()).thenReturn(expectedRoles);
        
        // Act
        List<RoleRepresentation> actualRoles = userService.getUserRoles(TEST_REALM, TEST_USER_ID);
        
        // Assert
        assertEquals(expectedRoles, actualRoles);
        assertEquals(1, actualRoles.size());
        assertEquals("role-name", actualRoles.get(0).getName());
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).users();
        verify(usersResource).get(TEST_USER_ID);
        verify(userResource).roles();
        verify(roleMappingResource).realmLevel();
        verify(roleScopeResource).listEffective();
    }
    
    @Test
    public void testGetUserRoles_UserNotFound_ReturnsEmptyList() {
        // Arrange
        when(usersResource.get(TEST_USER_ID)).thenReturn(userResource);
        when(roleScopeResource.listEffective()).thenThrow(new jakarta.ws.rs.NotFoundException("User not found"));
        
        // Act
        List<RoleRepresentation> actualRoles = userService.getUserRoles(TEST_REALM, TEST_USER_ID);
        
        // Assert
        assertTrue(actualRoles.isEmpty());
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).users();
        verify(usersResource).get(TEST_USER_ID);
        verify(userResource).roles();
        verify(roleMappingResource).realmLevel();
        verify(roleScopeResource).listEffective();
    }
    
    @Test
    public void testGetUserRoles_Exception_ReturnsEmptyList() {
        // Arrange
        when(usersResource.get(TEST_USER_ID)).thenReturn(userResource);
        when(roleScopeResource.listEffective()).thenThrow(new RuntimeException("Unexpected error"));
        
        // Act
        List<RoleRepresentation> actualRoles = userService.getUserRoles(TEST_REALM, TEST_USER_ID);
        
        // Assert
        assertTrue(actualRoles.isEmpty());
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).users();
        verify(usersResource).get(TEST_USER_ID);
        verify(userResource).roles();
        verify(roleMappingResource).realmLevel();
        verify(roleScopeResource).listEffective();
    }
    
    @Test
    public void testAddRoleToUser_Success_ReturnsSuccessMessage() {
        // Arrange
        String roleName = "role-name";
        RoleRepresentation role = new RoleRepresentation();
        role.setId("role-id");
        role.setName(roleName);
        
        when(usersResource.get(TEST_USER_ID)).thenReturn(userResource);
        when(realmResource.roles()).thenReturn(Mockito.mock(org.keycloak.admin.client.resource.RolesResource.class));
        when(realmResource.roles().get(roleName)).thenReturn(Mockito.mock(org.keycloak.admin.client.resource.RoleResource.class));
        when(realmResource.roles().get(roleName).toRepresentation()).thenReturn(role);
        
        // Act
        String result = userService.addRoleToUser(TEST_REALM, TEST_USER_ID, roleName);
        
        // Assert
        assertEquals("Successfully added role to user: " + TEST_USER_ID + " -> " + roleName, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).users();
        verify(usersResource).get(TEST_USER_ID);
        verify(realmResource).roles();
        verify(realmResource.roles()).get(roleName);
        verify(realmResource.roles().get(roleName)).toRepresentation();
        verify(userResource).roles();
        verify(roleMappingResource).realmLevel();
        verify(roleScopeResource).add(Mockito.anyList());
    }
    
    @Test
    public void testAddRoleToUser_UserOrRoleNotFound_ReturnsErrorMessage() {
        // Arrange
        String roleName = "role-name";
        
        when(usersResource.get(TEST_USER_ID)).thenReturn(userResource);
        when(realmResource.roles()).thenReturn(Mockito.mock(org.keycloak.admin.client.resource.RolesResource.class));
        when(realmResource.roles().get(roleName)).thenReturn(Mockito.mock(org.keycloak.admin.client.resource.RoleResource.class));
        when(realmResource.roles().get(roleName).toRepresentation()).thenThrow(new jakarta.ws.rs.NotFoundException("Role not found"));
        
        // Act
        String result = userService.addRoleToUser(TEST_REALM, TEST_USER_ID, roleName);
        
        // Assert
        assertEquals("User or role not found: " + TEST_USER_ID + " -> " + roleName, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).users();
        verify(usersResource).get(TEST_USER_ID);
        verify(realmResource).roles();
        verify(realmResource.roles()).get(roleName);
        verify(realmResource.roles().get(roleName)).toRepresentation();
    }
    
    @Test
    public void testAddRoleToUser_Exception_ReturnsErrorMessage() {
        // Arrange
        String roleName = "role-name";
        RoleRepresentation role = new RoleRepresentation();
        role.setId("role-id");
        role.setName(roleName);
        
        when(usersResource.get(TEST_USER_ID)).thenReturn(userResource);
        when(realmResource.roles()).thenReturn(Mockito.mock(org.keycloak.admin.client.resource.RolesResource.class));
        when(realmResource.roles().get(roleName)).thenReturn(Mockito.mock(org.keycloak.admin.client.resource.RoleResource.class));
        when(realmResource.roles().get(roleName).toRepresentation()).thenReturn(role);
        Mockito.doThrow(new RuntimeException("Unexpected error"))
            .when(roleScopeResource).add(Mockito.anyList());
        
        // Act
        String result = userService.addRoleToUser(TEST_REALM, TEST_USER_ID, roleName);
        
        // Assert
        assertTrue(result.startsWith("Error adding role to user: " + TEST_USER_ID + " -> " + roleName));
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).users();
        verify(usersResource).get(TEST_USER_ID);
        verify(realmResource).roles();
        verify(realmResource.roles()).get(roleName);
        verify(realmResource.roles().get(roleName)).toRepresentation();
        verify(userResource).roles();
        verify(roleMappingResource).realmLevel();
        verify(roleScopeResource).add(Mockito.anyList());
    }
    
    @Test
    public void testRemoveRoleFromUser_Success_ReturnsSuccessMessage() {
        // Arrange
        String roleName = "role-name";
        RoleRepresentation role = new RoleRepresentation();
        role.setId("role-id");
        role.setName(roleName);
        
        when(usersResource.get(TEST_USER_ID)).thenReturn(userResource);
        when(realmResource.roles()).thenReturn(Mockito.mock(org.keycloak.admin.client.resource.RolesResource.class));
        when(realmResource.roles().get(roleName)).thenReturn(Mockito.mock(org.keycloak.admin.client.resource.RoleResource.class));
        when(realmResource.roles().get(roleName).toRepresentation()).thenReturn(role);
        
        // Act
        String result = userService.removeRoleFromUser(TEST_REALM, TEST_USER_ID, roleName);
        
        // Assert
        assertEquals("Successfully removed role from user: " + TEST_USER_ID + " -> " + roleName, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).users();
        verify(usersResource).get(TEST_USER_ID);
        verify(realmResource).roles();
        verify(realmResource.roles()).get(roleName);
        verify(realmResource.roles().get(roleName)).toRepresentation();
        verify(userResource).roles();
        verify(roleMappingResource).realmLevel();
        verify(roleScopeResource).remove(Mockito.anyList());
    }
    
    @Test
    public void testRemoveRoleFromUser_UserOrRoleNotFound_ReturnsErrorMessage() {
        // Arrange
        String roleName = "role-name";
        
        when(usersResource.get(TEST_USER_ID)).thenReturn(userResource);
        when(realmResource.roles()).thenReturn(Mockito.mock(org.keycloak.admin.client.resource.RolesResource.class));
        when(realmResource.roles().get(roleName)).thenReturn(Mockito.mock(org.keycloak.admin.client.resource.RoleResource.class));
        when(realmResource.roles().get(roleName).toRepresentation()).thenThrow(new jakarta.ws.rs.NotFoundException("Role not found"));
        
        // Act
        String result = userService.removeRoleFromUser(TEST_REALM, TEST_USER_ID, roleName);
        
        // Assert
        assertEquals("User or role not found: " + TEST_USER_ID + " -> " + roleName, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).users();
        verify(usersResource).get(TEST_USER_ID);
        verify(realmResource).roles();
        verify(realmResource.roles()).get(roleName);
        verify(realmResource.roles().get(roleName)).toRepresentation();
    }
    
    @Test
    public void testRemoveRoleFromUser_Exception_ReturnsErrorMessage() {
        // Arrange
        String roleName = "role-name";
        RoleRepresentation role = new RoleRepresentation();
        role.setId("role-id");
        role.setName(roleName);
        
        when(usersResource.get(TEST_USER_ID)).thenReturn(userResource);
        when(realmResource.roles()).thenReturn(Mockito.mock(org.keycloak.admin.client.resource.RolesResource.class));
        when(realmResource.roles().get(roleName)).thenReturn(Mockito.mock(org.keycloak.admin.client.resource.RoleResource.class));
        when(realmResource.roles().get(roleName).toRepresentation()).thenReturn(role);
        Mockito.doThrow(new RuntimeException("Unexpected error"))
            .when(roleScopeResource).remove(Mockito.anyList());
        
        // Act
        String result = userService.removeRoleFromUser(TEST_REALM, TEST_USER_ID, roleName);
        
        // Assert
        assertTrue(result.startsWith("Error removing role from user: " + TEST_USER_ID + " -> " + roleName));
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).users();
        verify(usersResource).get(TEST_USER_ID);
        verify(realmResource).roles();
        verify(realmResource.roles()).get(roleName);
        verify(realmResource.roles().get(roleName)).toRepresentation();
        verify(userResource).roles();
        verify(roleMappingResource).realmLevel();
        verify(roleScopeResource).remove(Mockito.anyList());
    }
    
    @Test
    public void testResetPassword_Success_ReturnsSuccessMessage() {
        // Arrange
        String newPassword = "new-password";
        boolean temporary = false;
        
        when(usersResource.get(TEST_USER_ID)).thenReturn(userResource);
        
        // Act
        String result = userService.resetPassword(TEST_REALM, TEST_USER_ID, newPassword, temporary);
        
        // Assert
        assertEquals("Successfully reset password for user: " + TEST_USER_ID, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).users();
        verify(usersResource).get(TEST_USER_ID);
        verify(userResource).resetPassword(Mockito.any(CredentialRepresentation.class));
    }
    
    @Test
    public void testResetPassword_UserNotFound_ReturnsErrorMessage() {
        // Arrange
        String newPassword = "new-password";
        boolean temporary = false;
        
        when(usersResource.get(TEST_USER_ID)).thenReturn(userResource);
        Mockito.doThrow(new jakarta.ws.rs.NotFoundException("User not found"))
            .when(userResource).resetPassword(Mockito.any(CredentialRepresentation.class));
        
        // Act
        String result = userService.resetPassword(TEST_REALM, TEST_USER_ID, newPassword, temporary);
        
        // Assert
        assertEquals("User not found: " + TEST_USER_ID, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).users();
        verify(usersResource).get(TEST_USER_ID);
        verify(userResource).resetPassword(Mockito.any(CredentialRepresentation.class));
    }
    
    @Test
    public void testResetPassword_Exception_ReturnsErrorMessage() {
        // Arrange
        String newPassword = "new-password";
        boolean temporary = false;
        
        when(usersResource.get(TEST_USER_ID)).thenReturn(userResource);
        Mockito.doThrow(new RuntimeException("Unexpected error"))
            .when(userResource).resetPassword(Mockito.any(CredentialRepresentation.class));
        
        // Act
        String result = userService.resetPassword(TEST_REALM, TEST_USER_ID, newPassword, temporary);
        
        // Assert
        assertTrue(result.startsWith("Error resetting password for user: " + TEST_USER_ID));
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).users();
        verify(usersResource).get(TEST_USER_ID);
        verify(userResource).resetPassword(Mockito.any(CredentialRepresentation.class));
    }
    
    @Test
    public void testSendVerificationEmail_Success_ReturnsSuccessMessage() {
        // Arrange
        when(usersResource.get(TEST_USER_ID)).thenReturn(userResource);
        
        // Act
        String result = userService.sendVerificationEmail(TEST_REALM, TEST_USER_ID);
        
        // Assert
        assertEquals("Successfully sent verification email to user: " + TEST_USER_ID, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).users();
        verify(usersResource).get(TEST_USER_ID);
        verify(userResource).sendVerifyEmail();
    }
    
    @Test
    public void testSendVerificationEmail_UserNotFound_ReturnsErrorMessage() {
        // Arrange
        when(usersResource.get(TEST_USER_ID)).thenReturn(userResource);
        Mockito.doThrow(new jakarta.ws.rs.NotFoundException("User not found"))
            .when(userResource).sendVerifyEmail();
        
        // Act
        String result = userService.sendVerificationEmail(TEST_REALM, TEST_USER_ID);
        
        // Assert
        assertEquals("User not found: " + TEST_USER_ID, result);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).users();
        verify(usersResource).get(TEST_USER_ID);
        verify(userResource).sendVerifyEmail();
    }
    
    @Test
    public void testSendVerificationEmail_Exception_ReturnsErrorMessage() {
        // Arrange
        when(usersResource.get(TEST_USER_ID)).thenReturn(userResource);
        Mockito.doThrow(new RuntimeException("Unexpected error"))
            .when(userResource).sendVerifyEmail();
        
        // Act
        String result = userService.sendVerificationEmail(TEST_REALM, TEST_USER_ID);
        
        // Assert
        assertTrue(result.startsWith("Error sending verification email to user: " + TEST_USER_ID));
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).users();
        verify(usersResource).get(TEST_USER_ID);
        verify(userResource).sendVerifyEmail();
    }
    
    @Test
    public void testCountUsers_Success_ReturnsCount() {
        // Arrange
        int expectedCount = 5;
        when(usersResource.count()).thenReturn(expectedCount);
        
        // Act
        int actualCount = userService.countUsers(TEST_REALM);
        
        // Assert
        assertEquals(expectedCount, actualCount);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).users();
        verify(usersResource).count();
    }
    
    @Test
    public void testCountUsers_Exception_ReturnsNegativeOne() {
        // Arrange
        when(usersResource.count()).thenThrow(new RuntimeException("Unexpected error"));
        
        // Act
        int actualCount = userService.countUsers(TEST_REALM);
        
        // Assert
        assertEquals(-1, actualCount);
        verify(keycloak).realm(TEST_REALM);
        verify(realmResource).users();
        verify(usersResource).count();
    }
}