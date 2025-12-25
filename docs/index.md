# Keycloak MCP Server

A Model Context Protocol (MCP) server that provides programmatic access to Keycloak administration functionality. This server enables AI assistants and development tools to interact with Keycloak through a standardized interface.

## Features

- **JWT Token Authentication** - Each user authenticates with their own Keycloak credentials
- **45+ Operations** - Users, realms, clients, roles, groups, identity providers, authentication flows
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
| [Authentication](authentication.md) | JWT token authentication setup and usage |
| [Configuration](configuration.md) | Environment variables, TLS, and port settings |
| [Developers](developers.md) | Architecture, building from source, contributing |

## Available Operations

### User Management
`GET_USERS`, `GET_USER_BY_ID`, `GET_USER_BY_USERNAME`, `CREATE_USER`, `UPDATE_USER`, `DELETE_USER`, `RESET_PASSWORD`, `SEND_VERIFICATION_EMAIL`, `COUNT_USERS`

### Role & Group Management
`GET_REALM_ROLES`, `GET_REALM_ROLE`, `ADD_ROLE_TO_USER`, `REMOVE_ROLE_FROM_USER`, `GET_USER_ROLES`, `GET_GROUPS`, `GET_GROUP_MEMBERS`, `CREATE_GROUP`, `UPDATE_GROUP`, `DELETE_GROUP`, `CREATE_SUBGROUP`, `ADD_USER_TO_GROUP`, `REMOVE_USER_FROM_GROUP`, `GET_USER_GROUPS`

### Realm Management
`GET_REALMS`, `GET_REALM`, `CREATE_REALM`

### Client Management
`GET_CLIENTS`, `GET_CLIENT`, `CREATE_CLIENT`, `DELETE_CLIENT`, `GENERATE_CLIENT_SECRET`, `GET_CLIENT_ROLES`, `CREATE_CLIENT_ROLE`, `DELETE_CLIENT_ROLE`

### Identity Providers
`GET_IDENTITY_PROVIDERS`, `GET_IDENTITY_PROVIDER`, `GET_IDENTITY_PROVIDER_MAPPERS`

### Authentication Flows
`GET_AUTHENTICATION_FLOWS`, `GET_AUTHENTICATION_FLOW`, `CREATE_AUTHENTICATION_FLOW`, `DELETE_AUTHENTICATION_FLOW`, `GET_FLOW_EXECUTIONS`, `UPDATE_FLOW_EXECUTION`

### Search
`SEARCH_DISCOURSE` - Search Keycloak community forum

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
