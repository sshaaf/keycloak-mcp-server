# Keycloak MCP Server

The Keycloak MCP Server is a service that leverages the Quarkus framework to operate as a Model-Context Protocol (MCP) server. This project is designed to work with Keycloak for identity and access management, providing a robust and scalable solution.

## Features

- **Quarkus Framework**: Built using Quarkus to provide fast startup times and low memory footprint.
- **MCP Standard**: Implements the Model-Context Protocol for efficient standard input/output server operations.
- **Keycloak Integration**: Seamlessly integrates with Keycloak for authentication and authorization.

## Coverage
- **Realm**
- **Users**
- **Clients**


## Prerequisites

- **Java 21 or newer**: Ensure you have Java installed on your system.
- **Maven**: Required for building and running the project.

## Getting Started

Starting the local instance of keycloak

```bash
    docker-compose -f deploy/docker-compose.yml up
```

To package the application:

```bash
mvn clean package
```

### Running with Goose

Integrate and run this project as an extension with Goose:

```bash
 goose session --with-extension="java -jar target/keycloak-mcp-server-1.0.0-SNAPSHOT-runner.jar" 
```

questions you can ask?
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
