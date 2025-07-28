package dev.shaaf.experimental.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.shaaf.experimental.service.UserService;
import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolArg;
import io.quarkiverse.mcp.server.ToolCallException;
import jakarta.inject.Inject;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

public class UserTool {

    @Inject
    UserService userService;

    @Inject
    ObjectMapper mapper;

    @Tool(description = "Get all users from a keycloak realm")
    String getUsers(@ToolArg(description = "A String denoting the name of the realm where the users reside") String realm) {
        try {
            return mapper.writeValueAsString(userService.getUsers(realm));
        } catch (Exception e) {
            throw new ToolCallException("Failed to get users from realm");
        }
    }

    @Tool(description = "Create a new user in keycloak realm with the following mandatory fields realm, username, firstName, lastName, email, password")
    String addUser(@ToolArg(description = "A String denoting the name of the realm where the user resides") String realm,
                   @ToolArg(description = "A String denoting the username of the user to be created") String username,
                   @ToolArg(description = "A String denoting the first name of the user to be created") String firstName,
                   @ToolArg(description = "A String denoting the last name of the user to be created") String lastName,
                   @ToolArg(description = "A String denoting the email of the user to be created") String email,
                   @ToolArg(description = "A String denoting the password of the user to be created") String password) {
        return userService.addUser(realm, username, firstName, lastName, email, password);
    }

    // Just testing.. but probably lots of caution required for delete operations :)
    @Tool(description = "Delete a user in a keycloak realm")
    String deleteUser(@ToolArg(description = "A String denoting the name of the realm where the user resides") String realm,
                      @ToolArg(description = "A String denoting the username of the user to be deleted") String username) {
        return userService.deleteUser(realm, username);
    }

    @Tool(description = "Find a user by username in a realm")
    String getUserByUsername(@ToolArg(description = "A String denoting the name of the realm where the user resides") String realm,
                            @ToolArg(description = "A String denoting the username of the user to find") String username) {
        try {
            return mapper.writeValueAsString(userService.getUserByUsername(realm, username));
        } catch (Exception e) {
            throw new ToolCallException("Failed to get user by username: " + username);
        }
    }

    @Tool(description = "Get a specific user by ID")
    String getUserById(@ToolArg(description = "A String denoting the name of the realm where the user resides") String realm,
                      @ToolArg(description = "A String denoting the ID of the user to retrieve") String userId) {
        try {
            return mapper.writeValueAsString(userService.getUserById(realm, userId));
        } catch (Exception e) {
            throw new ToolCallException("Failed to get user by ID: " + userId);
        }
    }
    
    @Tool(description = "Update a user's information")
    String updateUser(@ToolArg(description = "A String denoting the name of the realm where the user resides") String realm,
                     @ToolArg(description = "A String denoting the ID of the user to update") String userId,
                     @ToolArg(description = "A String denoting the new username (optional)") String username,
                     @ToolArg(description = "A String denoting the new first name (optional)") String firstName,
                     @ToolArg(description = "A String denoting the new last name (optional)") String lastName,
                     @ToolArg(description = "A String denoting the new email (optional)") String email,
                     @ToolArg(description = "A boolean indicating whether the user should be enabled (optional)") Boolean enabled) {
        try {
            // First get the current user representation
            UserRepresentation user = userService.getUserById(realm, userId);
            if (user == null) {
                return "User not found: " + userId;
            }
            
            // Update only the fields that are provided
            if (username != null) user.setUsername(username);
            if (firstName != null) user.setFirstName(firstName);
            if (lastName != null) user.setLastName(lastName);
            if (email != null) user.setEmail(email);
            if (enabled != null) user.setEnabled(enabled);
            
            // Call the service to update the user
            return userService.updateUser(realm, userId, user);
        } catch (Exception e) {
            throw new ToolCallException("Failed to update user: " + userId + " - " + e.getMessage());
        }
    }
    
    @Tool(description = "Get groups a user belongs to")
    String getUserGroups(@ToolArg(description = "A String denoting the name of the realm where the user resides") String realm,
                        @ToolArg(description = "A String denoting the ID of the user") String userId) {
        try {
            return mapper.writeValueAsString(userService.getUserGroups(realm, userId));
        } catch (Exception e) {
            throw new ToolCallException("Failed to get user groups: " + userId);
        }
    }
    
    @Tool(description = "Add a user to a group")
    String addUserToGroup(@ToolArg(description = "A String denoting the name of the realm where the user and group reside") String realm,
                         @ToolArg(description = "A String denoting the ID of the user") String userId,
                         @ToolArg(description = "A String denoting the ID of the group") String groupId) {
        return userService.addUserToGroup(realm, userId, groupId);
    }
    
    @Tool(description = "Remove a user from a group")
    String removeUserFromGroup(@ToolArg(description = "A String denoting the name of the realm where the user and group reside") String realm,
                              @ToolArg(description = "A String denoting the ID of the user") String userId,
                              @ToolArg(description = "A String denoting the ID of the group") String groupId) {
        return userService.removeUserFromGroup(realm, userId, groupId);
    }
    
    @Tool(description = "Get roles assigned to a user")
    String getUserRoles(@ToolArg(description = "A String denoting the name of the realm where the user resides") String realm,
                       @ToolArg(description = "A String denoting the ID of the user") String userId) {
        try {
            return mapper.writeValueAsString(userService.getUserRoles(realm, userId));
        } catch (Exception e) {
            throw new ToolCallException("Failed to get user roles: " + userId);
        }
    }
    
    @Tool(description = "Add a role to a user")
    String addRoleToUser(@ToolArg(description = "A String denoting the name of the realm where the user resides") String realm,
                        @ToolArg(description = "A String denoting the ID of the user") String userId,
                        @ToolArg(description = "A String denoting the name of the role") String roleName) {
        return userService.addRoleToUser(realm, userId, roleName);
    }
    
    @Tool(description = "Remove a role from a user")
    String removeRoleFromUser(@ToolArg(description = "A String denoting the name of the realm where the user resides") String realm,
                             @ToolArg(description = "A String denoting the ID of the user") String userId,
                             @ToolArg(description = "A String denoting the name of the role") String roleName) {
        return userService.removeRoleFromUser(realm, userId, roleName);
    }
    
    @Tool(description = "Reset a user's password")
    String resetPassword(@ToolArg(description = "A String denoting the name of the realm where the user resides") String realm,
                        @ToolArg(description = "A String denoting the ID of the user") String userId,
                        @ToolArg(description = "A String denoting the new password") String newPassword,
                        @ToolArg(description = "A boolean indicating whether the password is temporary") boolean temporary) {
        return userService.resetPassword(realm, userId, newPassword, temporary);
    }
    
    @Tool(description = "Send verification email to a user")
    String sendVerificationEmail(@ToolArg(description = "A String denoting the name of the realm where the user resides") String realm,
                                @ToolArg(description = "A String denoting the ID of the user") String userId) {
        return userService.sendVerificationEmail(realm, userId);
    }
    
    @Tool(description = "Count users in a realm")
    String countUsers(@ToolArg(description = "A String denoting the name of the realm to count users in") String realm) {
        int count = userService.countUsers(realm);
        return count >= 0 ? String.valueOf(count) : "Failed to count users in realm: " + realm;
    }



}
