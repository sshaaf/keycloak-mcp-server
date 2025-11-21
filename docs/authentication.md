# Keycloak MCP Server Authentication

## Overview

The Keycloak MCP Server implements **user-based authentication** using JWT Bearer tokens. This means:

 **Each user authenticates with their own credentials**
 **Keycloak enforces its existing permission system**
 **No duplicate permission logic in the MCP server**
 **Realm isolation works automatically**
 **Role-based access control (RBAC) works naturally**

## How It Works

```

 User MCP Server Keycloak
(Cursor)


 1. Get JWT Token
 >
 <
 JWT Token (with user's permissions)

 2. MCP Request + JWT Token
 >
 3. Validate Token
 >
 <
 Token Valid

 4. Call Admin API (user token)
 >

 5. Keycloak checks permissions
 - Realm access?
 - Role permissions?
 - Client access?
 <
 < Response (filtered)
 MCP Response
```

### Key Benefits

1. **Automatic Permission Enforcement**
 - The MCP server uses the user's token to call Keycloak
 - Keycloak naturally enforces its own permissions
 - No need to duplicate permission logic

2. **Realm Isolation**
 - Users can only access realms they have permissions for
 - Read-only users can't modify resources
 - Admin users have full access

3. **Audit Trail**
 - All actions are logged under the user's identity
 - No shared admin account
 - Clear accountability

## Authentication Methods

### 1. User Token Authentication (Production)

**Recommended for:** Production deployments, multi-user environments

**How it works:**
- User authenticates with Keycloak and gets a JWT token
- Token is added to Cursor MCP configuration
- MCP server validates token and uses it for Keycloak API calls
- Keycloak enforces user's permissions

**Setup:**

```bash
# Get your personal token
./scripts/get-mcp-token.sh \
 --keycloak-url https://keycloak.example.com \
 --username alice \
 --password alice-password

# Add to ~/.cursor/mcp.json
{
 "mcpServers": {
 "keycloak": {
 "transport": "sse",
 "url": "https://mcp-server.example.com/mcp/sse",
 "headers": {
 "Authorization": "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..."
 }
 }
 }
}
```

## Configuration

### Application Properties

```properties
# ============================================================
# AUTHENTICATION & AUTHORIZATION
# ============================================================

# OIDC Configuration - Token Validation
quarkus.oidc.auth-server-url=${KC_URL}/realms/${KC_REALM:master}
quarkus.oidc.client-id=${OIDC_CLIENT_ID:mcp-server}
quarkus.oidc.credentials.secret=${OIDC_CLIENT_SECRET:}
quarkus.oidc.application-type=service
quarkus.oidc.token.issuer=${KC_URL}/realms/${KC_REALM:master}

# Security Policies - Require authentication for MCP endpoints
quarkus.http.auth.permission.mcp.paths=/mcp/*
quarkus.http.auth.permission.mcp.policy=authenticated

# Public endpoints (health checks, metrics)
quarkus.http.auth.permission.public.paths=/q/*
quarkus.http.auth.permission.public.policy=permit

# Development Mode - Disable authentication for local dev
%dev.quarkus.http.auth.permission.mcp.policy=permit
%dev.quarkus.oidc.enabled=false
```

### Environment Variables

| Variable | Description | Required | Example |
|----------|-------------|----------|---------|
| `KC_URL` | Keycloak server URL | Yes | `https://keycloak.example.com` |
| `KC_REALM` | Default Keycloak realm | No | `master` (default) |
| `OIDC_CLIENT_ID` | OIDC client ID for token validation | No | `mcp-server` (default) |

## Getting Your Token

### Method 1: Using the Helper Script

```bash
./scripts/get-mcp-token.sh \
 --keycloak-url https://keycloak.example.com \
 --realm master \
 --username your-username \
 --password your-password \
 --mcp-url https://mcp-server.example.com/mcp/sse
```

The script will:
1. Authenticate with Keycloak
2. Get your JWT token
3. Generate Cursor MCP configuration
4. Show token expiration time
5. Display your realm roles and permissions

### Method 2: Manual Token Request

```bash
# Get token
TOKEN=$(curl -X POST \
 https://keycloak.example.com/realms/master/protocol/openid-connect/token \
 -d 'grant_type=password' \
 -d 'client_id=admin-cli' \
 -d 'username=your-username' \
 -d 'password=your-password' | jq -r '.access_token')

# Use in Cursor config
echo $TOKEN
```

## Token Management

### Token Expiration

JWT tokens have a limited lifespan (typically 5-60 minutes). When your token expires:

1. **Symptoms:**
 - MCP operations fail with `401 Unauthorized`
 - "Token expired" errors in logs

2. **Solution:**
 - Generate a new token
 - Update `~/.cursor/mcp.json`
 - Reload MCP servers in Cursor

### Token Refresh

If you have a refresh token:

```bash
# Refresh your token
NEW_TOKEN=$(curl -X POST \
 https://keycloak.example.com/realms/master/protocol/openid-connect/token \
 -d 'grant_type=refresh_token' \
 -d 'client_id=admin-cli' \
 -d 'refresh_token=your-refresh-token' | jq -r '.access_token')
```

### Long-Lived Tokens (Development Only)

For development convenience, you can increase token lifespan in Keycloak:

1. Keycloak Admin Console
2. Realm Settings → Tokens tab
3. Access Token Lifespan → Set to desired duration (e.g., 24 hours)

 **Warning:** Long-lived tokens are a security risk in production!

## Permission Examples

### Scenario 1: Admin User

**User**: Alice (admin role in master realm)

**What Alice can do:**
```bash
 List all realms
 Create/delete realms
 Manage users in all realms
 Configure clients
 Manage authentication flows
 Everything!
```

**Token claims:**
```json
{
 "realm_access": {
 "roles": ["admin", "create-realm", "offline_access", "uma_authorization"]
 },
 "resource_access": {
 "master-realm": {
 "roles": ["view-realm", "manage-users", "manage-clients", ...]
 }
 }
}
```

### Scenario 2: Realm-Specific User

**User**: Bob (manager role in `quarkus` realm only)

**What Bob can do:**
```bash
 List realm: quarkus
 Manage users in quarkus realm
 View clients in quarkus realm
 Access master realm
 Access other realms
 Create new realms
```

**Token claims:**
```json
{
 "realm_access": {
 "roles": ["offline_access"]
 },
 "resource_access": {
 "quarkus-realm": {
 "roles": ["view-realm", "manage-users", "view-clients"]
 }
 }
}
```

### Scenario 3: Read-Only User

**User**: Carol (viewer role)

**What Carol can do:**
```bash
 List realms (that she has access to)
 View users
 View clients
 View authentication flows
 Create/delete/update anything
```

**Token claims:**
```json
{
 "realm_access": {
 "roles": ["offline_access"]
 },
 "resource_access": {
 "master-realm": {
 "roles": ["view-realm", "view-users", "view-clients"]
 }
 }
}
```

## Security Best Practices

### 1. Use Strong Passwords

```bash
# Bad
--password admin

# Good
--password "Str0ng!P@ssw0rd#2024"
```

### 2. Rotate Tokens Regularly

```bash
# Set shorter token lifespan in Keycloak
Access Token Lifespan: 5 minutes (for sensitive operations)
Refresh Token Lifespan: 30 minutes
```

### 3. Use Different Accounts per Environment

```
Development: dev-user
Staging: staging-user
Production: prod-user
```

### 4. Monitor Token Usage

Check Keycloak's admin console:
- Events → Login Events
- Monitor failed authentication attempts
- Review active sessions

### 5. Revoke Compromised Tokens

If a token is compromised:

```bash
# Logout user (revokes all tokens)
curl -X POST \
 https://keycloak.example.com/realms/master/protocol/openid-connect/logout \
 -d 'client_id=admin-cli' \
 -d 'refresh_token=compromised-token'
```

## Development Mode

For local development without authentication:

```bash
# Start in dev mode
mvn quarkus:dev

# No authentication required
curl http://localhost:8080/mcp/sse
```

Dev mode automatically:
- Disables OIDC authentication
- Allows unauthenticated access for development convenience

## Troubleshooting

### Issue: 401 Unauthorized

**Cause:** Token is invalid or expired

**Solution:**
1. Check token expiration:
 ```bash
 echo "$TOKEN" | cut -d'.' -f2 | base64 -d | jq '.exp'
 ```

2. Generate new token:
 ```bash
 ./scripts/get-mcp-token.sh --keycloak-url ... --username ... --password ...
 ```

3. Update Cursor config and reload MCP servers

### Issue: 403 Forbidden

**Cause:** User doesn't have required permissions

**Solution:**
1. Check your Keycloak roles:
 ```bash
 # In Keycloak Admin Console
 Users → Your User → Role Mappings
 ```

2. Assign necessary roles:
 - `admin` - Full access
 - `manage-users` - User management
 - `view-realm` - Read-only access

### Issue: Token Validation Failed

**Cause:** OIDC configuration mismatch

**Solution:**
1. Verify environment variables:
 ```bash
 echo $KC_URL
 echo $KC_REALM
 echo $OIDC_CLIENT_ID
 ```

2. Check MCP server logs:
 ```bash
 # Look for OIDC validation errors
 oc logs deployment/keycloak-mcp-server | grep OIDC
 ```

3. Ensure Keycloak issuer matches:
 ```bash
 curl -s $KC_URL/realms/$KC_REALM/.well-known/openid-configuration | jq .issuer
 ```

### Issue: Different User Than Expected

**Cause:** Token belongs to different user

**Solution:**
1. Decode your token to see who you are:
 ```bash
 echo "$TOKEN" | cut -d'.' -f2 | base64 -d | jq .preferred_username
 ```

2. Ensure you are using the correct username when getting token

## Summary

 **User Authentication**: Each user gets their own JWT token
 **Permission Enforcement**: Keycloak handles all authorization
 **Realm Isolation**: Users can only access their authorized realms
 **Audit Trail**: All actions logged under user identity
 **No Duplicate Logic**: No permission code in MCP server
 **Standard OAuth2**: Industry-standard authentication
 **Simple & Secure**: No shared credentials or service accounts

---

**For more information:**
- [getting-started.md](getting-started.md) - Setup instructions
- [get-mcp-token.sh](https://github.com/sshaaf/keycloak-mcp-server/blob/main/scripts/get-mcp-token.sh) - Token helper script
- [openshift-deployment.md](openshift-deployment.md) - Production deployment guide

**Happy Keycloak management! **

