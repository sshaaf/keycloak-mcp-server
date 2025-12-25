# Authentication

The Keycloak MCP Server uses JWT Bearer token authentication. Each user authenticates with their own Keycloak credentials, and Keycloak enforces its native permission system.

## How It Works

```
User                    MCP Server               Keycloak
  │                         │                        │
  │──── Get JWT Token ──────┼────────────────────────>│
  │<─────────────────────────────── JWT Token ────────│
  │                         │                        │
  │── MCP Request + JWT ───>│                        │
  │                         │──── Validate Token ───>│
  │                         │<─── Token Valid ───────│
  │                         │                        │
  │                         │── API Call (user JWT) ─>│
  │                         │<── Response (filtered) ─│
  │<── MCP Response ────────│                        │
```

**Key benefits:**
- Keycloak enforces its own permissions - no duplicate logic
- Users only see resources they have access to
- Full audit trail under each user's identity
- No shared service accounts

## Getting Your Token

### Using the Helper Script

```bash
./scripts/get-mcp-token.sh \
  --keycloak-url https://keycloak.example.com \
  --realm master \
  --username your-username \
  --password your-password \
  --mcp-url https://mcp-server.example.com/mcp/sse
```

The script outputs ready-to-use Cursor MCP configuration.

### Manual Token Request

```bash
TOKEN=$(curl -X POST \
  https://keycloak.example.com/realms/master/protocol/openid-connect/token \
  -d 'grant_type=password' \
  -d 'client_id=admin-cli' \
  -d 'username=your-username' \
  -d 'password=your-password' | jq -r '.access_token')

echo $TOKEN
```

## MCP Client Configuration

Add your token to your MCP client configuration:

```json
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

## Token Management

### Token Expiration

JWT tokens expire (typically 5-60 minutes). When expired:
- Operations fail with `401 Unauthorized`
- Generate a new token and update your configuration
- Reload MCP servers in your client

### Token Refresh

If you have a refresh token:

```bash
NEW_TOKEN=$(curl -X POST \
  https://keycloak.example.com/realms/master/protocol/openid-connect/token \
  -d 'grant_type=refresh_token' \
  -d 'client_id=admin-cli' \
  -d 'refresh_token=your-refresh-token' | jq -r '.access_token')
```

### Extending Token Lifespan (Development)

In Keycloak Admin Console:
1. Realm Settings → Tokens tab
2. Access Token Lifespan → Set desired duration

**Warning:** Long-lived tokens are a security risk in production.

## Permission Examples

### Admin User

A user with `admin` role in master realm can:
- List all realms
- Create/delete realms
- Manage users in all realms
- Configure clients
- Manage authentication flows

### Realm-Specific User

A user with manager role in `quarkus` realm only can:
- Manage users in quarkus realm
- View clients in quarkus realm
- **Cannot** access master or other realms
- **Cannot** create new realms

### Read-Only User

A user with only `view-*` roles can:
- List realms they have access to
- View users, clients, flows
- **Cannot** create, update, or delete anything

## Development Mode

For local development without authentication:

```bash
mvn quarkus:dev
```

Dev mode automatically disables OIDC authentication.

## Troubleshooting

### 401 Unauthorized

Token is invalid or expired.

```bash
# Check token expiration
echo "$TOKEN" | cut -d'.' -f2 | base64 -d | jq '.exp'

# Generate new token
./scripts/get-mcp-token.sh ...
```

### 403 Forbidden

User doesn't have required permissions.

1. Check your Keycloak roles: Users → Your User → Role Mappings
2. Assign necessary roles: `admin`, `manage-users`, `view-realm`, etc.

### Token Validation Failed

OIDC configuration mismatch.

```bash
# Verify environment
echo $KC_URL
echo $KC_REALM

# Check OIDC discovery
curl -s $KC_URL/realms/$KC_REALM/.well-known/openid-configuration | jq .issuer
```

## Security Best Practices

1. **Rotate tokens regularly** - Use short-lived tokens in production
2. **Use HTTPS** - Always use TLS for token transmission
3. **Monitor activity** - Review Keycloak audit logs regularly
4. **Separate accounts** - Use different accounts per environment
5. **Never commit tokens** - Keep tokens out of version control
