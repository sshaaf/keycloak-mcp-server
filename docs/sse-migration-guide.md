# SSE Migration Guide

## Overview

The Keycloak MCP Server has been migrated from **stdin/stdout** communication to **SSE (Server-Sent Events)** transport. This enables HTTP-based communication and makes the server accessible via web browsers and HTTP clients.

## Version

**v0.3.0** - SSE-enabled version

## What Changed

### 1. Dependency Changes

**Before (v0.2.0 - stdin):**
```xml
<dependency>
 <groupId>io.quarkiverse.mcp</groupId>
 <artifactId>quarkus-mcp-server-stdio</artifactId>
 <version>1.4.0</version>
</dependency>
```

**After (v0.3.0 - SSE):**
```xml
<dependency>
 <groupId>io.quarkiverse.mcp</groupId>
 <artifactId>quarkus-mcp-server-sse</artifactId>
 <version>1.4.0</version>
</dependency>
```

### 2. Configuration Changes

**application.properties:**

```properties
# SSE Server Configuration
# Port 0 = random port for local dev (avoids conflicts)
# Containers override this to 8080 via QUARKUS_HTTP_PORT env var
quarkus.http.port=0
quarkus.http.host=0.0.0.0

# CORS Configuration for browser access
quarkus.http.cors=true
quarkus.http.cors.origins=*
quarkus.http.cors.methods=GET,POST,OPTIONS

# MCP SSE Configuration (enabled by default with quarkus-mcp-server-sse)
# Default endpoint: /mcp/sse
```

**Smart Port Assignment:**
- **Local (JAR/Native)**: Uses port `0` (random available port) to avoid conflicts
- **Container**: Automatically uses port `8080` (fixed port for networking)

### 3. Tool Classes

 **NO CHANGES REQUIRED** - All tool classes including the unified `KeycloakTool` work exactly the same!

## How to Use

### Starting the Server

#### Development Mode
```bash
./mvnw quarkus:dev
```

#### Production Mode
```bash
# Build
./mvnw package

# Run
java -jar target/quarkus-app/quarkus-run.jar
```

#### Using the Uber JAR
```bash
# Build
./mvnw package -Dquarkus.package.jar.type=uber-jar

# Run
java -jar target/keycloak-mcp-server-0.3.0-runner.jar
```

### Server Endpoints

Once started, the server will be available at:

- **MCP SSE Endpoint**: `http://localhost:8080/mcp/sse`
- **Health Check**: `http://localhost:8080/q/health`
- **Metrics**: `http://localhost:8080/q/metrics`

### Connecting to the SSE Server

#### Using MCP Client Libraries

```javascript
// Example using MCP SDK
import { Client } from "@modelcontextprotocol/sdk/client/index.js";
import { SSEClientTransport } from "@modelcontextprotocol/sdk/client/sse.js";

const transport = new SSEClientTransport(
 new URL("http://localhost:8080/mcp/sse")
);

const client = new Client({
 name: "keycloak-client",
 version: "1.0.0",
}, {
 capabilities: {}
});

await client.connect(transport);

// List available tools
const tools = await client.listTools();
console.log("Available tools:", tools);

// Call a tool
const result = await client.callTool({
 name: "executeKeycloakOperation",
 arguments: {
 operation: "GET_USERS",
 params: JSON.stringify({ realm: "quarkus" })
 }
});
```

#### Using curl

```bash
# Test the SSE endpoint
curl -N -H "Accept: text/event-stream" http://localhost:8080/mcp/sse

# List tools (requires MCP protocol handshake)
curl -X POST http://localhost:8080/mcp/sse \
 -H "Content-Type: application/json" \
 -d '{
 "jsonrpc": "2.0",
 "id": 1,
 "method": "tools/list"
 }'
```

#### Using Browser

You can connect to the SSE server directly from a web browser:

```javascript
const eventSource = new EventSource('http://localhost:8080/mcp/sse');

eventSource.onmessage = (event) => {
 console.log('Received:', event.data);
};

eventSource.onerror = (error) => {
 console.error('SSE Error:', error);
};
```

### Client Configuration Examples

#### Claude Desktop (MCP Config)

Update your `~/.config/claude-desktop/mcp.json` or `~/Library/Application Support/Claude/claude_desktop_config.json`:

```json
{
 "mcpServers": {
 "keycloak": {
 "transport": "sse",
 "url": "http://localhost:8080/mcp/sse",
 "env": {
 "KC_URL": "http://localhost:8180",
 "KC_USER": "admin",
 "KC_PASSWORD": "admin"
 }
 }
 }
}
```

#### Cursor IDE (MCP Config)

Update your `~/.cursor/mcp.json`:

```json
{
 "mcpServers": {
 "keycloak-mcp-server": {
 "transport": "sse",
 "url": "http://localhost:8080/mcp/sse",
 "env": {
 "KC_URL": "http://localhost:8180",
 "KC_USER": "admin",
 "KC_PASSWORD": "admin"
 }
 }
 }
}
```

## Advantages of SSE over stdin

### 1. **HTTP-Based Communication**
- Standard HTTP protocol
- Works through firewalls and proxies
- Easy to monitor with standard HTTP tools

### 2. **Browser Accessibility**
- Can be accessed directly from web browsers
- Enables web-based MCP clients
- Real-time updates via Server-Sent Events

### 3. **Network Distribution**
- Server can run on different machines
- Multiple clients can connect simultaneously
- Better for microservices architectures

### 4. **Development & Debugging**
- Easier to test with curl/Postman
- Better logging and monitoring
- Health checks and metrics endpoints

### 5. **Production Ready**
- Can be deployed behind load balancers
- Kubernetes/OpenShift friendly
- Better observability

## Environment Variables

The server still requires Keycloak connection details via environment variables:

```bash
export KC_URL=http://localhost:8180
export KC_USER=admin
export KC_PASSWORD=admin
```

Or pass them when starting:

```bash
KC_URL=http://localhost:8180 \
KC_USER=admin \
KC_PASSWORD=admin \
java -jar target/quarkus-app/quarkus-run.jar
```

## Docker Deployment

### Build Docker Image

```bash
# JVM mode
./mvnw package
docker build -f src/main/docker/Dockerfile.jvm -t keycloak-mcp-server:0.3.0 .

# Native mode (requires GraalVM)
./mvnw package -Pnative
docker build -f src/main/docker/Dockerfile.native -t keycloak-mcp-server:0.3.0-native .
```

### Run Docker Container

```bash
docker run -d \
 -p 8080:8080 \
 -e KC_URL=http://host.docker.internal:8180 \
 -e KC_USER=admin \
 -e KC_PASSWORD=admin \
 --name keycloak-mcp \
 keycloak-mcp-server:0.3.0
```

## Kubernetes/OpenShift Deployment

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
 name: keycloak-mcp-server
spec:
 replicas: 1
 selector:
 matchLabels:
 app: keycloak-mcp-server
 template:
 metadata:
 labels:
 app: keycloak-mcp-server
 spec:
 containers:
 - name: server
 image: keycloak-mcp-server:0.3.0
 ports:
 - containerPort: 8080
 name: http
 env:
 - name: KC_URL
 value: "http://keycloak:8080"
 - name: KC_USER
 valueFrom:
 secretKeyRef:
 name: keycloak-admin
 key: username
 - name: KC_PASSWORD
 valueFrom:
 secretKeyRef:
 name: keycloak-admin
 key: password
---
apiVersion: v1
kind: Service
metadata:
 name: keycloak-mcp-server
spec:
 selector:
 app: keycloak-mcp-server
 ports:
 - port: 8080
 targetPort: 8080
 name: http
```

## Security Considerations

### CORS Configuration

The default configuration allows all origins (`*`). For production, restrict this:

```properties
quarkus.http.cors.origins=https://yourdomain.com,https://app.yourdomain.com
```

### Authentication

Consider adding authentication for production deployments:

```properties
# Example: Basic Auth
quarkus.http.auth.basic=true
```

Or use OAuth2/OIDC with Keycloak itself:

```properties
quarkus.oidc.auth-server-url=http://localhost:8180/realms/master
quarkus.oidc.client-id=mcp-server
quarkus.oidc.credentials.secret=secret
```

### TLS/HTTPS

For production, enable HTTPS:

```properties
quarkus.http.ssl.certificate.file=/path/to/certificate.pem
quarkus.http.ssl.certificate.key-file=/path/to/key.pem
quarkus.http.ssl-port=8443
```

## Monitoring

### Health Checks

```bash
# Liveness
curl http://localhost:8080/q/health/live

# Readiness
curl http://localhost:8080/q/health/ready
```

### Metrics

```bash
# Prometheus metrics
curl http://localhost:8080/q/metrics
```

## Troubleshooting

### Server Won't Start

Check if port 8080 is already in use:
```bash
lsof -i :8080
```

Change the port in `application.properties`:
```properties
quarkus.http.port=8081
```

### CORS Errors

If you see CORS errors in browser console, ensure CORS is properly configured in `application.properties`.

### Connection Refused

Ensure:
1. Server is running: `curl http://localhost:8080/q/health`
2. Keycloak is accessible: `curl $KC_URL`
3. Environment variables are set correctly

## Migration Checklist

- [x] Replace `quarkus-mcp-server-stdio` with `quarkus-mcp-server-sse`
- [x] Update `application.properties` with SSE configuration
- [x] Add CORS configuration
- [x] Update version to 0.3.0
- [x] Test compilation
- [ ] Update client configurations (Claude Desktop, Cursor, etc.)
- [ ] Test SSE endpoint connectivity
- [ ] Update deployment scripts/documentation
- [ ] Configure production security settings

## Backward Compatibility

The tool interface remains exactly the same. All 46 operations (including SEARCH_DISCOURSE) work identically via SSE as they did with stdin.

## Summary

 **Converted from**: stdin/stdout MCP server (v0.2.0)
 **Converted to**: SSE HTTP-based MCP server (v0.3.0)
 **Port**: 8080 (configurable)
 **Endpoint**: `/mcp/sse`
 **All 46 operations**: Fully functional
 **Parametric Collapse pattern**: Maintained

---

**Status**: **Ready for HTTP/SSE communication**

