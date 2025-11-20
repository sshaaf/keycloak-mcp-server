package dev.shaaf.keycloak.mcp.server.examples;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.shaaf.keycloak.mcp.server.KeycloakTool;
import dev.shaaf.keycloak.mcp.server.KeycloakTool.KeycloakOperation;

/**
 * Example class demonstrating how to use the unified KeycloakTool
 * with the Parametric Collapse pattern.
 */
public class KeycloakToolExample {

    private final KeycloakTool keycloakTool;
    private final ObjectMapper mapper;

    public KeycloakToolExample(KeycloakTool keycloakTool, ObjectMapper mapper) {
        this.keycloakTool = keycloakTool;
        this.mapper = mapper;
    }

    /**
     * Example: Get all users from a realm
     */
    public String getAllUsers(String realmName) throws Exception {
        String params = mapper.writeValueAsString(
            new ParamsBuilder()
                .add("realm", realmName)
                .build()
        );
        
        return keycloakTool.executeKeycloakOperation(
            KeycloakOperation.GET_USERS, 
            params
        );
    }

    /**
     * Example: Create a new user
     */
    public String createUser(String realm, String username, String firstName, 
                            String lastName, String email, String password) throws Exception {
        String params = mapper.writeValueAsString(
            new ParamsBuilder()
                .add("realm", realm)
                .add("username", username)
                .add("firstName", firstName)
                .add("lastName", lastName)
                .add("email", email)
                .add("password", password)
                .build()
        );
        
        return keycloakTool.executeKeycloakOperation(
            KeycloakOperation.CREATE_USER,
            params
        );
    }

    /**
     * Example: Add a role to a user
     */
    public String addRoleToUser(String realm, String userId, String roleName) throws Exception {
        String params = mapper.writeValueAsString(
            new ParamsBuilder()
                .add("realm", realm)
                .add("userId", userId)
                .add("roleName", roleName)
                .build()
        );
        
        return keycloakTool.executeKeycloakOperation(
            KeycloakOperation.ADD_ROLE_TO_USER,
            params
        );
    }

    /**
     * Example: Get all clients from a realm
     */
    public String getAllClients(String realm) throws Exception {
        String params = mapper.writeValueAsString(
            new ParamsBuilder()
                .add("realm", realm)
                .build()
        );
        
        return keycloakTool.executeKeycloakOperation(
            KeycloakOperation.GET_CLIENTS,
            params
        );
    }

    /**
     * Example: Create a new realm
     */
    public String createRealm(String realmName, String displayName, boolean enabled) throws Exception {
        String params = mapper.writeValueAsString(
            new ParamsBuilder()
                .add("realmName", realmName)
                .add("displayName", displayName)
                .add("enabled", enabled)
                .build()
        );
        
        return keycloakTool.executeKeycloakOperation(
            KeycloakOperation.CREATE_REALM,
            params
        );
    }

    /**
     * Example: Reset user password
     */
    public String resetUserPassword(String realm, String userId, String newPassword, boolean temporary) throws Exception {
        String params = mapper.writeValueAsString(
            new ParamsBuilder()
                .add("realm", realm)
                .add("userId", userId)
                .add("newPassword", newPassword)
                .add("temporary", temporary)
                .build()
        );
        
        return keycloakTool.executeKeycloakOperation(
            KeycloakOperation.RESET_PASSWORD,
            params
        );
    }

    /**
     * Example: Get all groups in a realm
     */
    public String getAllGroups(String realm) throws Exception {
        String params = mapper.writeValueAsString(
            new ParamsBuilder()
                .add("realm", realm)
                .build()
        );
        
        return keycloakTool.executeKeycloakOperation(
            KeycloakOperation.GET_GROUPS,
            params
        );
    }

    /**
     * Example: Add user to group
     */
    public String addUserToGroup(String realm, String userId, String groupId) throws Exception {
        String params = mapper.writeValueAsString(
            new ParamsBuilder()
                .add("realm", realm)
                .add("userId", userId)
                .add("groupId", groupId)
                .build()
        );
        
        return keycloakTool.executeKeycloakOperation(
            KeycloakOperation.ADD_USER_TO_GROUP,
            params
        );
    }

    /**
     * Example: Get authentication flows
     */
    public String getAuthenticationFlows(String realm) throws Exception {
        String params = mapper.writeValueAsString(
            new ParamsBuilder()
                .add("realm", realm)
                .build()
        );
        
        return keycloakTool.executeKeycloakOperation(
            KeycloakOperation.GET_AUTHENTICATION_FLOWS,
            params
        );
    }

    /**
     * Example: Generate new client secret
     */
    public String generateClientSecret(String realm, String clientId) throws Exception {
        String params = mapper.writeValueAsString(
            new ParamsBuilder()
                .add("realm", realm)
                .add("clientId", clientId)
                .build()
        );
        
        return keycloakTool.executeKeycloakOperation(
            KeycloakOperation.GENERATE_CLIENT_SECRET,
            params
        );
    }

    /**
     * Helper class for building parameter maps
     */
    private static class ParamsBuilder {
        private final java.util.Map<String, Object> params = new java.util.HashMap<>();

        public ParamsBuilder add(String key, Object value) {
            params.put(key, value);
            return this;
        }

        public java.util.Map<String, Object> build() {
            return params;
        }
    }

    /**
     * Main method with usage examples
     */
    public static void main(String[] args) {
        System.out.println("KeycloakTool Usage Examples");
        System.out.println("===========================");
        System.out.println();
        System.out.println("Example 1: Get all users");
        System.out.println("  Operation: GET_USERS");
        System.out.println("  Params: {\"realm\": \"quarkus\"}");
        System.out.println();
        System.out.println("Example 2: Create a user");
        System.out.println("  Operation: CREATE_USER");
        System.out.println("  Params: {");
        System.out.println("    \"realm\": \"quarkus\",");
        System.out.println("    \"username\": \"jdoe\",");
        System.out.println("    \"firstName\": \"John\",");
        System.out.println("    \"lastName\": \"Doe\",");
        System.out.println("    \"email\": \"john@example.com\",");
        System.out.println("    \"password\": \"secret\"");
        System.out.println("  }");
        System.out.println();
        System.out.println("Example 3: Add role to user");
        System.out.println("  Operation: ADD_ROLE_TO_USER");
        System.out.println("  Params: {");
        System.out.println("    \"realm\": \"quarkus\",");
        System.out.println("    \"userId\": \"user-id-here\",");
        System.out.println("    \"roleName\": \"admin\"");
        System.out.println("  }");
        System.out.println();
        System.out.println("See PARAMETRIC_COLLAPSE.md for complete documentation");
    }
}

