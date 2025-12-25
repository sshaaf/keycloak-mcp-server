# Getting Started

This guide covers all deployment options for the Keycloak MCP Server.

## Prerequisites

- Keycloak instance (v20.0+) running with HTTPS (production) or HTTP (development)
- Keycloak user account with appropriate permissions
- MCP-compatible client (Cursor IDE, Claude Desktop, etc.)

## Deployment Options

### Option 1: Docker Container

**Pull and run:**

```bash
docker run -d \
  --name keycloak-mcp-server \
  -p 8080:8080 \
  -e KC_URL=https://keycloak.example.com \
  -e KC_REALM=master \
  -e OIDC_CLIENT_ID=mcp-server \
  quay.io/sshaaf/keycloak-mcp-server:latest
```

**For local Keycloak (macOS/Windows):**

```bash
docker run -d \
  --name keycloak-mcp-server \
  -p 8080:8080 \
  -e KC_URL=http://host.docker.internal:8180 \
  -e KC_REALM=master \
  -e OIDC_CLIENT_ID=mcp-server \
  quay.io/sshaaf/keycloak-mcp-server:latest
```

**For local Keycloak (Linux):**

```bash
docker run -d \
  --name keycloak-mcp-server \
  --network host \
  -e KC_URL=http://localhost:8180 \
  -e KC_REALM=master \
  -e OIDC_CLIENT_ID=mcp-server \
  quay.io/sshaaf/keycloak-mcp-server:latest
```

**Verify:**

```bash
docker logs keycloak-mcp-server
curl http://localhost:8080/q/health
```

### Option 2: OpenShift/Kubernetes

**Quick deploy:**

```bash
oc new-project keycloak-mcp
oc apply -f deploy/openshift/
```

**Manual setup:**

```bash
# Create ConfigMap
oc create configmap keycloak-mcp-config \
  --from-literal=keycloak-url=https://keycloak.apps.example.com \
  --from-literal=keycloak-realm=master \
  --from-literal=client-id=mcp-server

# Deploy
oc apply -f deploy/openshift/deployment.yaml
oc apply -f deploy/openshift/service.yaml
oc apply -f deploy/openshift/route.yaml

# Get route URL
oc get route keycloak-mcp-server -o jsonpath='{.spec.host}'
```

**With self-signed Keycloak certificates:**

```bash
# Extract CA from Keycloak
oc get secret example-tls-secret -n keycloak \
  -o jsonpath='{.data.tls\.crt}' | base64 -d > keycloak-ca.crt

# Create CA ConfigMap
oc create configmap keycloak-ca-bundle --from-file=ca.crt=keycloak-ca.crt

# Deploy with CA mounted (uncomment volumes in deployment.yaml)
oc apply -f deploy/openshift/
```

### Option 3: Native Binary

Download from [releases](https://github.com/sshaaf/keycloak-mcp-server/releases):

```bash
# Linux
wget https://github.com/sshaaf/keycloak-mcp-server/releases/latest/download/keycloak-mcp-server-linux-x64
chmod +x keycloak-mcp-server-linux-x64

# macOS (Intel)
wget https://github.com/sshaaf/keycloak-mcp-server/releases/latest/download/keycloak-mcp-server-darwin-x64
chmod +x keycloak-mcp-server-darwin-x64

# macOS (Apple Silicon)
wget https://github.com/sshaaf/keycloak-mcp-server/releases/latest/download/keycloak-mcp-server-darwin-arm64
chmod +x keycloak-mcp-server-darwin-arm64
```

**Run:**

```bash
export KC_URL=https://keycloak.example.com
export KC_REALM=master
export OIDC_CLIENT_ID=mcp-server
export QUARKUS_HTTP_PORT=8080

./keycloak-mcp-server-linux-x64
```

### Option 4: Development Mode

```bash
git clone https://github.com/sshaaf/keycloak-mcp-server.git
cd keycloak-mcp-server

# Start with hot-reload
mvn quarkus:dev
```

Development mode:
- Disables authentication for convenience
- Hot-reload on code changes
- Dev UI at `http://localhost:8080/q/dev`

## Configure MCP Client

### Cursor IDE

Edit `~/.cursor/mcp.json`:

```json
{
  "mcpServers": {
    "keycloak": {
      "transport": "sse",
      "url": "http://localhost:8080/mcp/sse",
      "headers": {
        "Authorization": "Bearer <your-jwt-token>"
      }
    }
  }
}
```

Reload: `Cmd+Shift+P` → "Reload MCP Servers"

### Claude Desktop

Edit `~/.config/claude-desktop/mcp.json` (Linux) or `~/Library/Application Support/Claude/claude_desktop_config.json` (macOS):

```json
{
  "mcpServers": {
    "keycloak": {
      "transport": "sse",
      "url": "http://localhost:8080/mcp/sse",
      "headers": {
        "Authorization": "Bearer <your-jwt-token>"
      }
    }
  }
}
```

## Get Your JWT Token

Use the helper script:

```bash
./scripts/get-mcp-token.sh \
  --keycloak-url https://keycloak.example.com \
  --username your-username \
  --password your-password
```

Or manually:

```bash
TOKEN=$(curl -X POST \
  https://keycloak.example.com/realms/master/protocol/openid-connect/token \
  -d 'grant_type=password' \
  -d 'client_id=admin-cli' \
  -d 'username=your-username' \
  -d 'password=your-password' | jq -r '.access_token')

echo $TOKEN
```

## Test Your Setup

**Health check:**

```bash
curl http://localhost:8080/q/health
```

**Try in your MCP client:**

```
List all Keycloak realms
```

Expected: A list of realms from your Keycloak instance.

## Troubleshooting

### Server not connecting

```bash
# Check if running
docker ps | grep keycloak-mcp-server
docker logs keycloak-mcp-server

# Test endpoints
curl http://localhost:8080/q/health
curl -N http://localhost:8080/mcp/sse
```

### SSL/TLS errors

For self-signed certificates in development:

```bash
docker run -d \
  -e QUARKUS_TLS_TRUST_ALL=true \
  ...
```

For production, mount proper CA certificates (see Configuration guide).

### Authentication failures

```bash
# Test token manually
curl -X POST https://keycloak.example.com/realms/master/protocol/openid-connect/token \
  -d 'grant_type=password' \
  -d 'client_id=admin-cli' \
  -d 'username=your-username' \
  -d 'password=your-password'
```

### Token expired

Tokens typically expire in 5-60 minutes. Generate a new one:

```bash
./scripts/get-mcp-token.sh --keycloak-url ... --username ... --password ...
```

## Next Steps

- [Authentication Guide](authentication.md) - Detailed JWT authentication setup
- [Configuration Guide](configuration.md) - TLS, ports, and environment variables
- [Developers Guide](developers.md) - Building from source and contributing
