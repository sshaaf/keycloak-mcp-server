# Keycloak MCP Server

The Keycloak MCP Server is a service that leverages the Quarkus framework to operate as a Model-Context Protocol (MCP) server. This project is designed to work with Keycloak for identity and access management, providing a robust and scalable solution.

## Features

- **Quarkus Framework**: Built using Quarkus to provide fast startup times and low memory footprint.
- **MCP Standard**: Implements the Model-Context Protocol for efficient standard input/output server operations.
- **Keycloak Integration**: Seamlessly integrates with Keycloak for authentication and authorization.

## Prerequisites

- **Java 21 or newer**: Ensure you have Java installed on your system.
- **Maven**: Required for building and running the project.

## Getting Started

### Running in Development Mode

Run the application in development mode with live coding enabled:

```bash
./mvnw quarkus:dev
```
To package the application:

```bash
./mvnw package
```

### Running with Goose

Integrate and run this project as an extension with Goose:

```bash
goose session --with-extension="java -jar target/quarkus-app/quarkus-run.jar"
```

## Documentation

- [Quarkus Documentation](https://quarkus.io/documentation/)
- [Keycloak Documentation](https://www.keycloak.org/documentation.html)
- [MCP Server Guide](https://docs.quarkiverse.io/quarkus-mcp-server/dev/index.html)

## Contributing

Contributions are welcome! Please see the [contributing guidelines](CONTRIBUTING.md) for more information.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE.md) file for details.
