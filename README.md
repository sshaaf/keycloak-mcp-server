<!-- For a centered logo -->
<p align="center">
  <img src=".github/assets/keycloak-mcp-server.png" alt="Project Logo" width="128">
</p>
<h2 align="center">
  <b> An MCP Server for Keycloak </b>
</h2>

[![Java 21](https://img.shields.io/badge/Java-21-blue.svg)](https://openjdk.org/projects/jdk/21/)
[![Gradle 8](https://img.shields.io/badge/Gradle-8-green.svg)](https://gradle.org/)
[![Quarkus 3](https://img.shields.io/badge/Quarkus-3-blue.svg)](https://gradle.org/)

This project is designed to work with Keycloak for identity and access management, providing a robust and scalable solution for managing Keycloak resources through a command-line interface.

## Features

- **MCP Standard**: Implements the Model-Context Protocol for efficient standard input/output server operations.
- **Keycloak Integration**: Seamlessly integrates with Keycloak for authentication and authorization.
- **Comprehensive API**: Provides tools for managing various Keycloak resources:
  - Realms
  - Users
  - Clients
  - Roles
  - Groups
  - Identity Providers
  - Authentication Flows
- **Quarkus Framework**: Built using Quarkus to provide fast startup times and low memory footprint.

## Architecture

The project follows a layered architecture with tool classes that expose functionality through the MCP protocol and service classes that handle the actual operations with Keycloak.

### Tools Package Explanation

The tools package contains classes that expose Keycloak functionality through the MCP protocol:

1. **UserTool**: Manages Keycloak users, including creation, deletion, updating user information, and managing user roles and groups.

2. **RealmTool**: Manages Keycloak realms, including creation, deletion, updating realm settings, and managing realm events configuration.

3. **ClientTool**: Manages Keycloak clients, including creation, deletion, updating client settings, managing client secrets, and client roles.

4. **RoleTool**: Manages Keycloak roles, including creation, deletion, updating role settings, and managing role composites.

5. **GroupTool**: Manages Keycloak groups, including creation, deletion, updating group settings, managing group members, and group roles.

6. **IdentityProviderTool**: Manages Keycloak identity providers, including creation, deletion, updating identity provider settings, and managing identity provider mappers.

7. **AuthenticationTool**: Manages Keycloak authentication flows, including creation, deletion, and managing flow executions.

Each tool class follows a similar pattern:
- Injects a corresponding service class that handles the actual operations with Keycloak
- Injects an ObjectMapper for JSON serialization/deserialization
- Exposes methods with @Tool annotations that delegate to the service class
- Handles exceptions and provides meaningful error messages

## Getting started - configuration

The Keycloak MCP server is build output of 4 artifacts. 
- Native [MacOS, Linux, Windows]
- Uber jar

### VSCode
You can add the keycloak MCP server tools into VS Code by adding the following to your `mcp.json`

```yaml
  "keycloak_mcp_server": {
    "type": "stdio",
    "command": "<full path> keycloak-mcp-server-0.1",
    "args": [],
    "env": {
      "KC_URL": "http://localhost:8081",
      "KC_USER": "admin",
      "KC_PASSWORD": "admin"
    }
```

### Goose CLI

```yaml
extensions:
  keycloak_mcp_server:
    display_name: Keycloak MCP Server
    enabled: true
    name: keycloak-mcp-server
    timeout: 300
    type: stdio
    cmd: "<full path>keycloak-mcp-server-0.1"
    args: []
    env_keys:
      - "KC_URL"
```
You need to set an environment variable:
e.g.
```bash
export KC_URL=http://localhost:8081
```

If using the uber jar change the `cmd` and `args` as follows
```yaml
    cmd: "java"
    args: ["-jar", "path to jar"]
```

Now you can run `goose session` and the extension should be loaded.

## Example Usage

Here are some examples of how to use the Keycloak MCP Server with Goose:

```
( O)> can I create a new user in keycloak?
Yes, you can create a new user in Keycloak. To do this, you'll need to provide the following information about the user:

- **Realm**: The name of the realm where the user will reside.
- **Username**: The username for the new user.
- **First Name**: The first name of the user.
- **Last Name**: The last name of the user.
- **Email**: The email address of the user.
- **Password**: The password for the user's account.

You can provide these details, and I can assist you with creating the user.


----
( O)> list all users in quarkus realm
Here are the users in the "quarkus" realm:

1. **admin**
   - ID: `af134cab-f41c-4675-b141-205f975db679`

2. **alice**
   - ID: `eb4123a3-b722-4798-9af5-8957f823657a`

3. **jdoe**
   - ID: `1eed6a8e-a853-4597-b4c6-c4c2533546a0`

----
( O)> can you delete user sshaaf from realm quarkus

```

## CI/CD with GitHub Actions

This project uses GitHub Actions for continuous integration and delivery. The following workflows are available:

### Build and Test

The Build and Test workflow runs automatically on push and pull requests to the main branch. It:
- Builds the project
- Runs all tests
- Uploads test results as artifacts

This workflow ignores changes to image files (jpg, jpeg, png, gif, svg), txt files, and markdown files.

### Build Artifacts

The Build Artifacts workflow runs automatically on push and pull requests to the main branch. It:
- Builds an uber-jar
- Builds native binaries for Linux, MacOS, and Windows using GraalVM
- Uploads all artifacts

This workflow ignores changes to image files (jpg, jpeg, png, gif, svg), txt files, and markdown files.

### Release

The Release workflow is triggered manually and allows you to create a new release. To create a release:
1. Go to the Actions tab in the GitHub repository
2. Select the "Release" workflow
3. Click "Run workflow"
4. Enter the release version (e.g., "1.0.0")
5. Select whether this is a pre-release
6. Click "Run workflow"

The workflow will:
- Build an uber-jar
- Build native binaries for Linux, MacOS, and Windows
- Create a GitHub release with all artifacts

### Windows Native Build Requirements

Building native images on Windows requires Microsoft Visual C++ Build Tools. The GitHub Actions workflows have been configured to:

1. Set up the Visual Studio Developer Command Prompt using the `microsoft/setup-msbuild` action
2. Configure the Visual C++ build environment by:
   - Finding the Visual Studio installation path
   - Setting up the environment using `vcvarsall.bat`
   - Exporting all environment variables to make them available to the build process

This ensures that the `cl.exe` compiler (required by GraalVM for native image compilation on Windows) is available in the PATH environment variable.

## References

- [Keycloak](https://www.keycloak.org/) - Open Source Identity and Access Management
- [Quarkus](https://quarkus.io/) - A Kubernetes Native Java stack
- [Goose by Block](https://github.com/goose-ai/goose) - Command-line interface for AI assistants
- [Model-Context Protocol (MCP)](https://github.com/goose-ai/mcp) - Protocol for efficient standard input/output server operations

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.
