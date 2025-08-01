package dev.shaaf.keycloak.mcp.server.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolArg;
import io.quarkiverse.mcp.server.ToolCallException;
import jakarta.inject.Inject;

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

    @Tool(description = "Find a user by username in a keycloak realm")
    String getUserByUsername(@ToolArg(description = "A String denoting the name of the realm where the user resides") String realm,
                            @ToolArg(description = "A String denoting the username of the user to find") String username) {
        try {
            return mapper.writeValueAsString(userService.getUserByUsername(realm, username));
        } catch (Exception e) {
            throw new ToolCallException("Failed to get user by username: " + username);
        }
    }
    
    @Tool(description = "Add user to a group in a keycloak realm")
    String addUserToGroup(@ToolArg(description = "A String denoting the name of the realm where the user and group reside") String realm,
                         @ToolArg(description = "A String denoting the ID of the user") String userId,
                         @ToolArg(description = "A String denoting the ID of the group") String groupId) {
        return userService.addUserToGroup(realm, userId, groupId);
    }
    
    @Tool(description = "Remove user from a group in a keycloak realm")
    String removeUserFromGroup(@ToolArg(description = "A String denoting the name of the realm where the user and group reside") String realm,
                              @ToolArg(description = "A String denoting the ID of the user") String userId,
                              @ToolArg(description = "A String denoting the ID of the group") String groupId) {
        return userService.removeUserFromGroup(realm, userId, groupId);
    }
    
    @Tool(description = "Find roles assigned to a user in a keycloak realm")
    String getUserRoles(@ToolArg(description = "A String denoting the name of the realm where the user resides") String realm,
                       @ToolArg(description = "A String denoting the ID of the user") String userId) {
        try {
            return mapper.writeValueAsString(userService.getUserRoles(realm, userId));
        } catch (Exception e) {
            throw new ToolCallException("Failed to get user roles: " + userId);
        }
    }
    
    @Tool(description = "Add a role to user in a keycloak realm")
    String addRoleToUser(@ToolArg(description = "A String denoting the name of the realm where the user resides") String realm,
                        @ToolArg(description = "A String denoting the ID of the user") String userId,
                        @ToolArg(description = "A String denoting the name of the role") String roleName) {
        return userService.addRoleToUser(realm, userId, roleName);
    }
    
    @Tool(description = "Remove a role from a user from a keycloak realm")
    String removeRoleFromUser(@ToolArg(description = "A String denoting the name of the realm where the user resides") String realm,
                             @ToolArg(description = "A String denoting the ID of the user") String userId,
                             @ToolArg(description = "A String denoting the name of the role") String roleName) {
        return userService.removeRoleFromUser(realm, userId, roleName);
    }
}
