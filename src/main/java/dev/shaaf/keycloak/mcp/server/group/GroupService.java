package dev.shaaf.keycloak.mcp.server.group;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.GroupResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.Collections;
import java.util.List;

/**
 * Service for managing Keycloak groups
 */
@ApplicationScoped
public class GroupService {

    @Inject
    Keycloak keycloak;

    /**
     * Get all groups
     * @param realm The realm to get groups from
     * @return List of all groups
     */
    public List<GroupRepresentation> getGroups(String realm) {
        try {
            return keycloak.realm(realm).groups().groups();
        } catch (Exception e) {
            Log.error("Failed to get groups: " + realm, e);
            return Collections.emptyList();
        }
    }

    /**
     * Get a specific group
     * @param realm The realm where the group resides
     * @param groupId The ID of the group
     * @return The group representation or null if not found
     */
    public GroupRepresentation getGroup(String realm, String groupId) {
        try {
            return keycloak.realm(realm).groups().group(groupId).toRepresentation();
        } catch (NotFoundException e) {
            Log.error("Group not found: " + groupId, e);
            return null;
        } catch (Exception e) {
            Log.error("Failed to get group: " + groupId, e);
            return null;
        }
    }

    /**
     * Create a group
     * @param realm The realm where the group will be created
     * @param groupName The name of the group
     * @return Success or error message
     */
    public String createGroup(String realm, String groupName) {
        try {
            GroupRepresentation group = new GroupRepresentation();
            group.setName(groupName);
            
            Response response = keycloak.realm(realm).groups().add(group);
            if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
                return "Successfully created group: " + groupName;
            } else {
                Log.error("Failed to create group. Status: " + response.getStatus());
                response.close();
                return "Error creating group: " + groupName;
            }
        } catch (Exception e) {
            Log.error("Failed to create group: " + groupName, e);
            return "Error creating group: " + groupName + " - " + e.getMessage();
        }
    }

    /**
     * Update a group
     * @param realm The realm where the group resides
     * @param groupId The ID of the group
     * @param groupRepresentation The updated group representation
     * @return Success or error message
     */
    public String updateGroup(String realm, String groupId, GroupRepresentation groupRepresentation) {
        try {
            GroupResource groupResource = keycloak.realm(realm).groups().group(groupId);
            groupResource.update(groupRepresentation);
            return "Successfully updated group: " + groupId;
        } catch (NotFoundException e) {
            return "Group not found: " + groupId;
        } catch (Exception e) {
            Log.error("Failed to update group: " + groupId, e);
            return "Error updating group: " + groupId + " - " + e.getMessage();
        }
    }

    /**
     * Delete a group
     * @param realm The realm where the group resides
     * @param groupId The ID of the group
     * @return Success or error message
     */
    public String deleteGroup(String realm, String groupId) {
        try {
            // Check if group exists before attempting to delete
            GroupRepresentation group = getGroup(realm, groupId);
            if (group == null) {
                return "Group not found: " + groupId;
            }
            
            keycloak.realm(realm).groups().group(groupId).remove();
            return "Successfully deleted group: " + groupId;
        } catch (NotFoundException e) {
            return "Group not found: " + groupId;
        } catch (Exception e) {
            Log.error("Failed to delete group: " + groupId, e);
            return "Error deleting group: " + groupId + " - " + e.getMessage();
        }
    }

    /**
     * Get group members
     * @param realm The realm where the group resides
     * @param groupId The ID of the group
     * @return List of users in the group or empty list if not found
     */
    public List<UserRepresentation> getGroupMembers(String realm, String groupId) {
        try {
            return keycloak.realm(realm).groups().group(groupId).members();
        } catch (NotFoundException e) {
            Log.error("Group not found: " + groupId, e);
            return Collections.emptyList();
        } catch (Exception e) {
            Log.error("Failed to get group members: " + groupId, e);
            return Collections.emptyList();
        }
    }

    /**
     * Get group roles
     * @param realm The realm where the group resides
     * @param groupId The ID of the group
     * @return List of roles assigned to the group or empty list if not found
     */
    public List<RoleRepresentation> getGroupRoles(String realm, String groupId) {
        try {
            return keycloak.realm(realm).groups().group(groupId).roles().realmLevel().listEffective();
        } catch (NotFoundException e) {
            Log.error("Group not found: " + groupId, e);
            return Collections.emptyList();
        } catch (Exception e) {
            Log.error("Failed to get group roles: " + groupId, e);
            return Collections.emptyList();
        }
    }

    /**
     * Add role to group
     * @param realm The realm where the group and role reside
     * @param groupId The ID of the group
     * @param roleName The name of the role
     * @return Success or error message
     */
    public String addRoleToGroup(String realm, String groupId, String roleName) {
        try {
            // Get the role representation
            RoleRepresentation role = keycloak.realm(realm).roles().get(roleName).toRepresentation();
            
            // Add the role to the group
            keycloak.realm(realm).groups().group(groupId).roles().realmLevel().add(List.of(role));
            
            return "Successfully added role to group: " + groupId + " -> " + roleName;
        } catch (NotFoundException e) {
            return "Group or role not found: " + groupId + " -> " + roleName;
        } catch (Exception e) {
            Log.error("Failed to add role to group: " + groupId + " -> " + roleName, e);
            return "Error adding role to group: " + groupId + " -> " + roleName + " - " + e.getMessage();
        }
    }

    /**
     * Remove role from group
     * @param realm The realm where the group and role reside
     * @param groupId The ID of the group
     * @param roleName The name of the role
     * @return Success or error message
     */
    public String removeRoleFromGroup(String realm, String groupId, String roleName) {
        try {
            // Get the role representation
            RoleRepresentation role = keycloak.realm(realm).roles().get(roleName).toRepresentation();
            
            // Remove the role from the group
            keycloak.realm(realm).groups().group(groupId).roles().realmLevel().remove(List.of(role));
            
            return "Successfully removed role from group: " + groupId + " -> " + roleName;
        } catch (NotFoundException e) {
            return "Group or role not found: " + groupId + " -> " + roleName;
        } catch (Exception e) {
            Log.error("Failed to remove role from group: " + groupId + " -> " + roleName, e);
            return "Error removing role from group: " + groupId + " -> " + roleName + " - " + e.getMessage();
        }
    }

    /**
     * Create subgroup
     * @param realm The realm where the parent group resides
     * @param parentGroupId The ID of the parent group
     * @param subGroupName The name of the subgroup
     * @return Success or error message
     */
    public String createSubGroup(String realm, String parentGroupId, String subGroupName) {
        try {
            GroupRepresentation subGroup = new GroupRepresentation();
            subGroup.setName(subGroupName);
            
            Response response = keycloak.realm(realm).groups().group(parentGroupId).subGroup(subGroup);
            if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
                return "Successfully created subgroup: " + parentGroupId + " -> " + subGroupName;
            } else {
                Log.error("Failed to create subgroup. Status: " + response.getStatus());
                response.close();
                return "Error creating subgroup: " + parentGroupId + " -> " + subGroupName;
            }
        } catch (NotFoundException e) {
            return "Parent group not found: " + parentGroupId;
        } catch (Exception e) {
            Log.error("Failed to create subgroup: " + parentGroupId + " -> " + subGroupName, e);
            return "Error creating subgroup: " + parentGroupId + " -> " + subGroupName + " - " + e.getMessage();
        }
    }
}