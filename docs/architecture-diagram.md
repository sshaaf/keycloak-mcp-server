# Architecture Diagrams: Before and After Parametric Collapse

## Before: Multiple Tool Classes (Original Design)

```

 MCP Protocol
 (AI Model Interface)


 Exposes 37+ Individual Tools


 Tools Layer

 UserTool RealmTool ClientTool
 (9 methods) (3 methods) (9 methods)



 RoleTool GroupTool IdPTool
 (2 methods) (5 methods) (3 methods)



 AuthTool
 (6 methods)



 All delegate to...


 Service Layer

 UserService RealmService ClientService



 RoleService GroupService IdPService



 AuthService





 Keycloak Server


PROBLEMS:
 37+ tools exposed to AI models
 Cluttered API surface
 Difficult to maintain consistency
 Duplicated error handling logic
 Complex tool selection for AI
```

## After: Single Unified Tool (Parametric Collapse)

```

 MCP Protocol
 (AI Model Interface)


 Exposes 1 Tool


 Tools Layer


 KeycloakTool

 executeKeycloak
 Operation()

 params:
 - operation
 - JSON params



 Switch Router
 (45+ operations)



 Routes based on operation enum





 Service Layer

 UserService RealmService ClientService



 RoleService GroupService IdPService



 AuthService





 Keycloak Server


BENEFITS:
 1 tool exposed to AI models
 Clean, simple API surface
 Centralized error handling
 Consistent logging & validation
 Fast, type-safe routing
```

## Call Flow Comparison

### Before: Multiple Tool Classes

```
AI Model Request: "Create user John Doe"



 MCP Layer


 Selects from 37+ tools

 UserTool
 .addUser()


 Delegates

 UserService
 .addUser()




 Keycloak

```

### After: Single Unified Tool

```
AI Model Request: "Create user John Doe"



 MCP Layer


 Selects the 1 tool

 KeycloakTool
 .executeKeycloak
 Operation()

 operation: CREATE_USER
 params: {
 realm: "quarkus"
 username: "jdoe"
 firstName: "John"
 lastName: "Doe"
 email: "..."
 password: "..."
 }


 Routes via switch

 Switch Statement
 case CREATE_USER:


 Delegates

 UserService
 .addUser()




 Keycloak

```

## Operation Routing Detail

```

 KeycloakTool.executeKeycloakOperation

 Input: (operation: KeycloakOperation, params: JSON)


 Parse JSON params
 JsonNode paramsNode = mapper.readTree()




 Switch on operation

 switch(operation) {
 case GET_USERS: UserService.getUsers()
 case CREATE_USER: UserService.addUser()
 case GET_REALMS: RealmService.getRealms()
 case CREATE_CLIENT: ClientService.createClient()
 case ADD_ROLE_TO_USER: UserService.addRoleToUser()
 case GET_GROUPS: GroupService.getGroups()
 case GET_AUTHENTICATION_FLOWS: AuthService.getAuthFlows()
 // ... 38 more cases
 }




 Serialize result to JSON
 return mapper.writeValueAsString()


 Exception Handling:
 - Centralized logging
 - Consistent error messages
 - Operation context in errors

```

## Parameter Structure Comparison

### Before: Individual Parameters

```java
// UserTool.addUser() - 6 individual parameters
@Tool(description = "Create user")
String addUser(
 @ToolArg String realm,
 @ToolArg String username,
 @ToolArg String firstName,
 @ToolArg String lastName,
 @ToolArg String email,
 @ToolArg String password
)
```

### After: Structured JSON Parameters

```java
// KeycloakTool.executeKeycloakOperation()
@Tool(description = "Execute Keycloak operations")
String executeKeycloakOperation(
 @ToolArg KeycloakOperation operation, // Type-safe enum
 @ToolArg String params // Flexible JSON
)

// Example params:
{
 "realm": "quarkus",
 "username": "jdoe",
 "firstName": "John",
 "lastName": "Doe",
 "email": "john@example.com",
 "password": "secure123"
}
```

## Code Organization Comparison

### Before: Scattered Across Multiple Files

```
src/main/java/dev/shaaf/keycloak/mcp/server/
 user/
 UserService.java (Service logic)
 UserTool.java (9 @Tool methods)
 realm/
 RealmService.java (Service logic)
 RealmTool.java (3 @Tool methods)
 client/
 ClientService.java (Service logic)
 ClientTool.java (9 @Tool methods)
 role/
 RoleService.java (Service logic)
 RoleTool.java (2 @Tool methods)
 group/
 GroupService.java (Service logic)
 GroupTool.java (5 @Tool methods)
 idp/
 IdentityProviderService.java (Service logic)
 IdentityProviderTool.java (3 @Tool methods)
 authentication/
 AuthenticationService.java (Service logic)
 AuthenticationTool.java (6 @Tool methods)

Total: 14 files for tools layer
```

### After: Centralized in Single File

```
src/main/java/dev/shaaf/keycloak/mcp/server/
 KeycloakTool.java (1 @Tool method, 45+ operations)

 user/
 UserService.java (Service logic - unchanged)
 realm/
 RealmService.java (Service logic - unchanged)
 client/
 ClientService.java (Service logic - unchanged)
 role/
 RoleService.java (Service logic - unchanged)
 group/
 GroupService.java (Service logic - unchanged)
 idp/
 IdentityProviderService.java (Service logic - unchanged)
 authentication/
 AuthenticationService.java (Service logic - unchanged)

Total: 1 file for tools layer
```

## Complexity Metrics

### Before
- **Tool Classes**: 7
- **Tool Methods**: 37+
- **MCP Exposed Tools**: 37+
- **Lines of Tool Code**: ~800 (scattered)
- **Error Handling Patterns**: 7 (one per class)
- **JSON Serialization Points**: 37+

### After
- **Tool Classes**: 1
- **Tool Methods**: 1
- **MCP Exposed Tools**: 1
- **Lines of Tool Code**: ~450 (centralized)
- **Error Handling Patterns**: 1 (unified)
- **JSON Serialization Points**: 1

## Performance Characteristics

```

 Operation Overhead


 Before (Direct Method Call):
 MCP → Tool Method → Service Method
 Overhead: ~0μs (direct call)

 After (Unified Tool):
 MCP → Switch → Service Method
 Overhead: <1μs (switch statement O(1))

 JSON Parsing:
 100-500μs (depends on parameter size)

 Service Execution:
 1-1000ms (majority of execution time)

 Total Additional Overhead: <0.1% in typical use


```

## Extension Pattern

### Adding a New Operation

```

 Step 1: Add Enum Value


 public enum KeycloakOperation {
 // ... existing operations
 MY_NEW_OPERATION
 }





 Step 2: Add Switch Case


 switch (operation) {
 // ... existing cases
 case MY_NEW_OPERATION:
 return myService.myNewMethod(
 paramsNode.get("param1").asText()
 );
 }





 Step 3: Update Tool Description


 @Tool(description = "... MY_NEW_OPERATION; ...")




 DONE
```

## Summary

The Parametric Collapse transformation:

**Reduced**:
- 7 tool classes → 1 tool class
- 37+ MCP tools → 1 MCP tool
- ~800 lines of scattered code → ~450 lines centralized
- 7 error handling patterns → 1 unified pattern

**Maintained**:
- All 45+ operations
- All service layer code (0 changes)
- All functionality
- Performance characteristics

**Improved**:
- API simplicity for AI models
- Code maintainability
- Error handling consistency
- Extension ease
- Documentation clarity

---

**Result**: Clean, maintainable, scalable architecture

