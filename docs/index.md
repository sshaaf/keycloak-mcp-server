# Keycloak MCP Server

A Model Context Protocol (MCP) server that provides programmatic access to Keycloak administration functionality. This server enables AI assistants and development tools to interact with Keycloak through a standardized interface.

## Features

- **JWT Token Authentication** - Each user authenticates with their own Keycloak credentials
- **178 Operations** - Users, realms, clients, roles, groups, identity providers, auth flows, UMA, organizations, and more
- **SSE Transport** - HTTP-based Server-Sent Events for modern connectivity
- **Container Ready** - Multi-architecture images (AMD64/ARM64) on [Quay.io](https://quay.io/repository/sshaaf/keycloak-mcp-server)
- **Native Compilation** - GraalVM native images for fast startup

## Quick Start

### 1. Run the Server

```bash
docker run -d \
  -p 8080:8080 \
  -e KC_URL=https://keycloak.example.com \
  -e KC_REALM=master \
  -e OIDC_CLIENT_ID=mcp-server \
  quay.io/sshaaf/keycloak-mcp-server:latest
```

### 2. Get Your Token

```bash
./scripts/get-mcp-token.sh \
  --keycloak-url https://keycloak.example.com \
  --username your-username \
  --password your-password
```

### 3. Configure Your MCP Client

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

## Documentation

| Guide | Description |
|-------|-------------|
| [Getting Started](getting-started.md) | Deployment options: Docker, OpenShift, native binaries |
| [Operations](operations.md) | All 178 `executeKeycloakOperation` commands with request/response examples |
| [Authentication](authentication.md) | JWT token authentication setup and usage |
| [Configuration](configuration.md) | Environment variables, TLS, and port settings |
| [Developers](developers.md) | Architecture, building from source, contributing |

## Available Operations

Use the single MCP tool `executeKeycloakOperation(operation, params)`. See the full
[Operations reference](operations.md) for every command, required JSON fields, and
validated sample output against Keycloak 26.

### Highlights

| Area | Example operations |
|------|--------------------|
| Users | `GET_USERS`, `CREATE_USER`, `RESET_PASSWORD`, `COUNT_USERS` |
| Realms | `GET_REALMS`, `CREATE_REALM`, `SET_REALM_ENABLED` |
| Clients | `GET_CLIENTS`, `CREATE_CLIENT`, `GENERATE_CLIENT_SECRET` |
| Roles & groups | `GET_REALM_ROLES`, `CREATE_GROUP`, `ADD_ROLE_TO_USER` |
| Auth flows & IDPs | `GET_AUTHENTICATION_FLOWS`, `GET_IDENTITY_PROVIDERS` |
| UMA / Authz | `LIST_AUTHZ_RESOURCES`, `CREATE_AUTHZ_POLICY` |
| Organizations | `GET_ORGANIZATIONS`, `ADD_ORGANIZATION_MEMBER` |
| Community | `SEARCH_DISCOURSE` |

## Environment Variables

| Variable | Description | Required |
|----------|-------------|----------|
| `KC_URL` | Keycloak server URL | Yes |
| `KC_REALM` | Default realm (default: `master`) | No |
| `OIDC_CLIENT_ID` | OIDC client ID (default: `mcp-server`) | No |

## Resources

- **Container Images**: [quay.io/sshaaf/keycloak-mcp-server](https://quay.io/repository/sshaaf/keycloak-mcp-server)
- **GitHub**: [github.com/sshaaf/keycloak-mcp-server](https://github.com/sshaaf/keycloak-mcp-server)
- **Keycloak Community**: [keycloak.discourse.group](https://keycloak.discourse.group)

## License

MIT License - See LICENSE file for details.
