# Changelog v0.3.0 - SSE Migration

## Release Date
November 20, 2025

## Major Changes

### SSE Transport (Breaking Change)

Migrated from **stdin/stdout** communication to **SSE (Server-Sent Events)** HTTP-based transport.

**Impact**: This is a breaking change for client configurations. Clients need to be updated to use HTTP/SSE instead of process-based stdin/stdout.

### What Changed

#### 1. **Dependency Update**
- **Removed**: `quarkus-mcp-server-stdio` (v1.4.0)
- **Added**: `quarkus-mcp-server-sse` (v1.4.0)

#### 2. **Quarkus Version Update**
- Updated from **3.24.5** to **3.29.4**

#### 3. **Configuration Changes**
New `application.properties` settings:
```properties
# HTTP Server
quarkus.http.port=8080
quarkus.http.host=0.0.0.0

# CORS (for browser access)
quarkus.http.cors=true
quarkus.http.cors.origins=*

# MCP SSE Endpoint
quarkus.mcp.server.sse.path=/mcp/sse
quarkus.mcp.server.sse.enabled=true
```

#### 4. **Server Endpoints**
New HTTP endpoints available:
- **MCP SSE**: `http://localhost:8080/mcp/sse`
- **Health**: `http://localhost:8080/q/health`
- **Metrics**: `http://localhost:8080/q/metrics`

### What Stayed the Same

- **All 46 operations** work exactly as before
- **Parametric Collapse pattern** unchanged
- **KeycloakTool** unified tool interface unchanged
- **Service layer** (UserService, RealmService, etc.) unchanged
- **All functionality** preserved

## New Features

### HTTP-Based Communication
- Server runs as HTTP service on port 8080
- Can be accessed via standard HTTP clients
- Works through firewalls and proxies

### Multiple Client Support
- Multiple clients can connect simultaneously
- Browser-based clients supported
- Network distribution enabled

### Observability
- Health check endpoints
- Prometheus metrics
- Better logging and monitoring

### Container-Friendly
- Docker/Kubernetes ready
- Better for microservices
- Load balancer compatible

## Migration Guide

See [sse-migration-guide.md](sse-migration-guide.md) for complete migration instructions.

### Quick Start

1. **Start the server**:
```bash
./mvnw quarkus:dev
```

2. **Update client config** (Cursor example):
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

3. **Test the connection**:
```bash
curl http://localhost:8080/q/health
```

## Documentation Updates

### New Files
- `sse-migration-guide.md` - Complete SSE migration guide
- `CHANGELOG_v0.3.0.md` - This file

### Updated Files
- `README.md` - Updated with SSE configuration
- `pom.xml` - Dependency and version updates
- `application.properties` - SSE configuration

## Breaking Changes

### Client Configuration Required

**Before (v0.2.0 - stdin)**:
```json
{
 "mcpServers": {
 "keycloak-mcp-server": {
 "command": "java",
 "args": ["-jar", "path/to/keycloak-mcp-server.jar"],
 "env": {...}
 }
 }
}
```

**After (v0.3.0 - SSE)**:
```json
{
 "mcpServers": {
 "keycloak-mcp-server": {
 "transport": "sse",
 "url": "http://localhost:8080/mcp/sse",
 "env": {...}
 }
 }
}
```

### Server Must Be Started Separately

With SSE, the server runs independently as an HTTP service:
- **v0.2.0**: Client started the server process
- **v0.3.0**: Server must be running before clients connect

## Upgrade Path

### For Developers

1. Update to v0.3.0
2. Start server: `./mvnw quarkus:dev`
3. Update client configs to use SSE
4. Test operations

### For Production

1. Build new version: `./mvnw package`
2. Update deployment configs (Docker/K8s)
3. Configure CORS for production domains
4. Consider adding authentication
5. Deploy and update client configurations

## Advantages Over stdin

| Feature | stdin (v0.2.0) | SSE (v0.3.0) |
|---------|----------------|--------------|
| Transport | Process-based | HTTP-based |
| Multiple Clients | No | Yes |
| Browser Access | No | Yes |
| Network Distribution | No | Yes |
| Load Balancing | No | Yes |
| Health Checks | No | Yes |
| Metrics | Limited | Full |
| Debugging | Difficult | Easy |
| Container-Friendly | Moderate | Excellent |

## Known Issues

None at this time.

## Future Enhancements

Planned for future releases:
- WebSocket transport option
- Built-in authentication
- Rate limiting
- Request caching
- GraphQL endpoint

## Contributors

Thank you to all contributors who helped with this release!

## References

- [Quarkus 3.29.4 Release Notes](https://quarkus.io/blog/quarkus-3-29-0-released/)
- [MCP Protocol Specification](https://github.com/anthropics/model-context-protocol)
- [Server-Sent Events (SSE) MDN Docs](https://developer.mozilla.org/en-US/docs/Web/API/Server-sent_events)

---

**Full Changelog**: v0.2.0...v0.3.0

