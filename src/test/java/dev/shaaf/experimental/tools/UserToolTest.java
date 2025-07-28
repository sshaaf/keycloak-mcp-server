package dev.shaaf.experimental.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.shaaf.experimental.service.UserService;
import io.quarkiverse.mcp.server.ToolCallException;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
public class UserToolTest {

    @InjectMock
    UserService userService;

    @InjectMock
    ObjectMapper mapper;

    @Inject
    UserTool userTool;

    private static final String TEST_REALM = "test-realm";
    private static final String TEST_USERNAME = "test-user";
    private static final String TEST_USER_ID = "test-user-id";
    private static final String TEST_FIRST_NAME = "Test";
    private static final String TEST_LAST_NAME = "User";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "password";
    private static final String EXPECTED_JSON = "{\"users\":[{\"username\":\"test-user\"}]}";
    private static final String SUCCESS_MESSAGE = "User created successfully";
    private static final String DELETE_SUCCESS_MESSAGE = "User deleted successfully";

    @Test
    public void testGetUsers_Success_ReturnsJsonString() throws Exception {
        // Arrange
        List<UserRepresentation> users = new ArrayList<>();
        UserRepresentation user = new UserRepresentation();
        user.setUsername(TEST_USERNAME);
        users.add(user);

        when(userService.getUsers(TEST_REALM)).thenReturn(users);
        when(mapper.writeValueAsString(users)).thenReturn(EXPECTED_JSON);

        // Act
        String result = userTool.getUsers(TEST_REALM);

        // Assert
        assertEquals(EXPECTED_JSON, result);
        verify(userService).getUsers(TEST_REALM);
        verify(mapper).writeValueAsString(users);
    }

    @Test
    public void testGetUsers_MappingException_ThrowsToolCallException() throws Exception {
        // Arrange
        List<UserRepresentation> users = new ArrayList<>();
        when(userService.getUsers(TEST_REALM)).thenReturn(users);
        when(mapper.writeValueAsString(users)).thenThrow(new RuntimeException("Mapping error"));

        // Act & Assert
        assertThrows(ToolCallException.class, () -> userTool.getUsers(TEST_REALM));
        verify(userService).getUsers(TEST_REALM);
        verify(mapper).writeValueAsString(users);
    }

    @Test
    public void testAddUser_Success_ReturnsSuccessMessage() {
        // Arrange
        when(userService.addUser(
                eq(TEST_REALM),
                eq(TEST_USERNAME),
                eq(TEST_FIRST_NAME),
                eq(TEST_LAST_NAME),
                eq(TEST_EMAIL),
                eq(TEST_PASSWORD)
        )).thenReturn(SUCCESS_MESSAGE);

        // Act
        String result = userTool.addUser(
                TEST_REALM,
                TEST_USERNAME,
                TEST_FIRST_NAME,
                TEST_LAST_NAME,
                TEST_EMAIL,
                TEST_PASSWORD
        );

        // Assert
        assertEquals(SUCCESS_MESSAGE, result);
        verify(userService).addUser(
                TEST_REALM,
                TEST_USERNAME,
                TEST_FIRST_NAME,
                TEST_LAST_NAME,
                TEST_EMAIL,
                TEST_PASSWORD
        );
    }

    @Test
    public void testDeleteUser_Success_ReturnsSuccessMessage() {
        // Arrange
        when(userService.deleteUser(TEST_REALM, TEST_USERNAME)).thenReturn(DELETE_SUCCESS_MESSAGE);

        // Act
        String result = userTool.deleteUser(TEST_REALM, TEST_USERNAME);

        // Assert
        assertEquals(DELETE_SUCCESS_MESSAGE, result);
        verify(userService).deleteUser(TEST_REALM, TEST_USERNAME);
    }
    
    @Test
    public void testGetUserByUsername_Success_ReturnsJsonString() throws Exception {
        // Arrange
        UserRepresentation user = new UserRepresentation();
        user.setUsername(TEST_USERNAME);
        user.setId("user-id");
        
        when(userService.getUserByUsername(TEST_REALM, TEST_USERNAME)).thenReturn(user);
        when(mapper.writeValueAsString(user)).thenReturn("{\"username\":\"test-user\",\"id\":\"user-id\"}");
        
        // Act
        String result = userTool.getUserByUsername(TEST_REALM, TEST_USERNAME);
        
        // Assert
        assertEquals("{\"username\":\"test-user\",\"id\":\"user-id\"}", result);
        verify(userService).getUserByUsername(TEST_REALM, TEST_USERNAME);
        verify(mapper).writeValueAsString(user);
    }
    
    @Test
    public void testGetUserByUsername_UserNotFound_ReturnsEmptyJson() throws Exception {
        // Arrange
        when(userService.getUserByUsername(TEST_REALM, TEST_USERNAME)).thenReturn(null);
        when(mapper.writeValueAsString(null)).thenReturn("null");
        
        // Act
        String result = userTool.getUserByUsername(TEST_REALM, TEST_USERNAME);
        
        // Assert
        assertEquals("null", result);
        verify(userService).getUserByUsername(TEST_REALM, TEST_USERNAME);
        verify(mapper).writeValueAsString(null);
    }
    
    @Test
    public void testGetUserByUsername_MappingException_ThrowsToolCallException() throws Exception {
        // Arrange
        UserRepresentation user = new UserRepresentation();
        user.setUsername(TEST_USERNAME);
        
        when(userService.getUserByUsername(TEST_REALM, TEST_USERNAME)).thenReturn(user);
        when(mapper.writeValueAsString(user)).thenThrow(new RuntimeException("Mapping error"));
        
        // Act & Assert
        assertThrows(ToolCallException.class, () -> userTool.getUserByUsername(TEST_REALM, TEST_USERNAME));
        verify(userService).getUserByUsername(TEST_REALM, TEST_USERNAME);
        verify(mapper).writeValueAsString(user);
    }
    
    @Test
    public void testGetUserById_Success_ReturnsJsonString() throws Exception {
        // Arrange
        UserRepresentation user = new UserRepresentation();
        user.setId(TEST_USER_ID);
        user.setUsername(TEST_USERNAME);
        
        when(userService.getUserById(TEST_REALM, TEST_USER_ID)).thenReturn(user);
        when(mapper.writeValueAsString(user)).thenReturn("{\"id\":\"test-user-id\",\"username\":\"test-user\"}");
        
        // Act
        String result = userTool.getUserById(TEST_REALM, TEST_USER_ID);
        
        // Assert
        assertEquals("{\"id\":\"test-user-id\",\"username\":\"test-user\"}", result);
        verify(userService).getUserById(TEST_REALM, TEST_USER_ID);
        verify(mapper).writeValueAsString(user);
    }
    
    @Test
    public void testGetUserById_UserNotFound_ReturnsNullJson() throws Exception {
        // Arrange
        when(userService.getUserById(TEST_REALM, TEST_USER_ID)).thenReturn(null);
        when(mapper.writeValueAsString(null)).thenReturn("null");
        
        // Act
        String result = userTool.getUserById(TEST_REALM, TEST_USER_ID);
        
        // Assert
        assertEquals("null", result);
        verify(userService).getUserById(TEST_REALM, TEST_USER_ID);
        verify(mapper).writeValueAsString(null);
    }
    
    @Test
    public void testGetUserById_MappingException_ThrowsToolCallException() throws Exception {
        // Arrange
        UserRepresentation user = new UserRepresentation();
        user.setId(TEST_USER_ID);
        user.setUsername(TEST_USERNAME);
        
        when(userService.getUserById(TEST_REALM, TEST_USER_ID)).thenReturn(user);
        when(mapper.writeValueAsString(user)).thenThrow(new RuntimeException("Mapping error"));
        
        // Act & Assert
        assertThrows(ToolCallException.class, () -> userTool.getUserById(TEST_REALM, TEST_USER_ID));
        verify(userService).getUserById(TEST_REALM, TEST_USER_ID);
        verify(mapper).writeValueAsString(user);
    }
    
    @Test
    public void testUpdateUser_Success_ReturnsSuccessMessage() {
        // Arrange
        UserRepresentation user = new UserRepresentation();
        user.setId(TEST_USER_ID);
        user.setUsername(TEST_USERNAME);
        
        String newUsername = "new-username";
        String newFirstName = "New";
        String newLastName = "User";
        String newEmail = "new@example.com";
        Boolean enabled = true;
        
        String successMessage = "Successfully updated user: " + TEST_USER_ID;
        
        when(userService.getUserById(TEST_REALM, TEST_USER_ID)).thenReturn(user);
        when(userService.updateUser(eq(TEST_REALM), eq(TEST_USER_ID), any(UserRepresentation.class)))
            .thenReturn(successMessage);
        
        // Act
        String result = userTool.updateUser(TEST_REALM, TEST_USER_ID, newUsername, newFirstName, 
                                           newLastName, newEmail, enabled);
        
        // Assert
        assertEquals(successMessage, result);
        verify(userService).getUserById(TEST_REALM, TEST_USER_ID);
        verify(userService).updateUser(eq(TEST_REALM), eq(TEST_USER_ID), any(UserRepresentation.class));
    }
    
    @Test
    public void testUpdateUser_UserNotFound_ReturnsErrorMessage() {
        // Arrange
        when(userService.getUserById(TEST_REALM, TEST_USER_ID)).thenReturn(null);
        
        // Act
        String result = userTool.updateUser(TEST_REALM, TEST_USER_ID, "new-username", "New", 
                                           "User", "new@example.com", true);
        
        // Assert
        assertEquals("User not found: " + TEST_USER_ID, result);
        verify(userService).getUserById(TEST_REALM, TEST_USER_ID);
        verify(userService, never()).updateUser(anyString(), anyString(), any(UserRepresentation.class));
    }
    
    @Test
    public void testUpdateUser_ServiceException_ThrowsToolCallException() {
        // Arrange
        UserRepresentation user = new UserRepresentation();
        user.setId(TEST_USER_ID);
        user.setUsername(TEST_USERNAME);
        
        when(userService.getUserById(TEST_REALM, TEST_USER_ID)).thenReturn(user);
        when(userService.updateUser(eq(TEST_REALM), eq(TEST_USER_ID), any(UserRepresentation.class)))
            .thenThrow(new RuntimeException("Service error"));
        
        // Act & Assert
        assertThrows(ToolCallException.class, () -> 
            userTool.updateUser(TEST_REALM, TEST_USER_ID, "new-username", "New", "User", "new@example.com", true));
        
        verify(userService).getUserById(TEST_REALM, TEST_USER_ID);
        verify(userService).updateUser(eq(TEST_REALM), eq(TEST_USER_ID), any(UserRepresentation.class));
    }
    
    @Test
    public void testGetUserGroups_Success_ReturnsJsonString() throws Exception {
        // Arrange
        List<GroupRepresentation> groups = new ArrayList<>();
        GroupRepresentation group = new GroupRepresentation();
        group.setId("group-id");
        group.setName("group-name");
        groups.add(group);
        
        when(userService.getUserGroups(TEST_REALM, TEST_USER_ID)).thenReturn(groups);
        when(mapper.writeValueAsString(groups)).thenReturn("[{\"id\":\"group-id\",\"name\":\"group-name\"}]");
        
        // Act
        String result = userTool.getUserGroups(TEST_REALM, TEST_USER_ID);
        
        // Assert
        assertEquals("[{\"id\":\"group-id\",\"name\":\"group-name\"}]", result);
        verify(userService).getUserGroups(TEST_REALM, TEST_USER_ID);
        verify(mapper).writeValueAsString(groups);
    }
    
    @Test
    public void testGetUserGroups_EmptyList_ReturnsEmptyJsonArray() throws Exception {
        // Arrange
        List<GroupRepresentation> groups = new ArrayList<>();
        
        when(userService.getUserGroups(TEST_REALM, TEST_USER_ID)).thenReturn(groups);
        when(mapper.writeValueAsString(groups)).thenReturn("[]");
        
        // Act
        String result = userTool.getUserGroups(TEST_REALM, TEST_USER_ID);
        
        // Assert
        assertEquals("[]", result);
        verify(userService).getUserGroups(TEST_REALM, TEST_USER_ID);
        verify(mapper).writeValueAsString(groups);
    }
    
    @Test
    public void testGetUserGroups_MappingException_ThrowsToolCallException() throws Exception {
        // Arrange
        List<GroupRepresentation> groups = new ArrayList<>();
        GroupRepresentation group = new GroupRepresentation();
        group.setId("group-id");
        group.setName("group-name");
        groups.add(group);
        
        when(userService.getUserGroups(TEST_REALM, TEST_USER_ID)).thenReturn(groups);
        when(mapper.writeValueAsString(groups)).thenThrow(new RuntimeException("Mapping error"));
        
        // Act & Assert
        assertThrows(ToolCallException.class, () -> userTool.getUserGroups(TEST_REALM, TEST_USER_ID));
        verify(userService).getUserGroups(TEST_REALM, TEST_USER_ID);
        verify(mapper).writeValueAsString(groups);
    }
    
    @Test
    public void testAddUserToGroup_Success_ReturnsSuccessMessage() {
        // Arrange
        String groupId = "group-id";
        String successMessage = "Successfully added user to group: " + TEST_USER_ID + " -> " + groupId;
        
        when(userService.addUserToGroup(TEST_REALM, TEST_USER_ID, groupId)).thenReturn(successMessage);
        
        // Act
        String result = userTool.addUserToGroup(TEST_REALM, TEST_USER_ID, groupId);
        
        // Assert
        assertEquals(successMessage, result);
        verify(userService).addUserToGroup(TEST_REALM, TEST_USER_ID, groupId);
    }
    
    @Test
    public void testAddUserToGroup_Failure_ReturnsErrorMessage() {
        // Arrange
        String groupId = "group-id";
        String errorMessage = "User or group not found: " + TEST_USER_ID + " -> " + groupId;
        
        when(userService.addUserToGroup(TEST_REALM, TEST_USER_ID, groupId)).thenReturn(errorMessage);
        
        // Act
        String result = userTool.addUserToGroup(TEST_REALM, TEST_USER_ID, groupId);
        
        // Assert
        assertEquals(errorMessage, result);
        verify(userService).addUserToGroup(TEST_REALM, TEST_USER_ID, groupId);
    }
    
    @Test
    public void testRemoveUserFromGroup_Success_ReturnsSuccessMessage() {
        // Arrange
        String groupId = "group-id";
        String successMessage = "Successfully removed user from group: " + TEST_USER_ID + " -> " + groupId;
        
        when(userService.removeUserFromGroup(TEST_REALM, TEST_USER_ID, groupId)).thenReturn(successMessage);
        
        // Act
        String result = userTool.removeUserFromGroup(TEST_REALM, TEST_USER_ID, groupId);
        
        // Assert
        assertEquals(successMessage, result);
        verify(userService).removeUserFromGroup(TEST_REALM, TEST_USER_ID, groupId);
    }
    
    @Test
    public void testRemoveUserFromGroup_Failure_ReturnsErrorMessage() {
        // Arrange
        String groupId = "group-id";
        String errorMessage = "User or group not found: " + TEST_USER_ID + " -> " + groupId;
        
        when(userService.removeUserFromGroup(TEST_REALM, TEST_USER_ID, groupId)).thenReturn(errorMessage);
        
        // Act
        String result = userTool.removeUserFromGroup(TEST_REALM, TEST_USER_ID, groupId);
        
        // Assert
        assertEquals(errorMessage, result);
        verify(userService).removeUserFromGroup(TEST_REALM, TEST_USER_ID, groupId);
    }
    
    @Test
    public void testGetUserRoles_Success_ReturnsJsonString() throws Exception {
        // Arrange
        List<RoleRepresentation> roles = new ArrayList<>();
        RoleRepresentation role = new RoleRepresentation();
        role.setId("role-id");
        role.setName("role-name");
        roles.add(role);
        
        when(userService.getUserRoles(TEST_REALM, TEST_USER_ID)).thenReturn(roles);
        when(mapper.writeValueAsString(roles)).thenReturn("[{\"id\":\"role-id\",\"name\":\"role-name\"}]");
        
        // Act
        String result = userTool.getUserRoles(TEST_REALM, TEST_USER_ID);
        
        // Assert
        assertEquals("[{\"id\":\"role-id\",\"name\":\"role-name\"}]", result);
        verify(userService).getUserRoles(TEST_REALM, TEST_USER_ID);
        verify(mapper).writeValueAsString(roles);
    }
    
    @Test
    public void testGetUserRoles_EmptyList_ReturnsEmptyJsonArray() throws Exception {
        // Arrange
        List<RoleRepresentation> roles = new ArrayList<>();
        
        when(userService.getUserRoles(TEST_REALM, TEST_USER_ID)).thenReturn(roles);
        when(mapper.writeValueAsString(roles)).thenReturn("[]");
        
        // Act
        String result = userTool.getUserRoles(TEST_REALM, TEST_USER_ID);
        
        // Assert
        assertEquals("[]", result);
        verify(userService).getUserRoles(TEST_REALM, TEST_USER_ID);
        verify(mapper).writeValueAsString(roles);
    }
    
    @Test
    public void testGetUserRoles_MappingException_ThrowsToolCallException() throws Exception {
        // Arrange
        List<RoleRepresentation> roles = new ArrayList<>();
        RoleRepresentation role = new RoleRepresentation();
        role.setId("role-id");
        role.setName("role-name");
        roles.add(role);
        
        when(userService.getUserRoles(TEST_REALM, TEST_USER_ID)).thenReturn(roles);
        when(mapper.writeValueAsString(roles)).thenThrow(new RuntimeException("Mapping error"));
        
        // Act & Assert
        assertThrows(ToolCallException.class, () -> userTool.getUserRoles(TEST_REALM, TEST_USER_ID));
        verify(userService).getUserRoles(TEST_REALM, TEST_USER_ID);
        verify(mapper).writeValueAsString(roles);
    }
    
    @Test
    public void testAddRoleToUser_Success_ReturnsSuccessMessage() {
        // Arrange
        String roleName = "role-name";
        String successMessage = "Successfully added role to user: " + TEST_USER_ID + " -> " + roleName;
        
        when(userService.addRoleToUser(TEST_REALM, TEST_USER_ID, roleName)).thenReturn(successMessage);
        
        // Act
        String result = userTool.addRoleToUser(TEST_REALM, TEST_USER_ID, roleName);
        
        // Assert
        assertEquals(successMessage, result);
        verify(userService).addRoleToUser(TEST_REALM, TEST_USER_ID, roleName);
    }
    
    @Test
    public void testAddRoleToUser_Failure_ReturnsErrorMessage() {
        // Arrange
        String roleName = "role-name";
        String errorMessage = "User or role not found: " + TEST_USER_ID + " -> " + roleName;
        
        when(userService.addRoleToUser(TEST_REALM, TEST_USER_ID, roleName)).thenReturn(errorMessage);
        
        // Act
        String result = userTool.addRoleToUser(TEST_REALM, TEST_USER_ID, roleName);
        
        // Assert
        assertEquals(errorMessage, result);
        verify(userService).addRoleToUser(TEST_REALM, TEST_USER_ID, roleName);
    }
    
    @Test
    public void testRemoveRoleFromUser_Success_ReturnsSuccessMessage() {
        // Arrange
        String roleName = "role-name";
        String successMessage = "Successfully removed role from user: " + TEST_USER_ID + " -> " + roleName;
        
        when(userService.removeRoleFromUser(TEST_REALM, TEST_USER_ID, roleName)).thenReturn(successMessage);
        
        // Act
        String result = userTool.removeRoleFromUser(TEST_REALM, TEST_USER_ID, roleName);
        
        // Assert
        assertEquals(successMessage, result);
        verify(userService).removeRoleFromUser(TEST_REALM, TEST_USER_ID, roleName);
    }
    
    @Test
    public void testRemoveRoleFromUser_Failure_ReturnsErrorMessage() {
        // Arrange
        String roleName = "role-name";
        String errorMessage = "User or role not found: " + TEST_USER_ID + " -> " + roleName;
        
        when(userService.removeRoleFromUser(TEST_REALM, TEST_USER_ID, roleName)).thenReturn(errorMessage);
        
        // Act
        String result = userTool.removeRoleFromUser(TEST_REALM, TEST_USER_ID, roleName);
        
        // Assert
        assertEquals(errorMessage, result);
        verify(userService).removeRoleFromUser(TEST_REALM, TEST_USER_ID, roleName);
    }
    
    @Test
    public void testResetPassword_Success_ReturnsSuccessMessage() {
        // Arrange
        String newPassword = "new-password";
        boolean temporary = false;
        String successMessage = "Successfully reset password for user: " + TEST_USER_ID;
        
        when(userService.resetPassword(TEST_REALM, TEST_USER_ID, newPassword, temporary)).thenReturn(successMessage);
        
        // Act
        String result = userTool.resetPassword(TEST_REALM, TEST_USER_ID, newPassword, temporary);
        
        // Assert
        assertEquals(successMessage, result);
        verify(userService).resetPassword(TEST_REALM, TEST_USER_ID, newPassword, temporary);
    }
    
    @Test
    public void testResetPassword_Failure_ReturnsErrorMessage() {
        // Arrange
        String newPassword = "new-password";
        boolean temporary = false;
        String errorMessage = "User not found: " + TEST_USER_ID;
        
        when(userService.resetPassword(TEST_REALM, TEST_USER_ID, newPassword, temporary)).thenReturn(errorMessage);
        
        // Act
        String result = userTool.resetPassword(TEST_REALM, TEST_USER_ID, newPassword, temporary);
        
        // Assert
        assertEquals(errorMessage, result);
        verify(userService).resetPassword(TEST_REALM, TEST_USER_ID, newPassword, temporary);
    }
    
    @Test
    public void testSendVerificationEmail_Success_ReturnsSuccessMessage() {
        // Arrange
        String successMessage = "Successfully sent verification email to user: " + TEST_USER_ID;
        
        when(userService.sendVerificationEmail(TEST_REALM, TEST_USER_ID)).thenReturn(successMessage);
        
        // Act
        String result = userTool.sendVerificationEmail(TEST_REALM, TEST_USER_ID);
        
        // Assert
        assertEquals(successMessage, result);
        verify(userService).sendVerificationEmail(TEST_REALM, TEST_USER_ID);
    }
    
    @Test
    public void testSendVerificationEmail_Failure_ReturnsErrorMessage() {
        // Arrange
        String errorMessage = "User not found: " + TEST_USER_ID;
        
        when(userService.sendVerificationEmail(TEST_REALM, TEST_USER_ID)).thenReturn(errorMessage);
        
        // Act
        String result = userTool.sendVerificationEmail(TEST_REALM, TEST_USER_ID);
        
        // Assert
        assertEquals(errorMessage, result);
        verify(userService).sendVerificationEmail(TEST_REALM, TEST_USER_ID);
    }
    
    @Test
    public void testCountUsers_Success_ReturnsCount() {
        // Arrange
        int count = 5;
        
        when(userService.countUsers(TEST_REALM)).thenReturn(count);
        
        // Act
        String result = userTool.countUsers(TEST_REALM);
        
        // Assert
        assertEquals("5", result);
        verify(userService).countUsers(TEST_REALM);
    }
    
    @Test
    public void testCountUsers_Failure_ReturnsErrorMessage() {
        // Arrange
        int count = -1; // Error indicator
        
        when(userService.countUsers(TEST_REALM)).thenReturn(count);
        
        // Act
        String result = userTool.countUsers(TEST_REALM);
        
        // Assert
        assertEquals("Failed to count users in realm: " + TEST_REALM, result);
        verify(userService).countUsers(TEST_REALM);
    }
}