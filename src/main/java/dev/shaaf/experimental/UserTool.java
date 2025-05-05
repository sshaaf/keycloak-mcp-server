package dev.shaaf.experimental;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.shaaf.experimental.service.UserService;
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

    // Just testing.. but probably lots of caution required for delete operations :)
    @Tool(description = "Delete a user in a keycloak realm")
    String deleteUser(@ToolArg(description = "A String denoting the name of the realm where the user resides") String realm,
                      @ToolArg(description = "A String denoting the username of the user to be deleted") String username) {
        return userService.deleteUser(realm, username);
    }



}
