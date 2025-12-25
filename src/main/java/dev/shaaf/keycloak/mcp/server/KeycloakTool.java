package dev.shaaf.keycloak.mcp.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.shaaf.keycloak.mcp.server.authentication.AuthenticationService;
import dev.shaaf.keycloak.mcp.server.client.ClientService;
import dev.shaaf.keycloak.mcp.server.discourse.DiscourseService;
import dev.shaaf.keycloak.mcp.server.discourse.SearchResource;
import dev.shaaf.keycloak.mcp.server.group.GroupService;
import dev.shaaf.keycloak.mcp.server.idp.IdentityProviderService;
import dev.shaaf.keycloak.mcp.server.realm.RealmService;
import dev.shaaf.keycloak.mcp.server.role.RoleService;
import dev.shaaf.keycloak.mcp.server.user.UserService;
import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolArg;
import io.quarkiverse.mcp.server.ToolCallException;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import org.keycloak.representations.idm.AuthenticationExecutionInfoRepresentation;
import org.keycloak.representations.idm.AuthenticationFlowRepresentation;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.GroupRepresentation;

import java.util.Optional;

/**
 * Unified Keycloak Tool implementing the "Parametric Collapse" strategy.
 * Instead of having multiple tool classes, this single tool handles all Keycloak operations
 * by routing based on an operation type enum parameter.
 */
public class KeycloakTool {

    @Inject
    UserService userService;

    @Inject
    RealmService realmService;

    @Inject
    ClientService clientService;

    @Inject
    RoleService roleService;

    @Inject
    GroupService groupService;

    @Inject
    IdentityProviderService identityProviderService;

    @Inject
    AuthenticationService authenticationService;

    @Inject
    SearchResource searchResource;

    @Inject
    ObjectMapper mapper;

    /**
     * Enum defining all available Keycloak operations
     */
    public enum KeycloakOperation {
        // User Operations
        GET_USERS,
        GET_USER_BY_USERNAME,
        CREATE_USER,
        DELETE_USER,
        UPDATE_USER,
        GET_USER_BY_ID,
        GET_USER_GROUPS,
        ADD_USER_TO_GROUP,
        REMOVE_USER_FROM_GROUP,
        GET_USER_ROLES,
        ADD_ROLE_TO_USER,
        REMOVE_ROLE_FROM_USER,
        RESET_PASSWORD,
        SEND_VERIFICATION_EMAIL,
        COUNT_USERS,

        // Realm Operations
        GET_REALMS,
        GET_REALM,
        CREATE_REALM,

        // Client Operations
        GET_CLIENTS,
        GET_CLIENT,
        CREATE_CLIENT,
        DELETE_CLIENT,
        GENERATE_CLIENT_SECRET,
        GET_CLIENT_ROLES,
        CREATE_CLIENT_ROLE,
        DELETE_CLIENT_ROLE,

        // Role Operations
        GET_REALM_ROLES,
        GET_REALM_ROLE,

        // Group Operations
        GET_GROUPS,
        GET_GROUP_MEMBERS,
        CREATE_GROUP,
        UPDATE_GROUP,
        DELETE_GROUP,
        CREATE_SUBGROUP,

        // Identity Provider Operations
        GET_IDENTITY_PROVIDERS,
        GET_IDENTITY_PROVIDER,
        GET_IDENTITY_PROVIDER_MAPPERS,

        // Authentication Operations
        GET_AUTHENTICATION_FLOWS,
        GET_AUTHENTICATION_FLOW,
        CREATE_AUTHENTICATION_FLOW,
        DELETE_AUTHENTICATION_FLOW,
        GET_FLOW_EXECUTIONS,
        UPDATE_FLOW_EXECUTION,

        // Discourse Operations
        SEARCH_DISCOURSE
    }

    /**
     * Single unified tool method that handles all Keycloak operations.
     * Routes to the appropriate service based on the operation parameter.
     *
     * @param operation The type of Keycloak operation to perform
     * @param params    JSON string containing the parameters for the operation
     * @return JSON string result from the operation
     */
    @Tool(description = "Execute Keycloak operations. Supports user, realm, client, role, group, identity provider, authentication management, and discourse search. " +
            "Pass the operation type and parameters as JSON. Available operations: " +
            "User ops: GET_USERS, GET_USER_BY_USERNAME, CREATE_USER, DELETE_USER, UPDATE_USER, GET_USER_BY_ID, GET_USER_GROUPS, ADD_USER_TO_GROUP, REMOVE_USER_FROM_GROUP, GET_USER_ROLES, ADD_ROLE_TO_USER, REMOVE_ROLE_FROM_USER, RESET_PASSWORD, SEND_VERIFICATION_EMAIL, COUNT_USERS; " +
            "Realm ops: GET_REALMS, GET_REALM, CREATE_REALM; " +
            "Client ops: GET_CLIENTS, GET_CLIENT, CREATE_CLIENT, DELETE_CLIENT, GENERATE_CLIENT_SECRET, GET_CLIENT_ROLES, CREATE_CLIENT_ROLE, DELETE_CLIENT_ROLE; " +
            "Role ops: GET_REALM_ROLES, GET_REALM_ROLE; " +
            "Group ops: GET_GROUPS, GET_GROUP_MEMBERS, CREATE_GROUP, UPDATE_GROUP, DELETE_GROUP, CREATE_SUBGROUP; " +
            "IDP ops: GET_IDENTITY_PROVIDERS, GET_IDENTITY_PROVIDER, GET_IDENTITY_PROVIDER_MAPPERS; " +
            "Auth ops: GET_AUTHENTICATION_FLOWS, GET_AUTHENTICATION_FLOW, CREATE_AUTHENTICATION_FLOW, DELETE_AUTHENTICATION_FLOW, GET_FLOW_EXECUTIONS, UPDATE_FLOW_EXECUTION; " +
            "Discourse ops: SEARCH_DISCOURSE")
    public String executeKeycloakOperation(
            @ToolArg(description = "The operation to perform (e.g., GET_USERS, CREATE_USER, GET_REALMS, etc.)") KeycloakOperation operation,
            @ToolArg(description = "JSON object containing operation parameters. Required fields vary by operation. " +
                    "Common fields: realm (String), username (String), userId (String), email (String), " +
                    "firstName (String), lastName (String), password (String), groupId (String), " +
                    "roleName (String), clientId (String), etc.") String params) {
        
        try {
            JsonNode paramsNode = mapper.readTree(params);

            /**
             * Notes:
             * how about we put the ops in a hashMap<OP, Class.ExecutionCall>; An OpsFactory that can register all annotations?
             * Op Interface should have the getOpName, execute(paramsNode) with a return of String.
             * How should I register the ops. 1. Annotation, means CDI injection, but might have problems with the native build.
             * Also which service should be injected. Do I need a context object? or maybe create a ServiceFactory a Singleton
             * that can give access to all components that want to use the service.
             * */

            switch (operation) {
                // ========== USER OPERATIONS ==========
                case GET_USERS:
                    return mapper.writeValueAsString(
                            userService.getUsers(paramsNode.get("realm").asText())
                    );
                
                case GET_USER_BY_USERNAME:
                    return mapper.writeValueAsString(
                            userService.getUserByUsername(
                                    paramsNode.get("realm").asText(),
                                    paramsNode.get("username").asText()
                            )
                    );
                
                case CREATE_USER:
                    return userService.addUser(
                            paramsNode.get("realm").asText(),
                            paramsNode.get("username").asText(),
                            paramsNode.get("firstName").asText(),
                            paramsNode.get("lastName").asText(),
                            paramsNode.get("email").asText(),
                            paramsNode.get("password").asText()
                    );
                
                case DELETE_USER:
                    return userService.deleteUser(
                            paramsNode.get("realm").asText(),
                            paramsNode.get("username").asText()
                    );
                
                case UPDATE_USER:
                    return userService.updateUser(
                            paramsNode.get("realm").asText(),
                            paramsNode.get("userId").asText(),
                            mapper.treeToValue(paramsNode.get("userRepresentation"), 
                                    org.keycloak.representations.idm.UserRepresentation.class)
                    );
                
                case GET_USER_BY_ID:
                    return mapper.writeValueAsString(
                            userService.getUserById(
                                    paramsNode.get("realm").asText(),
                                    paramsNode.get("userId").asText()
                            )
                    );
                
                case GET_USER_GROUPS:
                    return mapper.writeValueAsString(
                            userService.getUserGroups(
                                    paramsNode.get("realm").asText(),
                                    paramsNode.get("userId").asText()
                            )
                    );
                
                case ADD_USER_TO_GROUP:
                    return userService.addUserToGroup(
                            paramsNode.get("realm").asText(),
                            paramsNode.get("userId").asText(),
                            paramsNode.get("groupId").asText()
                    );
                
                case REMOVE_USER_FROM_GROUP:
                    return userService.removeUserFromGroup(
                            paramsNode.get("realm").asText(),
                            paramsNode.get("userId").asText(),
                            paramsNode.get("groupId").asText()
                    );
                
                case GET_USER_ROLES:
                    return mapper.writeValueAsString(
                            userService.getUserRoles(
                                    paramsNode.get("realm").asText(),
                                    paramsNode.get("userId").asText()
                            )
                    );
                
                case ADD_ROLE_TO_USER:
                    return userService.addRoleToUser(
                            paramsNode.get("realm").asText(),
                            paramsNode.get("userId").asText(),
                            paramsNode.get("roleName").asText()
                    );
                
                case REMOVE_ROLE_FROM_USER:
                    return userService.removeRoleFromUser(
                            paramsNode.get("realm").asText(),
                            paramsNode.get("userId").asText(),
                            paramsNode.get("roleName").asText()
                    );
                
                case RESET_PASSWORD:
                    return userService.resetPassword(
                            paramsNode.get("realm").asText(),
                            paramsNode.get("userId").asText(),
                            paramsNode.get("newPassword").asText(),
                            paramsNode.has("temporary") && paramsNode.get("temporary").asBoolean()
                    );
                
                case SEND_VERIFICATION_EMAIL:
                    return userService.sendVerificationEmail(
                            paramsNode.get("realm").asText(),
                            paramsNode.get("userId").asText()
                    );
                
                case COUNT_USERS:
                    return String.valueOf(userService.countUsers(paramsNode.get("realm").asText()));
                
                // ========== REALM OPERATIONS ==========
                case GET_REALMS:
                    return mapper.writeValueAsString(realmService.getRealms());
                
                case GET_REALM:
                    return mapper.writeValueAsString(
                            realmService.getRealm(paramsNode.get("realmName").asText())
                    );
                
                case CREATE_REALM:
                    return realmService.createRealm(
                            paramsNode.get("realmName").asText(),
                            paramsNode.get("displayName").asText(),
                            paramsNode.has("enabled") && paramsNode.get("enabled").asBoolean()
                    );
                
                // ========== CLIENT OPERATIONS ==========
                case GET_CLIENTS:
                    return mapper.writeValueAsString(
                            clientService.getClients(paramsNode.get("realm").asText())
                    );
                
                case GET_CLIENT:
                    Optional<ClientRepresentation> client = clientService.findClientByClientId(
                            paramsNode.get("realm").asText(),
                            paramsNode.get("clientId").asText()
                    );
                    return mapper.writeValueAsString(client.orElse(null));
                
                case CREATE_CLIENT:
                    return clientService.createClient(
                            paramsNode.get("realm").asText(),
                            paramsNode.get("clientId").asText(),
                            paramsNode.get("redirectUris").asText()
                    );
                
                case DELETE_CLIENT:
                    return clientService.deleteClient(
                            paramsNode.get("realm").asText(),
                            paramsNode.get("clientId").asText()
                    );
                
                case GENERATE_CLIENT_SECRET:
                    return clientService.generateNewClientSecret(
                            paramsNode.get("realm").asText(),
                            paramsNode.get("clientId").asText()
                    );
                
                case GET_CLIENT_ROLES:
                    return mapper.writeValueAsString(
                            clientService.getClientRoles(
                                    paramsNode.get("realm").asText(),
                                    paramsNode.get("clientId").asText()
                            )
                    );
                
                case CREATE_CLIENT_ROLE:
                    return clientService.createClientRole(
                            paramsNode.get("realm").asText(),
                            paramsNode.get("clientId").asText(),
                            paramsNode.get("roleName").asText(),
                            paramsNode.get("description").asText()
                    );
                
                case DELETE_CLIENT_ROLE:
                    return clientService.deleteClientRole(
                            paramsNode.get("realm").asText(),
                            paramsNode.get("clientId").asText(),
                            paramsNode.get("roleName").asText()
                    );
                
                // ========== ROLE OPERATIONS ==========
                case GET_REALM_ROLES:
                    return mapper.writeValueAsString(
                            roleService.getRealmRoles(paramsNode.get("realm").asText())
                    );
                
                case GET_REALM_ROLE:
                    return mapper.writeValueAsString(
                            roleService.getRealmRole(
                                    paramsNode.get("realm").asText(),
                                    paramsNode.get("roleName").asText()
                            )
                    );
                
                // ========== GROUP OPERATIONS ==========
                case GET_GROUPS:
                    return mapper.writeValueAsString(
                            groupService.getGroups(paramsNode.get("realm").asText())
                    );
                
                case GET_GROUP_MEMBERS:
                    return mapper.writeValueAsString(
                            groupService.getGroupMembers(
                                    paramsNode.get("realm").asText(),
                                    paramsNode.get("groupId").asText()
                            )
                    );
                
                case CREATE_GROUP:
                    return groupService.createGroup(
                            paramsNode.get("realm").asText(),
                            paramsNode.get("groupName").asText()
                    );
                
                case UPDATE_GROUP:
                    GroupRepresentation groupRep = mapper.treeToValue(
                            paramsNode.get("groupRepresentation"), 
                            GroupRepresentation.class
                    );
                    return groupService.updateGroup(
                            paramsNode.get("realm").asText(),
                            paramsNode.get("groupId").asText(),
                            groupRep
                    );
                
                case DELETE_GROUP:
                    return groupService.deleteGroup(
                            paramsNode.get("realm").asText(),
                            paramsNode.get("groupId").asText()
                    );
                
                case CREATE_SUBGROUP:
                    return groupService.createSubGroup(
                            paramsNode.get("realm").asText(),
                            paramsNode.get("parentGroupId").asText(),
                            paramsNode.get("subGroupName").asText()
                    );
                
                // ========== IDENTITY PROVIDER OPERATIONS ==========
                case GET_IDENTITY_PROVIDERS:
                    return mapper.writeValueAsString(
                            identityProviderService.getIdentityProviders(paramsNode.get("realm").asText())
                    );
                
                case GET_IDENTITY_PROVIDER:
                    return mapper.writeValueAsString(
                            identityProviderService.getIdentityProvider(
                                    paramsNode.get("realm").asText(),
                                    paramsNode.get("alias").asText()
                            )
                    );
                
                case GET_IDENTITY_PROVIDER_MAPPERS:
                    return mapper.writeValueAsString(
                            identityProviderService.getIdentityProviderMappers(
                                    paramsNode.get("realm").asText(),
                                    paramsNode.get("alias").asText()
                            )
                    );
                
                // ========== AUTHENTICATION OPERATIONS ==========
                case GET_AUTHENTICATION_FLOWS:
                    return mapper.writeValueAsString(
                            authenticationService.getAuthenticationFlows(paramsNode.get("realm").asText())
                    );
                
                case GET_AUTHENTICATION_FLOW:
                    return mapper.writeValueAsString(
                            authenticationService.getAuthenticationFlow(
                                    paramsNode.get("realm").asText(),
                                    paramsNode.get("flowId").asText()
                            )
                    );
                
                case CREATE_AUTHENTICATION_FLOW:
                    AuthenticationFlowRepresentation flowRep = authenticationService.getAuthenticationFlow(
                            paramsNode.get("realm").asText(),
                            paramsNode.get("authFlowNameId").asText()
                    );
                    flowRep.setId(null);
                    flowRep.setAlias(paramsNode.get("authFlowNameId").asText() + "-copy");
                    return authenticationService.createAuthenticationFlow(
                            paramsNode.get("realm").asText(),
                            flowRep
                    );
                
                case DELETE_AUTHENTICATION_FLOW:
                    return authenticationService.deleteAuthenticationFlow(
                            paramsNode.get("realm").asText(),
                            paramsNode.get("flowId").asText()
                    );
                
                case GET_FLOW_EXECUTIONS:
                    return mapper.writeValueAsString(
                            authenticationService.getFlowExecutions(
                                    paramsNode.get("realm").asText(),
                                    paramsNode.get("flowAlias").asText()
                            )
                    );
                
                case UPDATE_FLOW_EXECUTION:
                    AuthenticationExecutionInfoRepresentation execution = mapper.treeToValue(
                            paramsNode.get("executionRepresentation"), 
                            AuthenticationExecutionInfoRepresentation.class
                    );
                    return authenticationService.updateFlowExecution(
                            paramsNode.get("realm").asText(),
                            paramsNode.get("flowAlias").asText(),
                            execution
                    );
                
                // ========== DISCOURSE OPERATIONS ==========
                case SEARCH_DISCOURSE:
                    return mapper.writeValueAsString(
                            searchResource.performSearch(paramsNode.get("query").asText())
                    );
                
                default:
                    throw new ToolCallException("Unknown operation: " + operation);
            }
            
        } catch (Exception e) {
            Log.error("Failed to execute Keycloak operation: " + operation, e);
            throw new ToolCallException("Failed to execute operation " + operation + ": " + e.getMessage());
        }
    }
}

