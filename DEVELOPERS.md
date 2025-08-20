# Keycloak MCP Server Developer Guide
This guide provides detailed technical information for developers working on the Keycloak MCP Server project.

## Building from Source
1. Clone the repository:
   ```
   git clone https://github.com/yourusername/keycloak-mcp-server.git
   cd keycloak-mcp-server
   ```
2. Build the project:
   ```
   ./mvnw clean package
   ```

## GitHub Actions Builds
Keycloak MCP Server uses GitHub Actions to automatically build and test the project on every commit.

### Accessing Built Artifacts
1. Go to the Actions tab in the GitHub repository.
2. Click on the latest workflow run.
3. Scroll down to the "Artifacts" section to download the built artifacts.

### Releases
Official releases are created when changes are pushed to the main branch. You can find the latest release with all artifacts on the Releases page.

## Running the Application
The Keycloak MCP Server is built using Quarkus, which provides fast startup times and a low memory footprint.

### Prerequisites
- Java 21 or higher
- Maven 3.6 or higher
- Keycloak server (for integration)

### Running in Development Mode
To run the application in development mode:
```bash
./mvnw quarkus:dev
```

This enables hot deployment with background compilation, allowing you to make changes to the code and automatically reload the application.

### Configuration
The application can be configured using the `application.properties` file located in `src/main/resources/`. Key configuration properties include:

- `quarkus.keycloak.url`: The URL of the Keycloak server

An example docker-compose file is provided for local development.

## Testing
Keycloak MCP Server includes a comprehensive test suite to ensure code quality and prevent regressions.

### Running Tests
To run the tests locally:
```bash
./mvnw test
```

This will execute all tests and generate a report in `build/reports/tests/test/index.html`.

### Continuous Integration
The GitHub Actions workflow automatically runs all tests for:
- Every push to the main branch
- Every pull request targeting the main branch

This ensures that all code changes pass tests before being merged, maintaining code quality and preventing regressions.

## Project Architecture
Follows a layered architecture with tool classes that expose functionality through the MCP protocol and service classes that handle the actual operations with Keycloak.
Each tool class follows a similar pattern:
- Injects a corresponding service class that handles the actual operations with Keycloak
- Injects an ObjectMapper for JSON serialization/deserialization
- Exposes methods with @Tool annotations that delegate to the service class

### Class Structure
The project includes the following main components:

- **Service Layer**: Handles the actual operations with Keycloak
  - `UserService`: Manages user-related operations
  - `RealmService`: Manages realm-related operations
  - `ClientService`: Manages client-related operations
  - `RoleService`: Manages role-related operations
  - `GroupService`: Manages group-related operations
  - `IdentityProviderService`: Manages identity provider-related operations
  - `AuthenticationService`: Manages authentication flow-related operations

- **Tools Layer**: Exposes functionality through the MCP protocol
  - `UserTool`: Exposes user-related operations
  - `RealmTool`: Exposes realm-related operations
  - `ClientTool`: Exposes client-related operations
  - `RoleTool`: Exposes role-related operations
  - `GroupTool`: Exposes group-related operations
  - `IdentityProviderTool`: Exposes identity provider-related operations
  - `AuthenticationTool`: Exposes authentication flow-related operations

### Class Diagram

Below is a class diagram showing the structure of the tools package and its relationships with the service layer:

```mermaid
graph TD
    subgraph "Goose CLI"
        GooseCLI[Goose CLI]
    end

    subgraph "Tools Layer"
        UserTool["UserTool"]
        RealmTool["RealmTool"]
        ClientTool["ClientTool"]
        RoleTool["RoleTool"]
        GroupTool["GroupTool"]
    end

    subgraph "Service Layer"
        UserService["UserService"]
        RealmService["RealmService"]
        ClientService["ClientService"]
        RoleService["RoleService"]
        GroupService["GroupService"]
    end

    subgraph "External Services"
        Keycloak[("Keycloak")]
    end

    %% Define Relationships
    GooseCLI --> UserTool
    GooseCLI --> RealmTool
    GooseCLI --> ClientTool
    GooseCLI --> RoleTool
    GooseCLI --> GroupTool

    UserTool --> UserService
    RealmTool --> RealmService
    ClientTool --> ClientService
    RoleTool --> RoleService
    GroupTool --> GroupService

    UserService --> Keycloak
    RealmService --> Keycloak
    ClientService --> Keycloak
    RoleService --> Keycloak
    GroupService --> Keycloak

    %% Styling
    style GooseCLI fill:#d4edda,stroke:#c3e6cb
    style Keycloak fill:#f8d7da,stroke:#f5c6cb

```
## Architecture

The project follows a layered architecture with tool classes that expose functionality through the MCP protocol and service classes that handle the actual operations with Keycloak.

### Tools Package Explanation

The tools package contains classes that expose Keycloak functionality through the MCP protocol:

1. **UserTool**: Manages Keycloak users, including creation, deletion, updating user information, and managing user roles and groups.

2. **RealmTool**: Manages Keycloak realms, including creation, deletion, updating realm settings, and managing realm events configuration.

3. **ClientTool**: Manages Keycloak clients, including creation, deletion, updating client settings, managing client secrets, and client roles.

4. **RoleTool**: Manages Keycloak roles, including creation, deletion, updating role settings, and managing role composites.

5. **GroupTool**: Manages Keycloak groups, including creation, deletion, updating group settings, managing group members, and group roles.


Each tool class follows a similar pattern:
- Injects a corresponding service class that handles the actual operations with Keycloak
- Injects an ObjectMapper for JSON serialization/deserialization
- Exposes methods with @Tool annotations that delegate to the service class
- Handles exceptions and provides meaningful error messages

### Building via source and running locally

You can start a local Keycloak instance using Docker Compose:

```bash
docker-compose -f deploy/docker-compose.yml up
```

### Building the Application

To build the application using Maven:

```bash
./mvnw clean package
```

To build an uber jar:

```bash
./mvnw clean package -Dquarkus.package.type=uber-jar
```
