# Parametric Collapse Strategy Implementation

## Overview

This project implements the "Parametric Collapse" strategy to consolidate multiple tool classes into a single, generic `KeycloakTool` class. This design pattern significantly reduces tool proliferation and improves API maintainability.

## The Problem

Before the implementation, the project had 7+ separate tool classes:
- `UserTool` (9 methods)
- `RealmTool` (3 methods)
- `ClientTool` (9 methods)
- `RoleTool` (2 methods)
- `GroupTool` (5 methods)
- `IdentityProviderTool` (3 methods)
- `AuthenticationTool` (6 methods)

This resulted in **37+ individual tool methods** exposed through the MCP protocol, creating a cluttered API surface.

## The Solution

The new `KeycloakTool` class consolidates all operations into:
- **1 unified tool method**: `executeKeycloakOperation()`
- **1 enum parameter**: `KeycloakOperation` (45+ values)
- **1 JSON parameter**: `params` containing operation-specific arguments

### Architecture

```

 KeycloakTool
 (Single Unified Tool Class)

 + executeKeycloakOperation()
 - operation: KeycloakOperation
 - params: JSON


 Routes to appropriate service





 UserService RealmService ...etc

```

### Benefits

1. **Reduced Tool Count**: From 37+ tools to 1 tool
2. **Better Maintainability**: All routing logic in one place
3. **Consistent Error Handling**: Centralized exception management
4. **Type Safety**: Enum-based operation selection
5. **Flexible Parameters**: JSON allows optional and complex parameters
6. **Easy Extension**: Add new operations by adding enum values and cases

## Usage Examples

### User Operations

#### Get all users in a realm
```json
{
 "operation": "GET_USERS",
 "params": {
 "realm": "quarkus"
 }
}
```

#### Create a new user
```json
{
 "operation": "CREATE_USER",
 "params": {
 "realm": "quarkus",
 "username": "jdoe",
 "firstName": "John",
 "lastName": "Doe",
 "email": "john.doe@example.com",
 "password": "SecurePass123!"
 }
}
```

#### Add role to user
```json
{
 "operation": "ADD_ROLE_TO_USER",
 "params": {
 "realm": "quarkus",
 "userId": "1eed6a8e-a853-4597-b4c6-c4c2533546a0",
 "roleName": "admin"
 }
}
```

#### Reset user password
```json
{
 "operation": "RESET_PASSWORD",
 "params": {
 "realm": "quarkus",
 "userId": "1eed6a8e-a853-4597-b4c6-c4c2533546a0",
 "newPassword": "NewSecurePass456!",
 "temporary": false
 }
}
```

### Realm Operations

#### Get all realms
```json
{
 "operation": "GET_REALMS",
 "params": {}
}
```

#### Create a realm
```json
{
 "operation": "CREATE_REALM",
 "params": {
 "realmName": "my-new-realm",
 "displayName": "My New Realm",
 "enabled": true
 }
}
```

### Client Operations

#### Get all clients
```json
{
 "operation": "GET_CLIENTS",
 "params": {
 "realm": "quarkus"
 }
}
```

#### Create a client
```json
{
 "operation": "CREATE_CLIENT",
 "params": {
 "realm": "quarkus",
 "clientId": "my-app",
 "redirectUris": "http://localhost:8080/*"
 }
}
```

#### Generate new client secret
```json
{
 "operation": "GENERATE_CLIENT_SECRET",
 "params": {
 "realm": "quarkus",
 "clientId": "my-app"
 }
}
```

### Group Operations

#### Get all groups
```json
{
 "operation": "GET_GROUPS",
 "params": {
 "realm": "quarkus"
 }
}
```

#### Create a group
```json
{
 "operation": "CREATE_GROUP",
 "params": {
 "realm": "quarkus",
 "groupName": "Developers"
 }
}
```

#### Create a subgroup
```json
{
 "operation": "CREATE_SUBGROUP",
 "params": {
 "realm": "quarkus",
 "parentGroupId": "abc123",
 "subGroupName": "Backend Developers"
 }
}
```

### Authentication Operations

#### Get authentication flows
```json
{
 "operation": "GET_AUTHENTICATION_FLOWS",
 "params": {
 "realm": "quarkus"
 }
}
```

#### Get flow executions
```json
{
 "operation": "GET_FLOW_EXECUTIONS",
 "params": {
 "realm": "quarkus",
 "flowAlias": "browser"
 }
}
```

## Complete Operation List

### User Operations (15)
- `GET_USERS` - List all users in a realm
- `GET_USER_BY_USERNAME` - Find a user by username
- `GET_USER_BY_ID` - Get user by ID
- `CREATE_USER` - Create a new user
- `DELETE_USER` - Delete a user
- `UPDATE_USER` - Update user details
- `GET_USER_GROUPS` - Get groups for a user
- `ADD_USER_TO_GROUP` - Add user to group
- `REMOVE_USER_FROM_GROUP` - Remove user from group
- `GET_USER_ROLES` - Get roles for a user
- `ADD_ROLE_TO_USER` - Assign role to user
- `REMOVE_ROLE_FROM_USER` - Remove role from user
- `RESET_PASSWORD` - Reset user password
- `SEND_VERIFICATION_EMAIL` - Send verification email
- `COUNT_USERS` - Count users in realm

### Realm Operations (3)
- `GET_REALMS` - List all realms
- `GET_REALM` - Get specific realm
- `CREATE_REALM` - Create new realm

### Client Operations (8)
- `GET_CLIENTS` - List all clients
- `GET_CLIENT` - Get specific client
- `CREATE_CLIENT` - Create new client
- `DELETE_CLIENT` - Delete a client
- `GENERATE_CLIENT_SECRET` - Generate new client secret
- `GET_CLIENT_ROLES` - Get client roles
- `CREATE_CLIENT_ROLE` - Create client role
- `DELETE_CLIENT_ROLE` - Delete client role

### Role Operations (2)
- `GET_REALM_ROLES` - List realm roles
- `GET_REALM_ROLE` - Get specific realm role

### Group Operations (6)
- `GET_GROUPS` - List all groups
- `GET_GROUP_MEMBERS` - Get group members
- `CREATE_GROUP` - Create new group
- `UPDATE_GROUP` - Update group
- `DELETE_GROUP` - Delete group
- `CREATE_SUBGROUP` - Create subgroup

### Identity Provider Operations (3)
- `GET_IDENTITY_PROVIDERS` - List identity providers
- `GET_IDENTITY_PROVIDER` - Get specific identity provider
- `GET_IDENTITY_PROVIDER_MAPPERS` - Get IDP mappers

### Authentication Operations (6)
- `GET_AUTHENTICATION_FLOWS` - List authentication flows
- `GET_AUTHENTICATION_FLOW` - Get specific flow
- `CREATE_AUTHENTICATION_FLOW` - Create new flow
- `DELETE_AUTHENTICATION_FLOW` - Delete flow
- `GET_FLOW_EXECUTIONS` - Get flow executions
- `UPDATE_FLOW_EXECUTION` - Update flow execution

## Implementation Details

### Service Layer
The `KeycloakTool` directly injects and uses all service classes:
- `UserService`
- `RealmService`
- `ClientService`
- `RoleService`
- `GroupService`
- `IdentityProviderService`
- `AuthenticationService`

### Error Handling
All operations are wrapped in try-catch blocks with:
- Logging via `io.quarkus.logging.Log`
- Consistent error messages via `ToolCallException`
- Detailed error context including operation type

### JSON Processing
Uses Jackson `ObjectMapper` for:
- Parsing input parameters
- Serializing return values
- Converting JSON to Keycloak representation objects

## Migration from Old Tools

If you were using the old individual tool classes, here is how to migrate:

### Before (UserTool.getUsers)
```java
@Tool(description = "Get all users from a keycloak realm")
String getUsers(String realm)
```

### After (KeycloakTool)
```java
executeKeycloakOperation(
 KeycloakOperation.GET_USERS,
 "{\"realm\": \"quarkus\"}"
)
```

## Extending the Tool

To add a new operation:

1. Add enum value to `KeycloakOperation`:
```java
public enum KeycloakOperation {
 // ... existing operations
 MY_NEW_OPERATION
}
```

2. Add case to switch statement in `executeKeycloakOperation()`:
```java
case MY_NEW_OPERATION:
 return myService.myNewMethod(
 paramsNode.get("param1").asText(),
 paramsNode.get("param2").asInt()
 );
```

3. Update tool description with the new operation name

## Design Pattern Benefits

### Before: Tool Explosion
```
UserTool {
 getUsers()
 createUser()
 deleteUser()
 ...
}

ClientTool {
 getClients()
 createClient()
 deleteClient()
 ...
}

// 7+ more tool classes...
```

### After: Parametric Collapse
```
KeycloakTool {
 executeKeycloakOperation(
 operation: KeycloakOperation,
 params: JSON
 )
}
```

The AI model sees **1 tool definition** instead of **37+ tool definitions**, making it:
- Easier to understand
- Faster to process
- Less prone to tool selection errors
- More maintainable

## Performance Considerations

- **Lazy Parameter Parsing**: JSON is only parsed once per call
- **Direct Service Delegation**: No additional abstraction layers
- **Minimal Overhead**: Switch statement is O(1) with modern JVM optimization
- **Same Memory Footprint**: Services are still singletons

## Testing

To test the implementation:

```bash
# Compile the project
mvn clean compile

# Run tests
mvn test

# Build the native executable
mvn package -Pnative
```

## Conclusion

The Parametric Collapse strategy successfully reduced the Keycloak MCP Server from **37+ individual tools** to **1 unified tool**, while maintaining all functionality and improving maintainability. This pattern can be applied to any domain where you have multiple similar operations that can be categorized and parameterized.

