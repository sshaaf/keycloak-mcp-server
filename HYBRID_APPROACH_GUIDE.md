# Hybrid Architecture Implementation Guide

**Date**: 2026-03-09
**Goal**: Mix service architecture with command pattern for best of both worlds

## Overview

This guide shows how to implement a hybrid approach that keeps the benefits of both architectures:
- ✅ Keep services for simple CRUD operations
- ✅ Add command pattern for complex operations
- ✅ Gradual migration without disrupting current progress

---

## Architecture

```
KeycloakTool (MCP Tool)
    ├── CommandRegistry (optional layer)
    │   └── Auto-discovers @KeycloakCommand beans
    │
    ├── Services (existing - for simple operations)
    │   ├── UserService
    │   ├── ClientService
    │   └── ClientScopeService
    │
    └── Commands (new - for complex operations)
        ├── BulkUserCreationCommand
        ├── ProvisionClientWithScopesCommand
        └── ComplexAuthFlowCommand
```

---

## Step 1: Create Command Infrastructure

### 1.1 Create KeycloakCommand Interface

```java
// src/main/java/dev/shaaf/keycloak/mcp/server/command/KeycloakCommand.java
package dev.shaaf.keycloak.mcp.server.command;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.keycloak.mcp.server.KeycloakOperation;

public interface KeycloakCommand {

    /**
     * The operation this command handles
     */
    KeycloakOperation getOperation();

    /**
     * Execute the command with given parameters
     *
     * @param params JSON parameters from the MCP request
     * @return Result string (typically JSON or message)
     * @throws Exception if execution fails
     */
    String execute(JsonNode params) throws Exception;

    /**
     * Human-readable description for documentation
     */
    default String getDescription() {
        return "Executes " + getOperation().name();
    }

    /**
     * Required parameter names for validation
     */
    default String[] getRequiredParams() {
        return new String[0];
    }
}
```

### 1.2 Create @RegisteredCommand Qualifier

```java
// src/main/java/dev/shaaf/keycloak/mcp/server/command/RegisteredCommand.java
package dev.shaaf.keycloak.mcp.server.command;

import jakarta.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Qualifier
@Retention(RUNTIME)
@Target({TYPE, METHOD, FIELD, PARAMETER})
public @interface RegisteredCommand {
}
```

### 1.3 Create CommandRegistry

```java
// src/main/java/dev/shaaf/keycloak/mcp/server/command/CommandRegistry.java
package dev.shaaf.keycloak.mcp.server.command;

import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import io.quarkus.logging.Log;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class CommandRegistry {

    private final Map<KeycloakOperation, KeycloakCommand> commands = new EnumMap<>(KeycloakOperation.class);

    @Inject
    @RegisteredCommand
    Instance<KeycloakCommand> discoveredCommands;

    @PostConstruct
    void initialize() {
        for (KeycloakCommand command : discoveredCommands) {
            commands.put(command.getOperation(), command);
            Log.infof("Registered command: %s", command.getOperation());
        }

        Log.infof("CommandRegistry initialized with %d commands", commands.size());
    }

    /**
     * Get command for an operation
     *
     * @param operation The operation to look up
     * @return Optional containing command if registered
     */
    public Optional<KeycloakCommand> getCommand(KeycloakOperation operation) {
        return Optional.ofNullable(commands.get(operation));
    }

    /**
     * Check if a command is registered for an operation
     *
     * @param operation The operation to check
     * @return true if command is registered
     */
    public boolean hasCommand(KeycloakOperation operation) {
        return commands.containsKey(operation);
    }

    /**
     * Get count of registered commands
     */
    public int getCommandCount() {
        return commands.size();
    }
}
```

### 1.4 Create AbstractCommand Base Class

```java
// src/main/java/dev/shaaf/keycloak/mcp/server/command/AbstractCommand.java
package dev.shaaf.keycloak.mcp.server.command;

import com.fasterxml.jackson.databind.JsonNode;
import io.quarkiverse.mcp.server.ToolCallException;

/**
 * Base class for commands with common validation utilities
 */
public abstract class AbstractCommand implements KeycloakCommand {

    /**
     * Get required string parameter
     */
    protected String requireString(JsonNode params, String paramName) {
        if (!params.has(paramName)) {
            throw new ToolCallException("Missing required parameter: " + paramName);
        }
        String value = params.get(paramName).asText();
        if (value == null || value.isBlank()) {
            throw new ToolCallException("Parameter " + paramName + " cannot be empty");
        }
        return value;
    }

    /**
     * Get optional string parameter with default
     */
    protected String optionalString(JsonNode params, String paramName, String defaultValue) {
        if (!params.has(paramName)) {
            return defaultValue;
        }
        return params.get(paramName).asText(defaultValue);
    }

    /**
     * Get required integer parameter
     */
    protected int requireInt(JsonNode params, String paramName) {
        if (!params.has(paramName)) {
            throw new ToolCallException("Missing required parameter: " + paramName);
        }
        return params.get(paramName).asInt();
    }

    /**
     * Get optional boolean parameter with default
     */
    protected boolean optionalBoolean(JsonNode params, String paramName, boolean defaultValue) {
        if (!params.has(paramName)) {
            return defaultValue;
        }
        return params.get(paramName).asBoolean(defaultValue);
    }
}
```

---

## Step 2: Update KeycloakTool

### 2.1 Add CommandRegistry Injection

```java
@Inject
CommandRegistry commandRegistry;
```

### 2.2 Update executeKeycloakOperation Method

```java
@Tool(description = "...")
public String executeKeycloakOperation(
        @ToolArg(description = "...") KeycloakOperation operation,
        @ToolArg(description = "...") String params) {

    try {
        JsonNode paramsNode = mapper.readTree(params);

        // HYBRID ROUTING: Check for registered command first
        Optional<KeycloakCommand> command = commandRegistry.getCommand(operation);
        if (command.isPresent()) {
            Log.debugf("Executing command for %s", operation);
            return command.get().execute(paramsNode);
        }

        // Fall back to traditional service routing
        switch (operation) {
            // Existing cases remain unchanged
            case GET_USERS:
                return mapper.writeValueAsString(
                        userService.getUsers(paramsNode.get("realm").asText())
                );

            // ... all other existing cases ...

            default:
                throw new ToolCallException("Unknown operation: " + operation);
        }

    } catch (Exception e) {
        Log.error("Failed to execute Keycloak operation: " + operation, e);
        throw new ToolCallException("Failed to execute operation " + operation + ": " + e.getMessage());
    }
}
```

---

## Step 3: Create Example Complex Command

### 3.1 Add Operation to Enum

```java
// In KeycloakOperation enum
BULK_CREATE_USERS,  // Complex command example
```

### 3.2 Implement Command

```java
// src/main/java/dev/shaaf/keycloak/mcp/server/command/user/BulkCreateUsersCommand.java
package dev.shaaf.keycloak.mcp.server.command.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import dev.shaaf.keycloak.mcp.server.command.AbstractCommand;
import dev.shaaf.keycloak.mcp.server.command.RegisteredCommand;
import dev.shaaf.keycloak.mcp.server.user.UserService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;

/**
 * Complex command that creates multiple users in one operation.
 * This demonstrates when commands are useful:
 * - Multiple service calls
 * - Transaction handling
 * - Complex validation
 */
@ApplicationScoped
@RegisteredCommand
public class BulkCreateUsersCommand extends AbstractCommand {

    @Inject
    UserService userService;

    @Inject
    ObjectMapper mapper;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.BULK_CREATE_USERS;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "users"};
    }

    @Override
    public String getDescription() {
        return "Create multiple users in a single operation with transaction-like behavior";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        String realm = requireString(params, "realm");
        JsonNode usersNode = params.get("users");

        if (!usersNode.isArray()) {
            throw new IllegalArgumentException("users must be an array");
        }

        List<String> results = new ArrayList<>();
        List<String> failures = new ArrayList<>();

        for (JsonNode userNode : usersNode) {
            try {
                String username = userNode.get("username").asText();
                String firstName = userNode.get("firstName").asText();
                String lastName = userNode.get("lastName").asText();
                String email = userNode.get("email").asText();
                String password = userNode.get("password").asText();

                String result = userService.addUser(realm, username, firstName, lastName, email, password);

                if (result.contains("Successfully")) {
                    results.add(username + ": " + result);
                } else {
                    failures.add(username + ": " + result);
                }
            } catch (Exception e) {
                failures.add("Error processing user: " + e.getMessage());
            }
        }

        // Build comprehensive result
        StringBuilder response = new StringBuilder();
        response.append("Bulk user creation results:\n");
        response.append("Successful: ").append(results.size()).append("\n");
        response.append("Failed: ").append(failures.size()).append("\n\n");

        if (!results.isEmpty()) {
            response.append("Successes:\n");
            results.forEach(r -> response.append("  - ").append(r).append("\n"));
        }

        if (!failures.isEmpty()) {
            response.append("\nFailures:\n");
            failures.forEach(f -> response.append("  - ").append(f).append("\n"));
        }

        return response.toString();
    }
}
```

### 3.3 Create Test

```java
// src/test/java/dev/shaaf/keycloak/mcp/server/command/user/BulkCreateUsersCommandTest.java
package dev.shaaf.keycloak.mcp.server.command.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import dev.shaaf.keycloak.mcp.server.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BulkCreateUsersCommandTest {

    @Mock
    private UserService userService;

    private BulkCreateUsersCommand command;
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        command = new BulkCreateUsersCommand();
        command.userService = userService;
        mapper = new ObjectMapper();
        command.mapper = mapper;
    }

    @Test
    void testGetOperation() {
        assertThat(command.getOperation()).isEqualTo(KeycloakOperation.BULK_CREATE_USERS);
    }

    @Test
    void testExecute_Success() throws Exception {
        // Given
        String params = """
            {
              "realm": "test-realm",
              "users": [
                {
                  "username": "user1",
                  "firstName": "First1",
                  "lastName": "Last1",
                  "email": "user1@test.com",
                  "password": "pass1"
                },
                {
                  "username": "user2",
                  "firstName": "First2",
                  "lastName": "Last2",
                  "email": "user2@test.com",
                  "password": "pass2"
                }
              ]
            }
            """;

        when(userService.addUser(anyString(), anyString(), anyString(), anyString(), anyString(), anyString()))
            .thenReturn("Successfully created user");

        // When
        String result = command.execute(mapper.readTree(params));

        // Then
        assertThat(result).contains("Successful: 2");
        assertThat(result).contains("Failed: 0");
    }
}
```

---

## Step 4: When to Use Each Pattern

### Use Service (Direct Routing) ✅

**Good for:**
- Simple CRUD operations
- Single service involvement
- Straightforward parameter mapping

**Examples:**
```java
case GET_USERS:
    return mapper.writeValueAsString(
        userService.getUsers(paramsNode.get("realm").asText())
    );

case CREATE_CLIENT_SCOPE:
    return clientScopeService.createClientScope(
        paramsNode.get("realm").asText(),
        mapper.treeToValue(paramsNode.get("clientScope"), ClientScopeRepresentation.class)
    );
```

### Use Command ⚡

**Good for:**
- Complex multi-service operations
- Bulk/batch operations
- Special validation requirements
- Orchestration logic
- Transaction-like behavior

**Examples:**
- BULK_CREATE_USERS (multiple service calls)
- PROVISION_CLIENT_WITH_SCOPES (client + multiple scopes)
- MIGRATE_AUTHENTICATION_FLOW (complex orchestration)

---

## Step 5: Gradual Migration Path

### Phase 1: Infrastructure (1-2 days)
- ✅ Create command interfaces and registry
- ✅ Update KeycloakTool with hybrid routing
- ✅ Add tests for infrastructure

### Phase 2: First Command (1 day)
- ✅ Implement one complex command (e.g., bulk operation)
- ✅ Test integration with existing services
- ✅ Verify both patterns work together

### Phase 3: Evaluate (ongoing)
- Monitor which pattern works better for different operations
- Create commands for new complex operations
- Keep simple operations in services

### Phase 4: Scale (as needed)
- Add more commands when beneficial
- Never feel obligated to move all operations to commands
- Keep the hybrid approach indefinitely if it works well

---

## Benefits of Hybrid Approach

### ✅ Best of Both Worlds
- Keep services for domain logic
- Add commands for complex operations
- No forced migration

### ✅ Gradual Adoption
- Start with one command
- Add more as needed
- No disruption to current progress

### ✅ Flexibility
- Choose the right pattern for each operation
- Not locked into one approach

### ✅ No Breaking Changes
- Existing operations continue to work
- Tests remain valid
- Progressive enhancement

---

## Example Usage

### Current State (Phase 2: 85 operations)
```
┌─────────────────────────┐
│ CommandRegistry (0)     │ ← No commands yet
└─────────────────────────┘

┌─────────────────────────┐
│ Services (85 ops)       │ ← All operations
│ - UserService           │
│ - ClientService         │
│ - ClientScopeService    │
│ - ...                   │
└─────────────────────────┘
```

### After Adding Hybrid (Phase 3+)
```
┌─────────────────────────┐
│ CommandRegistry (5)     │ ← Complex operations
│ - BulkCreateUsers       │
│ - ProvisionClient       │
│ - MigrateAuthFlow       │
│ - ...                   │
└─────────────────────────┘

┌─────────────────────────┐
│ Services (80 ops)       │ ← Simple CRUD operations
│ - UserService           │
│ - ClientService         │
│ - ClientScopeService    │
│ - ...                   │
└─────────────────────────┘
```

---

## Summary

The hybrid approach offers:

1. **Keep what works**: Services for simple operations
2. **Add flexibility**: Commands for complex operations
3. **No migration required**: Gradual adoption
4. **Best tool for each job**: Choose the right pattern

**Recommendation**:
- ✅ Implement infrastructure in Phase 3
- ✅ Start with one complex command
- ✅ Evaluate and expand as needed
- ✅ Never feel forced to migrate all operations

---

**Created**: 2026-03-09
**Status**: Ready to implement (optional)
**Effort**: 2-3 days for infrastructure + first command
**Risk**: Low (additive, no breaking changes)
