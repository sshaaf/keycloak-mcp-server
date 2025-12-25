# Developer Guide

Technical documentation for developers building, extending, or contributing to the Keycloak MCP Server.

## Prerequisites

- Java 21+
- Maven 3.6+
- Docker (for container builds)
- Keycloak instance (for testing)

## Building from Source

```bash
git clone https://github.com/sshaaf/keycloak-mcp-server.git
cd keycloak-mcp-server

# Build JAR
./mvnw clean package

# Build uber JAR
./mvnw package -Dquarkus.package.jar.type=uber-jar

# Build native binary (requires GraalVM)
./mvnw package -Pnative
```

## Running Locally

### Development Mode

```bash
./mvnw quarkus:dev
```

Features:
- Hot reload on code changes
- Dev UI at `http://localhost:8080/q/dev`
- Authentication disabled for convenience

### With Docker Compose

```bash
docker-compose -f deploy/docker-compose.yml up
```

Starts Keycloak and the MCP server together.

## Architecture

### Unified Tool Design (Parametric Collapse)

The project consolidates 45+ operations into a single `KeycloakTool` class:

```
┌─────────────────────────────────────────┐
│           KeycloakTool                  │
│  executeKeycloakOperation(op, params)   │
└────────────────┬────────────────────────┘
                 │ Routes via switch
    ┌────────────┼────────────┐
    ▼            ▼            ▼
┌────────┐  ┌────────┐  ┌────────┐
│UserSvc │  │RealmSvc│  │ClientSvc│ ...
└────────┘  └────────┘  └────────┘
```

**Benefits:**
- 1 MCP tool instead of 37+ individual tools
- Centralized error handling
- Type-safe operation selection via enum
- Easier to maintain and extend

### Project Structure

```
src/main/java/dev/shaaf/keycloak/mcp/server/
├── KeycloakTool.java           # Unified tool (45+ operations)
├── KeycloakClientFactory.java  # Request-scoped client creation
├── user/
│   └── UserService.java
├── realm/
│   └── RealmService.java
├── client/
│   └── ClientService.java
├── role/
│   └── RoleService.java
├── group/
│   └── GroupService.java
├── idp/
│   └── IdentityProviderService.java
└── authentication/
    └── AuthenticationService.java
```

### Adding a New Operation

1. **Add enum value:**

```java
public enum KeycloakOperation {
    // ... existing operations
    MY_NEW_OPERATION
}
```

2. **Add switch case in `executeKeycloakOperation()`:**

```java
case MY_NEW_OPERATION:
    return myService.myNewMethod(
        paramsNode.get("param1").asText(),
        paramsNode.get("param2").asInt()
    );
```

3. **Update tool description** with the new operation name.

## Building Container Images

Container images are built using Jib (no Docker daemon required):

```bash
# Build without pushing
./mvnw package -Dquarkus.container-image.build=true

# Build and push to Quay.io
./mvnw package \
  -Dquarkus.container-image.build=true \
  -Dquarkus.container-image.push=true \
  -Dquarkus.container-image.username=$QUAY_USERNAME \
  -Dquarkus.container-image.password=$QUAY_PASSWORD

# Build for specific platform
./mvnw package -Dquarkus.jib.platforms=linux/arm64
```

Images are automatically tagged with:
- Git commit SHA (primary): `49ff54e`
- Latest: `latest`

## CI/CD

GitHub Actions automatically:
- Builds JAR and native binaries on every push
- Pushes container images on `main` branch pushes
- Creates releases with version tags

### Required GitHub Secrets

| Secret | Description |
|--------|-------------|
| `QUAY_USERNAME` | Quay.io username or robot account |
| `QUAY_PASSWORD` | Quay.io password or robot token |

### Version Management

Version is automatically extracted from `pom.xml`:

```bash
mvn help:evaluate -Dexpression=project.version -q -DforceStdout
```

No hardcoded versions in workflows.

## Testing

```bash
# Run tests
./mvnw test

# Run with coverage
./mvnw verify
```

### Manual Testing

1. Start Keycloak: `docker-compose -f deploy/docker-compose.yml up keycloak`
2. Start MCP server: `./mvnw quarkus:dev`
3. Configure MCP client
4. Test operations in chat

## Contributing

### Pull Request Process

1. Fork the repository
2. Create a feature branch
3. Make changes with tests
4. Ensure `./mvnw test` passes
5. Submit pull request

### Commit Message Format

Use conventional commits for automatic versioning:

- `fix: ...` - Bug fixes (PATCH release)
- `feat: ...` - New features (MINOR release)
- `feat!: ...` or `BREAKING CHANGE:` - Breaking changes (MAJOR release)

### Code Style

- Follow existing patterns
- Write clear, readable code
- Include comments where necessary
- Test new functionality

## Troubleshooting Development

### "Address already in use"

```bash
# Find process
lsof -i :8080

# Kill it
kill -9 <pid>

# Or use random port (default in dev)
```

### Keycloak connection fails

```bash
# Check Keycloak is running
curl http://localhost:8180

# Check environment variables
echo $KC_URL
```

### Native build fails

Ensure GraalVM is installed:

```bash
# Install via SDKMAN
sdk install java 21.0.1-graalce

# Or use container build
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

## Resources

- [Quarkus Documentation](https://quarkus.io/guides/)
- [Keycloak Admin Client](https://www.keycloak.org/docs/latest/server_development/#admin-rest-api)
- [MCP Protocol](https://github.com/anthropics/model-context-protocol)
- [Jib Documentation](https://github.com/GoogleContainerTools/jib)
