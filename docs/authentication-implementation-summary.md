# JWT Bearer Token Authentication Implementation Summary

## Overview

Implemented user-based authentication for the Keycloak MCP Server using JWT Bearer tokens. This eliminates the security flaw where users could access Keycloak without proper authentication and enables Keycloak's native permission enforcement.

**Implementation Date**: November 21, 2024
**Version**: 0.4.0
**Status**: Complete

---

## Problem Statement

### Security Requirement

All MCP server operations must enforce user-specific permissions through Keycloak's native access control system.

### Solution

Implement JWT Bearer token authentication where:
- Each user obtains their own JWT token from Keycloak
- The MCP server validates the token
- The MCP server uses the user's token for all Keycloak API calls
- Keycloak enforces permissions naturally

---

## Architecture

### Authentication Flow

```
User → Keycloak (authenticate)
 ↓
User receives JWT token
 ↓
User → MCP Server (with JWT in Authorization header)
 ↓
MCP Server validates JWT token (OIDC)
 ↓
MCP Server → Keycloak Admin API (using user's token)
 ↓
Keycloak enforces user's permissions
```

### Key Principles

* **User tokens only**: No shared credentials or service accounts
* **Native permissions**: Keycloak enforces its own access control
* **Request-scoped**: Each request uses the authenticated user's token
* **Stateless**: No session management required

---

## Implementation Details

### 1. Dependencies Added

**File**: `pom.xml`

```xml
<!-- Security and Authentication -->
<dependency>
  <groupId>io.quarkus</groupId>
  <artifactId>quarkus-oidc</artifactId>
</dependency>
<dependency>
  <groupId>io.quarkus</groupId>
  <artifactId>quarkus-security</artifactId>
</dependency>
<dependency>
  <groupId>io.quarkus</groupId>
  <artifactId>quarkus-smallrye-health</artifactId>
</dependency>
```

**Purpose**: Enable OIDC token validation, security policies, and health checks

---

### 2. OIDC Configuration

**File**: `src/main/resources/application.properties`

```properties
# OIDC Configuration - Token Validation
quarkus.oidc.auth-server-url=${KC_URL}/realms/${KC_REALM:master}
quarkus.oidc.client-id=${OIDC_CLIENT_ID:mcp-server}
quarkus.oidc.application-type=service
quarkus.oidc.token.issuer=${KC_URL}/realms/${KC_REALM:master}

# Security Policies - Require authentication for MCP endpoints
quarkus.http.auth.permission.mcp.paths=/mcp/*
quarkus.http.auth.permission.mcp.policy=authenticated

# Public endpoints (health checks, metrics)
quarkus.http.auth.permission.public.paths=/q/*
quarkus.http.auth.permission.public.policy=permit
```

**Purpose**:
- Validate incoming JWT tokens using OIDC discovery
- Require authentication for MCP endpoints
- Allow public access to health checks

---

### 3. Keycloak Client Factory

**File**: `src/main/java/dev/shaaf/keycloak/mcp/server/KeycloakClientFactory.java` (NEW)

**Purpose**: Create Keycloak admin clients using the authenticated user's JWT token

**Key Features**:
- **Request-scoped** (creates client per request)
- **Uses authenticated user's JWT token** for all operations
- **Validates user authentication** before creating client

**Code Structure**:

```java
@RequestScoped
public class KeycloakClientFactory {
    
    @Inject SecurityIdentity securityIdentity;
    @Inject JsonWebToken jwt;
    
    public Keycloak createClient() {
        // Requires user authentication
        if (!securityIdentity.isAnonymous() && jwt != null) {
            return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .authorization("Bearer " + jwt.getRawToken())
                .build();
        }
        
        throw new IllegalStateException("Authentication required");
    }
}
```

---

### 4. Service Updates

**Updated Files**: All 7 service classes:
- `UserService.java`
- `RealmService.java`
- `ClientService.java`
- `RoleService.java`
- `GroupService.java`
- `IdentityProviderService.java`
- `AuthenticationFlowService.java`

**Changes**:
- Replaced direct `Keycloak` injection with `KeycloakClientFactory`
- Updated all methods to use `clientFactory.createClient()`
- Added proper error handling for authentication failures

---

### 5. Token Helper Script

**File**: `scripts/get-mcp-token.sh`

**Purpose**: Help users obtain JWT tokens and generate MCP configuration

**Features**:
- Authenticates with Keycloak
- Retrieves JWT token
- Generates Cursor MCP configuration
- Shows token expiration time
- Displays user roles

**Usage**:

```bash
./scripts/get-mcp-token.sh \
  --keycloak-url https://keycloak.example.com \
  --username your-username \
  --password your-password
```

---

### 6. Deployment Configuration

**File**: `deploy/openshift/deployment.yaml`

**Environment Variables**:

```yaml
env:
- name: KC_URL
  valueFrom:
    configMapKeyRef:
      name: keycloak-mcp-config
      key: keycloak-url
- name: KC_REALM
  valueFrom:
    configMapKeyRef:
      name: keycloak-mcp-config
      key: keycloak-realm
- name: OIDC_CLIENT_ID
  valueFrom:
    configMapKeyRef:
      name: keycloak-mcp-config
      key: client-id
```

**Note**: No secrets required. Token validation uses public OIDC discovery.

---

## User Configuration

### MCP Client Configuration

**File**: `~/.cursor/mcp.json`

```json
{
  "mcpServers": {
    "keycloak": {
      "transport": "sse",
      "url": "https://mcp-server.example.com/mcp/sse",
      "headers": {
        "Authorization": "Bearer <user-jwt-token>"
      }
    }
  }
}
```

### Obtaining Token

Users obtain their token using the helper script:

```bash
./scripts/get-mcp-token.sh \
  --keycloak-url https://keycloak.example.com \
  --username their-username \
  --password their-password
```

The script outputs the complete MCP configuration with the token.

---

## Security Benefits

### 1. User-Specific Permissions

Each user's permissions are enforced by Keycloak:

* **Realm Access**: Users only see realms they have access to
* **Role-Based Access**: Admin, manager, viewer roles work naturally
* **Client Access**: Limited to authorized clients
* **Operation Restrictions**: Based on assigned roles

### 2. No Shared Credentials

* No service account secrets in deployment
* No shared admin passwords
* Each user authenticates individually
* Tokens expire automatically

### 3. Full Audit Trail

Keycloak logs show:

* Which user performed each action
* When the action occurred
* What operations were performed
* Success or failure status

### 4. Native Access Control

* Uses Keycloak's built-in permission system
* No duplicate authorization logic in MCP server
* Consistent with other Keycloak clients
* Leverage existing role assignments

---

## Testing and Validation

### Validation Steps

1. **Deploy Updated MCP Server**
   ```bash
   oc apply -f deploy/openshift/
   ```

2. **User Obtains Token**
   ```bash
   ./scripts/get-mcp-token.sh --keycloak-url <url> --username <user>
   ```

3. **Configure MCP Client**
   Update `~/.cursor/mcp.json` with user token

4. **Test Operations**
   * List realms - should only show accessible realms
   * List users - should respect permissions
   * Attempt restricted operation - should fail appropriately

### Test Results

* User with admin role: Full access to all operations
* User with view-only role: Can read but not modify
* User without realm access: Cannot see other realms
* Expired token: Returns 401 Unauthorized

---

## Documentation

### New Documentation Created

1. **AUTHENTICATION.md** - Complete authentication guide
2. **AUTHENTICATION_implementation-summary.md** - This document
3. **Updated getting-started.md** - JWT authentication instructions
4. **Updated openshift-deployment.md** - Simplified deployment (no secrets)

### Documentation Updates

* Removed all service account references
* Updated environment variable lists
* Added token management instructions
* Included troubleshooting guides

---

## Migration Impact

### Breaking Changes

* Users must now obtain their own JWT tokens
* MCP client configuration requires Authorization header
* No unauthenticated access to MCP endpoints

### Migration Path

1. Deploy updated MCP server
2. Users run token helper script
3. Users update their MCP configuration
4. Users reload MCP servers in client

---

## Technical Achievements

### Code Quality

* Simplified authentication logic (removed fallback code)
* Better separation of concerns
* Request-scoped client creation
* Proper error handling

### Security Improvements

* Eliminated shared credential vulnerability
* Enforces principle of least privilege
* Provides full audit trail
* Uses industry-standard OAuth2/OIDC

### Operational Benefits

* No secrets in deployment manifests
* Simplified configuration
* Better user experience (own permissions)
* Easier to debug (per-user operations)

---

## Summary

Successfully implemented JWT Bearer token authentication for the Keycloak MCP Server. The implementation:

1. **Eliminates security flaws** - no shared admin access
2. **Enforces user permissions** - Keycloak's native access control
3. **Provides audit trail** - all actions under user identity
4. **Simplifies deployment** - no secrets required
5. **Includes tooling** - helper scripts for easy adoption
6. **Documented thoroughly** - guides for users and administrators

The MCP server now operates as a transparent, authenticated proxy that enforces Keycloak's existing permission system, making it suitable for production use with proper access control.

---

**Status**: Implementation Complete
**Version**: 0.4.0
**Date**: November 21, 2024
