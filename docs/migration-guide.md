# Migration Guide: Individual Tools → Unified KeycloakTool

This guide explains the transition from individual tool classes to the unified `KeycloakTool` using the Parametric Collapse pattern.

## Overview of Changes

### Before: Multiple Tool Classes (Old Approach)
```
src/main/java/dev/shaaf/keycloak/mcp/server/
 user/
 UserService.java
 UserTool.java (9 methods)
 realm/
 RealmService.java
 RealmTool.java (3 methods)
 client/
 ClientService.java
 ClientTool.java (9 methods)
 role/
 RoleService.java
 RoleTool.java (2 methods)
 group/
 GroupService.java
 GroupTool.java (5 methods)
 idp/
 IdentityProviderService.java
 IdentityProviderTool.java (3 methods)
 authentication/
 AuthenticationService.java
 AuthenticationTool.java (6 methods)

Total: 7 Tool Classes, 37+ Methods exposed as individual MCP tools
```

### After: Single Unified Tool (New Approach)
```
src/main/java/dev/shaaf/keycloak/mcp/server/
 KeycloakTool.java (1 method, 45+ operations)
 user/
 UserService.java
 realm/
 RealmService.java
 client/
 ClientService.java
 role/
 RoleService.java
 group/
 GroupService.java
 idp/
 IdentityProviderService.java
 authentication/
 AuthenticationService.java

Total: 1 Tool Class, 1 Method, 45+ Operations via enum
```

## What Changed

### Service Layer
 **NO CHANGES** - All service classes remain unchanged
- `UserService.java`
- `RealmService.java`
- `ClientService.java`
- `RoleService.java`
- `GroupService.java`
- `IdentityProviderService.java`
- `AuthenticationService.java`

### Tool Layer
 **Deprecated/Replaced** - Individual tool classes (can be kept for backward compatibility or removed)
- `UserTool.java`
- `RealmTool.java`
- `ClientTool.java`
- `RoleTool.java`
- `GroupTool.java`
- `IdentityProviderTool.java`
- `AuthenticationTool.java`

 **NEW** - Single unified tool class
- `KeycloakTool.java`

## Migration Examples

### User Operations

#### Before: UserTool.getUsers()
```java
@Tool(description = "Get all users from a keycloak realm")
String getUsers(@ToolArg(description = "A String denoting the name of the realm") String realm) {
 try {
 return mapper.writeValueAsString(userService.getUsers(realm));
 } catch (Exception e) {
 throw new ToolCallException("Failed to get users from realm");
 }
}
```

#### After: KeycloakTool.executeKeycloakOperation()
```java
// Operation: GET_USERS
// Params: {"realm": "quarkus"}

case GET_USERS:
 return mapper.writeValueAsString(
 userService.getUsers(paramsNode.get("realm").asText())
 );
```

---

#### Before: UserTool.addUser()
```java
@Tool(description = "Create a new user in keycloak realm")
String addUser(@ToolArg(description = "realm") String realm,
 @ToolArg(description = "username") String username,
 @ToolArg(description = "firstName") String firstName,
 @ToolArg(description = "lastName") String lastName,
 @ToolArg(description = "email") String email,
 @ToolArg(description = "password") String password) {
 return userService.addUser(realm, username, firstName, lastName, email, password);
}
```

#### After: KeycloakTool.executeKeycloakOperation()
```java
// Operation: CREATE_USER
// Params: {
// "realm": "quarkus",
// "username": "jdoe",
// "firstName": "John",
// "lastName": "Doe",
// "email": "john@example.com",
// "password": "secure123"
// }

case CREATE_USER:
 return userService.addUser(
 paramsNode.get("realm").asText(),
 paramsNode.get("username").asText(),
 paramsNode.get("firstName").asText(),
 paramsNode.get("lastName").asText(),
 paramsNode.get("email").asText(),
 paramsNode.get("password").asText()
 );
```

---

### Client Operations

#### Before: ClientTool.getClients()
```java
@Tool(description = "Get all clients from a keycloak realm")
String getClients(@ToolArg(description = "realm name") String realm) {
 try {
 return mapper.writeValueAsString(clientService.getClients(realm));
 } catch (Exception e) {
 throw new ToolCallException("Unable to get clients from realm");
 }
}
```

#### After: KeycloakTool.executeKeycloakOperation()
```java
// Operation: GET_CLIENTS
// Params: {"realm": "quarkus"}

case GET_CLIENTS:
 return mapper.writeValueAsString(
 clientService.getClients(paramsNode.get("realm").asText())
 );
```

---

### Group Operations

#### Before: GroupTool.createGroup()
```java
@Tool(description = "Create a group in a realm")
String createGroup(@ToolArg(description = "realm name") String realm,
 @ToolArg(description = "group name") String groupName) {
 return groupService.createGroup(realm, groupName);
}
```

#### After: KeycloakTool.executeKeycloakOperation()
```java
// Operation: CREATE_GROUP
// Params: {
// "realm": "quarkus",
// "groupName": "Developers"
// }

case CREATE_GROUP:
 return groupService.createGroup(
 paramsNode.get("realm").asText(),
 paramsNode.get("groupName").asText()
 );
```

---

## Complete Operation Mapping

### User Operations (15)
| Old Method | New Operation | Parameters |
|------------|---------------|------------|
| `getUsers(realm)` | `GET_USERS` | `{realm}` |
| `getUserByUsername(realm, username)` | `GET_USER_BY_USERNAME` | `{realm, username}` |
| `addUser(realm, username, ...)` | `CREATE_USER` | `{realm, username, firstName, lastName, email, password}` |
| `getUserRoles(realm, userId)` | `GET_USER_ROLES` | `{realm, userId}` |
| `addRoleToUser(realm, userId, roleName)` | `ADD_ROLE_TO_USER` | `{realm, userId, roleName}` |
| `removeRoleFromUser(realm, userId, roleName)` | `REMOVE_ROLE_FROM_USER` | `{realm, userId, roleName}` |
| `addUserToGroup(realm, userId, groupId)` | `ADD_USER_TO_GROUP` | `{realm, userId, groupId}` |
| `removeUserFromGroup(realm, userId, groupId)` | `REMOVE_USER_FROM_GROUP` | `{realm, userId, groupId}` |
| *Not exposed before* | `DELETE_USER` | `{realm, username}` |
| *Not exposed before* | `UPDATE_USER` | `{realm, userId, userRepresentation}` |
| *Not exposed before* | `GET_USER_BY_ID` | `{realm, userId}` |
| *Not exposed before* | `GET_USER_GROUPS` | `{realm, userId}` |
| *Not exposed before* | `RESET_PASSWORD` | `{realm, userId, newPassword, temporary}` |
| *Not exposed before* | `SEND_VERIFICATION_EMAIL` | `{realm, userId}` |
| *Not exposed before* | `COUNT_USERS` | `{realm}` |

### Realm Operations (3)
| Old Method | New Operation | Parameters |
|------------|---------------|------------|
| `getRealms()` | `GET_REALMS` | `{}` |
| `getRealm(realmName)` | `GET_REALM` | `{realmName}` |
| `createRealm(realmName, displayName, enabled)` | `CREATE_REALM` | `{realmName, displayName, enabled}` |

### Client Operations (8)
| Old Method | New Operation | Parameters |
|------------|---------------|------------|
| `getClients(realm)` | `GET_CLIENTS` | `{realm}` |
| `getClient(realm, clientId)` | `GET_CLIENT` | `{realm, clientId}` |
| `addClient(realm, clientId, redirectUris)` | `CREATE_CLIENT` | `{realm, clientId, redirectUris}` |
| `deleteClient(realm, clientId)` | `DELETE_CLIENT` | `{realm, clientId}` |
| `generateNewClientSecret(realm, clientId)` | `GENERATE_CLIENT_SECRET` | `{realm, clientId}` |
| `getClientRoles(realm, clientId)` | `GET_CLIENT_ROLES` | `{realm, clientId}` |
| `createClientRole(realm, clientId, roleName, desc)` | `CREATE_CLIENT_ROLE` | `{realm, clientId, roleName, description}` |
| `deleteClientRole(realm, clientId, roleName)` | `DELETE_CLIENT_ROLE` | `{realm, clientId, roleName}` |

### Role Operations (2)
| Old Method | New Operation | Parameters |
|------------|---------------|------------|
| `getRealmRoles(realm)` | `GET_REALM_ROLES` | `{realm}` |
| `getRealmRole(realm, roleName)` | `GET_REALM_ROLE` | `{realm, roleName}` |

### Group Operations (6)
| Old Method | New Operation | Parameters |
|------------|---------------|------------|
| `getGroups(realm)` | `GET_GROUPS` | `{realm}` |
| `getGroupMembers(realm, groupId)` | `GET_GROUP_MEMBERS` | `{realm, groupId}` |
| `createGroup(realm, groupName)` | `CREATE_GROUP` | `{realm, groupName}` |
| `updateGroup(realm, groupId, groupJson)` | `UPDATE_GROUP` | `{realm, groupId, groupRepresentation}` |
| `deleteGroup(realm, groupId)` | `DELETE_GROUP` | `{realm, groupId}` |
| `createSubGroup(realm, parentId, subName)` | `CREATE_SUBGROUP` | `{realm, parentGroupId, subGroupName}` |

### Identity Provider Operations (3)
| Old Method | New Operation | Parameters |
|------------|---------------|------------|
| `getIdentityProviders(realm)` | `GET_IDENTITY_PROVIDERS` | `{realm}` |
| `getIdentityProvider(realm, alias)` | `GET_IDENTITY_PROVIDER` | `{realm, alias}` |
| `getIdentityProviderMappers(realm, alias)` | `GET_IDENTITY_PROVIDER_MAPPERS` | `{realm, alias}` |

### Authentication Operations (6)
| Old Method | New Operation | Parameters |
|------------|---------------|------------|
| `getAuthenticationFlows(realm)` | `GET_AUTHENTICATION_FLOWS` | `{realm}` |
| `getAuthenticationFlow(realm, flowId)` | `GET_AUTHENTICATION_FLOW` | `{realm, flowId}` |
| `createAuthenticationFlow(realm, authFlowNameId)` | `CREATE_AUTHENTICATION_FLOW` | `{realm, authFlowNameId}` |
| `deleteAuthenticationFlow(realm, flowId)` | `DELETE_AUTHENTICATION_FLOW` | `{realm, flowId}` |
| `getFlowExecutions(realm, flowAlias)` | `GET_FLOW_EXECUTIONS` | `{realm, flowAlias}` |
| `updateFlowExecution(realm, flowAlias, json)` | `UPDATE_FLOW_EXECUTION` | `{realm, flowAlias, executionRepresentation}` |

## Backward Compatibility

### Option 1: Keep Both (Recommended for Gradual Migration)
Keep the old tool classes alongside the new `KeycloakTool` for a transition period:
- Allows existing integrations to continue working
- Gives users time to migrate
- Deprecate old tools in documentation
- Remove in a future major version

### Option 2: Remove Old Tools (Clean Break)
Remove all individual tool classes immediately:
- Cleaner codebase
- Forces migration
- May break existing integrations
- Recommended only if you control all clients

## Testing

After migration, verify that all operations work:

```bash
# Compile
mvn clean compile

# Run tests
mvn test

# Build native image
mvn package -Pnative
```

## Common Migration Patterns

### Pattern 1: Simple Parameter Mapping
```java
// Old: Direct parameters
userTool.getUsers("quarkus")

// New: JSON params
keycloakTool.executeKeycloakOperation(
 GET_USERS,
 "{\"realm\":\"quarkus\"}"
)
```

### Pattern 2: Complex Object Parameters
```java
// Old: Parsed JSON string
groupTool.updateGroup(realm, groupId, groupJsonString)

// New: Nested JSON object
keycloakTool.executeKeycloakOperation(
 UPDATE_GROUP,
 "{\"realm\":\"...\", \"groupId\":\"...\", \"groupRepresentation\":{...}}"
)
```

### Pattern 3: Optional Parameters
```java
// Old: Separate method or default values
// Not easily supported

// New: Optional fields in JSON
keycloakTool.executeKeycloakOperation(
 RESET_PASSWORD,
 "{\"realm\":\"...\", \"userId\":\"...\", \"newPassword\":\"...\", \"temporary\":false}"
)
// If 'temporary' is not provided, defaults to false
```

## Benefits Recap

1. **Reduced Complexity**: 37+ tools → 1 tool
2. **Better Error Handling**: Centralized error management
3. **Type Safety**: Enum prevents operation name typos
4. **Flexibility**: JSON allows easy addition of optional parameters
5. **Maintainability**: One place to add logging, metrics, validation
6. **Documentation**: Single comprehensive doc instead of scattered docs
7. **AI-Friendly**: Models process 1 tool faster than 37 tools

## Support

For questions or issues during migration:
- Check [parametric-collapse.md](parametric-collapse.md) for detailed examples
- See KeycloakTool.java for code samples
- Open an issue on GitHub

---

**Migration Status**: Complete - New unified tool ready for use
**Backward Compatibility**: Optional - Old tool classes can be kept or removed based on your needs

