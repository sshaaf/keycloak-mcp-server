package dev.shaaf.keycloak.mcp.server.service;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class UserService {

    @Inject
    Keycloak keycloak;

    /**
     * Get all users from a realm
     * @param realm The realm to get users from
     * @return List of all user representations in the realm
     */
    public List<UserRepresentation> getUsers(String realm) {
        return keycloak.realm(realm).users().list();

    }

    /**
     * Create a new user in a realm
     * @param realm The realm where the user will be created
     * @param username The username for the new user
     * @param firstName The first name of the user
     * @param lastName The last name of the user
     * @param email The email address of the user
     * @param password The password for the user
     * @return Success or error message
     */
    public String addUser(String realm, String username, String firstName, String lastName, String email, String password) {
        UserRepresentation user = new UserRepresentation();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setEnabled(true);
        user.setEmail(email);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);
        user.setCredentials(List.of(credential));
        Response response = keycloak.realm(realm).users().create(user);
        if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
            return "Successfully created user: " + username;
        } else {
            Log.error("Failed to create user. Status: " + response.getStatus());
            response.close();
            return "Error creating user: "+" "+username;
        }
    }

    /**
     * Delete a user from a realm
     * @param realm The realm where the user resides
     * @param username The username of the user to delete
     * @return Success or error message
     */
    public String deleteUser(String realm, String username) {
        UserRepresentation user = getUserByUsername(realm, username);
        if (user != null) {
            Response response = keycloak.realm(realm).users().delete(user.getId());
            if(response.getStatus() == Response.Status.NO_CONTENT.getStatusCode())
                return "successfully deleted: "+user.getId();
            else
                return "failed to delete: "+user.getId();
        }
        else
            return "User not found: "+user.getId();
    }


    /**
     * Find a user by username in a realm
     * @param realm The realm where the user resides
     * @param username The username to search for
     * @return The user representation or null if not found
     */
    public UserRepresentation getUserByUsername(String realm, String username) {
        return keycloak.realm(realm).users()
                .search(username)
                .stream()
                .filter(user -> username.equals(user.getUsername()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Get a specific user by ID
     * @param realm The realm where the user resides
     * @param userId The ID of the user to retrieve
     * @return The user representation or null if not found
     */
    public UserRepresentation getUserById(String realm, String userId) {
        try {
            return keycloak.realm(realm).users().get(userId).toRepresentation();
        } catch (NotFoundException e) {
            Log.error("User not found: " + userId, e);
            return null;
        } catch (Exception e) {
            Log.error("Failed to get user: " + userId, e);
            return null;
        }
    }

    /**
     * Update a user
     * @param realm The realm where the user resides
     * @param userId The ID of the user to update
     * @param userRepresentation The updated user representation
     * @return Success or error message
     */
    public String updateUser(String realm, String userId, UserRepresentation userRepresentation) {
        try {
            UserResource userResource = keycloak.realm(realm).users().get(userId);
            userResource.update(userRepresentation);
            return "Successfully updated user: " + userId;
        } catch (NotFoundException e) {
            return "User not found: " + userId;
        } catch (Exception e) {
            Log.error("Failed to update user: " + userId, e);
            return "Error updating user: " + userId + " - " + e.getMessage();
        }
    }

    /**
     * Get user groups
     * @param realm The realm where the user resides
     * @param userId The ID of the user
     * @return List of groups the user belongs to or empty list if not found
     */
    public List<GroupRepresentation> getUserGroups(String realm, String userId) {
        try {
            return keycloak.realm(realm).users().get(userId).groups();
        } catch (NotFoundException e) {
            Log.error("User not found: " + userId, e);
            return Collections.emptyList();
        } catch (Exception e) {
            Log.error("Failed to get user groups: " + userId, e);
            return Collections.emptyList();
        }
    }

    /**
     * Add user to group
     * @param realm The realm where the user and group reside
     * @param userId The ID of the user
     * @param groupId The ID of the group
     * @return Success or error message
     */
    public String addUserToGroup(String realm, String userId, String groupId) {
        try {
            keycloak.realm(realm).users().get(userId).joinGroup(groupId);
            return "Successfully added user to group: " + userId + " -> " + groupId;
        } catch (NotFoundException e) {
            return "User or group not found: " + userId + " -> " + groupId;
        } catch (Exception e) {
            Log.error("Failed to add user to group: " + userId + " -> " + groupId, e);
            return "Error adding user to group: " + userId + " -> " + groupId + " - " + e.getMessage();
        }
    }

    /**
     * Remove user from group
     * @param realm The realm where the user and group reside
     * @param userId The ID of the user
     * @param groupId The ID of the group
     * @return Success or error message
     */
    public String removeUserFromGroup(String realm, String userId, String groupId) {
        try {
            keycloak.realm(realm).users().get(userId).leaveGroup(groupId);
            return "Successfully removed user from group: " + userId + " -> " + groupId;
        } catch (NotFoundException e) {
            return "User or group not found: " + userId + " -> " + groupId;
        } catch (Exception e) {
            Log.error("Failed to remove user from group: " + userId + " -> " + groupId, e);
            return "Error removing user from group: " + userId + " -> " + groupId + " - " + e.getMessage();
        }
    }

    /**
     * Get user roles
     * @param realm The realm where the user resides
     * @param userId The ID of the user
     * @return List of roles assigned to the user or empty list if not found
     */
    public List<RoleRepresentation> getUserRoles(String realm, String userId) {
        try {
            // Get effective roles (realm + client roles)
            return keycloak.realm(realm).users().get(userId).roles().realmLevel().listEffective();
        } catch (NotFoundException e) {
            Log.error("User not found: " + userId, e);
            return Collections.emptyList();
        } catch (Exception e) {
            Log.error("Failed to get user roles: " + userId, e);
            return Collections.emptyList();
        }
    }

    /**
     * Add role to user
     * @param realm The realm where the user resides
     * @param userId The ID of the user
     * @param roleName The name of the role
     * @return Success or error message
     */
    public String addRoleToUser(String realm, String userId, String roleName) {
        try {
            // Get the role representation
            RoleRepresentation role = keycloak.realm(realm).roles().get(roleName).toRepresentation();
            
            // Add the role to the user
            keycloak.realm(realm).users().get(userId).roles().realmLevel().add(List.of(role));
            
            return "Successfully added role to user: " + userId + " -> " + roleName;
        } catch (NotFoundException e) {
            return "User or role not found: " + userId + " -> " + roleName;
        } catch (Exception e) {
            Log.error("Failed to add role to user: " + userId + " -> " + roleName, e);
            return "Error adding role to user: " + userId + " -> " + roleName + " - " + e.getMessage();
        }
    }

    /**
     * Remove role from user
     * @param realm The realm where the user resides
     * @param userId The ID of the user
     * @param roleName The name of the role
     * @return Success or error message
     */
    public String removeRoleFromUser(String realm, String userId, String roleName) {
        try {
            // Get the role representation
            RoleRepresentation role = keycloak.realm(realm).roles().get(roleName).toRepresentation();
            
            // Remove the role from the user
            keycloak.realm(realm).users().get(userId).roles().realmLevel().remove(List.of(role));
            
            return "Successfully removed role from user: " + userId + " -> " + roleName;
        } catch (NotFoundException e) {
            return "User or role not found: " + userId + " -> " + roleName;
        } catch (Exception e) {
            Log.error("Failed to remove role from user: " + userId + " -> " + roleName, e);
            return "Error removing role from user: " + userId + " -> " + roleName + " - " + e.getMessage();
        }
    }

    /**
     * Reset user password
     * @param realm The realm where the user resides
     * @param userId The ID of the user
     * @param newPassword The new password
     * @param temporary Whether the password is temporary
     * @return Success or error message
     */
    public String resetPassword(String realm, String userId, String newPassword, boolean temporary) {
        try {
            // Create credential representation
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(newPassword);
            credential.setTemporary(temporary);
            
            // Reset password
            keycloak.realm(realm).users().get(userId).resetPassword(credential);
            
            return "Successfully reset password for user: " + userId;
        } catch (NotFoundException e) {
            return "User not found: " + userId;
        } catch (Exception e) {
            Log.error("Failed to reset password for user: " + userId, e);
            return "Error resetting password for user: " + userId + " - " + e.getMessage();
        }
    }

    /**
     * Send verification email
     * @param realm The realm where the user resides
     * @param userId The ID of the user
     * @return Success or error message
     */
    public String sendVerificationEmail(String realm, String userId) {
        try {
            keycloak.realm(realm).users().get(userId).sendVerifyEmail();
            return "Successfully sent verification email to user: " + userId;
        } catch (NotFoundException e) {
            return "User not found: " + userId;
        } catch (Exception e) {
            Log.error("Failed to send verification email to user: " + userId, e);
            return "Error sending verification email to user: " + userId + " - " + e.getMessage();
        }
    }

    /**
     * Count users in a realm
     * @param realm The realm to count users in
     * @return The number of users in the realm
     */
    public int countUsers(String realm) {
        try {
            return keycloak.realm(realm).users().count();
        } catch (Exception e) {
            Log.error("Failed to count users in realm: " + realm, e);
            return -1;
        }
    }

}

