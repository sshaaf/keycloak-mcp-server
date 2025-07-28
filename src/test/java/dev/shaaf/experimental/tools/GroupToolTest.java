package dev.shaaf.experimental.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.shaaf.experimental.service.GroupService;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
public class GroupToolTest {

    @InjectMock
    GroupService groupService;

    @InjectMock
    ObjectMapper mapper;

    @Inject
    GroupTool groupTool;

    private static final String TEST_REALM = "test-realm";
    private static final String TEST_GROUP_ID = "test-group-id";
    private static final String TEST_GROUP_NAME = "test-group";
    private static final String TEST_PARENT_GROUP_ID = "parent-group-id";
    private static final String TEST_SUBGROUP_NAME = "test-subgroup";
    private static final String TEST_ROLE_NAME = "test-role";
    private static final String EXPECTED_JSON = "{\"groups\":[{\"id\":\"test-group-id\",\"name\":\"test-group\"}]}";
    private static final String SUCCESS_MESSAGE = "Group created successfully";
    private static final String DELETE_SUCCESS_MESSAGE = "Group deleted successfully";

    @Test
    public void testGetGroups_Success_ReturnsJsonString() throws Exception {
        // Arrange
        List<GroupRepresentation> groups = new ArrayList<>();
        GroupRepresentation group = new GroupRepresentation();
        group.setId(TEST_GROUP_ID);
        group.setName(TEST_GROUP_NAME);
        groups.add(group);

        when(groupService.getGroups(TEST_REALM)).thenReturn(groups);
        when(mapper.writeValueAsString(groups)).thenReturn(EXPECTED_JSON);

        // Act
        String result = groupTool.getGroups(TEST_REALM);

        // Assert
        assertEquals(EXPECTED_JSON, result);
        verify(groupService).getGroups(TEST_REALM);
        verify(mapper).writeValueAsString(groups);
    }

    @Test
    public void testGetGroups_MappingException_ThrowsToolCallException() throws Exception {
        // Arrange
        List<GroupRepresentation> groups = new ArrayList<>();
        when(groupService.getGroups(TEST_REALM)).thenReturn(groups);
        when(mapper.writeValueAsString(groups)).thenThrow(new RuntimeException("Mapping error"));

        // Act & Assert
        assertThrows(ToolCallException.class, () -> groupTool.getGroups(TEST_REALM));
        verify(groupService).getGroups(TEST_REALM);
        verify(mapper).writeValueAsString(groups);
    }

    @Test
    public void testGetGroup_Success_ReturnsJsonString() throws Exception {
        // Arrange
        GroupRepresentation group = new GroupRepresentation();
        group.setId(TEST_GROUP_ID);
        group.setName(TEST_GROUP_NAME);

        when(groupService.getGroup(TEST_REALM, TEST_GROUP_ID)).thenReturn(group);
        when(mapper.writeValueAsString(group)).thenReturn("{\"id\":\"test-group-id\",\"name\":\"test-group\"}");

        // Act
        String result = groupTool.getGroup(TEST_REALM, TEST_GROUP_ID);

        // Assert
        assertEquals("{\"id\":\"test-group-id\",\"name\":\"test-group\"}", result);
        verify(groupService).getGroup(TEST_REALM, TEST_GROUP_ID);
        verify(mapper).writeValueAsString(group);
    }

    @Test
    public void testGetGroup_MappingException_ThrowsToolCallException() throws Exception {
        // Arrange
        GroupRepresentation group = new GroupRepresentation();
        group.setId(TEST_GROUP_ID);
        group.setName(TEST_GROUP_NAME);

        when(groupService.getGroup(TEST_REALM, TEST_GROUP_ID)).thenReturn(group);
        when(mapper.writeValueAsString(group)).thenThrow(new RuntimeException("Mapping error"));

        // Act & Assert
        assertThrows(ToolCallException.class, () -> groupTool.getGroup(TEST_REALM, TEST_GROUP_ID));
        verify(groupService).getGroup(TEST_REALM, TEST_GROUP_ID);
        verify(mapper).writeValueAsString(group);
    }

    @Test
    public void testCreateGroup_Success_ReturnsSuccessMessage() {
        // Arrange
        when(groupService.createGroup(TEST_REALM, TEST_GROUP_NAME)).thenReturn(SUCCESS_MESSAGE);

        // Act
        String result = groupTool.createGroup(TEST_REALM, TEST_GROUP_NAME);

        // Assert
        assertEquals(SUCCESS_MESSAGE, result);
        verify(groupService).createGroup(TEST_REALM, TEST_GROUP_NAME);
    }

    @Test
    public void testUpdateGroup_Success_ReturnsSuccessMessage() throws Exception {
        // Arrange
        String groupJson = "{\"id\":\"test-group-id\",\"name\":\"updated-group\"}";
        GroupRepresentation groupRepresentation = new GroupRepresentation();
        groupRepresentation.setId(TEST_GROUP_ID);
        groupRepresentation.setName("updated-group");

        when(mapper.readValue(groupJson, GroupRepresentation.class)).thenReturn(groupRepresentation);
        when(groupService.updateGroup(eq(TEST_REALM), eq(TEST_GROUP_ID), any(GroupRepresentation.class)))
                .thenReturn("Successfully updated group: " + TEST_GROUP_ID);

        // Act
        String result = groupTool.updateGroup(TEST_REALM, TEST_GROUP_ID, groupJson);

        // Assert
        assertEquals("Successfully updated group: " + TEST_GROUP_ID, result);
        verify(mapper).readValue(groupJson, GroupRepresentation.class);
        verify(groupService).updateGroup(eq(TEST_REALM), eq(TEST_GROUP_ID), any(GroupRepresentation.class));
    }

    @Test
    public void testUpdateGroup_MappingException_ThrowsToolCallException() throws Exception {
        // Arrange
        String groupJson = "{\"id\":\"test-group-id\",\"name\":\"updated-group\"}";
        when(mapper.readValue(anyString(), eq(GroupRepresentation.class)))
                .thenThrow(new RuntimeException("Mapping error"));

        // Act & Assert
        assertThrows(ToolCallException.class, () -> groupTool.updateGroup(TEST_REALM, TEST_GROUP_ID, groupJson));
        verify(mapper).readValue(groupJson, GroupRepresentation.class);
    }

    @Test
    public void testDeleteGroup_Success_ReturnsSuccessMessage() {
        // Arrange
        when(groupService.deleteGroup(TEST_REALM, TEST_GROUP_ID)).thenReturn(DELETE_SUCCESS_MESSAGE);

        // Act
        String result = groupTool.deleteGroup(TEST_REALM, TEST_GROUP_ID);

        // Assert
        assertEquals(DELETE_SUCCESS_MESSAGE, result);
        verify(groupService).deleteGroup(TEST_REALM, TEST_GROUP_ID);
    }

    @Test
    public void testGetGroupMembers_Success_ReturnsJsonString() throws Exception {
        // Arrange
        List<UserRepresentation> members = new ArrayList<>();
        UserRepresentation user = new UserRepresentation();
        user.setId("user-id");
        user.setUsername("username");
        members.add(user);

        when(groupService.getGroupMembers(TEST_REALM, TEST_GROUP_ID)).thenReturn(members);
        when(mapper.writeValueAsString(members)).thenReturn("[{\"id\":\"user-id\",\"username\":\"username\"}]");

        // Act
        String result = groupTool.getGroupMembers(TEST_REALM, TEST_GROUP_ID);

        // Assert
        assertEquals("[{\"id\":\"user-id\",\"username\":\"username\"}]", result);
        verify(groupService).getGroupMembers(TEST_REALM, TEST_GROUP_ID);
        verify(mapper).writeValueAsString(members);
    }

    @Test
    public void testGetGroupMembers_MappingException_ThrowsToolCallException() throws Exception {
        // Arrange
        List<UserRepresentation> members = new ArrayList<>();
        when(groupService.getGroupMembers(TEST_REALM, TEST_GROUP_ID)).thenReturn(members);
        when(mapper.writeValueAsString(members)).thenThrow(new RuntimeException("Mapping error"));

        // Act & Assert
        assertThrows(ToolCallException.class, () -> groupTool.getGroupMembers(TEST_REALM, TEST_GROUP_ID));
        verify(groupService).getGroupMembers(TEST_REALM, TEST_GROUP_ID);
        verify(mapper).writeValueAsString(members);
    }

    @Test
    public void testGetGroupRoles_Success_ReturnsJsonString() throws Exception {
        // Arrange
        List<RoleRepresentation> roles = new ArrayList<>();
        RoleRepresentation role = new RoleRepresentation();
        role.setId("role-id");
        role.setName(TEST_ROLE_NAME);
        roles.add(role);

        when(groupService.getGroupRoles(TEST_REALM, TEST_GROUP_ID)).thenReturn(roles);
        when(mapper.writeValueAsString(roles)).thenReturn("[{\"id\":\"role-id\",\"name\":\"test-role\"}]");

        // Act
        String result = groupTool.getGroupRoles(TEST_REALM, TEST_GROUP_ID);

        // Assert
        assertEquals("[{\"id\":\"role-id\",\"name\":\"test-role\"}]", result);
        verify(groupService).getGroupRoles(TEST_REALM, TEST_GROUP_ID);
        verify(mapper).writeValueAsString(roles);
    }

    @Test
    public void testGetGroupRoles_MappingException_ThrowsToolCallException() throws Exception {
        // Arrange
        List<RoleRepresentation> roles = new ArrayList<>();
        when(groupService.getGroupRoles(TEST_REALM, TEST_GROUP_ID)).thenReturn(roles);
        when(mapper.writeValueAsString(roles)).thenThrow(new RuntimeException("Mapping error"));

        // Act & Assert
        assertThrows(ToolCallException.class, () -> groupTool.getGroupRoles(TEST_REALM, TEST_GROUP_ID));
        verify(groupService).getGroupRoles(TEST_REALM, TEST_GROUP_ID);
        verify(mapper).writeValueAsString(roles);
    }

    @Test
    public void testAddRoleToGroup_Success_ReturnsSuccessMessage() {
        // Arrange
        String successMessage = "Successfully added role to group: " + TEST_GROUP_ID + " -> " + TEST_ROLE_NAME;
        when(groupService.addRoleToGroup(TEST_REALM, TEST_GROUP_ID, TEST_ROLE_NAME)).thenReturn(successMessage);

        // Act
        String result = groupTool.addRoleToGroup(TEST_REALM, TEST_GROUP_ID, TEST_ROLE_NAME);

        // Assert
        assertEquals(successMessage, result);
        verify(groupService).addRoleToGroup(TEST_REALM, TEST_GROUP_ID, TEST_ROLE_NAME);
    }

    @Test
    public void testRemoveRoleFromGroup_Success_ReturnsSuccessMessage() {
        // Arrange
        String successMessage = "Successfully removed role from group: " + TEST_GROUP_ID + " -> " + TEST_ROLE_NAME;
        when(groupService.removeRoleFromGroup(TEST_REALM, TEST_GROUP_ID, TEST_ROLE_NAME)).thenReturn(successMessage);

        // Act
        String result = groupTool.removeRoleFromGroup(TEST_REALM, TEST_GROUP_ID, TEST_ROLE_NAME);

        // Assert
        assertEquals(successMessage, result);
        verify(groupService).removeRoleFromGroup(TEST_REALM, TEST_GROUP_ID, TEST_ROLE_NAME);
    }

    @Test
    public void testCreateSubGroup_Success_ReturnsSuccessMessage() {
        // Arrange
        String successMessage = "Successfully created subgroup: " + TEST_PARENT_GROUP_ID + " -> " + TEST_SUBGROUP_NAME;
        when(groupService.createSubGroup(TEST_REALM, TEST_PARENT_GROUP_ID, TEST_SUBGROUP_NAME)).thenReturn(successMessage);

        // Act
        String result = groupTool.createSubGroup(TEST_REALM, TEST_PARENT_GROUP_ID, TEST_SUBGROUP_NAME);

        // Assert
        assertEquals(successMessage, result);
        verify(groupService).createSubGroup(TEST_REALM, TEST_PARENT_GROUP_ID, TEST_SUBGROUP_NAME);
    }
}