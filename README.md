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
[![Ask DeepWiki](https://deepwiki.com/badge.svg)](https://deepwiki.com/sshaaf/keycloak-mcp-server)
[![semantic-release: angular](https://img.shields.io/badge/semantic--release-angular-e10079?logo=semantic-release)](https://github.com/semantic-release/semantic-release)

This project is designed to work with Keycloak for identity and access management, providing a robust and scalable solution for managing Keycloak resources through a command-line interface.

### Releases
The Keycloak MCP server is available in the following formats:
- **Uber JAR**: Available in regular releases and builds
- **Native Binaries**: Available in regular releases and builds for MacOS, Linux, and Windows

## Features

- **MCP Standard**: Implements the Model-Context Protocol for efficient standard input/output server operations.
- **Keycloak Integration**: Seamlessly integrates with Keycloak for authentication and authorization.
- **Comprehensive API**: Provides tools for managing various Keycloak resources:
```mermaid
graph LR
    subgraph Realm
        U[Users]
        C[Clients]
        R[Roles]
        G[Groups]
        IdP[Identity Providers]
        Auth[Authentication Flows]
    end

```
- **Quarkus Framework**: Built using Quarkus to provide fast startup times and low memory footprint.

## Getting started - configuration

### Cursor
You can add the following in the config in the `~/.cursor/mcp.json`
```yaml
{
  "mcpServers": {
    "keycloak_mcp_server": {
      "type": "stdio",
      "command": "<full path> keycloak-mcp-server-0.1",
      "args": [],
      "env": {
        "KC_URL": "http://localhost:8081",
        "KC_USER": "admin",
        "KC_PASSWORD": "admin"
      }
    }
  }
}

```


You can add the keycloak server by adding the following to `claude_desktop_config`.
### Claude Code
```yaml
{
  "mcpServers": {
    "keycloak": {
      "command": "<full path>/keycloak-mcp-server-0.1",
      "args": [],
      "env": {
        "KC_URL": "http://localhost:8081",
        "KC_USER": "admin",
        "KC_PASSWORD": "admin"
      }
    }
  }
}
```
### VSCode
You can add the keycloak MCP server tools into VS Code by adding the following to your `mcp.json`.

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

Now you can run `goose session` and the extension should be loaded.

### Uber Jar
The examples above are for native binaries. however you can also use the uber-jar
If using the uber jar change the `cmd` and `args` as follows
```yaml
    cmd|command: "java"
    args: ["-jar", "path to jar"]
```



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

## References

- [Keycloak](https://www.keycloak.org/) - Open Source Identity and Access Management
- [Quarkus](https://quarkus.io/) - A Kubernetes Native Java stack
- [Goose by Block](https://github.com/goose-ai/goose) - Command-line interface for AI assistants
- [Model-Context Protocol (MCP)](https://github.com/goose-ai/mcp) - Protocol for efficient standard input/output server operations

## Contributing

Contributions are welcome! For more information please read the following documents. 
- [Developers guide](DEVELOPERS.md)
- [Contributors guide](CONTRIBUTORS.md)
- [Release workflow](RELEASE_WORKFLOW.md)
