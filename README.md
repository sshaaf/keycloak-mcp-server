<!-- For a centered logo -->
<p align="center">
  <img src=".github/assets/keycloak-mcp-server.png" alt="Project Logo" width="128">
</p>
<h2 align="center">
  <b> An MCP Server for Keycloak </b>
</h2>

# Keycloak MCP Server

A Model Context Protocol (MCP) server that provides programmatic access to Keycloak administration functionality.

## Overview

The Keycloak MCP Server enables AI assistants and development tools to interact with Keycloak through the Model Context Protocol. It supports comprehensive Keycloak operations including user management, realm configuration, client administration, and authentication flow management.

## Key Features

* User JWT Token Authentication
* Comprehensive Keycloak Operations (users, realms, clients, roles, groups, etc.)
* SSE Transport for HTTP-based communication
* Production-ready OpenShift/Kubernetes deployment
* Multi-architecture container images
* GraalVM native image support

## Quick Start

### Using Docker

```bash
docker run -d \
  --name keycloak-mcp-server \
  -p 8080:8080 \
  -e KC_URL=https://keycloak.example.com \
  -e KC_REALM=master \
  -e OIDC_CLIENT_ID=mcp-server \
  quay.io/sshaaf/keycloak-mcp-server:latest
```

### Authentication

Users authenticate with their own JWT tokens from Keycloak:

```bash
# Get your token
./scripts/get-mcp-token.sh \
  --keycloak-url https://keycloak.example.com \
  --username your-username \
  --password your-password
```

Configure in your MCP client (`~/.cursor/mcp.json`):

```json
{
  "mcpServers": {
    "keycloak": {
      "transport": "sse",
      "url": "https://mcp-server.example.com/mcp/sse",
      "headers": {
        "Authorization": "Bearer <your-jwt-token>"
      }
    }
  }
}
```

## Documentation

Complete documentation is available in the `docs` directory:

* [Getting Started Guide](docs/getting-started.md) - Setup instructions
* [Authentication Guide](docs/authentication.md) - User authentication
* [OpenShift Deployment](docs/openshift-deployment.md) - Production deployment
* [Developers Guide](docs/developers.md) - Development guide
* [Full Documentation Index](docs/index.md) - Complete table of contents

### Building Documentation

This project uses MkDocs for documentation. To build and serve locally:

```bash
pip install mkdocs-material
mkdocs serve
```

Visit http://localhost:8000 to view the documentation.

## Container Images

Pre-built images are available on Quay.io:

```bash
docker pull quay.io/sshaaf/keycloak-mcp-server:latest
```

Images are automatically built and pushed on commits to main and on releases.

## Building

### JAR

```bash
mvn clean package
java -jar target/quarkus-app/quarkus-run.jar
```

### Native Image

```bash
mvn clean package -Pnative
./target/keycloak-mcp-server-runner
```

### Container Image

```bash
mvn clean package -Dquarkus.container-image.build=true
```

## Technology Stack

* **Quarkus** - Cloud-native Java framework
* **Keycloak Admin Client** - Official Keycloak Java client
* **MCP Protocol** - Model Context Protocol for AI integration
* **Jib** - Containerization without Docker daemon
* **GraalVM** - Native image compilation support

## License

MIT License - see LICENSE file for details.

## Contributing

Contributions are welcome. See [Contributors Guide](docs/contributors.md) for details.

## Support

* Documentation: [docs/index.md](docs/index.md)
* Issues: GitHub Issues
* Community: [Keycloak Discourse](https://keycloak.discourse.group)

---

**Maintainer**: Shaaf Syed  
**Repository**: https://github.com/sshaaf/keycloak-mcp-server  
**Container Registry**: https://quay.io/repository/sshaaf/keycloak-mcp-server
