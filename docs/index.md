# Keycloak MCP Server Documentation

## Introduction

The Keycloak MCP Server is a Model Context Protocol (MCP) server implementation that provides programmatic access to Keycloak administration functionality. This server enables AI assistants and development tools to interact with Keycloak through a standardized interface, supporting operations such as user management, realm configuration, client administration, and authentication flow management.

### Key Features

* **User JWT Token Authentication**: Each user authenticates with their own Keycloak credentials, ensuring proper permission enforcement
* **Comprehensive Keycloak Operations**: Support for users, realms, clients, roles, groups, identity providers, and authentication flows
* **SSE Transport**: Server-Sent Events (SSE) transport for HTTP-based communication
* **Production Ready**: Deployable on Kubernetes/OpenShift with proper security configurations
* **Containerized**: Available as container images with multi-architecture support
* **Native Compilation**: Supports GraalVM native image for fast startup and low memory footprint

### Architecture

The server is built using:

* **Quarkus Framework**: Modern Java framework optimized for cloud-native applications
* **Keycloak Admin Client**: Official Keycloak Java client for administrative operations
* **MCP Protocol**: Implements the Model Context Protocol for AI assistant integration
* **JWT Authentication**: Uses Keycloak's native JWT tokens for user authentication

## Table of Contents

### Getting Started

* [Getting Started Guide](getting-started.md) - Complete setup instructions for Docker, OpenShift, native binaries, and development mode
* [Authentication Guide](authentication.md) - Detailed guide on user JWT token authentication and configuration

### Deployment

* [OpenShift Deployment](openshift-deployment.md) - Production deployment guide for OpenShift/Kubernetes
* [Keycloak TLS Setup](keycloak-tls-setup.md) - Configure HTTPS/TLS for Keycloak in production environments
* [Container Guide](jib-container-guide.md) - Container image building and deployment using Jib

### Configuration

* [Port Configuration](port-configuration.md) - Smart port configuration for local development and containers
* [Git Commit Tagging](git-commit-tagging.md) - Automatic container image tagging with git commit SHA
* [Version Automation](version-automation.md) - Automated version extraction from Maven POM

### CI/CD

* [GitHub Actions Setup](github-actions-setup.md) - Configure GitHub Actions for automated builds and deployments
* [CI/CD Updates](ci-cd-updates.md) - Overview of CI/CD pipeline configuration and benefits

### Migration Guides

* [SSE Migration Guide](sse-migration-guide.md) - Migrate from stdio to SSE transport
* [Migration Guide](migration-guide.md) - Migrate from individual tools to unified KeycloakTool

### Technical Documentation

* [Parametric Collapse](parametric-collapse.md) - Design pattern for consolidating multiple tools into a single parameterized tool
* [Architecture Diagram](architecture-diagram.md) - System architecture and component relationships
* [Implementation Summary](implementation-summary.md) - Technical implementation details
* [Authentication Implementation](authentication-implementation-summary.md) - JWT authentication implementation details

### Development

* [Developers Guide](developers.md) - Guide for developers contributing to the project
* [Contributors Guide](contributors.md) - How to contribute to the project
* [Scripts Guide](scripts-guide.md) - Helper scripts for token management and setup

### Release Information

* [Changelog v0.3.0](changelog-v0.3.0.md) - Release notes for version 0.3.0
* [Completion Checklist](completion-checklist.md) - Project completion status and milestones

## Quick Links

### Authentication

The MCP server uses JWT Bearer token authentication. Each user must obtain their own JWT token from Keycloak and configure it in their MCP client.

**Get Token:**
```bash
./scripts/get-mcp-token.sh \
  --keycloak-url https://keycloak.example.com \
  --username your-username \
  --password your-password
```

**Configure MCP Client:**
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

### Deployment Options

#### Docker Container

```bash
docker run -d \
  --name keycloak-mcp-server \
  -p 8080:8080 \
  -e KC_URL=https://keycloak.example.com \
  -e KC_REALM=master \
  -e OIDC_CLIENT_ID=mcp-server \
  quay.io/sshaaf/keycloak-mcp-server:latest
```

#### OpenShift

```bash
oc apply -f deploy/openshift/
```

See the [OpenShift Deployment Guide](openshift-deployment.md) for complete instructions.

#### Native Binary

Download the native binary for your platform from the releases page and run:

```bash
export KC_URL=https://keycloak.example.com
export KC_REALM=master
export OIDC_CLIENT_ID=mcp-server
export QUARKUS_HTTP_PORT=8080

./keycloak-mcp-server-linux-x64
```

### Available Operations

The Keycloak MCP Server supports the following operations:

#### User Management
* GET_USERS - List all users
* GET_USER_BY_ID - Get user by ID
* GET_USER_BY_USERNAME - Get user by username
* CREATE_USER - Create new user
* UPDATE_USER - Update existing user
* DELETE_USER - Delete user
* RESET_PASSWORD - Reset user password
* SEND_VERIFICATION_EMAIL - Send verification email
* COUNT_USERS - Count users in realm

#### Realm Management
* GET_REALMS - List all realms
* GET_REALM - Get specific realm
* CREATE_REALM - Create new realm

#### Client Management
* GET_CLIENTS - List all clients
* GET_CLIENT - Get specific client
* CREATE_CLIENT - Create new client
* DELETE_CLIENT - Delete client
* GENERATE_CLIENT_SECRET - Generate client secret
* GET_CLIENT_ROLES - Get client roles
* CREATE_CLIENT_ROLE - Create client role
* DELETE_CLIENT_ROLE - Delete client role

#### Role Management
* GET_REALM_ROLES - List realm roles
* GET_REALM_ROLE - Get specific realm role
* ADD_ROLE_TO_USER - Assign role to user
* REMOVE_ROLE_FROM_USER - Remove role from user
* GET_USER_ROLES - Get user's roles

#### Group Management
* GET_GROUPS - List all groups
* GET_GROUP_MEMBERS - Get group members
* CREATE_GROUP - Create new group
* UPDATE_GROUP - Update group
* DELETE_GROUP - Delete group
* CREATE_SUBGROUP - Create subgroup
* ADD_USER_TO_GROUP - Add user to group
* REMOVE_USER_FROM_GROUP - Remove user from group
* GET_USER_GROUPS - Get user's groups

#### Identity Provider Management
* GET_IDENTITY_PROVIDERS - List identity providers
* GET_IDENTITY_PROVIDER - Get specific identity provider
* GET_IDENTITY_PROVIDER_MAPPERS - Get identity provider mappers

#### Authentication Flow Management
* GET_AUTHENTICATION_FLOWS - List authentication flows
* GET_AUTHENTICATION_FLOW - Get specific flow
* CREATE_AUTHENTICATION_FLOW - Create new flow
* DELETE_AUTHENTICATION_FLOW - Delete flow
* GET_FLOW_EXECUTIONS - Get flow executions
* UPDATE_FLOW_EXECUTION - Update flow execution

#### Search
* SEARCH_DISCOURSE - Search Keycloak community forum

## Security Considerations

### Authentication

* Each user authenticates with their own JWT token
* Keycloak enforces its native permission system
* No shared credentials or service accounts
* Full audit trail by user identity

### Token Management

* Tokens have configurable expiration times (recommended: 15-30 minutes)
* Tokens can be refreshed using refresh tokens
* Use HTTPS for all token transmission
* Store tokens securely in client configuration

### Production Deployment

* Always use HTTPS for Keycloak in production
* Configure proper TLS certificates
* Use Kubernetes secrets for sensitive configuration
* Enable audit logging in Keycloak
* Implement network policies for pod communication

## Support and Contributing

### Documentation

Complete documentation is available in this directory. Start with the [Getting Started Guide](getting-started.md) for setup instructions.

### Contributing

Contributions are welcome. Please see the [Contributors Guide](contributors.md) for information on how to contribute to the project.

### Issues

Report issues on the GitHub repository issue tracker.

### Community

* Keycloak Discourse: https://keycloak.discourse.group
* GitHub Repository: https://github.com/sshaaf/keycloak-mcp-server

## License

This project is licensed under the MIT License. See the LICENSE file in the repository root for details.

---

**Version**: 0.3.0  
**Last Updated**: November 2025  
**Maintainer**: Shaaf Syed

